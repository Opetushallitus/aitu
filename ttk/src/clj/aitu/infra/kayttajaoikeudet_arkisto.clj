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

(ns aitu.infra.kayttajaoikeudet-arkisto
  (:require [korma.db :as db]
            [korma.core :as sql]
            [clojure.tools.logging :as log]
            [oph.korma.korma-auth :refer [*current-user-oid*]]
            [aitu.infra.kayttaja-arkisto :as kayttaja-arkisto])
  (:use [aitu.integraatio.sql.korma]))

(defn hae-jasenyys-ja-sopimukset [kayttajaid]
  (first (sql/select henkilo
           (sql/fields :henkiloid :kayttaja_oid)
           (sql/with jasenyys
             (sql/fields :alkupvm :loppupvm)
             (sql/with tutkintotoimikunta
               (sql/fields :diaarinumero :tkunta) 
                (sql/with jarjestamissopimus
                  (sql/fields :jarjestamissopimusid))
               ))
           (sql/where {:kayttaja_oid kayttajaid}))))
  
(defn hae-oikeudet
  ([oid] "Hakee oikeudet kuvaavan tietorakenteen käyttäjätunnuksen perusteella."
    (db/transaction
      (let [kayttaja (kayttaja-arkisto/hae oid)
            oikeudet (hae-jasenyys-ja-sopimukset oid)
            _ (log/debug "kontekstioikeuksia on .. " oikeudet)]
        {:oid oid
         :kayttajan_nimi (str (:etunimi kayttaja) " " (:sukunimi kayttaja))
         :henkiloid (:henkiloid oikeudet)
         :roolitunnus (:rooli kayttaja)
         :toimikunta_jasen (set (map :tkunta (:jasenyys oikeudet)))})))
  ([] "Hakee sisäänkirjautuneen käyttäjän oikeudet"
    (db/transaction
      (assert (realized? *current-user-oid*) "Ongelma sisäänkirjautumisessa. uid -> oid mappays epäonnistui.")
      (hae-oikeudet @*current-user-oid*))))

