(ns aitu.rest-api.db-validation
  (:require [aitu.integraatio.sql.validationtest :as validationtest]
            [stencil.core :as s]
            [aitu.compojure-util :as cu :refer [GET*]]
            [compojure.api.core :refer [defroutes*]]))

(def query-list @validationtest/default-query-list)

(defroutes* reitit
  (GET* "/" []
    :kayttooikeus :status
    (let [results-raw (validationtest/run-queries! query-list)
          results (map (fn [rm] {:title (:title rm) :results (map (fn [n] {:result n}) (:results rm))}) results-raw)]
      (s/render-file "html/dbvalidation"
                     {:testit results}))))
