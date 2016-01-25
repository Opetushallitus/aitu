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

(ns aitu.integraatio.sql.sql-kayttajaoikeudet-arkisto-test
  (:require [clojure.test :refer [deftest testing is are use-fixtures]]
            [aitu.infra.kayttajaoikeudet-arkisto :as arkisto :refer :all]
            [aitu.toimiala.kayttajaoikeudet :as ko :refer :all]
            [aitu.toimiala.kayttajaroolit :refer [kayttajaroolit]]
            [aitu.test-timeutil :refer [vuoden-kuluttua]]
            [aitu.integraatio.sql.test-data-util :refer :all]
            [korma.core :as sql]
            [aitu.integraatio.sql.test-util :refer [tietokanta-fixture testikayttaja-oid]]))

(use-fixtures :each tietokanta-fixture)

(deftest ^:integraatio oikeuksien-haku-toimii-yllapitajalle-joka-ei-ole-jasen!
  (let [jasenyys-map (arkisto/hae-jasenyys-ja-sopimukset testikayttaja-oid)
        oikeus-map (arkisto/hae-oikeudet testikayttaja-oid)]
    (is (empty? jasenyys-map))
    (is (= (:oid oikeus-map) testikayttaja-oid))))

(defn yhdista-henkilo-ja-kayttaja!
  [oid henkiloid]
  (sql/exec-raw (str "update henkilo set kayttaja_oid = '" oid "' where henkiloid=" henkiloid)))

(deftest ^:integraatio hae-jasenyys-toimii-yhdelle-jasenyydelle!
  (lisaa-toimikunta! {:diaarinumero "2013/01/001" :tkunta "T12345"})
  (lisaa-henkilo! {:henkiloid -1234})
  (let [jasen (lisaa-jasen! {:henkiloid -1234 :toimikunta "T12345" :loppupvm (vuoden-kuluttua)})]
    (yhdista-henkilo-ja-kayttaja! testikayttaja-oid -1234)
    (let [jasenyys-map (arkisto/hae-jasenyys-ja-sopimukset testikayttaja-oid)
          testi-jasenyys (first (:jasenyys jasenyys-map))]
      (println jasenyys-map)
      (is (= (:tkunta testi-jasenyys) "T12345"))
      (is (= (:henkiloid jasenyys-map) -1234)))))

(deftest ^:integraatio hae-jasenyys-toimii-kahdelle-jasenyydelle!
  (lisaa-toimikunta! {:diaarinumero "2013/01/001" :tkunta "T12345"})
  (lisaa-toimikunta! {:diaarinumero "2013/01/002" :tkunta "T11111" :tilikoodi "9876"})
  (lisaa-henkilo! {:henkiloid -1234})
  (let [jasen (lisaa-jasen! {:henkiloid -1234 :toimikunta "T12345" :loppupvm (vuoden-kuluttua)})
        jasen-2 (lisaa-jasen! {:henkiloid -1234 :toimikunta "T11111" :loppupvm (vuoden-kuluttua)})]
    (yhdista-henkilo-ja-kayttaja! testikayttaja-oid -1234)
    (let [jasenyys-map (arkisto/hae-jasenyys-ja-sopimukset testikayttaja-oid)
          jasen-toimikunnat (set (filter #(not (nil? %)) (map :tkunta (:jasenyys jasenyys-map))))]
      (is (= jasen-toimikunnat (set ["T12345" "T11111"]))))))


(deftest ^:integraatio kayttajaoikeudet-jasenesitys-test
  (binding [*current-user-authmap* 
            {:oid "OID.T-9999"
             :henkiloid "-1000"
             :jarjesto -1
             :roolitunnus (:jarjesto kayttajaroolit)}]
    (let [oikeudet (arkisto/hae-oikeudet)]
      (is (= #{{:henkiloid -1000} {:henkiloid -1001}}
            (set (hae-muokattavat-jasenesitys)))))))
 

