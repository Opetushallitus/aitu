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

(ns aitu.integraatio.sql.sql-oppilaitos-arkisto-test
  (:require [clojure.test :refer [deftest testing is use-fixtures]]
            [korma.core :as sql]
            [aitu.infra.oppilaitos-arkisto :as arkisto]
            [aitu.infra.jarjestamissopimus-arkisto :as jarjestamissopimus-arkisto]
            [aitu.integraatio.sql.test-util :refer [tietokanta-fixture]]
            [aitu.integraatio.sql.test-data-util :refer :all]
            [aitu.integraatio.sql.jarjestamissopimus-arkisto-test :as jarjestamissopimus-arkisto-test]
            [aitu.toimiala.oppilaitos :as oppilaitos]))

(use-fixtures :each tietokanta-fixture)

(defn luo-testidata
  []
  (sql/exec-raw (str "insert into oppilaitos("
                  "oppilaitoskoodi,"
                  "nimi"
                  ")values("
                  "'OP1',"
                  "'Testioppilaitoksen nimi'"
                  ")")))

(defn aseta-sopimuksen-toimikunta-vanhentuneeksi
  []
  (sql/exec-raw (str "update tutkintotoimikunta set toimikausi_loppu='2000-12-01' where tkunta='TKUN'")))

(defn hae-ja-taydenna
  [oppilaitoskoodi]
  (oppilaitos/taydenna-oppilaitos (arkisto/hae oppilaitoskoodi)))

(deftest ^:integraatio hae-ehdoilla-nimi
  (lisaa-koulutustoimija! {:ytunnus "KT1"})
  (lisaa-oppilaitos! {:koulutustoimija "KT1"
                      :oppilaitoskoodi "OL1"
                      :nimi "foo bar baz"})
  (lisaa-oppilaitos! {:koulutustoimija "KT1"
                      :oppilaitoskoodi "OL2"
                      :nimi "FÅÅ BAR BAZ"})
  (lisaa-oppilaitos! {:koulutustoimija "KT1"})
  (is (= (set (map :oppilaitoskoodi (arkisto/hae-ehdoilla {:nimi "bar"})))
         #{"OL1" "OL2"})))

(deftest ^:integraatio hae-ehdoilla-sopimuksia
  (let [kt (lisaa-koulutustoimija! {:ytunnus "KT1"})
        ol (lisaa-oppilaitos! {:koulutustoimija "KT1"
                               :nimi "Oppilaitos1"
                               :oppilaitoskoodi "OL1"})]
    (lisaa-jarjestamissopimus! kt ol)
    (lisaa-oppilaitos! {:koulutustoimija "KT1"
                        :nimi "Oppilaitos1"})
    (is (= (map :oppilaitoskoodi (arkisto/hae-ehdoilla {:sopimuksia "kylla"
                                                  :nimi "Oppilaitos1"}))
           ["OL1"]))))

(deftest ^:integraatio hae-ehdoilla-ei-sopimuksia
  (let [kt (lisaa-koulutustoimija! {:ytunnus "KT1"})
        ol (lisaa-oppilaitos! {:koulutustoimija "KT1"
                               :nimi "Oppilaitos1"})]
    (lisaa-jarjestamissopimus! kt ol)
    (lisaa-oppilaitos! {:koulutustoimija "KT1"
                        :nimi "Oppilaitos1"
                        :oppilaitoskoodi "OL1"})
    (is (= (map :oppilaitoskoodi (arkisto/hae-ehdoilla {:sopimuksia "ei"
                                                :nimi "Oppilaitos1"}))
           ["OL1"]))))

(deftest ^:integraatio hae-ehdoilla-tunnus
  (lisaa-koulutus-ja-opintoala! {:koulutusalakoodi "KA1"}
                                {:opintoalakoodi "OA1"})
  (lisaa-tutkinto! {:opintoala "OA1"
                    :tutkintotunnus "T1"})
  (lisaa-koulutus-ja-opintoala! {:koulutusalakoodi "KA2"}
                                {:opintoalakoodi "OA2"})
  (lisaa-tutkinto! {:opintoala "OA2"
                    :tutkintotunnus "T2"})
  (let [kt1 (lisaa-koulutustoimija! {:ytunnus "KT1"})
        o1 (lisaa-oppilaitos! {:koulutustoimija "KT1"
                               :oppilaitoskoodi "OL1"})
        sop1 (lisaa-jarjestamissopimus! kt1 o1)
        tv1 (lisaa-tutkintoversio! {:tutkintotunnus "T1"})

        kt2 (lisaa-koulutustoimija! {:ytunnus "KT2"})
        o2 (lisaa-oppilaitos! {:koulutustoimija "KT2"
                               :oppilaitoskoodi "OL2"})
        sop2 (lisaa-jarjestamissopimus! kt2 o2)
        tv2 (lisaa-tutkintoversio! {:tutkintotunnus "T2"})]
    (lisaa-tutkinto-sopimukselle! sop1 (:tutkintoversio_id tv1))
    (lisaa-tutkinto-sopimukselle! sop2 (:tutkintoversio_id tv2))

    (is (= (map :oppilaitoskoodi (arkisto/hae-ehdoilla {:tunnus "T1"}))
           ["OL1"]))
    (is (= (map :oppilaitoskoodi (arkisto/hae-ehdoilla {:tunnus "OA1"}))
           ["OL1"]))))

(deftest ^:integraatio hae-termilla-test
  (let [termi "Testioppilaitoksen nimi"
        osumia-alussa (count (arkisto/hae-termilla termi))
        _ (luo-testidata)
        osumia (count (arkisto/hae-termilla termi))]
    (testing "Oppilaitos löytyy haettaessa nimellä"
      (is (> osumia osumia-alussa)))))

(deftest ^:integraatio hae-oppilaitoskoodilla-test
  (let [oppilaitos {:oppilaitoskoodi "12345" :nimi "Oppilaitos O"}
        _ (arkisto/lisaa! oppilaitos)]
    (testing "Oppilaitos löytyy haettaessa oppilaitoskoodilla"
      ;; kun
      (let [haettu-oppilaitos (select-keys (hae-ja-taydenna "12345") [:oppilaitoskoodi :nimi])]
        ;; niin
        (is (= haettu-oppilaitos oppilaitos))))))

(deftest ^:integraatio hae-oppilaitos-test
  (jarjestamissopimus-arkisto-test/lisaa-testidata!)
  (let [jarjestamissopimusid (:jarjestamissopimusid (jarjestamissopimus-arkisto/lisaa! (jarjestamissopimus-arkisto-test/arbitrary-sopimus)))
        _ (jarjestamissopimus-arkisto/lisaa-tutkinnot-sopimukselle! jarjestamissopimusid [12345, 23456])]
    (testing "Oppilaitoksen järjestämissopimus on voimassa kun toimikunta, sopimus ja sopimuksen tutkinnot ovat voimassa"
      (let [oppilaitos (hae-ja-taydenna "OP1")]
        (is (-> oppilaitos :jarjestamissopimus first :voimassa))))
    (testing "Oppilaitoksen järjestämissopimus ei ole voimassa, jos toimikunta ei ole voimassa"
      (aseta-sopimuksen-toimikunta-vanhentuneeksi)
      (let [oppilaitos (hae-ja-taydenna "OP1")]
        (is (false? (-> oppilaitos :jarjestamissopimus first :voimassa)))))))
