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

(ns aitu.rest-api.koulutustoimija
  (:require [compojure.api.core :refer [GET defroutes]]
            [aitu.infra.koulutustoimija-arkisto :as arkisto]
            [aitu.toimiala.koulutustoimija :as koulutustoimija]
            [oph.common.util.http-util :refer [csv-download-response cachable-response response-or-404]]
            [aitu.toimiala.skeema :refer :all]
            [aitu.util :refer [muodosta-csv]]))

(def koulutustoimijakenttien-jarjestys [:nimi_fi :nimi_sv :ytunnus :sopimusten_maara])

(defroutes raportti-reitit
  (GET "/csv" req
    :kayttooikeus :yleinen-rest-api
    (csv-download-response (muodosta-csv (arkisto/hae-ehdoilla (assoc (:params req) :avaimet koulutustoimijakenttien-jarjestys))
                                         koulutustoimijakenttien-jarjestys)
                           "koulutustoimijat.csv")))

(defroutes reitit
  (GET "/" req
    :summary "Hakee kaikki koulutustoimijat"
    :return [KoulutustoimijaLista]
    :kayttooikeus :yleinen-rest-api
    (cachable-response req (arkisto/hae-julkiset-tiedot)))
  (GET "/haku" [termi :as req]
    :summary "Hakee kaikki koulutustoimijat joiden nimi sisältää annetun termin"
    :return [KoulutustoimijaLinkki]
    :kayttooikeus :yleinen-rest-api
    (response-or-404 (arkisto/hae-termilla termi)))
  (GET "/nimet" req
    :summary "Hakee listan koulutustoimijoiden nimistä"
    :kayttooikeus :yleinen-rest-api
    :return [KoulutustoimijaLinkki]
    (cachable-response req (arkisto/hae-nimet)))
  (GET "/:ytunnus" [ytunnus]
    :summary "Hakee koulutustoimijan y-tunnuksella"
    :return KoulutustoimijaLaajatTiedot
    :kayttooikeus :yleinen-rest-api
    (response-or-404 (arkisto/hae ytunnus)))
  (GET "/haku/ala" req
    :summary "Hakee kaikki koulutustoimijat joiden opintoalan, tutkinnon, osaamisalan tai tutkinnonosan tunnus on annettu"
    :return [KoulutustoimijaLista]
    :kayttooikeus :yleinen-rest-api
    (println "requ " (:params req))
;    (cachable-response req (arkisto/hae-ehdoilla {:tunnus nil}))))
    (cachable-response req (arkisto/hae-ehdoilla {:tunnus (:tunnus (:params req))
                                                  :sopimuksia (:sopimuksia (:params req))})))) ; TODO: default
