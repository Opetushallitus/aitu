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

(ns aitu.rest-api.organisaatiomuutos
  (:require [compojure.api.core :refer [GET POST defroutes]]
            [aitu.infra.organisaatiomuutos-arkisto :as arkisto]
            [oph.common.util.http-util :refer [response-or-404]]
            [aitu.toimiala.skeema :refer :all]))

(defroutes reitit
  (GET "/" []
    :kayttooikeus :organisaatiomuutos
    (response-or-404 (arkisto/hae-tekemattomat)))
  (GET "/maara" []
    :kayttooikeus :yleinen-rest-api
    (response-or-404 (arkisto/tekemattomien-maara)))
  (POST "/:id/tehty" [id]
    :kayttooikeus :organisaatiomuutos
    (arkisto/merkitse-tehdyksi (Integer/parseInt id))
    {:status 200}))
