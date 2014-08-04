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

(ns aitu.asetukset
  (:require [clojure.java.io :refer [file resource]]
            clojure.set
            [clojure.tools.logging :as log]
            oph.log
            [oph.common.util.util :refer [pisteavaimet->puu
                              deep-merge
                              deep-update-vals
                              paths]]
            [aitu.util :refer [kaikki-optional]]
            [schema.core :as s]
            [schema.coerce :as sc])
  (:import [org.apache.log4j PropertyConfigurator]))

(def Asetukset {:server {:port s/Int
                         :base-url s/Str
                         :pool-size s/Int}
                :db {:host s/Str
                     :port s/Int
                     :name s/Str
                     :user s/Str
                     :password s/Str
                     :maximum-pool-size s/Int
                     :minimum-pool-size s/Int}
                :session-timeout s/Int
                :cas-auth-server {:url s/Str
                                  :unsafe-https Boolean
                                  :enabled Boolean}
                :ldap-auth-server {:host s/Str
                                   :port s/Int
                                   :user (s/maybe s/Str)
                                   :password (s/maybe s/Str)}
                :koodistopalvelu {:url s/Str}
                :organisaatiopalvelu {:url s/Str}
                :eraajo Boolean
                :development-mode Boolean
                :ominaisuus {:proto Boolean}
                :log4j {:properties-file s/Str
                        :refresh-interval s/Int}})

(defn string->boolean [x]
  (case x
    "true" true
    "false" false
    x))

(defn string-coercion-matcher [schema]
  (or (sc/string-coercion-matcher schema)
      ({Boolean string->boolean} schema)))

(defn coerce-asetukset [asetukset]
  ((sc/coercer Asetukset string-coercion-matcher) asetukset))

(def oletusasetukset
  (s/validate Asetukset {:server {:port 8080
                                  :base-url "" ; http://localhost:8080
                                  :pool-size 4}
                         :db {:host "127.0.0.1"
                              :port 2345
                              :name "ttk"
                              :user "ttk_user"
                              :password "ttk"
                              :maximum-pool-size 15
                              :minimum-pool-size 3}
                         :session-timeout (* 8 60 60)
                         :cas-auth-server {:url "https://localhost:9443/cas-server-webapp-3.5.2"
                                           :unsafe-https false
                                           :enabled true}
                         :ldap-auth-server {:host "localhost"
                                            :port 10389
                                            :user "cn=aituserv,ou=People,dc=opintopolku,dc=fi"
                                            :password "salasana"}
                         :koodistopalvelu {:url "https://virkailija.opintopolku.fi/koodisto-service/rest/json/"}
                         :organisaatiopalvelu {:url "https://virkailija.opintopolku.fi/organisaatio-service/rest/organisaatio/"}
                         :eraajo false
                         :development-mode false ; oletusarvoisesti ei olla kehitysmoodissa. Pitää erikseen kääntää päälle jos tarvitsee kehitysmoodia.
                         :ominaisuus {:proto false}
                         :log4j {:properties-file "resources/log4j.properties" :refresh-interval 3000} ; päivitä log4j asetukset kerran kolmessa sekunnissa dynaamisesti
                         }))

(def build-id (delay (if-let [r (resource "build-id.txt")]
                       (.trim (slurp r))
                       "dev")))

(defn kehitysmoodi?
  [asetukset]
  (true? (:development-mode asetukset)))

(defn konfiguroi-lokitus
  "Konfiguroidaan log4j asetukset tiedostosta joka määritellään asetuksissa."
  [asetukset]
  (oph.log/lisaa-uid-ja-requestid-hook)
  (let [filepath (:properties-file (:log4j asetukset))
        refresh (:refresh-interval (:log4j asetukset))
        log4j-configfile (file filepath)
        cfpath (.getAbsolutePath log4j-configfile)]
    (log/info "log4j configuration reset. Watch " cfpath " interval " refresh)
    (PropertyConfigurator/configure cfpath)
    (PropertyConfigurator/configureAndWatch cfpath refresh)))

(defn lue-asetukset-tiedostosta
  [polku]
  (try
    (with-open [reader (clojure.java.io/reader polku)]
      (doto (java.util.Properties.)
        (.load reader)))
    (catch java.io.FileNotFoundException _
      (log/info "Asetustiedostoa ei löydy. Käytetään oletusasetuksia")
      {})))

(defn tulkitse-asetukset
  [property-map]
  (->> property-map
     (into {})
     pisteavaimet->puu))

(defn lue-asetukset
  ([oletukset] (lue-asetukset oletukset "ttk.properties"))
  ([oletukset polku]
    (->>
      (lue-asetukset-tiedostosta polku)
      (tulkitse-asetukset)
      (deep-merge oletukset)
      (coerce-asetukset))))
