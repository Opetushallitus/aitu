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

(ns aitu.rest-api.haku
  (:require [aitu.infra.haku-arkisto :as arkisto]
            aitu.compojure-util
            [oph.common.util.http-util :refer [response-or-404]]
            [compojure.api.core :refer [GET defroutes]]))

(defroutes reitit
  (GET "/" [& ehdot]
    :summary "Pikahaku yksilöivällä tunnuksella eri käsitteisiin"
    :kayttooikeus :etusivu_haku
    
    (let [respo
          ; HUOM: Regex hoitaa tässä SQL-injektion. Arkisto-koodissa ei ole sanitointia.
          (if (nil? (re-matches #"[0-9/\-]+" (:tunnus ehdot)))
            {} ; kiellettyjä merkkejä
            (arkisto/hae-tunnuksella-ensimmainen (:tunnus ehdot))
            )]
      (response-or-404 respo))))
