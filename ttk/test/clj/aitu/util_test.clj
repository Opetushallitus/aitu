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

(ns aitu.util-test
  (:require [clojure.test :refer :all]
            [aitu.util :refer :all]
            [valip.predicates :refer [present?]]
            [clj-time.core :as time]
            [clojure.tools.logging :as log]))

(deftest keyword-vertailu-test
  (let [jarjestys [:a :b :c]]
    (testing "vertailee avaimet oikein, tuntemattomat avaimet viimeiseksi"
      (are [tulos key1 key2] (= tulos (keyword-vertailu jarjestys key1 key2))
           -1 :a :b
           1 :b :a
           0 :b :b
           1 :c :a
           -1 :a :x
           -1 :c :x
           1 :x :c
           0 :x :x))))

(deftest otsikot-ja-sarakkeet-jarjestykseen-test
  (let [jarjestys [:b :c]]
    (are [tulos data] (= tulos (otsikot-ja-sarakkeet-jarjestykseen data jarjestys))
         [[:b :c] ["b1" "c1"]] [{:c "c1" :b "b1"}]
         [[:b :c] ["b1" "c1"] ["b2" "c2"]] [{:c "c1" :b "b1"} {:c "c2" :b "b2"}]
         [[:b :c :a] ["b1" "c1" "a1"]] [{:a "a1" :c "c1" :b "b1"}])))

(deftest muodosta-csv-kayttaa-sarakkeiden-selvakielisia-otsikoita
  (is (= (muodosta-csv [{:nimi_fi "foo", :nimi_sv "fåå"}] [:nimi_fi :nimi_sv])
         "Nimi suomeksi;Nimi ruotsiksi\nfoo;fåå\n")))

(deftest muodosta-csv-logittaa-virheen-jos-otsikkoa-ei-loydy
  (let [log (atom [])]
    (with-redefs [log/log* (fn [_ level _ message]
                             (swap! log conj [level message]))]
      (muodosta-csv [{:nimi_fi "foo", :asdf "asdf"}] [:nimi_fi :asdf])
      (is (= (count @log) 1))
      (is (= (first (first @log)) :error))
      (is (re-matches #".*asdf.*" (second (first @log)))))))

(deftest numerokenttien-etunollien-pakotus-test
  (testing "laittaa vain numerokenttiin etunollapakotuksen"
    (is (= (pakota-numerokentat-csv-stringsoluiksi "Nimi;Numero\nfoo123;0123456789\n")
          "Nimi;Numero\nfoo123;=\"0123456789\"\n"))))

(deftest csv-rivi-soluiksi-test
  (testing "pilkkominen katkaisee solut oikeasta kohtaa"
    (is (= (csv-rivi-soluiksi "A;\"B\";\"A\"\"B\"")
           ["A"
            "\"B\""
            "\"A\"\"B\""]))
    (is (= (csv-rivi-soluiksi "\";\";\"1\"\"2\";\"1;2\"")
           ["\";\""
            "\"1\"\"2\""
            "\"1;2\""]))
    (is (= (csv-rivi-soluiksi "\"\n\";\"12\n34\";\"12\n34;56\";;1234")
           ["\"\n\""
            "\"12\n34\""
            "\"12\n34;56\""
            ""
            "1234"]))))

(deftest konversiot-toimivat
  (let [a [{:nimi_fi "foo", :truthy true} {:nimi_fi "bar" :truthy false}]
        b (muodosta-csv (convert-values a) [:nimi_fi :truthy] {:nimi_fi "nimi" :truthy "totuus"})]
    (is (= b "nimi;totuus\nfoo;Kyllä\nbar;Ei\n"))))

  
