
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
            [aitu.integraatio.sql.sql-koulutustoimija-arkisto-test :refer [kt-testidata!]]
            [aitu.integraatio.sql.test-util :refer [tietokanta-fixture]]
            [aitu.integraatio.sql.test-data-util :refer :all]
            [aitu.integraatio.sql.jarjestamissopimus-arkisto-test :as jarjestamissopimus-arkisto-test]))

(use-fixtures :each tietokanta-fixture)

(defn luo-testidata
  []
  (lisaa-koulutustoimija! {:ytunnus "KT1"})
  (lisaa-oppilaitos! {:koulutustoimija "KT1"
                      :oppilaitoskoodi "OP1"
                      :nimi "Testioppilaitoksen nimi"}))

(defn aseta-sopimuksen-toimikunta-vanhentuneeksi
  []
  (sql/exec-raw (str "update tutkintotoimikunta set toimikausi_loppu='2000-12-01' where tkunta='TKUN'")))

(deftest ^:integraatio hae-ehdoilla-nimi
  (kt-testidata!)
  (testing "nimellä haku ei välitä isoista ja pienistä kirjaimista"
    (is (= (set (map :oppilaitoskoodi (arkisto/hae-ehdoilla {:nimi "bar"})))
           #{"OL1" "OL2"}))))

(deftest ^:integraatio hae-ehdoilla-sopimuksia
  (lisaa-koulutus-ja-opintoala!)
  (let [kt (lisaa-koulutustoimija! {:ytunnus "KT1"})
        ol (lisaa-oppilaitos! {:koulutustoimija "KT1"
                               :nimi "Oppilaitos1"
                               :oppilaitoskoodi "OL1"})
        tv (lisaa-tutkinto-ja-versio! "111111")
        js (lisaa-jarjestamissopimus! kt ol)]
    (lisaa-tutkinto-sopimukselle! js tv)
    (lisaa-oppilaitos! {:koulutustoimija "KT1"
                        :nimi "Oppilaitos1"})
    (is (= (map :oppilaitoskoodi (arkisto/hae-ehdoilla {:sopimuksia "kylla"
                                                        :nimi "Oppilaitos1"}))
           ["OL1"]))))

(deftest ^:integraatio hae-ehdoilla-ei-sopimuksia
  (lisaa-koulutus-ja-opintoala!)
  (let [kt (lisaa-koulutustoimija! {:ytunnus "KT1"})
        ol (lisaa-oppilaitos! {:koulutustoimija "KT1"
                               :nimi "Oppilaitos1"})
        tv (lisaa-tutkinto-ja-versio! "111111")
        js (lisaa-jarjestamissopimus! kt ol)]
    (lisaa-tutkinto-sopimukselle! js tv)
    (lisaa-oppilaitos! {:koulutustoimija "KT1"
                        :nimi "Oppilaitos1"
                        :oppilaitoskoodi "OL1"})
    (is (empty? (map :oppilaitoskoodi (arkisto/hae-ehdoilla {:sopimuksia "ei"
                                                             :nimi "Oppilaitos1"}))))))

(deftest ^:integraatio hae-ehdoilla-tutkinto
  (lisaa-koulutus-ja-opintoala! {:koulutusalakoodi "KA2"}
                                {:opintoalakoodi "OA2"})
  (lisaa-tutkinto! {:opintoala "OA2"
                    :tutkintotunnus "T2"})
  (let [kt1 (lisaa-koulutustoimija! {:ytunnus "KT1"})
        o1 (lisaa-oppilaitos! {:koulutustoimija "KT1"
                               :oppilaitoskoodi "OL1"})
        sop1 (lisaa-jarjestamissopimus! kt1 o1)
        _ (lisaa-tutkinto-sopimukselle! sop1 -10000)

        kt2 (lisaa-koulutustoimija! {:ytunnus "KT2"})
        o2 (lisaa-oppilaitos! {:koulutustoimija "KT2"
                               :oppilaitoskoodi "OL2"})
        sop2 (lisaa-jarjestamissopimus! kt2 o2)
        tv2 (lisaa-tutkintoversio! {:tutkintotunnus "T2"})
        _ (lisaa-tutkinto-sopimukselle! sop2 (:tutkintoversio_id tv2))]
    (is (= (map :oppilaitoskoodi (arkisto/hae-ehdoilla {:tunnus "324601"}))
           ["OL1"]))))

(deftest ^:integraatio hae-ehdoilla-opintoala
  (lisaa-koulutus-ja-opintoala! {:koulutusalakoodi "KA2"}
                                {:opintoalakoodi "OA2"})
  (lisaa-tutkinto! {:opintoala "OA2"
                    :tutkintotunnus "T2"})
  (let [kt1 (lisaa-koulutustoimija! {:ytunnus "KT1"})
        o1 (lisaa-oppilaitos! {:koulutustoimija "KT1"
                               :oppilaitoskoodi "OL1"})
        sop1 (lisaa-jarjestamissopimus! kt1 o1)
        _ (lisaa-tutkinto-sopimukselle! sop1 -10000)

        kt2 (lisaa-koulutustoimija! {:ytunnus "KT2"})
        o2 (lisaa-oppilaitos! {:koulutustoimija "KT2"
                               :oppilaitoskoodi "OL2"})
        sop2 (lisaa-jarjestamissopimus! kt2 o2)
        tv2 (lisaa-tutkintoversio! {:tutkintotunnus "T2"})
        _ (lisaa-tutkinto-sopimukselle! sop2 (:tutkintoversio_id tv2))]
    (is (= (map :oppilaitoskoodi (arkisto/hae-ehdoilla {:tunnus "202"}))
           ["OL1"]))))

(deftest ^:integraatio hae-termilla-test
  (let [termi "Testioppilaitoksen nimi"
        osumia-alussa (count (arkisto/hae-termilla termi))
        _ (luo-testidata)
        osumia (count (arkisto/hae-termilla termi))]
    (testing "Oppilaitos löytyy haettaessa nimellä"
      (is (> osumia osumia-alussa)))))

(deftest ^:integraatio hae-oppilaitoskoodilla-test
  (lisaa-koulutustoimija! {:ytunnus "KT1"})
  (let [oppilaitos {:oppilaitoskoodi "12345"
                    :nimi "Oppilaitos O"
                    :koulutustoimija "KT1"}
        _ (arkisto/lisaa! oppilaitos)]
    (testing "Oppilaitos löytyy haettaessa oppilaitoskoodilla"
      (is (= (select-keys (arkisto/hae "12345") [:oppilaitoskoodi :nimi])
             {:oppilaitoskoodi "12345", :nimi "Oppilaitos O"})))))
