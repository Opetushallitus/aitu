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
  (:require [compojure.core :as c]
            [cheshire.core :as cheshire]
            [aitu.infra.kayttaja-arkisto :as kayttaja-arkisto]
            [aitu.infra.henkilo-arkisto :as arkisto]
            [aitu.infra.i18n :as i18n]
            [oph.common.util.http-util :refer :all]
            [aitu.rest-api.http-util :refer :all]
            [aitu.toimiala.henkilo :as henkilo]
            [aitu.toimiala.skeema :as skeema]
            [oph.common.util.util :refer [uusin-muokkausaika]]
            [valip.predicates :refer [present? max-length]]
            [ring.util.response :refer [response]]
            [aitu.compojure-util :as cu]
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

(def henkilokenttien-jarjestys [:sukunimi :etunimi :toimikunta_fi :toimikunta_sv :rooli :jasenyys_alku :jasenyys_loppu
                                :sahkoposti :puhelin :organisaatio :osoite :postinumero :postitoimipaikka :aidinkieli])

(c/defroutes raportti-reitit
  (cu/defapi :henkilo_haku nil :get "/csv" req
    (csv-download-response (muodosta-csv (arkisto/hae-ehdoilla (assoc (:params req) :avaimet henkilokenttien-jarjestys))
                                         henkilokenttien-jarjestys)
                           "henkilot.csv")))

(c/defroutes reitit
  (cu/defapi :henkilo_lisays nil :post "/"
    [& henkilodto]
    (validoi henkilodto (henkilon-validointisaannot) ((i18n/tekstit) :validointi)
      (s/validate skeema/HenkilonTiedot henkilodto)
      (let [uusi-henkilo (arkisto/lisaa! henkilodto)]
        {:status 200
         :body   (cheshire/generate-string uusi-henkilo)})))

  (cu/defapi :henkilo_haku nil :get "/" [toimikausi :as req]
    (let [henkilot (if (= toimikausi "nykyinen")
                     (arkisto/hae-nykyiset)
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
         :body (cheshire/generate-string henkilot)
         :headers (get-cache-headers henkilot-muokattu)}
        {:status 304})))

  (cu/defapi :henkilo_haku nil :get "/:henkiloid" [henkiloid]
    (json-response (henkilo/taydenna-henkilo (arkisto/hae-hlo-ja-ttk (Integer/parseInt henkiloid)))))

  (cu/defapi :henkilo_haku nil :get "/nimi/:etunimi/:sukunimi" [etunimi sukunimi]
    (json-response (arkisto/hae-hlo-nimella etunimi sukunimi)))

  (cu/defapi :henkilo_haku nil :get "/nimi/" [termi]
      (json-response (arkisto/hae-hlo-nimen-osalla termi)))

  (cu/defapi :henkilo_paivitys {:henkiloid henkiloid, :kayttaja (:oid kayttaja)} :put "/:henkiloid"
    [sukunimi etunimi henkiloid organisaatio jarjesto keskusjarjesto aidinkieli sukupuoli sahkoposti puhelin kayttaja
     osoite postinumero postitoimipaikka lisatiedot nayttomestari sahkoposti_julkinen osoite_julkinen puhelin_julkinen syntymavuosi kokemusvuodet]
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
