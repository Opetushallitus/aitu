
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

(ns aitu.integraatio.sql.sql-oppilaitos-arkisto-test
  (:require [clojure.test :refer [deftest testing is use-fixtures]]
            [korma.core :as sql]
            [aitu.infra.oppilaitos-arkisto :as arkisto]
            [aitu.infra.jarjestamissopimus-arkisto :as jarjestamissopimus-arkisto]
            [aitu.integraatio.sql.sql-koulutustoimija-arkisto-test :refer [kt-testidata!]]
            [aitu.integraatio.sql.test-util :refer [tietokanta-fixture]]
            [aitu.integraatio.sql.test-data-util :refer :all]
            [aitu.integraatio.sql.jarjestamissopimus-arkisto-test :as jarjestamissopimus-arkisto-test]))

(use-fixtures :each tietokanta-fixture)

(defn luo-testidata
  []
  (lisaa-koulutustoimija! {:ytunnus "KT1"})
  (lisaa-oppilaitos! {:koulutustoimija "KT1"
                      :oppilaitoskoodi "OP1"
                      :nimi "Testioppilaitoksen nimi"}))

(defn aseta-sopimuksen-toimikunta-vanhentuneeksi
  []
  (sql/exec-raw (str "update tutkintotoimikunta set toimikausi_loppu='2000-12-01' where tkunta='TKUN'")))

(deftest ^:integraatio hae-ehdoilla-nimi
  (kt-testidata!)
  (testing "nimellä haku ei välitä isoista ja pienistä kirjaimista"
    (is (= (set (map :oppilaitoskoodi (arkisto/hae-ehdoilla {:sopimuksia "kaikki" :nimi "bar"})))
           #{"OL1" "OL2"}))))

(deftest ^:integraatio hae-ehdoilla-sopimuksia
  (kt-testidata!)
  (is (= (set (map :oppilaitoskoodi (arkisto/hae-ehdoilla {:sopimuksia "kylla"
                                                           :nimi "Oppilaitos"})))
         #{"OL1" "OL2" "OL5"}))
  (is (= (set (map :oppilaitoskoodi (arkisto/hae-ehdoilla {:sopimuksia "ei"
                                                           :nimi "Oppilaitos"})))
        #{"OL4"}))
  (is (= (set (map :oppilaitoskoodi (arkisto/hae-ehdoilla {:sopimuksia "kaikki"
                                                           :nimi "Oppilaitos"})))
        #{"OL1" "OL2" "OL4" "OL5"})))

(deftest ^:integraatio hae-ehdoilla-tutkinto
  (kt-testidata!)
  (testing "tutkinnon tunnuksella haku toimii"
    (is (= (map :oppilaitoskoodi (arkisto/hae-ehdoilla {:tunnus "T1" :sopimuksia "kylla"}))
           ["OL1"]))
    (is (= (set (map :oppilaitoskoodi (arkisto/hae-ehdoilla {:tunnus "927128" :sopimuksia "kylla"})))
           #{"OL2" "OL5"})))
  (testing "opintoalan tunnuksella haku toimii"
    (is (= (map :oppilaitoskoodi (arkisto/hae-ehdoilla {:tunnus "OA1" :sopimuksia "kylla"}))
         ["OL1"]))))

(deftest ^:integraatio hae-oppilaitoskoodilla-test
  (kt-testidata!)
  (is (= (select-keys (arkisto/hae "OL1") [:oppilaitoskoodi :nimi])
         {:oppilaitoskoodi "OL1", :nimi "Oppilaitos o1 bar bar"})))
