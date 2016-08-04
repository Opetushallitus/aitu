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
            aitu.compojure-util
            [aitu.toimiala.henkilo :as henkilo]
            [aitu.toimiala.kayttajaoikeudet :as kayttajaoikeudet]
            [aitu.toimiala.skeema :as skeema]
            [oph.common.util.util :refer [uusin-muokkausaika]]
            [valip.predicates :refer [present? max-length]]
            [ring.util.response :refer [response]]
            [compojure.api.core :refer [GET POST PUT defroutes]]
            [schema.core :as s]
            [aitu.util :refer [muodosta-csv]]))

(defn ^:private henkilon-nimi [henkilo]
  (str (:sukunimi henkilo) ", " (:etunimi henkilo)))

(defn henkilon-validointisaannot
  ([]
    [[:etunimi present? :pakollinen]
     [:sukunimi present? :pakollinen]
     [:postinumero (max-length 5) :liian-pitka]])
  ([henkiloid kayttaja-oid]
    (conj (henkilon-validointisaannot)
     ; TODO: mitä tapahtuu jos annetaan parametreina nil ja nil?
     [:kayttaja_oid #(or (kayttaja-arkisto/kayttaja-liitetty-henkiloon? henkiloid %) ; käyttäjä liitetty tähän henkilöön
                       (not (kayttaja-arkisto/kayttaja-liitetty-johonkin-henkiloon? %))) ; tai ei mihinkään henkilöön
      ; validointiviesti
      [:kayttaja-kaytossa (henkilon-nimi
                            (when kayttaja-oid
                              (kayttaja-arkisto/hae-kayttajaan-liitetty-henkilo kayttaja-oid)))]])))

(defn rajaa-henkilon-kentat [henkilo]
  (-> henkilo
    (select-keys [:henkiloid :etunimi :sukunimi :jasenyydet])
    (update-in [:jasenyydet] (partial map #(select-keys % [:nimi_fi :nimi_sv :diaarinumero :toimikausi_alku :toimikausi_loppu])))))

(defn piilota-salaiset-kentat
  "Piilottaa osan tietokentistä jos käyttäjä ei ole ylläpitäjä"
  [henkilo]
  (if (kayttajaoikeudet/yllapitaja?)
    henkilo
    (if (seq? henkilo)
      (henkilo/piilota-salaiset-henkiloilta henkilo)
      (henkilo/poista-salaiset-henkilolta henkilo))))

(def henkilokenttien-jarjestys [:sukunimi :etunimi :toimikunta_fi :toimikunta_sv :rooli :jasenyys_alku :jasenyys_loppu
                                :jasenyys_status
                                :sahkoposti :puhelin :organisaatio :osoite :postinumero :postitoimipaikka :aidinkieli])

(defroutes raportti-reitit
  (GET "/csv" req
    :kayttooikeus :henkilo_haku
    (let [henkilot (->>
                     (arkisto/hae-ehdoilla (:params req))
                     piilota-salaiset-kentat
                     (map #(select-keys % henkilokenttien-jarjestys)))]
      (csv-download-response (muodosta-csv henkilot henkilokenttien-jarjestys) "henkilot.csv"))))

(defn tarkista-paivitettavat-kentat
  "Tarkistaa että järjestö-käyttäjä ei yritä päivittää kenttiä, joiden käsittely ei ole sallittua"
  [henkilodto]
  (when (= (:roolitunnus kayttajaoikeudet/*current-user-authmap*) "JARJESTO")
    (cond
      (:kayttaja_oid henkilodto)
        (throw (IllegalArgumentException. "JARJESTO-roolilla henkilölle ei voi asettaa kayttaja_oid-tietoa"))
      (:lisatiedot henkilodto)
        (throw (IllegalArgumentException. "JARJESTO-roolilla henkilölle ei voi käsitellä lisätieto-kenttää")))))

(defroutes reitit
  (POST "/" [& henkilodto]
    :kayttooikeus :henkilo_lisays
    (validoi henkilodto (henkilon-validointisaannot) ((i18n/tekstit) :validointi)
      (s/validate skeema/HenkilonTiedot henkilodto)
      (tarkista-paivitettavat-kentat henkilodto)
      (let [uusi-henkilo (arkisto/lisaa! henkilodto)]
        (response-or-404 uusi-henkilo))))

  (GET "/" [toimikausi :as req]
    :kayttooikeus :henkilo_haku
    (let [henkilot (case toimikausi
                     "nykyinen_voimassa" (arkisto/hae-nykyiset-voimassa)
                     "nykyinen" (arkisto/hae-nykyiset)
                     "tuleva" (arkisto/hae-tulevat)
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

  (GET "/:henkiloid" [henkiloid]
    :kayttooikeus :henkilo_haku
    (response-or-404 (henkilo/taydenna-henkilo
                     (piilota-salaiset-kentat (arkisto/hae-hlo-ja-ttk (Integer/parseInt henkiloid) (:jarjesto kayttajaoikeudet/*current-user-authmap*))))))

  (GET "/nimi/:etunimi/:sukunimi" [etunimi sukunimi]
    :kayttooikeus :henkilo_haku
    (response-or-404 (piilota-salaiset-kentat
                     (arkisto/hae-hlo-nimella etunimi sukunimi (:jarjesto kayttajaoikeudet/*current-user-authmap*)))))

  (GET "/nimi/" [termi]
    :kayttooikeus :henkilo_haku
    (response-or-404 (piilota-salaiset-kentat
                     (arkisto/hae-hlo-nimen-osalla termi (:jarjesto kayttajaoikeudet/*current-user-authmap*)))))

  (PUT "/:henkiloid"
    []
    :path-params [henkiloid :- s/Int]
    :body-params [sukunimi etunimi organisaatio jarjesto keskusjarjesto aidinkieli sukupuoli sahkoposti puhelin kayttaja
                  osoite postinumero postitoimipaikka lisatiedot nayttomestari sahkoposti_julkinen osoite_julkinen puhelin_julkinen syntymavuosi kokemusvuodet

                  ; TODO tarpeettomia, pitäisi poistaa frontista
                  keskusjarjesto_nimi jasenyys muutettu_kayttaja luotuaika henkiloid :- s/Int luotu_kayttaja jarjesto_nimi_fi muutettuaika jarjesto_nimi_sv]
    :kayttooikeus [:henkilo_paivitys {:henkiloid henkiloid, :kayttaja (:oid kayttaja)}]
      (let [id henkiloid
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
          (tarkista-paivitettavat-kentat henkilodto)
          (arkisto/paivita! henkilodto)
          (response-or-404 henkilodto)))))
