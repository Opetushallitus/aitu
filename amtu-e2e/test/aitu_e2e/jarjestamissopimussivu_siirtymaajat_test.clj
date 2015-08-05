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

(ns aitu-e2e.jarjestamissopimussivu-siirtymaajat-test
  (:require [clojure.test :refer [deftest is testing]]
            [clj-webdriver.taxi :as w]
            [aitu-e2e.util :refer :all]
            [aitu-e2e.aitu-util :refer :all]
            [aitu-e2e.data-util :refer [with-data]]))

(defn sopimussivu [jarjestamissopimusid] (str "/fi/#/sopimus/" jarjestamissopimusid "/tiedot"))

(defn nayta-tutkinto
  []
  (->
    (w/find-elements {:css ".e2e-tutkintoversio"})
    first
    w/click))

(defn tutkinnon-siirtymaaika-paattyy
  []
  (-> *ng*
    (.repeater "sopimusJaTutkinto in sopimus.sopimus_ja_tutkinto")
    (.column "sopimusJaTutkinto.tutkintoversio.siirtymaaika_paattyy")
    w/find-element
    w/text))

(deftest jarjestamissopimussivu-siirtymaajat-test
  (testing
    "järjestämissopimussivu: siirtymäajat:"
    (with-webdriver
      ;; Oletetaan, että
      (with-data
        {:toimikunnat [{:tkunta "TTK1"
                        :toimikausi 2}]
         :koulutustoimijat [{:ytunnus "0000000-0"}]
         :oppilaitokset [{:oppilaitoskoodi "12345"
                          :koulutustoimija "0000000-0"}]
         :koulutusalat [{:koodi "KA1"}]
         :opintoalat [{:koodi "OA1"
                       :koulutusala "KA1"}]}
        (testing
          "tutkinto on voimassa:"
          (with-data
            {:tutkinnot [{:nimi "T1"
                          :tutkintotunnus "TU1"
                          :opintoala "OA1"
                          :tutkintoversio_id 1}]
             :jarjestamissopimukset [{:sopimusnumero "123"
                                      :jarjestamissopimusid 1230
                                      :toimikunta "TTK1"
                                      :sopijatoimikunta "TTK1"
                                      :koulutustoimija "0000000-0"
                                      :tutkintotilaisuuksista_vastaava_oppilaitos "12345"}]
             :sopimus_ja_tutkinto [{:jarjestamissopimusid 1230
                                    :sopimus_ja_tutkinto [{:tutkintoversio_id 1}]}]}

            (avaa-uudelleenladaten (sopimussivu 1230))
            (nayta-tutkinto)
            (testing "siirtymäajan päättymispäivä on tyhjä"
                     (is (= (tutkinnon-siirtymaaika-paattyy) "")))))
        (testing
          "tutkinnon voimassaolo on päättynyt, tutkinnossa siirtymäaika:"
          (with-data
            {:tutkinnot [{:nimi "T1"
                          :tutkintotunnus "TU1"
                          :opintoala "OA1"
                          :tutkintoversio_id 1
                          :siirtymaajan_loppupvm "2015-02-01"}]
             :jarjestamissopimukset [{:sopimusnumero "123"
                                      :jarjestamissopimusid 1230
                                      :toimikunta "TTK1"
                                      :sopijatoimikunta "TTK1"
                                      :koulutustoimija "0000000-0"
                                      :tutkintotilaisuuksista_vastaava_oppilaitos "12345"}]
             :sopimus_ja_tutkinto [{:jarjestamissopimusid 1230
                                    :sopimus_ja_tutkinto [{:tutkintoversio_id 1}]}]}

            (avaa-uudelleenladaten (sopimussivu 1230))
            (nayta-tutkinto)
            (testing "siirtymäajan päättymispäivä"
                     (is (= (tutkinnon-siirtymaaika-paattyy) "01.02.2015")))))))))
