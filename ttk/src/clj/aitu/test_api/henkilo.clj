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

(ns aitu.test-api.henkilo
  "Testien tarvitsemat REST-rajapinnat jos niitä on. Näitä ei tarvita normaalista käyttöliittymästä koskaan"
  (:require [compojure.core :as c]
            [cheshire.core :as cheshire]
            [aitu.infra.henkilo-arkisto :as arkisto]
            [aitu.rest-api.http-util :refer [validoi]]
            [valip.predicates :refer [present?]]
            [korma.db :as db]
            [aitu.toimiala.skeema :as skeema]
            [schema.core :as s])
  (:use aitu.toimiala.henkilo))

(c/defroutes reitit
  (c/DELETE "/:id" [id]
    (db/transaction
      (arkisto/poista! (Integer/parseInt id))
      {:status 200}))
  (c/DELETE "/" []
    (db/transaction
      (arkisto/tyhjenna!)
      {:status 200}))
  (c/POST "/:henkiloid"
          [sukunimi etunimi henkiloid organisaatio jarjesto keskusjarjesto aidinkieli sukupuoli sahkoposti puhelin
           osoite postinumero postitoimipaikka lisatiedot nayttomestari sahkoposti_julkinen osoite_julkinen puhelin_julkinen kayttaja_oid]
    (db/transaction
      (let [henkilodto {:henkiloid (Integer/parseInt henkiloid)
                        :etunimi etunimi
                        :sukunimi sukunimi
                        :organisaatio organisaatio
                        :aidinkieli aidinkieli
                        :sukupuoli sukupuoli
                        :sahkoposti sahkoposti
                        :sahkoposti_julkinen sahkoposti_julkinen
                        :puhelin puhelin
                        :puhelin_julkinen puhelin_julkinen
                        :osoite osoite
                        :osoite_julkinen osoite_julkinen
                        :postinumero postinumero
                        :postitoimipaikka postitoimipaikka
                        :jarjesto jarjesto
                        :lisatiedot lisatiedot
                        :nayttomestari nayttomestari
                        :kayttaja_oid kayttaja_oid}]
        (validoi henkilodto
                 [[:etunimi present? :pakollinen]
                  [:sukunimi present? :pakollinen]]
                 {}
          (s/validate skeema/Henkilo henkilodto)
          (let [uusi-henkilo (arkisto/lisaa! henkilodto)]
            {:status 200
             :body (cheshire/generate-string uusi-henkilo)}))))))
