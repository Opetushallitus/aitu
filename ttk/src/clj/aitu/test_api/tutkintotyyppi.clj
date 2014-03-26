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

(ns aitu.test-api.tutkintotyyppi
  "Testien tarvitsemat REST-rajapinnat jos niitä on. Näitä ei tarvita normaalista käyttöliittymästä koskaan"
  (:require [compojure.core :as c]
            [clj-time.coerce :as time-coerce]
            [aitu.rest-api.http-util :refer [parse-iso-date]]
            [aitu.infra.tutkintotyyppi-arkisto :as arkisto]
            [korma.db :as db]))

(c/defroutes reitit
  (c/DELETE "/:tyyppi" [tyyppi]
    (db/transaction
      (arkisto/poista! {:tyyppi tyyppi})
      {:status 200}))
  (c/POST "/" [tyyppi selite_fi selite_sv]
    (db/transaction
      (arkisto/lisaa! {:tyyppi tyyppi
                       :selite_fi selite_fi
                       :selite_sv selite_sv})
      {:status 200})))
