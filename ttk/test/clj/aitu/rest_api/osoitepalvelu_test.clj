(ns aitu.rest-api.osoitepalvelu-test
  (:require
    [clojure.test :refer :all]
    [peridot.core :as peridot]
    [cheshire.core :as cheshire]
    [oph.korma.korma-auth :as auth]
    [aitu.integraatio.sql.test-data-util :refer :all]
    [aitu.rest-api.session-util :refer :all]))

(def toimikunta-reply
  {:toimikunnat [{:id "Gulo gulo", :nimi {:fi "Aavasaksalainen testitoimikunta", :sv "Aakkosissa Aavasaksa asettuu alkuun"}, :kielisyys "fi", :sahkoposti "trolololoo@solita.fi", :toimikausi "voimassa", :jasenyydet []} 
                 {:id "Lynx lynx", :nimi {:fi "Lattaraudan taivutuksen testitoimikunta", :sv "Ruotsalaisen lattaraudan testitoimikunta"}, :kielisyys "sv", :sahkoposti "rauta-aika@solita.fi", :toimikausi "voimassa", :jasenyydet []}],
   :oppilaitokset []})

(deftest ^:integraatio osoitepalvelu-api
  (let [crout (init-peridot!)
        skerta (fn [suoritus] (dissoc suoritus :suorituskerta_id))]
    (run-with-db (constantly true)
      #(let [s (peridot/session crout)
             osoitteet (mock-request s "/api/osoitepalvelu" :get {})
             ]
        (is (= toimikunta-reply (body-json (:response osoitteet))))
        ))))

