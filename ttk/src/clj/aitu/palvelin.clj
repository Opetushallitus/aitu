;; Copyright (c) 2013 The Finnish National Board of Education - Opetushallitus
;;
;; This program is free software:  Licensed under the EUPL, Version 1.1 or - as
;; soon as they will be approved by the European Commission - subsequent versions
;; of the EUPL (the "Licence");
;;
;; You may not use this work except in compliance with the Licence.
;; You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
;;
;; This program is distributed in the hope that it will be useful,
;; but WITHOUT ANY WARRANTY; without even the implied warranty of
;; MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
;; European Union Public Licence for more details.

(ns aitu.palvelin
  "Sovelluksen HTTP-palvelin."
  (:gen-class)
  (:require [clojure.java.io :as io]
            [org.httpkit.server :as hs]
            [ring.middleware.multipart-params :refer [wrap-multipart-params]]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.middleware.session :refer [wrap-session]]
            [ring.middleware.session.memory :refer [memory-store]]
            [ring.util.request :refer [path-info request-url]]
            [ring.middleware.content-type :refer [wrap-content-type]]
            [ring.middleware.x-headers :refer [wrap-frame-options]]
            [ring.middleware.session-timeout :refer [wrap-idle-session-timeout]]
            [clojure.tools.logging :as log]
            [cheshire.generate :as json-gen]
            schema.core
            [stencil.core :as s]
            [compojure.core :as c]

            oph.korma.korma

            [oph.common.infra.print-wrapper :refer [debug-request log-request-wrapper]]
            [aitu.asetukset :refer [lue-asetukset oletusasetukset build-id kehitysmoodi? service-path]]
            [oph.common.infra.asetukset :refer [konfiguroi-lokitus]]
            [oph.common.infra.i18n :as i18n]
            [oph.common.infra.common-audit-log :refer [req-metadata-saver-wrapper konfiguroi-common-audit-lokitus]]
            [aitu.infra.auth-wrapper :as auth]
            [clj-cas-client.core :refer [cas]]
            [cas-single-sign-out.middleware :refer [wrap-cas-single-sign-out]]
            [oph.common.infra.anon-auth :as anon-auth]
            [oph.korma.korma-auth :as ka]
            [aitu.toimiala.kayttajaoikeudet :refer [*current-user-authmap* yllapitaja?]]
            [oph.common.util.poikkeus :refer [wrap-poikkeusten-logitus]]
            [aitu.integraatio.kayttooikeuspalvelu :as kop]
            [aitu.integraatio.clamav :as clamav]
            [aitu.infra.eraajo :as eraajo]
            [aitu.infra.eraajo.sopimusten-voimassaolo :as sopimusten-voimassaolo]
            aitu.compojure-util
            aitu.reitit)
  (:import [java.net.URL]))

(schema.core/set-fn-validation! true)

(defn cas-server-url [asetukset]
  (:url (:cas-auth-server asetukset)))

(defn service-url [asetukset]
  (let [base-url (get-in asetukset [:server :base-url])
        port (get-in asetukset [:server :port])]
    (cond
      (empty? base-url) (str "http://localhost:" port "/")
      (.endsWith base-url "/") base-url
      :else (str base-url "/"))))

(defn ajax-request? [request]
  (get-in request [:headers "angular-ajax-request"]))

(def swagger-resources
  "Swagger API resources, not authenticated using CAS"
  #{"/api-docs" "/swagger.json" "/fi/swagger.json"})

(defn auth-middleware
  [handler asetukset]
  (when (and (kehitysmoodi? asetukset)
          (:unsafe-https (:cas-auth-server asetukset))
          (:enabled (:cas-auth-server asetukset)))
    (anon-auth/enable-development-mode!))

  (fn [request]
      (let [cas-handler (auth/wrap-sessionuser handler)
            auth-handler (cas cas-handler #(cas-server-url asetukset) #(service-url asetukset) :no-redirect? ajax-request?)]
      (cond
        (some #(.startsWith (path-info request) %) swagger-resources)
          (do
            (log/info "swagger API docs are public, no auth")
            (handler request))
        (and (kehitysmoodi? asetukset) (not (:enabled (:cas-auth-server asetukset))))
          (let [anon-auth-handler (anon-auth/auth-cas-user cas-handler ka/default-test-user-uid)]
           (log/info "development, no CAS")
           (anon-auth-handler request))
        (and (kehitysmoodi? asetukset) ((:headers request) "uid"))
          (let [fake-auth-handler (anon-auth/auth-cas-user cas-handler ((:headers request) "uid"))]
            (log/info "development, fake CAS")
            (fake-auth-handler request))
        :else (auth-handler request)))))

(defn sammuta [palvelin]
  ((:sammuta palvelin)))

(defn ^:integration-api kaynnista-eraajon-ajastimet! [asetukset]
  (let [kop (kop/tee-kayttooikeuspalvelu (:ldap-auth-server asetukset))]
    (eraajo/kaynnista-ajastimet! kop asetukset)))

(defn timeout-response [asetukset]
  {:status 403
   :headers {"Content-Type" "text/html"}
   :body (s/render-file "html/sessio-vanhentunut" {:service-url (get-in asetukset [:server :base-url])})})

(defn clamav-mock
  "Lisää development-modessa handleriin routen, joka emuloi ClamAV:ta"
  [handler asetukset]
  (let [eicar-string "X5O!P%@AP[4\\PZX54(P^)7CC)7}$EICAR-STANDARD-ANTIVIRUS-TEST-FILE!$H+H*"]
    (if (:development-mode asetukset)
      (c/routes
        (c/POST "/scan" req (let [file (slurp (get-in req [:params "file" :tempfile]) :encoding "ASCII")]
                              (if (= file eicar-string)
                                clamav/found-reply
                                clamav/ok-reply)))
        handler)
      handler)))

(defn app [asetukset]
  (let [hostname (-> asetukset :server :base-url java.net.URL. .getHost)
        audit-asetukset (assoc aitu.asetukset/common-audit-log-asetukset :hostname hostname)]
    (konfiguroi-common-audit-lokitus audit-asetukset))
  (json-gen/add-encoder org.joda.time.DateTime
                        (fn [c json-generator]
                          (.writeString json-generator (.toString c))))
  (json-gen/add-encoder org.joda.time.LocalDate
                        (fn [c json-generator]
                          (.writeString json-generator (.toString c "dd.MM.yyyy"))))

  (let [session-store (memory-store)
        reitit (aitu.reitit/reitit asetukset)]
    (->
      reitit
      (i18n/wrap-locale
        :ei-redirectia #"/api.*|/scan.*"
        :base-url (-> asetukset :server :base-url))
      (wrap-idle-session-timeout {:timeout (:session-timeout asetukset)
                                  :timeout-response (timeout-response asetukset)})
      (auth-middleware asetukset)
      log-request-wrapper
      req-metadata-saver-wrapper   ;; Huom: Tämän oltava "auth-middleware":n jälkeen

      (clamav-mock asetukset)
      wrap-multipart-params
      (wrap-resource "public")
      wrap-content-type
      (wrap-frame-options :sameorigin)
      (wrap-session {:store session-store
                     :cookie-attrs {:http-only true
                                    :path (service-path (get-in asetukset [:server :base-url]))
                                    :secure (not (:development-mode asetukset))}})
      (wrap-cas-single-sign-out session-store)
      wrap-poikkeusten-logitus)))

(defn ^:integration-api kaynnista! [oletus-asetukset]
  (try
    (let [asetukset (lue-asetukset oletus-asetukset)
          _ (deliver aitu.asetukset/asetukset asetukset)
          _ (konfiguroi-lokitus asetukset)
          _ (log/info "Käynnistetään Aitu" @build-id)
          _ (oph.korma.korma/luo-db (:db asetukset))
          upload-limit (* 10 1024 1024) ; max file upload (and general HTTP body) size in bytes
          sammuta (hs/run-server
                    (app asetukset)
                    {:port (get-in asetukset [:server :port])
                     :max-body upload-limit
                     :thread (get-in asetukset [:server :pool-size])})]
      (when (or (not (:development-mode asetukset))
                (:eraajo asetukset))
        (kaynnista-eraajon-ajastimet! asetukset))
      (log/info "Kehitysmoodi päällä:" (kehitysmoodi? asetukset))
      (log/info "Palvelin käynnistetty:" (service-url asetukset))
      (.start (Thread. sopimusten-voimassaolo/paivita-sopimusten-voimassaolo!))
      {:sammuta sammuta
       :asetukset asetukset})
  (catch Throwable t
    (let [virheviesti "Palvelimen käynnistys epäonnistui"]
      (log/error t virheviesti)
      (binding [*out* *err*]
        (println virheviesti))
      (.printStackTrace t *err*)
      (System/exit 1)))))

(defn -main []
  (kaynnista! oletusasetukset))