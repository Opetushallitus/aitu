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

(ns aitu.rest-api.henkilo
  (:require [cheshire.core :as cheshire]
            [aitu.infra.kayttaja-arkisto :as kayttaja-arkisto]
            [aitu.infra.henkilo-arkisto :as arkisto]
            [aitu.infra.i18n :as i18n]
            [oph.common.util.http-util :refer :all]
            [aitu.rest-api.http-util :refer :all]
            [aitu.toimiala.henkilo :as henkilo]
            [aitu.toimiala.kayttajaoikeudet :as kayttajaoikeudet]
            [aitu.toimiala.skeema :as skeema]
            [oph.common.util.util :refer [uusin-muokkausaika]]
            [valip.predicates :refer [present? max-length]]
            [ring.util.response :refer [response]]
            [aitu.compojure-util :as cu :refer [GET* POST* PUT*]]
            [compojure.api.core :refer [defroutes*]]
            [schema.core :as s]
            [aitu.util :refer [muodosta-csv]]))

(defn ^:private henkilon-nimi [henkilo]
  (str (:sukunimi henkilo) ", " (:etunimi henkilo)))

(defn henkilon-validointisaannot
  ([]
    (henkilon-validointisaannot nil nil))
  ([henkiloid kayttaja-oid]
    [[:etunimi present? :pakollinen]
     [:sukunimi present? :pakollinen]
     [:postinumero (max-length 5) :liian-pitka]
     [:kayttaja_oid #(or (kayttaja-arkisto/kayttaja-liitetty-henkiloon? henkiloid %)
                         (not (kayttaja-arkisto/kayttaja-liitetty-johonkin-henkiloon? %))) [:kayttaja-kaytossa (henkilon-nimi (kayttaja-arkisto/hae-kayttajaan-liitetty-henkilo kayttaja-oid))]]]))

(defn rajaa-henkilon-kentat [henkilo]
  (-> henkilo
    (select-keys [:henkiloid :etunimi :sukunimi :jasenyydet])
    (update-in [:jasenyydet] (partial map #(select-keys % [:nimi_fi :nimi_sv :diaarinumero :toimikausi_alku :toimikausi_loppu])))))

(def henkilokenttien-jarjestys [:sukunimi :etunimi :toimikunta_fi :toimikunta_sv :rooli :jasenyys_alku :jasenyys_loppu
                                :sahkoposti :puhelin :organisaatio :osoite :postinumero :postitoimipaikka :aidinkieli])

(defroutes* raportti-reitit
  (GET* "/csv" req
    :kayttooikeus :henkilo_haku
    (csv-download-response (muodosta-csv (arkisto/hae-ehdoilla (assoc (:params req) :avaimet henkilokenttien-jarjestys))
                                         henkilokenttien-jarjestys)
                           "henkilot.csv")))

(defroutes* reitit
  (POST* "/" [& henkilodto]
    :kayttooikeus :henkilo_lisays
    (validoi henkilodto (henkilon-validointisaannot) ((i18n/tekstit) :validointi)
      (s/validate skeema/HenkilonTiedot henkilodto)
      (if (and (= (:roolitunnus kayttajaoikeudet/*current-user-authmap*) "JARJESTO")
               (:kayttaja_oid henkilodto))
        (throw (IllegalArgumentException. "JARJESTO-roolilla henkilölle ei voi asettaa kayttaja_oid-tietoa")))
      (let [uusi-henkilo (arkisto/lisaa! henkilodto)]
        (json-response uusi-henkilo))))

  (GET* "/" [toimikausi :as req]
    :kayttooikeus :henkilo_haku
    (let [henkilot (case toimikausi
                     "nykyinen_voimassa" (arkisto/hae-nykyiset-voimassa)
                     "nykyinen" (arkisto/hae-nykyiset)
                     (arkisto/hae-kaikki))
          cache-muokattu (get-cache-date req)
          henkilot-muokattu (->
                              (uusin-muokkausaika
                                henkilot
                                [:muutettuaika]
                                [:toimikunnat :muutettuaika]
                                [:toimikunnat :toimikunta_muutettuaika])
                              (.withMillisOfSecond 0))]
      (if (> 0 (compare cache-muokattu henkilot-muokattu))
        {:status 200
         :body (cheshire/generate-string (map rajaa-henkilon-kentat henkilot))
         :headers (get-cache-headers henkilot-muokattu)}
        {:status 304})))

  (GET* "/:henkiloid" [henkiloid]
    :kayttooikeus :henkilo_haku
    (json-response (henkilo/taydenna-henkilo (arkisto/hae-hlo-ja-ttk (Integer/parseInt henkiloid)))))

  (GET* "/nimi/:etunimi/:sukunimi" [etunimi sukunimi]
    :kayttooikeus :henkilo_haku
    (json-response (arkisto/hae-hlo-nimella etunimi sukunimi)))

  (GET* "/nimi/" [termi]
    :kayttooikeus :henkilo_haku
    (json-response (arkisto/hae-hlo-nimen-osalla termi)))

  (PUT* "/:henkiloid"
    [sukunimi etunimi henkiloid organisaatio jarjesto keskusjarjesto aidinkieli sukupuoli sahkoposti puhelin kayttaja
     osoite postinumero postitoimipaikka lisatiedot nayttomestari sahkoposti_julkinen osoite_julkinen puhelin_julkinen syntymavuosi kokemusvuodet]
    :kayttooikeus :henkilo_paivitys
    :konteksti {:henkiloid henkiloid, :kayttaja (:oid kayttaja)}
      (let [id (Integer/parseInt henkiloid)
            kayttaja-oid (:oid kayttaja)
            henkilodto {:henkiloid id
                        :kayttaja_oid kayttaja-oid
                        :etunimi etunimi
                        :sukunimi sukunimi
                        :organisaatio organisaatio
                        :aidinkieli aidinkieli
                        :sukupuoli sukupuoli
                        :sahkoposti sahkoposti
                        :sahkoposti_julkinen sahkoposti_julkinen
                        :puhelin puhelin
                        :puhelin_julkinen puhelin_julkinen
                        :osoite osoite
                        :osoite_julkinen osoite_julkinen
                        :postinumero postinumero
                        :postitoimipaikka postitoimipaikka
                        :jarjesto (:jarjesto jarjesto)
                        :lisatiedot lisatiedot
                        :nayttomestari nayttomestari
                        :syntymavuosi syntymavuosi
                        :kokemusvuodet kokemusvuodet}]
        (validoi henkilodto (henkilon-validointisaannot id kayttaja-oid) ((i18n/tekstit) :validointi)
          (s/validate skeema/Henkilo henkilodto)
          (arkisto/paivita! henkilodto)
          (json-response henkilodto)))))
