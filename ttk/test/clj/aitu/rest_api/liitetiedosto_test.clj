(ns aitu.rest-api.liitetiedosto-test
  (:require
    [clojure.test :refer :all]
    [aitu.rest-api.session-util :refer :all]
    [peridot.core :as peridot]))
 
(deftest ^:integraatio vaaraa-tiedostotyyppia-ei-saa-lapi
  (let [crout (init-peridot!)
        file (clojure.java.io/file "test-resources/angband.zip")]
    (let [response (-> (peridot/session crout)
                     (mock-request "/api/jarjestamissopimus/38829/suunnitelma/3001" :post {"file" file}))]
      (is (= (:status (:response response)) 404)))
    (let [response (-> (peridot/session  crout)
                     (mock-request "/api/jarjestamissopimus/38829/liite/3001" :post {"file" file}))]
      (is (= (:status (:response response)) 404)))))
