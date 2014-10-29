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

(ns aitu.toimiala.voimassaolo.toimikunta-test
  (:require [clojure.test :refer [deftest testing is are]]
            [aitu.toimiala.voimassaolo.toimikunta :refer :all]))

(deftest voimassaolo-toimikunta-test
 (testing
   "täydennä toimikunnan ja liittyvien tietojen voimassaolo:"
   (with-redefs [aitu.toimiala.voimassaolo.saanto.toimikunta/taydenna-toimikunnan-voimassaolo
                 (fn [toimikunta] (merge toimikunta
                                         {:toimikunta-taydennetty true}))
                 aitu.toimiala.voimassaolo.jarjestamissopimus/taydenna-sopimukseen-liittyvien-tietojen-voimassaolo
                 (fn [sopimus] (merge sopimus
                                      {:sopimus-taydennetty true}))]
     (let [toimikunta {}]
       (testing
         "täydentää toimikunnan"
         (is (true? (:toimikunta-taydennetty (taydenna-toimikunnan-ja-liittyvien-tietojen-voimassaolo toimikunta))))))
     (testing
       "ei muuta nil-arvoa"
       (is (nil? (taydenna-toimikunnan-ja-liittyvien-tietojen-voimassaolo nil)))))))
