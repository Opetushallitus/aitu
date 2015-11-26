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

(ns aitu-e2e.henkilosivu-test
  (:require [clojure.test :refer [deftest is testing]]
            [clj-webdriver.taxi :as w]
            [aitu-e2e.henkilolista-test :refer [henkilolista]]
            [aitu-e2e.util :refer :all]
            [aitu-e2e.aitu-util :refer :all]
            [aitu-e2e.data-util :refer [with-data with-cleaned-data]]
            [aitu-e2e.datatehdas :as dt]))

(defn henkilosivu [id] (str "/fi/#/henkilot/" id "/tiedot"))
(defn henkilon-muokkaussivu [id] (str "/fi/#/henkilot/" id "/muokkaa"))
(defn uuden-henkilon-luonti [] (str "/fi/#/henkilot/uusi"))
(defn paina-luo-uusi-henkilo []
  (w/click {:css ".e2e-luo-uusi-henkilo"})
  (odota-angular-pyyntoa))
(defn henkilon-kokonimi-lisayksessa [] (elementin-teksti "jasen.henkilo.sukunimi"))
(defn henkilon-kokonimi [] (first (map w/text (w/xpath-finder "//section[1]/fieldset[1]/p"))))
(defn kirjoita-henkilon-tietokenttaan [kentta arvo]  (w/input-text (str "input[ng-model=\"" kentta "\"]") arvo))
(defn jatka-jasenyyden-luontiin []
  (w/click "button[ng-click=\"lisaaHenkiloJaSiirrySeuraavaan(jasen.henkilo)\"]")
  (odota-angular-pyyntoa))

(defn poista-jarjestovalinta []
  (w/click {:css "fieldset[model=\"jasen.henkilo.jarjesto\"][model-id-property=\"jarjesto\"] abbr.select2-search-choice-close"})
  (odota-angular-pyyntoa))

(defn jarjeston-nimi []
  (w/text (w/find-element {:css ".e2e-jasen-henkilo-jarjesto-jarjesto_nimi"})))

(defn valitse-jarjesto [jarjeston-nimi]
  (valitse-select2-optio "jasen.henkilo.jarjesto" "jarjesto" jarjeston-nimi))

(defn tayta-pakolliset-kentat! []
  (kirjoita-henkilon-tietokenttaan "jasen.henkilo.etunimi" "Sihto")
  (kirjoita-henkilon-tietokenttaan "jasen.henkilo.sukunimi" "Amakuutio")
  (kirjoita-henkilon-tietokenttaan "jasen.henkilo.sahkoposti" "sihto.amakuutio@example.com")
  (kirjoita-henkilon-tietokenttaan "jasen.henkilo.osoite" "Åkerlundinkatu")
  (kirjoita-henkilon-tietokenttaan "jasen.henkilo.postinumero" "33330")
  (kirjoita-henkilon-tietokenttaan "jasen.henkilo.postitoimipaikka" "Tampere"))

(deftest ^:no-ie henkilosivu-test
  (testing "henkilösivu"
    (testing "pitäisi käyttää henkilön nimeä sivun otsikkona"
      (with-webdriver
        ;; Oletetaan, että
        (with-data
          {:henkilot [{:henkiloid 999
                       :etunimi "Ahto"
                       :sukunimi "Simakuutio"}]}
          ;; Kun
          (avaa (henkilosivu 999))
          ;; Niin
          (is (= (sivun-otsikko) "AHTO SIMAKUUTIO")))))
    (testing "pitäisi näyttää henkilön toimikuntajäsenyydet"
      (with-webdriver
        ;; Oletetaan, että
        (with-data
          {:henkilot [{:henkiloid 999}]
           :toimikunnat [{:nimi_fi "Toimikunta 1"
                          :tkunta "TTK1"}]
           :jasenet [{:toimikunta "TTK1"
                      :henkilo {:henkiloid 999}}]}
          ;; Kun
          (avaa (henkilosivu 999))
          ;; Niin
          (is (re-find #"Toimikunta 1 \(2013\)" (listarivi ".nykyiset-jasenyydet" 0)))))))
  (testing "henkilönmuokkaussivu"
    (testing "henkilön tietojen muokkaus onnistuu ja näytetään ilmoitus onnistumisesta"
      (with-webdriver
        ;; Oletetaan, että
        (with-data
          {:henkilot [{:henkiloid 999
                       :etunimi "Ahto"
                       :sukunimi "Simakuutio"}]}
          ;; Kun
          (avaa (henkilon-muokkaussivu 999))
          (kirjoita-henkilon-tietokenttaan "jasen.henkilo.sukunimi", "Muokattu")
          (odota-angular-pyyntoa)
          (tallenna)
          (odota-angular-pyyntoa)
          ;;Niin
          (is (= (viestin-teksti) "Henkilön tietoja muokattu"))
          (is (= (henkilon-kokonimi) "Ahto SimakuutioMuokattu")))))
    (testing "tallennusnappi on disabloituna jos pakollisia tietoja puuttuu."
      (with-webdriver
        ;; Oletetaan, että
        (with-data
          {:henkilot [{:henkiloid 999
                       :etunimi "Ahto"
                       :sukunimi "Simakuutio"}]}
          ;; Kun
          (avaa (henkilon-muokkaussivu 999))
          (tyhjenna-input "jasen.henkilo.sukunimi")
          (odota-angular-pyyntoa)
          ;;Niin
          (is (not (tallennus-nappi-aktiivinen?)))))))
  (testing "uuden henkilön luonti"
    (testing "uuden henkilön luontiin pääsee painamalla uuden henkilön luonti -nappia henkilölistaussivulla"
      (with-webdriver
        (with-cleaned-data
          {}
          ;; Kun
          (avaa (uuden-henkilon-luonti))
          (paina-luo-uusi-henkilo)
          (tayta-pakolliset-kentat!)
          (w/select-option {:css "span[nimi*=\"sukupuoli\"] > select"} {:text "mies"})
          (w/select-option {:css "span[nimi*=\"kieli\"] > select"} {:text "suomi"})
          (odota-angular-pyyntoa)
          (jatka-jasenyyden-luontiin)
          ;;Niin
          (is (= (viestin-teksti) "Henkilö luotu"))
          (is (= (henkilon-kokonimi-lisayksessa) "Sihto Amakuutio")))))
    (testing "uuden henkilön luonti onnistuu ja näytetään ilmoitus onnistumisesta"
      (with-webdriver
        (with-cleaned-data
          {}
          ;; Kun
          (avaa (uuden-henkilon-luonti))
          (paina-luo-uusi-henkilo)
          (tayta-pakolliset-kentat!)
          (w/select-option {:css "span[nimi*=\"sukupuoli\"] > select"} {:text "mies"})
          (w/select-option {:css "span[nimi*=\"kieli\"] > select"} {:text "suomi"})
          (odota-angular-pyyntoa)
          (jatka-jasenyyden-luontiin)
          ;;Niin
          (is (= (viestin-teksti) "Henkilö luotu"))
          (is (= (henkilon-kokonimi-lisayksessa) "Sihto Amakuutio")))))
    (testing "uuden henkilön luonti epäonnistuu liian pitkään postinumeroon"
      (with-webdriver
        (with-cleaned-data
          {}
          ;; Kun
          (avaa (uuden-henkilon-luonti))
          (paina-luo-uusi-henkilo)
          (tayta-pakolliset-kentat!)
          (w/select-option {:css "span[nimi*=\"sukupuoli\"] > select"} {:text "mies"})
          (w/select-option {:css "span[nimi*=\"kieli\"] > select"} {:text "suomi"})
          (kirjoita-henkilon-tietokenttaan "jasen.henkilo.postinumero" "123456")
          (odota-angular-pyyntoa)
          (jatka-jasenyyden-luontiin)
          ;;Niin
          (is (= (viestin-teksti) "Henkilön luonti ei onnistunut"))
          (is (= (viestit-virheellisista-kentista) ["Postinumero : Arvo on liian pitkä"])))))
    (testing "uuden henkilön luonti ei onnistu jos vaadittuja tietoja puuttuu"
      (with-webdriver
        (avaa henkilolista)
        (w/click {:css "button.luo-uusi"})
        (odota-angular-pyyntoa)
        (is (.endsWith (w/current-url) "/#/henkilot/uusi"))))))

(deftest henkilo-jarjestolla-test
  (testing "henkilö jolla on järjestö"
    (testing "pitäisi näyttää järjestön nimi"
      (with-webdriver
        ;; Oletetaan, että
        (let [jarjesto (dt/jarjesto-nimella "Pikkujärjestö")
              henkilo (dt/henkilo-jarjestolla "Ahto" "Simakuutio" (:jarjestoid jarjesto))]
          (with-data {:jarjestot [jarjesto]
                      :henkilot [henkilo]}
            ;; Kun
            (avaa (henkilosivu (:henkiloid henkilo)))
            ;; Niin
            (is (= (jarjeston-nimi) "Pikkujärjestö"))))))))

(deftest henkilo-jarjestolla-muokkaus-test
  (testing "henkilö järjestön muokkaus"
    (testing "pitäisi pystyä poistamaan järjestö henkilöltä"
      (with-webdriver
        ;; Oletetaan, että
        (let [jarjesto (dt/jarjesto-nimella "Pikkujärjestö")
              henkilo (dt/henkilo-jarjestolla "Ahto" "Simakuutio" (:jarjestoid jarjesto))]
          (with-data {:jarjestot [jarjesto]
                      :henkilot [henkilo]}
            ;; Kun
            (avaa (henkilon-muokkaussivu (:henkiloid henkilo)))
            (poista-jarjestovalinta)
            (tallenna)
            ;; Niin
            (is (= (jarjeston-nimi) ""))))))
    (testing "pitäisi pystyä lisäämään järjestö henkilölle"
      (with-webdriver
        ;; Oletetaan, että
        (let [jarjesto (dt/jarjesto-nimella "Pikkujärjestö")
              henkilo (dt/henkilo-nimella "Ahto" "Simakuutio")]
          (with-data {:jarjestot [jarjesto]
                      :henkilot [henkilo]}
            ;; Kun
            (avaa (henkilon-muokkaussivu (:henkiloid henkilo)))
            (valitse-jarjesto "Pikkujärjestö")
            (tallenna)
            ;; Niin
            (is (= (jarjeston-nimi) "Pikkujärjestö"))))))))

(deftest oph-643-test
  (testing "Henkilön keskusjärjestön tulee näkyä henkilönmuokkaussivulla"
    (with-webdriver
      (let [jarjesto (dt/jarjesto-nimella "Pikkujärjestö")
            henkilo (dt/henkilo-jarjestolla "Ahto" "Simakuutio" (:jarjestoid jarjesto))]
        (with-data
          {:jarjestot [jarjesto]
           :henkilot [henkilo]}
          (avaa (henkilon-muokkaussivu (:henkiloid henkilo)))
          ;; Oikea järjestö on valittu sekä ensimmäisellä että toisella
          ;; muokkauskerralla
          (is (w/find-element {:class "select2-chosen", :text "Pikkujärjestö"}))
          (tallenna)
          (avaa (henkilon-muokkaussivu (:henkiloid henkilo)))
          (is (w/find-element {:class "select2-chosen", :text "Pikkujärjestö"})))))))

(deftest henkilosivu-muokkaus-pakolliset-kentat-test
  (testing "Henkilön muokkaussivun pakolliset kentät"
    (with-webdriver
      (with-data
        {:henkilot [{:henkiloid 999
                     :etunimi "Ahto"
                     :sukunimi "Simakuutio"}]}
        (avaa (henkilon-muokkaussivu 999))
        (is (pakollinen-kentta? "Etunimi"))
        (is (pakollinen-kentta? "Sukunimi"))))))
