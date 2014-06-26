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

(ns aitu.test-api.koulutustoimija
  "Testien tarvitsemat REST-rajapinnat jos niitä on. Näitä ei tarvita normaalista käyttöliittymästä koskaan"
  (:require [compojure.core :as c]
            [aitu.infra.koulutustoimija-arkisto :as koulutustoimija-arkisto]
            [korma.db :as db]))

(c/defroutes reitit
  (c/POST "/" [ytunnus nimi_fi nimi_sv sahkoposti puhelin osoite postinumero postitoimipaikka]
    (db/transaction
      (koulutustoimija-arkisto/lisaa! {:ytunnus ytunnus
                             :nimi_fi nimi_fi
                             :nimi_sv nimi_sv
                             :sahkoposti sahkoposti
                             :puhelin puhelin
                             :osoite osoite
                             :postinumero postinumero
                             :postitoimipaikka postitoimipaikka})
      {:status 200}))
  (c/DELETE "/:ytunnus" [ytunnus]
    (db/transaction
      (koulutustoimija-arkisto/poista! ytunnus)
      {:status 200})))
