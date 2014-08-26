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

(ns aitu.integraatio.sql.sql-tutkinto-arkisto-test
  (:require [clojure.test :refer [deftest testing is are use-fixtures]]
            [aitu.infra.tutkinto-arkisto :as arkisto]
            [aitu.toimiala.tutkinto :refer :all]
            [aitu.integraatio.sql.test-util :refer [tietokanta-fixture]]
            [aitu.integraatio.sql.test-data-util :refer :all]
            [clj-time.core :as time]))

(use-fixtures :each tietokanta-fixture)

(deftest ^:integraatio sql-crud-lisaa!
  "Testaa crud operaatiot tietokantaan. Aiheuttaa kannan tyhjennyksen tällä hetkellä."
  (lisaa-koulutus-ja-opintoala!)
  (let [arkisto-count (count (arkisto/hae-kaikki))]
    (lisaa-tutkinto! {:tutkintotunnus "545445"})
    (is (= (count (arkisto/hae-kaikki)) (inc arkisto-count)))))

(deftest ^:integraatio hae-test
  "Pitäisi tulla nil olemattomalla tutkintotunnuksella"
  (is (nil? (arkisto/hae "qwerty"))))

(deftest ^:integraatio hae-ehdoilla-tyhjat-ehdot
  (lisaa-koulutus-ja-opintoala! {:koulutusalakoodi "KA"}
                                {:opintoalakoodi "OA"})
  (lisaa-tutkinto! {:tutkintotunnus "T1"
                    :opintoala "OA"
                    :uusin_versio_id 1})
  (lisaa-tutkintoversio! {:tutkintoversio_id 1
                          :tutkintotunnus "T1"
                          :voimassa_alkupvm (time/local-date 1900 1 1)
                          :siirtymaajan_loppupvm (time/local-date 2199 1 1)})
  (lisaa-tutkinto! {:tutkintotunnus "T2"
                    :opintoala "OA"
                    :uusin_versio_id 2})
  (lisaa-tutkintoversio! {:tutkintoversio_id 2
                          :tutkintotunnus "T2"
                          :voimassa_alkupvm (time/local-date 1900 1 1)
                          :siirtymaajan_loppupvm (time/local-date 1901 1 1)})
  (is (= (set (map :tutkintotunnus (arkisto/hae-ehdoilla {})))
         #{"T1"})))

(deftest ^:integraatio hae-ehdoilla-voimassaolevat
  (lisaa-koulutus-ja-opintoala! {:koulutusalakoodi "KA"}
                                {:opintoalakoodi "OA"})
  (lisaa-tutkinto! {:tutkintotunnus "T1"
                    :opintoala "OA"
                    :uusin_versio_id 1})
  (lisaa-tutkintoversio! {:tutkintoversio_id 1
                          :tutkintotunnus "T1"
                          :voimassa_alkupvm (time/local-date 1900 1 1)
                          :siirtymaajan_loppupvm (time/local-date 2199 1 1)})
  (lisaa-tutkinto! {:tutkintotunnus "T2"
                    :opintoala "OA"
                    :uusin_versio_id 2})
  (lisaa-tutkintoversio! {:tutkintoversio_id 2
                          :tutkintotunnus "T2"
                          :voimassa_alkupvm (time/local-date 1900 1 1)
                          :siirtymaajan_loppupvm (time/local-date 1901 1 1)})
  (is (= (set (map :tutkintotunnus (arkisto/hae-ehdoilla {:voimassa "kaikki"})))
         #{"T1" "T2"})))

(deftest ^:integraatio hae-ehdoilla-nimi
  (lisaa-koulutus-ja-opintoala! {:koulutusalakoodi "KA"}
                                {:opintoalakoodi "OA"})
  (lisaa-tutkinto! {:tutkintotunnus "T1"
                    :nimi_fi "foo bar baz"
                    :opintoala "OA"
                    :uusin_versio_id 1})
  (lisaa-tutkintoversio! {:tutkintoversio_id 1
                          :tutkintotunnus "T1"})
  (lisaa-tutkinto! {:tutkintotunnus "T2"
                    :nimi_sv "FÅÅ BAR BÅZ"
                    :opintoala "OA"
                    :uusin_versio_id 2})
  (lisaa-tutkintoversio! {:tutkintoversio_id 2
                          :tutkintotunnus "T2"})
  (lisaa-tutkinto! {:tutkintotunnus "T3"
                    :opintoala "OA"
                    :uusin_versio_id 3})
  (lisaa-tutkintoversio! {:tutkintoversio_id 3
                          :tutkintotunnus "T3"})
  (is (= (set (map :tutkintotunnus (arkisto/hae-ehdoilla {:nimi "bar"})))
         #{"T1" "T2"})))
