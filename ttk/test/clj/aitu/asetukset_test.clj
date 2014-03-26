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

(ns aitu.asetukset-test
  (:require [clojure.test :refer :all]
            [aitu.asetukset :refer :all]))

(deftest lue-asetukset-test []
  (testing "palauttaa tyhjän mapin jos annettua tiedostoa ei löydy"
    (is (= {} (lue-asetukset-tiedostosta "olematon tiedosto")))))

(deftest viallinen-avain-ei-toimi []
  (testing "viallinen avain ei kelpaa asetuksissa"
    (is (thrown? Throwable
          (tarkista-avaimet {:a :b :c {:dd :ee}})))))

(deftest oikea-avain-toimii []
  (testing "oikeat avaimet toimivat"
    (tarkista-avaimet {:development-mode "true"})))

(deftest asetusten-tyyppikonversio-toimii []
  (testing "tyyppikonversio toimii boolean-tyypeille"
    (is (= true (:development-mode (tulkitse-asetukset {:development-mode "true"}))))))
