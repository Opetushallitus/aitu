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

(ns aitu.toimiala.kayttajaoikeudet
  "https://knowledge.solita.fi/pages/viewpage.action?pageId=56984327"
  (:require [aitu.infra.jarjestamissopimus-arkisto :as jarjestamissopimus-arkisto]))

(def ^:dynamic *current-user-authmap*)

;; kayttajarooli-taulun arvot
(def yllapitajarooli "YLLAPITAJA")
(def kayttajarooli "KAYTTAJA")
(def osoitepalvelurooli "OSOITEPALVELU")

(def toimikunnan-muokkaus-roolit #{"puheenjohtaja",
                                   "varapuheenjohtaja",
                                   "sihteeri",
                                   "jasen",
                                   "ulkopuolinensihteeri"})

(defn yllapitaja?
  ([kayttaja-map]
    {:pre [(contains? #{yllapitajarooli kayttajarooli} (:roolitunnus kayttaja-map))]}
    (= yllapitajarooli (:roolitunnus kayttaja-map)))
  ([] (yllapitaja? *current-user-authmap*)))

(defn toimikunta-jasen?
  ([kayttaja-map toimikuntaid]
   (some #(= % toimikuntaid) (map :tkunta (:toimikunta kayttaja-map))))
  ([toimikuntaid] (toimikunta-jasen? *current-user-authmap* toimikuntaid)))

(defn toimikunnan-muokkausoikeus?
  ([kayttaja-map toimikuntaid]
  (let [toimikunnan_jasenyydet (filter #(= (:tkunta %) toimikuntaid) (:toimikunta kayttaja-map))
        roolit (map :rooli toimikunnan_jasenyydet)]
    (some toimikunnan-muokkaus-roolit roolit)))
  ([toimikuntaid] (toimikunnan-muokkausoikeus? *current-user-authmap* toimikuntaid)))

(defn aitu-kayttaja?
  ([x] (aitu-kayttaja?))
  ([]
    (let [roolitunnus (:roolitunnus *current-user-authmap*)]
      (or (= roolitunnus yllapitajarooli)
          (= roolitunnus kayttajarooli)))))

(defn osoitepalvelu-kayttaja?
  ([x] (osoitepalvelu-kayttaja?))
  ([]
    (let [roolitunnus (:roolitunnus *current-user-authmap*)]
      (or (= roolitunnus osoitepalvelurooli)
          (= roolitunnus yllapitajarooli)))))

(def sallittu-kaikille (constantly true))

(defn sallittu-yllapitajalle [& _] (yllapitaja?))

(defn int-arvo [arvo]
  {:post [(= (type %) Integer)]}
  (if (= (type arvo) String)
    (Integer/parseInt arvo)
    arvo))

;; Tämän mapin arvoja käytetään makron `aitu.compojure-util/defapi-with-auth`
;; muodostamassa koodissa, joten ne eivät saa olla funktio-olioita
;; (ks. http://stackoverflow.com/a/11287181).
(def yllapitotoiminnot
  `{:toimikunta_luonti  sallittu-yllapitajalle
    :toimikunta_paivitys sallittu-yllapitajalle
    :henkilo_lisays  sallittu-yllapitajalle
    :toimikuntajasen_yllapito sallittu-yllapitajalle
    :tiedote_muokkaus sallittu-yllapitajalle
    :status sallittu-yllapitajalle
    :ohje_muokkaus sallittu-yllapitajalle
    :etusivu_haku sallittu-yllapitajalle})

;; Kuten yllä, arvot eivät saa olla funktio-olioita.
(def kayttajatoiminnot
  `{:henkilo_paivitys #(or (yllapitaja?) (= (:henkiloid *current-user-authmap*) %))
    :omat_tiedot #(or (yllapitaja?) (= (:oid *current-user-authmap*) %))
    :logitus aitu-kayttaja?
    :kayttajan_tiedot aitu-kayttaja?
    :ohjeet_luku aitu-kayttaja?
    :toimikunta_haku aitu-kayttaja?
    :toimikunta_katselu aitu-kayttaja?
    :etusivu aitu-kayttaja?
    :henkilo_haku aitu-kayttaja?
    :yleinen-rest-api sallittu-kaikille
    :osoitepalvelu-api osoitepalvelu-kayttaja?})

(def sopimustoiminnot
  `{:sopimustiedot_paivitys #(or (yllapitaja?) (toimikunnan-muokkausoikeus? (jarjestamissopimus-arkisto/hae-jarjestamissopimuksen-toimikunta (int-arvo %))))
    :sopimustiedot_luku #(or (yllapitaja?) (toimikunta-jasen? (jarjestamissopimus-arkisto/hae-jarjestamissopimuksen-toimikunta (int-arvo %))))
    :suunnitelma_luku #(or (yllapitaja?) (toimikunta-jasen? (jarjestamissopimus-arkisto/hae-jarjestamissopimuksen-toimikunta (int-arvo %))))
    :sopimuksen_liite_luku #(or (yllapitaja?) (toimikunta-jasen? (jarjestamissopimus-arkisto/hae-jarjestamissopimuksen-toimikunta (int-arvo %))))})


(def toimikuntatoiminnot
  `{:sopimus_lisays  #(or (yllapitaja?) (toimikunnan-muokkausoikeus? %))})

(def toiminnot (conj yllapitotoiminnot kayttajatoiminnot toimikuntatoiminnot sopimustoiminnot))

(defn kayttajan-oikeudet [t id]
  (for [toiminto (seq t)
        :let [sallittu ((eval (val toiminto)) id)]
        :when sallittu]
    (key toiminto)))

(defn lisaa-kayttajan-oikeudet [entityt t avain]
  (map #(assoc % :oikeudet (kayttajan-oikeudet t (avain %)) :tunniste (avain %)) entityt))

(defn paivita-kayttajan-toimikuntakohtaiset-oikeudet [kayttajan-tiedot]
  (update-in kayttajan-tiedot [:toimikunta] lisaa-kayttajan-oikeudet toimikuntatoiminnot :tkunta))

(defn paivita-kayttajan-sopimuskohtaiset-oikeudet [kayttajan-tiedot]
  (update-in kayttajan-tiedot [:jarjestamissopimus] lisaa-kayttajan-oikeudet sopimustoiminnot :jarjestamissopimusid))
