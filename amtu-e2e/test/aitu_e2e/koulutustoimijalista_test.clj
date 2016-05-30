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

(ns aitu-e2e.koulutustoimijalista-test
  (:require [clojure.set :refer [subset?]]
            [clojure.test :refer [deftest is testing]]
            [clj-webdriver.taxi :as w]
            [aitu-e2e.util :refer :all]
            [aitu-e2e.aitu-util :refer :all]
            [aitu-e2e.datatehdas :as dt]
            [aitu-e2e.data-util :refer [with-data]]
            [aitu-e2e.oppilaitoslista-test :refer [oppilaitoshaku-sopimukset-data]]))

(def koulutustoimijalista "/fi/#/search-koulutustoimija")

(defn valitse-ei-sopimuksia []
  (w/click "#sopimuksia_ei")
  (odota-angular-pyyntoa))

(defn valitse-nayta-kaikki []
  (w/click "#sopimuksia_kaikki")
  (odota-angular-pyyntoa))

(defn nakyvat-koulutustoimijat []
  (map w/text (w/find-elements {:css ".e2e-koulutustoimija-nimi"})))

(deftest koulutustoimijalista-test []
  (with-webdriver
    (testing "koulutustoimijalista"
      ;; Oletetaan, että
      (with-data (oppilaitoshaku-sopimukset-data)
        (testing "pitäisi näyttaa lista koulutustoimijoista joilla on järjestämissopimus"
          ;; Kun
          (avaa koulutustoimijalista)
          ;; Niin
          (is (subset? #{"aaAnkkalinnan kaupunki" "aaHanhivaaran kaupunki"}
                       (set (nakyvat-koulutustoimijat)))))

        (testing "pitäisi näyttaa lista koulutustoimijoista, joilla ei ole järjestämissopimusta, mutta on ollut aikaisemmin."
          ;; Kun
          (avaa koulutustoimijalista)
          (valitse-ei-sopimuksia)
          ;; Niin
          (is (empty? (set (nakyvat-koulutustoimijat)))))

        (testing "pitäisi näyttaa lista kaikista koulutustoimijoista"
          ;; Kun
          (avaa koulutustoimijalista)
          (valitse-nayta-kaikki)
          ;; Niin
          (is (subset? #{"aaAnkkalinnan kaupunki" "aaHanhivaaran kaupunki"}
                       (set (nakyvat-koulutustoimijat)))))

        (testing "Pitäisi näyttää lista koulutustoimijoista, joilla on tietyn opintoalan tutkinto vastuulla"
          ;; Kun
          (avaa koulutustoimijalista)
          (valitse-select2-optio "search.ala" "tunnus" "Sähköala")
          (odota-angular-pyyntoa)
          ;; Niin
          (is (= (nakyvat-koulutustoimijat) ["aaAnkkalinnan kaupunki" "aaHanhivaaran kaupunki"])))))))
