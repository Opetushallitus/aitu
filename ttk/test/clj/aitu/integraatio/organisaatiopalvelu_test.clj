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

(ns aitu.integraatio.organisaatiopalvelu-test
  (:require [clojure.test :refer :all]
            [aitu.integraatio.organisaatiopalvelu :refer :all]))

(deftest oid-polku-test
  (let [koulutustoimijakoodit [{:ytunnus "0000000-0"
                                :oid "1"}]
        oppilaitoskoodit [{:oppilaitoskoodi "12345"
                           :oid "2"
                           :parentOid "1"}
                          {:oppilaitoskoodi "23456"
                           :oid "3"
                           :parentOid "2"}]]
    (testing "oid-polku"
      (is (= (oid-polku koulutustoimijakoodit oppilaitoskoodit)
             {"1" "0000000-0"
              "2" "0000000-0"
              "3" "0000000-0"})))))
