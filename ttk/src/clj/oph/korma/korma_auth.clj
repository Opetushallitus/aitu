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

(ns oph.korma.korma-auth
  "SQL Kormalle oma kantayhteyksien hallinta. Sitoo kantayhteyteen sisäänkirjautuneen käyttäjän."
  (:require [clojure.tools.logging :as log]
            [aitu.toimiala.kayttajaoikeudet :as ko]))

(def jarjestelmakayttaja "JARJESTELMA")
(def integraatiokayttaja "INTEGRAATIO")
(def default-test-user-oid "OID.T-1001")
(def default-test-user-uid "T-1001")
(def ^:private psql-varname "aitu.kayttaja")
(def ^:dynamic *current-user-uid*)
(def ^:dynamic *current-user-oid*)

(defn exception-as-error
  "Muuttaa poikkeukset (Exception) erroreiksi (Error) funktiokutsun aikana.
  Korman käyttämässä C3P0 connection poolissa on vika jonka takia poikkeukset johtavat ikuiseen silmukkaan. Error menee läpi.
  Kts. http://sourceforge.net/p/c3p0/bugs/100/"
  [f]
  (try
    (f)
    (catch Exception e
      (throw (Error. (.getMessage e) e)))))

(defn validate-user
  [con uid]
  {:pre [(string? uid)]}
  (log/debug "tarkistetaan käyttäjä " uid)
  (let [pstmt (doto 
                (.prepareStatement con "select * from kayttaja where uid = ?")
                (.setString 1 uid))
        rs (.executeQuery pstmt)
        valid (.next rs)]
    (when-not valid (throw (IllegalArgumentException. (str "Käyttäjä " uid " puuttuu tietokannasta"))))
    (let [voimassa (.getBoolean rs "voimassa")
          rooli (.getString rs "rooli")
          oid (.getString rs "oid")]
      (when-not voimassa (throw (IllegalArgumentException. (str "Käyttäjätunnus " uid " ei ole voimassa."))))
      (log/debug (str "user " uid " ok. Rooli " rooli ))
      oid)))

(defn auth-onCheckOut
  [this c s]
  (exception-as-error
    #(do
       (log/debug "auth user " *current-user-uid*)
       (let [oid (validate-user c *current-user-uid*)]
         (deliver *current-user-oid* oid)
         (doto
           (.createStatement c)
           (.execute (str "set session " psql-varname " = '" oid "'"))))
       (log/debug "con ok" (.hashCode c)))))
    
(defn auth-onCheckIn
  [this c s]
  (exception-as-error
    #(do
       (log/debug "connection release ")
       (doto
         (.createStatement c)
         (.execute (str "SET " psql-varname " TO DEFAULT")))
       (log/debug "con release ok" (.hashCode c)))))

(defonce customizer-impl
  (oph.korma.auth.C3P0NamedCustomizer/setImpl
    (reify com.mchange.v2.c3p0.ConnectionCustomizer
      (onCheckIn [this c s]
        (auth-onCheckIn this c s))
      (onCheckOut [this c s]
        (auth-onCheckOut this c s)))))
