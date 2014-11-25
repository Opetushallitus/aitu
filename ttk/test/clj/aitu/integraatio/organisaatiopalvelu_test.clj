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

(deftest generoi-oid->ytunnus-test
  ;; Koulutustoimija 1 <- oppilaitos 2 <-  oppilaitos 3
  (let [koulutustoimijakoodit [{:ytunnus "0000000-0"
                                :oid "1"}]
        oppilaitoskoodit [{:oppilaitoskoodi "12345"
                           :oid "2"
                           :parentOid "1"}
                          {:oppilaitoskoodi "23456"
                           :oid "3"
                           :parentOid "2"}]]
    (testing "Oppilaitoksen Y-tunnuksena käytetään oppilaitoksen koulutustoimijan Y-tunnusta"
      (is (= (generoi-oid->y-tunnus koulutustoimijakoodit oppilaitoskoodit)
             {"1" "0000000-0"
              "2" "0000000-0"
              "3" "0000000-0"})))))

(deftest generoi-oid->ytunnus-ohitetaan-oppilaitos-ilman-parenttia-test
  ;; Oppilaitos ilman parenttia
  (let [koulutustoimijakoodit [{:ytunnus "0000000-0"
                                :oid "1"}]
        oppilaitoskoodit [{:oppilaitoskoodi "12345"
                           :oid "2"
                           :parentOid "1"}
                          {:oppilaitoskoodi "123123"
                           :oid "3"
                           :parentOid "123123"}
                          {:oppilaitoskoodi "23456"
                           :oid "4"
                           :parentOid "1"}]]
    (testing "Oppilaitos ilman parenttia ohitetaan"
      (is (= (generoi-oid->y-tunnus koulutustoimijakoodit oppilaitoskoodit)
             {"1" "0000000-0"
              "2" "0000000-0"
              "4" "0000000-0"})))))
