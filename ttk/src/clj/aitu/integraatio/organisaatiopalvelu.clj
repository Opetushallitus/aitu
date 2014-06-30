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
  (:require [aitu.util :refer [get-json-from-url map-by diff-maps some-value]]
            [aitu.infra.oppilaitos-arkisto :as oppilaitos-arkisto]
            [aitu.infra.koulutustoimija-arkisto :as koulutustoimija-arkisto]
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

(defn ^:private nimi-sv [koodi]
  ((some-fn :sv :fi :en) (:nimi koodi)))

(defn ^:private postinumero [koodi]
  (when-let [postinumerokoodi (get-in koodi [:postiosoite :postinumeroUri])]
    (subs postinumerokoodi 6)))

(defn ^:private email [koodi]
  (some :email (:yhteystiedot koodi)))

(defn ^:private www-osoite [koodi]
  (some :www (:yhteystiedot koodi)))

(defn ^:private puhelin [koodi]
  (:numero (some-value #(= "puhelin" (:tyyppi %)) (:yhteystiedot koodi))))

(defn ^:private y-tunnus [koodi]
  (or (:ytunnus koodi) (:virastoTunnus koodi)))

(defn ^:private koodi->koulutustoimija [koodi]
  {:nimi_fi (nimi koodi)
   :nimi_sv (nimi-sv koodi)
   :oid (:oid koodi)
   :sahkoposti (email koodi)
   :puhelin (puhelin koodi)
   :osoite (get-in koodi [:postiosoite :osoite])
   :postinumero (postinumero koodi)
   :postitoimipaikka (get-in koodi [:postiosoite :postitoimipaikka])
   :www_osoite (www-osoite koodi)
   :ytunnus (y-tunnus koodi)})

(defn ^:private koodi->oppilaitos [koodi]
  {:nimi (nimi koodi)
   :oid (:oid koodi)
   :sahkoposti (email koodi)
   :puhelin (puhelin koodi)
   :osoite (get-in koodi [:postiosoite :osoite])
   :postinumero (postinumero koodi)
   :postitoimipaikka (get-in koodi [:postiosoite :postitoimipaikka])
   :www_osoite (www-osoite koodi)
   :oppilaitoskoodi (:oppilaitosKoodi koodi)})

(defn ^:private koodi->toimipaikka [koodi]
  {:nimi (nimi koodi)
   :oid (:oid koodi)
   :sahkoposti (email koodi)
   :puhelin (puhelin koodi)
   :osoite (get-in koodi [:postiosoite :osoite])
   :postinumero (postinumero koodi)
   :postitoimipaikka (get-in koodi [:postiosoite :postitoimipaikka])
   :www_osoite (www-osoite koodi)
   :toimipaikkakoodi (:toimipistekoodi koodi)})

(defn ^:private koulutustoimijan-kentat [koulutustoimija]
  (when koulutustoimija
    (select-keys koulutustoimija [:nimi_fi :nimi_sv :oid :sahkoposti :puhelin :osoite
                                  :postinumero :postitoimipaikka :www_osoite
                                  :ytunnus])))

(defn ^:private oppilaitoksen-kentat [oppilaitos]
  (when oppilaitos
    (select-keys oppilaitos [:nimi :oid :sahkoposti :puhelin :osoite
                             :postinumero :postitoimipaikka :www_osoite
                             :oppilaitoskoodi :koulutustoimija])))

(defn ^:private toimipaikan-kentat [toimipaikka]
  (when toimipaikka
    (select-keys toimipaikka [:nimi :oid :sahkoposti :puhelin :osoite
                              :postinumero :postitoimipaikka :www_osoite
                              :toimipaikkakoodi :oppilaitos])))

(defn ^:private tyyppi [koodi]
  (cond
    (some #{"Koulutustoimija"} (:tyypit koodi)) :koulutustoimija
    (haluttu-tyyppi? koodi) :oppilaitos
    (:toimipistekoodi koodi) :toimipaikka))

(defn ^:private oid-polku [koulutustoimijakoodit oppilaitoskoodit]
  (loop [oid->ytunnus (into {} (for [kt koulutustoimijakoodit]
                                 [(:oid kt) (:ytunnus kt)]))
         oppilaitoskoodit oppilaitoskoodit]
    (if (seq oppilaitoskoodit)
      (recur (into oid->ytunnus (for [o oppilaitoskoodit
                                      :when (contains? oid->ytunnus (:parentOid o))]
                                  [(:oid o) (oid->ytunnus (:parentOid o))]))
             (remove #(contains? oid->ytunnus (:parentOid %)) oppilaitoskoodit))
      oid->ytunnus)))

(defn ^:private paivita-koulutustoimijat! [koodit]
  (let [koulutustoimijat (->> (koulutustoimija-arkisto/hae-kaikki)
                           (map-by :ytunnus))]
    (doseq [koodi (vals (map-by y-tunnus koodit)) ;; Poistetaan duplikaatit
            :let [uusi-kt (koodi->koulutustoimija koodi)
                  y-tunnus (:ytunnus uusi-kt)
                  vanha-kt (koulutustoimijan-kentat (get koulutustoimijat y-tunnus))]
            :when y-tunnus]
      (cond
        (nil? vanha-kt) (do
                          (log/info "Uusi koulutustoimija: " (:ytunnus uusi-kt))
                          (koulutustoimija-arkisto/lisaa! uusi-kt))
        (not= vanha-kt uusi-kt) (do
                                  (log/info "Muuttunut koulutustoimija: " (:ytunnus uusi-kt))
                                  (koulutustoimija-arkisto/paivita! uusi-kt))))))

(defn ^:private paivita-oppilaitokset! [koodit koulutustoimijakoodit]
  (let [oid->ytunnus (oid-polku koulutustoimijakoodit koodit)
        oppilaitokset (->> (oppilaitos-arkisto/hae-kaikki)
                        (map-by :oppilaitoskoodi))]
    (doseq [koodi (vals (map-by :oppilaitosKoodi koodit)) ;; Poistetaan duplikaatit
            :when (contains? oid->ytunnus (:parentOid koodi))
            :let [oppilaitoskoodi (:oppilaitosKoodi koodi)
                  koulutustoimija (oid->ytunnus (:parentOid koodi))
                  vanha-oppilaitos (oppilaitoksen-kentat (get oppilaitokset oppilaitoskoodi))
                  uusi-oppilaitos (assoc (koodi->oppilaitos koodi)
                                         :koulutustoimija koulutustoimija)]]
      (cond
        (nil? vanha-oppilaitos) (do
                                  (log/info "Uusi oppilaitos: " (:oppilaitoskoodi uusi-oppilaitos))
                                  (oppilaitos-arkisto/lisaa! uusi-oppilaitos))
        (not= vanha-oppilaitos uusi-oppilaitos) (do
                                                  (log/info "Muuttunut oppilaitos: " (:oppilaitoskoodi uusi-oppilaitos))
                                                  (oppilaitos-arkisto/paivita! uusi-oppilaitos))))))


(defn ^:private paivita-toimipaikat! [koodit oppilaitoskoodit]
  (let [oid->oppilaitostunnus (into {} (for [o oppilaitoskoodit]
                                         [(:oid o) (:oppilaitosKoodi o)]))
        toimipaikat (->> (oppilaitos-arkisto/hae-kaikki-toimipaikat)
                      (map-by :toimipaikkakoodi))]
    (doseq [koodi (vals (map-by :toimipistekoodi koodit)) ;; Poistetaan duplikaatit
            :when (contains? oid->oppilaitostunnus (:parentOid koodi))
            :let [toimipaikkakoodi (:toimipistekoodi koodi)
                  oppilaitos (oid->oppilaitostunnus (:parentOid koodi))
                  vanha-toimipaikka (toimipaikan-kentat (get toimipaikat toimipaikkakoodi))
                  uusi-toimipaikka (assoc (koodi->toimipaikka koodi)
                                          :oppilaitos oppilaitos)]]
      (cond
        (nil? vanha-toimipaikka) (do
                                   (log/info "Uusi toimipaikka: " (:toimipaikkakoodi uusi-toimipaikka))
                                   (oppilaitos-arkisto/lisaa-toimipaikka! uusi-toimipaikka))
        (not= vanha-toimipaikka uusi-toimipaikka) (do
                                                    (log/info "Muuttunut toimipaikka: " (:toimipaikkakoodi uusi-toimipaikka))
                                                    (oppilaitos-arkisto/paivita-toimipaikka! uusi-toimipaikka))))))

(defn paivita-organisaatiot!
  [asetukset]
  (log/info "Aloitetaan organisaatioiden päivitys organisaatiopalvelusta")
  (let [kaikki-koodit (hae-kaikki (get asetukset "url"))
        koodit (group-by tyyppi kaikki-koodit)
        _ (log/info "Haettu kaikki organisaatiot")
        koulutustoimijakoodit (:koulutustoimija koodit)
        oppilaitoskoodit (:oppilaitos koodit)
        toimipaikkakoodit (:toimipaikka koodit)]
    (paivita-koulutustoimijat! koulutustoimijakoodit)
    (paivita-oppilaitokset! oppilaitoskoodit koulutustoimijakoodit)
    (paivita-toimipaikat! toimipaikkakoodit oppilaitoskoodit)))
