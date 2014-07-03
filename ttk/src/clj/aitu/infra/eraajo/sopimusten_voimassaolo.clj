(ns aitu.infra.eraajo.sopimusten-voimassaolo
  (:require [clojurewerkz.quartzite.jobs :as j
             :refer [defjob]]
            [clojurewerkz.quartzite.conversion :as qc]
            [clojure.tools.logging :as log]
            [oph.korma.korma-auth
             :refer [*current-user-uid* *current-user-oid* jarjestelmakayttaja]]
            [aitu.infra.jarjestamissopimus-arkisto :as sopimus-arkisto]))

(defn paivita-sopimusten-voimassaolo! []
  (binding [*current-user-oid* (promise)
            *current-user-uid* jarjestelmakayttaja]
    (doseq [{:keys [jarjestamissopimusid voimassa]} (sopimus-arkisto/hae-kaikki)]
      (sopimus-arkisto/aseta-voimassaolo jarjestamissopimusid voimassa))))

;; Cloverage ei tykkää `defrecord`eja generoivista makroista, joten hoidetaan
;; `defjob`:n homma käsin.
(defrecord PaivitaSopimustenVoimassaoloJob []
   org.quartz.Job
   (execute [this ctx]
     (paivita-sopimusten-voimassaolo!)))
