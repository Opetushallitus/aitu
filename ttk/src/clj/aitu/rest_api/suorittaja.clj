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
  (:require [aitu.infra.suorittaja-arkisto :as arkisto]
            [aitu.compojure-util :as cu :refer [GET* POST* PUT* DELETE*]]
            [compojure.api.core :refer [defroutes*]]
            [oph.common.util.http-util :refer [json-response]]))

(defroutes* reitit
  (GET* "/" []
    :kayttooikeus :arviointipaatos
    (json-response (arkisto/hae-kaikki)))
  (POST* "/" [& form]
    :kayttooikeus :arviointipaatos
    (json-response (arkisto/lisaa! form)))
  (PUT* "/:suorittajaid" [suorittajaid & suorittaja]
    :kayttooikeus :arviointipaatos
    (json-response (arkisto/tallenna! (Integer/parseInt suorittajaid) suorittaja)))
  (DELETE* "/:suorittajaid" [suorittajaid]
    :kayttooikeus :arviointipaatos
    (arkisto/poista! (Integer/parseInt suorittajaid))))
