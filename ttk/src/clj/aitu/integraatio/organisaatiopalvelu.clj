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
            [aitu.infra.oppilaitos-arkisto :as arkisto]))

(defn hae-kaikki [asetukset]
  (let [oids (get-json-from-url (:url asetukset))]
    (for [oid oids]
      (get-json-from-url (str (:url asetukset) oid)))))

(def ^:private halutut-tyypit
  #{"oppilaitostyyppi_21"
    "oppilaitostyyppi_22"
    "oppilaitostyyppi_23"
    "oppilaitostyyppi_24"
    "oppilaitostyyppi_41"
    "oppilaitostyyppi_42"
    "oppilaitostyyppi_61"
    "oppilaitostyyppi_62"
    "oppilaitostyyppi_63"
    "oppilaitostyyppi_93"
    "oppilaitostyyppi_99"
    "oppilaitostyyppi_xx"})

(defn ^:private haluttu-tyyppi? [koodi]
  (some-> (:oppilaitosTyyppiUri koodi)
          (subs 0 19)
          halutut-tyypit))

(defn ^:private nimi [koodi]
  ((some-fn :fi :sv :en) (:nimi koodi)))

(defn ^:private koodi->oppilaitos [koodi]
  {:nimi (nimi koodi)
   :oid (:oid koodi)
   :sahkoposti (:emailOsoite koodi)
   :puhelin (:puhelinnumero koodi)
   :osoite (get-in koodi [:postiosoite :osoite])
   :postinumero (some-> (get-in koodi [:postiosoite :postinumeroUri])
                        (subs 6))
   :postitoimipaikka (get-in koodi [:postiosoite :postitoimipaikka])
   :www_osoite (:wwwOsoite koodi)
   :oppilaitoskoodi (:oppilaitosKoodi koodi)})

(defn ^:private koodi->toimipaikka [koodi]
  {:nimi (nimi koodi)
   :oid (:oid koodi)
   :sahkoposti (:emailOsoite koodi)
   :puhelin (:puhelinnumero koodi)
   :osoite (get-in koodi [:postiosoite :osoite])
   :postinumero (some-> (get-in koodi [:postiosoite :postinumeroUri])
                        (subs 6))
   :postitoimipaikka (get-in koodi [:postiosoite :postitoimipaikka])
   :www_osoite (:wwwOsoite koodi)
   :toimipaikkakoodi (:toimipistekoodi koodi)})

(defn ^:private oppilaitoksen-kentat [oppilaitos]
  (select-keys oppilaitos [:nimi :oid :sahkoposti :puhelin :osoite
                           :postinumero :postitoimipaikka :www_osoite
                           :oppilaitoskoodi]))

(defn ^:private toimipaikan-kentat [oppilaitos]
  (select-keys oppilaitos [:nimi :oid :sahkoposti :puhelin :osoite
                           :postinumero :postitoimipaikka :www_osoite
                           :toimipaikkakoodi :oppilaitos]))

(defn ^:private tyyppi [koodi]
  (cond
    (haluttu-tyyppi? koodi) :oppilaitos
    (:toimipistekoodi koodi) :toimipaikka))

(defn ^:private paivita-oppilaitokset! [koodit]
  (let [oppilaitokset (->> (arkisto/hae-kaikki)
                        (map-by :oid))]
    (doseq [koodi koodit
            :let [oid (:oid koodi)
                  vanha-oppilaitos (oppilaitoksen-kentat (get oppilaitokset oid))
                  uusi-oppilaitos (koodi->oppilaitos koodi)]]
      (cond
        (nil? vanha-oppilaitos) (arkisto/lisaa! uusi-oppilaitos)
        (not= vanha-oppilaitos uusi-oppilaitos) (arkisto/paivita! uusi-oppilaitos)))))


(defn ^:private paivita-toimipaikat! [koodit]
  (let [oppilaitokset (->> (arkisto/hae-kaikki)
                        (map-by :oid))
        toimipaikat (->> (arkisto/hae-kaikki-toimipaikat)
                      (map-by :oid))]
    (doseq [koodi koodit
            :when (contains? oppilaitokset (:parentOid koodi))
            :let [oid (:oid koodi)
                  oppilaitos (get oppilaitokset (:parentOid koodi))
                  vanha-toimipaikka (toimipaikan-kentat (get toimipaikat oid))
                  uusi-toimipaikka (assoc (koodi->toimipaikka koodi)
                                          :oppilaitos (:oppilaitoskoodi oppilaitos))]]
      (cond
        (nil? vanha-toimipaikka) (arkisto/lisaa-toimipaikka! uusi-toimipaikka)
        (not= vanha-toimipaikka uusi-toimipaikka) (arkisto/paivita-toimipaikka! uusi-toimipaikka)))))

(defn paivita-organisaatiot!
  [asetukset]
  (let [koodit (map-by tyyppi (hae-kaikki asetukset))
        oppilaitoskoodit (:oppilaitos koodit)
        toimipaikkakoodit (:toimipaikka koodit)]
    (paivita-oppilaitokset! oppilaitoskoodit)
    (paivita-toimipaikat! toimipaikkakoodit)))
