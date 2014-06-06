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
            [aitu.util :refer [map-by]]
            [korma.db :as db]))

(defn uusi [[tutkintotunnus muutos]]
  "Jos muutos on uuden tiedon lisääminen, palauttaa uudet tiedot, muuten nil"
  (when (vector? muutos)
    (assoc (first muutos) :tutkintotunnus tutkintotunnus)))

(defn uudet-arvot [muutos]
  (into {}
        (for [[k v] muutos
             :when v]
         [k (first v)])))

(defn muuttunut [[tutkintotunnus muutos]]
  "Jos muutos on tietojen muuttuminen, palauttaa muuttuneet tiedot, muuten nil"
  (when (and (map? muutos)
             (not-every? nil? (vals muutos)))
    (merge {:tutkintotunnus tutkintotunnus}
          (uudet-arvot muutos))))

(defn tallenna-uudet-opintoalat! [opintoalat]
  (doseq [ala opintoalat]
    (log/info "Lisätään opintoala " (:opintoala_tkkoodi ala))
    (opintoala-arkisto/lisaa! ala)))

(defn tallenna-muuttuneet-opintoalat! [opintoalat]
  (doseq [ala opintoalat]
    (log/info "Päivitetään opintoala " (:opintoala_tkkoodi ala) ", muutokset: " (dissoc ala :opintoala_tkkoodi))
    (opintoala-arkisto/paivita! ala)))

(defn nimi->selite [ala]
  (clojure.set/rename-keys ala {:nimi_fi :selite_fi
                                :nimi_sv :selite_sv}))

(defn tallenna-opintoalat! [opintoalat]
  (let [uudet (for [[alakoodi ala] opintoalat
                    :when (and (vector? ala) (first ala))]
                (nimi->selite (first ala)))
        muuttuneet (for [[alakoodi ala] opintoalat
                         :when (map? ala)]
                     (nimi->selite (uudet-arvot ala)))]
    (tallenna-uudet-opintoalat! uudet)
    (tallenna-muuttuneet-opintoalat! muuttuneet)))

(defn tallenna-uudet-koulutusalat! [koulutusalat]
  (doseq [ala koulutusalat]
    (log/info "Lisätään koulutusala " (:koulutusala_tkkoodi ala))
    (koulutusala-arkisto/lisaa! ala)))

(defn tallenna-muuttuneet-koulutusalat! [koulutusalat]
  (doseq [ala koulutusalat]
    (log/info "Päivitetään koulutusala " (:koulutusala_tkkoodi ala) ", muutokset: " (dissoc ala :koulutusala_tkkoodi))
    (koulutusala-arkisto/paivita! ala)))

(defn tallenna-koulutusalat! [koulutusalat]
  (>pprint koulutusalat)
  (let [uudet (for [[alakoodi ala] koulutusalat
                    :when (vector? ala)]
                (nimi->selite (first ala)))
        muuttuneet (for [[alakoodi ala] koulutusalat
                         :when (map? ala)]
                     (nimi->selite (uudet-arvot ala)))]
    (tallenna-uudet-koulutusalat! uudet)
    (tallenna-muuttuneet-koulutusalat! muuttuneet)))

(defn tallenna-uudet-tutkinnonosat! [tutkintoversio-id tutkinnonosat]
  (doseq [{:keys [jarjestysnumero tutkinnonosa]} tutkinnonosat]
    (log/info "Lisätään tutkinnonosa " (:osatunnus tutkinnonosa))
    (tutkinto-arkisto/lisaa-tutkinnon-osa! tutkintoversio-id
                                           jarjestysnumero
                                           (assoc tutkinnonosa :versio 1))))

(defn tallenna-muuttuneet-tutkinnonosat! [tutkintoversio-id tutkinnonosat]
  (doseq [{:keys [jarjestysnumero tutkinnonosa]} tutkinnonosat]
    (log/info "Päivitetään tutkinnonosa " (:osatunnus tutkinnonosa) ", muutokset: " (dissoc tutkinnonosa :osatunnus))
    (tutkinto-arkisto/paivita-tutkinnon-osa! tutkintoversio-id
                                             jarjestysnumero
                                             tutkinnonosa)))

(defn tallenna-tutkinnonosat! [tutkintoversio-id tutkinnonosat]
  (let [uudet (for [osa tutkinnonosat
                    :when (vector? (:tutkinnonosa osa))]
                (update-in osa [:tutkinnonosa] first))
        muuttuneet (for [osa tutkinnonosat
                         :when (map? (:tutkinnonosa osa))]
                     (update-in osa [:tutkinnonosa] uudet-arvot))]
    (tallenna-uudet-tutkinnonosat! tutkintoversio-id uudet)
    (tallenna-muuttuneet-tutkinnonosat! tutkintoversio-id muuttuneet)))

(defn tallenna-uudet-osaamisalat! [tutkintoversio-id osaamisalat]
  (doseq [osaamisala osaamisalat]
    (log/info "Lisätään osaamisala " (:osaamisalatunnus osaamisala))
    (tutkinto-arkisto/lisaa-osaamisala! (assoc osaamisala
                                               :versio 1
                                               :tutkintoversio tutkintoversio-id))))

(defn tallenna-muuttuneet-osaamisalat! [tutkintoversio-id osaamisalat]
  (doseq [osaamisala osaamisalat]
    (log/info "Päivitetään osaamisala " (:osaamisalatunnus osaamisala) ", muutokset: " (dissoc osaamisala :osaamisalatunnus))
    (tutkinto-arkisto/paivita-osaamisala! (assoc osaamisala :tutkintoversio tutkintoversio-id))))

(defn tallenna-osaamisalat! [tutkintoversio-id osaamisalat]
  (let [uudet (keep uusi osaamisalat)
        muuttuneet (keep muuttunut osaamisalat)]
    (tallenna-uudet-osaamisalat! tutkintoversio-id uudet)
    (tallenna-muuttuneet-osaamisalat! tutkintoversio-id muuttuneet)))

(defn paivita-tutkinto! [koodistoasetukset tutkinto]
  (when (:jarjestyskoodistoversio tutkinto)
    (let [vanha-tutkintoversio (tutkinto-arkisto/hae-tutkinto (:tutkintotunnus tutkinto))
          jarjestyskoodisto (koodisto/hae-koodisto koodistoasetukset (:osajarjestyskoodisto vanha-tutkintoversio) (:jarjestyskoodistoversio vanha-tutkintoversio))]
      (tutkinto-arkisto/paivita-tutkintoversio! {:tutkintoversio_id (:tutkintoversio_id vanha-tutkintoversio)
                                                 :voimassa_loppupvm (:voimassa_loppupvm jarjestyskoodisto)})))
  (if (every? nil? ((juxt :osaamisalat :tutkinnonosat) tutkinto))
    (let [tutkintotunnus (:tutkintotunnus tutkinto)
          tutkintotiedot (select-keys tutkinto [:nimi_fi :nimi_sv :tyyppi :tutkintotaso :opintoala])
          versiotiedot (select-keys tutkinto [:voimassa_alkupvm :voimassa_loppupvm])]
      (when (not-every? nil? (vals tutkintotiedot))
        (tutkinto-arkisto/paivita! tutkintotunnus tutkintotiedot))
      (when (not-every? nil? (vals versiotiedot))
        (let [tutkintoversio-id (:tutkintoversio_id (tutkinto-arkisto/hae-tutkinto (:tutkintotunnus tutkinto)))]
          (tutkinto-arkisto/paivita-tutkintoversio! (assoc versiotiedot :tutkintoversio_id tutkintoversio-id)))))
    (tutkinto-arkisto/paivita-tutkinto! (dissoc tutkinto :osaamisalat :tutkinnonosat))))

(defn paivita-tutkinnot! [koodistoasetukset tutkintomuutokset]
  (let [opintoalat (set (map :opintoala_tkkoodi (opintoala-arkisto/hae-kaikki)))
        {:keys [tutkinnot osaamisalat tutkinnonosat]} tutkintomuutokset]
    (doseq [t (keep uusi tutkinnot)
            :when (contains? opintoalat (:opintoala t))]
      (log/info "Lisätään tutkinto " (:tutkintotunnus t))
      (let [tutkintoversio-id (tutkinto-arkisto/lisaa-tutkinto-ja-versio! (assoc (dissoc t :osaamisalat :tutkinnonosat)
                                                                                 :versio 1))
            osaamisalat (map osaamisalat (:osaamisalat t))
            tutkinnonosat (for [{:keys [osatunnus jarjestysnumero]} (:tutkinnonosat t)]
                            {:jarjestysnumero jarjestysnumero
                             :tutkinnonosa (tutkinnonosat osatunnus)})]
        (tallenna-osaamisalat! tutkintoversio-id osaamisalat)
        (tallenna-tutkinnonosat! tutkintoversio-id tutkinnonosat)))
    (doseq [t (keep muuttunut tutkinnot)
            :when (contains? opintoalat (:opintoala t))]
      (log/info "Päivitetään tutkinto " (:tutkintotunnus t) ", muutokset: " (dissoc t :tutkintotunnus))
      (let [tutkintoversio-id (paivita-tutkinto! koodistoasetukset t)
            osaamisalat (map osaamisalat (:osaamisalat t))
            tutkinnonosat (for [{:keys [osatunnus jarjestysnumero]} (:tutkinnonosat t)]
                            {:jarjestysnumero jarjestysnumero
                             :tutkinnonosa (tutkinnonosat osatunnus)})]
        (tallenna-osaamisalat! tutkintoversio-id osaamisalat)
        (tallenna-tutkinnonosat! tutkintoversio-id tutkinnonosat)))))

(defn paivita-tutkinnot-koodistopalvelusta! [asetukset]
  (try
    (db/transaction
      (log/info "Aloitetaan tutkintojen päivitys koodistopalvelusta")
      (tallenna-koulutusalat! (koodisto/hae-koulutusala-muutokset asetukset))
      (tallenna-opintoalat! (koodisto/hae-opintoala-muutokset asetukset))
      (paivita-tutkinnot! asetukset (koodisto/hae-tutkinto-muutokset asetukset))
      (log/info "Tutkintojen päivitys koodistopalvelusta valmis"))
    (catch org.postgresql.util.PSQLException e
      (log/error e "Tutkintojen päivitys koodistopalvelusta epäonnistui"))))

