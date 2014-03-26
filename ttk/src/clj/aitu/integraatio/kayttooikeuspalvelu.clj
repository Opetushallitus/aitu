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

(ns aitu.integraatio.kayttooikeuspalvelu
  (:require [clojure.string :as s]
            [clojure.tools.logging :as log]
            [clj-ldap.client :as ldap]
            [aitu.toimiala.kayttajaoikeudet
             :refer [yllapitajarooli kayttajarooli]]))

(def aitu-organisaatio "1.2.246.562.10.2013121312395140176502")

(defn aitu-ryhma-cn [nimi]
  (str "APP_AITU_" nimi "_" aitu-organisaatio))

(def roolin-ryhma-cn
  {yllapitajarooli (aitu-ryhma-cn "CRUD")
   kayttajarooli (aitu-ryhma-cn "UPDATE")})

(defn ryhma-dn [ryhma-cn]
  (str "cn=" ryhma-cn ",ou=groups,dc=example,dc=com"))

(defn henkilo-dn [oid]
  (str "uid=" oid ",ou=people,dc=example,dc=com"))

(defn kayttajat [kayttooikeuspalvelu rooli]
  {:pre [(contains? roolin-ryhma-cn rooli)]}
  (let [yhteys (kayttooikeuspalvelu)
        roolin-ryhma-dn (ryhma-dn (roolin-ryhma-cn rooli))]
    (try
      (if-let [ryhma (ldap/get yhteys roolin-ryhma-dn)]
        (let [kayttaja-dnt (:uniqueMember ryhma)
              ;; Jos ryhmällä on vain yksi uniqueMember-attribuutti, clj-ldap
              ;; palauttaa arvon (stringin) eikä vektoria arvoista.
              kayttaja-dnt (if (string? kayttaja-dnt)
                             [kayttaja-dnt]
                             kayttaja-dnt)]
          (doall
            (for [kayttaja-dn kayttaja-dnt
                  :let [kayttaja (ldap/get yhteys kayttaja-dn)
                        _ (assert kayttaja)
                        [etunimi sukunimi] (s/split (:givenName kayttaja) #" ")]]
              {:oid (:employeeNumber kayttaja)
               :uid (:uid kayttaja)
               :etunimi etunimi
               :sukunimi (or sukunimi "")
               :rooli rooli})))
        (log/warn "Roolin" rooli "ryhmää" roolin-ryhma-dn
                  "ei löytynyt, ei lueta roolin käyttäjiä"))
      (finally
        (.close yhteys)))))

(defn tee-kayttooikeuspalvelu [ldap-auth-server-asetukset]
  (fn []
    (let [{:keys [host port]} ldap-auth-server-asetukset]
      (ldap/connect {:host (str host ":" port)}))))
