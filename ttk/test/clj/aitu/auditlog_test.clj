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

(ns aitu.auditlog-test
   (:require
     [clojure.test :refer :all]
     [aitu.auditlog :as auditlog]
     [oph.korma.korma-auth :as ka]
     [clojure.tools.logging :as log]))

(defn ^:private log-through
  "palauttaa auditlogiin kirjoitetun viestin levelin ja sisällön kun kutsutaan funktiota f"
  [f]
  (let [log (atom [])]
    (with-redefs [log/log* (fn [_ level _ msg]
                             (swap! log conj [level msg]))]
      (reset! log [])
      (binding [ka/*current-user-uid* "T-X"]
        (f)
        @log))))

(defn ^:private log-validate
  [f expected-msg]
  (is (= expected-msg (log-through f))))

(deftest test-jarjestamissopimus-paivitys
  (testing "logittaa oikein järjestämissopimuksen päivityksen"
    (log-validate
      #(auditlog/jarjestamissopimus-paivitys! 123 "12/12")
      [[:info "uid: T-X oper: päivitys kohde: järjestämissopimus meta: ({:sopimusid 123, :diaarinumero \"12/12\"})"]])))

(deftest test-jarjestamissopimus-lisays
  (testing "logittaa oikein järjestämissopimuksen lisäyksen"
   (log-validate
     #(auditlog/jarjestamissopimus-lisays! 123 "12/12")
     [[:info "uid: T-X oper: lisäys kohde: järjestämissopimus meta: ({:sopimusid 123, :diaarinumero \"12/12\"})"]])))

