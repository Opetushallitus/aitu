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
            aitu.log
            [aitu.util :refer [pisteavaimet->puu
                              deep-merge
                              deep-update-vals
                              paths]])
  (:import [org.apache.log4j PropertyConfigurator]))

(def oletusasetukset
  {:server {:port "8080"
            :base-url "" ; http://localhost:8080
            :pool-size "4"}
   :db {:host "127.0.0.1"
        :port "2345"
        :name "ttk"
        :user "ttk_user"
        :password "ttk"
        :maximum-pool-size "15"
        :minimum-pool-size "3"}
   :cas-auth-server {:url "https://localhost:9443/cas-server-webapp-3.5.2"
                     :unsafe-https false
                     :enabled true}
   :ldap-auth-server {:host "localhost"
                      :port 10389
                      :user nil
                      :password nil}
   :koodistopalvelu {:url "https://virkailija.opintopolku.fi/koodisto-service/rest/json/"}
   :organisaatiopalvelu {:url "https://virkailija.opintopolku.fi/organisaatio-service/rest/organisaatio/"}
   :eraajo false
   :development-mode false ; oletusarvoisesti ei olla kehitysmoodissa. Pitää erikseen kääntää päälle jos tarvitsee kehitysmoodia.
   :ominaisuus {:proto false}
   :log4j {:properties-file "resources/log4j.properties" :refresh-interval 3000} ; päivitä log4j asetukset kerran kolmessa sekunnissa dynaamisesti
   })

(def build-id (delay (if-let [r (resource "build-id.txt")]
                       (.trim (slurp r))
                       "dev")))

(defn kehitysmoodi?
  [asetukset]
  (true? (:development-mode asetukset)))

(def konversio-map
  {"true" true})

(defn konvertoi-arvo
  [x]
  (get konversio-map x x))

(defn konfiguroi-lokitus
  "Konfiguroidaan log4j asetukset tiedostosta joka määritellään asetuksissa."
  [asetukset]
  (aitu.log/lisaa-uid-ja-requestid-hook)
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

(defn tarkista-avaimet
  [m]
  (let [vaarat-avaimet
        (clojure.set/difference (paths m) (paths oletusasetukset))]
    (assert (empty? vaarat-avaimet) (str "Viallisia avaimia asetuksissa: " vaarat-avaimet)))
  m)

(defn tulkitse-asetukset
  [property-map]
  (tarkista-avaimet
    (deep-update-vals konvertoi-arvo
       (->> property-map
          (into {})
          pisteavaimet->puu))))

(defn lue-asetukset
  ([oletukset] (lue-asetukset oletukset "ttk.properties"))
  ([oletukset polku]
    (->>
      (lue-asetukset-tiedostosta polku)
      (tulkitse-asetukset)
      (deep-merge oletukset)
      (deep-update-vals konvertoi-arvo)
      (tarkista-avaimet))))
