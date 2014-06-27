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
            [aitu-e2e.datatehdas :as dt]
            [aitu-e2e.data-util :refer [with-data]]
            [aitu-e2e.jarjestajalista-test :refer [jarjestajahaku-sopimukset-data]]))

(def koulutustoimijalista "/fi/#/search-koulutustoimija")

(defn nakyvat-koulutustoimijat []
  (map w/text (w/find-elements (-> *ng*
                                 (.repeater "hakutulos in hakutulokset")
                                 (.column "hakutulos.nimi")))))

(deftest koulutustoimijalista-test []
  (with-webdriver
    (testing "koulutustoimijalista"
      ;; Oletetaan, että
      (with-data (jarjestajahaku-sopimukset-data)
        (testing "pitäisi näyttaa lista koulutustoimijoista"
          ;; Kun
          (avaa koulutustoimijalista)
          ;; Niin
          (is (subset? #{"aaRuikonperän koulutuskuntayhtymä" "aaAnkkalinnan kaupunki" "aaHanhivaaran kaupunki"}
                       (set (nakyvat-koulutustoimijat)))))

        (testing "Pitäisi näyttää lista koulutustoimijoista joilla on tietyn opintoalan tutkinto vastuulla"
          ;; Kun
          (avaa koulutustoimijalista)
          (valitse-select2-optio "search" "termi" "Sähköala")
          (odota-angular-pyyntoa)
          ;; Niin
          (is (= (nakyvat-koulutustoimijat) ["aaAnkkalinnan kaupunki" "aaHanhivaaran kaupunki"])))))))
