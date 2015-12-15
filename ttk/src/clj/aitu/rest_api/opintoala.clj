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

(ns aitu.rest-api.opintoala
  (:require [aitu.infra.opintoala-arkisto :as arkisto]
            [oph.common.util.http-util :refer [json-response]]
            [aitu.compojure-util :as cu :refer [GET*]]
            [compojure.api.core :refer [defroutes*]]))

(defroutes* reitit
  (GET* "/haku" [termi]
    :summary "Koulutusalan haku termillä. (Aitu ei master, julkista tietoa)"
    :kayttooikeus :yleinen-rest-api
    (json-response (arkisto/hae-termilla termi)))
  (GET* "/" []
    :summary "Kaikki opintoalat. (Aitu ei master, julkista tietoa)"
    :kayttooikeus :yleinen-rest-api
    (json-response (arkisto/hae-kaikki)))
  (GET* "/:koodi" [koodi]
    :summary "Opintoala tunnuksella. (Aitu ei master, julkista tietoa)"
    :kayttooikeus :yleinen-rest-api
    (json-response (arkisto/hae koodi))))
