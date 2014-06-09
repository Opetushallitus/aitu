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

(defn tallenna-uudet-opintoalat [opintoalat]
  (doseq [ala opintoalat]
    (opintoala-arkisto/lisaa! ala)))

(defn tallenna-muuttuneet-opintoalat [opintoalat]
  (doseq [ala opintoalat]
    (opintoala-arkisto/paivita! ala)))

(defn nimi->selite [ala]
  (clojure.set/rename-keys ala {:nimi_fi :selite_fi
                                :nimi_sv :selite_sv}))

(defn tallenna-opintoalat [opintoalat]
  (let [uudet (for [[alakoodi ala] opintoalat
                    :when (vector? ala)]
                (nimi->selite (first ala)))
        muuttuneet (for [[alakoodi ala] opintoalat
                         :when (map? ala)]
                     (nimi->selite (uudet-arvot ala)))]
    (tallenna-uudet-opintoalat uudet)
    (tallenna-muuttuneet-opintoalat muuttuneet)))

(defn tallenna-uudet-koulutusalat [koulutusalat]
  (doseq [ala koulutusalat]
    (koulutusala-arkisto/lisaa! ala)))

(defn tallenna-muuttuneet-koulutusalat [koulutusalat]
  (doseq [ala koulutusalat]
    (koulutusala-arkisto/paivita! ala)))

(defn nimi->selite [ala]
  (clojure.set/rename-keys ala {:nimi_fi :selite_fi
                                :nimi_sv :selite_sv}))

(defn tallenna-koulutusalat [koulutusalat]
  (let [uudet (for [[alakoodi ala] koulutusalat
                    :when (vector? ala)]
                (nimi->selite (first ala)))
        muuttuneet (for [[alakoodi ala] koulutusalat
                         :when (map? ala)]
                     (nimi->selite (uudet-arvot ala)))]
    (tallenna-uudet-koulutusalat uudet)
    (tallenna-muuttuneet-koulutusalat muuttuneet)))

(defn tallenna-uudet-tutkinnonosat [tutkintoversio-id tutkinnonosat]
  (doseq [{:keys [jarjestysnumero tutkinnonosa]} tutkinnonosat]
    (tutkinto-arkisto/lisaa-tutkinnon-osa! tutkintoversio-id
                                          jarjestysnumero
                                          (assoc tutkinnonosa :versio 1))))

(defn tallenna-muuttuneet-tutkinnonosat [tutkintoversio-id tutkinnonosat]
  (doseq [{:keys [jarjestysnumero tutkinnonosa]} tutkinnonosat]
    (tutkinto-arkisto/paivita-tutkinnon-osa! tutkintoversio-id
                                             jarjestysnumero
                                             tutkinnonosa)))

(defn tallenna-tutkinnonosat [tutkintoversio-id tutkinnonosat]
  (let [uudet (for [osa tutkinnonosat
                    :when (vector? (:tutkinnonosa osa))]
                (update-in osa [:tutkinnonosa] first))
        muuttuneet (for [osa tutkinnonosat
                         :when (map? (:tutkinnonosa osa))]
                     (update-in osa [:tutkinnonosa] uudet-arvot))]
    (tallenna-uudet-tutkinnonosat tutkintoversio-id uudet)
    (tallenna-muuttuneet-tutkinnonosat tutkintoversio-id muuttuneet)))

(defn tallenna-uudet-osaamisalat [tutkintoversio-id osaamisalat]
  (doseq [osaamisala osaamisalat]
    (tutkinto-arkisto/lisaa-osaamisala! (assoc osaamisala
                                               :versio 1
                                               :tutkintoversio tutkintoversio-id))))

(defn tallenna-muuttuneet-osaamisalat [tutkintoversio-id osaamisalat]
  (doseq [osaamisala osaamisalat]
    (tutkinto-arkisto/paivita-osaamisala! (assoc osaamisala :tutkintoversio tutkintoversio-id))))

(defn tallenna-osaamisalat! [tutkintoversio-id osaamisalat]
  (let [uudet (keep uusi osaamisalat)
        muuttuneet (keep muuttunut osaamisalat)]
    (tallenna-uudet-osaamisalat tutkintoversio-id uudet)
    (tallenna-muuttuneet-osaamisalat tutkintoversio-id muuttuneet)))

(defn paivita-tutkinnot! [tutkintomuutokset]
  (try
    (db/transaction
      (let [opintoalat (set (map :opintoala_tkkoodi (opintoala-arkisto/hae-kaikki)))
            {:keys [tutkinnot osaamisalat tutkinnonosat]} tutkintomuutokset]
        (doseq [t (keep uusi tutkinnot)
                :when (contains? opintoalat (:opintoala t))]
          (let [tutkintoversio-id (tutkinto-arkisto/lisaa-tutkinto-ja-versio! (dissoc t :osaamisalat :tutkinnonosat))
                osaamisalat (map osaamisalat (:osaamisalat t))
                tutkinnonosat (for [{:keys [osatunnus jarjestysnumero]} (:tutkinnonosat t)]
                                {:jarjestysnumero jarjestysnumero
                                 :tutkinnonosa (tutkinnonosat osatunnus)})]
            (tallenna-osaamisalat tutkintoversio-id osaamisalat)
            (tallenna-tutkinnonosat tutkintoversio-id tutkinnonosat)))
        (doseq [t (keep muuttunut tutkinnot)
                :when (contains? opintoalat (:opintoala t))]
          (let [tutkintoversio-id (tutkinto-arkisto/paivita-tutkinto! (dissoc t :osaamisalat :tutkinnonosat))
                osaamisalat (map osaamisalat (:osaamisalat t))
                tutkinnonosat (for [{:keys [osatunnus jarjestysnumero]} (:tutkinnonosat t)]
                                {:jarjestysnumero jarjestysnumero
                                 :tutkinnonosa (tutkinnonosat osatunnus)})]
            (tallenna-osaamisalat tutkintoversio-id osaamisalat)
            (tallenna-tutkinnonosat tutkintoversio-id tutkinnonosat)))))
    (catch org.postgresql.util.PSQLException e
      (log/error e "Tutkintojen päivitys koodistopalvelusta epäonnistui"))))

(defn paivita-tutkinnot-koodistopalvelusta! [asetukset]
  (tallenna-koulutusalat! (koodisto/koulutusala-muutokset asetukset))
  (tallenna-opintoalat! (koodisto/opintoala-muutokset asetukset))
  (paivita-tutkinnot! (koodisto/tutkinto-muutokset asetukset)))

