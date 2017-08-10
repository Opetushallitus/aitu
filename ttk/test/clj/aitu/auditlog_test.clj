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
     [clj-time.local :as time-local]
     [oph.korma.korma-auth :as ka]
     [oph.common.infra.common-audit-log :as common-audit-log]
     [oph.common.infra.common-audit-log-test :as common-audit-log-test]
     [aitu.auditlog :as auditlog]
     [aitu.log-util :refer :all]))

(defn ^:private log-validate-with-mock-user
  [f expected-msg]
  (binding [ka/*current-user-uid* "T-X"
            ka/*current-user-oid* (promise)
            common-audit-log/*request-meta* common-audit-log-test/test-request-meta]
    (deliver ka/*current-user-oid* "T-X-oid")
    (common-audit-log/konfiguroi-common-audit-lokitus common-audit-log-test/test-environment-meta)
    (log-validate f expected-msg)))

(deftest test-jarjestamissopimus-paivitys
  (testing "logittaa oikein järjestämissopimuksen päivityksen"
    (log-validate-with-mock-user
      #(auditlog/jarjestamissopimus-paivitys! 123 "12/12")
      [[:info "{\"operation\":\"päivitys\",\"type\":\"log\",\"hostname\":\"host\",\"applicationType\":\"virkailija\",\"delta\":[{\"op\":\"päivitys\",\"path\":\"sopimusid\",\"value\":123},{\"op\":\"päivitys\",\"path\":\"diaarinumero\",\"value\":\"12/12\"}],\"logSeq\":1,\"bootTime\":\"1980-09-20T01:02:03.123\",\"target\":{\"järjestämissopimus\":null,\"id\":\"123\"},\"serviceName\":\"aitu\",\"version\":1,\"timestamp\":\"2017-08-10T15:19:03.877+03:00\",\"user\":{\"oid\":\"T-X-oid\",\"ip\":\"192.168.50.1\",\"session\":\"955d43a3-c02d-4ab8-a61f-141f29c44a84\",\"userAgent\":\"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.90 Safari/537.36\"}}"]]
      )))

(deftest test-jarjestamissopimus-lisays
  (testing "logittaa oikein järjestämissopimuksen lisäyksen"
   (log-validate-with-mock-user
     #(auditlog/jarjestamissopimus-lisays! 123 "12/12")
     [[:info "{\"operation\":\"lisäys\",\"type\":\"log\",\"hostname\":\"host\",\"applicationType\":\"virkailija\",\"delta\":[{\"op\":\"lisäys\",\"path\":\"sopimusid\",\"value\":123},{\"op\":\"lisäys\",\"path\":\"diaarinumero\",\"value\":\"12/12\"}],\"logSeq\":1,\"bootTime\":\"1980-09-20T01:02:03.123\",\"target\":{\"järjestämissopimus\":null,\"id\":\"123\"},\"serviceName\":\"aitu\",\"version\":1,\"timestamp\":\"2017-08-10T15:19:03.877+03:00\",\"user\":{\"oid\":\"T-X-oid\",\"ip\":\"192.168.50.1\",\"session\":\"955d43a3-c02d-4ab8-a61f-141f29c44a84\",\"userAgent\":\"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.90 Safari/537.36\"}}"]]
     )))

