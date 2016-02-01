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
  (testing "Testaa crud operaatiot tietokantaan. Aiheuttaa kannan tyhjennyksen tällä hetkellä."
    (let [arkisto-count (count (arkisto/hae-kaikki))]
      (arkisto/lisaa! data/default-henkilo)
      (is (= (count (arkisto/hae-kaikki)) (inc arkisto-count))))))

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
       (is (nil? (arkisto/hae-hlo-ja-ttk -999999 nil))))))

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
  (let [mennyt-toimikunta (lisaa-toimikunta-vanhalle-kaudelle!)
        mennyt-henkilo (lisaa-henkilo! {:etunimi "mennyt"})
        _ (lisaa-jasen! {:toimikunta (:tkunta mennyt-toimikunta)
                         :henkiloid (:henkiloid mennyt-henkilo)})

        nykyinen-toimikunta (lisaa-toimikunta-voimassaolevalle-kaudelle!)
        nykyinen-henkilo (lisaa-henkilo! {:etunimi "nykyinen"})
        _ (lisaa-jasen! {:toimikunta (:tkunta nykyinen-toimikunta)
                         :henkiloid (:henkiloid nykyinen-henkilo)})

        ei-toimikuntaa (lisaa-henkilo! {:etunimi "ei toimikuntaa"})]
    (is
      (empty? (clojure.set/difference #{"mennyt" "nykyinen" "ei toimikuntaa"} (set (map :etunimi (arkisto/hae-ehdoilla {})))))
      "Lisätyt henkilöt löytyvät")))

(deftest ^:integraatio hae-ehdoilla-nykyinen-toimikausi
  (let [mennyt-toimikunta  (lisaa-toimikunta-vanhalle-kaudelle! {:tkunta "TK1"})
        mennyt-henkilo (lisaa-henkilo! {:etunimi "mennyt"})
        _ (lisaa-jasen! {:toimikunta "TK1"
                         :henkiloid (:henkiloid mennyt-henkilo)})

        nykyinen-toimikunta (lisaa-toimikunta-voimassaolevalle-kaudelle! {:tkunta "TK2"})
        nykyinen-henkilo (lisaa-henkilo! {:etunimi "nykyinen"})
        _ (lisaa-jasen! {:toimikunta "TK2"
                         :henkiloid (:henkiloid nykyinen-henkilo)})

        ei-toimikuntaa (lisaa-henkilo! {:etunimi "ei toimikuntaa"})]
    (is (= (set (map :etunimi (arkisto/hae-ehdoilla {:toimikausi "nykyinen"})))
           #{"nykyinen"}))))

(deftest ^:integraatio hae-ehdoilla-toimikunta
  (let [toimikunta-1 (lisaa-toimikunta-voimassaolevalle-kaudelle! {:nimi_fi "foo bar baz"})
        henkilo-1 (lisaa-henkilo! {:etunimi "nimi1"})
        _ (lisaa-jasen! {:toimikunta (:tkunta toimikunta-1)
                         :henkiloid (:henkiloid henkilo-1)})

        toimikunta-2 (lisaa-toimikunta-voimassaolevalle-kaudelle! {:nimi_sv "FÅÅ BAR BAZ"})
        henkilo-2 (lisaa-henkilo! {:etunimi "nimi2"})
        _ (lisaa-jasen! {:toimikunta (:tkunta toimikunta-2)
                         :henkiloid (:henkiloid henkilo-2)})

        toimikunta-3 (lisaa-toimikunta-voimassaolevalle-kaudelle!)
        henkilo-3 (lisaa-henkilo!)
        _ (lisaa-jasen! {:toimikunta (:tkunta toimikunta-3)
                         :henkiloid (:henkiloid henkilo-3)})

        ei-toimikuntaa (lisaa-henkilo!)]
    (is (= (set (map :etunimi (arkisto/hae-ehdoilla {:toimikunta "bar"})))
           #{"nimi1" "nimi2"}))))

(deftest ^:integraatio hae-ehdoilla-nimi
  (lisaa-henkilo! {:henkiloid 1000
                   :etunimi "foo bar baz"})
  (lisaa-henkilo! {:henkiloid 1001
                   :sukunimi "FÅÅ BAR BAZ"})
  (lisaa-henkilo! {:henkiloid 1002})
  (is (= (set (map :henkiloid (arkisto/hae-ehdoilla {:nimi "bar"})))
         #{1000 1001})))

(deftest ^:integraatio hae-ehdoilla-monta-jasenyytta
  (let [toimikunta-1 (lisaa-toimikunta-voimassaolevalle-kaudelle! {:nimi_fi "foo"})
        toimikunta-2 (lisaa-toimikunta-voimassaolevalle-kaudelle! {:nimi_fi "bar"})
        henkilo (lisaa-henkilo! {:etunimi "nimi1"})
        _ (lisaa-jasen! {:toimikunta (:tkunta toimikunta-1)
                         :henkiloid (:henkiloid henkilo)})
        _ (lisaa-jasen! {:toimikunta (:tkunta toimikunta-2)
                         :henkiloid (:henkiloid henkilo)})]
    (is
      (empty? (clojure.set/difference #{"foo" "bar"} (set (map :toimikunta_fi (arkisto/hae-ehdoilla {})))))
      "Lisätyt jäsenyydet löytyvät")))

; lein test :only aitu.integraatio.sql.sql-henkilo-arkisto-test/hae-esitetty-henkilo
(deftest ^:integraatio hae-esitetty-henkilo
  (let [toimikunta-1 (lisaa-toimikunta-voimassaolevalle-kaudelle! {:nimi_fi "foo"})]
    (lisaa-henkilo! {:henkiloid 1000
                     :etunimi "foo bar baz"
                     :jarjesto -1})
    (is (= #{-1000 -1001 1000} (set (map :henkiloid (arkisto/hae-jarjeston-esitetyt-henkilot -1)))))
    (lisaa-jasen! {:toimikunta (:tkunta toimikunta-1)
                   :henkiloid 1000})
    (is (= #{-1000 -1001} (set (map :henkiloid (arkisto/hae-jarjeston-esitetyt-henkilot -1)))))))

(deftest ^:integraatio hae-esitetty-henkilo-jasenjarjestosta
  (let [toimikunta-1 (lisaa-toimikunta-voimassaolevalle-kaudelle! {:nimi_fi "foo"})]
    (lisaa-henkilo! {:henkiloid 1000
                     :etunimi "foo bar baz"
                     :jarjesto -2})
    (is (= #{-1000 -1001 1000} (set (map :henkiloid (arkisto/hae-jarjeston-esitetyt-henkilot -1)))))
    (is (= #{-1001 1000} (set (map :henkiloid (arkisto/hae-jarjeston-esitetyt-henkilot -2)))))
    (lisaa-jasen! {:toimikunta (:tkunta toimikunta-1)
                   :henkiloid 1000})
    (is (= #{-1000 -1001} (set (map :henkiloid (arkisto/hae-jarjeston-esitetyt-henkilot -1)))))
    (is (= #{-1001} (set (map :henkiloid (arkisto/hae-jarjeston-esitetyt-henkilot -2)))))))