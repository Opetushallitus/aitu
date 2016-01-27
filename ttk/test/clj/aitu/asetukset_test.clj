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

(deftest lue-asetukset-test
  (testing "palauttaa tyhjän mapin jos annettua tiedostoa ei löydy"
    (is (= {} (lue-asetukset-tiedostosta "olematon tiedosto")))))

(deftest service-path-test
  (testing "context path from URL"
     (are [url path] (= (service-path url) path)
       "http://localhost" "/"
       "http://localhost:8080" "/"
       "http://foohost/" "/"
       "https://opintopolku.fi/aitu/" "/aitu"
       "https://mooze.org:6510/k-rad" "/k-rad")))