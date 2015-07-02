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
  (:require [compojure.core :as c]
            [aitu.infra.suorittaja-arkisto :as arkisto]
            [aitu.compojure-util :as cu]
            [oph.common.util.http-util :refer [json-response]]))

(c/defroutes reitit
  (cu/defapi :yleinen-rest-api nil :get "/" []
    (json-response (arkisto/hae-kaikki)))
  (cu/defapi :yleinen-rest-api nil :post "/" [& form]
    (arkisto/lisaa! form))
  (cu/defapi :yleinen-rest-api nil :put "/:suorittajaid" [suorittajaid & suorittaja]
    (arkisto/tallenna! (Integer/parseInt suorittajaid) suorittaja))
  (cu/defapi :yleinen-rest-api nil :delete "/:suorittajaid" [suorittajaid]
    (arkisto/poista! (Integer/parseInt suorittajaid))))
