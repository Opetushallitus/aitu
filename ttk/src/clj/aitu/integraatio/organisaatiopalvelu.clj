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

(ns aitu.integraatio.organisaatiopalvelu
  (:require [aitu.util :refer [get-json-from-url map-by diff-maps]]
            [aitu.infra.oppilaitos-arkisto :as arkisto]
            [clojure.tools.logging :as log]))

(defn hae-kaikki [url]
  (let [oids (get-json-from-url url)]
    (for [oid oids]
      (get-json-from-url (str url oid)))))


;; Koodistopalvelun oppilaitostyyppikoodistosta
(def ^:private halutut-tyypit
  #{"oppilaitostyyppi_21" ;; Ammatilliset oppilaitokset
    "oppilaitostyyppi_22" ;; Ammatilliset erityisoppilaitokset
    "oppilaitostyyppi_23" ;; Ammatilliset erikoisoppilaitokset
    "oppilaitostyyppi_24" ;; Ammatilliset aikuiskoulutuskeskukset
    "oppilaitostyyppi_41" ;; Ammattikorkeakoulut
    "oppilaitostyyppi_42" ;; Yliopistot
    "oppilaitostyyppi_61" ;; Musiikkioppilaitokset
    "oppilaitostyyppi_62" ;; Liikunnan koulutuskeskukset
    "oppilaitostyyppi_63" ;; Kansanopistot
    "oppilaitostyyppi_93" ;; Muut koulutuksen järjestäjät
    "oppilaitostyyppi_99" ;; Muut oppilaitokset
    "oppilaitostyyppi_xx" ;; Tyyppi ei tiedossa
    })

(defn ^:private haluttu-tyyppi? [koodi]
  (when-let [tyyppi (:oppilaitosTyyppiUri koodi)]
    (contains? halutut-tyypit (subs tyyppi 0 19))))

(defn ^:private nimi [koodi]
  ((some-fn :fi :sv :en) (:nimi koodi)))

(defn ^:private postinumero [koodi]
  (when-let [postinumerokoodi (get-in koodi [:postiosoite :postinumeroUri])]
    (subs postinumerokoodi 6)))

(defn ^:private koodi->oppilaitos [koodi]
  {:nimi (nimi koodi)
   :oid (:oid koodi)
   :sahkoposti (:emailOsoite koodi)
   :puhelin (:puhelinnumero koodi)
   :osoite (get-in koodi [:postiosoite :osoite])
   :postinumero (postinumero koodi)
   :postitoimipaikka (get-in koodi [:postiosoite :postitoimipaikka])
   :www_osoite (:wwwOsoite koodi)
   :oppilaitoskoodi (:oppilaitosKoodi koodi)})

(defn ^:private koodi->toimipaikka [koodi]
  {:nimi (nimi koodi)
   :oid (:oid koodi)
   :sahkoposti (:emailOsoite koodi)
   :puhelin (:puhelinnumero koodi)
   :osoite (get-in koodi [:postiosoite :osoite])
   :postinumero (postinumero koodi)
   :postitoimipaikka (get-in koodi [:postiosoite :postitoimipaikka])
   :www_osoite (:wwwOsoite koodi)
   :toimipaikkakoodi (:toimipistekoodi koodi)})

(defn ^:private oppilaitoksen-kentat [oppilaitos]
  (when oppilaitos 
    (select-keys oppilaitos [:nimi :oid :sahkoposti :puhelin :osoite
                             :postinumero :postitoimipaikka :www_osoite
                             :oppilaitoskoodi])))

(defn ^:private toimipaikan-kentat [toimipaikka]
  (when toimipaikka
    (select-keys toimipaikka [:nimi :oid :sahkoposti :puhelin :osoite
                              :postinumero :postitoimipaikka :www_osoite
                              :toimipaikkakoodi :oppilaitos])))

(defn ^:private tyyppi [koodi]
  (cond
    (haluttu-tyyppi? koodi) :oppilaitos
    (:toimipistekoodi koodi) :toimipaikka))

(defn ^:private paivita-oppilaitokset! [koodit]
  (let [oppilaitokset (->> (arkisto/hae-kaikki-integraatiolle)
                        (map-by :oid))]
    (doseq [koodi koodit
            :let [oid (:oid koodi)
                  vanha-oppilaitos (oppilaitoksen-kentat (get oppilaitokset oid))
                  uusi-oppilaitos (koodi->oppilaitos koodi)]]
      (cond
        (nil? vanha-oppilaitos) (do
                                  (log/info "Uusi oppilaitos: " (:oppilaitoskoodi uusi-oppilaitos))
                                  (arkisto/lisaa! uusi-oppilaitos))
        (not= vanha-oppilaitos uusi-oppilaitos) (do
                                                  (log/info "Muuttunut oppilaitos: " (:oppilaitoskoodi uusi-oppilaitos))
                                                  (arkisto/paivita! uusi-oppilaitos))))))


(defn ^:private paivita-toimipaikat! [koodit]
  (let [oppilaitokset (->> (arkisto/hae-kaikki-integraatiolle)
                        (map-by :oid))
        toimipaikat (->> (arkisto/hae-kaikki-toimipaikat-integraatiolle)
                      (map-by :oid))]
    (doseq [koodi koodit
            :when (contains? oppilaitokset (:parentOid koodi))
            :let [oid (:oid koodi)
                  oppilaitos (get oppilaitokset (:parentOid koodi))
                  vanha-toimipaikka (toimipaikan-kentat (get toimipaikat oid))
                  uusi-toimipaikka (assoc (koodi->toimipaikka koodi)
                                          :oppilaitos (:oppilaitoskoodi oppilaitos))]]
      (cond
        (nil? vanha-toimipaikka) (do
                                   (log/info "Uusi toimipaikka: " (:toimipaikkakoodi uusi-toimipaikka)) 
                                   (arkisto/lisaa-toimipaikka! uusi-toimipaikka))
        (not= vanha-toimipaikka uusi-toimipaikka) (do 
                                                    (log/info "Muuttunut toimipaikka: " (diff-maps uusi-toimipaikka vanha-toimipaikka))
                                                    (arkisto/paivita-toimipaikka! uusi-toimipaikka))))))

(defn paivita-organisaatiot!
  [asetukset]
  (log/info "Aloitetaan organisaatioiden päivitys organisaatiopalvelusta")
  (let [kaikki-koodit (hae-kaikki (:url asetukset))
        koodit (group-by tyyppi kaikki-koodit)
        _ (log/info "Haettu kaikki organisaatiot")
        oppilaitoskoodit (:oppilaitos koodit)
        toimipaikkakoodit (:toimipaikka koodit)]
    (paivita-oppilaitokset! oppilaitoskoodit)
    (paivita-toimipaikat! toimipaikkakoodit)))
