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
            [oph.korma.korma-auth :as ka]
            [oph.korma.korma-auth :refer [*current-user-oid*]]
            [aitu.infra.kayttaja-arkisto :as kayttaja-arkisto]
            [aitu.toimiala.kayttajaoikeudet :as ko]
            [aitu.integraatio.sql.korma :refer :all]))

; TODO: miksi first? Eihän tämä nyt voi toimia oikein??
(defn hae-jasenyys-ja-sopimukset [kayttajaid]
  (first (sql/select henkilo
           (sql/fields :henkiloid :kayttaja_oid)
           (sql/with jasenyys
             (sql/fields :alkupvm :loppupvm :rooli)
             (sql/with tutkintotoimikunta
               (sql/fields :diaarinumero :tkunta :toimikausi_alku :toimikausi_loppu)
                (sql/with jarjestamissopimus
                  (sql/fields :jarjestamissopimusid))
               ))
           (sql/where {:kayttaja_oid kayttajaid}))))

(defn impersonoitu-kayttaja
  "tarvittaessa suorittaa ylläpitäjätason käyttäjälle impersonaation"
  [alkuperainen-kayttaja]
  (if (and (ko/yllapitajarooli? (:rooli alkuperainen-kayttaja))
           ko/*impersonoitu-oid*)
    (kayttaja-arkisto/hae ko/*impersonoitu-oid*)
    alkuperainen-kayttaja))

(defn hae-oikeudet
  ([oid] "Hakee oikeudet kuvaavan tietorakenteen käyttäjätunnuksen tai impersonoidun käyttäjätunnuksen perusteella."
    (db/transaction
      (let [alkuperainen-kayttaja (kayttaja-arkisto/hae oid)
            kayttaja (impersonoitu-kayttaja alkuperainen-kayttaja)
            oikeudet (hae-jasenyys-ja-sopimukset (:oid kayttaja))
            jasenyys (:jasenyys oikeudet)
            _ (log/debug "kontekstioikeuksia on .. " oikeudet)]
        {:oid (:oid kayttaja)
         :kayttajan_nimi (str (:etunimi kayttaja) " " (:sukunimi kayttaja))
         :henkiloid (:henkiloid oikeudet)
         :roolitunnus (:rooli kayttaja)
         :toimikunta (set (map #(select-keys % [:tkunta
                                                :rooli
                                                :alkupvm
                                                :loppupvm
                                                :toimikausi_alku
                                                :toimikausi_loppu]) jasenyys))
         :jarjesto (:jarjesto kayttaja)
         :jarjestamissopimus (set (flatten (map :jarjestamissopimus jasenyys)))})))
  ([] "Hakee sisäänkirjautuneen käyttäjän oikeudet"
    (db/transaction
      (let [userid ka/*current-user-uid*]
        (assert (realized? *current-user-oid*) (str "Ongelma sisäänkirjautumisessa. Käyttäjätunnuksella " userid " ei ole käyttöoikeuksia. (uid -> oid epäonnistui)."))
        (hae-oikeudet @*current-user-oid*)))))

