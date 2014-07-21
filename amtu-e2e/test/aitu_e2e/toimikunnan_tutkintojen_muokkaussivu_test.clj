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

(ns aitu-e2e.toimikunnan-tutkintojen-muokkaussivu-test
  (:require [clojure.test :refer [deftest is testing]]
            [clj-webdriver.taxi :as w]
            [aitu-e2e.util :refer :all]
            [aitu-e2e.aitu-util :refer :all]
            [aitu-e2e.data-util :refer [with-data with-cleaned-data]]))

(defn avaa-toimikuntasivu [diaarinumero] (avaa (str "/fi/#/toimikunta/" diaarinumero "/tiedot")))

(defn muokkaa-toimialaa []
  (w/click "button[ng-click=\"muokkaaToimialaa()\"]")
  (odota-angular-pyyntoa))

(def test-data {:toimikunnat [{:nimi_fi "Testialan tutkintotoimikunta"
                               :diaarinumero "98/11/543"
                               :toimiala "Toimiala 1"
                               :tkunta "ILMA"
                               :kielisyys "fi"
                               :toimikausi 2}]
                :koulutusalat [{:selite_fi "Testi koulutusala"
                                :koodi "KA1"}]
                :opintoalat [{:selite_fi "Testi opintoala"
                              :koodi "OA1"
                              :koulutusala "KA1"}]
                :perusteet [{:diaarinumero "123/04/13"}
                            {:diaarinumero "123/04/14"}]
                :tutkinnot [{:nimi_fi "Testialan tutkinto"
                             :tutkintotunnus "TU1"
                             :opintoala "OA1"
                             :peruste "123/04/13"}
                            {:nimi_fi "Testialan tutkinto2"
                             :tutkintotunnus "TU2"
                             :opintoala "OA1"
                             :peruste "123/04/14"}]
                :toimikunta_ja_tutkinto [{:toimikunta "ILMA"
                                          :tutkintotunnus "TU1"}]})

(defn toimikunnan-tutkinnot []
  (sort (map w/text (w/find-elements (-> *ng*
                                 (.repeater "tutkinto in tutkintoJarjestetty")
                                 (.column "tutkinto.nimi"))))))

(deftest toimikunnan-tutkintojen-naytto-test
  (testing "Toimikunnan tutkintojen muokkaussivu aukeaa ja näyttää alussa valittuna toimikunnan tutkinnot"
    (with-webdriver
      (with-data test-data
        (avaa-toimikuntasivu "98/11/543")
        (muokkaa-toimialaa)
        (klikkaa-linkkia "Testi koulutusala")
        (klikkaa-linkkia "Testi opintoala")
        (->
          (w/find-element {:tag :a :text "TU1 - Testialan tutkinto"})
          (elementilla-luokka? "added")
          (is))))))

(deftest toimikunnan-tutkintojen-poisto-test
  (testing "Toimikunnan tutkintojen muokkaussivulla voi poistaa tutkintoja"
    (with-webdriver
      (with-cleaned-data test-data
        (avaa-toimikuntasivu "98/11/543")
        (is (= (toimikunnan-tutkinnot) ["TU1 Testialan tutkinto"]))
        (muokkaa-toimialaa)
        (klikkaa-linkkia "Testi koulutusala")
        (klikkaa-linkkia "Testi opintoala")
        (klikkaa-linkkia "TU1 - Testialan tutkinto")
        (tallenna-ja-hyvaksy-dialogi)
        (is (= (viestin-teksti) "Toimialan muokkaus onnistui"))
        (is (= (toimikunnan-tutkinnot) []))))))

(deftest toimikunnan-tutkintojen-lisays-test
  (testing "Toimikunnan tutkintojen muokkaussivulla voi lisätä tutkintoja"
    (with-webdriver
      (with-cleaned-data test-data
        (avaa-toimikuntasivu "98/11/543")
        (is (= (toimikunnan-tutkinnot) ["TU1 Testialan tutkinto"]))
        (muokkaa-toimialaa)
        (klikkaa-linkkia "Testi koulutusala")
        (klikkaa-linkkia "Testi opintoala")
        (klikkaa-linkkia "TU2 - Testialan tutkinto2")
        (tallenna)
        (is (= (viestin-teksti) "Toimialan muokkaus onnistui"))
        (is (= (toimikunnan-tutkinnot) ["TU1 Testialan tutkinto" "TU2 Testialan tutkinto2"]))))))
