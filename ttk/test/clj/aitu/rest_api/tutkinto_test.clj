(ns aitu.rest-api.tutkinto-test
  (:require
    [compojure.api.meta :as met]
    aitu.compojure-util
    [clojure.test :refer :all]
    [peridot.core :as peridot]
    [cheshire.core :as cheshire]
    [oph.korma.korma-auth :as auth]
    [aitu.integraatio.sql.test-data-util :refer :all]
    [aitu.rest-api.session-util :refer :all]))

(def osaamisalat-reply
  {:tutkintotunnus "927128", :opintoala "201", :tutkintoversio_id -20000, :peruste "34/911/2010", 
   :osaamisala [{:nimi_fi "Käsityöyrittäjyyden osaamisala (keksitty)", :nimi_sv nil, :osaamisalatunnus "9876" :osaamisala_id -20001} 
                {:nimi_fi "Käsityöopettajan osaamisala (keksitty)", :nimi_sv nil, :osaamisalatunnus "9875" :osaamisala_id -20002}]})

(deftest ^:integraatio osaamisalat-api
  (let [crout (init-peridot!)]
    (run-with-db (constantly true)
      #(let [s (peridot/session crout)
             osaamisalat (mock-request s "/api/tutkinto/osaamisalat/927128" :get {})]
        (is (= osaamisalat-reply (body-json (:response osaamisalat))))
        ))))
