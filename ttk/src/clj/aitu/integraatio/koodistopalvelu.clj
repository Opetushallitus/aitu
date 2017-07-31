;; Copyright (c) 2013 The Finnish National Board of Education - Opetushallitus
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

(ns aitu.integraatio.koodistopalvelu
  (:require [clj-time.format :as time]
            [clojure.set :refer [intersection difference rename-keys]]
            [oph.common.util.util :refer :all]
            [aitu.infra.tutkinto-arkisto :as tutkinto-arkisto]
            [aitu.infra.koulutusala-arkisto :as koulutusala-arkisto]
            [aitu.infra.opintoala-arkisto :as opintoala-arkisto]
            [aitu.util :refer :all]))

;; Tässä nimiavaruudessa viitataan "koodi"-sanalla koodistopalvelun palauttamaan tietorakenteeseen.
;; Jos koodi on muutettu Aitun käyttämään muotoon, siihen viitataan ko. käsitteen nimellä, esim. "osatutkinto".

(defn koodi->kasite
  "Muuttaa koodistopalvelun koodin ohjelmassa käytettyyn muotoon.
Koodin arvo laitetaan arvokentta-avaimen alle."
  [koodi arvokentta]
  (when koodi
    (let [metadata_fi (first (filter #(= "FI" (:kieli %)) (:metadata koodi)))
          metadata_sv (first (filter #(= "SV" (:kieli %)) (:metadata koodi)))]
      {:nimi_fi (:nimi metadata_fi)
       :nimi_sv (:nimi metadata_sv)
       :kuvaus_fi (:kuvaus metadata_fi)
       :kuvaus_sv (:kuvaus metadata_sv)
       :koodiUri (:koodiUri koodi)
       :versio (:versio koodi)
       arvokentta (:koodiArvo koodi)
       :voimassa_alkupvm (or (some-> (:voimassaAlkuPvm koodi) parse-ymd) (clj-time.core/local-date 2199 1 1))
       :voimassa_loppupvm (or (some-> (:voimassaLoppuPvm koodi) parse-ymd) (clj-time.core/local-date 2199 1 1))})))

;  "koulutusalaoph2002"
(def ^:private koulutusala-koodisto "isced2011koulutusalataso1")

; "opintoalaoph2002"
(def ^:private opintoala-koodisto "isced2011koulutusalataso2")

(defn koodi->tutkinto [koodi]
  (koodi->kasite koodi :tutkintotunnus))

(defn koodi->koulutusala [koodi]
  (koodi->kasite koodi :koulutusala_tkkoodi))

(defn koodi->opintoala [koodi]
  (koodi->kasite koodi :opintoala_tkkoodi))

(defn koodi->osaamisala [koodi]
  (koodi->kasite koodi :osaamisalatunnus))

(defn ^:private hae-koodit
  "Hakee kaikki koodit annetusta koodistosta ja asettaa koodin koodiarvon avaimeksi arvokentta"
  ([asetukset koodisto] (get-json-from-url (str (:url asetukset) koodisto "/koodi")))
  ([asetukset koodisto versio] (get-json-from-url (str (:url asetukset) koodisto "/koodi?koodistoVersio=" versio))))

(defn ^:private hae-koodi
  [asetukset koodisto koodiuri versio]
  (try
    (get-json-from-url (str (:url asetukset) koodisto "/koodi/" koodiuri "?koodistoVersio=" versio))
    (catch clojure.lang.ExceptionInfo _
      nil)))

(defn kuuluu-koodistoon
  "Filtteröi koodilistasta annetun koodiston koodit"
  [koodisto]
  (fn [koodi]
    (= koodisto (get-in koodi [:koodisto :koodistoUri]))))

(defn ^:private opintoala-koodi?
  [koodi]
  ((kuuluu-koodistoon opintoala-koodisto) koodi))

(defn ^:private koulutusala-koodi?
  [koodi]
  ((kuuluu-koodistoon koulutusala-koodisto) koodi))

(defn ^:private osaamisala-koodi?
  [koodi]
  ((kuuluu-koodistoon "osaamisala") koodi))

(defn ^:private tutkintotyyppi-koodi?
  [koodi]
  ((kuuluu-koodistoon "tutkintotyyppi") koodi))

(defn koodiston-uusin-versio
  [asetukset koodisto]
  (try
    (loop [versio nil]
      (when-let [json (get-json-from-url (str (:url asetukset)
                                              koodisto
                                              (when versio (str "?koodistoVersio=" versio))))]
        (if (= "HYVAKSYTTY" (:tila json))
          (:versio json)
          (recur (dec (:versio json))))))
    (catch clojure.lang.ExceptionInfo _
      1)))

(defn ^:private tutkintotasokoodi?
  [koodi]
  ((kuuluu-koodistoon "koulutustyyppi") koodi))

(defn ^:private tutkintotasokoodi->tutkintotaso
  [koodi]
  (when koodi
    (case (:koodiArvo koodi)
      "12" "erikoisammattitutkinto"
      "11" "ammattitutkinto"
      "13" "perustutkinto"
      "4" "perustutkinto"
      "1" "perustutkinto"
      nil)))

(defn ^:private tutkintonimike-koodi?
  [koodi]
  (and ((kuuluu-koodistoon "tutkintonimikkeet") koodi)
       (not= "00000" (:koodiArvo koodi))))

(def ^:private tutkintonimike-kentat [:nimi_fi :nimi_sv :nimiketunnus])

(defn ^:private lisaa-opintoalaan-koulutusala
  [asetukset opintoala]
  (let [ylakoodit (get-json-from-url (str (:url asetukset) "relaatio/sisaltyy-ylakoodit/" (:koodiUri opintoala)))
        koulutusala (some-value koulutusala-koodi? ylakoodit)]
    (assoc opintoala :koulutusala_tkkoodi (:koodiArvo koulutusala))))

(defn ^:private hae-alakoodit
  [asetukset koodi] (get-json-from-url (str (:url asetukset) "relaatio/sisaltyy-alakoodit/" (:koodiUri koodi) "?koodiVersio=" (:versio koodi))))

(defn lisaa-alakoodien-data
  [asetukset tutkinto]
  (let [alakoodit (hae-alakoodit asetukset tutkinto)
        opintoala (some-value opintoala-koodi? alakoodit)
        koulutusala (some-value koulutusala-koodi? alakoodit)
        osaamisalakoodit (filter osaamisala-koodi? alakoodit)
        osaamisalakoodistoversio (koodiston-uusin-versio asetukset "osaamisala")
        osaamisalat (remove nil? (for [{koodi-uri :koodiUri} osaamisalakoodit]
                                   (koodi->osaamisala (hae-koodi asetukset "osaamisala" koodi-uri osaamisalakoodistoversio))))
        tutkintotyyppi (some-value tutkintotyyppi-koodi? alakoodit)
        tutkintotasokoodi (some-value tutkintotasokoodi? alakoodit)
        nimikkeet (->> (mapcat (partial hae-alakoodit asetukset) osaamisalat)
                    (concat alakoodit)
                    (filter tutkintonimike-koodi?)
                    (map #(select-keys (koodi->kasite % :nimiketunnus) tutkintonimike-kentat))
                    distinct)]
    (merge tutkinto
           {:opintoala_tkkoodi (:koodiArvo opintoala)
            :koulutusala_tkkoodi (:koodiArvo koulutusala)
            :tyyppi (:koodiArvo tutkintotyyppi)
            :tutkintotaso (tutkintotasokoodi->tutkintotaso tutkintotasokoodi)
            :tutkintonimikkeet nimikkeet})))

(defn hae-koodisto
  [asetukset koodisto versio]
  (koodi->kasite (get-json-from-url (str (:url asetukset) koodisto "?koodistoVersio=" versio)) :koodisto))

(defn hae-tutkinto
  [asetukset tutkintotunnus versio]
  (hae-koodi asetukset "koulutus" (str "koulutus_" tutkintotunnus) versio))

(defn hae-tutkinnot
  [asetukset]
  (let [koodistoversio (koodiston-uusin-versio asetukset "koulutus")]
    (map koodi->tutkinto (hae-koodit asetukset "koulutus" #_koodistoversio))))

(defn hae-koulutusalat
  [asetukset]
  (let [koodistoversio (koodiston-uusin-versio asetukset koulutusala-koodisto)]
    (->> (hae-koodit asetukset koulutusala-koodisto koodistoversio)
      (map koodi->koulutusala)
      (map #(dissoc % :kuvaus_fi :kuvaus_sv)))))

(defn hae-opintoalat
  [asetukset]
  (let [koodistoversio (koodiston-uusin-versio asetukset opintoala-koodisto)]
    (->> (hae-koodit asetukset opintoala-koodisto koodistoversio)
      (map koodi->opintoala)
      (map (partial lisaa-opintoalaan-koulutusala asetukset))
      (map #(dissoc % :kuvaus_fi :kuvaus_sv)))))

(defn muutokset
  [uusi vanha]
  (into {}
        (for [[avain [uusi-arvo vanha-arvo :as diff]] (diff-maps uusi vanha)
              :when diff]
          [avain (cond
                   (or (nil? vanha-arvo) (nil? uusi-arvo)) diff
                   (map? uusi-arvo) (diff-maps uusi-arvo vanha-arvo)
                   :else diff)])))

(def ^:private tutkinnon-kentat
  [:nimi_fi :nimi_sv :tutkintotunnus :tutkintotaso :opintoala
   :voimassa_alkupvm :voimassa_loppupvm :tyyppi :koulutusala :koodistoversio])

(defn tutkintodata->vertailumuoto
  [tutkintodata]
  (let [tutkinnot (for [tutkinto tutkintodata
                        :let [tutkintotunnus (:tutkintotunnus tutkinto)
                              tutkintonimikkeet (for [nimike (:tutkintonimike tutkinto)]
                                                  (select-keys nimike [:nimi_fi :nimi_sv :nimiketunnus]))]]
                    (-> tutkinto
                      (rename-keys {:koulutusala_tkkoodi :koulutusala
                                    :opintoala_tkkoodi :opintoala})
                      (select-keys tutkinnon-kentat)
                      (assoc :tutkintonimikkeet tutkintonimikkeet)))
        tutkintonimikkeet (map-by :nimiketunnus (mapcat :tutkintonimikkeet tutkinnot))
        tutkinnot (->> tutkinnot
                    (map #(update-in % [:tutkintonimikkeet] (comp set (partial map :nimiketunnus))))
                    (map-by :tutkintotunnus))]
    {:tutkinnot tutkinnot
     :tutkintonimikkeet tutkintonimikkeet}))

(defn koodistodata->vertailumuoto
  [koodistoversio koodistodata]
  (let [tutkinnot (->>
                    koodistodata
                    (map #(assoc % :koodistoversio koodistoversio))
                    (filter #(#{"02" "03"} (:tyyppi %)))
                    (map #(rename-keys % {:opintoala_tkkoodi :opintoala, :koulutusala_tkkoodi :koulutusala}))
                    (map #(select-keys % (conj tutkinnon-kentat :tutkintonimikkeet))))
        tutkintonimikkeet (->> tutkinnot
                            (mapcat :tutkintonimikkeet)
                            (map #(select-keys % [:nimi_fi :nimi_sv :nimiketunnus]))
                            (map-by :nimiketunnus))
        tutkinnot (->> tutkinnot
                 (map #(update-in % [:tutkintonimikkeet] (comp set (partial map :nimiketunnus))))
                 (map #(assoc % :koodistoversio koodistoversio))
                 (map-by :tutkintotunnus))]
    {:tutkinnot tutkinnot
     :tutkintonimikkeet tutkintonimikkeet}))

(defn tutkinto-muutokset
  [tutkintodata koodistoversio koodistodata]
  (let [{vanhat-tutkinnot :tutkinnot
         vanhat-tutkintonimikkeet :tutkintonimikkeet} (tutkintodata->vertailumuoto tutkintodata)
        {uudet-tutkinnot :tutkinnot
         uudet-tutkintonimikkeet :tutkintonimikkeet} (koodistodata->vertailumuoto koodistoversio koodistodata)]
    {:tutkinnot (muutokset uudet-tutkinnot vanhat-tutkinnot)
     :tutkintonimikkeet (muutokset uudet-tutkintonimikkeet vanhat-tutkintonimikkeet)}))

(defn hae-tutkinto-muutokset
  [asetukset]
  (let [koodistoversio (koodiston-uusin-versio asetukset "koulutus")
        vanhat-tutkinnot (tutkinto-arkisto/hae-tutkinnot-koodistopalvelulle)
        koodiston-tutkinnot (->> (hae-tutkinnot asetukset)
                              (map (partial lisaa-alakoodien-data asetukset)))]
    (tutkinto-muutokset vanhat-tutkinnot koodistoversio koodiston-tutkinnot)))

;; Koulutusalat ja opintoalat käsitellään hieman eri tavalla, koska tietomallissa niillä on selite eikä nimi

(defn hae-koulutusala-muutokset
  [asetukset]
  (let [vanhat (into {} (for [koulutusala (koulutusala-arkisto/hae-kaikki)]
                          [(:koulutusala_tkkoodi koulutusala) {:koulutusala_tkkoodi (:koulutusala_tkkoodi koulutusala)
                                                              :nimi_fi (:selite_fi koulutusala)
                                                              :nimi_sv (:selite_sv koulutusala)
                                                              :voimassa_alkupvm (:voimassa_alkupvm koulutusala)
                                                              :voimassa_loppupvm (:voimassa_loppupvm koulutusala)}]))
        uudet (map-by :koulutusala_tkkoodi
                      (map #(dissoc % :koodiUri :versio) (hae-koulutusalat asetukset)))]
    (muutokset uudet vanhat)))

(defn updaterf [defaultv]
  (fn [v] (if (nil? v) defaultv v)))

(defn hae-opintoala-muutokset
  [asetukset]
  (let [vanhat (into {} (for [opintoala (opintoala-arkisto/hae-kaikki)]
                          [(:opintoala_tkkoodi opintoala) {:opintoala_tkkoodi (:opintoala_tkkoodi opintoala)
                                                           :koulutusala_tkkoodi (:koulutusala_tkkoodi opintoala)
                                                           :nimi_fi (:selite_fi opintoala)
                                                           :nimi_sv (:selite_sv opintoala)
                                                           :voimassa_alkupvm (:voimassa_alkupvm opintoala)
                                                           :voimassa_loppupvm (:voimassa_loppupvm opintoala)}]))
        ; TODO: toimiiko tämä nyt vielä?
        opintoalat (map #(update % :voimassa_loppupvm (updaterf (clj-time.core/local-date 2199 1 1))) (hae-opintoalat asetukset))
        uudet (map-by :opintoala_tkkoodi
                      (map #(dissoc % :koodiUri :versio) opintoalat))]
    (muutokset uudet vanhat)))
