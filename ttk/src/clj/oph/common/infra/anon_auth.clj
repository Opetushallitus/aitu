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

(ns oph.common.infra.anon-auth
  "Testitarkoituksia varten anonyymi-autentikaatiofiltteri, joka emuloi CAS-autentikaatiota"
  (:require [oph.korma.korma-auth :as ka]
            [clojure.tools.logging :as log]))

(defn auth-cas-user [ring-handler]
  (log/info "!! Anon auth enabled")
  (fn [request]
    (let [req (assoc request :username
                     (get (:headers request) "uid" ka/default-test-user-uid))
          _ (log/debug "authorized user " (:username req))]
      (ring-handler req))))

(defn ^:test-api enable-development-mode!
  []
  (log/info "!! Muutetaan JVM:n käyttämää SSL validointia testitarkoituksia varten!!")
  (let [old-config (oph.cas.util.DevelopmentSSLAuthUtil/enableUntrustedSSL)]
    (log/info "SSL ok")))

; TODO: defmacro tms. joka on tämän tyylinen olisi parempi tapa hoitaa asia..
; with-unsafe-ssl
;   (let [old-config (oph.cas.util.DevelopmentSSLAuthUtil/enableUntrustedSSLForLocalhostOnly)]
;     body
;     (oph.cas.util.DevelopmentSSLAuthUtil/swapSSLConfig old-config)
;

