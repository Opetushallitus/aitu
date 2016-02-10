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

(ns aitu.rest-api.tutkinto
  (:require [aitu.infra.tutkinto-arkisto :as arkisto]
            [aitu.toimiala.tutkinto :as tutkinto]
            [aitu.toimiala.voimassaolo.saanto.tutkinto :as voimassaolo]
            [aitu.compojure-util :as cu :refer [GET*]]
            [compojure.api.core :refer [defroutes]]
            [aitu.util :refer [muodosta-csv]]
            [oph.common.util.http-util :refer [csv-download-response cachable-json-response json-response]]))

(def tutkintokenttien-jarjestys [:tutkintotunnus :nimi_fi :nimi_sv :peruste :opintoala_fi :opintoala_sv])

(def raporttikenttien-jarjestys [:opintoalatunnus :opintoala_fi :tutkintotunnus :tutkinto_fi :tutkinto_sv :tutkintotaso
                                 :peruste :kieli :ytunnus :koulutustoimija_fi :toimikunta :toimikunta_fi :tilikoodi :toimikausi_alku :toimikausi_loppu
                                 :lukumaara])

(defroutes raportti-reitit
  (GET* "/csv" req
    :kayttooikeus :yleinen-rest-api
    (csv-download-response
      (muodosta-csv (arkisto/hae-ehdoilla
                      (assoc (:params req) :avaimet tutkintokenttien-jarjestys))
                    tutkintokenttien-jarjestys)
      "tutkinnot.csv"))
  (GET* "/raportti" req
    :kayttooikeus :raportti
    (csv-download-response
      (muodosta-csv (arkisto/hae-raportti (assoc (:params req) :avaimet raporttikenttien-jarjestys))
                     raporttikenttien-jarjestys)
      "nayttotutkinnot_raportti.csv")))

(defn rajaa-tutkinnon-kentat [tutkinto]
  (select-keys tutkinto [:tutkintotunnus :nimi_fi :nimi_sv :opintoala_nimi_fi :opintoala_nimi_sv
                         :opintoala :tutkintotaso :peruste :voimassa]))

(defroutes reitit
  (GET* "/" [:as req]
    :kayttooikeus :yleinen-rest-api
    (cachable-json-response req (map (comp rajaa-tutkinnon-kentat voimassaolo/taydenna-tutkinnon-voimassaolo)
                                     (arkisto/hae-kaikki))))
  (GET* "/:tutkintotunnus" [tutkintotunnus]
    :kayttooikeus :yleinen-rest-api
    (json-response (tutkinto/taydenna-tutkinto (arkisto/hae tutkintotunnus))))
  (GET* "/haku/osat" [termi :as req]
    :kayttooikeus :yleinen-rest-api
    (cachable-json-response req (arkisto/hae-opintoalat-tutkinnot-osaamisalat-tutkinnonosat termi)))
  (GET* "/haku/tutkinnot" [termi :as req]
    :kayttooikeus :yleinen-rest-api
    (cachable-json-response req (arkisto/hae-opintoalat-tutkinnot termi))))
