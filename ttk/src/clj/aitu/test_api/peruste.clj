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

(ns aitu.test-api.peruste
  "Testien tarvitsemat REST-rajapinnat jos niitä on. Näitä ei tarvita normaalista käyttöliittymästä koskaan"
  (:require [compojure.core :as c]
            [cheshire.core :as cheshire]
            [aitu.infra.peruste-arkisto :as arkisto]
            [oph.common.util.http-util :refer [parse-iso-date]]
            [korma.db :as db]))

(c/defroutes reitit
  (c/POST "/" [diaarinumero alkupvm]
    (db/transaction
      (arkisto/lisaa! {:diaarinumero diaarinumero
                       :alkupvm (parse-iso-date alkupvm)})
      {:status 200}))
  (c/DELETE "/:diaarinumero" [diaarinumero]
    (db/transaction
      (arkisto/poista! {:diaarinumero diaarinumero})
      {:status 200})))
