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
  (:require [aitu.infra.jarjestamissopimus-arkisto :as jarjestamissopimus-arkisto]
            [aitu.infra.ttk-arkisto :as ttk-arkisto]
            [aitu.toimiala.voimassaolo.saanto.toimikunta :refer [toimikunta-vanhentunut?]]
            [aitu.toimiala.voimassaolo.saanto.jasenyys :refer [taydenna-jasenyyden-voimassaolo]]
            [aitu.toimiala.kayttajaroolit :refer :all]))

(def ^:dynamic *current-user-authmap*)
(def ^:dynamic *impersonoitu-oid* nil)

(def toimikunnan-muokkaus-roolit #{"puheenjohtaja",
                                   "varapuheenjohtaja",
                                   "sihteeri",
                                   "jasen",
                                   "ulkopuolinensihteeri"})

(defn onko-kayttajan-rooli?
  [kayttaja-map rooli]
  {:pre [(some #{(:roolitunnus kayttaja-map)} (vals kayttajaroolit))]}
  (= rooli (:roolitunnus kayttaja-map)))

(defn yllapitajarooli?
  [rooli]
  (= (:yllapitaja kayttajaroolit) rooli))

(defn yllapitaja?
  ([kayttaja-map]
   (onko-kayttajan-rooli? kayttaja-map (:yllapitaja kayttajaroolit)))
  ([] (yllapitaja? *current-user-authmap*)))

(defn oph-katselija? []
  (onko-kayttajan-rooli? *current-user-authmap* (:oph-katselija kayttajaroolit)))

(defn jasenyys-voimassa? [jasenyys]
  (:voimassa (taydenna-jasenyyden-voimassaolo jasenyys (not (toimikunta-vanhentunut? jasenyys)))))

(defn toimikunta-jasen?
  ([kayttaja-map toimikuntaid]
   (some #(= % toimikuntaid) (map :tkunta (filter jasenyys-voimassa? (:toimikunta kayttaja-map)))))
  ([toimikuntaid] (toimikunta-jasen? *current-user-authmap* toimikuntaid)))

(defn toimikunnan-muokkausoikeus?
  ([kayttaja-map toimikuntaid]
  (let [toimikunnan-voimassaolevat-jasenyydet (filter #(and (= (:tkunta %) toimikuntaid) (jasenyys-voimassa? %)) (:toimikunta kayttaja-map))
        roolit (map :rooli toimikunnan-voimassaolevat-jasenyydet)]
    (some toimikunnan-muokkaus-roolit roolit)))
  ([toimikuntaid] (toimikunnan-muokkausoikeus? *current-user-authmap* toimikuntaid)))

(defn henkilon-muokkausoikeus-toimikunnan-kautta?
  [kayttaja-map henkiloid]
  (let [henkilon-toimikunnat (filter jasenyys-voimassa? (ttk-arkisto/hae-toimikuntien-jasenyydet henkiloid))]
    (some #(toimikunnan-muokkausoikeus? kayttaja-map (:toimikunta %)) henkilon-toimikunnat)))

(defn aitu-kayttaja?
  ([x] (aitu-kayttaja?))
  ([]
    (let [roolitunnus (:roolitunnus *current-user-authmap*)]
      (or (= roolitunnus (:kayttaja kayttajaroolit))
          (= roolitunnus (:yllapitaja kayttajaroolit))
          (= roolitunnus (:oph-katselija kayttajaroolit))))))

(defn osoitepalvelu-kayttaja?
  ([x] (osoitepalvelu-kayttaja?))
  ([]
    (let [roolitunnus (:roolitunnus *current-user-authmap*)]
      (or (= roolitunnus (:osoitepalvelu kayttajaroolit))
          (= roolitunnus (:yllapitaja kayttajaroolit))))))

(def sallittu-kaikille (constantly true))

(defn sallittu-yllapitajalle [& _] (yllapitaja?))

(defn sallittu-impersonoidulle [& _]
  (or (yllapitaja?) (not= *impersonoitu-oid* nil)))

(defn int-arvo [arvo]
  {:post [(integer? %)]}
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
    :etusivu_haku sallittu-yllapitajalle
    :impersonointi sallittu-yllapitajalle
    :organisaatiomuutos sallittu-yllapitajalle
    :raportti sallittu-yllapitajalle})

;; Kuten yllä, arvot eivät saa olla funktio-olioita.
(def kayttajatoiminnot
  `{:omat_tiedot #(or (yllapitaja?) (= (:oid *current-user-authmap*) %))
    :logitus aitu-kayttaja?
    :kayttajan_tiedot aitu-kayttaja?
    :ohjeet_luku aitu-kayttaja?
    :toimikunta_haku aitu-kayttaja?
    :toimikunta_katselu aitu-kayttaja?
    :etusivu aitu-kayttaja?
    :henkilo_haku aitu-kayttaja?
    :yleinen-rest-api sallittu-kaikille
    :osoitepalvelu-api osoitepalvelu-kayttaja?
    :impersonointi-lopetus sallittu-impersonoidulle})

(def sopimustoiminnot
  `{:sopimustiedot_paivitys #(or (yllapitaja?) (toimikunnan-muokkausoikeus? (jarjestamissopimus-arkisto/hae-jarjestamissopimuksen-toimikunta (int-arvo %))))
    :sopimustiedot_luku #(or (yllapitaja?) (oph-katselija?) (toimikunta-jasen? (jarjestamissopimus-arkisto/hae-jarjestamissopimuksen-toimikunta (int-arvo %))))
    :suunnitelma_luku #(or (yllapitaja?) (oph-katselija?) (toimikunta-jasen? (jarjestamissopimus-arkisto/hae-jarjestamissopimuksen-toimikunta (int-arvo %))))
    :sopimuksen_liite_luku #(or (yllapitaja?) (oph-katselija?) (toimikunta-jasen? (jarjestamissopimus-arkisto/hae-jarjestamissopimuksen-toimikunta (int-arvo %))))})


(def toimikuntatoiminnot
  `{:sopimus_lisays  #(or (yllapitaja?) (toimikunnan-muokkausoikeus? %))})

(def henkilotoiminnot
  `{:henkilo_paivitys #(or (yllapitaja?) (= (:henkiloid *current-user-authmap*) %) (henkilon-muokkausoikeus-toimikunnan-kautta? *current-user-authmap* (int-arvo %)))})

(def toiminnot (conj yllapitotoiminnot kayttajatoiminnot toimikuntatoiminnot sopimustoiminnot henkilotoiminnot))

(defn evaluoi-oikeudet [toiminnot id]
  (for [toiminto (seq toiminnot)
        :let [sallittu ((eval (val toiminto)) id)]
        :when sallittu]
    (key toiminto)))

(defn henkilo-oikeudet [toiminnot _]
  (for [toiminto (seq toiminnot)]
    (key toiminto)))

(defn lisaa-kayttajan-oikeudet [entityt toiminnot-tarkastus-fn toiminnot avain]
  (map #(assoc % :oikeudet (toiminnot-tarkastus-fn toiminnot (avain %)) :tunniste (avain %)) entityt))

(defn paivita-kayttajan-toimikuntakohtaiset-oikeudet [kayttajan-tiedot]
  (update-in kayttajan-tiedot [:toimikunta] lisaa-kayttajan-oikeudet evaluoi-oikeudet toimikuntatoiminnot :tkunta))

(defn paivita-kayttajan-sopimuskohtaiset-oikeudet [kayttajan-tiedot]
  (update-in kayttajan-tiedot [:jarjestamissopimus] lisaa-kayttajan-oikeudet evaluoi-oikeudet sopimustoiminnot :jarjestamissopimusid))

(defn liita-kayttajan-henkilo-oikeudet [kayttajan-tiedot]
  (let [kayttajan-toimikunnat (filter jasenyys-voimassa? (:toimikunta kayttajan-tiedot))
        toimikunnat-joihin-muokkausrooli (filter #(some toimikunnan-muokkaus-roolit [(:rooli %)]) kayttajan-toimikunnat)
        muokattaviin-toimikuntiin-kuuluvat-henkilot (filter jasenyys-voimassa? (ttk-arkisto/hae-toimikuntien-henkilot (map :tkunta toimikunnat-joihin-muokkausrooli)))
        kaikki-muokattavat-henkilot (distinct (conj muokattaviin-toimikuntiin-kuuluvat-henkilot (select-keys kayttajan-tiedot [:henkiloid])))]
    (-> kayttajan-tiedot
        (assoc :henkilo kaikki-muokattavat-henkilot)
        (update-in [:henkilo] lisaa-kayttajan-oikeudet henkilo-oikeudet henkilotoiminnot :henkiloid))))
