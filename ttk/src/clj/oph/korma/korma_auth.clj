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
  "SQL Kormalle oma kantayhteyksien hallinta. Sitoo kantayhteyteen sisäänkirjautuneen käyttäjän. BoneCP pool."
  (:require [clojure.tools.logging :as log]))

(def jarjestelmakayttaja "JARJESTELMA")
(def integraatiokayttaja "INTEGRAATIO")
(def default-test-user-oid "OID.T-1001")
(def default-test-user-uid "T-1001")
(def ^:private psql-varname "aitu.kayttaja")
(def ^:dynamic *current-user-uid*)
(def ^:dynamic *current-user-oid*)

(defn validate-user
  [con uid]
  {:pre [(string? uid)]}
  (log/debug "tarkistetaan käyttäjä " uid)
  (with-open [pstmt (doto
                      (.prepareStatement con "select * from kayttaja where uid = ?")
                      (.setString 1 uid))
              rs (.executeQuery pstmt)]
    (let [valid (.next rs)]
      (when-not valid (throw (IllegalArgumentException. (str "Käyttäjä " uid " puuttuu tietokannasta"))))
     (let [voimassa (.getBoolean rs "voimassa")
           rooli (.getString rs "rooli")
           oid (.getString rs "oid")]
       (when-not voimassa (throw (IllegalArgumentException. (str "Käyttäjätunnus " uid " ei ole voimassa."))))
       (log/debug (str "user " uid " ok. Rooli " rooli ))
       oid))))

(defn exec-sql
  "execute sql and close statement."
  [c sql]
  (with-open [stm (.createStatement c)]
    (.execute stm sql)))

(defn auth-onCheckOut
  [c]
  (log/debug "auth user " *current-user-uid*)
  (try
    (let [oid (validate-user c *current-user-uid*)]
      (deliver *current-user-oid* oid)
      (exec-sql c (str "set session " psql-varname " = '" oid "'")))
    (catch IllegalArgumentException e
      (.printStackTrace e)))
  (log/debug "con ok" (.hashCode c)))
    
(defn auth-onCheckIn
  [c]
  (log/debug "connection release ")
  (exec-sql c (str "SET " psql-varname " TO DEFAULT"))
  (log/debug "con release ok" (.hashCode c)))

(defonce customizer-impl-bonecp
  (proxy [com.jolbox.bonecp.hooks.AbstractConnectionHook] []
    (onCheckIn [c]
      (auth-onCheckIn c))
    (onCheckOut [c]
      (auth-onCheckOut c))))
