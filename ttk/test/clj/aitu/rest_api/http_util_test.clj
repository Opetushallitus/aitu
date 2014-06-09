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

(ns aitu.rest-api.http-util-test
  (:require [clojure.test :refer [deftest testing is]]
            [cheshire.core :as json]
            [aitu.rest-api.http-util :refer :all]))

(deftest validointi-test
  (testing "validoi*"
    (testing "palauttaa funktion paluuarvon, jos map validoituu"
      (is (= (validoi {:foo 1} [[:foo pos? :virhe]] {}
               :tulos)
             :tulos)))

    (testing "ei suorita runkoa, jos map ei validoidu"
      (let [kutsuttu? (atom false)]
        (validoi {:foo 0} [[:foo pos? :virhe]] {}
           (reset! kutsuttu? true))
        (is (not @kutsuttu?))))

    (testing "palauttaa HTTP-virheen, jos map ei validoidu"
      (let [vastaus (validoi {:foo 0} [[:foo pos? :virhe]] {}
                      :tulos)]
        (is (= (:status vastaus) 400))))

    (testing "listaa virheellisten kenttien virheet JSON:na vastauksen rungossa"
      (let [vastaus (validoi {:foo 1
                              :bar -3}
                             [[:foo pos? :oltava-positiivinen]
                              [:bar pos? :oltava-positiivinen]
                              [:bar #(zero? (mod % 2)) :oltava-parillinen]]
                             {}
                      :tulos)]
        (is (= (get-in vastaus [:headers "Content-Type"]) "application/json"))
        (is (= (json/parse-string (:body vastaus))
               {"errors" {"bar" ["oltava-positiivinen" "oltava-parillinen"]}}))))

    (testing "käyttää annettuja virhetekstejä"
      (let [vastaus (validoi {:foo 0} [[:foo pos? :oltava-positiivinen]]
                             {:oltava-positiivinen "Arvon on oltava positiivinen"}
                      :tulos)]
        (is (= (-> vastaus :body json/parse-string (get-in ["errors" "foo"]))
               ["Arvon on oltava positiivinen"]))))))

(deftest json-response-test
  (testing "json-response"
    (testing "palauttaa 404-vastauksen nil-syötteellä"
      (is (= (:status (json-response nil)) 404)))

    (testing "palauttaa content-typen ei-nil-syötteellä"
      (let [data {:foo "Bar"}]
        (is (= (:headers (json-response data))
               {"Content-Type" "application/json"}))))

    (testing "palauttaa 200-vastauksen ei-nil-syötteellä"
      (let [data {:foo "Bar"}]
        (is (= (:status (json-response data)) 200))))

    (testing "palauttaa vastauksen sarjallisetettuna ei-nil-syötteellä"
      (let [data {:foo "Bar"}]
        (is (= (:body (json-response data))
               "{\"foo\":\"Bar\"}"))))))
