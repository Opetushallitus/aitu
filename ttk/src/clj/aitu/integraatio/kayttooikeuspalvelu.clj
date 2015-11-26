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
            [aitu.toimiala.kayttajaroolit :refer [kayttajaroolit]]))

(def aitu-organisaatio "1.2.246.562.10.2013121312395140176502")
(def toimikunnat-organisaatio "1.2.246.562.10.59113820717")
(def jarjesto-organisaatio "*")

(defn aitu-ryhma-cn [organisaatio nimi]
  (str "APP_AITU_" nimi "_" organisaatio))

(def roolin-ryhma-cnt
  {(:yllapitaja kayttajaroolit) [(aitu-ryhma-cn aitu-organisaatio "CRUD")]
   (:kayttaja kayttajaroolit) [(aitu-ryhma-cn toimikunnat-organisaatio "READ_UPDATE")
                               (aitu-ryhma-cn aitu-organisaatio "READ_UPDATE")]
   (:osoitepalvelu kayttajaroolit) [(aitu-ryhma-cn aitu-organisaatio "OSOITEPALVELU")]
   (:oph-katselija kayttajaroolit) [(aitu-ryhma-cn aitu-organisaatio "READ")]
   (:aipal kayttajaroolit) [(aitu-ryhma-cn aitu-organisaatio "AIPALREAD")]
   (:jarjesto kayttajaroolit) [(aitu-ryhma-cn jarjesto-organisaatio "TYOELAMAJARJESTO")]})


(defn ryhma-cn-filter [ryhma-cn]
  {:filter (str "cn=" ryhma-cn)})

(def ryhma-base "ou=Groups,dc=opintopolku,dc=fi")

(defn kayttajat [kayttooikeuspalvelu rooli oid->jarjesto-id]
  {:pre [(contains? roolin-ryhma-cnt rooli)]}
  (with-open [yhteys (kayttooikeuspalvelu)]
    (apply concat (for [cn (roolin-ryhma-cnt rooli)
                        :let [cn-filter (ryhma-cn-filter cn)]]
                    (if-let [ryhmat (ldap/search yhteys ryhma-base cn-filter)]
                      (doall (for [ryhma ryhmat
                                   :let [jarjesto-oid (last (s/split (:cn ryhma) #"_"))
                                         kayttaja-dnt (:uniqueMember ryhma)
                                         ;; Jos ryhmällä on vain yksi uniqueMember-attribuutti, clj-ldap
                                         ;; palauttaa arvon (stringin) eikä vektoria arvoista.
                                         kayttaja-dnt (if (string? kayttaja-dnt)
                                                        [kayttaja-dnt]
                                                        kayttaja-dnt)]
                                   kayttaja-dn kayttaja-dnt
                                   :let [kayttaja (ldap/get yhteys kayttaja-dn)]
                                   :when kayttaja
                                   ;; Järjestökäyttäjällä on oltava järjestö
                                   :when (or (not= rooli (:jarjesto kayttajaroolit))
                                             (oid->jarjesto-id jarjesto-oid))
                                   :let [[etunimi toinennimi] (s/split (:cn kayttaja) #" ")
                                         sukunimi (:sn kayttaja)]]
                               {:oid (:employeeNumber kayttaja)
                                :uid (:uid kayttaja)
                                :etunimi etunimi
                                :sukunimi (or sukunimi "")
                                :rooli rooli
                                :jarjesto (oid->jarjesto-id jarjesto-oid)}))
                      (log/warn "Roolin" rooli "ryhmää" cn
                                "ei löytynyt, ei lueta roolin käyttäjiä"))))))

(defn tee-kayttooikeuspalvelu [ldap-auth-server-asetukset]
  (fn []
    (let [{:keys [host port user password ssl]} ldap-auth-server-asetukset
          asetukset (merge {:host (str host ":" port)
                            :ssl? ssl}
                           (when user {:bind-dn user})
                           (when password {:password password}))]
      (ldap/connect asetukset))))
