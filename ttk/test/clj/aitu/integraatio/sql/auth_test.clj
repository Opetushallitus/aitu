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

(ns aitu.integraatio.sql.auth-test
  (:require [clojure.test :refer :all]
            [aitu.infra.henkilo-arkisto :as henkilo-arkisto :refer :all]
            [aitu.toimiala.henkilo :refer :all]
            [oph.korma.korma-auth :as auth]
            [oph.korma.korma-auth :as ka]
            [aitu.integraatio.sql.test-data-util :as data]
            [oph.common.infra.i18n :as i18n]
            [aitu.integraatio.sql.test-util :refer [tietokanta-fixture testikayttaja-oid tietokanta-fixture-oid testi-locale alusta-korma!]]
            [aitu.toimiala.kayttajaoikeudet :refer [*current-user-authmap*]]
            [aitu.toimiala.kayttajaroolit :refer [kayttajaroolit]]))

(deftest ^:integraatio auth-user-set!
  "testaa että authorization systeemi audit-trailia varten toimii"
  []
  (tietokanta-fixture
    #(let [henkilo-id (:henkiloid (henkilo-arkisto/lisaa! data/default-henkilo))
           henkilo (henkilo-arkisto/hae henkilo-id)]
      (is (= testikayttaja-oid (:muutettu_kayttaja henkilo)))
      (is (= testikayttaja-oid (:luotu_kayttaja henkilo))))))

(defn tietokanta-oper
  "Annettu käyttäjätunnus sidotaan Kormalle testifunktion ajaksi."
  [f kayttaja]
  (alusta-korma!)
  (binding [ka/*current-user-uid* (:uid kayttaja) ; testin aikana eri käyttäjä
            ka/*current-user-oid* (promise)
            i18n/*locale* testi-locale
            *current-user-authmap* kayttaja]
    (deliver ka/*current-user-oid* (:oid kayttaja))
    (f)))

(deftest ^:integraatio puuttuva-kayttaja-ei-kelpaa!
  "Testaa että kannasta puuttuvalla käyttäjätunnuksella ei voi avata kantayhteyksiä."
  []
  (let [puuttuva-kayttaja {:oid "AKUANKKA"}
        olemassaoleva-kayttaja {:roolitunnus (:yllapitaja kayttajaroolit), :oid auth/default-test-user-oid, :uid auth/default-test-user-uid }
        arbitrary-sql-read henkilo-arkisto/hae-kaikki]
    (is (thrown? Throwable
                 (tietokanta-oper
                   arbitrary-sql-read puuttuva-kayttaja)))
    ; mutta toimii olemassaolevalla käyttäjätunnuksella
    (tietokanta-oper
      arbitrary-sql-read olemassaoleva-kayttaja)))

(deftest ^:integraatio lakannut-kayttaja-ei-kelpaa!
  "Testaa että lakkautetulla käyttäjätunnuksella ei voi avata kantayhteyksiä."
  []
  (let [lakannut-kayttajatunnus "KONVERSIO"]
    (is (thrown? Throwable
                 (tietokanta-fixture-oid
                   henkilo-arkisto/hae-kaikki lakannut-kayttajatunnus)))))

