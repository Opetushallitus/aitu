(ns aitu.reitit
  (:require
            [cheshire.core :as json]
            [stencil.core :as s]
            [stencil.loader :as sl]
            [clojure.tools.logging :as log]
            [compojure.core :as c]
            schema.core
            [aitu.compojure-util :as cu]
            [clojure.pprint :refer [pprint]]
            [compojure.api.sweet :refer :all]
            [ring.swagger.core :refer [json-type]]

            [aitu.infra.i18n :as i18n]
            [aitu.toimiala.kayttajaoikeudet
             :refer [*current-user-authmap* yllapitaja?]]
            [aitu.infra.status :refer [status]]
            [aitu.asetukset :refer [build-id kehitysmoodi?]]
            [aitu.toimiala.skeema :refer :all]
            [aitu.rest-api.http-util :refer [json-response]]
            [aitu.infra.csrf-token :refer [aseta-csrf-token tarkasta-csrf-token]]

            aitu.rest-api.db-validation
            aitu.rest-api.ttk
            aitu.rest-api.henkilo
            aitu.rest-api.oppilaitos
            aitu.rest-api.tutkinto
            aitu.rest-api.toimikausi
            aitu.rest-api.kayttaja
            aitu.rest-api.koulutusala
            aitu.rest-api.opintoala
            aitu.rest-api.jarjestamissopimus
            aitu.rest-api.jarjesto
            aitu.rest-api.enum
            aitu.rest-api.tutkintorakenne
            aitu.rest_api.js-log
            aitu.rest-api.tiedote
            aitu.rest-api.ohje
            aitu.rest-api.haku
            aitu.rest-api.osoitepalvelu

            aitu.test-api.ttk
            aitu.test-api.tutkinto
            aitu.test-api.tutkintoversio
            aitu.test-api.tutkintotyyppi
            aitu.test-api.peruste
            aitu.test-api.koulutusala
            aitu.test-api.opintoala
            aitu.test-api.oppilaitos
            aitu.test-api.jarjestamissopimus
            aitu.test-api.henkilo
            aitu.test-api.e2e
            aitu.test-api.jarjesto))

(defn angular-template [nimi asetukset]
  (when-let [mustache (sl/load (str "html/angular/" nimi))]
    (s/render mustache {:i18n (i18n/tekstit)
                        :ominaisuus (:ominaisuus asetukset)
                        :base-url (-> asetukset :server :base-url)
                        :yllapitaja yllapitaja?})))

(defn testapi
  [asetukset]
  (if (kehitysmoodi? asetukset)
    (do
      (log/info "!! TEST-API reititään!!")
      (c/routes
        (c/context "/api/test/ttk" [] aitu.test-api.ttk/reitit)
        (c/context "/api/test/tutkinto" [] aitu.test-api.tutkinto/reitit)
        (c/context "/api/test/tutkintoversio" [] aitu.test-api.tutkintoversio/reitit)
        (c/context "/api/test/tutkintotyyppi" [] aitu.test-api.tutkintotyyppi/reitit)
        (c/context "/api/test/koulutusala" [] aitu.test-api.koulutusala/reitit)
        (c/context "/api/test/opintoala" [] aitu.test-api.opintoala/reitit)
        (c/context "/api/test/peruste" [] aitu.test-api.peruste/reitit)
        (c/context "/api/test/oppilaitos" [] aitu.test-api.oppilaitos/reitit)
        (c/context "/api/test/jarjestamissopimus" [] aitu.test-api.jarjestamissopimus/reitit)
        (c/context "/api/test/henkilo" [] aitu.test-api.henkilo/reitit)
        (c/context "/api/test/e2e" [] aitu.test-api.e2e/reitit)
        (c/context "/api/test/jarjesto" [] aitu.test-api.jarjesto/reitit)))
    (c/routes)))

(defn reitit [asetukset]
  (c/routes
    (swagger-ui "/api-docs")
    (swagger-docs
      :title "AITU API"
      :description "AITUn julkinen rajapinta")
    (swaggered "AITU"
      (c/context "/api/ttk" [] (tarkasta-csrf-token aitu.rest-api.ttk/reitit))
      (c/context "/api/henkilo" [] (tarkasta-csrf-token aitu.rest-api.henkilo/reitit))
      (c/context "/api/kayttaja" [] (tarkasta-csrf-token aitu.rest-api.kayttaja/reitit))
      (c/context "/api/koulutusala" [] (tarkasta-csrf-token aitu.rest-api.koulutusala/reitit))
      (c/context "/api/opintoala" [] (tarkasta-csrf-token aitu.rest-api.opintoala/reitit))
      (c/context "/api/tutkinto" [] (tarkasta-csrf-token aitu.rest-api.tutkinto/reitit))
      (c/context "/api/toimikausi" [] (tarkasta-csrf-token aitu.rest-api.toimikausi/reitit))
      (c/context "/api/oppilaitos" [] (tarkasta-csrf-token aitu.rest-api.oppilaitos/reitit))
      (c/context "/api/jarjestamissopimus" [] (tarkasta-csrf-token aitu.rest-api.jarjestamissopimus/reitit))
      (c/context "/api/tutkintorakenne" []  (tarkasta-csrf-token aitu.rest-api.tutkintorakenne/reitit))
      (c/context "/api/jarjesto" [] (tarkasta-csrf-token aitu.rest-api.jarjesto/reitit))
      (c/context "/api/enum" [] (tarkasta-csrf-token aitu.rest-api.enum/reitit))
      (c/context "/api/jslog" [] (tarkasta-csrf-token aitu.rest_api.js-log/reitit))
      (c/context "/api/tiedote" [] (tarkasta-csrf-token aitu.rest-api.tiedote/reitit))
      (c/context "/api/ohje" [] (tarkasta-csrf-token aitu.rest-api.ohje/reitit))
      (c/context "/api/haku" [] (tarkasta-csrf-token aitu.rest-api.haku/reitit))
      (c/context "/api/osoitepalvelu" [] aitu.rest-api.osoitepalvelu/reitit)
      (c/context "/api/db-validation" [] aitu.rest-api.db-validation/reitit ))
    (testapi asetukset)
    (c/GET "/template/:nimi" [nimi]
      (angular-template nimi asetukset))
    (cu/defapi :etusivu nil :get "/" []
      {:body (s/render-file "html/ttk" {:build-id @build-id
                                        :current-user (:kayttajan_nimi *current-user-authmap*)
                                        :base-url (-> asetukset :server :base-url)
                                        :logout-url (str (-> asetukset :cas-auth-server :url) "/logout")
                                        :ominaisuus (:ominaisuus asetukset)
                                        :i18n (i18n/tekstit)
                                        :i18n-json (json/generate-string (i18n/tekstit))
                                        :yllapitaja yllapitaja?})
       :status 200
       :headers {"Content-Type" "text/html"
                 "Set-cookie" (aseta-csrf-token)}})
    (cu/defapi :status nil :get "/status" []
      (s/render-file "html/status"
                     (assoc (status)
                            :asetukset (with-out-str
                                         (-> asetukset
                                           (assoc-in [:db :password] "*****")
                                           (assoc-in [:ldap-auth-server :password] "*****")
                                           pprint))
                            :build-id @build-id)))))
