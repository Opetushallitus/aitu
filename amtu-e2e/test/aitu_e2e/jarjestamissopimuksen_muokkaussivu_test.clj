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

(ns aitu-e2e.jarjestamissopimuksen-muokkaussivu-test
  (:require [clojure.test :refer [deftest is testing]]
            [aitu-e2e.jarjestamissopimussivu-test :refer [sopimussivu jarjestamissopimus-data]]
            [clj-webdriver.taxi :as w]
            [clj-http.client :as hc]
            [aitu-e2e.util :refer :all]
            [aitu-e2e.data-util :as du]))

(defn avaa-sopimuksen-muokkaussivu [jarjestamissopimusid]
  (avaa (sopimussivu jarjestamissopimusid))
  (w/click "button[ng-click=\"muokkaa()\"]")
  (odota-angular-pyyntoa))

(defn siirry-sopimuksen-muokkaussivulle [jarjestamissopimusid]
  (avaa (str "/fi/#/sopimus/" jarjestamissopimusid "/muokkaa")))

(defn tutkintolistan-kentan-arvo
  "Hakee tutkintolistalta ensimmäisestä tutkinnosta kentän arvon"
  [kentta]
  (-> *ng* (.repeater "sopimusJaTutkinto in sopimus.sopimus_ja_tutkinto") (.column kentta) (w/find-element) (w/text)))

(defn poista-suunnitelma []
  (w/click {:css "ul.file-list button[ng-click=\"poistaSuunnitelma()\"]"}))

(defn odota-poistodialogia []
  (odota-dialogia #"Olet poistamassa järjestämissuunnitelmaa."))

(defn filejen-lkm []
  (count (w/find-elements {:css "ul.file-list li"})))

(defn jarjestamissopimus-data-with-suunnitelma []
  (assoc jarjestamissopimus-data :jarjestamissuunnitelmat [{:tutkintoversio 1 :jarjestamissopimusid 1230}]))

(defn avaa-tutkinnon-tiedot []
  (w/click ".accordion-header")
  (odota-angular-pyyntoa))

(def testi-tutkinnonosat
  [{:osatunnus "testi1"
    :tutkintotunnus "TU1"
    :nimi "Tutkinnon osa 1"
    :kuvaus "Ensimmäisen tutkinnonosan kuvaus"
    :jarjestysnumero 1
    :tutkintoversio 1}
   {:osatunnus "testi2"
    :tutkintotunnus "TU1"
    :nimi "Tutkinnon osa 2"
    :kuvaus "Toisen tutkinnonosan kuvaus"
    :jarjestysnumero 2
    :tutkintoversio 1}])

(def testi-osaamisala
  {:osaamisalatunnus "ala1"
   :nimi "Osaamisala 1"
   :kuvaus "Osaamisalan kuvaus"
   :tutkintoversio 1
   :voimassa_alkupvm du/menneisyydessa})

(def testi-toimipaikat
  [{:nimi "Toimipaikka 1"
    :oppilaitos "12345"}
   {:nimi "Toimipaikka 2"
    :oppilaitos "12345"}])

(defn jarjestamissopimus-data-with-tutkinnon-osat []
  (->
    jarjestamissopimus-data
    (assoc :tutkinnonosat testi-tutkinnonosat)
    (assoc :osaamisalat [testi-osaamisala])
    (assoc :toimipaikat testi-toimipaikat)))

(defn valitse-osat []
  (w/click ".sopimus-tutkinnon-osat input[type=\"radio\"][value=\"false\"]")
  (odota-angular-pyyntoa))

(defn valitut-tutkinnon-osat []
  (set (map w/text (w/find-elements {:css "table.sopimuksen-tutkinnonosien-valinta.tutkinnonosat tr.valittu td span.nimi"}))))

(defn jarjestysnumerot []
  (set (map w/text (w/find-elements {:css "table.sopimuksen-tutkinnonosien-valinta.tutkinnonosat td.jarjestysnumero"}))))

(defn tutkinnonosien-kuvaukset []
  (set (map w/text (w/find-elements {:css "table.sopimuksen-tutkinnonosien-valinta.tutkinnonosat td span.kuvaus"}))))

(defn osaamisalojen-kuvaukset []
  (set (map w/text (w/find-elements {:css "table.sopimuksen-tutkinnonosien-valinta.osaamisalat td span.kuvaus"}))))

(defn valitut-osaamisalat []
  (set (map w/text (w/find-elements {:css "table.sopimuksen-tutkinnonosien-valinta.osaamisalat tr.valittu td span.nimi"}))))

(defn syota-vastuuhenkilon-tietokenttaan
  [luokkapolku kentta arvo]
  (w/input-text {:css (str luokkapolku "[ng-model=\"" kentta "\"]")} arvo))

(defn loytyyko-vastuuhenkilon-tieto
  [luokka haettava-arvo]
  (not
    (nil?
      (hae-teksti-jquery-selektorilla (str "$('." luokka " fieldset > div:contains(\"" haettava-arvo "\")')")))))

(defn nayttotutkintomestari-kentan-arvo
  [luokka]
  (hae-teksti-jquery-selektorilla (str "$('." luokka " fieldset > div:contains(\"Näyttötutkintomestari:\")')")))


(deftest ^:no-ie jarjestamissopimuksen-muokkaussivu-test
  (testing "Järjestämissopimuksen muokkausivu aukeaa kun painaa muokkaa nappia sopimuksen tietosivulta"
    (with-webdriver
      (du/with-data jarjestamissopimus-data
        (avaa-sopimuksen-muokkaussivu 1230)
        (w/visible? (w/find-element {:css "button[ng-click=\"tallenna()\"]"})))))
  (testing "Jarjestamissopimuksen muokkaus onnistuu vaihtamatta kenttien arvoja"
    (with-webdriver
      (du/with-data jarjestamissopimus-data
        (avaa-sopimuksen-muokkaussivu 1230)
        (tallenna)
        (is (= (viestin-teksti) "Järjestämissopimuksen tietoja muokattu")))))
  (testing "Jarjestamissopimuksen muokkaus onnistuu kun kenttiin syöttää validit arvot"
    (testing "Jarjestamissopimuksen muokkaus onnistuu kun kenttiin syöttää validit arvot"
      (with-webdriver
        (du/with-data jarjestamissopimus-data
          (avaa-sopimuksen-muokkaussivu 1230)
          (syota-pvm "sopimus.alkupvm" "24.03.1980")
          (syota-pvm "sopimus.loppupvm" "01.02.2199")
          (valitse-select2-optio "sopimus.koulutustoimija" "ytunnus" "Hanhivaaran kaupunki")
          (valitse-select2-optio "sopimus.tutkintotilaisuuksista_vastaava_oppilaitos" "oppilaitoskoodi" "Hanhivaaran urheiluopisto")
          (syota-kenttaan "sopimus.vastuuhenkilo" "Roope Ankka")
          (syota-kenttaan "sopimus.sahkoposti" "rankka@jokudomain.com")
          (syota-kenttaan "sopimus.puhelin" "040123456")

          (syota-vastuuhenkilon-tietokenttaan ".vastuuhenkilo input" "nimi" "Aku Ankka")
          (syota-vastuuhenkilon-tietokenttaan ".vastuuhenkilo input" "sahkoposti" "aankka@jokudomain.com")
          (syota-vastuuhenkilon-tietokenttaan ".vastuuhenkilo input" "puhelin" "040234567")
          (w/select-option {:css ".vastuuhenkilo select"} {:text "ei"})
          (syota-vastuuhenkilon-tietokenttaan ".vastuuhenkilo textarea" "lisatiedot" "Lisätietoja Akusta")

          (syota-vastuuhenkilon-tietokenttaan ".varavastuuhenkilo input" "nimi" "Hannu Hanhi")
          (syota-vastuuhenkilon-tietokenttaan ".varavastuuhenkilo input" "sahkoposti" "hhanhi@jokudomain.com")
          (syota-vastuuhenkilon-tietokenttaan ".varavastuuhenkilo input" "puhelin" "04054321")
          (w/select-option {:css ".varavastuuhenkilo select"} {:text "kyllä"})
          (syota-vastuuhenkilon-tietokenttaan ".varavastuuhenkilo textarea" "lisatiedot" "Lisätietoja Hannusta")

          (w/select-option {:css "span[nimi=\"kieli\"] > select"} {:text "suomi"})
          (odota-angular-pyyntoa)
          (tallenna)
          (is (= (viestin-teksti) "Järjestämissopimuksen tietoja muokattu"))
          (avaa-tutkinnon-tiedot)
          (testing "Sopimuksen perustiedot tallettuvat oikein"
            (is (= (elementin-teksti "sopimus.alkupvm") "24.03.1980 - 01.02.2199"))
            (is (= (elementin-teksti "sopimus.koulutustoimija.nimi") "Hanhivaaran kaupunki"))
            (is (= (elementin-teksti "sopimus.tutkintotilaisuuksista_vastaava_oppilaitos.nimi") "Hanhivaaran urheiluopisto"))
            (is (= (elementin-teksti "sopimus.vastuuhenkilo") "Roope Ankka"))
            (is (= (elementin-teksti "sopimus.sahkoposti") "rankka@jokudomain.com"))
            (is (= (elementin-teksti "sopimus.puhelin") "040123456"))
            (is (= (w/text (w/find-element {:css "p > span[nimi=\"kieli\"]"})) "suomi")))
          (testing "Sopimuksen vastuuhenkilön tiedot tallettuvat oikein"
            (is (loytyyko-vastuuhenkilon-tieto "vastuuhenkilo" "Aku Ankka"))
            (is (loytyyko-vastuuhenkilon-tieto "vastuuhenkilo" "aankka@jokudomain.com"))
            (is (loytyyko-vastuuhenkilon-tieto "vastuuhenkilo" "040234567"))
            (is (= (nayttotutkintomestari-kentan-arvo "vastuuhenkilo") "Näyttötutkintomestari: ei"))
            (is (loytyyko-vastuuhenkilon-tieto "vastuuhenkilo" "Lisätietoja Akusta")))
          (testing "Sopimuksen varavastuuhenkilön tiedot tallettuvat oikein"
            (is (loytyyko-vastuuhenkilon-tieto "varavastuuhenkilo" "Hannu Hanhi"))
            (is (loytyyko-vastuuhenkilon-tieto "varavastuuhenkilo" "hhanhi@jokudomain.com"))
            (is (loytyyko-vastuuhenkilon-tieto "varavastuuhenkilo" "04054321"))
            (is (= (nayttotutkintomestari-kentan-arvo "varavastuuhenkilo") "Näyttötutkintomestari: kyllä"))
            (is (loytyyko-vastuuhenkilon-tieto "varavastuuhenkilo" "Lisätietoja Hannusta")))))))
  (testing "Jarjestamissopimuksen muokkaus ei onnistu, jos pakollisia tietoja puuttuu"
    (with-webdriver
      (du/with-data jarjestamissopimus-data
        (avaa-sopimuksen-muokkaussivu 1230)
        (syota-pvm "sopimus.alkupvm" "")
        (odota-angular-pyyntoa)
        (is (= (not (tallennus-nappi-aktiivinen?)))))))
  (testing "Jarjestamissopimuksen muokkaus ei onnistu, alkupvm loppupvm:n jälkeen"
    (with-webdriver
      (du/with-data jarjestamissopimus-data
        (avaa-sopimuksen-muokkaussivu 1230)
        (syota-pvm "sopimus.alkupvm" "01.02.2009")
        (syota-pvm "sopimus.loppupvm" "24.03.1980")
        (odota-angular-pyyntoa)
        (tallenna)
        (is (= (viestin-teksti) "Järjestämissopimuksen tietojen muokkaus ei onnistunut")))))
  (testing "Järjestämissopimuksen muokkaus ei onnistu, jos sopimusnumero käytössä toisella sopimuksella"
    (with-webdriver
      (du/with-data jarjestamissopimus-data
        (avaa-sopimuksen-muokkaussivu 1230)
        (syota-kenttaan "sopimus.sopimusnumero" "321")
        (odota-angular-pyyntoa)
        (tallenna)
        (is (= (viestin-teksti) "Järjestämissopimuksen tietojen muokkaus ei onnistunut"))
        (-> (viestit-virheellisista-kentista) first (= "Sopimusnumero : Arvon tulee olla uniikki") is))))
  (testing "Järjestämissopimuksen muokkaus onnistuu, jos sopimusnumero vaihdetaan toiseksi uniikiksi merkkijonoksi"
    (with-webdriver
      (du/with-data jarjestamissopimus-data
        (avaa-sopimuksen-muokkaussivu 1230)
        (syota-kenttaan "sopimus.sopimusnumero" "3210abc")
        (odota-angular-pyyntoa)
        (tallenna)
        (is (= (viestin-teksti) "Järjestämissopimuksen tietoja muokattu"))
        (is (= (elementin-teksti "sopimus.sopimusnumero") "3210abc"))))))

(deftest jarjestamissopimus-muokkaussivu-pakolliset-kentat-test
  (testing "Järjestämissopimuksen muokkaussivu - pakolliset kentät:"
    (with-webdriver
      (du/with-data jarjestamissopimus-data
        (avaa-sopimuksen-muokkaussivu 1230)
        (is (pakollinen-kentta? "Sopimusnumero"))
        (is (pakollinen-kentta? "Järjestämissopimus alkaa"))
        (is (pakollinen-kentta? "Nykyinen toimikunta"))
        (is (pakollinen-kentta? "Koulutuksen järjestäjä, yhteisö tai säätiö"))))))

(deftest jarjestamissuunnitelman-poistaminen-test
  (testing "Järjestämissuunnitelman poistaminen heittää confirm dialogin"
    (with-webdriver
      (du/with-data (jarjestamissopimus-data-with-suunnitelma)
        (avaa-sopimuksen-muokkaussivu 1230)
        (is (= (filejen-lkm) 1))
        (poista-suunnitelma)
        (odota-poistodialogia))))
  (testing "Järjestämissopimus poistuu hyväksymällä confirm"
    (with-webdriver
      (du/with-data (jarjestamissopimus-data-with-suunnitelma)
        (avaa-sopimuksen-muokkaussivu 1230)
        (poista-suunnitelma)
        (odota-poistodialogia)
        (hyvaksyn-dialogin)
        (is (= (filejen-lkm) 0)))))
  (testing "Järjestämissopimus ei poistu, jos peruuttaa confirmin"
    (with-webdriver
      (du/with-data (jarjestamissopimus-data-with-suunnitelma)
        (avaa-sopimuksen-muokkaussivu 1230)
        (poista-suunnitelma)
        (odota-poistodialogia)
        (peruutan-dialogin)
        (is (= (filejen-lkm) 1))))))

(deftest sopimuksen-tutkintojen-osien-ja-osaamisalojen-muokkaus-test
  (testing "Radiobuttoneista on aluksi valittuna Kaikki osaamisalat ja tutkinnon osat"
    (with-webdriver
      (du/with-data (jarjestamissopimus-data-with-tutkinnon-osat)
        (avaa-sopimuksen-muokkaussivu 1230)
        (is(= (count (w/find-elements {:css ".sopimus-tutkinnon-osat input[type=\"radio\"]:first-child:checked"})) 1)))))
  (testing "Valitsemalla Seuraavat osaamisalat ja tutkinnon osat: -option valitaan oletuksena kaikki tutkintoon liittyvät osat ja osaamisalat."
    (with-webdriver
      (du/with-data (jarjestamissopimus-data-with-tutkinnon-osat)
        (avaa-sopimuksen-muokkaussivu 1230)
        (valitse-osat)
        (testing "Kaikki tutkinnonosat ovat valittuna"
          (is (= (valitut-tutkinnon-osat) (set (map :nimi testi-tutkinnonosat)))))
        (testing "Kaikki osaamisalat ovat valittuna"
          (is (= (valitut-osaamisalat) #{(:nimi testi-osaamisala)})))
        (testing "Järjestysnumerot näytetään oikein"
          (is (= (jarjestysnumerot) #{"1" "2"})))
        (testing "Kuvaukset näytetään oikein tutkinnonosille"
          (is (= (tutkinnonosien-kuvaukset) (set (map :kuvaus testi-tutkinnonosat)))))
        (testing "Kuvaukset näytetään oikein osaamisaloille"
          (is (= (osaamisalojen-kuvaukset) #{(:kuvaus testi-osaamisala)}))))))
  (testing "Tallennus onnistuu ja sopimuksen tiedoissa näkyvät tallennetut tiedot"
    (with-webdriver
      (du/with-data (jarjestamissopimus-data-with-tutkinnon-osat)
        (avaa-sopimuksen-muokkaussivu 1230)
        (valitse-osat)
        (tallenna)
        (is (= (viestin-teksti) "Järjestämissopimuksen tietoja muokattu"))
        (odota-angular-pyyntoa)
        (avaa-tutkinnon-tiedot)
        (is (= (set (map w/text (-> *ng*
                                  (.repeater "sopimusJaTutkinto in sopimus.sopimus_ja_tutkinto")
                                  (.column "sopimusJaTutkinto.tutkintoversio.nimi")
                                  (w/find-elements))))
                #{"TU1 Testialan tutkinto (tutkinnon osia: 2/2, osaamisaloja: 1/1)"}))
        (is (= (set (map w/text (w/find-elements (-> *ng* (.repeater "valittuTutkinnonosa in valitutTutkinnonosat") (.column "valittuTutkinnonosa.nimi"))))) (set (map :nimi testi-tutkinnonosat))))
        (is (= (map w/text (w/find-elements (-> *ng* (.repeater "valittuOsaamisala in valitutOsaamisalat") (.column "valittuOsaamisala.nimi")))) [(:nimi testi-osaamisala)]))
        (is (= (count (w/find-elements {:text "Sopimukseen kuuluvat tutkinnon osat (2/2)"})) 1))
        (is (= (count (w/find-elements {:text "Sopimukseen kuuluvat osaamisalat (1/1)"})) 1))
        (is (= (jarjestysnumerot) #{"1" "2"}))))))

(deftest ei-voimassaoleva-sopimus-test
  (testing "Vanhan järjestämissopimuksen muokkaus ei onnistu"
    (with-webdriver
      (du/with-data jarjestamissopimus-data
        (du/aseta-jarjestamissopimus-paattyneeksi
          (get-in jarjestamissopimus-data [:jarjestamissopimukset 0]))
        (siirry-sopimuksen-muokkaussivulle 1230)
        (tallenna)
        (is (= (viestin-teksti) "Järjestämissopimuksen tietojen muokkaus ei onnistunut"))))))

(def project-clj (str (java.lang.System/getProperty "user.dir") "/project.clj"))

(defn aseta-liite [tyyppi polku]
  (w/send-keys {:css (str "div[liitetyyppi=\"" tyyppi "\"] input[type=\"file\"]")} polku)
  (odota-kunnes (-> (w/find-elements {:css (str "div[liitetyyppi=\"" tyyppi "\"] button[upload-submit]:not(.ng-hide)")}) (count) (> 0)))
  (odota-angular-pyyntoa))

(defn paina-tallenna-liite-nappia [tyyppi]
  (w/click {:css (str "div[liitetyyppi=\"" tyyppi "\"] button[upload-submit]")})
  (Thread/sleep 500) ; Kunnes tulee parempi tapa odotella vastausta
  (odota-angular-pyyntoa))

;; Olettaa, että sopimuksella on vain yksi näyttötutkinto.
(defn sopimuksen-liitteet []
  (let [linkit (w/find-elements
                 (-> *ng*
                   (.repeater "liite in sopimusJaTutkinto.liitteet")
                   (.column "liite.sopimuksen_liite_filename")))]
    (into {} (map (juxt w/text #(w/attribute % "href")) linkit))))

;; Olettaa, että sopimuksella on vain yksi näyttötutkinto.
(defn sopimuksen-suunnitelmat []
  (let [linkit (w/find-elements
                 (-> *ng*
                   (.repeater "suunnitelma in sopimusJaTutkinto.jarjestamissuunnitelmat")
                   (.column "suunnitelma.jarjestamissuunnitelma_filename")))]
    (into {} (map (juxt w/text #(w/attribute % "href")) linkit))))

(defn lataa-tiedosto-webdriverin-istunnossa [url]
  (:body (hc/get url
                 {:cookies {"ring-session" (w/cookie "ring-session")}})))

(defn lisaa-suunnitelma-sopimukseen [nro polku]
  (avaa-sopimuksen-muokkaussivu nro)
  (aseta-liite "jarjestamissuunnitelmat" polku)
  (paina-tallenna-liite-nappia "jarjestamissuunnitelmat"))

;; Tiedostonlatausnappuloiden tyylittely on tehty lisäämällä tyylitellyn napin
;; päälle läpinäkyvä (opacity: 0%) <input type="file"> -elementti.
;; IE-WebDriverin mielestä opacity 0% -elementit eivät ole käsiteltäviä, joten
;; tiedoston syöttäminen ei onnistu samalla tavalla, kuin Firefoxilla.
(deftest ^:no-ie jarjestamisuunnitelman-lisays-test
  (testing "Lisätty järjestämissuunnitelma näkyy sopimuksen sivulla"
    (with-webdriver
      (du/with-cleaned-data jarjestamissopimus-data
        (lisaa-suunnitelma-sopimukseen 1230 project-clj)
        (is (= (map key (sopimuksen-suunnitelmat)) ["project.clj"]))))))

;; Ks. yllä
(deftest ^:no-ie jarjestamissuunnitelman-lataus-test
  (testing "Järjestämissuunnitelman lataus palauttaa alkuperäisen tiedoston sellaisenaan"
    (with-webdriver
      (du/with-cleaned-data jarjestamissopimus-data
        (lisaa-suunnitelma-sopimukseen 1230 project-clj)
        (is (= (lataa-tiedosto-webdriverin-istunnossa
                 (val (first (sopimuksen-suunnitelmat))))
               (slurp project-clj)))))))

(defn lisaa-liite-sopimukseen [nro polku]
  (avaa-sopimuksen-muokkaussivu nro)
  (aseta-liite "liitteet" polku)
  (paina-tallenna-liite-nappia "liitteet"))

;; Ks. yllä
(deftest ^:no-ie liitteen-lisays-test
  (testing "Lisätty liite näkyy sopimuksen sivulla"
    (with-webdriver
      (du/with-cleaned-data jarjestamissopimus-data
        (lisaa-liite-sopimukseen 1230 project-clj)
        (is (= (map key (sopimuksen-liitteet)) ["project.clj"]))))))

;; Ks. yllä
(deftest ^:no-ie liitteen-lataus-test
  (testing "Liitteen lataus palauttaa alkuperäisen tiedoston sellaisenaan"
    (with-webdriver
      (du/with-cleaned-data jarjestamissopimus-data
        (lisaa-liite-sopimukseen 1230 project-clj)
        (is (= (lataa-tiedosto-webdriverin-istunnossa
                 (val (first (sopimuksen-liitteet))))
               (slurp project-clj)))))))
