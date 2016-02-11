(ns aitu.restructure
  (:require [compojure.api.meta]

            [aitu.compojure-util :as cu]))

(defmethod compojure.api.meta/restructure-param :kayttooikeus
  [_ kayttooikeus {:keys [body] :as acc}]
  "Käyttoikeuslaajennos compojure-apin rajapintoihin. Esim:

  :kayttooikeus :jasenesitys-poisto
  :kayttooikeus [:jasenesitys-poisto jasenyysid]"

  (let [konteksti (when (vector? kayttooikeus) (second kayttooikeus))
        kayttooikeus (if (vector? kayttooikeus) (first kayttooikeus) kayttooikeus)]
    (-> acc
        (assoc-in [:swagger :description] (str "Käyttöoikeus " kayttooikeus " , konteksti: " (or konteksti "N/A")))
        (assoc :body [`(cu/autorisoitu-transaktio ~kayttooikeus ~konteksti (do ~@body))]))))
