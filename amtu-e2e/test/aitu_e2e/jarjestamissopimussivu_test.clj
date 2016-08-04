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

(ns aitu-e2e.jarjestamissopimussivu-test
  (:require [clojure.test :refer [deftest is testing]]
            [clj-webdriver.taxi :as w]
            [clj-time.core :as time]
            [aitu-e2e.util :refer :all]
            [aitu-e2e.aitu-util :refer :all]
            [aitu-e2e.data-util :as du]))

(defn sopimussivu [jarjestamissopimusid] (str "/fi/#/sopimus/" jarjestamissopimusid "/tiedot"))

(defn tutkinnon-perusteen-diaarinumero
  "Hakee diaarinumerokentän, joka on accordionin sisällä."
  []
  (first (w/find-elements {:css ".e2e-tutkinnon-perusteen-diaarinumero"})))

(def jarjestamissopimus-data {:toimikunnat [{:nimi_fi "Testialan tutkintotoimikunta"
                                             :diaarinumero "98/11/543"
                                             :toimiala "Toimiala 1"
                                             :tkunta "ILMA"
                                             :kielisyys "fi"
                                             :toimikausi 3}]
                              :henkilot [{:henkiloid 998
                                          :sukunimi "Ankka"
                                          :etunimi "Aku"}]
                              :jasenet [{:toimikunta "ILMA"
                                         :diaarinumero "98/11/543"
                                         :henkilo {:henkiloid 998}}]
                              :koulutustoimijat [{:ytunnus "0000000-0"
                                                  :nimi_fi "Ankkalinnan kaupunki"}
                                                 {:ytunnus "1111111-1"
                                                  :nimi_fi "Hanhivaaran kaupunki"}]
                              :oppilaitokset [{:oppilaitoskoodi "12345"
                                               :koulutustoimija "0000000-0"
                                               :nimi "Ankkalinnan aikuiskoulutuskeskus"}
                                              {:oppilaitoskoodi "24681"
                                               :koulutustoimija "1111111-1"
                                               :nimi "Hanhivaaran urheiluopisto"}]
                              :koulutusalat [{:selite_fi "Testi koulutusala"
                                              :koodi "KA1"}]
                              :opintoalat [{:selite_fi "Testi opintoala"
                                            :koodi "OA1"
                                            :koulutusala "KA1"}]
                              :perusteet [{:diaarinumero "123/04/13"}
                                          {:diaarinumero "123/04/14"}]
                              :tutkinnot [{:nimi_fi "Testialan tutkinto"
                                           :tutkintotunnus "TU1"
                                           :tutkintoversio_id 1
                                           :opintoala "OA1"
                                           :peruste "123/04/13"}
                                          {:nimi_fi "Testialan tutkinto2"
                                           :tutkintotunnus "TU2"
                                           :tutkintoversio_id 2
                                           :opintoala "OA1"
                                           :peruste "123/04/14"}]
                              :jarjestamissopimukset [{:sopimusnumero "123"
                                                       :jarjestamissopimusid 1230
                                                       :toimikunta "ILMA"
                                                       :sopijatoimikunta "ILMA"
                                                       :koulutustoimija "0000000-0"
                                                       :tutkintotilaisuuksista_vastaava_oppilaitos "12345"}
                                                      {:sopimusnumero "321"
                                                       :jarjestamissopimusid 1231
                                                       :toimikunta "ILMA"
                                                       :sopijatoimikunta "ILMA"
                                                       :koulutustoimija "0000000-0"
                                                       :tutkintotilaisuuksista_vastaava_oppilaitos "12345"}]
                              :sopimus_ja_tutkinto [{:jarjestamissopimusid 1230
                                                     :sopimus_ja_tutkinto [{:tutkintoversio_id 1
                                                                            :alkupvm du/menneisyydessa
                                                                            :loppupvm du/tulevaisuudessa}]}
                                                    {:jarjestamissopimusid 1231
                                                     :sopimus_ja_tutkinto [{:tutkintoversio_id 1
                                                                            :alkupvm du/menneisyydessa
                                                                            :loppupvm du/tulevaisuudessa}]}]})

(deftest jarjestamissopimussivu-test
  (testing "järjestämissopimussivu"
    (with-webdriver
      ;; Oletetaan, että
      (du/with-data jarjestamissopimus-data

        (avaa (sopimussivu 1230))
        (testing "otsikko näkyy sivulla"
          (let [otsikko (-> (w/find-element {:tag "h1"})
                            (w/text))]
            (is (= otsikko "JÄRJESTÄMISSOPIMUS"))))

        (testing "sopimusnumero näkyy sivulla"
          (let [sopimusnumero (-> *ng*
                                  (.binding "sopimus.sopimusnumero")
                                  (w/find-elements)
                                  (first)
                                  (w/text))]
            (is (= sopimusnumero "123"))))

        (testing "toimikunnan nimi näkyy sivulla"
          (let [toimikunnan-nimi (w/text (w/find-element {:css ".e2e-sopimus-toimikunta-nimi"}))]
            (is (= toimikunnan-nimi "Testialan tutkintotoimikunta (2013)"))))

        (testing "sopijatoimikunnan nimi näkyy sivulla"
          (let [toimikunnan-nimi (w/text (w/find-element {:css ".e2e-sopimus-sopijatoimikunta-nimi"}))]
            (is (= toimikunnan-nimi "Testialan tutkintotoimikunta (2013)"))))

        (testing "oppilaitoksen nimi näkyy sivulla"
          (let [oppilaitoksen-nimi (w/text (w/find-element {:css ".e2e-sopimus-koulutustoimija-nimi"}))]
            (is (= oppilaitoksen-nimi "Ankkalinnan kaupunki"))))

        (testing "oppilaitoksen nimi näkyy sivulla"
          (let [oppilaitoksen-nimi (-> *ng*
                                       (.binding "sopimus.tutkintotilaisuuksista_vastaava_oppilaitos.nimi")
                                       (w/find-elements)
                                       (first)
                                       (w/text))]
            (is (= oppilaitoksen-nimi "Ankkalinnan aikuiskoulutuskeskus"))))

        (testing "voimassaoloaika näkyy sivulla"
          (let [voimassaoloaika (w/text (w/find-element {:css ".e2e-sopimus-alkupvm"}))]
            (is (= voimassaoloaika
                   (str (du/paivamaara-kayttoliittyman-muodossa du/menneisyydessa-pvm) " - "
                        (du/paivamaara-kayttoliittyman-muodossa du/tulevaisuudessa-pvm))))))

        (testing "napista pääsee nykyisen toimikunnan sivulle"
          (w/visible? (w/find-element {:css "button[ng-click=\"siirryToimikunnanSivulle(sopimus.toimikunta.diaarinumero)\"]"})))
        (let [tutkinnot (first (w/find-elements {:css ".e2e-tutkintoversio"}))]
          (testing "tutkinnot näkyvät sivulla"
            (-> tutkinnot
              (w/text)
              (= "TU1 Testialan tutkinto (koko tutkinto)")
              (is)))
          (testing "tutkinnon detaljit ovat aluksi poissa näkyvistä"
            (is (not (w/visible? (tutkinnon-perusteen-diaarinumero)))))
          (testing "tutkinnon detaljit saa näkyville klikkaamalla accordionin otsikkoa"
            (w/click tutkinnot)
            (testing
              "tutkinnon detailit näkyvissä"
              (is (w/visible? (tutkinnon-perusteen-diaarinumero))))
            (testing
              "tutkinnon perusteen diaarinumero"
              (is (= (w/text (tutkinnon-perusteen-diaarinumero)) "123/04/13")))))))))

(deftest ei-voimassaoleva-sopimus-test
  (testing "järjestämissopimussivu - ei voimassa"
    (with-webdriver
      (du/with-data (assoc-in jarjestamissopimus-data [:sopimus_ja_tutkinto 0 :sopimus_ja_tutkinto 0 :loppupvm] du/menneisyydessa)
        (avaa (sopimussivu 1230))
        (testing "Otsikon perässä on teksti (ei voimassa)"
          (is (= (sivun-otsikko) "JÄRJESTÄMISSOPIMUS (EI VOIMASSA)")))))))
