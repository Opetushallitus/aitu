(ns aitu.rest-api.dbvalidation-test
  (:require
    [clojure.test :refer :all]
    [aitu.integraatio.sql.test-util :refer [tietokanta-fixture alusta-korma!]]
    [aitu.integraatio.sql.test-util :refer :all]
    [aitu.asetukset :refer [lue-asetukset oletusasetukset]]
    [aitu.palvelin :as palvelin]
    [peridot.core :as peridot]
    [oph.korma.korma-auth :as ka]
    [oph.korma.korma-auth :as auth]
    [oph.common.infra.i18n :as i18n]
    [aitu.integraatio.sql.korma :as korma]
    [aitu.toimiala.kayttajaoikeudet :refer [*current-user-authmap*]]
    [aitu.toimiala.kayttajaroolit :refer [kayttajaroolit]]))

(defn with-auth-user [f]
  (let [olemassaoleva-kayttaja {:roolitunnus (:yllapitaja kayttajaroolit), :oid auth/default-test-user-oid, :uid auth/default-test-user-uid }]
    (binding [ka/*current-user-uid* (:uid olemassaoleva-kayttaja)
              ka/*current-user-oid* (promise)
              i18n/*locale* testi-locale
              *current-user-authmap* olemassaoleva-kayttaja]
      (deliver ka/*current-user-oid* (:oid olemassaoleva-kayttaja))
      (f))))

(defn mock-request [app url method params]
  (with-auth-user
    #(peridot/request app url
                      :request-method method
                      :headers { "x-xsrf-token" "token"}
                      :cookies { "XSRF-TOKEN" {:value "token"}}
                      :params params)))

(deftest ^:integraatio db-validation-toimii
  (let [asetukset
        (-> oletusasetukset
          (assoc-in [:cas-auth-server :enabled] false)
          (assoc :development-mode true))
        _ (alusta-korma! asetukset)
        crout (palvelin/app asetukset)]
    (let [response (-> (peridot/session crout)
                     (mock-request "/api/db-validation" :get {}))]
      (is (= (:status (:response response)) 200))
      (print response)
      )))