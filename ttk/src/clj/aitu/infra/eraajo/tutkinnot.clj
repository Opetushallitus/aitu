(ns aitu.infra.eraajo.tutkinnot
  (:require [aitu.integraatio.koodistopalvelu :as koodisto]
            [clojurewerkz.quartzite.jobs :as j
             :refer [defjob]]
            [clojurewerkz.quartzite.conversion :as qc]
            [clojure.tools.logging :as log]
            [oph.korma.korma-auth
             :refer [*current-user-uid* *current-user-oid* integraatiokayttaja]]
            [aitu.infra.tutkinto-arkisto :as tutkinto-arkisto]
            [aitu.infra.opintoala-arkisto :as opintoala-arkisto]
            [aitu.infra.koulutusala-arkisto :as koulutusala-arkisto]
            [oph.common.util.util :refer [map-by remove-nil-vals]]
            [korma.db :as db]))

(defn uusi
  "Jos muutos on uuden tiedon lisääminen, palauttaa uudet tiedot, muuten nil"
  [[tutkintotunnus muutos]]
  (when (vector? muutos)
    (assoc (first muutos) :tutkintotunnus tutkintotunnus)))

(defn uudet-arvot [muutos]
  (into {}
        (for [[k v] muutos
             :when v]
         [k (first v)])))

(defn muuttunut
  "Jos muutos on tietojen muuttuminen, palauttaa muuttuneet tiedot, muuten nil"
  [[tutkintotunnus muutos]]
  (when (and (map? muutos)
             (not-every? nil? (vals muutos)))
    (merge {:tutkintotunnus tutkintotunnus}
          (uudet-arvot muutos))))

(defn ^:integration-api tallenna-uudet-opintoalat! [opintoalat]
  (doseq [ala opintoalat]
    (log/info "Lisätään opintoala " (:opintoala_tkkoodi ala))
    (opintoala-arkisto/lisaa! ala)))

(defn ^:integration-api tallenna-muuttuneet-opintoalat! [opintoalat]
  (doseq [ala opintoalat]
    (log/info "Päivitetään opintoala " (:opintoala_tkkoodi ala) ", muutokset: " (dissoc ala :opintoala_tkkoodi))
    (opintoala-arkisto/paivita! ala)))

(defn nimi->selite [ala]
  (clojure.set/rename-keys ala {:nimi_fi :selite_fi
                                :nimi_sv :selite_sv}))

(defn ^:integration-api tallenna-opintoalat! [opintoalat]
  (let [uudet (for [[alakoodi ala] opintoalat
                    :when (and (vector? ala) (first ala))]
                (nimi->selite (first ala)))
        muuttuneet (for [[alakoodi ala] opintoalat
                         :when (map? ala)]
                     (nimi->selite (uudet-arvot ala)))]
    (tallenna-uudet-opintoalat! uudet)
    (tallenna-muuttuneet-opintoalat! muuttuneet)))

(defn ^:integration-api tallenna-uudet-koulutusalat! [koulutusalat]
  (doseq [ala koulutusalat]
    (log/info "Lisätään koulutusala " (:koulutusala_tkkoodi ala))
    (koulutusala-arkisto/lisaa! ala)))

(defn ^:integration-api tallenna-muuttuneet-koulutusalat! [koulutusalat]
  (doseq [ala koulutusalat]
    (log/info "Päivitetään koulutusala " (:koulutusala_tkkoodi ala) ", muutokset: " (dissoc ala :koulutusala_tkkoodi))
    (koulutusala-arkisto/paivita! ala)))

(defn ^:integration-api tallenna-koulutusalat! [koulutusalat]
  (let [uudet (for [[alakoodi ala] koulutusalat
                    :when (and (vector? ala) (first ala))]
                (nimi->selite (first ala)))
        muuttuneet (for [[alakoodi ala] koulutusalat
                         :when (map? ala)]
                     (nimi->selite (uudet-arvot ala)))]
    (tallenna-uudet-koulutusalat! uudet)
    (tallenna-muuttuneet-koulutusalat! muuttuneet)))

(defn ^:integration-api tallenna-uudet-tutkintonimikkeet! [tutkintoversio-id tutkintonimikkeet]
  (doseq [tutkintonimike tutkintonimikkeet]
    (log/info "Lisätään tutkintonimike " (:nimiketunnus tutkintonimike))
    (tutkinto-arkisto/lisaa-tai-paivita-tutkintonimike! tutkintoversio-id tutkintonimike)))

(defn ^:integration-api tallenna-muuttuneet-tutkintonimikkeet! [tutkintoversio-id tutkintonimikkeet]
  (doseq [tutkintonimike tutkintonimikkeet]
    (log/info "Päivitetään tutkintonimike " (:nimiketunnus tutkintonimike) ", muutokset " (dissoc tutkintonimike :nimiketunnus))
    (tutkinto-arkisto/lisaa-tai-paivita-tutkintonimike! tutkintoversio-id tutkintonimike)))

(defn ^:integration-api tallenna-tutkintonimikkeet! [tutkintoversio-id tutkintonimikkeet]
  (when (seq tutkintonimikkeet)
    (tutkinto-arkisto/poista-tutkinnon-tutkintonimikkeet! tutkintoversio-id))
  (let [uudet (for [nimike tutkintonimikkeet
                    :when (vector? nimike)]
                (first nimike))
        muuttuneet (for [nimike tutkintonimikkeet
                         :when (map? nimike)]
                     (uudet-arvot nimike))]
    (tallenna-uudet-tutkintonimikkeet! tutkintoversio-id uudet)
    (tallenna-muuttuneet-tutkintonimikkeet! tutkintoversio-id muuttuneet)))

(defn ^:integration-api paivita-tutkinto! [tutkinto]
  (let [tutkintotunnus (:tutkintotunnus tutkinto)
        tutkintotiedot (remove-nil-vals (select-keys tutkinto [:nimi_fi :nimi_sv :tyyppi :tutkintotaso :opintoala]))]
    (when (not-every? nil? (vals tutkintotiedot))
      (tutkinto-arkisto/paivita! tutkintotunnus tutkintotiedot))
    (:tutkintoversio_id (tutkinto-arkisto/hae-tutkinto (:tutkintotunnus tutkinto)))))

(defn ^:integration-api paivita-tutkinnot! [tutkintomuutokset]
  (let [opintoalat (set (map :opintoala_tkkoodi (opintoala-arkisto/hae-kaikki)))
        {:keys [tutkinnot tutkintonimikkeet]} tutkintomuutokset]
    (doseq [t (keep uusi tutkinnot)]
      (if-not (contains? opintoalat (:opintoala t))
        (log/warn "Tutkinnolla" (:tutkintotunnus t) (or (:nimi_fi t) (:nimi_sv t)) "ei ole opintoalaa")
        (do
          (log/info "Lisätään tutkinto " (:tutkintotunnus t))
          (let [tutkintoversio-id (tutkinto-arkisto/lisaa-tutkinto-ja-versio! (assoc (dissoc t :osaamisalat :tutkinnonosat)
                                                                                     :versio 1))
                tutkintonimikkeet (map tutkintonimikkeet (:tutkintonimikkeet t))]
            (tallenna-tutkintonimikkeet! tutkintoversio-id tutkintonimikkeet)))))
    (doseq [t (keep muuttunut tutkinnot)]
      #_(log/info "Päivitetään tutkinto " (:tutkintotunnus t) ", muutokset: " (dissoc t :tutkintotunnus))
      (let [tutkintoversio-id (paivita-tutkinto! t)
            tutkintonimikkeet (map tutkintonimikkeet (:tutkintonimikkeet t))]
        (tallenna-tutkintonimikkeet! tutkintoversio-id tutkintonimikkeet)))))

(defn ^:integration-api paivita-tutkinnot-koodistopalvelusta! [asetukset]
  (try
    (binding [*current-user-uid* integraatiokayttaja
            ;; Tietokantayhteyden avaus asettaa *current-user-oid*-promisen
            ;; arvon. Kun käsitellään HTTP-pyyntöä, auth-wrapper luo tämän
            ;; promisen. Koska tätä funktiota ei kutsuta HTTP-pyynnön
            ;; käsittelijästä, meidän täytyy luoda promise itse.
            *current-user-oid* (promise)]
      (db/transaction
        (log/info "Aloitetaan tutkintojen päivitys koodistopalvelusta")
        (tallenna-koulutusalat! (koodisto/hae-koulutusala-muutokset asetukset))
        (tallenna-opintoalat! (koodisto/hae-opintoala-muutokset asetukset))
        (paivita-tutkinnot! (koodisto/hae-tutkinto-muutokset asetukset))
        (log/info "Tutkintojen päivitys koodistopalvelusta valmis")))
    (catch org.postgresql.util.PSQLException e
      (log/error e "Tutkintojen päivitys koodistopalvelusta epäonnistui"))))

;; Cloverage ei tykkää `defrecord`eja generoivista makroista, joten hoidetaan
;; `defjob`:n homma käsin.
(defrecord PaivitaTutkinnotJob []
  org.quartz.Job
  (execute [this ctx]
    (let [{asetukset "asetukset"} (qc/from-job-data ctx)]
      (paivita-tutkinnot-koodistopalvelusta! (clojure.walk/keywordize-keys asetukset)))))

