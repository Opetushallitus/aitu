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

(ns aitu-e2e.tutkintosivu-siirtymaajat-test
  (:require [clojure.test :refer [deftest is testing]]
            [clj-webdriver.taxi :as w]
            [clj-time.core :as time]
            [aitu-e2e.util :refer :all]
            [aitu-e2e.aitu-util :refer :all]
            [aitu-e2e.data-util :as du]))

(defn tutkintosivu [tutkintotunnus] (str "/fi/#/tutkinto/" tutkintotunnus))

(defn tutkinnon-siirtymaaika-paattyy-kentta
  []
  (-> *ng*
    (.binding "tutkinto.siirtymaaika_paattyy")
    w/find-element))

(defn tutkinnon-siirtymaaika-paattyy
  []
  (w/text (tutkinnon-siirtymaaika-paattyy-kentta)))

(deftest tutkintosivu-siirtymaajat-test
  (testing
    "tutkintosopimussivu: siirtymäajat:"
    (du/with-data
      {:toimikunnat [{:tkunta "TTK1"
                      :toimikausi 2}]
       :koulutustoimijat [{:ytunnus "0000000-0"}]
       :oppilaitokset [{:oppilaitoskoodi "12345"
                        :koulutustoimija "0000000-0"}]
       :koulutusalat [{:koodi "KA1"}]
       :opintoalat [{:koodi "OA1"
                     :koulutusala "KA1"}]}
      (with-webdriver
        (testing
          "tutkinto on voimassa:"
          (du/with-data
            {:tutkinnot [{:nimi "T1"
                          :tutkintotunnus "TU1"
                          :opintoala "OA1"}]}

            (avaa (tutkintosivu "TU1"))
            (testing "siirtymäaika päättyy-kenttää ei näytetä"
                     (is (not (w/visible? (tutkinnon-siirtymaaika-paattyy-kentta))))))))
      (with-webdriver
        (testing
          "tutkinnon voimassaolo on päättynyt, siirtymäaika käynnissä:"
          (du/with-data
            {:tutkinnot [{:nimi "T1"
                          :tutkintotunnus "TU1"
                          :opintoala "OA1"
                          :voimassa_loppupvm du/menneisyydessa
                          :siirtymaajan_loppupvm du/tulevaisuudessa}]}

            (avaa (tutkintosivu "TU1"))
            (testing "siirtymäajan päättymispäivä näytetään"
                     (is (w/visible? (tutkinnon-siirtymaaika-paattyy-kentta)))
                     (is (= (tutkinnon-siirtymaaika-paattyy)
                            (du/paivamaara-kayttoliittyman-muodossa du/tulevaisuudessa-pvm)))))))
      (with-webdriver
        (testing
          "tutkinnon voimassaolon siirtymäaika on päättynyt:"
          (du/with-data
            {:tutkinnot [{:nimi "T1"
                          :tutkintotunnus "TU1"
                          :opintoala "OA1"
                          :siirtymaajan_loppupvm du/menneisyydessa}]}

            (avaa (tutkintosivu "TU1"))
            (testing "siirtymäajan päättymispäivä näytetään"
                     (is (w/visible? (tutkinnon-siirtymaaika-paattyy-kentta)))
                     (is (= (tutkinnon-siirtymaaika-paattyy)
                            (du/paivamaara-kayttoliittyman-muodossa du/menneisyydessa-pvm))))))))))

