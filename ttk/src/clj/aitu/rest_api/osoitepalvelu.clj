(ns aitu.rest-api.osoitepalvelu
  (:require [aitu.infra.ttk-arkisto :as ttk-arkisto]
            [aitu.infra.oppilaitos-arkisto :as oppilaitos-arkisto]
            [aitu.toimiala.skeema :refer :all]
            [compojure.api.core :refer [GET defroutes]]
            [oph.common.util.http-util :refer [json-response]]))

(defroutes reitit
  (GET "/" []
    :summary "INTEGRAATIO: Hakee osoitepalvelun tarvitsemat tiedot"
    :return Osoitepalvelu
    :kayttooikeus :osoitepalvelu-api
    (let [oppilaitokset (oppilaitos-arkisto/hae-osoitepalvelulle)
          toimikunnat (ttk-arkisto/hae-osoitepalvelulle)]
      (json-response {:toimikunnat toimikunnat
                      :oppilaitokset oppilaitokset}))))
