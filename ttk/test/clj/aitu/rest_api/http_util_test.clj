(ns aitu.rest-api.http-util-test
  (:import java.nio.charset.Charset)
  (:require [clojure.test :refer :all]
            [aitu.rest-api.http-util :refer :all]))

(deftest textfile-download-response-test
  (testing "textfile-download-response"
    (testing "Palauttaa datan unicode-merkkijonona, jos koodausta ei ole määritelty"
      (is (= "åäö" (-> (textfile-download-response "åäö" "foo.txt" "text/plain")
                     :body))))

    (testing "Palauttaa datan määritellyssä koodauksessa"
      (is (= "åäö" (-> (textfile-download-response "åäö" "foo.txt" "text/plain"
                                                   {:charset "CP1252"})
                     :body
                     (slurp :encoding "CP1252")))))))
