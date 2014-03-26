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
  (:require [compojure.core :as c]
            [aitu.infra.tiedote-arkisto :as arkisto]
            [aitu.rest-api.http-util :refer [json-response]]
            [aitu.compojure-util :as cu]
            [cheshire.core :as cheshire]
            [korma.db :as db]))

(c/defroutes reitit
  (cu/defapi :yleinen-rest-api nil :get "/" []
    (let [tiedote (arkisto/hae)]
      (-> (if (nil? tiedote)
            {}
            tiedote)
        json-response)))

  (cu/defapi :tiedote_muokkaus nil :post "/" [teksti_fi teksti_sv]
    (let [tiedote (arkisto/poista-ja-lisaa! {:teksti_fi teksti_fi :teksti_sv teksti_sv})]
        {:status 200
         :body (cheshire/generate-string tiedote)}))

  (cu/defapi :tiedote_muokkaus nil :delete "/" []
    (arkisto/poista!)
    {:status 200}))
