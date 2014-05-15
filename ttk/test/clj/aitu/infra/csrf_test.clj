(ns aitu.infra.csrf-test
  (:require [clojure.test :refer [deftest testing is are]]
            [aitu.infra.csrf-token :refer :all]))

(defn generoi-request [cookie-arvo header-arvo]
  {:cookies {"XSRF-TOKEN" {:value cookie-arvo}}
   :headers {"x-xsrf-token" header-arvo}})

(deftest tarkasta-csrf-token-test
  (testing "CSRF tokenin tarkastus"
    (let [tarkastus-fn (tarkasta-csrf-token (fn [_] {:status 200}))
          virheellinen-request (generoi-request "virheellinentoken" "token")
          request (generoi-request "token" "token")]
      (is (= (tarkastus-fn virheellinen-request) {:status 401}))
      (is (= (tarkastus-fn request) {:status 200})))))
