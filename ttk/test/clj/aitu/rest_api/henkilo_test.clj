(ns aitu.rest-api.henkilo-test
  (:require
    [clojure.test :refer :all]
    [peridot.core :as peridot]
    [aitu.rest-api.session-util :refer :all]
    [aitu.toimiala.kayttajaroolit :refer [kayttajaroolit]]
    [aitu.integraatio.sql.test-data-util :as data, :refer :all]
    [oph.common.infra.common-audit-log :as common-audit-log]
    [oph.common.infra.common-audit-log-test :as common-audit-log-test]))

(def jarjesto-usermap
  {:roolitunnus (:jarjesto kayttajaroolit), :oid "OID.T-9999", :uid "T-9999"}) ; T-9999 ==> henkiloid -1000, jarjestoid -1

(deftest ^:integraatio henkilo-haku-kenttien-nakyvyydet
  (let [crout (init-peridot!)]
    (binding [ common-audit-log/*request-meta* common-audit-log-test/test-request-meta]
      (common-audit-log/konfiguroi-common-audit-lokitus (common-audit-log-test/test-environment-meta "aitu"))
      (run-with-db
        #(let ; [toimikunta-1 (lisaa-toimikunta-voimassaolevalle-kaudelle! {:nimi_fi "foo"})]
           [_ (lisaa-henkilo! {:henkiloid 1000
                               :etunimi "foo bar baz"
                               :puhelin "SIIKRIT"
                               :puhelin_julkinen false
                               :jarjesto -1})])
        #(let [response-jarj-omat (-> (peridot/session crout)
                                    (mock-request "/api/henkilo/-1000" :get {} jarjesto-usermap)) ; Puhelinnumero julkinen = false
               response-admin (-> (peridot/session crout)
                                (mock-request "/api/henkilo/-1000" :get {})) ; Puhelinnumero julkinen = false
               response-jarj-lisatty (-> (peridot/session crout)
                                       (mock-request "/api/henkilo/1000" :get {} jarjesto-usermap))]
           (is (nil? (get (body-json (:response response-jarj-omat)) :puhelin)))
           (is (= "050-TIUKKA-PAIKKA" (get (body-json (:response response-admin)) :puhelin)))
           (is (nil? (get (body-json (:response response-jarj-lisatty)) :puhelin)))

           (is (= (:status (:response response-admin)) 200)))
        jarjesto-usermap))))
