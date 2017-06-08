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
  (:require [compojure.api.core :refer [DELETE GET POST PUT defroutes]]
            [cheshire.core :as cheshire]
            [schema.core :as s]
            [clojure.string :as str]
            [oph.common.util.http-util :refer [response-or-404 luo-validoinnin-virhevastaus]]
            [sade.validators :as sade-validators]
            [aitu.compojure-util :refer :all]
            [aitu.infra.suorittaja-arkisto :as arkisto]
            [aitu.toimiala.skeema :refer [Suorittaja]]))

; TODO: luo-validoinnin-virhevastaus mieluummin..  OPH-1877
(defn- hetu-virhevastaus
  []
  {:status 400
   :headers {"Content-Type" "application/json; charset=utf-8"}
   :body (cheshire/generate-string
           {:errors [:hetu "Viallinen henkilötunnus"]})})

(defn- hetu-kaytossa-virhevastaus
  []
  {:status 400
   :headers {"Content-Type" "application/json; charset=utf-8"}
   :body (cheshire/generate-string
           {:errors [:hetu "Henkilötunnus on toisella opiskelijalla käytössä."]})})

(defn- hetu-validated [suorittaja suorittaja-operaatio-fn]
  (try
    (let [hetu (:hetu suorittaja)
          suorittajaid (when (:suorittajaid suorittaja)
                         (Integer/parseInt (:suorittajaid suorittaja)))]
      (if (or (str/blank? hetu) (not (sade-validators/valid-hetu? hetu)))
        (hetu-virhevastaus)
        (if (arkisto/hetu-kaytossa? suorittajaid hetu)
          (hetu-kaytossa-virhevastaus)
          (response-or-404 (suorittaja-operaatio-fn)))))
    (catch Exception _
      (hetu-virhevastaus))))

(defroutes reitit
  (GET "/:suorittajaid" [suorittajaid]
    :kayttooikeus :arviointipaatos
    (response-or-404 (arkisto/hae (Integer/parseInt suorittajaid))))
  (GET "/" []
    :kayttooikeus :arviointipaatos
    (response-or-404 (arkisto/hae-kaikki)))
  (POST "/" []
    :kayttooikeus :arviointipaatos
    :body [suorittaja Suorittaja]
    (let [suorittaja-operaatio-fn (fn [] (arkisto/lisaa! suorittaja))]
      (hetu-validated suorittaja suorittaja-operaatio-fn)))
  (PUT "/:suorittajaid" [suorittajaid & suorittaja]
    :kayttooikeus :arviointipaatos
    (let [suorittaja-operaatio-fn (fn [] (arkisto/tallenna! (Integer/parseInt suorittajaid) suorittaja))]
      (hetu-validated suorittaja suorittaja-operaatio-fn)))
  (DELETE "/:suorittajaid" [suorittajaid]
    :kayttooikeus :arviointipaatos
    (arkisto/poista! (Integer/parseInt suorittajaid))))
