(ns aitu.rest-api.arvioija-test
  (:require
    [compojure.api.meta :as met]
    aitu.compojure-util
    [clojure.test :refer :all]
    [peridot.core :as peridot]
    [cheshire.core :as cheshire]
    [oph.korma.korma-auth :as auth]
    [aitu.integraatio.sql.test-data-util :refer :all]
    [aitu.rest-api.session-util :refer :all]))
 
(deftest ^:integraatio arvioija-api
  (let [crout (init-peridot!)]
    (run-with-db (constantly true)
      #(let [s (peridot/session crout)]
         (are [hakuehdot result] (= result (body-json (:response (mock-request s "/api/arvioija/haku" :get hakuehdot))))
           {:nimi "Ilmarinen"} '({:arvioija_id -2 :nimi "Seppo Ilmarinen" :rooli "itsenainen" :nayttotutkintomestari true})
           {:nimi "Iki-turso"} '()
           {:nimi "äinä"} '({:arvioija_id -1 :nimi "Väinämöinen" :rooli "opettaja" :nayttotutkintomestari false})
           )))))

