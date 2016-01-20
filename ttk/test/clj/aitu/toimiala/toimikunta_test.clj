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

(ns aitu.toimiala.toimikunta-test
    (:require [clojure.test :refer [deftest is are testing]]
              [clj-time.core :as time]
              [aitu.toimiala.toimikunta :refer :all]
              [aitu.test-timeutil :refer :all]
              [schema.core :as s]))

(deftest taydenna-toimikunta-test
 (testing
   "täydennä toimikunta:"
   (with-redefs [aitu.toimiala.voimassaolo.toimikunta/taydenna-toimikunnan-ja-liittyvien-tietojen-voimassaolo
                 (fn [toimikunta] (merge toimikunta
                                         {:voimassaolo-taydennetty true}))
                 aitu.toimiala.henkilo/piilota-salaiset
                 (fn [toimikunta kentta] (merge toimikunta
                                                {:salaiset-piilotettu-kentassa kentta}))]
     (let [toimikunta {}]
       (testing
         "täydentää voimassaolon"
         (is (true? (:voimassaolo-taydennetty (taydenna-toimikunta toimikunta)))))
       (testing
         "piilottaa salaiset tiedot jäsenyyksissä"
         (is (= :jasenyys (:salaiset-piilotettu-kentassa (taydenna-toimikunta toimikunta)))))))))
;     (testing
;       "ei muuta nil-arvoa"
;       (is (nil? (taydenna-toimikunta nil)))))))
