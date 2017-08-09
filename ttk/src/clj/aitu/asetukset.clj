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
            [schema.coerce :as sc]
            [clj-time.local :as time-local]))

(def asetukset (promise))

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
                :clamav {:host s/Str
                         :port s/Int}
                :session-timeout s/Int
                :cas-auth-server {:url s/Str
                                  :unsafe-https Boolean
                                  :enabled Boolean}
                :ldap-auth-server {:host s/Str
                                   :port s/Int
                                   :user (s/maybe s/Str)
                                   :password (s/maybe s/Str)
                                   :ssl Boolean}
                :eperusteet-palvelu {:url s/Str}
                :koodistopalvelu {:url s/Str}
                :organisaatiopalvelu {:url s/Str}
                :eraajo Boolean
                :development-mode Boolean
                :ominaisuus {:proto Boolean}
                :logback {:properties-file s/Str}
                :ajastus {:organisaatiopalvelu s/Str
                          :kayttooikeuspalvelu s/Str
                          :sopimusten-voimassaolo s/Str
                          :tutkinnot s/Str
                          :eperusteet s/Str}
                :paatospohja-oletukset {:esittelijan_asema s/Str
                                        :esittelija s/Str
                                        :hyvaksyjan_asema s/Str
                                        :hyvaksyja s/Str
                                        :jakelu s/Str
                                        :paatosteksti s/Str}})

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
                         :clamav {:host "localhost"
                                  :port 8080}
                         :session-timeout (* 8 60 60)
                         :cas-auth-server {:url "https://localhost:9443/cas-server-webapp-3.5.2"
                                           :unsafe-https false
                                           :enabled true}
                         :ldap-auth-server {:host "localhost"
                                            :port 10389
                                            :user "cn=aituserv,ou=People,dc=opintopolku,dc=fi"
                                            :password "salasana"
                                            :ssl false}
                         :eperusteet-palvelu {:url "https://virkailija.opintopolku.fi/eperusteet-service/"}
                         :koodistopalvelu {:url "https://virkailija.opintopolku.fi/koodisto-service/rest/json/"}
                         :organisaatiopalvelu {:url "https://virkailija.opintopolku.fi/organisaatio-service/rest/organisaatio/"}
                         :eraajo false
                         :development-mode false ; oletusarvoisesti ei olla kehitysmoodissa. Pitää erikseen kääntää päälle jos tarvitsee kehitysmoodia.
                         :ominaisuus {:proto false}
                         :logback {:properties-file "resources/logback.xml"}
                         :ajastus {:organisaatiopalvelu "0 0 3 * * ?" ;; Joka päivä klo 03:00
                                   :kayttooikeuspalvelu "0 0/5 * * * ?" ;; Viiden minuutin välein
                                   :sopimusten-voimassaolo "0 0 4 * * ?" ;; Joka päivä klo 04:00
                                   :tutkinnot "0 0 5 * * ?" ;; Joka päivä klo 05:00
                                   :eperusteet "0 0 6 * * ?" ;; Joka päivä klo 06:00
                                   }
                         :paatospohja-oletukset {:esittelijan_asema ""
                                                 :esittelija ""
                                                 :hyvaksyjan_asema ""
                                                 :hyvaksyja ""
                                                 :jakelu ""
                                                 :paatosteksti ""}}))

(def common-audit-log-asetukset {:boot-time        (time-local/local-now)
                                 :hostname         "localhost"
                                 :service-name     "aitu"
                                 :application-type "virkailija"})

(def build-id (delay (if-let [r (resource "build-id.txt")]
                       (.trim (slurp r :encoding "UTF-8"))
                       "dev")))

(defn kehitysmoodi?
  [asetukset]
  (true? (:development-mode asetukset)))

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

(defn service-path [base-url]
  (let [path (drop 3 (clojure.string/split base-url #"/"))]
    (str "/" (clojure.string/join "/" path))))
