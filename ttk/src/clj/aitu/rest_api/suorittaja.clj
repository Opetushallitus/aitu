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

(ns aitu.rest-api.suorittaja
  (:require [aitu.infra.suorittaja-arkisto :as arkisto]
            aitu.compojure-util
            [compojure.api.core :refer [DELETE GET POST PUT defroutes]]
            [oph.common.util.http-util :refer [response-or-404 luo-validoinnin-virhevastaus]]
            [sade.validators :as sade-validators]
            [cheshire.core :as cheshire]))

; TODO: luo-validoinnin-virhevastaus mieluummin.. 
(defn hetu-virhevastaus
  []
  {:status 400
   :headers {"Content-Type" "application/json"}
   :body (cheshire/generate-string
           {:errors [:hetu "Viallinen henkilötunnus"]})})

(defroutes reitit
  (GET "/" []
    :kayttooikeus :arviointipaatos
    (response-or-404 (arkisto/hae-kaikki)))
  (POST "/" [& suorittaja]
    :kayttooikeus :arviointipaatos
    (if (and (:hetu suorittaja) (not (sade-validators/valid-hetu? (:hetu suorittaja))))
      (hetu-virhevastaus)
      (response-or-404 (arkisto/lisaa! suorittaja))))
  (PUT "/:suorittajaid" [suorittajaid & suorittaja]
    :kayttooikeus :arviointipaatos
    ; TODO: hetun validointi
    (if (and (:hetu suorittaja) (not (sade-validators/valid-hetu? (:hetu suorittaja))))
      (hetu-virhevastaus)
      (response-or-404 (arkisto/tallenna! (Integer/parseInt suorittajaid) suorittaja))))
  (DELETE "/:suorittajaid" [suorittajaid]
    :kayttooikeus :arviointipaatos
    (arkisto/poista! (Integer/parseInt suorittajaid))))
