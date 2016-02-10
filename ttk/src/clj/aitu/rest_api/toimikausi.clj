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

(ns aitu.rest-api.toimikausi
  (:require [aitu.infra.toimikausi-arkisto :as arkisto]
            [aitu.compojure-util :as cu :refer [GET*]]
            [compojure.api.core :refer [defroutes]]
            [oph.common.util.http-util :refer [json-response]]))

(defroutes reitit
  (GET* "/" []
    :summary "Palauttaa tutkintotoimikuntien toimikaudet."
    :kayttooikeus :yleinen-rest-api
    (let [toimikaudet (arkisto/hae-kaikki)]
      (json-response toimikaudet))))
