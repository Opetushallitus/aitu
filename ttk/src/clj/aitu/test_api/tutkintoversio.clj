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

(ns aitu.test-api.tutkintoversio
  "Testien tarvitsemat REST-rajapinnat jos niitä on. Näitä ei tarvita normaalista käyttöliittymästä koskaan"
  (:require [compojure.core :as c]
            [clj-time.coerce :as time-coerce]
            [aitu.rest-api.http-util :refer [parse-iso-date]]
            [aitu.infra.tutkinto-arkisto :as tutkinto-arkisto]
            [korma.db :as db]))

(c/defroutes reitit
  (c/DELETE "/:tutkintoversio_id" [tutkintoversio_id]
    (db/transaction
      (tutkinto-arkisto/poista-tutkintoversio! (Integer/parseInt tutkintoversio_id))
      {:status 200}))
  (c/POST "/" [tutkintotunnus tutkintoversio_id versio koodistoversio peruste siirtymaajan_loppupvm voimassa_alkupvm voimassa_loppupvm]
    (db/transaction
      (tutkinto-arkisto/lisaa-tutkintoversio! (merge {:tutkintotunnus tutkintotunnus
                                                      :voimassa_alkupvm (parse-iso-date voimassa_alkupvm)
                                                      :voimassa_loppupvm (parse-iso-date voimassa_loppupvm)
                                                      :versio versio
                                                      :koodistoversio koodistoversio
                                                      :tutkintoversio_id tutkintoversio_id
                                                      :hyvaksytty true}
                                                     (when siirtymaajan_loppupvm
                                                       {:siirtymaajan_loppupvm (parse-iso-date siirtymaajan_loppupvm)})
                                                     (when peruste
                                                       {:peruste peruste})))
      {:status 200})))
