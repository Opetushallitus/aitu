(ns aitu.rest-api.kayttooikeus-test
  (:require [cheshire.core :as cheshire]
            [clojure.test :refer :all]
            [peridot.core :as peridot]

            [aitu.rest-api.session-util :refer :all]))

(deftest kayttoikeus-pitaa-olla-maaritetty
  (let [app (init-peridot!)
        swagger-json (-> {:app app}
                         (peridot/request "/swagger.json" :request-method :get)
                         (peridot/follow-redirect)
                         :response
                         :body
                         slurp
                         (cheshire/parse-string))]
    (testing "Käyttöoikeus:"
      (doseq [[path methods] (get-in swagger-json ["paths"])
              [method method-definition] methods]
        (testing (str path " " method)
          (is (.contains (get-in method-definition ["description"]) "Käyttöoikeus")))))))
