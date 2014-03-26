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

(ns aitu.rest-api.kayttaja
  (:require [compojure.core :as c]
            [oph.korma.korma-auth :as ka]
            [aitu.infra.kayttaja-arkisto :as arkisto]
            [aitu.rest-api.http-util :refer [json-response]]
            [aitu.toimiala.kayttajaoikeudet :as ko]
            [aitu.compojure-util :as cu]
            [korma.db :as db]))

(c/defroutes reitit
  (c/GET "/" []
    (db/transaction
      (json-response (arkisto/hae @ka/*current-user-oid*))))
  (cu/defapi :omat_tiedot oid :get "/:oid" [oid]
    (json-response (arkisto/hae oid))))
