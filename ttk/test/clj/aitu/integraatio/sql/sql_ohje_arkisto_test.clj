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

(ns aitu.integraatio.sql.sql-ohje-arkisto-test
  (:require [clojure.test :refer [deftest testing is use-fixtures]]
            [aitu.integraatio.sql.test-util :refer [tietokanta-fixture]]
            [aitu.infra.ohje-arkisto :as arkisto]))

(use-fixtures :each tietokanta-fixture)

(def testi-ohje
  {:ohjetunniste "ohje1"
   :teksti_fi "ohjeteksti1"
   :teksti_sv "ohjeteksti1 (sv)"})

(def paivitetty-testi-ohje
  {:ohjetunniste "ohje1"
   :teksti_fi "ohjeteksti paivitetty"
   :teksti_sv "ohjeteksti paivitetty (sv)"})

(deftest ^:integraatio ohjeen-talletus-ja-haku-test
  (testing "Uuden ohjeen luonti onnistuu"
    (arkisto/muokkaa-tai-luo-uusi! testi-ohje)
    (is (= (select-keys (arkisto/hae (:ohjetunniste testi-ohje)) [:ohjetunniste :teksti_fi :teksti_sv]) testi-ohje)))
  (testing "Olemassaolevan ohjeen muokkaus onnistuu"
    (arkisto/muokkaa-tai-luo-uusi! paivitetty-testi-ohje)
    (is (= (select-keys (arkisto/hae (:ohjetunniste testi-ohje)) [:ohjetunniste :teksti_fi :teksti_sv]) paivitetty-testi-ohje))))
