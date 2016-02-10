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

(ns aitu.rest-api.suoritus
  (:require [aitu.infra.suoritus-arkisto :as arkisto]
            [aitu.compojure-util :as cu :refer [GET* POST* DELETE*]]
            [compojure.api.core :refer [defroutes]]
            [oph.common.util.http-util :refer [json-response]]))

(defroutes reitit
  (GET* "/" [& ehdot]
    :kayttooikeus :arviointipaatos
    (json-response (arkisto/hae-kaikki ehdot)))
  (DELETE* "/:suorituskerta-id" [suorituskerta-id]
    :kayttooikeus :arviointipaatos
    (let [suorituskerta-id (Integer/parseInt suorituskerta-id)
          suorituskerta (arkisto/hae suorituskerta-id)]
      (if (= "luonnos" (:tila suorituskerta))
        (json-response (arkisto/poista! suorituskerta-id))
        {:status 403})))
  (POST* "/" [& suoritus]
    :kayttooikeus :arviointipaatos
    (json-response (arkisto/lisaa! suoritus)))
  (POST* "/laheta" [suoritukset]
    :kayttooikeus :arviointipaatos
    (json-response (arkisto/laheta! suoritukset)))
  (POST* "/hyvaksy" [suoritukset]
    :kayttooikeus :arviointipaatos
    (json-response (arkisto/hyvaksy! suoritukset))))
