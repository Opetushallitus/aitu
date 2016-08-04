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

(ns aitu.rest-api.tiedote
  (:require [aitu.infra.tiedote-arkisto :as arkisto]
            aitu.compojure-util
            [oph.common.util.http-util :refer [response-or-404]]
            [compojure.api.core :refer [DELETE GET POST defroutes]]))

(defroutes reitit
  (GET "/:tiedoteid" [tiedoteid]
    :kayttooikeus :etusivu
    (let [tiedoteid (Integer/parseInt tiedoteid)
          tiedote (arkisto/hae tiedoteid)]
      (-> (if (nil? tiedote)
            {:tiedoteid tiedoteid}
            tiedote)
        response-or-404)))

  (POST "/:tiedoteid" [tiedoteid teksti_fi teksti_sv]
    :kayttooikeus :tiedote_muokkaus
    (let [tiedote (arkisto/poista-ja-lisaa! (Integer/parseInt tiedoteid) {:teksti_fi teksti_fi :teksti_sv teksti_sv})]
        (response-or-404 tiedote)))

  (DELETE "/:tiedoteid" [tiedoteid]
    :kayttooikeus :tiedote_muokkaus
    (arkisto/poista! (Integer/parseInt tiedoteid))
    {:status 200}))
