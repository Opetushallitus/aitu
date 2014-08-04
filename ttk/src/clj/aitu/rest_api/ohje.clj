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

(ns aitu.rest-api.ohje
  (:require [compojure.core :as c]
            [aitu.infra.ohje-arkisto :as arkisto]
            [oph.common.util.http-util :refer [json-response]]
            [aitu.compojure-util :as cu]
            [cheshire.core :as cheshire]))

(c/defroutes reitit
  (cu/defapi :ohjeet_luku nil :get "/:ohjetunniste" [ohjetunniste]
    (if-let [ohje (arkisto/hae ohjetunniste)]
      (json-response ohje)
      {:status 200}))
  (cu/defapi :ohje_muokkaus nil :put "/:ohjetunniste" [ohjetunniste teksti_fi teksti_sv]
    (arkisto/muokkaa-tai-luo-uusi! {:ohjetunniste ohjetunniste
                                    :teksti_fi teksti_fi
                                    :teksti_sv teksti_sv})
    {:status 200}))
