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
            [aitu.infra.kayttaja-arkisto :as arkisto]
            [aitu.toimiala.kayttajaoikeudet :refer [paivita-kayttajan-toimikuntakohtaiset-oikeudet
                                                    paivita-kayttajan-sopimuskohtaiset-oikeudet
                                                    liita-kayttajan-henkilo-oikeudet]]
            [aitu.toimiala.kayttajaroolit :refer [kayttajaroolit]]
            [aitu.infra.kayttajaoikeudet-arkisto :as ko-arkisto]
            [oph.common.util.http-util :refer [json-response]]
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
    (json-response (arkisto/hae-impersonoitava-termilla termi)))
  (GET "/toimikuntakayttajat" [termi]
    :kayttooikeus :toimikuntakayttaja-listaus
    (json-response (arkisto/hae-toimikuntakayttajat-termilla termi)))
  (GET "/" []
    :kayttooikeus :kayttajan_tiedot
    (let [oikeudet (ko-arkisto/hae-oikeudet)
          roolitunnus (:roolitunnus oikeudet)]
      (if (or (= roolitunnus (:yllapitaja kayttajaroolit))
              (= roolitunnus (:oph-katselija kayttajaroolit)))
        (json-response oikeudet)
        (-> oikeudet
            paivita-kayttajan-toimikuntakohtaiset-oikeudet
            paivita-kayttajan-sopimuskohtaiset-oikeudet
            liita-kayttajan-henkilo-oikeudet
            json-response))))
  (GET "/jarjesto" []
    :kayttooikeus :kayttajan_tiedot
    (json-response (or (arkisto/hae-jarjesto (:jarjesto ko/*current-user-authmap*)) {})))
  (GET "/:oid" [oid]
    :kayttooikeus :omat_tiedot
    (json-response (arkisto/hae oid))))

