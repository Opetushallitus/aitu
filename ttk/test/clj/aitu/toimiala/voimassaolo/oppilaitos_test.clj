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
            [aitu.toimiala.voimassaolo.oppilaitos :refer :all]))

(deftest voimassaolo-oppilaitos-test
 (testing
   "täydennä oppilaitoksen ja liittyvien tietojen voimassaolo:"
   (with-redefs [aitu.toimiala.voimassaolo.saanto.oppilaitos/taydenna-oppilaitoksen-voimassaolo
                 (fn [oppilaitos] (merge oppilaitos
                                         {:oppilaitos-taydennetty true}))
                 aitu.toimiala.voimassaolo.jarjestamissopimus/taydenna-sopimuksen-ja-liittyvien-tietojen-voimassaolo
                 (fn [sopimus] (merge sopimus
                                      {:sopimus-taydennetty true}))]
     (let [oppilaitos {}]
       (testing
         "täydentää oppilaitoksen"
         (is (true? (:oppilaitos-taydennetty (taydenna-oppilaitoksen-ja-liittyvien-tietojen-voimassaolo oppilaitos))))))
     (testing
       "täydentää järjestämissopimukset"
       (let [oppilaitos {:jarjestamissopimus [{} {}]}
             taydennetyt-sopimukset (:jarjestamissopimus (taydenna-oppilaitoksen-ja-liittyvien-tietojen-voimassaolo oppilaitos))]
         (is (every? true? (map :sopimus-taydennetty taydennetyt-sopimukset)))))
     (testing
       "ei muuta nil-arvoa"
       (is (nil? (taydenna-oppilaitoksen-ja-liittyvien-tietojen-voimassaolo nil)))))))
