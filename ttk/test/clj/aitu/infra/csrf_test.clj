(ns aitu.infra.csrf-test
  (:require [clojure.test :refer [deftest testing is are]]
            [aitu.infra.csrf-token :refer :all]))

(defn generoi-request [cookie-arvo header-arvo]
  {:cookies {"XSRF-TOKEN" {:value cookie-arvo}}
   :headers {"x-xsrf-token" header-arvo}})

(defn generoi-request-token-parameterihin [cookie-arvo header-arvo]
  {:cookies {"XSRF-TOKEN" {:value cookie-arvo}}
   :multipart-params {"x-xsrf-token" header-arvo}})

(deftest validi-csrf-token-test
  (testing "CSRF tokenin tarkastus"
    (let [virheellinen-request (generoi-request "virheellinentoken" "token")
          request (generoi-request "token" "token")
          virheellinen-param-request (generoi-request-token-parameterihin "virheellinentoken" "token")
          param-request (generoi-request-token-parameterihin "token" "token")]
      (is (not (validi-csrf-token? virheellinen-request)))
      (is (validi-csrf-token? request))
      (is (not (validi-csrf-token? virheellinen-param-request)))
      (is (validi-csrf-token? param-request)))))

(deftest wrap-tarkasta-csrf-token-test
  (testing "CSRF token wrapper"
    (let [tarkastus-fn (wrap-tarkasta-csrf-token (fn [_] {:status 200}))
          virheellinen-request (generoi-request "virheellinentoken" "token")
          request (generoi-request "token" "token")]
      (is (= (tarkastus-fn virheellinen-request) {:status 401}))
      (is (= (tarkastus-fn request) {:status 200})))))
