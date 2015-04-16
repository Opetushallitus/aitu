(ns aitu.rest-api.db-validation
  (:require [compojure.core :as c]
            [aitu.integraatio.sql.validationtest :as validationtest]
            [oph.common.util.http-util :refer [json-response]]
            [stencil.core :as s]
            [aitu.compojure-util :as cu]))

(def query-list @validationtest/default-query-list)

(c/defroutes reitit
  (cu/defapi :status nil :get "/" []
    (let [results-raw (validationtest/run-queries! query-list)
          results (map (fn [rm] {:title (:title rm) :results (map (fn [n] {:result n}) (:results rm))}) results-raw)]
      (s/render-file "html/dbvalidation"
                     {:testit results}))))
