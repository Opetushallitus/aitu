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
  "https://confluence.csc.fi/pages/viewpage.action?pageId=53519037"
  (:require [aitu.infra.jarjestamissopimus-arkisto :as jarjestamissopimus-arkisto]
            [aitu.infra.ttk-arkisto :as ttk-arkisto]
            [aitu.infra.kayttaja-arkisto :as kayttaja-arkisto]
            [aitu.toimiala.voimassaolo.saanto.toimikunta :refer [toimikunta-vanhentunut?]]
            [aitu.toimiala.voimassaolo.saanto.jasenyys :refer [taydenna-jasenyyden-voimassaolo]]
            [aitu.toimiala.kayttajaroolit :refer :all]))

(def ^:dynamic *current-user-authmap*)
(def ^:dynamic *impersonoitu-oid* nil)

(defn int-arvo [arvo]
  {:post [(integer? %)]}
  (if (= (type arvo) String)
    (Integer/parseInt arvo)
    arvo))

(def toimikunnan-muokkaus-roolit #{"puheenjohtaja",
                                   "varapuheenjohtaja",
                                   "sihteeri",
                                   "jasen",
                                   "ulkopuolinensihteeri"})

(defn onko-kayttajan-rooli?
  ([kayttaja-map rooli]
    {:pre [(some #{(:roolitunnus kayttaja-map)} (vals kayttajaroolit))]}
    (= rooli (:roolitunnus kayttaja-map)))
  ([rooli-kw]
    (onko-kayttajan-rooli? *current-user-authmap* (rooli-kw kayttajaroolit))))
  

(defn yllapitajarooli?
  [rooli]
  (= (:yllapitaja kayttajaroolit) rooli))

(defn yllapitaja? []
  (onko-kayttajan-rooli? :yllapitaja))
 
(defn oph-katselija? []
  (onko-kayttajan-rooli? :oph-katselija))

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

(defn omien-tietojen-muokkausoikeus?
  [kayttaja-map henkiloid kayttaja-oid]
  (and (= (:henkiloid *current-user-authmap*) henkiloid)
       (kayttaja-arkisto/kayttaja-liitetty-henkiloon? (int-arvo henkiloid) kayttaja-oid)))

(defn henkilon-muokkausoikeus-toimikunnan-kautta?
  [kayttaja-map henkiloid kayttaja-oid]
  (let [henkilon-toimikunnat (filter jasenyys-voimassa? (ttk-arkisto/hae-toimikuntien-jasenyydet henkiloid))]
    (and (some #(toimikunnan-muokkausoikeus? kayttaja-map (:toimikunta %)) henkilon-toimikunnat)
         (kayttaja-arkisto/kayttaja-liitetty-henkiloon? henkiloid kayttaja-oid))))

(defn aitu-kayttaja?
  "Mikä tahansa käyttäjärooli Aituun, joka ei ole järjestelmärajapintaan liittyvä."
  []
  (let [roolitunnus (:roolitunnus *current-user-authmap*)
        roolitunnukset (vals (select-keys kayttajaroolit ihmiskayttajat))]
    (some #(= % roolitunnus) roolitunnukset)))

(defn osoitepalvelu-kayttaja? [] 
  (or (onko-kayttajan-rooli? :osoitepalvelu))
      (onko-kayttajan-rooli? :yllapitaja))

(defn aipal-kayttaja? [] 
  (or (onko-kayttajan-rooli? :aipal))
      (onko-kayttajan-rooli? :yllapitaja ))

(defn jarjesto-kayttaja? [] 
  (onko-kayttajan-rooli? :jarjesto))

(def sallittu-kaikille 
  "Sallittu kaikille, ilman autentikointia. Tätä on tarkoitus käyttää vain julkisesti saatavilla olevien tietojen välittämiseen."
  (constantly true))

(defn sallittu-yllapitajalle-ja-jarjestolle [& _] (or (yllapitaja?) (jarjesto-kayttaja?)))

(defn toimikunnan-katselu? 
  "Sallittu muille käyttäjille, mutta ei järjestökäyttäjälle."
  ([x] (toimikunnan-katselu?))
  ([]
    (and (aitu-kayttaja?)      
      (let [roolitunnus (:roolitunnus *current-user-authmap*)]
        (not (= roolitunnus (:jarjesto kayttajaroolit)))))))

(defn toimikunnan-paivitys?
  "Sallittu ylläpitäjälle ja toimikunnan sihteerille kun ammattisihteerit saadaan."
  ; TODO: Sihteeriroolin käsittely
  [tkunta]
  (yllapitaja?))

(defn sallittu-impersonoidulle [& _]
  (or (yllapitaja?) (not= *impersonoitu-oid* nil)))

;; Tämän mapin arvoja käytetään makron `aitu.compojure-util/defapi-with-auth`
;; muodostamassa koodissa, joten ne eivät saa olla funktio-olioita
;; (ks. http://stackoverflow.com/a/11287181).
(def yllapitotoiminnot
  `{:toimikunta_luonti yllapitaja?
    :toimikunta_paivitys toimikunnan-paivitys?
    :raportointi yllapitaja?
    :toimikuntajasen_yllapito yllapitaja?
    :toimikuntakayttaja-listaus yllapitaja?
    :tiedote_muokkaus yllapitaja?
    :status yllapitaja?
    :ohje_muokkaus yllapitaja?
    :etusivu_haku yllapitaja?
    :impersonointi yllapitaja?
    :organisaatiomuutos yllapitaja?
    :raportti yllapitaja?
    :paatos yllapitaja?})

(defn jasenesityksen-poisto-sallittu? [jasenyysid]
  (let [jasenyys (ttk-arkisto/hae-jasen jasenyysid)
        luotu-kayttaja (:luotu_kayttaja jasenyys)
        kirjautunut-kayttaja (:oid *current-user-authmap*)]
    (and (= "esitetty" (:status jasenyys))
         (= luotu-kayttaja kirjautunut-kayttaja))))

;; Kuten yllä, arvot eivät saa olla funktio-olioita.
(def kayttajatoiminnot
  `{:omat_tiedot #(or (yllapitaja?) (= (:oid *current-user-authmap*) %))
    :logitus aitu-kayttaja?
    :kayttajan_tiedot aitu-kayttaja?
    :ohjeet_luku aitu-kayttaja?
    :toimikunta_haku aitu-kayttaja?
    :toimikunta_katselu toimikunnan-katselu?
    :etusivu aitu-kayttaja?
    :henkilo_haku aitu-kayttaja?
    :yleinen-rest-api sallittu-kaikille
    :osoitepalvelu-api osoitepalvelu-kayttaja?
    :aipal  aipal-kayttaja?
    :impersonointi-lopetus sallittu-impersonoidulle
    :henkilo_lisays sallittu-yllapitajalle-ja-jarjestolle
    :toimikuntajasen_lisays sallittu-yllapitajalle-ja-jarjestolle
    :jasenesitykset sallittu-yllapitajalle-ja-jarjestolle
    :jasenesitys-poisto #(or (yllapitaja?) (jasenesityksen-poisto-sallittu? (int-arvo %)))
    :arviointipaatos yllapitaja? ; väliaikainen tilanne. pitää määritellä tarkemmin kenelle muille oikeuksia annetaan
    })

(defn sopimuksen-muokkaus-sallittu? [sopimusid]
  (let [id (int-arvo sopimusid)]
    (or (yllapitaja?)
      (let [sopimus (jarjestamissopimus-arkisto/hae id)
            voimassa? (:voimassa sopimus)]
        (and voimassa? (toimikunnan-muokkausoikeus? (jarjestamissopimus-arkisto/hae-jarjestamissopimuksen-toimikunta id)))))))

(def sopimustoiminnot
  `{:sopimustiedot_paivitys sopimuksen-muokkaus-sallittu?
    :sopimustiedot_luku #(or (yllapitaja?) (oph-katselija?) (toimikunta-jasen? (jarjestamissopimus-arkisto/hae-jarjestamissopimuksen-toimikunta (int-arvo %))))
    :suunnitelma_luku #(or (yllapitaja?) (oph-katselija?) (toimikunta-jasen? (jarjestamissopimus-arkisto/hae-jarjestamissopimuksen-toimikunta (int-arvo %))))
    :sopimuksen_liite_luku #(or (yllapitaja?) (oph-katselija?) (toimikunta-jasen? (jarjestamissopimus-arkisto/hae-jarjestamissopimuksen-toimikunta (int-arvo %))))})


(def toimikuntatoiminnot
  `{:sopimus_lisays  #(or (yllapitaja?) (toimikunnan-muokkausoikeus? %))})

(def henkilotoiminnot
  `{:henkilo_paivitys #(or (yllapitaja?)
                           (omien-tietojen-muokkausoikeus? *current-user-authmap* (:henkiloid %) (:kayttaja %))
                           (henkilon-muokkausoikeus-toimikunnan-kautta? *current-user-authmap* (int-arvo (:henkiloid %)) (:kayttaja %)))})

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
