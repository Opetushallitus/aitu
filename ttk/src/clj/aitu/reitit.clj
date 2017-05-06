(ns aitu.reitit
  (:require [clojure.pprint :refer [pprint]]
            [clojure.tools.logging :as log]

            [cheshire.core :as json]
            [compojure.api.sweet :refer [GET api context swagger-routes]]
            [compojure.core :as c]
            [compojure.route :as r]
            schema.core
            [stencil.core :as s]
            [stencil.loader :as sl]

            [aitu.asetukset :refer [build-id kehitysmoodi? service-path]]
            [aitu.infra.i18n :as i18n]
            [oph.common.infra.status :refer [status]]
            [aitu.toimiala.kayttajaoikeudet :refer [*current-user-authmap* *impersonoitu-oid* yllapitaja?]]
            [aitu.toimiala.skeema :refer :all]
            [oph.common.infra.csrf-token :refer [aseta-csrf-token wrap-tarkasta-csrf-token]]

            aitu.rest-api.aipal
            aitu.rest-api.arvioija
            aitu.rest-api.db-validation
            aitu.rest-api.enum
            aitu.rest-api.haku
            aitu.rest-api.henkilo
            aitu.rest-api.jarjestamissopimus
            aitu.rest-api.jarjesto
            aitu.rest-api.jasenesitykset
            aitu.rest-api.kayttaja
            aitu.rest-api.koulutusala
            aitu.rest-api.koulutustoimija
            aitu.rest-api.ohje
            aitu.rest-api.opintoala
            aitu.rest-api.oppilaitos
            aitu.rest-api.organisaatiomuutos
            aitu.rest-api.osoitepalvelu
            aitu.rest-api.rahoitusmuoto
            aitu.rest-api.suorittaja
            aitu.rest-api.suoritus
            aitu.rest-api.tiedote
            aitu.rest-api.toimikausi
            aitu.rest-api.ttk
            aitu.rest-api.tutkinnonosa
            aitu.rest-api.tutkinto
            aitu.rest-api.tutkintorakenne
            aitu.rest_api.js-log

            aitu.test-api.e2e
            aitu.test-api.henkilo
            aitu.test-api.jarjestamissopimus
            aitu.test-api.jarjesto
            aitu.test-api.koulutusala
            aitu.test-api.koulutustoimija
            aitu.test-api.opintoala
            aitu.test-api.oppilaitos
            aitu.test-api.peruste
            aitu.test-api.ttk
            aitu.test-api.tutkinto
            aitu.test-api.tutkintotyyppi
            aitu.test-api.tutkintoversio))

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
      (log/info "!! TEST-API reititetään!!")
      (c/routes
        (c/context "/api/test/ttk" [] aitu.test-api.ttk/reitit)
        (c/context "/api/test/tutkinto" [] aitu.test-api.tutkinto/reitit)
        (c/context "/api/test/tutkintoversio" [] aitu.test-api.tutkintoversio/reitit)
        (c/context "/api/test/tutkintotyyppi" [] aitu.test-api.tutkintotyyppi/reitit)
        (c/context "/api/test/koulutusala" [] aitu.test-api.koulutusala/reitit)
        (c/context "/api/test/opintoala" [] aitu.test-api.opintoala/reitit)
        (c/context "/api/test/peruste" [] aitu.test-api.peruste/reitit)
        (c/context "/api/test/koulutustoimija" [] aitu.test-api.koulutustoimija/reitit)
        (c/context "/api/test/oppilaitos" [] aitu.test-api.oppilaitos/reitit)
        (c/context "/api/test/jarjestamissopimus" [] aitu.test-api.jarjestamissopimus/reitit)
        (c/context "/api/test/henkilo" [] aitu.test-api.henkilo/reitit)
        (c/context "/api/test/e2e" [] aitu.test-api.e2e/reitit)
        (c/context "/api/test/jarjesto" [] aitu.test-api.jarjesto/reitit)))
    (c/routes)))

(defn reitit [asetukset]
  (let [base-url (get-in asetukset [:server :base-url])]
    (api
      (swagger-routes
        {:ui "/api-docs"
         :spec "/swagger.json"
         :data {:info {:title "AITU API"
                       :description "AITUn rajapinnat. Sisältää sekä integraatiorajapinnat muihin järjestelmiin, että Aitun sisäiseen käyttöön tarkoitetut rajapinnat."}
                :basePath (str (service-path base-url))}})
      (context "/api/ttk"  [] aitu.rest-api.ttk/raportti-reitit)
      (context "/api/ttk" [] aitu.rest-api.ttk/paatos-reitit)
      (context "/api/ttk" [] :middleware [wrap-tarkasta-csrf-token] aitu.rest-api.ttk/reitit)
      (context "/api/henkilo" [] aitu.rest-api.henkilo/raportti-reitit)
      (context "/api/henkilo" [] :middleware [wrap-tarkasta-csrf-token] aitu.rest-api.henkilo/reitit)
      (context "/api/kayttaja" [] :middleware [wrap-tarkasta-csrf-token] aitu.rest-api.kayttaja/reitit)
      (context "/api/koulutusala" [] :middleware [wrap-tarkasta-csrf-token] aitu.rest-api.koulutusala/reitit)
      (context "/api/opintoala" [] :middleware [wrap-tarkasta-csrf-token] aitu.rest-api.opintoala/reitit)
      (context "/api/tutkinto" [] aitu.rest-api.tutkinto/raportti-reitit)
      (context "/api/tutkinto" [] :middleware [wrap-tarkasta-csrf-token] aitu.rest-api.tutkinto/reitit)
      (context "/api/toimikausi" [] :middleware [wrap-tarkasta-csrf-token] aitu.rest-api.toimikausi/reitit)
      (context "/api/oppilaitos" [] aitu.rest-api.oppilaitos/raportti-reitit)
      (context "/api/oppilaitos" [] :middleware [wrap-tarkasta-csrf-token] aitu.rest-api.oppilaitos/reitit)
      (context "/api/koulutustoimija" [] aitu.rest-api.koulutustoimija/raportti-reitit)
      (context "/api/koulutustoimija" [] :middleware [wrap-tarkasta-csrf-token] aitu.rest-api.koulutustoimija/reitit)
      (context "/api/jarjestamissopimus" [] aitu.rest-api.jarjestamissopimus/raportti-reitit)
      (context "/api/jarjestamissopimus" [] aitu.rest-api.jarjestamissopimus/liite-lataus-reitit)
      (context "/api/jarjestamissopimus" [] :middleware [wrap-tarkasta-csrf-token] aitu.rest-api.jarjestamissopimus/reitit)
      (context "/api/tutkintorakenne" [] :middleware [wrap-tarkasta-csrf-token] aitu.rest-api.tutkintorakenne/reitit)
      (context "/api/jarjesto" [] :middleware [wrap-tarkasta-csrf-token] aitu.rest-api.jarjesto/reitit)
      (context "/api/enum" [] :middleware [wrap-tarkasta-csrf-token] aitu.rest-api.enum/reitit)
      (context "/api/jslog" [] :middleware [wrap-tarkasta-csrf-token] aitu.rest_api.js-log/reitit)
      (context "/api/tiedote" [] :middleware [wrap-tarkasta-csrf-token] aitu.rest-api.tiedote/reitit)
      (context "/api/ohje" [] :middleware [wrap-tarkasta-csrf-token] aitu.rest-api.ohje/reitit)
      (context "/api/haku" [] :middleware [wrap-tarkasta-csrf-token] aitu.rest-api.haku/reitit)
      (context "/api/osoitepalvelu" [] aitu.rest-api.osoitepalvelu/reitit)
      (context "/api/db-validation" [] aitu.rest-api.db-validation/reitit)
      (context "/api/organisaatiomuutos" [] aitu.rest-api.organisaatiomuutos/reitit)
      (context "/api/rahoitusmuoto" [] :middleware [wrap-tarkasta-csrf-token] aitu.rest-api.rahoitusmuoto/reitit)
      (context "/api/suorittaja" [] :middleware [wrap-tarkasta-csrf-token] aitu.rest-api.suorittaja/reitit)
      (context "/api/suoritus" [] aitu.rest-api.suoritus/reitit-lataus)
      (context "/api/suoritus" [] aitu.rest-api.suoritus/raportti-reitit)
      (context "/api/suoritus" [] :middleware [wrap-tarkasta-csrf-token] aitu.rest-api.suoritus/reitit)
      (context "/api/tutkinnonosa" [] :middleware [wrap-tarkasta-csrf-token] aitu.rest-api.tutkinnonosa/reitit)
      (context "/api/jasenesitykset" [] aitu.rest-api.jasenesitykset/reitit-csv)
      (context "/api/jasenesitykset" [] :middleware [wrap-tarkasta-csrf-token] aitu.rest-api.jasenesitykset/reitit)
      (context "/api/aipal" [] aitu.rest-api.aipal/reitit)
      (context "/api/arvioija" [] :middleware [wrap-tarkasta-csrf-token] aitu.rest-api.arvioija/reitit)
      (testapi asetukset)
      (c/GET ["/template/:nimi" :nimi #"[a-z/-]+"] [nimi]
        (angular-template nimi asetukset))
      (GET "/" []
        :kayttooikeus :etusivu
        {:body    (s/render-file "html/ttk" {:build-id     @build-id
                                             :current-user (:kayttajan_nimi *current-user-authmap*)
                                             :impersonoitu (if *impersonoitu-oid* "true" "false")
                                             :base-url     (-> asetukset :server :base-url)
                                             :logout-url   (str (-> asetukset :cas-auth-server :url) "/logout")
                                             :ominaisuus   (:ominaisuus asetukset)
                                             :i18n         (i18n/tekstit)
                                             :i18n-json    (json/generate-string (i18n/tekstit))
                                             :yllapitaja   yllapitaja?})
         :status  200
         :headers {"Content-Type" "text/html"
                   "Set-cookie"   (aseta-csrf-token (-> asetukset :server :base-url service-path))}})
      (GET "/status" []
        :summary "Tietoa Aitun asetuksista palvelimella. Versionumerot yms. ongelmien selvittämistä varten."
        :kayttooikeus :status
        (s/render-file "html/status"
                       (assoc (status)
                         :asetukset (with-out-str
                                      (-> asetukset
                                          (assoc-in [:db :password] "*****")
                                          (assoc-in [:ldap-auth-server :password] "*****")
                                          pprint))
                         :build-id @build-id)))
      (r/not-found "Not found"))))
