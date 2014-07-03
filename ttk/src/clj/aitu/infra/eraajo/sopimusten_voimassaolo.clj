(ns aitu.infra.eraajo.sopimusten-voimassaolo
  (:require [clojurewerkz.quartzite.jobs :as j
             :refer [defjob]]
            [clojurewerkz.quartzite.conversion :as qc]
            [clojure.tools.logging :as log]
            [korma.db :as db]
            [oph.korma.korma-auth
             :refer [*current-user-uid* *current-user-oid* jarjestelmakayttaja]]
            [aitu.infra.jarjestamissopimus-arkisto :as sopimus-arkisto]))

(defn paivita-sopimusten-voimassaolo! []
  (log/info "Päivitetään sopimusten voimassaolotieto")
  (binding [*current-user-oid* (promise)
            *current-user-uid* jarjestelmakayttaja]
    (db/transaction
      (doseq [{:keys [jarjestamissopimusid voimassa]} (sopimus-arkisto/hae-kaikki)]
       (sopimus-arkisto/aseta-voimassaolo jarjestamissopimusid voimassa))))
  (log/info "Sopimusten voimassaolotiedon päivitys valmis"))

;; Cloverage ei tykkää `defrecord`eja generoivista makroista, joten hoidetaan
;; `defjob`:n homma käsin.
(defrecord PaivitaSopimustenVoimassaoloJob []
   org.quartz.Job
   (execute [this ctx]
     (paivita-sopimusten-voimassaolo!)))
