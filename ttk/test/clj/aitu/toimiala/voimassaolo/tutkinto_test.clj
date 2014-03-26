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

(ns aitu.toimiala.voimassaolo.tutkinto-test
  (:require [clojure.test :refer [deftest testing is are]]
            [aitu.toimiala.voimassaolo.tutkinto :refer :all]))

(deftest voimassaolo-tutkinto-test
 (testing
   "täydennä tutkinto:"
   (with-redefs [aitu.toimiala.voimassaolo.saanto.tutkinto/taydenna-tutkinnon-voimassaolo
                 (fn [tutkinto] (merge tutkinto
                                       {:tutkinto-taydennetty true}))
                 aitu.toimiala.voimassaolo.jarjestamissopimus/taydenna-sopimuksen-ja-liittyvien-tietojen-voimassaolo
                 (fn [sopimus] (merge sopimus
                                      {:sopimus-taydennetty true}))]
     (let [tutkinto {}]
       (testing
         "täydentää tutkinnon"
         (is (true? (:tutkinto-taydennetty (taydenna-tutkinnon-ja-liittyvien-tietojen-voimassaolo tutkinto))))))
     (testing
       "täydentää järjestämissopimukset"
       (let [tutkinto {:sopimus_ja_tutkinto [{:jarjestamissopimus {}}
                                             {:jarjestamissopimus {}}]}
             taydennetyt-sopimukset (map :jarjestamissopimus
                                        (:sopimus_ja_tutkinto
                                          (taydenna-tutkinnon-ja-liittyvien-tietojen-voimassaolo tutkinto)))]
         (is (every? true? (map :sopimus-taydennetty taydennetyt-sopimukset)))))
     (testing
       "ei muuta nil-arvoa"
       (is (nil? (taydenna-tutkinnon-ja-liittyvien-tietojen-voimassaolo nil)))))))
