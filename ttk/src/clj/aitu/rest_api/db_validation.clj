(ns aitu.rest-api.db-validation
  (:require [compojure.core :as c]
            [aitu.integraatio.sql.validationtest :as validationtest]
            [oph.common.util.http-util :refer [json-response]]
            [aitu.compojure-util :as cu]))

(def query-list @validationtest/default-query-list)

(c/defroutes reitit
  (cu/defapi :status nil :get "/" []
    (json-response (validationtest/run-queries! query-list))))
