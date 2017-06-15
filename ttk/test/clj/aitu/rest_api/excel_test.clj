(ns aitu.rest-api.excel-test
  (:require
    [clojure.test :refer :all]
    [aitu.rest-api.session-util :refer :all]
    [peridot.core :as peridot]
    [clojure.java.io :as io]))

(deftest ^:integraatio viallinen-excel-ei-toimi
  (let [crout (init-peridot!)
        file (io/file  "test-resources/tutosat_perus.xlsx")]

    (let [response (-> (peridot/session crout)
                                         (mock-request "/api/suoritus/excel-lataus" :post {"file" file}))]
      (is (= 404 (:status (:response response)))))))
