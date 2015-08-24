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
  (:require [compojure.core :as c]
            [aitu.infra.suoritus-arkisto :as arkisto]
            [aitu.compojure-util :as cu]
            [oph.common.util.http-util :refer [json-response]]))

(c/defroutes reitit
  (cu/defapi :yleinen-rest-api nil :get "/" []
    (json-response (arkisto/hae-kaikki)))
  (cu/defapi :yleinen-rest-api nil :delete "/:suorituskerta-id" [suorituskerta-id]
    (let [suorituskerta-id (Integer/parseInt suorituskerta-id)
          suorituskerta (arkisto/hae suorituskerta-id)]
      (if (= "luonnos" (:tila suorituskerta))
        (json-response (arkisto/poista! suorituskerta-id))
        {:status 403})))
  (cu/defapi :yleinen-rest-api nil :post "/" [& suoritus]
    (json-response (arkisto/lisaa! suoritus)))
  (cu/defapi :yleinen-rest-api nil :post "/laheta" [suoritukset]
    (json-response (arkisto/laheta! suoritukset)))
  (cu/defapi :yleinen-rest-api nil :post "/hyvaksy" [suoritukset]
    (json-response (arkisto/hyvaksy! suoritukset))))
