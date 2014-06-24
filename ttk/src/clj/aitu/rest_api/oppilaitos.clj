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
  (:require [compojure.core :as c]
            [cheshire.core :as cheshire]
            [korma.db :as db]
            [aitu.compojure-util :as cu]
            [aitu.infra.oppilaitos-arkisto :as arkisto]
            [aitu.rest-api.http-util :refer [cachable-json-response json-response]]
            [aitu.toimiala.oppilaitos :as oppilaitos]
            [aitu.toimiala.skeema :refer :all]
            [compojure.api.sweet :refer :all]))

(defroutes* reitit
  (GET* "/" [:as req]
    :summary "Hakee kaikki oppilaitokset"
    :return [OppilaitosTiedot]
    (cu/autorisoitu-transaktio :yleinen-rest-api nil
      (cachable-json-response req (arkisto/hae-kaikki-julkiset-tiedot) [OppilaitosTiedot])))

  (GET*  "/haku" [termi :as req]
    :summary "Hakee kaikki oppilaitokset joiden nimi sisältää annetun termin"
    :return [OppilaitosLinkki]
    (cu/autorisoitu-transaktio :yleinen-rest-api nil
      (cachable-json-response req (arkisto/hae-termilla termi) [OppilaitosLinkki])))

  (GET* "/:oppilaitoskoodi" [oppilaitoskoodi]
    :summary "Hakee oppilaitoksen oppilaitoskoodilla"
    :return OppilaitosLaajatTiedot
    (cu/autorisoitu-transaktio :yleinen-rest-api nil
      (json-response (oppilaitos/taydenna-oppilaitos (arkisto/hae oppilaitoskoodi)) OppilaitosLaajatTiedot)))

  (GET* "/haku/ala" [termi :as req]
    :summary "Hakee kaikki oppilaitokset joiden opintoalan, tutkinnon, osaamisalan tai tutkinnonosan nimi sisältää annetun termin"
    :return [OppilaitosTiedot]
    (cu/autorisoitu-transaktio :yleinen-rest-api nil
      (cachable-json-response req (arkisto/hae-alalla termi) [OppilaitosTiedot]))))
