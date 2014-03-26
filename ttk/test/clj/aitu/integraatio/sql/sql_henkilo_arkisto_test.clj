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
            [aitu.infra.henkilo-arkisto :as arkisto :refer :all]
            [aitu.toimiala.henkilo :refer :all]
            [korma.core :as sql]
            [aitu.integraatio.sql.test-util :refer [tietokanta-fixture]]
            [aitu.integraatio.sql.test-data-util :as data]))

(deftest ^:integraatio sql-crud-lisaa!
  "Testaa crud operaatiot tietokantaan. Aiheuttaa kannan tyhjennyksen tällä hetkellä."
  (tietokanta-fixture
    (fn []
      (let [arkisto-count (count (arkisto/hae-kaikki))]
        (arkisto/lisaa! data/default-henkilo)
        (is (= (count (arkisto/hae-kaikki)) (inc arkisto-count)))))))

(deftest yhdista-henkilot-test
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
  (tietokanta-fixture
    (fn []
      (testing "hae-hlo-ja-ttk"
        (testing "palauttaa nil olemattomalla id:llä"
           (is (nil? (arkisto/hae-hlo-ja-ttk -999999))))))))

(deftest ^:integraatio hae-nykyiset-test
  (tietokanta-fixture
    (fn []
      (testing "hae-nykyiset"
        (let [henkiloita-alussa (count (arkisto/hae-nykyiset))
              testihenkilo (arkisto/lisaa! data/default-henkilo)]
          (sql/exec-raw (str "insert into tutkintotoimikunta("
                          "tkunta,"
                          "nimi_fi,"
                          "kielisyys,"
                          "toimikausi_id,"
                          "toimikausi_alku,"
                          "toimikausi_loppu"
                          ")values("
                          "'TKUN',"
                          "'nimi',"
                          "'fi',"
                          "2,"
                          "'2013-01-01',"
                          "'2016-01-01'"
                          ")"))
          (sql/exec-raw (str "insert into jasenyys("
                          "henkiloid,"
                          "toimikunta,"
                          "rooli,"
                          "alkupvm,"
                          "loppupvm"
                          ")values("
                          (:henkiloid testihenkilo) ","
                          "'TKUN',"
                          "'sihteeri',"
                          "'2013-01-01',"
                          "'2016-01-01'"
                          ")"))
          (let [henkiloita (count (arkisto/hae-nykyiset))]
            (is (= henkiloita (+ henkiloita-alussa 1)))))))))

