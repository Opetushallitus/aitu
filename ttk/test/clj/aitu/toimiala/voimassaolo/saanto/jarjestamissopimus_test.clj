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

(ns aitu.toimiala.voimassaolo.saanto.jarjestamissopimus-test
  (:require [clojure.test :refer [deftest is are testing]]
            [clj-time.core :as time]
            [aitu.toimiala.voimassaolo.saanto.jarjestamissopimus :refer :all]
            [aitu.test-timeutil :refer :all]))

(deftest sopimuksen-voimassaoloajalla?-test
 (testing
   "sopimuksen voimassaoloajalla?:"
   (are [kuvaus jarjestamissopimus odotettu-tulos]
        (is (= (sopimuksen-voimassaoloajalla? jarjestamissopimus) odotettu-tulos) kuvaus)
        "ei ole alkanut" {:alkupvm tulevaisuudessa
                          :loppupvm tulevaisuudessa} false
        "on alkanut tänään" {:alkupvm tanaan
                             :loppupvm tulevaisuudessa} true
        "loppupäivä on tulevaisuudessa" {:alkupvm menneisyydessa
                                         :loppupvm tulevaisuudessa} true
        "loppupäivä on tänään" {:alkupvm menneisyydessa
                                :loppupvm tanaan} true
        "loppupäivä on menneisyydessä" {:alkupvm menneisyydessa
                                        :loppupvm menneisyydessa} false
        "loppupäivää ei ole määritelty" {:alkupvm menneisyydessa} true
        "loppupäivää on nil" {:alkupvm menneisyydessa
                              :loppupvm nil} true)))

(deftest joku-tutkinto-voimassa?-test
 (testing
   "joku tutkinto voimassa?:"
   (let [tutkinto {}]
     (testing
       "tutkinto on voimassa"
       (with-redefs [aitu.toimiala.voimassaolo.saanto.tutkinto/tutkinto-voimassa?
                     (constantly true)]
         (is (true? (joku-tutkinto-voimassa? [tutkinto])))))
     (testing
       "tutkinto ei ole voimassa"
       (with-redefs [aitu.toimiala.voimassaolo.saanto.tutkinto/tutkinto-voimassa?
                     (constantly false)]
         (is (false? (joku-tutkinto-voimassa? [tutkinto])))))
     (testing
       "kaikki tutkinnot eivät ole voimassa"
       (let [tutkinto1 {:voimassa false}
             tutkinto2 {:voimassa true}]
         (with-redefs [aitu.toimiala.voimassaolo.saanto.tutkinto/tutkinto-voimassa?
                       :voimassa]
           (is (true? (joku-tutkinto-voimassa? [tutkinto1 tutkinto2]))))))
     (testing
       "ei tutkintoja"
       (with-redefs [aitu.toimiala.voimassaolo.saanto.tutkinto/tutkinto-voimassa?
                     (constantly nil)]
         (is (true? (joku-tutkinto-voimassa? []))))))))

