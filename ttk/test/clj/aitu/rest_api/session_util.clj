(ns aitu.rest-api.session-util
    (:require
    aitu.compojure-util
    [aitu.integraatio.sql.test-util :refer [tietokanta-fixture alusta-korma!]]
    [aitu.integraatio.sql.test-util :refer :all]
    [aitu.asetukset :refer [lue-asetukset oletusasetukset]]
    [aitu.palvelin :as palvelin]
    [peridot.core :as peridot]
    [cheshire.core :as cheshire]
    [oph.korma.korma-auth :as ka]
    [oph.korma.korma-auth :as auth]
    [korma.db :as db]
    [infra.test.data :as testdata]
    [oph.common.infra.i18n :as i18n]
    [aitu.toimiala.kayttajaoikeudet :refer [*current-user-authmap*]]
    [aitu.toimiala.kayttajaroolit :refer [kayttajaroolit]]))

(def default-usermap
  {:roolitunnus (:yllapitaja kayttajaroolit), :oid auth/default-test-user-oid, :uid auth/default-test-user-uid})

(defn with-auth-user
  "Suorittaa funktion simuloiden sisäänkirjautuneen käyttäjän tilatietoja."
  ([f]
    (with-auth-user f default-usermap))
  ([f olemassaoleva-kayttaja]
    (binding [ka/*current-user-uid* (:uid olemassaoleva-kayttaja)
              ka/*current-user-oid* (promise)
              i18n/*locale* testi-locale
              *current-user-authmap* olemassaoleva-kayttaja]
      (deliver ka/*current-user-oid* (:oid olemassaoleva-kayttaja))
      (f))))

(defn mock-plain-request
  "Ei autentikoitua käyttäjää, eikä CSRF-tokeneita."
  [app url method params]
  (peridot/request app url
                   :request-method method
                   :params params))

(defn mock-json-post
  "Autentikoitu testikäyttäjä ja fake CSRF-token."
  [app url json-body]
  (with-auth-user
    #(peridot/request app url
       :request-method :post
       :headers { "x-xsrf-token" "token"
                  "uid" (:uid default-usermap)}
       :cookies { "XSRF-TOKEN" {:value "token"}}
       :content-type "application/json; charset=utf-8"
       :body json-body)
    default-usermap))

(defn mock-request
  "Autentikoitu testikäyttäjä ja fake CSRF-token."
  ([app url method params user-map]
    (with-auth-user
      #(peridot/request app url
         :request-method method
         :headers { "x-xsrf-token" "token"
                    "uid" (:uid user-map)}
         :cookies { "XSRF-TOKEN" {:value "token"}}
         :params params)
      user-map))
  ([app url method params]
    (mock-request app url method params default-usermap)))

(defn init-peridot! []
  (let [asetukset
        (-> oletusasetukset
          (assoc-in [:cas-auth-server :enabled] true)
          (assoc :development-mode true))
        _ (alusta-korma! asetukset)]
    (palvelin/app asetukset)))

(defn with-peridot
  "Alustaa app stackin ja Korman, mutta ei avaa transaktiota tietokantaan. Parametrina annettu f on funktio, joka ottaa parametrina peridot-session testikoodia varten."
  [f]
  (let [asetukset
        (-> oletusasetukset
          (assoc-in [:cas-auth-server :enabled] true)
          (assoc :development-mode true))
        pool (alusta-korma! asetukset)
        crout (palvelin/app asetukset)]
    (try
      (f crout)
      (finally
        (-> pool :pool :datasource .close)))))

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