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

(ns aitu.integraatio.sql.sql-koulutustoimija-arkisto-test
  (:require [clojure.test :refer [deftest testing is use-fixtures]]
            [korma.core :as sql]
            [aitu.infra.koulutustoimija-arkisto :as arkisto]
            [aitu.infra.jarjestamissopimus-arkisto :as jarjestamissopimus-arkisto]
            [aitu.integraatio.sql.test-util :refer [tietokanta-fixture]]
            [aitu.integraatio.sql.test-data-util :refer :all]
            [aitu.integraatio.sql.jarjestamissopimus-arkisto-test :as jarjestamissopimus-arkisto-test]
            [aitu.toimiala.koulutustoimija :as koulutustoimija]))

(use-fixtures :each tietokanta-fixture)

(defn luo-testidata
  []
  (sql/exec-raw (str "insert into koulutustoimija("
                  "ytunnus,"
                  "nimi_fi"
                  ")values("
                  "'0000000-0',"
                  "'Koulutustoimijan nimi'"
                  ")")))

(defn aseta-sopimuksen-toimikunta-vanhentuneeksi
  []
  (sql/exec-raw (str "update tutkintotoimikunta set toimikausi_loppu='2000-12-01' where tkunta='TKUN'")))

(defn hae-ja-taydenna
  [y-tunnus]
  (koulutustoimija/taydenna-koulutustoimija (arkisto/hae y-tunnus)))

(deftest ^:integraatio hae-ehdoilla-nimi
  (lisaa-koulutustoimija! {:ytunnus "KT1"
                           :nimi_fi "foo bar baz"})
  (lisaa-koulutustoimija! {:ytunnus "KT2"
                           :nimi_sv "FÅÅ BAR BAZ"})
  (lisaa-koulutustoimija!)
  (is (= (set (map :ytunnus (arkisto/hae-ehdoilla {:nimi "bar"})))
         #{"KT1" "KT2"})))

(deftest ^:integraatio hae-ehdoilla-sopimuksia
  (lisaa-koulutustoimija! {:nimi_fi "KT"})
  (let [kt (lisaa-koulutustoimija! {:ytunnus "KT1"
                                    :nimi_fi "KT"})
        ol (lisaa-oppilaitos! {:koulutustoimija "KT1"})]
    (lisaa-jarjestamissopimus! kt ol)
    (is (= (map :ytunnus (arkisto/hae-ehdoilla {:sopimuksia "kylla"
                                                :nimi "KT"}))
           ["KT1"]))))

(deftest ^:integraatio hae-ehdoilla-ei-sopimuksia
  (lisaa-koulutustoimija! {:ytunnus "KT2"
                           :nimi_fi "KT"})
  (let [kt (lisaa-koulutustoimija! {:ytunnus "KT1"
                                    :nimi_fi "KT"})
        ol (lisaa-oppilaitos! {:koulutustoimija "KT1"})]
    (lisaa-jarjestamissopimus! kt ol)
    (is (= (map :ytunnus (arkisto/hae-ehdoilla {:sopimuksia "ei"
                                                :nimi "KT"}))
           ["KT2"]))))

(deftest ^:integraatio hae-ehdoilla-tutkinto
  (lisaa-koulutus-ja-opintoala! {:koulutusalakoodi "KA1"}
                                {:opintoalakoodi "OA1"})
  (lisaa-tutkinto! {:opintoala "OA1"
                    :tutkintotunnus "T1"})
  (lisaa-koulutus-ja-opintoala! {:koulutusalakoodi "KA2"}
                                {:opintoalakoodi "OA2"})
  (lisaa-tutkinto! {:opintoala "OA2"
                    :tutkintotunnus "T2"})
  (let [kt1 (lisaa-koulutustoimija! {:ytunnus "KT1"})
        o1 (lisaa-oppilaitos! {:koulutustoimija "KT1"})
        sop1 (lisaa-jarjestamissopimus! kt1 o1)
        tv1 (lisaa-tutkintoversio! {:tutkintotunnus "T1"})
        _ (lisaa-tutkinto-sopimukselle! sop1 (:tutkintoversio_id tv1))

        kt2 (lisaa-koulutustoimija! {:ytunnus "KT2"})
        o2 (lisaa-oppilaitos! {:koulutustoimija "KT2"})
        sop2 (lisaa-jarjestamissopimus! kt2 o2)
        tv2 (lisaa-tutkintoversio! {:tutkintotunnus "T2"})
        _ (lisaa-tutkinto-sopimukselle! sop2 (:tutkintoversio_id tv2))]
    (is (= (map :ytunnus (arkisto/hae-ehdoilla {:tunnus "T1"}))
           ["KT1"]))))

(deftest ^:integraatio hae-ehdoilla-opintoala
  (lisaa-koulutus-ja-opintoala! {:koulutusalakoodi "KA1"}
                                {:opintoalakoodi "OA1"})
  (lisaa-tutkinto! {:opintoala "OA1"
                    :tutkintotunnus "T1"})
  (lisaa-koulutus-ja-opintoala! {:koulutusalakoodi "KA2"}
                                {:opintoalakoodi "OA2"})
  (lisaa-tutkinto! {:opintoala "OA2"
                    :tutkintotunnus "T2"})
  (let [kt1 (lisaa-koulutustoimija! {:ytunnus "KT1"})
        o1 (lisaa-oppilaitos! {:koulutustoimija "KT1"})
        sop1 (lisaa-jarjestamissopimus! kt1 o1)
        tv1 (lisaa-tutkintoversio! {:tutkintotunnus "T1"})
        _ (lisaa-tutkinto-sopimukselle! sop1 (:tutkintoversio_id tv1))

        kt2 (lisaa-koulutustoimija! {:ytunnus "KT2"})
        o2 (lisaa-oppilaitos! {:koulutustoimija "KT2"})
        sop2 (lisaa-jarjestamissopimus! kt2 o2)
        tv2 (lisaa-tutkintoversio! {:tutkintotunnus "T2"})
        _ (lisaa-tutkinto-sopimukselle! sop2 (:tutkintoversio_id tv2))]
    (is (= (map :ytunnus (arkisto/hae-ehdoilla {:tunnus "OA1"}))
           ["KT1"]))))

(deftest ^:integraatio hae-termilla-test
  (let [termi "Koulutustoimijan nimi"
        osumia-alussa (count (arkisto/hae-termilla termi))
        _ (luo-testidata)
        osumia (count (arkisto/hae-termilla termi))]
    (testing "Koulutustoimija löytyy haettaessa nimellä"
      (is (> osumia osumia-alussa)))))

(deftest ^:integraatio hae-y-tunnuksella-test
  (let [koulutustoimija {:ytunnus "1111111-1" :nimi_fi "Koulutustoimija K"}]
    (testing "Koulutustoimija löytyy y-tunnuksella hakiessa"
      ;; kun
      (arkisto/lisaa! koulutustoimija)
      (let [haettu-koulutustoimija (select-keys (hae-ja-taydenna "1111111-1") [:ytunnus :nimi_fi])]
        ;; niin
        (is (= koulutustoimija haettu-koulutustoimija))))))

(deftest ^:integraatio hae-oppilaitos-test
  (jarjestamissopimus-arkisto-test/lisaa-testidata!)
  (let [jarjestamissopimusid (:jarjestamissopimusid (jarjestamissopimus-arkisto/lisaa! (jarjestamissopimus-arkisto-test/arbitrary-sopimus)))]
    (jarjestamissopimus-arkisto/lisaa-tutkinnot-sopimukselle! jarjestamissopimusid [12345 23456])
    (testing "Koulutustoimijan järjestämissopimus on voimassa kun toimikunta, sopimus ja sopimuksen tutkinnot ovat voimassa"
      (let [koulutustoimija (hae-ja-taydenna "KT1")]
        (is (-> koulutustoimija :jarjestamissopimus first :voimassa))))
    (testing "Koulutustoimijan järjestämissopimus ei ole voimassa, jos toimikunta ei ole voimassa"
      (aseta-sopimuksen-toimikunta-vanhentuneeksi)
      (let [koulutustoimija (hae-ja-taydenna "KT1")]
        (is (false? (-> koulutustoimija :jarjestamissopimus first :voimassa)))))))
