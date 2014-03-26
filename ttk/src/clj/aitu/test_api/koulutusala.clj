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

(ns aitu.test-api.koulutusala
  "Testien tarvitsemat REST-rajapinnat jos niitä on. Näitä ei tarvita normaalista käyttöliittymästä koskaan"
  (:require [compojure.core :as c]
            [clj-time.core :as time]
            [aitu.infra.koulutusala-arkisto :as koulutusala-arkisto]
            [korma.db :as db]))

(c/defroutes reitit
  (c/DELETE "/" []
    (db/transaction
      (koulutusala-arkisto/tyhjenna!)
      {:status 200}))
  (c/DELETE "/:koodi" [koodi]
    (db/transaction
      (koulutusala-arkisto/poista! koodi)
      {:status 200}))
  (c/POST "/" [koodi selite_fi selite_sv]
    (db/transaction
      (koulutusala-arkisto/lisaa! {:koulutusala_tkkoodi koodi
                                   :selite_fi selite_fi
                                   :selite_sv selite_sv
                                   :voimassa_alkupvm (time/minus (time/today) (time/days 1))
                                   :voimassa_loppupvm (time/plus (time/today) (time/days 1))})
      {:status 200})))

