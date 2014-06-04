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
            [aitu.infra.tutkinto-arkisto :as tutkinto-arkisto]
            [aitu.infra.koulutusala-arkisto :as koulutusala-arkisto]
            [aitu.infra.opintoala-arkisto :as opintoala-arkisto]
            [clojure.set :refer [intersection difference rename-keys]]
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
       arvokentta (:koodiArvo koodi)
       :voimassa_alkupvm (or (some-> (:voimassaAlkuPvm koodi) parse-ymd) (clj-time.core/local-date 2199 1 1))
       :voimassa_loppupvm (or (some-> (:voimassaLoppuPvm koodi) parse-ymd) (clj-time.core/local-date 2199 1 1))})))

(defn koodi->tutkinto [koodi]
  (koodi->kasite koodi :tutkintotunnus))

(defn koodi->koulutusala [koodi]
  (koodi->kasite koodi :koulutusala_tkkoodi))

(defn koodi->opintoala [koodi]
  (koodi->kasite koodi :opintoala_tkkoodi))

(defn koodi->tutkinnonosa [koodi]
  (koodi->kasite koodi :osatunnus))

(defn koodi->osaamisala [koodi]
  (koodi->kasite koodi :osaamisalatunnus))

(defn ^:private hae-koodit
  "Hakee kaikki koodit annetusta koodistosta ja asettaa koodin koodiarvon avaimeksi arvokentta"
  [asetukset koodisto]
  (get-json-from-url (str (:url asetukset) koodisto "/koodi")))

(defn ^:private hae-rinnasteiset
  "Hakee koodistopalvelusta annetun koodin kanssa rinnasteiset koodit"
  [asetukset koodi]
  (get-json-from-url (str (:url asetukset) "relaatio/rinnasteinen/" (:koodiUri koodi))))

(defn kuuluu-koodistoon
  "Filtteröi koodilistasta annetun koodiston koodit"
  [koodisto]
  (fn [koodi]
    (= koodisto (get-in koodi [:koodisto :koodistoUri]))))

(defn ^:private opintoala-koodi?
  [koodi]
  ((kuuluu-koodistoon "opintoalaoph2002") koodi))

(defn ^:private koulutusala-koodi?
  [koodi]
  ((kuuluu-koodistoon "koulutusalaoph2002") koodi))

(defn ^:private osaamisala-koodi?
  [koodi]
  ((kuuluu-koodistoon "osaamisala") koodi))

(defn ^:private osajarjestys-koodi?
  [koodi]
  (.endsWith (get-in koodi [:koodisto :koodistoUri]) "jarjestys"))

(defn ^:private alakoodit->jarjestyskoodit
  [alakoodit]
  (for [koodi (filter osajarjestys-koodi? alakoodit)]
    {:koodiUri (:koodiUri koodi)
     :jarjestysnumero (Integer/parseInt (:koodiArvo koodi))}))

(defn ^:private alakoodit->jarjestyskoodisto [alakoodit]
  (-> (some-value osajarjestys-koodi? alakoodit) :koodisto :koodistoUri))

(defn ^:private jarjestyskoodi->tutkinnonosa
  [asetukset koodi]
  (some-> (koodi->tutkinnonosa (first (hae-rinnasteiset asetukset koodi)))
    (assoc :jarjestysnumero (:jarjestysnumero koodi))))

(defn ^:private tutkintotyyppi-koodi?
  [koodi]
  ((kuuluu-koodistoon "tutkintotyyppi") koodi))

(defn koodiston-uusin-versio
  [asetukset koodisto]
  (loop [versio nil]
    (when-let [json (get-json-from-url (str (:url asetukset)
                                            koodisto
                                            (when versio (str "?koodistoVersio=" versio))))]
      (if (= "HYVAKSYTTY" (:tila json))
        (:versio json)
        (recur (dec (:versio json)))))))

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

(defn ^:private lisaa-opintoalaan-koulutusala
  [asetukset opintoala]
  (let [ylakoodit (get-json-from-url (str (:url asetukset) "relaatio/sisaltyy-ylakoodit/" (:koodiUri opintoala)))
        koulutusala (some-value koulutusala-koodi? ylakoodit)]
    (assoc opintoala :koulutusala_tkkoodi (:koodiArvo koulutusala))))

(defn ^:private lisaa-opintoala-koulutusala-tutkinnonosat
  [asetukset tutkinto]
  (let [alakoodit (get-json-from-url (str (:url asetukset) "relaatio/sisaltyy-alakoodit/" (:koodiUri tutkinto)))
        opintoala (some-value opintoala-koodi? alakoodit)
        koulutusala (some-value koulutusala-koodi? alakoodit)
        osajarjestyskoodisto (alakoodit->jarjestyskoodisto alakoodit)
        jarjestyskoodistoversio (some->> osajarjestyskoodisto (koodiston-uusin-versio asetukset))
        tutkinnonosat (keep (partial jarjestyskoodi->tutkinnonosa asetukset) (alakoodit->jarjestyskoodit alakoodit))
        osaamisalat (map koodi->osaamisala (filter osaamisala-koodi? alakoodit))
        tutkintotyyppi (some-value tutkintotyyppi-koodi? alakoodit)
        tutkintotasokoodi (some-value tutkintotasokoodi? alakoodit)]
    (assoc tutkinto :opintoala_tkkoodi (:koodiArvo opintoala)
                    :koulutusala_tkkoodi (:koodiArvo koulutusala)
                    :tutkinnonosat tutkinnonosat
                    :tyyppi (:koodiArvo tutkintotyyppi)
                    :tutkintotaso (tutkintotasokoodi->tutkintotaso tutkintotasokoodi)
                    :osaamisalat osaamisalat
                    :jarjestyskoodistoversio jarjestyskoodistoversio)))

(defn hae-tutkinnot
  [asetukset]
  (map koodi->tutkinto (hae-koodit asetukset "koulutus")))

(defn hae-koulutusalat
  [asetukset]
  (->> (hae-koodit asetukset "koulutusalaoph2002")
    (map koodi->koulutusala)
    (map #(dissoc % :kuvaus_fi :kuvaus_sv))))

(defn hae-opintoalat
  [asetukset]
  (->> (hae-koodit asetukset "opintoalaoph2002")
    (map koodi->opintoala)
    (map (partial lisaa-opintoalaan-koulutusala asetukset))
    (map #(dissoc % :kuvaus_fi :kuvaus_sv))))

(defn hae-tutkinnonosat
  [asetukset]
  (map koodi->tutkinnonosa (hae-koodit asetukset "tutkinnonosat")))

(defn hae-osaamisalat
  [asetukset]
  (map koodi->osaamisala (hae-koodit asetukset "osaamisala")))

(defn tutkintorakenne
  "Lukee koko tutkintorakenteen koodistosta. Suoritus kestää n. 8 minuuttia ja aiheuttaa
tuhansia http-pyyntöjä koodistopalveluun.

Palautuva tutkintorakenne on lista koulutusaloista, joista jokainen
sisältää listan siihen kuuluvista opintoaloista, joista jokainen
sisältää listan siihen kuuluvista tutkinnoista, joista jokainen
sisältää listat siihen kuuluvista osaamisaloista ja tutkinnonosista."
   [asetukset]
   (let [oa-tunnus->tutkinnot (group-by :opintoala_tkkoodi
                                        (for [tutkinto (hae-tutkinnot asetukset)]
                                          (lisaa-opintoala-koulutusala-tutkinnonosat asetukset tutkinto)))
         ;; Muodostetaan map opintoalatunnuksesta koulutusalatunnukseen.
         ;; Koulutusalatunnus saadaan mistä tahansa kyseisen opintoalan tutkinnosta.
         ;; Koodistopalvelussa ei ole relaatiota opintoala->koulutusala.
         oa-tunnus->ka-tunnus (into {} (for [[oa-tunnus tutkinnot] oa-tunnus->tutkinnot]
                                         {oa-tunnus (:koulutusala_tkkoodi (first tutkinnot))}))
         ka-tunnus->opintoalat (group-by :koulutusala_tkkoodi
                                         (for [opintoala (hae-opintoalat asetukset)
                                               :let [oa-tunnus (:opintoala_tkkoodi opintoala)]]
                                           (assoc opintoala
                                                  :tutkinnot (oa-tunnus->tutkinnot oa-tunnus [])
                                                  :koulutusala_tkkoodi (oa-tunnus->ka-tunnus oa-tunnus))))]
     (for [koulutusala (hae-koulutusalat asetukset)
           :let [ka-tunnus (:koulutusala_tkkoodi koulutusala)]]
       (assoc koulutusala
              :opintoalat (ka-tunnus->opintoalat ka-tunnus [])))))

(defn muutokset
  [uusi vanha]
  (into {}
        (for [[avain [uusi-arvo vanha-arvo :as diff]] (diff-maps uusi vanha)
              :when diff]
          [avain (cond
                  (nil? uusi-arvo) diff
                  (nil? vanha-arvo) diff
                  (map? uusi-arvo) (diff-maps uusi-arvo vanha-arvo)
                  :else diff)])))

(defn ^:private tunnus-jarjestysnro [tutkinnonosa]
  (select-keys tutkinnonosa [:osatunnus :jarjestysnumero]))

(def ^:private tutkinnon-kentat
  [:nimi_fi :nimi_sv :tutkintotunnus :tutkintotaso :opintoala
   :voimassa_alkupvm :voimassa_loppupvm :tyyppi :koulutusala])

(defn tutkinto-muutokset
  [asetukset]
  (let [koodistoversio (koodiston-uusin-versio asetukset "koulutus")
        vanhat (for [tutkinto (tutkinto-arkisto/hae-tutkinnot-tutkinnonosat-osaamisalat)
                     :let [tutkintotunnus (:tutkintotunnus tutkinto)
                           tutkinnonosat (for [osa (:tutkinto_ja_tutkinnonosa tutkinto)]
                                           (select-keys osa [:nimi_fi :nimi_sv :osatunnus :jarjestysnumero
                                                             :voimassa_alkupvm :voimassa_loppupvm]))
                           osaamisalat (for [ala (:osaamisala tutkinto)]
                                         (select-keys ala [:nimi_fi :nimi_sv :osaamisalatunnus
                                                           :voimassa_alkupvm :voimassa_loppupvm]))]]
                 (-> tutkinto
                   (rename-keys {:koulutusala_tkkoodi :koulutusala})
                   (select-keys tutkinnon-kentat)
                   (assoc :tutkinnonosat tutkinnonosat
                          :osaamisalat osaamisalat)))
        vanhat-tutkinnonosat (map-by :osatunnus (mapcat :tutkinnonosat vanhat))
        vanhat-osaamisalat (map-by :osaamisalatunnus (mapcat :osaamisalat vanhat))
        vanhat (->> vanhat
                 (map #(update-in % [:tutkinnonosat] (comp set (partial map tunnus-jarjestysnro))))
                 (map #(update-in % [:osaamisalat] (comp set (partial map :osaamisalatunnus))))
                 (map-by :tutkintotunnus))
        uudet (->>
                (hae-tutkinnot asetukset)
                (map (partial lisaa-opintoala-koulutusala-tutkinnonosat asetukset))
                (filter #(#{"02" "03"} (:tyyppi %)))
                (map #(rename-keys % {:opintoala_tkkoodi :opintoala, :koulutusala_tkkoodi :koulutusala}))
                (map #(select-keys % (conj tutkinnon-kentat :tutkinnonosat :osaamisalat))))
        uudet-tutkinnonosat (->> uudet
                              (mapcat :tutkinnonosat)
                              (map #(dissoc % :koodiUri :kuvaus_fi))
                              (map-by :osatunnus))
        uudet-osaamisalat (->> uudet
                              (mapcat :osaamisalat)
                              (map #(dissoc % :koodiUri :kuvaus_fi))
                              (map-by :osaamisalatunnus))
        uudet (->> uudet
                 (map #(update-in % [:tutkinnonosat] (comp set (partial map tunnus-jarjestysnro))))
                 (map #(update-in % [:osaamisalat] (comp set (partial map :osaamisalatunnus))))
                 (map #(assoc % :koodistoversio koodistoversio))
                 (map-by :tutkintotunnus))]
    {:tutkinnot (muutokset uudet vanhat)
     :osaamisalat (muutokset uudet-osaamisalat vanhat-osaamisalat)
     :tutkinnonosat (muutokset uudet-tutkinnonosat vanhat-tutkinnonosat)}))

;; Koulutusalat ja opintoalat käsitellään hieman eri tavalla, koska tietomallissa niillä on selite eikä nimi

(defn koulutusala-muutokset
  [asetukset]
  (let [vanhat (into {} (for [koulutusala (koulutusala-arkisto/hae-kaikki)]
                          [(:koulutusala_tkkoodi koulutusala) {:koulutusala_tkkoodi (:koulutusala_tkkoodi koulutusala)
                                                              :nimi_fi (:selite_fi koulutusala)
                                                              :nimi_sv (:selite_sv koulutusala)
                                                              :voimassa_alkupvm (:voimassa_alkupvm koulutusala)
                                                              :voimassa_loppupvm (:voimassa_loppupvm koulutusala)}]))
        uudet (map-by :koulutusala_tkkoodi
                      (map #(dissoc % :koodiUri) (hae-koulutusalat asetukset)))]
    (muutokset uudet vanhat)))

(defn opintoala-muutokset
  [asetukset]
  (let [vanhat (into {} (for [opintoala (opintoala-arkisto/hae-kaikki)]
                          [(:opintoala_tkkoodi opintoala) {:opintoala_tkkoodi (:opintoala_tkkoodi opintoala)
                                                           :koulutusala_tkkoodi (:koulutusala_tkkoodi opintoala)
                                                           :nimi_fi (:selite_fi opintoala)
                                                           :nimi_sv (:selite_sv opintoala)
                                                           :voimassa_alkupvm (:voimassa_alkupvm opintoala)
                                                           :voimassa_loppupvm (:voimassa_loppupvm opintoala)}]))
        uudet (map-by :opintoala_tkkoodi
                      (map #(dissoc % :koodiUri) (hae-opintoalat asetukset)))]
    (muutokset uudet vanhat)))
