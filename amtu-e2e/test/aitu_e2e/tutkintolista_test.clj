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

(ns aitu-e2e.tutkintolista-test
  (:require [clojure.set :refer [subset?]]
            [clojure.test :refer [deftest is testing]]
            [clj-webdriver.taxi :as w]
            [clj-time.core :as time]
            [aitu-e2e.util :refer :all]
            [aitu-e2e.aitu-util :refer :all]
            [aitu-e2e.data-util :refer [with-data]]))

(def tutkintolista "/fi/#/search-tutkinto")

(defn nakyvat-tutkinnot []
  (map w/text (w/find-elements {:css ".e2e-hakutulos-nimi"})))

(defn nayta-kaikki []
  (valitse-radiobutton "tutkintoHakuehto.voimassaolo" "kaikki"))

(defn valitse-opintoala [tunnus]
  (w/select-option "#opintoala" {:value tunnus}))

(deftest tutkintolista-test []
  (with-webdriver
    (testing "tutkintolista"
      (testing "pitäisi näyttaa lista tutkinnoista"
        ;; Oletetaan, että
        (with-data {:koulutusalat [{:koodi "KA1"}]
                    :opintoalat [{:koodi "OA1"
                                  :koulutusala "KA1"}
                                 {:koodi "OA2"
                                  :koulutusala "KA1"}]
                    :tutkinnot [{:nimi_fi "A tutkinto 1"
                                 :tutkintotunnus "TU1"
                                 :opintoala "OA1"},
                                {:nimi_fi "A tutkinto 2"
                                 :tutkintotunnus "TU2"
                                 :opintoala "OA2"}
                                {:nimi_fi "Vanha tutkinto"
                                 :tutkintotunnus "TU3"
                                 :opintoala "OA1"
                                 :voimassa_alkupvm (time/local-date 2010 1 1)
                                 :voimassa_loppupvm (time/local-date 2010 12 31)
                                 :siirtymaajan_loppupvm (time/local-date 2012 12 31)}]}
          ;; Kun
          (avaa tutkintolista)
          ;; Niin
          (is (subset? #{"A tutkinto 1" "A tutkinto 2"}
                       (set (nakyvat-tutkinnot))))
          (is (not (subset? #{"Vanha tutkinto"}
                            (set (nakyvat-tutkinnot)))))

          ;; Kun
          (nayta-kaikki)
          ;; Niin
          (is (subset? #{"A tutkinto 1" "A tutkinto 2" "Vanha tutkinto"}
                       (set (nakyvat-tutkinnot))))

          ;; Kun
          (valitse-opintoala "OA1")
          ;; Niin
          (is (subset? #{"A tutkinto 1" "Vanha tutkinto"}
                       (set (nakyvat-tutkinnot))))
          (is (not (subset? #{"A tutkinto 2"}
                            (set (nakyvat-tutkinnot))))))))))
