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

(ns aitu.infra.oppilaitos-arkisto
  (:require  [korma.core :as sql]
             [aitu.infra.jarjestamissopimus-arkisto :as sopimus-arkisto]
             [aitu.integraatio.sql.oppilaitos :as oppilaitos-kaytava]
             [aitu.toimiala.oppilaitos :as toimiala]
             [aitu.util :refer [select-and-rename-keys]]
             [oph.common.util.util :refer [sisaltaako-kentat?]]
             [oph.korma.korma :refer :all]
             )
  (:use [aitu.integraatio.sql.korma]))

(defn ^:test-api poista!
  "Poistaa oppilaitoksen."
  [oppilaitoskoodi]
  (sql/delete oppilaitos
    (sql/where {:oppilaitoskoodi oppilaitoskoodi})))

(defn ^:integration-api lisaa!
  "Lisää uuden oppilaitoksen."
  [uusi-oppilaitos]
  (sql/insert oppilaitos
    (sql/values uusi-oppilaitos)))

(defn ^:integration-api paivita!
  "Päivittää oppilaitoksen."
  [laitos]
  (sql/update oppilaitos
    (sql/set-fields (dissoc laitos :oppilaitoskoodi))
    (sql/where {:oppilaitoskoodi (:oppilaitoskoodi laitos)})))

(defn ^:integration-api lisaa-toimipaikka!
  "Lisää toimipaikan"
  [t]
  (sql/insert toimipaikka
    (sql/values t)))

(defn ^:integration-api paivita-toimipaikka!
  [t]
  (sql/update toimipaikka
    (sql/set-fields (dissoc t :toimipaikkakoodi))
    (sql/where {:toimipaikkakoodi (:toimipaikkakoodi t)})))

(defn ^:test-api poista-toimipaikka!
  "Poistaa kaikki toimipaikat oppilaitokselta"
  [toimipaikkakoodi]
  (sql/delete toimipaikka
    (sql/where {:toimipaikkakoodi toimipaikkakoodi})))

(defn hae-kaikki-julkiset-tiedot
  "Hakee kaikkien oppilaitokset julkiset tiedot"
  []
  (sql/select oppilaitos
    (sql/fields :oppilaitoskoodi :nimi :kieli :muutettu_kayttaja :luotu_kayttaja :muutettuaika :luotuaika
                :sahkoposti :puhelin :osoite :postinumero :postitoimipaikka :www_osoite :alue :koulutustoimija
                (sql/raw "(select count(*) from jarjestamissopimus where tutkintotilaisuuksista_vastaava_oppilaitos = oppilaitoskoodi and voimassa) as sopimusten_maara"))
    (sql/order :nimi)))

(defn hae-kaikki
  "Hakee kaikkien oppilaitosten kaikki tiedot"
  []
  (sql/select oppilaitos))

(defn hae-kaikki-toimipaikat-julkiset-tiedot []
  (sql/select toimipaikka
    (sql/fields :toimipaikkakoodi :nimi :kieli :muutettu_kayttaja :luotu_kayttaja :muutettuaika :luotuaika
                :sahkoposti :puhelin :osoite :postinumero :postitoimipaikka :www_osoite :oppilaitos)
    (sql/order :nimi)))

(defn hae-kaikki-toimipaikat []
  (sql/select toimipaikka))

(defn hae-alalla
  "Hakee kaikki tietyn alan oppilaitokset. Ala sisältää opintoalan, tutkinnon, osaamisalan ja tutkinnon osan."
  [ala]
  (let [termi (str "%" ala "%")]
    (sql/select oppilaitos
      (sql/fields :oppilaitoskoodi :nimi :kieli :koulutustoimija :alue :muutettu_kayttaja :luotu_kayttaja :muutettuaika :luotuaika
                  :sahkoposti :puhelin :osoite :postinumero :postitoimipaikka :www_osoite
                  [(sql/subselect jarjestamissopimus
                     (sql/aggregate (count :*) :count)
                     (sql/where {:jarjestamissopimus.tutkintotilaisuuksista_vastaava_oppilaitos :oppilaitos.oppilaitoskoodi
                                 :jarjestamissopimus.voimassa true})) :sopimusten_maara])
      (sql/where (or (clojure.string/blank? ala)
                     (sql/sqlfn exists (sql/subselect jarjestamissopimus
                                         (sql/join :inner sopimus-ja-tutkinto
                                                   (= :jarjestamissopimus.jarjestamissopimusid :sopimus_ja_tutkinto.jarjestamissopimusid))
                                         (sql/join :inner tutkintoversio
                                                   (= :sopimus_ja_tutkinto.tutkintoversio :tutkintoversio.tutkintoversio_id))
                                         (sql/join :inner nayttotutkinto
                                                   (= :tutkintoversio.tutkintotunnus :nayttotutkinto.tutkintotunnus))
                                         (sql/join :left opintoala
                                                   (= :nayttotutkinto.opintoala :opintoala.opintoala_tkkoodi))
                                         (sql/join :left tutkinto-ja-tutkinnonosa
                                                   (= :tutkintoversio.tutkintoversio_id :tutkinto_ja_tutkinnonosa.tutkintoversio))
                                         (sql/join :left tutkinnonosa
                                                   (= :tutkinto_ja_tutkinnonosa.tutkinnonosa :tutkinnonosa.tutkinnonosa_id))
                                         (sql/join :left osaamisala
                                                   (= :tutkintoversio.tutkintoversio_id :osaamisala.tutkintoversio))
                                         (sql/where (and {:jarjestamissopimus.tutkintotilaisuuksista_vastaava_oppilaitos :oppilaitos.oppilaitoskoodi}
                                                         (or {:opintoala.selite_fi [ilike termi]}
                                                             {:opintoala.selite_sv [ilike termi]}
                                                             {:osaamisala.nimi_fi [ilike termi]}
                                                             {:osaamisala.nimi_sv [ilike termi]}
                                                             {:tutkinnonosa.nimi_fi [ilike termi]}
                                                             {:tutkinnonosa.nimi_sv [ilike termi]}
                                                             {:nayttotutkinto.nimi_fi [ilike termi]}
                                                             {:nayttotutkinto.nimi_sv [ilike termi]})))))))
      (sql/order :oppilaitoskoodi :ASC))))

(defn hae-termilla
  "Suodattaa hakutuloksia hakutermillä"
  [termi]
  (for [oppilaitos (hae-kaikki)
        :when (sisaltaako-kentat? oppilaitos [:nimi] termi)]
    (select-keys oppilaitos [:oppilaitoskoodi :nimi])))

(defn hae
  "Hakee oppilaitoksen oppilaitoskoodilla"
  [oppilaitoskoodi]
  (let [oppilaitos (oppilaitos-kaytava/hae oppilaitoskoodi)
        jarjestamissopimukset (sopimus-arkisto/hae-oppilaitoksen-sopimukset oppilaitoskoodi)]
    (some-> oppilaitos
            (assoc :jarjestamissopimus jarjestamissopimukset))))

(defn rajaa-oppilaitoksen-kentat
  "Valitsee oppilaitoksen tiedoista osoitepalvelun tarvitsemat kentät"
  [oppilaitos]
  (let [nimi (select-and-rename-keys oppilaitos [[:nimi :fi] [:nimi :sv]])
        oid (:oid oppilaitos)]
    (assoc oppilaitos :nimi nimi
                      :oid oid)))

(defn hae-osoitepalvelulle
  "Hakee osoitepalvelun tarvitsemat tiedot oppilaitoksista"
  []
  (let [oppilaitokset (sql/select oppilaitos
                        (sql/fields :oppilaitoskoodi :nimi :sahkoposti
                                    :osoite :postinumero :postitoimipaikka))
        sopimukset (group-by :tutkintotilaisuuksista_vastaava_oppilaitos (sopimus-arkisto/hae-kaikki-osoitepalvelulle))]
    (for [oppilaitos oppilaitokset
          :let [oppilaitoksen-sopimukset (some->> (get sopimukset (:oppilaitoskoodi oppilaitos))
                                           (map #(dissoc % :oppilaitos :tutkintotilaisuuksista_vastaava_oppilaitos :koulutustoimija)))]
          :when oppilaitoksen-sopimukset]
      (-> oppilaitos
        rajaa-oppilaitoksen-kentat
        (assoc :sopimukset oppilaitoksen-sopimukset)))))
