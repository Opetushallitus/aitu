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

(ns aitu-e2e.jasenen-lisays-sivu-test
  (:require [clojure.test :refer [deftest is testing]]
            [clj-webdriver.taxi :as w]
            [aitu-e2e.util :refer :all]
            [aitu-e2e.data-util :refer [with-cleaned-data with-data poista]]
            [clojure.set :refer [subset?]]))

(defn jasenen-lisays-sivu [diaarinumero] (str "/fi/#/toimikunta/" diaarinumero "/jasenet/uusi"))

(defn voimassaolevat-jasenet []
  (map w/text (w/find-elements (-> *ng*
                                 (.repeater "henkilo in toimikunta.jasenyys")
                                 (.column "henkilo.sukunimi")))))

(defn kirjoita-pvm-valitsin-kenttaan [kentta arvo]
  (tyhjenna-datepicker-input kentta)
  (w/input-text (str "fieldset[valittu-pvm=\"" kentta "\"] input[type=\"text\"]") arvo)
  (odota-angular-pyyntoa))

(defn kirjoita-tietokenttaan [kentta arvo]
  (w/input-text (str "input[ng-model=\"" kentta "\"]") arvo)
  (odota-angular-pyyntoa))
(defn paina-nappia []
  (w/click {:css "button:not([class*='ng-hide']):not([class*='open-datepicker'])"})
  (odota-angular-pyyntoa))
(defn hae-henkilo [nimi]
  (valitse-select2-optio "search" "osat" nimi)
  (odota-angular-pyyntoa)
  (paina-nappia))
(defn paina-lisaa-jasen-nappia []
  (w/click {:text "Lisää jäsen"})
  (odota-angular-pyyntoa))

(def testi-toimikunnat [{:diaarinumero "98/11/543"
                         :tkunta "ILMA"
                         :toimikausi 2}])
(def testi-henkilot [{:henkiloid 998
                      :sukunimi "Ankka"
                      :etunimi "Aku"},
                     {:henkiloid 999
                      :sukunimi "Hiiri"
                      :etunimi "Mikki"},
                     {:henkiloid 997
                      :sukunimi "Sisu"
                      :etunimi "Simo"}])
(def testi-jasenet [{:diaarinumero "98/11/543"
                     :toimikunta "ILMA"
                     :henkilo {:henkiloid 998}},
                    {:diaarinumero "98/11/543"
                     :toimikunta "ILMA"
                     :henkilo {:henkiloid 999}}])
(def jasenen-poisto-fn #(str "/api/test/ttk/" (:toimikunta %) "/jasen/" (:henkiloid %)))
(def jasenien-poisto-fn #(str "/api/test/ttk/" (:toimikunta %) "/jasen"))

(defn lisaa-testijasen-toimikuntaan
  [alkupvm loppupvm]
  (avaa (jasenen-lisays-sivu "98/11/543"))
  (hae-henkilo "Simo Sisu")
  (paina-nappia)
  ;; Kun
  (w/select-option {:css "span[nimi*=\"rooli\"] > select"} {:text "sihteeri"})
  (w/select-option {:css "span[nimi*=\"edustus\"] > select"} {:text "opettajat"})
  (kirjoita-pvm-valitsin-kenttaan "jasen.alkupvm" alkupvm)
  (kirjoita-pvm-valitsin-kenttaan "jasen.loppupvm" loppupvm)
  (paina-lisaa-jasen-nappia))

(deftest ^:no-ie jasenen-lisays-sivu-test
  (testing "Toimikunnan jäsenen lisäys sivu"
    (testing "henkilön haku"
      ;; Oletetaan, että
      (with-data {:toimikunnat testi-toimikunnat
                  :henkilot testi-henkilot
                  :jasenet testi-jasenet}
        (with-webdriver
          (testing "pitäisi olla muokkaustilassa jos henkilöä ei valitse"
            ;; Kun
            (avaa (jasenen-lisays-sivu "98/11/543"))
            (paina-nappia)
            ;; Niin
            (is (w/enabled? {:css "button[ng-click=\"lisaaHenkiloJaSiirrySeuraavaan(jasen.henkilo)\"]"}))
            (not (w/enabled? {:css "button[ng-click=\"siirrySeuraavaan()\"]"}))))
        (with-webdriver
          (testing "pitäisi olla valintatilassa jos henkilö löytyy"
            ;; Kun
            (avaa (jasenen-lisays-sivu "98/11/543"))
            (hae-henkilo "Simo Sisu")
            ;; Niin
            (is (w/enabled? {:css "button[ng-click=\"siirrySeuraavaan()\"]"}))
            (not (w/enabled? {:css "button[ng-click=\"lisaaHenkiloJaSiirrySeuraavaan(jasen.henkilo)\"]"}))))
        (with-webdriver
          (testing "Pitäisi näyttää löydetyn henkilön tiedot"
            ;; Kun
            (avaa (jasenen-lisays-sivu "98/11/543"))
            (hae-henkilo "Simo Sisu")
            ;; Niin
            (is (= (elementin-teksti "henkilo.sukunimi") "Simo Sisu"))))))
    (testing "toimikunnan jäsenen lisäys"
      ;; Oletetaan, että
      (with-data {:toimikunnat testi-toimikunnat
                  :henkilot testi-henkilot
                  :jasenet testi-jasenet}
        (testing "onnistuu kun alkupvm toimikunnan voimassaoloajan sisällä"
          (with-webdriver
            (avaa (jasenen-lisays-sivu "98/11/543"))
            (hae-henkilo "Simo Sisu")
            (paina-nappia)
            ;; Kun
            (w/select-option {:css "span[nimi*=\"rooli\"] > select"} {:text "sihteeri"})
            (w/select-option {:css "span[nimi*=\"edustus\"] > select"} {:text "opettajat"})
            (kirjoita-pvm-valitsin-kenttaan "jasen.alkupvm" "02.08.2013")
            (kirjoita-pvm-valitsin-kenttaan "jasen.loppupvm" "31.07.2016")
            (paina-lisaa-jasen-nappia)
            ;; Niin
            (is (= #{"Aku Ankka" "Mikki Hiiri" "Simo Sisu"}
                   (set (voimassaolevat-jasenet))))
            (poista [{:toimikunta "ILMA" :henkiloid 997}] jasenen-poisto-fn)))
        (testing "onnistuu jos alkupvm sama kuin toimikunnan toimikauden alkupvm"
          (with-webdriver
            (avaa (jasenen-lisays-sivu "98/11/543"))
            (hae-henkilo "Simo Sisu")
            (paina-nappia)
            ;; Kun
            (w/select-option {:css "span[nimi*=\"rooli\"] > select"} {:text "sihteeri"})
            (w/select-option {:css "span[nimi*=\"edustus\"] > select"} {:text "opettajat"})
            (kirjoita-pvm-valitsin-kenttaan "jasen.alkupvm" "01.08.2013")
            (kirjoita-pvm-valitsin-kenttaan "jasen.loppupvm" "31.07.2016")
            (paina-lisaa-jasen-nappia)
            ;; Niin
            (is (= #{"Aku Ankka" "Mikki Hiiri" "Simo Sisu"}
                  (set (voimassaolevat-jasenet))))
            (poista [{:toimikunta "ILMA" :henkiloid 997}] jasenen-poisto-fn)))
        (testing "Ei onnistu jos henkilöllä jo jäsenyys toimikunnassa jonka voimassaolo päällekkäin lisättävän jäsenyyden voimassaolon kanssa"
          (with-webdriver
            (lisaa-testijasen-toimikuntaan "01.08.2013" "10.09.2013")
            (is (= (viestin-teksti) "Jäsen luotu"))
            (lisaa-testijasen-toimikuntaan "01.08.2013" "31.07.2014")
            (is (= (viestin-teksti) "Jäsenen luonti ei onnistunut"))
            (poista [{:toimikunta "ILMA" :henkiloid 997}] jasenen-poisto-fn)))
        (testing "Onnistuu jos henkilöllä jo jäsenyys toimikunnassa jonka voimassaolo ei mene päällekkäin lisättävän jäsenyyden voimassaolon kanssa"
          (with-webdriver
            (lisaa-testijasen-toimikuntaan "01.08.2013" "10.09.2013")
            (is (= (viestin-teksti) "Jäsen luotu"))
            (lisaa-testijasen-toimikuntaan "11.09.2013" "31.07.2014")
            (is (= (viestin-teksti) "Jäsen luotu"))
            (poista [{:toimikunta "ILMA" :henkiloid 997}] jasenen-poisto-fn)))
        (testing "ei onnistu, jos alkupvm toimikauden lopun jälkeen"
          (with-webdriver
            (avaa (jasenen-lisays-sivu "98/11/543"))
            (hae-henkilo "Simo Sisu")
            (paina-nappia)
            ;; Kun
            (w/select-option {:css "span[nimi*=\"rooli\"] > select"} {:text "sihteeri"})
            (w/select-option {:css "span[nimi*=\"edustus\"] > select"} {:text "opettajat"})
            (kirjoita-pvm-valitsin-kenttaan "jasen.alkupvm" "01.08.2016")
            (kirjoita-pvm-valitsin-kenttaan "jasen.loppupvm" "02.08.2016")
            (paina-lisaa-jasen-nappia)
            ;;Niin
            (is (= (viestin-teksti) "Jäsenen luonti ei onnistunut"))))
        (testing "ei onnistu, jos alkupvm ennen toimikauden alkua"
          (with-webdriver
            (avaa (jasenen-lisays-sivu "98/11/543"))
            (hae-henkilo "Simo Sisu")
            (paina-nappia)
            ;; Kun
            (w/select-option {:css "span[nimi*=\"rooli\"] > select"} {:text "sihteeri"})
            (w/select-option {:css "span[nimi*=\"edustus\"] > select"} {:text "opettajat"})
            (kirjoita-pvm-valitsin-kenttaan "jasen.alkupvm" "29.07.2013")
            (kirjoita-pvm-valitsin-kenttaan "jasen.loppupvm" "31.07.2016")
            (paina-lisaa-jasen-nappia)
            ;; Niin
            (is (= (viestin-teksti) "Jäsenen luonti ei onnistunut"))))))))

(deftest ^:no-ie uuden-henkilon-lisays-jaseneksi-test
  (testing "uuden henkilön lisäys jäseneksi"
    (testing "tuntemattoman henkilön lisäys jäseneksi onnistuu"
      (with-cleaned-data {:toimikunnat testi-toimikunnat
                          :henkilot testi-henkilot
                          :jasenet testi-jasenet}
        (with-webdriver
          (avaa (jasenen-lisays-sivu "98/11/543"))
          (paina-nappia)
          ;; Kun
          (kirjoita-tietokenttaan "jasen.henkilo.etunimi" "Roope")
          (kirjoita-tietokenttaan "jasen.henkilo.sukunimi" "Setä")
          (paina-nappia)
          (w/select-option {:css "span[nimi*=\"rooli\"] > select"} {:text "sihteeri"})
          (w/select-option {:css "span[nimi*=\"edustus\"] > select"} {:text "opettajat"})
          (kirjoita-pvm-valitsin-kenttaan "jasen.alkupvm" "01.08.2013")
          (kirjoita-pvm-valitsin-kenttaan "jasen.loppupvm" "31.07.2016")
          (paina-lisaa-jasen-nappia)
          ;; Niin
          (is (= (set (voimassaolevat-jasenet))
                 #{"Aku Ankka" "Mikki Hiiri" "Roope Setä"})))))))
