(ns aitu.restructure
  (:require [compojure.api.meta]

            [aitu.compojure-util :as cu]))

(defmethod compojure.api.meta/restructure-param :kayttooikeus
  [_ kayttooikeus_spec {:keys [body] :as acc}]
  "Käyttoikeuslaajennos compojure-apin rajapintoihin. Esim:

  :kayttooikeus :jasenesitys-poisto
  :kayttooikeus [:jasenesitys-poisto jasenyysid]"

  (let [[kayttooikeus konteksti] (if (vector? kayttooikeus_spec) kayttooikeus_spec [kayttooikeus_spec])]
    (-> acc
        (assoc-in [:swagger :description] (str "Käyttöoikeus " kayttooikeus " , konteksti: " (or konteksti "N/A")))
        (assoc :body [`(cu/autorisoitu-transaktio ~kayttooikeus ~konteksti (do ~@body))]))))
