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

(ns aitu.integraatio.sql.sql-henkilo-arkisto-test
  (:require [clojure.test :refer [deftest testing is are use-fixtures]]
            [clj-time.core :as time]
            [aitu.infra.henkilo-arkisto :as arkisto :refer :all]
            [aitu.toimiala.henkilo :refer :all]
            [korma.core :as sql]
            [aitu.integraatio.sql.test-util :refer [tietokanta-fixture]]
            [aitu.integraatio.sql.test-data-util :as data, :refer :all]))

(use-fixtures :each tietokanta-fixture)

(deftest ^:integraatio sql-crud-lisaa!
  "Testaa crud operaatiot tietokantaan. Aiheuttaa kannan tyhjennyksen tällä hetkellä."
  (let [arkisto-count (count (arkisto/hae-kaikki))]
    (arkisto/lisaa! data/default-henkilo)
    (is (= (count (arkisto/hae-kaikki)) (inc arkisto-count)))))

;; Vaatii fixturen takia tietokannan, joten merkitään integraatiotestiksi
(deftest ^:integraatio yhdista-henkilot-test
  (let [henkilot [{:henkiloid 1, :etunimi "eka"}
                  {:henkiloid 2, :etunimi "toka"}
                  {:henkiloid 3, :etunimi "kolmas"}]
        jasenyydet [{:henkiloid 1, :toimikunta 1}
                    {:henkiloid 1, :toimikunta 2}
                    {:henkiloid 2, :toimikunta 3}]
        yhdistetyt (yhdista-henkilot-ja-jasenyydet henkilot jasenyydet)]

    (testing "yhdista-henkilot"

      (testing "säilyttää kaikki henkilöt"
        (is (= (count yhdistetyt) 3)))

      (testing "säilyttää henkilöiden tiedot"
        (is (= (-> yhdistetyt (nth 0) :etunimi)
               "eka")))

      (testing "liittää oikeat toimikunnat henkilöihin"
        (is (= (-> yhdistetyt (nth 0) :jasenyydet)
               [{:henkiloid 1, :toimikunta 1}
                {:henkiloid 1, :toimikunta 2}]))
        (is (= (-> yhdistetyt (nth 1) :jasenyydet)
               [{:henkiloid 2, :toimikunta 3}]))
        (is (= (-> yhdistetyt (nth 2) :jasenyydet) []))))))

(deftest ^:integraatio olematon-henkilo-test
  (testing "hae-hlo-ja-ttk"
    (testing "palauttaa nil olemattomalla id:llä"
       (is (nil? (arkisto/hae-hlo-ja-ttk -999999))))))

(deftest ^:integraatio hae-nykyiset-test
  (testing "hae-nykyiset"
    (let [henkiloita-alussa (count (arkisto/hae-nykyiset))
          henkiloita-voimassa-alussa (count (arkisto/hae-nykyiset-voimassa))
          testihenkilo (arkisto/lisaa! data/default-henkilo)]
      (sql/exec-raw (str "insert into tutkintotoimikunta("
                         "tkunta,"
                         "nimi_fi,nimi_sv,"
                         "kielisyys,"
                         "toimikausi_id,"
                         "toimikausi_alku,"
                         "toimikausi_loppu"
                         ")values("
                         "'TKUN',"
                         "'nimi','nimi',"
                         "'fi',"
                         "2,"
                         "'2013-01-01',"
                         "'2016-01-01'"
                         ")"))
      (sql/exec-raw (str "insert into jasenyys("
                         "henkiloid,"
                         "toimikunta,"
                         "rooli,"
                         "edustus,"
                         "alkupvm,"
                         "loppupvm"
                         ")values("
                         (:henkiloid testihenkilo) ","
                         "'TKUN',"
                         "'sihteeri',"
                         "'itsenainen',"
                         "'2013-01-01',"
                         "'2015-10-01'"
                         ")"))
      (let [henkiloita (count (arkisto/hae-nykyiset))
            henkiloita-voimassa (count (arkisto/hae-nykyiset-voimassa))]
        (is (= henkiloita (+ henkiloita-alussa 1)))
        (is (= henkiloita-voimassa henkiloita-voimassa-alussa))))))

(deftest ^:integraatio hae-ehdoilla-tyhjat-ehdot
  (let [mennyt-toimikausi (lisaa-toimikausi! {:voimassa false
                                              :alkupvm (time/local-date 1900 1 1)
                                              :loppupvm (time/local-date 1902 12 31)})
        mennyt-toimikunta (lisaa-toimikunta! {:toimikausi_id (:toimikausi_id mennyt-toimikausi)})
        mennyt-henkilo (lisaa-henkilo! {:etunimi "mennyt"})
        _ (lisaa-jasen! {:toimikunta (:tkunta mennyt-toimikunta)
                         :henkiloid (:henkiloid mennyt-henkilo)})

        nykyinen-toimikausi (lisaa-toimikausi! {:voimassa true
                                                :alkupvm (time/local-date 1903 1 1)
                                                :loppupvm (time/local-date 2099 12 31)})
        nykyinen-toimikunta (lisaa-toimikunta! {:toimikausi_id (:toimikausi_id nykyinen-toimikausi)})
        nykyinen-henkilo (lisaa-henkilo! {:etunimi "nykyinen"})
        _ (lisaa-jasen! {:toimikunta (:tkunta nykyinen-toimikunta)
                         :henkiloid (:henkiloid nykyinen-henkilo)})

        ei-toimikuntaa (lisaa-henkilo! {:etunimi "ei toimikuntaa"})])
  (is (= (set (map :etunimi (arkisto/hae-ehdoilla {})))
         #{"mennyt" "nykyinen" "ei toimikuntaa"})))

(deftest ^:integraatio hae-ehdoilla-nykyinen-toimikausi
  (let [mennyt-toimikausi (lisaa-toimikausi! {:voimassa false
                                              :alkupvm (time/local-date 1900 1 1)
                                              :loppupvm (time/local-date 1902 12 31)})
        mennyt-toimikunta (lisaa-toimikunta! {:toimikausi_id (:toimikausi_id mennyt-toimikausi)})
        mennyt-henkilo (lisaa-henkilo! {:etunimi "mennyt"})
        _ (lisaa-jasen! {:toimikunta (:tkunta mennyt-toimikunta)
                         :henkiloid (:henkiloid mennyt-henkilo)})

        nykyinen-toimikausi (lisaa-toimikausi! {:voimassa true
                                                :alkupvm (time/local-date 1903 1 1)
                                                :loppupvm (time/local-date 2099 12 31)})
        nykyinen-toimikunta (lisaa-toimikunta! {:toimikausi_id (:toimikausi_id nykyinen-toimikausi)})
        nykyinen-henkilo (lisaa-henkilo! {:etunimi "nykyinen"})
        _ (lisaa-jasen! {:toimikunta (:tkunta nykyinen-toimikunta)
                         :henkiloid (:henkiloid nykyinen-henkilo)})

        ei-toimikuntaa (lisaa-henkilo! {:etunimi "ei toimikuntaa"})])
  (is (= (set (map :etunimi (arkisto/hae-ehdoilla {:toimikausi "nykyinen"})))
         #{"nykyinen"})))

(deftest ^:integraatio hae-ehdoilla-toimikunta
  (let [toimikausi (lisaa-toimikausi! {:voimassa true
                                       :alkupvm (time/local-date 1903 1 1)
                                       :loppupvm (time/local-date 2099 12 31)})

        toimikunta-1 (lisaa-toimikunta! {:toimikausi_id (:toimikausi_id toimikausi)
                                         :nimi_fi "foo bar baz"})
        henkilo-1 (lisaa-henkilo! {:etunimi "nimi1"})
        _ (lisaa-jasen! {:toimikunta (:tkunta toimikunta-1)
                         :henkiloid (:henkiloid henkilo-1)})

        toimikunta-2 (lisaa-toimikunta! {:toimikausi_id (:toimikausi_id toimikausi)
                                         :nimi_sv "FÅÅ BAR BAZ"})
        henkilo-2 (lisaa-henkilo! {:etunimi "nimi2"})
        _ (lisaa-jasen! {:toimikunta (:tkunta toimikunta-2)
                         :henkiloid (:henkiloid henkilo-2)})

        toimikunta-3 (lisaa-toimikunta! {:toimikausi_id (:toimikausi_id toimikausi)})
        henkilo-3 (lisaa-henkilo!)
        _ (lisaa-jasen! {:toimikunta (:tkunta toimikunta-3)
                         :henkiloid (:henkiloid henkilo-3)})

        ei-toimikuntaa (lisaa-henkilo!)])
  (is (= (set (map :etunimi (arkisto/hae-ehdoilla {:toimikunta "bar"})))
         #{"nimi1" "nimi2"})))

(deftest ^:integraatio hae-ehdoilla-nimi
  (lisaa-henkilo! {:henkiloid 1000
                   :etunimi "foo bar baz"})
  (lisaa-henkilo! {:henkiloid 1001
                   :sukunimi "FÅÅ BAR BAZ"})
  (lisaa-henkilo! {:henkiloid 1002})
  (is (= (set (map :henkiloid (arkisto/hae-ehdoilla {:nimi "bar"})))
         #{1000 1001})))

(deftest ^:integraatio hae-ehdoilla-monta-jasenyytta
  (let [toimikausi (lisaa-toimikausi! {:voimassa true
                                       :alkupvm (time/local-date 1903 1 1)
                                       :loppupvm (time/local-date 2099 12 31)})
        toimikunta-1 (lisaa-toimikunta! {:toimikausi_id (:toimikausi_id toimikausi)
                                         :nimi_fi "foo"})
        toimikunta-2 (lisaa-toimikunta! {:toimikausi_id (:toimikausi_id toimikausi)
                                         :nimi_fi "bar"})
        henkilo (lisaa-henkilo! {:etunimi "nimi1"})
        _ (lisaa-jasen! {:toimikunta (:tkunta toimikunta-1)
                         :henkiloid (:henkiloid henkilo)})
        _ (lisaa-jasen! {:toimikunta (:tkunta toimikunta-2)
                         :henkiloid (:henkiloid henkilo)})])
  (is (= (set (map :toimikunta_fi (arkisto/hae-ehdoilla {})))
         #{"foo" "bar"})))
