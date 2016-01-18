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
             [aitu.integraatio.sql.koulutustoimija :as koulutustoimija-kaytava]
             [oph.common.util.util :refer [select-and-rename-keys sisaltaako-kentat?]]
             [clojure.string :refer [blank?]])
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

(defn ^:integration-api laske-voimassaolo! []
  (sql/update oppilaitos
    (sql/set-fields {:voimassa false})
    (sql/where {:lakkautuspaiva [< (sql/raw "current_date")]})))

(defn ^:integration-api laske-toimipaikkojen-voimassaolo! []
  (sql/update toimipaikka
    (sql/set-fields {:voimassa false})
    (sql/where {:lakkautuspaiva [< (sql/raw "current_date")]})))

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

(defn hae-voimassaolevat
  "Hakee voimassaolevien oppilaitosten kaikki tiedot"
  []
  (sql/select oppilaitos
    (sql/where {:voimassa true})
    (sql/order :nimi)))

(defn hae-kaikki-toimipaikat-julkiset-tiedot []
  (sql/select toimipaikka
    (sql/fields :toimipaikkakoodi :nimi :kieli :muutettu_kayttaja :luotu_kayttaja :muutettuaika :luotuaika
                :sahkoposti :puhelin :osoite :postinumero :postitoimipaikka :www_osoite :oppilaitos)
    (sql/order :nimi)))

(defn hae-kaikki-toimipaikat []
  (sql/select toimipaikka))

(defn hae-ehdoilla
  "Hakee kaikki ehtoja vastaavat oppilaitokset. Ala sisältää opintoalan, tutkinnon, osaamisalan ja tutkinnon osan."
  [ehdot]
  (let [nimi (str "%" (:nimi ehdot) "%")
        oppilaitokset (->
                        (sql/select* oppilaitos)
                        (sql/join :left :jarjestamissopimus
                                  (and (= :oppilaitos.oppilaitoskoodi :jarjestamissopimus.tutkintotilaisuuksista_vastaava_oppilaitos)
                                       (= :jarjestamissopimus.voimassa true)))
                        (sql/fields :oppilaitoskoodi :nimi :kieli :koulutustoimija :alue :muutettu_kayttaja :luotu_kayttaja :muutettuaika
                                    :luotuaika :sahkoposti :puhelin :osoite :postinumero :postitoimipaikka :www_osoite)
                        (sql/aggregate (count :jarjestamissopimus.jarjestamissopimusid) :sopimusten_maara)
                        (sql/group :oppilaitoskoodi :nimi :kieli :koulutustoimija :alue :muutettu_kayttaja :luotu_kayttaja :muutettuaika
                                   :luotuaika :sahkoposti :puhelin :osoite :postinumero :postitoimipaikka :www_osoite)
                        (cond->
                          (not (blank? (:tunnus ehdot))) (sql/where (sql/sqlfn exists (sql/subselect jarjestamissopimus
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
                                                                                                        (or {:opintoala.opintoala_tkkoodi (:tunnus ehdot)}
                                                                                                            {:osaamisala.osaamisalatunnus (:tunnus ehdot)}
                                                                                                            {:tutkinnonosa.osatunnus (:tunnus ehdot)}
                                                                                                            {:nayttotutkinto.tutkintotunnus (:tunnus ehdot)}))))))
                          (not (blank? (:nimi ehdot))) (sql/where {:nimi [ilike nimi]})
                          (= (:sopimuksia ehdot) "kylla") (sql/having (> (sql/sqlfn count :jarjestamissopimus.jarjestamissopimusid) 0))
                          (= (:sopimuksia ehdot) "ei") (sql/having (= (sql/sqlfn count :jarjestamissopimus.jarjestamissopimusid) 0)))
                        (sql/order :nimi :ASC)
                        sql/exec)]
    (if (:avaimet ehdot)
      (map #(select-keys % (:avaimet ehdot)) oppilaitokset)
      oppilaitokset)))

(defn hae-termilla
  "Suodattaa hakutuloksia hakutermillä"
  [termi]
  (let [termi (str \% termi \%)
        oppilaitokset (sql/select oppilaitos
                        (sql/fields :oppilaitoskoodi :nimi)
                        (sql/where {:voimassa true
                                    :nimi [ilike termi]})
                        (sql/order :nimi))]
    (for [oppilaitos oppilaitokset]
      {:oppilaitoskoodi (:oppilaitoskoodi oppilaitos)
       :nimi (str (:nimi oppilaitos) " (" (:oppilaitoskoodi oppilaitos) ")")})))

(defn hae
  "Hakee oppilaitoksen oppilaitoskoodilla"
  [oppilaitoskoodi]
  (let [oppilaitos (oppilaitos-kaytava/hae oppilaitoskoodi)
        jarjestamissopimukset (sopimus-arkisto/hae-oppilaitoksen-sopimukset oppilaitoskoodi)]
    (some-> oppilaitos
            (assoc :jarjestamissopimus jarjestamissopimukset)
            (update-in [:koulutustoimija] koulutustoimija-kaytava/hae-linkki))))

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
