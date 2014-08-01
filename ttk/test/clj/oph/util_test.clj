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

(ns oph.util-test
  (:require [clojure.test :refer :all]
            [oph.common.util.util :refer :all]
            [valip.predicates :refer [present?]]
            [clj-time.core :as time]
            [clojure.tools.logging :as log]))

(deftest pisteavaimet->puu-test
  (is (= (pisteavaimet->puu {:foo.bar.baz 123
                             :foo.bar.blah 456})
         {:foo {:bar {:baz 123
                      :blah 456}}})))

(deftest get-in-list-test
  (let [test-map {:foo 0
                  :bar [{:baz 1}
                        {:baz 2}
                        {:baz 3}]}]
    (is (= 0 (get-in-list test-map [:foo])))
    (is (= [1 2 3] (get-in-list test-map [:bar :baz])))
    (is (= :not-found (get-in-list test-map [:bloo] :not-found)))))

(deftest uusin-muokkausaika-test
  (let [test-map {:muokattu (time/date-time 2013 1 1)
                 :foo {:muokattu (time/date-time 2013 2 1)}
                 :bar [{:muokattu (time/date-time 2013 3 1)}
                       {:muokattu (time/date-time 2013 4 1)}]}]
    (is (= (time/date-time 2013 1 1) (uusin-muokkausaika [test-map] [:muokattu])))
    (is (= (time/date-time 2013 2 1) (uusin-muokkausaika [test-map] [:muokattu] [:foo :muokattu])))
    (is (= (time/date-time 2013 4 1) (uusin-muokkausaika [test-map] [:muokattu] [:foo :muokattu] [:bar :muokattu])))))

(deftest sisaltaako-kentat-test
  (let [test-data [{:etunimi "Ahto"
                    :sukunimi "Simakuutio"}
                   {:etunimi "Teemu"
                    :sukunimi "Teekkari"}]]
    (are [termi kentat maara] (= (count (filter #(sisaltaako-kentat?  % kentat termi) test-data)) maara)
         "ahto" [:etunimi] 1
         "to sima" [:etunimi :sukunimi] 1
         "aku ankka" [:etunimi :sukunimi] 0)))

(deftest diff-maps-test
  (let [old {:key1 1
             :key2 2
             :key3 3}
        new {:key2 2
             :key3 4
             :key4 4}]
     (is (= (diff-maps new old)
            {:key1 [nil 1]
             :key2 nil
             :key3 [4 3]
             :key4 [4 nil]}))))

(deftest map-by-test
  (let [coll [{:key 1
               :value "a"}
              {:key 2
               :value "b"}
              {:key 1
               :value "c"}
              {:value "d"}]]
    (is (= (map-by :key coll)
           {1 {:key 1
               :value "c"}
            2 {:key 2
               :value "b"}}))))

(deftest retrying-test
  "Testaa transaktio retry-logiikan toiminnan"
  (let [log (atom [])]
    (with-redefs [log/log* (fn [_ level e _]
                             (swap! log conj [level (.getMessage e)]))]
      (testing "retrying"
        (testing "suorittaa annetun koodilohkon ja palauttaa sen arvon"
          (is (= (retrying Exception 10 :foo) :foo)))

        (testing "suorittaa annetun koodilohkon uudelleen, jos se heittää poikkeuksen"
          (let [n (atom 0)]
            (is (= (retrying Exception 10 (if (< (swap! n inc) 3)
                                            (throw (Exception.))
                                            @n))
                   3))))

        (testing "ei suorita koodilohkoa uudelleen, jos poikkeus ei ole annetun tyyppinen"
          (let [n (atom 0)]
            (is (thrown-with-msg? Error #"1"
                  (retrying Exception 10 (throw (Error. (str (swap! n inc)))))))))

        (testing "päästää poikkeuksen läpi annetun yritysmäärän jälkeen"
          (let [n (atom 0)]
            (is (thrown-with-msg? Exception #"10"
                  (retrying Exception 10 (throw (Exception. (str (swap! n inc)))))))))

        (testing "logittaa jokaisen uudelleenyritykseen johtaneen poikkeuksen"
          (reset! log [])
          (let [n (atom 0)]
            (try
              (retrying Exception 3 (throw (Exception. (str (swap! n inc)))))
              (catch Exception _)))
          (is (= @log [[:warn "1"] [:warn "2"]])))))))
 