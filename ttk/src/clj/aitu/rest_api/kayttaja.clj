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

(ns aitu.rest-api.kayttaja
  (:require [compojure.core :as c]
            [oph.korma.korma-auth :as ka]
            [aitu.infra.kayttaja-arkisto :as arkisto]
            [aitu.toimiala.kayttajaoikeudet :refer [paivita-kayttajan-toimikuntakohtaiset-oikeudet
                                                    paivita-kayttajan-sopimuskohtaiset-oikeudet
                                                    liita-kayttajan-henkilo-oikeudet
                                                    yllapitajarooli]]
            [aitu.infra.kayttajaoikeudet-arkisto :as ko-arkisto]
            [aitu.rest-api.http-util :refer [json-response]]
            [aitu.toimiala.kayttajaoikeudet :as ko]
            [aitu.compojure-util :as cu]
            [korma.db :as db]))

(c/defroutes reitit
  (cu/defapi :kayttajan_tiedot nil :get "/" []
             (let [oikeudet (ko-arkisto/hae-oikeudet)]
               (if (= (:roolitunnus oikeudet) yllapitajarooli)
                 (json-response oikeudet)
                 (-> oikeudet
                     paivita-kayttajan-toimikuntakohtaiset-oikeudet
                     paivita-kayttajan-sopimuskohtaiset-oikeudet
                     liita-kayttajan-henkilo-oikeudet
                     json-response))))
  (cu/defapi :omat_tiedot oid :get "/:oid" [oid]
    (json-response (arkisto/hae oid))))
