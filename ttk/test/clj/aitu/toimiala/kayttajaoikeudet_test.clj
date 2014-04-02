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

(ns aitu.toimiala.kayttajaoikeudet-test
  (:require [clojure.test :refer :all]
            [clojure.set :refer [union intersection]]
            [aitu.toimiala.kayttajaoikeudet :refer :all]))

(defn saako-tehda?
  "Saako käyttäjä tehdä annetun toiminnon. Toiminnon kohde voi vaikuttaa kontekstisensitiivisiin oikeuksiin ellei käyttäjä ole ylläpitäjä-roolissa"
  [kayttaja-map toiminto kohdeid]
  {:pre [(contains? toiminnot toiminto)
         (contains? #{yllapitajarooli kayttajarooli} (:roolitunnus kayttaja-map))]}
  (binding [*current-user-authmap* kayttaja-map]
    (let [auth-fn (get toiminnot toiminto)]
      (if (nil? kohdeid)
        ((eval auth-fn))
        ((eval auth-fn) kohdeid)))))

(def kayttaja-map
  {:oid "foo123"
   :henkiloid "henkiloid123"
   :roolitunnus kayttajarooli
   :toimikunta_jasen #{"123"}})

(deftest vain-lueteltu-oikeus-kelpaa []
   (is (thrown? Throwable
          (saako-tehda? kayttaja-map :trolol-laulanta nil))))

(deftest omien-tietojen-paivitys-kay []
  (is (= true (saako-tehda? kayttaja-map :henkilo_paivitys "henkiloid123"))))

(deftest toisen-tietojen-paivitys-ei-kay[]
  (is (= false (saako-tehda? kayttaja-map :henkilo_paivitys "adsdas"))))

(deftest oman-toimikunnan-tietojen-paivitys-kay []
  (is (= true (saako-tehda? kayttaja-map :toimikunta_paivitys "123"))))

(deftest toisen-toimikunnan-tietojen-paivitys-ei-kay []
  (is (= false (saako-tehda? kayttaja-map :toimikunta_paivitys "asdds"))))

(deftest kayttaja-ei-saa-tehda-yllapitotoimintoja []
  (is (thrown? Throwable
    (saako-tehda? kayttaja-map :toimikunta_luonti "asdds"))))

(deftest yllapitaja-saa-tehda-kaikki-kayttajatoiminnot []
  (let [yllapitaja {:oid "l" :roolitunnus yllapitajarooli}]
    (doseq [oikeus (keys kayttajatoiminnot)]
      (is (= true (saako-tehda? yllapitaja oikeus "fooid"))))))

(deftest ei-ole-paallekkaisia-oikeus-tunnisteita []
  (is (empty? (intersection (set (keys kayttajatoiminnot)) (set (keys yllapitotoiminnot))))))

