;; Copyright (c) 2016 The Finnish National Board of Education - Opetushallitus
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

(ns aitu.integraatio.eperusteet-test
  (:require [clojure.test :refer :all]
            [clojure.java.io :as io]
            [clojure.walk :refer [keywordize-keys]]
            [clj-time.core :as time]
            [cheshire.core :as json]
            [aitu.integraatio.eperusteet :refer :all]
            oph.common.util.util))

(deftest hae-perusteet-test
  (with-redefs [oph.common.util.util/get-json-from-url (constantly
                                                         (-> (io/resource "eperusteet.json")
                                                             (slurp)
                                                             (json/parse-string)
                                                             keywordize-keys))]
    (let [perusteet (hae-perusteet nil nil)]
      (testing "hae-perusteet"
        (testing "löytää kaikki perusteet sivulta"
          (is (= 24 (count perusteet))))
        (testing "parsii perusteen oikein"
          (is (= (first perusteet)
                 ["355201" {:diaarinumero "26/011/2006"
                            :eperustetunnus 509690
                            :voimassa_alkupvm (time/local-date 2006 5 31)
                            :voimassa_loppupvm (time/local-date 2199 1 1)
                            :siirtymaajan_loppupvm (time/local-date 2199 1 1)}])))))))