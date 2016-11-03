
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
(ns aitu.integraatio.sql.suorittaja-test
  (:require [clojure.test :refer [deftest testing is use-fixtures]]
            [korma.core :as sql]
            [aitu.infra.suorittaja-arkisto :as suorittaja-arkisto]
            [aitu.integraatio.sql.test-util :refer [tietokanta-fixture]]
            [aitu.integraatio.sql.test-data-util :refer :all]))
  
(use-fixtures :each tietokanta-fixture)

(deftest ^:integraatio suorittaja-hetutarkistus
  (is (= false (suorittaja-arkisto/hetu-kaytossa? -2 "101066-9451")))
  (is (= false (suorittaja-arkisto/hetu-kaytossa? -1 "")))
  (is (= true (suorittaja-arkisto/hetu-kaytossa? -1 "101066-9451")))
  (is (= false (suorittaja-arkisto/hetu-kaytossa? -1 nil)))
  (is (= true (suorittaja-arkisto/hetu-kaytossa? nil "101066-9451"))))