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

(ns aitu.integraatio.sql.haku-arkisto-test
  (:require  [korma.core :as sql]
             [clojure.test :refer [deftest testing is use-fixtures]]
             [aitu.integraatio.sql.test-util :refer [tietokanta-fixture]]
             [aitu.integraatio.sql.test-data-util :refer :all]
             [aitu.infra.haku-arkisto :as arkisto]
             [aitu.integraatio.sql.korma :refer :all]))

(use-fixtures :each tietokanta-fixture)

(deftest ^:integraatio hae-tunnuksella-toimii-yhdella-osumalla-test
  (testing "palauttaa tiedon kun löytyy vain yksi vastaus"
    (lisaa-toimikunta! {:diaarinumero "12345"})
    (is (:url (arkisto/hae-tunnuksella-ensimmainen "12345")))))

(deftest ^:integraatio hae-tunnuksella-toimii-useammalla-osumalla-test
  (testing "palauttaa ensimmäisen kun löytyy useampi vastaus"
    (lisaa-koulutus-ja-opintoala!)
    (lisaa-tutkinto! {:tutkintotunnus "12345"})
    (lisaa-toimikunta! {:diaarinumero "12345"})
    (is (:url (arkisto/hae-tunnuksella-ensimmainen "12345")))))

(deftest ^:integraatio hae-tunnuksella-toimii-jos-ei-loydy-test
  (testing "palauttaa tyhjän kun ei löydy mitään"
    (is (not (arkisto/hae-tunnuksella-ensimmainen "12345")))))
