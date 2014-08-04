(ns oph.rest-api.js-log-test
  (:require [clojure.test :refer [deftest testing is]]
            [oph.rest_api.js-log :as js-log]))

(deftest test-linefeeds
  (is (= "a!!b" (js-log/sanitize "a\n\rb")))
  (is (= "c" (js-log/sanitize "c")))
  (is (= "" (js-log/sanitize ""))))

(deftest test-length-limit
  (let [long-str (reduce str (map str (range 1 js-log/maxlength)))]
    (is (< js-log/maxlength (count long-str)))
    (is (= js-log/maxlength (count (js-log/sanitize long-str))))))
    
