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

(ns aitu.infra.eraajo.eperusteet-test
  (:require [clojure.test :refer :all]
            [clj-time.core :as time]
            [cheshire.core :as cheshire]
            [aitu.integraatio.sql.test-util :refer [tietokanta-fixture]]
            [aitu.infra.eraajo.eperusteet :refer :all]
            [aitu.integraatio.sql.test-data-util :refer :all]
            aitu.integraatio.eperusteet
            aitu.infra.tutkinto-arkisto))

(use-fixtures :each tietokanta-fixture)

(defn lisaa-testidata! []
  (lisaa-koulutus-ja-opintoala! {:koulutusalakoodi "KA"}
                                {:opintoalakoodi "OA"})
  (lisaa-tutkinto! {:tutkintotunnus "123456"
                    :opintoala "OA"
                    :uusin_versio_id -1})
  (lisaa-tutkintoversio! {:tutkintoversio_id -2
                          :eperustetunnus 1
                          :tutkintotunnus "123456"
                          :peruste "1/042/2015"
                          :voimassa_alkupvm (time/local-date 2015 1 1)
                          :voimassa_loppupvm (time/local-date 2015 6 30)})
  (lisaa-tutkintoversio! {:tutkintoversio_id -1
                          :eperustetunnus 2
                          :tutkintotunnus "123456"
                          :peruste "2/042/2015"
                          :voimassa_alkupvm (time/local-date 2015 7 1)
                          :voimassa_loppupvm (time/local-date 2199 1 1)}))

(def tutkintotunnus "123456")

(def ensimmainen {:diaarinumero "1/042/2015"
                  :eperustetunnus 1
                  :voimassa_alkupvm (time/local-date 2015 1 1)
                  :voimassa_loppupvm (time/local-date 2015 7 31)
                  :siirtymaajan_loppupvm (time/local-date 2017 1 1)})

(def toinen-vanha {:diaarinumero "2/042/2015"
                   :eperustetunnus 2
                   :voimassa_alkupvm (time/local-date 2015 7 1)
                   :voimassa_loppupvm (time/local-date 2199 1 1)
                   :siirtymaajan_loppupvm (time/local-date 2199 1 1)})

(def toinen-uusi {:diaarinumero "2/042/2015"
                  :eperustetunnus 2
                  :voimassa_alkupvm (time/local-date 2015 8 1)
                  :voimassa_loppupvm (time/local-date 2015 8 31)
                  :siirtymaajan_loppupvm (time/local-date 2199 1 1)})

(def kolmas {:diaarinumero "3/042/2015"
             :eperustetunnus 3
             :voimassa_alkupvm (time/local-date 2015 9 1)
             :voimassa_loppupvm (time/local-date 2199 1 1)
             :siirtymaajan_loppupvm (time/local-date 2199 1 1)})

(deftest ^:integraatio muuttuneet-perusteet-test
  (lisaa-testidata!)
  (testing "muuttuneet-perusteet palauttaa listan muuttuneista ja uusista perusteista"
    (is (= (map :peruste (muuttuneet-perusteet [[tutkintotunnus ensimmainen]
                                                [tutkintotunnus toinen-vanha]
                                                [tutkintotunnus kolmas]]))
           [(:diaarinumero ensimmainen) (:diaarinumero kolmas)]))))


; eperusteet-515672.json on eperusteiden palauttama json-reply, 10.5. 2017
; https://eperusteet.opintopolku.fi/eperusteet-service/api/perusteet/515672/kaikki
(deftest ^:integraatio eperusteet-json-test
  (let [parsed-peruste
        (aitu.integraatio.eperusteet/muotoile-peruste 
          (cheshire/parse-string (slurp "test-resources/eperusteet-515672.json" :encoding "UTF-8") true))
    ikuisesti-pvm (time/local-date 2199 1 1)]
    (= ikuisesti-pvm (:voimassa_loppupvm parsed-peruste))
    (= ikuisesti-pvm (:siirtymaajan_loppupvm parsed-peruste))
    (= '("354403") (:tutkinnot parsed-peruste))))
    
    
(deftest ^:integraatio paivita-perusteet-test
  (lisaa-testidata!)
  (with-redefs [aitu.integraatio.eperusteet/hae-perusteet (constantly [[tutkintotunnus ensimmainen]
                                                                       [tutkintotunnus toinen-uusi]
                                                                       [tutkintotunnus kolmas]])
                aitu.infra.tutkinto-arkisto/hae-viimeisin-eperusteet-paivitys (constantly nil)
                aitu.infra.tutkinto-arkisto/tallenna-viimeisin-eperusteet-paivitys! (constantly nil)]
    (testing "paivita-perusteet lisää muutokset tietokantaan"
      (paivita-perusteet! nil)
      (testing "vanhan perusteen päivitys onnistuu"
        (let [tut (aitu.infra.tutkinto-arkisto/hae-tutkintoversio-perusteella tutkintotunnus (:diaarinumero ensimmainen))]
          (is (= (:voimassa_loppupvm tut)
                 (:voimassa_loppupvm ensimmainen)))
          (is (= (:siirtymaajan_loppupvm tut)
                 (:siirtymaajan_loppupvm ensimmainen)))))
      (testing "nykyisen perusteen päivitys onnistuu"
          (is (= (:voimassa_alkupvm (aitu.infra.tutkinto-arkisto/hae-tutkintoversio-perusteella tutkintotunnus (:diaarinumero toinen-uusi)))
                 (:voimassa_alkupvm toinen-uusi))))
      (testing "uuden perusteen lisääminen onnistuu"
        (is (= (:voimassa_alkupvm (aitu.infra.tutkinto-arkisto/hae-tutkintoversio-perusteella tutkintotunnus (:diaarinumero kolmas)))
               (:voimassa_alkupvm kolmas))))
      (testing "tietokantaan tulee vain yksi uusi peruste"
        (is (= (count (aitu.infra.tutkinto-arkisto/hae-tutkintoversiot tutkintotunnus))
               3))))))