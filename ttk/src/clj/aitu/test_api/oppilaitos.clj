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

(ns aitu.test-api.oppilaitos
  "Testien tarvitsemat REST-rajapinnat jos niitä on. Näitä ei tarvita normaalista käyttöliittymästä koskaan"
  (:require [compojure.core :as c]
            [aitu.infra.oppilaitos-arkisto :as oppilaitos-arkisto]
            [korma.db :as db]))

(c/defroutes reitit
  (c/POST "/" [oppilaitoskoodi nimi alue sahkoposti puhelin osoite postinumero postitoimipaikka]
    (db/transaction
      (oppilaitos-arkisto/lisaa! {:oppilaitoskoodi oppilaitoskoodi
                                  :nimi nimi
                                  :alue alue
                                  :sahkoposti sahkoposti
                                  :puhelin puhelin
                                  :osoite osoite
                                  :postinumero postinumero
                                  :postitoimipaikka postitoimipaikka})
      {:status 200}))
  (c/DELETE "/:oppilaitoskoodi" [oppilaitoskoodi]
    (db/transaction
      (oppilaitos-arkisto/poista! oppilaitoskoodi)
      {:status 200}))
  (c/POST "/toimipaikka/:oppilaitos" [oppilaitos nimi toimipaikkakoodi]
    (db/transaction
      (oppilaitos-arkisto/lisaa-toimipaikka! {:oppilaitos oppilaitos
                                              :nimi nimi
                                              :toimipaikkakoodi toimipaikkakoodi})
      {:status 200}))
  (c/DELETE "/toimipaikka/:toimipaikkakoodi" [toimipaikkakoodi]
    (db/transaction
      (oppilaitos-arkisto/poista-toimipaikka! toimipaikkakoodi)
      {:status 200})))
