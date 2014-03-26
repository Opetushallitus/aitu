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

(ns aitu.integraatio.sql.sql-enum-arkisto-test
  (:require [clojure.test :refer [deftest testing is are use-fixtures]]
            [korma.core :as sql]
            [aitu.infra.enum-arkisto :as arkisto]
            [aitu.integraatio.sql.test-util :refer [tietokanta-fixture]]))

(use-fixtures :each tietokanta-fixture)

(deftest ^:integraatio hae-enum-test
  (testing "Hae enum tieto"
    (testing "pitäisi palauttaa oikea määrä edustus enumista"
      (is (= (count (arkisto/hae-kaikki "edustus")) 6)))

    (testing "pitäisi palauttaa oikea määrä alue enumista"
      (is (= (count (arkisto/hae-kaikki "alue")) 7)))

    (testing "pitäisi palauttaa oikea määrä rooli enumista"
      (is (= (count (arkisto/hae-kaikki "rooli")) 6)))

    (testing "pitäisi palauttaa oikea määrä kieli enumista"
      (is (= (count (arkisto/hae-kaikki "kieli")) 5)))

    (testing "pitäisi palauttaa oikea määrä sukupuoli enumista"
      (is (= (count (arkisto/hae-kaikki "sukupuoli")) 2)))

    (testing "olemattomalla enumilla pitäisi tulla assertiovirhe"
      (is (thrown? AssertionError (arkisto/hae-kaikki "jotain_muuta"))))))
