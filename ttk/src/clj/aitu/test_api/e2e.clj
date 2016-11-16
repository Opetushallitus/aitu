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

(ns aitu.test-api.e2e
  "Testien tarvitsemat yleiset REST-rajapinnat. Näitä ei tarvita normaalista käyttöliittymästä koskaan"
  (:require [compojure.core :as c]
            [clj-time.core :as time]
            [infra.test.data :as testdata]
            [oph.korma.korma-auth :as ka]
            [korma.db :as db]))

(c/defroutes reitit
  (c/DELETE "/data" []
    (db/transaction
      (testdata/tyhjenna-testidata! ka/default-test-user-oid)
      {:status 200}))
  (c/DELETE "/:oid" [oid]
    (db/transaction
      (testdata/tyhjenna-testidata! oid)
      (testdata/poista-testikayttaja! oid)
      {:status 200})))
