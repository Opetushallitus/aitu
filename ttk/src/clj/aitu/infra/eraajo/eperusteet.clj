;; Copyright (c) 2015 The Finnish National Board of Education - Opetushallitus
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

(ns aitu.infra.eraajo.eperusteet
  (:require [clj-time.core :as time]
            [clojurewerkz.quartzite.conversion :as qc]
            [clojure.tools.logging :as log]
            [korma.db :as db]
            [aitu.infra.tutkinto-arkisto :as tutkinto-arkisto]
            [aitu.integraatio.eperusteet :as eperusteet]
            [oph.korma.korma-auth
             :refer [*current-user-uid* *current-user-oid* integraatiokayttaja]]))

(defn valitse-perusteen-kentat [peruste]
  (select-keys peruste [:peruste :eperustetunnus :voimassa_alkupvm :voimassa_loppupvm :siirtymaajan_loppupvm]))

(defn muuttuneet-perusteet [perusteet]
  (for [[tutkintotunnus peruste] perusteet
        :let [diaarinumero (:diaarinumero peruste)
              tutkinto (tutkinto-arkisto/hae-tutkinto tutkintotunnus)
              vanha-peruste (tutkinto-arkisto/hae-tutkintoversio-perusteella tutkintotunnus diaarinumero (:eperustetunnus peruste))
              peruste (valitse-perusteen-kentat (assoc peruste :peruste diaarinumero))]
        :when (and tutkinto
                   (not= peruste (valitse-perusteen-kentat vanha-peruste)))]
    (assoc peruste :tutkintotunnus (:tutkintotunnus tutkinto))))

(defn ^:integration-api paivita-perusteet! [asetukset]
  (binding [*current-user-uid* integraatiokayttaja
            ;; Tietokantayhteyden avaus asettaa *current-user-oid*-promisen
            ;; arvon. Kun käsitellään HTTP-pyyntöä, auth-wrapper luo tämän
            ;; promisen. Koska tätä funktiota ei kutsuta HTTP-pyynnön
            ;; käsittelijästä, meidän täytyy luoda promise itse.
            *current-user-oid* (promise)]
    (db/transaction
      (log/info "Päivitetään tutkintojen perusteet ePerusteet-järjestelmästä")
      (let [nyt (time/now)
            viimeisin-haku (tutkinto-arkisto/hae-viimeisin-eperusteet-paivitys)
            perusteet (eperusteet/hae-perusteet viimeisin-haku asetukset)
            muuttuneet (muuttuneet-perusteet perusteet)]
        
        (doseq [tutkinto muuttuneet]
          (log/info "Päivitetään tutkinto" (:tutkintotunnus tutkinto) (:peruste tutkinto))
          (tutkinto-arkisto/paivita-tutkinto! tutkinto))
        (log/info (str "Tutkinnot päivitetty, " (count muuttuneet) " kpl ,  päivitetään osat ja osaamisalat"))
        (doseq [[_ peruste] perusteet]
          (tutkinto-arkisto/paivita-tutkinnonosat! peruste)
          (tutkinto-arkisto/paivita-osaamisalat! peruste))
        (tutkinto-arkisto/tallenna-viimeisin-eperusteet-paivitys! nyt)
        (log/info "Tutkintojen perusteiden päivitys valmis")))))

;; Cloverage ei tykkää `defrecord`eja generoivista makroista, joten hoidetaan
;; `defjob`:n homma käsin.
(defrecord PaivitaPerusteetJob []
  org.quartz.Job
  (execute [this ctx]
    (let [{asetukset "asetukset"} (qc/from-job-data ctx)]
      (paivita-perusteet! (clojure.walk/keywordize-keys asetukset)))))
