;; Copyright (c) 2015 The Finnish National Board of Education - Opetushallitus
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

(ns aitu.infra.jasenesitykset-arkisto-test
  (:require [clojure.test :refer :all]
            [aitu.infra.jasenesitykset-arkisto :as arkisto]
            [aitu.integraatio.sql.test-data-util :as test-data]
            [aitu.integraatio.sql.test-util :refer [tietokanta-fixture]]))

(use-fixtures :each tietokanta-fixture)

(deftest ^:integraatio nakyvyys-testi
  (test-data/lisaa-jarjesto! {:jarjestoid -100
                              :nimi_fi "Keskusjärjestö"
                              :keskusjarjestotieto true})
  (test-data/lisaa-jarjesto! {:jarjestoid -200
                              :nimi_fi "Jäsenjärjestö 1"
                              :keskusjarjestoid -100})
  (test-data/lisaa-jarjesto! {:jarjestoid -300
                              :nimi_fi "Jäsenjärjestö 2"
                              :keskusjarjestoid -100})
  (test-data/lisaa-henkilo! {:henkiloid -100})
  (test-data/lisaa-henkilo! {:henkiloid -200})
  (test-data/lisaa-toimikunta! {:tkunta "-100"})
  (test-data/lisaa-jasen! {:henkiloid -100
                           :toimikunta "-100"
                           :status "esitetty"
                           :esittaja -100})
  (test-data/lisaa-jasen! {:henkiloid -200
                           :toimikunta "-100"
                           :status "esitetty"
                           :esittaja -200})

  (testing "Keskusjärjestö näkee kaikki jäsenesityspyynnöt"
    (let [esitykset (arkisto/hae -100 {})]
      (is (= (count esitykset) 2))))

  (testing "Alijärjestö näkee vain oman jäsenesityspyynnön"
    (let [esitykset (arkisto/hae -200 {})]
      (is (= (count esitykset) 1))
      (is (every? #{-200} (map :esittaja esitykset)))))

  (testing "Alijärjestö ei näe toisen alijärjestön jäsenesityspyyntöjä"
    (let [esitykset (arkisto/hae -300 {})]
      (is (= (count esitykset) 0))))

  (testing "OPH näkee kaikki jäsenesityspyynnöt"
    (let [esitykset (arkisto/hae nil {})]
      (is (>= (count esitykset) 2)))))
