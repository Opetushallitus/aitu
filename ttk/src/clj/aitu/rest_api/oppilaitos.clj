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

(ns aitu.rest-api.oppilaitos
  (:require [compojure.api.core :refer [GET defroutes]]
            aitu.compojure-util
            [aitu.infra.oppilaitos-arkisto :as arkisto]
            [oph.common.util.http-util :refer [cachable-response response-or-404]]
            [aitu.toimiala.skeema :refer :all]
            [aitu.util :refer [muodosta-csv]]
            [oph.common.util.http-util :refer [csv-download-response]]))

(def oppilaitoskenttien-jarjestys [:nimi :oppilaitoskoodi :sopimusten_maara])

(defroutes raportti-reitit
  (GET "/csv" req
    :kayttooikeus :yleinen-rest-api
    (csv-download-response (muodosta-csv (arkisto/hae-ehdoilla (assoc (:params req) :avaimet oppilaitoskenttien-jarjestys))
                                         oppilaitoskenttien-jarjestys)
                           "oppilaitokset.csv")))

(defroutes reitit
  (GET "/" [:as req]
    :summary "Hakee kaikki oppilaitokset"
    :return [OppilaitosLista]
    :kayttooikeus :yleinen-rest-api
    (cachable-response req (arkisto/hae-kaikki-julkiset-tiedot)))

  (GET "/haku" [termi :as req]
    :summary "Hakee kaikki oppilaitokset joiden nimi sisältää annetun termin"
    :return [OppilaitosLinkki]
    :kayttooikeus :yleinen-rest-api
    (cachable-response req (arkisto/hae-termilla termi)))

  (GET "/:oppilaitoskoodi" [oppilaitoskoodi]
    :summary "Hakee oppilaitoksen oppilaitoskoodilla"
    :return OppilaitosLaajatTiedot
    :kayttooikeus :yleinen-rest-api
    (response-or-404 (arkisto/hae oppilaitoskoodi)))

  (GET "/haku/ala" [tunnus sopimuksia :as req]
    :summary "Hakee kaikki oppilaitokset joiden opintoalan, tutkinnon, osaamisalan tai tutkinnonosan tunnus on annettu"
    :return [OppilaitosLista]
    :kayttooikeus :yleinen-rest-api
    (cachable-response req (arkisto/hae-ehdoilla {:tunnus tunnus
                                                  :sopimuksia sopimuksia}))))
