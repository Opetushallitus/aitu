;; Copyright (c) 2015 The Finnish National Board of Education - Opetushallitus
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

(ns aitu.rest-api.tutkinnonosa
  (:require [aitu.infra.tutkinnonosa-arkisto :as arkisto]
            aitu.compojure-util
            [compojure.api.core :refer [GET defroutes]]
            [oph.common.util.http-util :refer [response-or-404]]))

(defroutes reitit
  (GET "/" [tutkintoversioid]
    :summary "Tutkinnon osat tutkintoversion perusteella. (ePerusteet on tietojen master-järjestelmä)"
    :kayttooikeus :yleinen-rest-api
    (let [osat (if (nil? tutkintoversioid) 
                 (arkisto/hae nil)
                 (arkisto/hae-versiolla (Integer/parseInt tutkintoversioid)))]
    (response-or-404 osat))))
