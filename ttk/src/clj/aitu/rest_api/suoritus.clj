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
            aitu.compojure-util
            [compojure.api.core :refer [DELETE GET POST defroutes]]
            [oph.common.util.http-util :refer [response-or-404]]))

; TODO: tilan huomiointi operaatioissa - voiko hyväksyttyä päivittää? ei voi.
(defroutes reitit
  (GET "/" [& ehdot]
    :kayttooikeus :arviointipaatos
    (response-or-404 (arkisto/hae-kaikki ehdot)))
  (GET "/:suorituskerta-id" [suorituskerta-id]
       :kayttooikeus :arviointipaatos
       (response-or-404 (arkisto/hae-tiedot suorituskerta-id)))
  (DELETE "/:suorituskerta-id" [suorituskerta-id]
    :kayttooikeus :arviointipaatos
    (let [suorituskerta-id (Integer/parseInt suorituskerta-id)
          suorituskerta (arkisto/hae suorituskerta-id)]
      (if (= "luonnos" (:tila suorituskerta))
        (response-or-404 (arkisto/poista! suorituskerta-id))
        {:status 403})))
  (POST "/" [& suoritus]
    :kayttooikeus :arviointipaatos
    (response-or-404 (arkisto/lisaa-tai-paivita! suoritus)))
  (POST "/laheta" [suoritukset]
    :kayttooikeus :arviointipaatos
    (response-or-404 (arkisto/laheta! suoritukset)))
  (POST "/hyvaksy" [suoritukset]
    :kayttooikeus :arviointipaatos
    (response-or-404 (arkisto/hyvaksy! suoritukset))))
