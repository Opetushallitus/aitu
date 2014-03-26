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

(ns ttk-db.core-test
  (:import [java.net URLEncoder
                     URLDecoder])
  (:require [clojure.test :refer [deftest is are testing]]
            [ttk-db.core :refer [properties->jdbc-url parse-uri]]))

(def sample-properties
  (str "conan.origin = cimmerian\n"
       "db.host = 127.0.0.1\n"
       "db.port = 2345\n"
       "db.name = ttk\n"
       "db.user = ttk_adm\n"
       "db.password = ttk-adm\n"
       "db.application-user = ttk_user\n"
       "cas-auth-server.url = https://testi.virkailija.opintopolku.fi/cas\n"))

(def sample-properties-erikoismerkkeja
  (str "conan.origin = cimmerian\n"
       "db.host = 127.0.0.1\n"
       "db.port = 2345\n"
       "db.name = ttk\n"
       "db.user = ttk_käyttäjä$123\n"
       "db.password = ttk_$ylläpitäjä@:123\n"
       "db.application-user = ttk_user\n"
       "cas-auth-server.url = https://testi.virkailija.opintopolku.fi/cas\n"))

(deftest jdbc-url-parse
  (let [reader (java.io.StringReader. sample-properties)]
    (is (= "postgresql://ttk_adm:ttk-adm@127.0.0.1:2345/ttk" (properties->jdbc-url reader)))))

(deftest jdbc-url-parse-test-erikoismerkkeja
  (let [reader (java.io.StringReader. sample-properties-erikoismerkkeja)]
    (is (= (str "postgresql://ttk_k%C3%A4ytt%C3%A4j%C3%A4%24123:ttk_%24yll%C3%A4pit%C3%A4j%C3%A4%40%3A123@127.0.0.1:2345/ttk")
           (properties->jdbc-url reader)))))

(deftest parse-uri-test-erikoismerkkeja
  (let [reader (java.io.StringReader. sample-properties-erikoismerkkeja)
        tulos (parse-uri (properties->jdbc-url reader))]
    (are [keyword teksti] (= (keyword tulos) teksti)
         :user "ttk_käyttäjä$123"
         :passwd "ttk_$ylläpitäjä@:123"
         :postgre-uri "jdbc:postgresql://127.0.0.1:2345/ttk")))
