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
  (select-keys peruste [:peruste :voimassa_alkupvm :voimassa_loppupvm :siirtymaajan_loppupvm]))

(defn hae-muutokset [viimeisin-haku asetukset]
  (let [uudet (eperusteet/hae-perusteet viimeisin-haku asetukset)]
    (for [[tutkintotunnus peruste] uudet
          :let [diaarinumero (:diaarinumero peruste)
                tutkinto (tutkinto-arkisto/hae-tutkinto tutkintotunnus)
                vanha-peruste (tutkinto-arkisto/hae-peruste diaarinumero)
                peruste (valitse-perusteen-kentat (assoc peruste :peruste diaarinumero))]
          :when (and tutkinto
                     (not= peruste (valitse-perusteen-kentat vanha-peruste)))]
      (assoc peruste :tutkintotunnus (:tutkintotunnus tutkinto)))))


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
            muutokset (hae-muutokset viimeisin-haku asetukset)]
        (doseq [tutkinto muutokset]
          (log/info "Päivitetään tutkinto" (:tutkintotunnus tutkinto) (:peruste tutkinto))
          (tutkinto-arkisto/paivita-tutkinto! tutkinto))
        (tutkinto-arkisto/tallenna-viimeisin-eperusteet-paivitys! nyt))
      (log/info "Tutkintojen perusteiden päivitys valmis"))))

;; Cloverage ei tykkää `defrecord`eja generoivista makroista, joten hoidetaan
;; `defjob`:n homma käsin.
(defrecord PaivitaPerusteetJob []
  org.quartz.Job
  (execute [this ctx]
    (let [{asetukset "asetukset"} (qc/from-job-data ctx)]
      (paivita-perusteet! (clojure.walk/keywordize-keys asetukset)))))
