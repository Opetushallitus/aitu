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

(ns aitu-e2e.jarjestamissopimuksen-tutkintojen-muokkaussivu-test
  (:require [clojure.test :refer [deftest is testing]]
            [aitu-e2e.jarjestamissopimussivu-test :refer [sopimussivu jarjestamissopimus-data]]
            [clj-webdriver.taxi :as w]
            [aitu-e2e.util :refer :all]
            [aitu-e2e.aitu-util :refer :all]
            [aitu-e2e.data-util :refer [with-data]]))

(defn avaa-tutkintojen-muokkaussivu [jarjestamissopimusid]
  (avaa (sopimussivu jarjestamissopimusid))
  (w/click "button[ng-click=\"muokkaaTutkintoja()\"]")
  (odota-angular-pyyntoa))

(deftest jarjestamissopimuksen-tutkintojen-muokkaussivu-test
  (testing "Järjestämissopimuksen tutkintojen muokkaussivu aukeaa kun painaa muokkaa nappia sopimuksen tietosivulta"
    (with-webdriver
      (with-data jarjestamissopimus-data
        (avaa-tutkintojen-muokkaussivu 1230)
        (w/visible? (w/find-element {:css "button[ng-click=\"tallenna()\"]"})))))
  (testing "Järjestämissopimuksen tutkintojen muokkaussivulla näkyy tutkintorakenne josta valittuna oikea tutkinto"
    (with-webdriver
      (with-data jarjestamissopimus-data
        (avaa-tutkintojen-muokkaussivu 1230)
        (klikkaa-linkkia "Testi koulutusala")
        (klikkaa-linkkia "Testi opintoala")
        (->
          (w/find-element {:xpath "//a[starts-with(., 'TU1 - Testialan tutkinto')]"})
          (elementilla-luokka? "added")
          (is)))))
  (testing "Järjestämissopimuksen tutkintojen muokkaussivulla voi poistaa tutkintoja hyväksymällä confirm"
    (with-webdriver
      (with-data jarjestamissopimus-data
        (avaa-tutkintojen-muokkaussivu 1230)
        (klikkaa-linkkia "Testi koulutusala")
        (klikkaa-linkkia "Testi opintoala")
        (w/click {:xpath "//a[starts-with(., 'TU1 - Testialan tutkinto')]"})
        (odota-angular-pyyntoa)
        (w/click "button[ng-click=\"tallenna()\"]")
        (odota-dialogia #"Oletko varma")
        (hyvaksyn-dialogin)
        (is (= (viestin-teksti) "Tutkintojen muokkaus onnistui"))
        (->
          (w/find-element {:text "Ei tutkintoja"})
          (w/visible?)
          (is)))))
  (testing "Järjestämissopimuksen tutkintojen muokkaussivulla voi lisätä tutkintoja klikkaamalla tutkintorakennepuusta tutkintoa"
    (with-webdriver
      (with-data jarjestamissopimus-data
        (avaa-tutkintojen-muokkaussivu 1230)
        (klikkaa-linkkia "Testi koulutusala")
        (klikkaa-linkkia "Testi opintoala")
        (w/click {:xpath "//a[starts-with(., 'TU2 - Testialan tutkinto2')]"})
        (odota-angular-pyyntoa)
        (tallenna)
        (is (= (viestin-teksti) "Tutkintojen muokkaus onnistui"))

        (is (= (set (map w/text (w/find-elements {:css ".e2e-tutkintoversio"})))
          #{"TU1 Testialan tutkinto (koko tutkinto) – 123/04/13", "TU2 Testialan tutkinto2 (koko tutkinto) – 123/04/14"}))))))
