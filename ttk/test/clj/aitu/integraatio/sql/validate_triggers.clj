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

(ns aitu.integraatio.sql.validate-triggers
  (:require
    [clojure.test :refer [deftest testing is use-fixtures]]
    [aitu.integraatio.sql.test-util :refer [tietokanta-fixture]]
    [oph.postgresql-util :refer [tarkista-triggerit]]))

(use-fixtures :each tietokanta-fixture)

(deftest ^:integraatio tarkista-puuttuvat-triggerit
  (testing "tarkistetaan että kaikilla omilla tauluilla on ainakin joku update/insert triggeri"
    (let [vialliset-taulut (filter #(not= (:table_name %) "sopimuspaivitys") (tarkista-triggerit))]
     (is (empty? vialliset-taulut) (str "viallisia tauluja! " vialliset-taulut)))))