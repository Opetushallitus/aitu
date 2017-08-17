(ns aitu.rest-api.session-util
    (:require
    aitu.compojure-util
    [aitu.integraatio.sql.test-util :refer [tietokanta-fixture alusta-korma! with-authenticated-user]]
    [aitu.asetukset :refer [lue-asetukset oletusasetukset]]
    [aitu.palvelin :as palvelin]
    [peridot.core :as peridot]
    [cheshire.core :as cheshire]
    [oph.korma.korma-auth :as auth]
    [korma.db :as db]
    [infra.test.data :as testdata]
    [oph.common.infra.i18n :as i18n]
                [clojure.tools.logging :as log]
    [aitu.toimiala.kayttajaoikeudet :refer [*current-user-authmap*]]
    [oph.common.infra.common-audit-log :as common-audit-log]
    [oph.common.infra.common-audit-log-test :as common-audit-log-test]    
    [aitu.toimiala.kayttajaroolit :refer [kayttajaroolit]]))

(def default-usermap
  {:roolitunnus (:yllapitaja kayttajaroolit), :oid auth/default-test-user-oid, :uid auth/default-test-user-uid})

(defn with-auth-user
  "Suorittaa funktion simuloiden sisäänkirjautuneen käyttäjän tilatietoja."
  ([f]
    (with-auth-user f default-usermap))
  ([f olemassaoleva-kayttaja]
    (with-authenticated-user f (:oid olemassaoleva-kayttaja) (:uid olemassaoleva-kayttaja))))

(defn mock-plain-request
  "Ei autentikoitua käyttäjää, eikä CSRF-tokeneita."
  [app url method params]
  (peridot/request app url
                   :request-method method
                   :params params))

(defn http-headers
  ([]
    (http-headers (:uid default-usermap)))
  ([uid] 
    {"x-xsrf-token" "token"
     "uid" uid
     "user-agent" (:user-agent common-audit-log-test/test-request-meta)
     "X-Forwarded-For" "192.168.50.1"}))

(def http-cookies
  { "XSRF-TOKEN" {:value "token"}
    "ring-session" {:value (:session common-audit-log-test/test-request-meta)}})

(defn mock-json-post
  "Autentikoitu testikäyttäjä ja fake CSRF-token."
  [app url json-body]
  (with-auth-user
    #(peridot/request app url
       :request-method :post
       :headers (http-headers)
       :cookies http-cookies
       :content-type "application/json; charset=utf-8"
       :body json-body)
    default-usermap))

(defn mock-request
  "Autentikoitu testikäyttäjä ja fake CSRF-token."
  ([app url method params user-map]
    (with-auth-user
      #(peridot/request app url
         :request-method method
         :headers (http-headers (:uid user-map))
         :cookies http-cookies
         :params params)
      user-map))
  ([app url method params]
    (mock-request app url method params default-usermap)))

(defn init-peridot! []
  (let [asetukset
        (-> oletusasetukset
          (assoc-in [:cas-auth-server :enabled] true)
          (assoc :development-mode true)
          (assoc-in [:server :base-url] "http://localhost:8080"))
        _ (alusta-korma! asetukset)]
    (palvelin/app asetukset)))

(defn with-peridot
  "Alustaa app stackin ja Korman, mutta ei avaa transaktiota tietokantaan. Parametrina annettu f on funktio, joka ottaa parametrina peridot-session testikoodia varten."
  [f]
  (binding [ common-audit-log/*request-meta* common-audit-log-test/test-request-meta]
     (common-audit-log/konfiguroi-common-audit-lokitus common-audit-log-test/test-environment-meta)
     (let [asetukset
           (-> oletusasetukset
             (assoc-in [:cas-auth-server :enabled] true)
             (assoc :development-mode true)
             (assoc-in [:server :base-url] "http://localhost:8080"))
           pool (alusta-korma! asetukset)
           crout (palvelin/app asetukset)]
       (try
         (f crout)
         (finally
           (-> pool :pool :datasource .close))))))

(defn body-json [response]
  (cheshire/parse-string (slurp (:body response) :encoding "UTF-8") true))

(defn generate-escaped-json-string [form]
  (cheshire/generate-string form {:escape-non-ascii true}))

(defn run-with-db
  ([dataf testf]
    (run-with-db dataf testf default-usermap))
  ([dataf testf usermap]
    (try
      (with-auth-user
        #(db/transaction
           (dataf)) usermap)
      (testf)
      (finally
        (with-auth-user
          #(db/transaction
             (testdata/tyhjenna-testidata! (:oid usermap))))))))