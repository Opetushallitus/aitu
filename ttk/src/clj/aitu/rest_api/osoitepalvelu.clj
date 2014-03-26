(ns aitu.rest-api.osoitepalvelu
  (:require [aitu.infra.ttk-arkisto :as ttk-arkisto]
            [aitu.infra.oppilaitos-arkisto :as oppilaitos-arkisto]
            [compojure.api.sweet :refer :all]
            [aitu.toimiala.skeema :refer :all]
            [aitu.rest-api.http-util :refer [json-response]]))

(defroutes* reitit
  (GET* "/" []
    :summary "Hakee osoitepalvelun tarvitsemat tiedot"
    :return Osoitepalvelu
    (let [oppilaitokset (oppilaitos-arkisto/hae-osoitepalvelulle)
          toimikunnat (ttk-arkisto/hae-osoitepalvelulle)]
      (json-response {:toimikunnat toimikunnat
                      :oppilaitokset oppilaitokset}
                     Osoitepalvelu))))
