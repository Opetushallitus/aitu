(ns aitu.rest-api.dbvalidation-test
  (:require
    [clojure.test :refer :all]
    [peridot.core :as peridot]
    [aitu.rest-api.session-util :refer :all]))

(deftest ^:integraatio db-validation-toimii
  (let [crout (init-peridot!)]
    (let [response (-> (peridot/session crout)
                     (mock-request "/api/db-validation" :get {}))]
      (is (= (:status (:response response)) 200)))))