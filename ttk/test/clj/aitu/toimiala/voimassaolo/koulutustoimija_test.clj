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

(ns aitu.toimiala.voimassaolo.koulutustoimija-test
  (:require [clojure.test :refer [deftest testing is are]]
            [aitu.toimiala.voimassaolo.koulutustoimija :refer :all]))

(deftest voimassaolo-koulutustoimija-test
 (testing
   "täydennä koulutustoimijan ja liittyvien tietojen voimassaolo:"
   (with-redefs [aitu.toimiala.voimassaolo.jarjestamissopimus/taydenna-sopimuksen-ja-liittyvien-tietojen-voimassaolo
                 (fn [sopimus] (assoc sopimus :sopimus-taydennetty true))]
     (testing
       "täydentää järjestämissopimukset"
       (let [koulutustoimija {:jarjestamissopimus [{} {}]}
             taydennetyt-sopimukset (:jarjestamissopimus (taydenna-koulutustoimijan-ja-liittyvien-tietojen-voimassaolo koulutustoimija))]
         (is (every? true? (map :sopimus-taydennetty taydennetyt-sopimukset)))))
     (testing
       "ei muuta nil-arvoa"
       (is (nil? (taydenna-koulutustoimijan-ja-liittyvien-tietojen-voimassaolo nil)))))))
