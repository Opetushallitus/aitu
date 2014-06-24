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
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [ring.middleware.multipart-params :refer [wrap-multipart-params]]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.middleware.json :refer [wrap-json-params wrap-json-response]]
            [ring.middleware.session :refer [wrap-session]]
            [ring.middleware.session.memory :refer [memory-store]]
            [ring.middleware.content-type :refer [wrap-content-type]]
            [ring.middleware.x-headers :refer [wrap-frame-options]]
            [clojure.tools.logging :as log]
            [cheshire.generate :as json-gen]
            schema.core

            aitu.integraatio.sql.korma

            [aitu.infra.print-wrapper :refer [debug-request log-request-wrapper]]
            [aitu.asetukset :refer [lue-asetukset oletusasetukset konfiguroi-lokitus build-id kehitysmoodi?]]
            [aitu.infra.i18n :as i18n]
            [aitu.infra.auth-wrapper :as auth]
            [clj-cas-client.core :refer [cas]]
            [cas-single-sign-out.middleware :refer [wrap-cas-single-sign-out]]
            [aitu.infra.anon-auth :as anon-auth]
            [aitu.toimiala.kayttajaoikeudet
             :refer [*current-user-authmap* yllapitaja?]]
            [aitu.poikkeus :refer [wrap-poikkeusten-logitus]]
            [aitu.integraatio.kayttooikeuspalvelu :as kop]
            [aitu.infra.eraajo :as eraajo]))

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

(defn service-path [base-url]
  (let [path (drop 3 (clojure.string/split base-url #"/"))]
    (str "/" (clojure.string/join "/" path))))

(defn ajax-request? [request]
  (get-in request [:headers "angular-ajax-request"]))

(defn auth-middleware
  [handler asetukset]
  (when (and (kehitysmoodi? asetukset)
             (:unsafe-https (:cas-auth-server asetukset))
             (:enabled (:cas-auth-server asetukset)))
    (anon-auth/enable-development-mode!))
  (if (and (kehitysmoodi? asetukset)
           (not (:enabled (:cas-auth-server asetukset))))
    (anon-auth/auth-cas-user handler)
    (fn [request]
      (let [auth-handler (if (and (kehitysmoodi? asetukset)
                                  ((:headers request) "uid"))
                           (anon-auth/auth-cas-user handler)
                           (cas handler #(cas-server-url asetukset) #(service-url asetukset) :no-redirect? ajax-request?))]
        (auth-handler request)))))

(defn sammuta [palvelin]
  ((:sammuta palvelin)))

(defn kaynnista-eraajon-ajastimet! [asetukset]
  (let [kop (kop/tee-kayttooikeuspalvelu (:ldap-auth-server asetukset))]
    (eraajo/kaynnista-ajastimet! kop (:organisaatiopalvelu asetukset))))

(defn kaynnista! [oletus-asetukset]
  (try
    (let [asetukset (lue-asetukset oletus-asetukset)
          _ (konfiguroi-lokitus asetukset)
          _ (log/info "Käynnistetään Aitu" @build-id)
          _ (aitu.integraatio.sql.korma/luo-db (:db asetukset))
          upload-limit 100000000 ; max file upload (and general HTTP body) size in bytes
          session-store (memory-store)
          _ (require 'aitu.reitit)
          reitit ((eval 'aitu.reitit/reitit) asetukset)
          sammuta (hs/run-server
                    (-> reitit
                      wrap-json-response
                      wrap-keyword-params
                      wrap-json-params
                      (i18n/wrap-locale
                        :ei-redirectia #"/api.*"
                        :base-url (-> asetukset :server :base-url))
                      auth/wrap-sessionuser
                      log-request-wrapper
                      (auth-middleware asetukset)
                      wrap-multipart-params
                      wrap-params
                      (wrap-resource "public")
                      wrap-content-type
                      (wrap-frame-options :sameorigin)
                      (wrap-session {:store session-store
                                     :cookie-attrs {:http-only true
                                                    :path (service-path(get-in asetukset [:server :base-url]))
                                                    :secure (not (:development-mode asetukset))}})
                      (wrap-cas-single-sign-out session-store)
                      wrap-poikkeusten-logitus)
                    {:port (get-in asetukset [:server :port])
                     :max-body upload-limit
                     :thread (get-in asetukset [:server :pool-size])})]
      (json-gen/add-encoder org.joda.time.DateTime
        (fn [c json-generator]
          (.writeString json-generator (.toString c))))
      (json-gen/add-encoder org.joda.time.LocalDate
        (fn [c json-generator]
          (.writeString json-generator (.toString c "dd.MM.yyyy"))))
      (when (or (not (:development-mode asetukset))
                (:eraajo asetukset))
        (kaynnista-eraajon-ajastimet! asetukset))
      (log/info "Kehitysmoodi päällä:" (kehitysmoodi? asetukset))
      (log/info "Palvelin käynnistetty:" (service-url asetukset))
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
