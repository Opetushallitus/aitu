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

(ns aitu-e2e.oppilaitoslista-test
  (:require [clojure.set :refer [subset?]]
            [clojure.test :refer [deftest is testing]]
            [clj-webdriver.taxi :as w]
            [aitu-e2e.util :refer :all]
            [aitu-e2e.aitu-util :refer :all]
            [aitu-e2e.datatehdas :as dt]
            [aitu-e2e.data-util :refer [with-data]]))

(def oppilaitoslista "/fi/#/search-oppilaitos")

(defn nakyvat-oppilaitokset []
  (map w/text (w/find-elements (-> *ng*
                                 (.repeater "hakutulos in hakutulokset")
                                 (.column "hakutulos.nimi")))))

(defn valitse-ei-sopimuksia []
  (w/click "#sopimuksia_ei")
  (odota-angular-pyyntoa))

(defn valitse-nayta-kaikki []
  (w/click "#sopimuksia_kaikki")
  (odota-angular-pyyntoa))

; kolme oppilaitosta
; opintoala "sähköala"
; johon kuuluu tutkinto jotta
; kahdella oppilaitoksella voimassa oleva järjestämissopimus tutkintoon ja siten opintoalaan

(defn oppilaitoshaku-sopimukset-data []
  (let [koulutustoimija1 (dt/setup-koulutustoimija "KT1" "aaAnkkalinnan kaupunki")
        koulutustoimija2 (dt/setup-koulutustoimija "KT2" "aaHanhivaaran kaupunki")
        koulutustoimija-ilman-sopimusta (dt/setup-koulutustoimija "KT3" "aaRuikonperän koulutuskuntayhtymä")
        oppilaitos1 (dt/oppilaitos-nimella "aaAnkkalinnan aikuiskoulutuskeskus" (:ytunnus koulutustoimija1))
        oppilaitos2 (dt/oppilaitos-nimella "aaHanhivaaran kauppaopisto" (:ytunnus koulutustoimija2))
        oppilaitos-ilman-sopimusta (dt/oppilaitos-nimella "aaRuikonperän multakurkkuopisto" (:ytunnus koulutustoimija-ilman-sopimusta))
        tutkinto-map (dt/tutkinto-opintoalan-nimella "Sähköala")
        tutkinto (:tutkinnot tutkinto-map)
        toimikuntatunnus "ILMA"
        toimikunta-map (dt/setup-toimikunta toimikuntatunnus)
        sopimus1 (dt/setup-voimassaoleva-jarjestamissopimus koulutustoimija1 oppilaitos1 toimikuntatunnus tutkinto)
        sopimus2 (dt/setup-voimassaoleva-jarjestamissopimus koulutustoimija2 oppilaitos2 toimikuntatunnus tutkinto)]
    (dt/merge-datamaps tutkinto-map toimikunta-map sopimus1 sopimus2
      {:oppilaitokset [oppilaitos1 oppilaitos2 oppilaitos-ilman-sopimusta]
       :koulutustoimijat [koulutustoimija1 koulutustoimija2 koulutustoimija-ilman-sopimusta]})))

(deftest tutkintolista-test []
  (with-webdriver
    (testing "oppilaitoslista"
      ;; Oletetaan, että
      (with-data (oppilaitoshaku-sopimukset-data)
        (testing "pitäisi näyttaa lista oppilaitoksista joilla on järjestämissopimus"
          ;; Kun
          (avaa oppilaitoslista)
          ;; Niin
          (is (subset? #{"aaAnkkalinnan aikuiskoulutuskeskus" "aaHanhivaaran kauppaopisto"}
                       (set (nakyvat-oppilaitokset)))))

        (testing "pitäisi näyttaa lista oppilaitoksista joilla ei ole järjestämissopimusta"
          ;; Kun
          (avaa oppilaitoslista)
          (valitse-ei-sopimuksia)
          ;; Niin
          (is (subset? #{"aaRuikonperän multakurkkuopisto"}
                       (set (nakyvat-oppilaitokset)))))

        (testing "pitäisi näyttaa lista kaikista oppilaitoksista"
          ;; Kun
          (avaa oppilaitoslista)
          (valitse-nayta-kaikki)
          ;; Niin
          (is (subset? #{"aaRuikonperän multakurkkuopisto" "aaAnkkalinnan aikuiskoulutuskeskus" "aaHanhivaaran kauppaopisto"}
                       (set (nakyvat-oppilaitokset)))))

        (testing "Pitäisi näyttää lista oppilaitoksista joilla on tietyn opintoalan tutkinto vastuulla"
          ;; Kun
          (avaa oppilaitoslista)
          (valitse-select2-optio "search" "tunnus" "Sähköala")
          (odota-angular-pyyntoa)
          ;; Niin
          (is (= (nakyvat-oppilaitokset) ["aaAnkkalinnan aikuiskoulutuskeskus" "aaHanhivaaran kauppaopisto"])))))))
