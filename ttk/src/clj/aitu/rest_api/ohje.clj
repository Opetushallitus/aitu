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

(ns aitu.rest-api.ohje
  (:require [aitu.infra.ohje-arkisto :as arkisto]
            aitu.restructure
            [oph.common.util.http-util :refer [response-or-404]]
            [compojure.api.core :refer [GET PUT defroutes]]))

(defroutes reitit
  (GET "/:ohjetunniste" [ohjetunniste]
    :kayttooikeus :ohjeet_luku
    (if-let [ohje (arkisto/hae ohjetunniste)]
      (response-or-404 ohje)
      {:status 200}))
  (PUT "/:ohjetunniste" [ohjetunniste teksti_fi teksti_sv]
    :kayttooikeus :ohje_muokkaus
    (arkisto/muokkaa-tai-luo-uusi! {:ohjetunniste ohjetunniste
                                    :teksti_fi teksti_fi
                                    :teksti_sv teksti_sv})
    {:status 200}))
