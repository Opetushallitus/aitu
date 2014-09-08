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

(ns aitu.rest-api.tutkinto
  (:require [compojure.core :as c]
            [cheshire.core :as cheshire]
            [aitu.infra.tutkinto-arkisto :as arkisto]
            [aitu.toimiala.tutkinto :as tutkinto]
            [aitu.toimiala.voimassaolo.saanto.tutkinto :as voimassaolo]
            [aitu.compojure-util :as cu]
            [aitu.rest-api.http-util :refer [csv-download-response]]
            [aitu.util :refer [muodosta-csv]]
            [oph.common.util.http-util :refer [cachable-json-response json-response]]
            [korma.db :as db]))

(def tutkintokenttien-jarjestys [:tutkintotunnus :nimi_fi :nimi_sv :opintoala_fi :opintoala_sv])

(c/defroutes raportti-reitit
  (c/GET "/csv" req
    (cu/autorisoitu-transaktio :yleinen-rest-api nil
      (csv-download-response
        (muodosta-csv (arkisto/hae-ehdoilla
                        (assoc (:params req) :avaimet tutkintokenttien-jarjestys))
                      tutkintokenttien-jarjestys)
        "tutkinnot.csv"))))

(c/defroutes reitit
  (cu/defapi :yleinen-rest-api nil :get "/" [:as req]
    (cachable-json-response req (map voimassaolo/taydenna-tutkinnon-voimassaolo
                                     (arkisto/hae-kaikki))))
  (cu/defapi :yleinen-rest-api nil :get "/:tutkintotunnus" [tutkintotunnus]
    (json-response (tutkinto/taydenna-tutkinto (arkisto/hae tutkintotunnus))))
  (cu/defapi :yleinen-rest-api nil :get "/haku/osat" [termi :as req]
    (cachable-json-response req (arkisto/hae-opintoalat-tutkinnot-osaamisalat-tutkinnonosat termi)))
  (cu/defapi :yleinen-rest-api nil :get "/haku/tutkinnot" [termi :as req]
    (cachable-json-response req (arkisto/hae-opintoalat-tutkinnot termi))))

