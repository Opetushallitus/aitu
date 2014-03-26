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

(ns aitu.infra.kayttaja-arkisto-test
  (:require [clojure.test :refer :all]
            [korma.core :as sql]
            [aitu.integraatio.sql.test-util :refer :all]
            [aitu.infra.kayttaja-arkisto :refer :all]
            [aitu.integraatio.sql.korma :as taulut]
            [oph.korma.korma-auth :refer [*current-user-oid* integraatiokayttaja]]
            [aitu.toimiala.kayttajaoikeudet
             :refer [yllapitajarooli kayttajarooli]]))

(defn kayttajat [& kt]
  (map merge
       (for [i (iterate inc 0)]
         {:etunimi (str "etu" i)
          :sukunimi (str "suku" i)
          :rooli kayttajarooli})
       kt))

(defn on-olemassa-kayttajat [& kt]
  (sql/insert taulut/kayttaja
    (sql/values (apply kayttajat kt))))

(defn voimassaolevat-kayttajat []
  (sql/select taulut/kayttaja
              (sql/where {:voimassa true})
              (sql/where {:luotu_kayttaja [= @*current-user-oid*]})))

(deftest ^:integraatio voimassaolo-lakkaa-test
  (testing "Puuttuvat käyttäjät merkitään ei-voimassaoleviksi"
    (testidata-poistaen-kayttajana integraatiokayttaja
      ;; Oletetaan, että
      (on-olemassa-kayttajat {:oid "jaavoimaan"
                              :voimassa true}
                             {:oid "voimassaololakkaa"
                              :voimassa true})
      ;; Kun
      (paivita! (kayttajat {:oid "jaavoimaan"}))
      ;; Niin
      (is (= (map :oid (voimassaolevat-kayttajat)) ["jaavoimaan"])))))

(deftest ^:integraatio voimassaolo-palaa-test
  (testing "Ei-voimassaolevat käyttäjät palautetaan voimassaoleviksi"
    (testidata-poistaen-kayttajana integraatiokayttaja
      ;; Oletetaan, että
      (on-olemassa-kayttajat {:oid "voimassaolopalaa"
                              :voimassa false}
                             {:oid "jaapoisvoimasta"
                              :voimassa false})
      ;; Kun
      (paivita! (kayttajat {:oid "voimassaolopalaa"}))
      ;; Niin
      (is (= (map :oid (voimassaolevat-kayttajat)) ["voimassaolopalaa"])))))

(deftest ^:integraatio paivitys-test
  (testing "Vanhojen käyttäjien tiedot päivitetään"
    (testidata-poistaen-kayttajana integraatiokayttaja
      ;; Oletetaan, että
      (on-olemassa-kayttajat {:oid "oid"
                              :etunimi "Wanha"
                              :sukunimi "Käyttäjä"})
      ;; Kun
      (paivita! (kayttajat {:oid "oid"
                            :etunimi "Lol"
                            :sukunimi "Noob"}))
      ;; Niin
      (is (= (map #(select-keys % [:oid :etunimi :sukunimi])
                  (voimassaolevat-kayttajat))
             [{:oid "oid"
               :etunimi "Lol"
               :sukunimi "Noob"}])))))

(deftest ^:integraatio paivitys-samalla-oidilla-test
  (testing "Jos sama käyttäjä annetaan useasti, viimeisimmät tiedot jäävät voimaan."
    (testidata-poistaen-kayttajana integraatiokayttaja
      ;; Oletetaan, että
      (on-olemassa-kayttajat {:oid "oid"
                              :etunimi "Wanha"
                              :sukunimi "Käyttäjä"})
      ;; Kun
      (paivita! (kayttajat {:oid "oid"
                            :etunimi "Lol"
                            :sukunimi "Noob"}
                           {:oid "oid"
                            :etunimi "Spurdo"
                            :sukunimi "Spärde"}))
      ;; Niin
      (is (= (map #(select-keys % [:oid :etunimi :sukunimi])
                  (voimassaolevat-kayttajat))
             [{:oid "oid"
               :etunimi "Spurdo"
               :sukunimi "Spärde"}])))))

(deftest ^:integraatio lisays-test
  (testing "Uudet käyttäjät lisätään"
    (testidata-poistaen-kayttajana integraatiokayttaja
      ;; Kun
      (paivita! (kayttajat {:oid "oid"
                            :etunimi "Lol"
                            :sukunimi "Noob"}))
      ;; Niin
      (is (= (map #(select-keys % [:oid :etunimi :sukunimi])
                  (voimassaolevat-kayttajat))
             [{:oid "oid"
               :etunimi "Lol"
               :sukunimi "Noob"}])))))

(deftest ^:integraatio lisays-samalla-oidilla-test
  (testing "Jos sama käyttäjä lisätään useasti, viimeisimmät tiedot jäävät voimaan."
    (testidata-poistaen-kayttajana integraatiokayttaja
      ;; Kun
      (paivita! (kayttajat {:oid "oid"
                            :etunimi "Lol"
                            :sukunimi "Noob"}
                           {:oid "oid"
                            :etunimi "Spurdo"
                            :sukunimi "Spärde"}))
      ;; Niin
      (is (= (map #(select-keys % [:oid :etunimi :sukunimi])
                  (voimassaolevat-kayttajat))
             [{:oid "oid"
               :etunimi "Spurdo"
               :sukunimi "Spärde"}])))))
