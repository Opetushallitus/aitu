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

(ns aitu.integraatio.sql.i18n-enum-test
  (:require [clojure.test :refer :all]
            [aitu.infra.i18n :as i18n]
            [korma.core :as sql]
            [aitu.integraatio.sql.test-util :refer [tietokanta-fixture testi-locale]]))

(use-fixtures :each tietokanta-fixture)

(defn enum-arvot
  "Palauttaa lokalisaatiosta kaikki tekstit enum avaimen alta"
  []
  (:enum (binding [i18n/*locale* testi-locale]
           (i18n/tekstit))))

(defn enum-keyword->nimi
  "Muuttaa lokalisaatiossa olevan enum-arvo avaimen enum nimeksi"
  [enum-keyword]
  (first (clojure.string/split (name enum-keyword) #"-")))

(defn hae-enum-kannasta
  "hakee kannasta kaikki arvot tietylle enum nimelle"
  [nimi]
  (sql/exec-raw (str "select nimi "
                     "from " nimi) :results))

(deftest ^:integraatio enum-arvot-kannassa-test
  "Testaa että lokaaleista löytyvät enum arvojen vastineet ovat kannassa"
  []
  (doseq [avain (keys (enum-arvot))]
    (let [nimi (enum-keyword->nimi avain)
          kanta-arvot (set (for [arvo (hae-enum-kannasta nimi)] (:nimi arvo)))
          lokalisoidut (set (for [arvo (keys (avain (enum-arvot)))] (name arvo)))]
      (is (= kanta-arvot lokalisoidut)))))
