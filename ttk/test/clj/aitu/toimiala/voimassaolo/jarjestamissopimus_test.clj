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

(ns aitu.toimiala.voimassaolo.jarjestamissopimus-test
  (:require [clojure.test :refer [deftest is testing]]
            [aitu.toimiala.voimassaolo.jarjestamissopimus :refer :all]))

(deftest voimassaolo-sopimus-test
  (testing
    "täydennä sopimuksen ja liittyvien tietojen voimassaolo:"
    (with-redefs [aitu.toimiala.voimassaolo.saanto.tutkinto/taydenna-tutkinnon-voimassaolo
                  (fn [tutkinto] (assoc tutkinto :tutkinto-taydennetty true))
                  aitu.toimiala.voimassaolo.saanto.toimikunta/taydenna-toimikunnan-voimassaolo
                  (fn [toimikunta] (assoc toimikunta :toimikunta-taydennetty true))]
      (testing
        "täydentää sopimuksen tutkinnot"
        (let [sopimus {:sopimus_ja_tutkinto [{:tutkinto {}}
                                             {:tutkinto {}}]
                       :toimikunta {}}
              taydennetyt-tutkinnot (map :tutkintoversio
                                        (:sopimus_ja_tutkinto
                                          (taydenna-sopimukseen-liittyvien-tietojen-voimassaolo sopimus)))]
          (is (every? true? (map :tutkinto-taydennetty taydennetyt-tutkinnot)))))
      (testing
        "täydentää sopimuksen toimikunnan"
        (let [sopimus {:toimikunta {}}]
          (is (true? (:toimikunta-taydennetty (:toimikunta (taydenna-sopimukseen-liittyvien-tietojen-voimassaolo sopimus)))))))
      (testing
        "ei muuta nil-arvoa"
        (is (nil? (taydenna-sopimukseen-liittyvien-tietojen-voimassaolo nil)))))))
