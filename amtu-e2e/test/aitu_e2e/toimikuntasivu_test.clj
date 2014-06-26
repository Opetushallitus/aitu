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

(ns aitu-e2e.toimikuntasivu-test
  (:require [clojure.test :refer [deftest is testing]]
            [clj-webdriver.taxi :as w]
            [aitu-e2e.util :refer :all]
            [aitu-e2e.data-util :refer [with-data
                                        aseta-toimikunta-paattyneeksi]]
            [clojure.set :refer [subset?]]))

(defn toimikuntasivu [diaarinumero] (str "/fi/#/toimikunta/" diaarinumero "/tiedot"))
(defn toimikunnan-muokkaussivu [diaarinumero] (str "/fi/#/toimikunta/" diaarinumero "/muokkaa"))

; ByAngular hakee scope-muuttujan arvon sijaan lähimmän elementin jossa binding esiintyy.
; Tämän takia toimikaudesta tällä sivulla ei saa suoraan eritellyksi alku-ja loppupäivämääriä.
(defn toimikausi []
  (elementin-teksti "toimikunta.toimikausi_alku"))

(defn voimassaolevat-jasenet []
  (map w/text (w/find-elements (-> *ng*
                                 (.repeater "henkilo in nykyisetJasenetJarjestetty")
                                 (.column "henkilo.sukunimi")))))

(defn entiset-jasenet []
  (map w/text (w/find-elements (-> *ng*
                                 (.repeater "henkilo in entisetJasenetJarjestetty")
                                 (.column "henkilo.sukunimi")))))

(defn jarjestamissopimukset []
  (map w/text (w/find-elements (-> *ng*
                                 (.repeater "sopimus in sopimuksetJarjestetty")
                                 (.column "sopimus.sopimusnumero")))))
(defn sopimuksen-tutkinnot []
  (map w/text (w/find-elements (-> *ng*
                                 (.repeater "sopimusJaTutkinto in sopimus.sopimus_ja_tutkinto")
                                 (.column "sopimusJaTutkinto.tutkintoversio.nimi")))))

(defn klikkaa-taulukon-sarakkeen-otsikkoa [taulukko sarake]
  (w/click {:css (str "jasenyyksien-listaus.nykyiset-jasenyydet table[jarjestettava-taulukko=\"" taulukko "\"] th[jarjestettava-sarake=\"" sarake "\"]")})
  (odota-angular-pyyntoa))

(defn jasenien-nimet []
  (map w/text (w/find-elements {:css "jasenyyksien-listaus.nykyiset-jasenyydet tbody td:nth-child(2)"})))

(def toimikuntasivu-testidata
  {:toimikunnat [{:nimi_fi "Ilmastointialan tutkintotoimikunta"
                  :diaarinumero "98/11/543"
                  :toimiala "Toimiala 1"
                  :tilikoodi "9876"
                  :tkunta "ILMA"
                  :kielisyys "fi"
                  :sahkoposti "toimikunta@mail.fi"
                  :toimikausi_alku "2013-08-01"
                  :toimikausi 2}]
   :henkilot [{:henkiloid 998
               :sukunimi "Ankka"
               :etunimi "Aku"},
              {:henkiloid 999
               :sukunimi "Peloton"
               :etunimi "Pelle"}]
   :jasenet [{:toimikunta "ILMA"
              :diaarinumero "98/11/543"
              :henkilo {:henkiloid 998}
              :rooli "jasen"},
             {:toimikunta "ILMA"
              :diaarinumero "98/11/543"
              :henkilo {:henkiloid 999}
              :edustus "asiantuntija"
              :rooli "asiantuntija"}]
   :koulutustoimijat [{:ytunnus "0000000-0"
                       :nimi_fi "Ankkalinnan kaupunki"}]
   :oppilaitokset [{:oppilaitoskoodi "12345"
                    :koulutustoimija "0000000-0"
                    :nimi "Ankkalinnan aikuiskoulutuskeskus"}]
   :koulutusalat [{:selite_fi "Tekniikan ja liikenteen ala"
                   :koodi "KA1"}]
   :opintoalat [{:selite_fi "Sähköala"
                 :koodi "OA1"
                 :koulutusala "KA1"}]
   :tutkinnot [{:nimi_fi "Ilmastointialan tutkinto"
                :tutkintotunnus "TU1"
                :opintoala "OA1"
                :tutkintoversio_id 1}]
   :jarjestamissopimukset [{:sopimusnumero "123"
                            :jarjestamissopimusid 1230
                            :toimikunta "ILMA"
                            :sopijatoimikunta "ILMA"
                            :tutkintotilaisuuksista_vastaava_oppilaitos "12345"
                            :koulutustoimija "0000000-0"}]
   :sopimus_ja_tutkinto [{:jarjestamissopimusid 1230
                          :sopimus_ja_tutkinto [{:tutkintoversio_id 1}]}]})

(deftest toimikuntasivu-test
  (testing "toimikuntasivu"
    (with-webdriver
      ;; Oletetaan, että
      (with-data toimikuntasivu-testidata
        ;; Kun
        (avaa (toimikuntasivu "98/11/543"))
        ;; Niin
        (testing "pitäisi näyttää toimikunnan nimi otsikkona"
          (is (= (sivun-otsikko) "ILMASTOINTIALAN TUTKINTOTOIMIKUNTA")))
        (testing "pitäisi näyttää diaarinumero"
          (is (= (elementin-teksti "toimikunta.diaarinumero") "98/11/543")))
        (testing "pitäisi näyttää tilikoodi"
          (is (= (elementin-teksti "toimikunta.tilikoodi") "9876")))
        (testing "pitäisi näyttää kielisyys"
          (is (= (enum-elementin-teksti "kieli") "suomi")))
        (testing "pitäisi näyttää toimikausi"
          (is (= (toimikausi) "01.08.2013 – 31.07.2016")))
        (testing "pitäisi näyttää sähköposti"
          (is (= (elementin-teksti "toimikunta.sahkoposti") "toimikunta@mail.fi")))
        (testing "pitäisi näyttää nykyinen jäsen"
           (is (subset? #{"Aku Ankka" "Pelle Peloton"} (set (jasenien-nimet)))))
        (testing "pitäisi näyttää järjestämissopimus"
                          (is (= (first (jarjestamissopimukset)) "123")))
        (testing "pitäisi näyttää tutkinnot järjestämissopimukselle"
          (is (= (first (sopimuksen-tutkinnot)) "Ilmastointialan tutkinto")))))))

(deftest toimikuntasivu-muokkaus-test
  (testing "toimikuntasivu"
    (with-webdriver
      ;; Oletetaan, että
      (with-data toimikuntasivu-testidata
        ;; Kun
        (avaa (toimikunnan-muokkaussivu "98/11/543"))
        (w/select-option {:css "span[nimi*=\"kieli\"] > select"} {:text "kaksikielinen"})
        (tallenna)
        ;; Niin
        (testing "pitäisi näyttää toimikunnan nimi otsikkona"
          (is (= (sivun-otsikko) "ILMASTOINTIALAN TUTKINTOTOIMIKUNTA")))
        (testing "pitäisi näyttää diaarinumero"
          (is (= (elementin-teksti "toimikunta.diaarinumero") "98/11/543")))
        (testing "pitäisi näyttää tilikoodi"
          (is (= (elementin-teksti "toimikunta.tilikoodi") "9876")))
        (testing "pitäisi näyttää kielisyys"
          (is (= (enum-elementin-teksti "kieli") "kaksikielinen")))
        (testing "pitäisi näyttää toimikausi"
          (is (= (toimikausi) "01.08.2013 – 31.07.2016")))))))

(deftest toimikuntasivu-muokkaus-test-pakolliset-kentat
  (testing "Toimikuntasivu - pakolliset kentät:"
    (with-webdriver
      (with-data toimikuntasivu-testidata
        (avaa (toimikunnan-muokkaussivu "98/11/543"))
        (is (pakollinen-kentta? "Alkupäivä"))
        (is (pakollinen-kentta? "Loppupäivä"))))))

(deftest vanhentuneen-toimikunnan-muokkaus-test
  (testing "Vanhentuneen toimikunnan muokkaus ei onnistu"
    (with-webdriver
      (with-data toimikuntasivu-testidata
        (aseta-toimikunta-paattyneeksi (get-in toimikuntasivu-testidata [:toimikunnat 0]))
        (avaa (toimikunnan-muokkaussivu "98/11/543"))
        (tallenna)
        (is (= (viestin-teksti) "Toimikunnan muokkaus ei onnistunut"))))))

(deftest toimikuntasivu-sahkoposti-muokkaus-test
  (testing "toimikuntasivu"
    (with-webdriver
      ;; Oletetaan, että
      (with-data
        {:toimikunnat [{:diaarinumero "98/11/543"
                        :sahkoposti "vanha@posti.fi"
                        :toimikausi 2}]}
        ;; Kun
        (avaa (toimikunnan-muokkaussivu "98/11/543"))
        (syota-kenttaan "toimikunta.sahkoposti" "uusi@mail.com")
        (tallenna)
        ;; Niin
        (testing "pitäisi näyttää sähköposti"
          (is (= (elementin-teksti "toimikunta.sahkoposti") "uusi@mail.com")))))))

(deftest toimikuntasivu-jarjestely-test
  (testing "toimikuntasivu - järjestely"
    (with-webdriver
      (with-data toimikuntasivu-testidata
        (avaa (toimikuntasivu "98/11/543"))
          (testing "Voimassaolevat jäsenet on järjestetty alussa sukunimen mukaan aakkosjärjestykseen"
            (is (= (vec (jasenien-nimet)) ["Aku Ankka" "Pelle Peloton"])))
          (testing "Klikkaamalla otsikkoa uudelleen järjestely muuttuu käänteiseksi"
            (klikkaa-taulukon-sarakkeen-otsikkoa "jasenet" "sukunimi")
            (is (= (vec (jasenien-nimet)) ["Pelle Peloton" "Aku Ankka" ])))
          (testing "Klikkaamalla otsikkoa uudelleen, järjestely muuttuu normaaliksi"
            (klikkaa-taulukon-sarakkeen-otsikkoa "jasenet" "sukunimi")
            (is (= (vec (jasenien-nimet)) ["Aku Ankka" "Pelle Peloton"])))
          (testing "Klikkaamalla toista otsikkoa vielä kerran, järjestely muuttuu"
            (klikkaa-taulukon-sarakkeen-otsikkoa "jasenet" "edustus")
            (is (= (vec (jasenien-nimet)) ["Pelle Peloton" "Aku Ankka" ])))
          (testing "Enum arvon perusteella järjestettäessä järjestetään näytettävän arvon mukaan."
            (klikkaa-taulukon-sarakkeen-otsikkoa "jasenet" "rooli") ;;Roolin nimet: jäsen, pysyvä asiantuntija
            (is (= (vec (jasenien-nimet)) ["Aku Ankka" "Pelle Peloton"])))))))

(deftest ei-voimassaoleva-toimikuntasivu-test
  (testing "toimikuntasivu - ei voimassa"
    (with-webdriver
      (with-data toimikuntasivu-testidata
        (aseta-toimikunta-paattyneeksi
          (get-in toimikuntasivu-testidata [:toimikunnat 0]))
        (avaa (toimikuntasivu "98/11/543"))
        (testing "Otsikon perässä on teksti (ei voimassa)"
          (is (= (sivun-otsikko) "ILMASTOINTIALAN TUTKINTOTOIMIKUNTA (EI VOIMASSA)")))
        (testing "Sivulla ei näy muokkaustoiminnallisuuksia"
          (is (= (count (filter w/displayed? (w/find-elements {:css "button.edit-icon button.add-icon"}))) 0)))))))
