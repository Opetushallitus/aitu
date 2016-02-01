(ns aitu.rest-api.auth-test
  (:require
    [clojure.test :refer :all]
    [peridot.core :as peridot]
    [aitu.rest-api.session-util :refer :all]))

(deftest ^:integraatio swagger-api-on-julkinen
  (let [crout (init-peridot!)]
    (let [response (-> (peridot/session crout)
                     (mock-plain-request "/api-docs/index.html" :get {}))]
      (is (= (:status (:response response)) 200)))))

(deftest ^:integraatio path-traversal-swaggerin-kautta-ei-onnistu-apiin
  ; https://www.owasp.org/index.php/Path_Traversal
  (let [crout (init-peridot!)]
    (let [response (-> (peridot/session crout)
                     (mock-plain-request "/api-docs/../api/kayttaja/" :get {}))]
      (is (= (:status (:response response)) 404))))) ; not found, ei compojure-routea

(deftest ^:integraatio path-traversal-swaggerin-kautta-ei-onnistu-staattiseen-resurssiin
  ; https://www.owasp.org/index.php/Path_Traversal
  (let [crout (init-peridot!)]
    (let [response (-> (peridot/session crout)
                     (mock-plain-request "/api-docs/../fi/template/direktiivit/tiedote" :get {}))]
      (is (= (:status (:response response)) 404))))) ; not found

(deftest ^:integraatio normaali-api-ei-ole-julkinen
  (let [crout (init-peridot!)]
    (let [response (-> (peridot/session crout)
                     (mock-plain-request "/api/kayttaja/" :get {}))]
      (is (= (:status (:response response)) 302))))) ; CAS redirect

(deftest ^:integraatio normaali-api-toimii
  (let [crout (init-peridot!)]
    (let [response (-> (peridot/session crout)
                     (mock-request "/api/kayttaja" :get {}))]
      (is (= (:status (:response response)) 200)))))
