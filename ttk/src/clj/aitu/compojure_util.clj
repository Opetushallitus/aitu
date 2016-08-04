;; Copyright (c) 2013 The Finnish National Board of Education - Opetushallitus
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

(ns aitu.compojure-util
 (:require compojure.api.core
           [oph.compojure-util :as cu]
           [aitu.toimiala.kayttajaoikeudet :as ko]))

;; Käyttöoikeuslaajennos compojure-apin rajapintoihin. Esim:
;;
;; :kayttooikeus :jasenesitys-poisto
;; :kayttooikeus [:jasenesitys-poisto jasenyysid]
(defmethod compojure.api.meta/restructure-param :kayttooikeus
  [_ kayttooikeus_spec {:keys [body] :as acc}]
  (let [[kayttooikeus konteksti] (if (vector? kayttooikeus_spec) kayttooikeus_spec [kayttooikeus_spec])]
    (-> acc
      (assoc-in [:swagger :description] (str "Käyttöoikeus " kayttooikeus " , konteksti: " (or konteksti "N/A")))
      (assoc :body [`(cu/autorisoitu-transaktio ~ko/toiminnot ~kayttooikeus ~konteksti (do ~@body))]))))

(defmacro autorisoi
  "Tarkastaa käyttöoikeudet"
  [toiminto konteksti & body]
  `(cu/autorisoi ~ko/toiminnot ~toiminto ~konteksti ~@body))