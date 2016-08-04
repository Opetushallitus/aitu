(ns aitu.rest-api.db-validation
  (:require [aitu.integraatio.sql.validationtest :as validationtest]
            aitu.compojure-util
            [stencil.core :as s]
            [compojure.api.core :refer [GET defroutes]]))

(def query-list @validationtest/default-query-list)

(defroutes reitit
  (GET "/" []
    :summary "Tietosisällön validointikyselyiden tulokset, raportti."
    :kayttooikeus :status
    (let [results-raw (validationtest/run-queries! query-list)
          results (map (fn [rm] {:title (:title rm) :results (map (fn [n] {:result n}) (:results rm))}) results-raw)]
      (s/render-file "html/dbvalidation"
                     {:testit results}))))
