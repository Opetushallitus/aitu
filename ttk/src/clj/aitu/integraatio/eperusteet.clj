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
            [oph.common.util.util :refer :all]))

(defn lataa-kaikki-sivut [url options]
  (loop [vanha-data []
         sivu 0]
    (let [{:keys [data sivuja]} (get-json-from-url url (assoc-in options [:query-params :sivu] sivu))
          data (concat vanha-data data)]
      (if (>= (inc sivu) sivuja)
        data
        (recur data (inc sivu))))))

(defn numeroi [index m]
  (assoc m :jarjestys (inc index)))

(defn osaamistaso-hyva [tasot]
  (some-value-with :_osaamistaso "3" tasot))

(defn muotoile-ammattitaidon-kuvaukset [kohde]
  (for [kriteeri (or (:kriteerit (osaamistaso-hyva (:osaamistasonKriteerit kohde)))
                     (:kriteerit (first (:osaamistasonKriteerit kohde))))] ;; Vain perustutkinnoilla on erilliset osaamistasot määriteltyinä
    {:nimi_fi (:fi kriteeri)
     :nimi_sv (:sv kriteeri)}))

(defn muotoile-arvioinnin-kohdealue [index alue]
  {:jarjestys (inc index)
   :nimi_fi (get-in alue [:otsikko :fi])
   :nimi_sv (get-in alue [:otsikko :sv])
   :ammattitaidon_kuvaukset (map-indexed numeroi (mapcat muotoile-ammattitaidon-kuvaukset (:arvioinninKohteet alue)))})

(defn osatunnus [osa]
  (cond
    (= 6 (count (:koodiArvo osa))) (:koodiArvo osa)
    (:koodiUri osa) (second (re-matches #"^tutkinnonosat_(\d+)$" (:koodiUri osa)))
    (:koodiArvo osa) (second (re-matches #"^tutkinnonosat_(\d+)$" (:koodiArvo osa)))))

(defn muotoile-tutkinnonosa [index osa]
  {:osatunnus (osatunnus osa)
   :jarjestys (inc index)
   :nimi_fi (get-in osa [:nimi :fi])
   :nimi_sv (get-in osa [:nimi :sv])
   :arvioinnin_kohdealueet (map-indexed muotoile-arvioinnin-kohdealue (get-in osa [:arviointi :arvioinninKohdealueet]))})

(defn yhteinen-osa? [rakenne]
  (= (get-in rakenne [:nimi :fi]) "Yhteiset tutkinnon osat"))

(defn hae-osat
  ([rakenne] (hae-osat "valinnainen" rakenne))
  ([tyyppi rakenne]
    (if (:osat rakenne)
      (let [tyyppi (if (yhteinen-osa? rakenne)
                     "yhteinen"
                     tyyppi)]
        (mapcat (partial hae-osat tyyppi) (:osat rakenne)))
      (let [tyyppi (if (:pakollinen rakenne)
                     "pakollinen"
                     tyyppi)]
        [(assoc rakenne :tyyppi tyyppi)]))))

(defn muotoile-suoritustapa
  [osa-id->osatunnus]
  (fn [suoritustapa]
    (let [osaviite->osatunnus (into {} (for [viite (:tutkinnonOsaViitteet suoritustapa)]
                                        [(str (:id viite)) (osa-id->osatunnus (:_tutkinnonOsa viite))]))]
      {:suoritustapakoodi (:suoritustapakoodi suoritustapa)
       :osat (map-indexed (fn [index osa]
                            {:jarjestys (inc index)
                             :tyyppi (:tyyppi osa)
                             :tutkinnonosa (osaviite->osatunnus (:_tutkinnonOsaViite osa))})
                          (hae-osat (:rakenne suoritustapa)))})))

(defn muotoile-peruste [peruste]
  (let [osa-id->osatunnus (into {} (for [osa (:tutkinnonOsat peruste)]
                                     [(str (:id osa)) (osatunnus osa)]))]
    {:diaarinumero (:diaarinumero peruste)
     :voimassa_alkupvm (c/to-local-date (:voimassaoloAlkaa peruste))
     :voimassa_loppupvm (or (c/to-local-date (:voimassaoloLoppuu peruste)) (time/local-date 2199 1 1))
     :siirtymaajan_loppupvm (or (c/to-local-date (:siirtymaPaattyy peruste)) (time/local-date 2199 1 1))
     :tutkinnonosat (map-indexed muotoile-tutkinnonosa (:tutkinnonOsat peruste))
     :tutkinnot (map :koulutuskoodiArvo (:koulutukset peruste))}))

(defn hae-peruste [id asetukset]
  (muotoile-peruste (get-json-from-url (str (:url asetukset) "api/perusteet/" id "/kaikki"))))

(defn hae-perusteet [viimeisin-haku asetukset]
  (for [peruste-id (map :id (lataa-kaikki-sivut (str (:url asetukset) "api/perusteet") {:query-params {:muokattu (c/to-long viimeisin-haku)}}))
        :let [peruste (hae-peruste peruste-id asetukset)]
        tutkintotunnus (:tutkinnot peruste)]
    [tutkintotunnus (dissoc peruste :tutkinnot)]))
