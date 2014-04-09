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

(def toimikunnan-muokkaus-roolit #{"puheenjohtaja", "varapuheenjohtaja", "sihteeri"})

(defn yllapitaja?
  ([kayttaja-map]
    {:pre [(contains? #{yllapitajarooli kayttajarooli} (:roolitunnus kayttaja-map))]}
    (= yllapitajarooli (:roolitunnus kayttaja-map)))
  ([] (yllapitaja? *current-user-authmap*)))

(defn toimikunta-jasen?
  ([kayttaja-map toimikuntaid]
    (some #(= % toimikuntaid) (map :tkunta (:toimikunta_jasen kayttaja-map))))
  ([toimikuntaid] (toimikunta-jasen? *current-user-authmap* toimikuntaid)))

(defn toimikunnan-muokkausoikeus?
  ([kayttaja-map toimikuntaid]
  (let [toimikunnan_jasenyydet (filter #(= (:tkunta %) toimikuntaid) (:toimikunta_jasen kayttaja-map))
        roolit (map :rooli toimikunnan_jasenyydet)]
    (some toimikunnan-muokkaus-roolit roolit)))
  ([toimikuntaid] (toimikunnan-muokkausoikeus? *current-user-authmap* toimikuntaid)))

(defn aitu-kayttaja?
  ([x] (aitu-kayttaja?))
  ([]
    (let [roolitunnus (:roolitunnus *current-user-authmap*)]
      (or (= roolitunnus yllapitajarooli)
          (= roolitunnus kayttajarooli)))))

(def sallittu-kaikille (constantly true))

;; Tämän mapin arvoja käytetään makron `aitu.compojure-util/defapi-with-auth`
;; muodostamassa koodissa, joten ne eivät saa olla funktio-olioita
;; (ks. http://stackoverflow.com/a/11287181).
(def yllapitotoiminnot
  `{:toimikunta_luonti  yllapitaja?
    :henkilo_lisays  yllapitaja?
    :toimikuntajasen_yllapito yllapitaja?
    :tiedote_muokkaus yllapitaja?
    :status yllapitaja?
    :ohje_muokkaus yllapitaja?
    :etusivu_haku yllapitaja?})

;; Kuten yllä, arvot eivät saa olla funktio-olioita.
(def kayttajatoiminnot
  `{:toimikunta_paivitys #(or (yllapitaja?) (toimikunta-jasen? %))
    :toimikunta_katselu #(or (yllapitaja?) (toimikunta-jasen? %))
    :henkilo_paivitys #(or (yllapitaja?) (= (:henkiloid *current-user-authmap*) %))
    :omat_tiedot #(or (yllapitaja?) (= (:oid *current-user-authmap*) %))
    :sopimus_lisays  #(or (yllapitaja?) (toimikunnan-muokkausoikeus? %))
    :sopimustiedot_paivitys #(or (yllapitaja?) (toimikunnan-muokkausoikeus? (jarjestamissopimus-arkisto/hae-jarjestamissopimuksen-toimikunta (Integer/parseInt %))))
    :sopimustiedot_luku #(or (yllapitaja?) (toimikunta-jasen? (jarjestamissopimus-arkisto/hae-jarjestamissopimuksen-toimikunta (Integer/parseInt %))))
    :suunnitelma_luku #(or (yllapitaja?) (toimikunta-jasen? (jarjestamissopimus-arkisto/hae-jarjestamissopimuksen-toimikunta (Integer/parseInt %))))
    :sopimuksen_liite_luku #(or (yllapitaja?) (toimikunta-jasen? (jarjestamissopimus-arkisto/hae-jarjestamissopimuksen-toimikunta (Integer/parseInt %))))
    :logitus aitu-kayttaja?
    :ohjeet_luku aitu-kayttaja?
    :toimikunta_haku sallittu-kaikille
    :etusivu sallittu-kaikille
    :henkilo_haku sallittu-kaikille
    :yleinen-rest-api sallittu-kaikille})

(def toiminnot (conj yllapitotoiminnot kayttajatoiminnot))
