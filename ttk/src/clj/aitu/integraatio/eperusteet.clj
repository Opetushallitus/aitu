;; Copyright (c) 2015 The Finnish National Board of Education - Opetushallitus
;;
;; This program is free software:  Licensed under the EUPL, Version 1.1 or - as
;; soon as they will be approved by the European Commission - subsequent versions
;; of the EUPL (the "Licence");
;;
;; You may not use this work except in compliance with the Licence.
;; You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
;;
;; This program is distributed in the hope that it will be useful,
;; but WITHOUT ANY WARRANTY; without even the implied warranty of
;; MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
;; European Union Public Licence for more details.

(ns aitu.integraatio.eperusteet
  (:require [clj-time.coerce :as c]
            [clj-time.core :as time]
            [clojure.tools.logging :as log]
            [oph.common.util.util :refer :all]
            [oph.korma.common :refer [to-hki-local-date]]))

(defn lataa-kaikki-sivut [url options]
  (loop [vanha-data []
         sivu 0]
    (let [{:keys [data sivuja]} (get-json-from-url url (assoc-in options [:query-params :sivu] sivu))
          data (concat vanha-data data)]
      (if (>= (inc sivu) sivuja)
        data
        (recur data (inc sivu))))))

(defn osatunnus [osa]
  (cond
    (= 6 (count (:koodiArvo osa))) (:koodiArvo osa)
    (:koodiUri osa) (second (re-matches #"^tutkinnonosat_(\d+)$" (:koodiUri osa)))
    (:koodiArvo osa) (second (re-matches #"^tutkinnonosat_(\d+)$" (:koodiArvo osa)))))

(defn osaamisalatunnus [rakenne]
  (second (re-matches #"^osaamisala_(.*)$" (get-in rakenne [:osaamisala :osaamisalakoodiUri] ""))))

(defn hae-osat
  ([rakenne] (hae-osat nil rakenne))
  ([osaamisala rakenne]
    (let [osaamisala (or osaamisala (osaamisalatunnus rakenne))]
      (if (:osat rakenne)
        (mapcat (partial hae-osat osaamisala) (:osat rakenne))
        [(assoc rakenne :osaamisala osaamisala)]))))

(defn muotoile-suoritustapa [osa-id->osatunnus osaamisalat suoritustapa]
  (let [osaviite->osatunnus (into {} (for [viite (:tutkinnonOsaViitteet suoritustapa)]
                                       [(str (:id viite)) (osa-id->osatunnus (:_tutkinnonOsa viite))]))
        tutkinnonosat (for [osa (hae-osat (:rakenne suoritustapa))]
                        {:tutkinnonosa (osaviite->osatunnus (:_tutkinnonOsaViite osa))
                         :osaamisala (:osaamisala osa)})
        osaamisalan-tutkinnonosat (group-by :osaamisala tutkinnonosat)
        osaamisalat (for [ala osaamisalat
                          :let [osat (concat (osaamisalan-tutkinnonosat nil)
                                             (osaamisalan-tutkinnonosat (:osaamisalatunnus ala)))]]
                      (assoc ala :osat (map-indexed (fn [index osa] (assoc osa :jarjestys (inc index))) osat)))]
    (doseq [ala osaamisalat]
      (when-not (seq (osaamisalan-tutkinnonosat (:osaamisalatunnus ala)))
        (log/warn "Osaamisalalla ei ole tutkinnonosia:" (:osaamisalatunnus ala) (:nimi_fi ala))))
    {:osaamisalat osaamisalat}))

(defn muotoile-osaamisala
  [ala]
  {:osaamisalatunnus (second (re-matches #"^osaamisala_(.*)$" (:uri ala)))
   :nimi_fi (or (get-in ala [:nimi :fi]) (get-in ala [:nimi :sv]))
   :nimi_sv (get-in ala [:nimi :sv])})

(defn muotoile-tutkinnonosa [index osa]
  {:osatunnus (osatunnus osa)
   :jarjestys (inc index)
   :nimi_fi (or (get-in osa [:nimi :fi]) (get-in osa [:nimi :sv]))
   :nimi_sv (get-in osa [:nimi :sv])})

(defn muotoile-peruste [peruste]
  (let [osa-id->osatunnus (into {} (for [osa (:tutkinnonOsat peruste)]
                                     [(str (:id osa)) (osatunnus osa)]))
        osaamisalat (map muotoile-osaamisala (:osaamisalat peruste))
        tutkinnonosat (map-indexed muotoile-tutkinnonosa (:tutkinnonOsat peruste))
        nayttotutkinto (some-value-with :suoritustapakoodi "naytto" (:suoritustavat peruste))]
    (when nayttotutkinto
      (when (and (nil? (:siirtymaPaattyy peruste)) (not (nil? (:voimassaoloLoppuu peruste))))
        (log/warn (str "Tutkinnon " (:id peruste) " siirtymäaikaa ei ole asetettu, voimassaolon päättymispäivä on asetettu. ")))   
      (merge {:diaarinumero (:diaarinumero peruste)
              :eperustetunnus (:id peruste)
              :voimassa_alkupvm (to-hki-local-date (:voimassaoloAlkaa peruste))
              :voimassa_loppupvm (or (to-hki-local-date (:voimassaoloLoppuu peruste)) (time/local-date 2199 1 1))
              :siirtymaajan_loppupvm (or (to-hki-local-date (:siirtymaPaattyy peruste)) (time/local-date 2199 1 1))              
              :tutkinnot (map :koulutuskoodiArvo (:koulutukset peruste))
              :tutkinnonosat tutkinnonosat}
             (muotoile-suoritustapa osa-id->osatunnus osaamisalat nayttotutkinto))
      )))

(defn hae-peruste [id asetukset]
  (let [peruste-data (get-json-from-url (str (:url asetukset) "api/perusteet/" id "/kaikki"))]
    (muotoile-peruste peruste-data)))

(defn hae-perusteet [viimeisin-haku asetukset]
  (for [{peruste-id :id} (lataa-kaikki-sivut (str (:url asetukset) "api/perusteet") {:query-params {:muokattu (c/to-long viimeisin-haku)}})
        :let [peruste (hae-peruste peruste-id asetukset)]
        tutkintotunnus (:tutkinnot peruste)]
    [tutkintotunnus (-> peruste
                      (dissoc :tutkinnot)
                      (assoc :tutkinto tutkintotunnus))]))
