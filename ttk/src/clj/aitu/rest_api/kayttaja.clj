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
  (:require [oph.korma.korma-auth :as ka]
            aitu.restructure
            [aitu.infra.kayttaja-arkisto :as arkisto]
            [aitu.toimiala.kayttajaoikeudet :refer [paivita-kayttajan-toimikuntakohtaiset-oikeudet
                                                    paivita-kayttajan-sopimuskohtaiset-oikeudet
                                                    liita-kayttajan-henkilo-oikeudet]]
            [aitu.toimiala.kayttajaroolit :refer [kayttajaroolit]]
            [aitu.infra.kayttajaoikeudet-arkisto :as ko-arkisto]
            [oph.common.util.http-util :refer [response-or-404]]
            [aitu.toimiala.kayttajaoikeudet :as ko]
            [compojure.api.core :refer [GET POST defroutes]]))

(defroutes reitit
  (POST "/impersonoi" [:as {session :session}, oid]
    :kayttooikeus :impersonointi
    {:status 200
     :session (assoc session :impersonoitu-oid oid)})
  (POST "/lopeta-impersonointi" {session :session}
    :kayttooikeus :impersonointi-lopetus
    {:status 200
     :session (dissoc session :impersonoitu-oid)})
  (GET "/impersonoitava" [termi]
    :kayttooikeus :impersonointi
    (response-or-404 (arkisto/hae-impersonoitava-termilla termi)))
  (GET "/toimikuntakayttajat" [termi]
    :kayttooikeus :toimikuntakayttaja-listaus
    (response-or-404 (arkisto/hae-toimikuntakayttajat-termilla termi)))
  (GET "/" []
    :kayttooikeus :kayttajan_tiedot
    (let [oikeudet (ko-arkisto/hae-oikeudet)
          roolitunnus (:roolitunnus oikeudet)]
      (if (or (= roolitunnus (:yllapitaja kayttajaroolit))
              (= roolitunnus (:paivittaja kayttajaroolit))
              (= roolitunnus (:oph-katselija kayttajaroolit)))
        (response-or-404 oikeudet) ; roolipohjaiset, kontekstista riippumattomat, oikeudet
        (-> oikeudet ; kontekstisensitiiviset oikeudet
            paivita-kayttajan-toimikuntakohtaiset-oikeudet
            paivita-kayttajan-sopimuskohtaiset-oikeudet
            liita-kayttajan-henkilo-oikeudet
            response-or-404))))
  (GET "/jarjesto" []
    :kayttooikeus :kayttajan_tiedot
    (response-or-404 (or (arkisto/hae-jarjesto (:jarjesto ko/*current-user-authmap*)) {})))
  (GET "/:oid" [oid]
    :kayttooikeus :omat_tiedot
    (response-or-404 (arkisto/hae oid))))

