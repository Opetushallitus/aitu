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
             [aitu.util :refer [sisaltaako-kentat? select-and-rename-keys]])
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
                :sahkoposti :puhelin :osoite :postinumero :postitoimipaikka :www_osoite :alue :koulutustoimija)
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
  (if (clojure.string/blank? ala)
    (hae-kaikki-julkiset-tiedot)
    (let [termi (str "%" ala "%")]
      (map sql-timestamp->joda-datetime
           (sql/exec-raw [(str "select oppilaitoskoodi, nimi, kieli, muutettu_kayttaja, luotu_kayttaja, muutettuaika, luotuaika, "
                               "sahkoposti, puhelin, osoite, postinumero, postitoimipaikka, www_osoite, alue, koulutustoimija "
                               "from oppilaitos ol "
                               "where exists (select 1 from jarjestamissopimus js "
                               "              join sopimus_ja_tutkinto st on js.jarjestamissopimusid = st.jarjestamissopimusid "
                               "              join tutkintoversio tv on st.tutkintoversio = tv.tutkintoversio_id "
                               "              join nayttotutkinto t on tv.tutkintotunnus = t.tutkintotunnus "
                               "              left join opintoala oa on t.opintoala = oa.opintoala_tkkoodi "
                               "              left join tutkinto_ja_tutkinnonosa tjt on tv.tutkintoversio_id = tjt.tutkintoversio "
                               "              left join tutkinnonosa tos on tjt.tutkinnonosa = tos.tutkinnonosa_id "
                               "              left join osaamisala osala on tv.tutkintoversio_id = osala.tutkintoversio "
                               "              where js.tutkintotilaisuuksista_vastaava_oppilaitos = ol.oppilaitoskoodi "
                               "                    and (oa.selite_fi ilike ? "
                               "                         or oa.selite_sv ilike ? "
                               "                         or osala.nimi_fi ilike ? "
                               "                         or osala.nimi_sv ilike ? "
                               "                         or tos.nimi_fi ilike ? "
                               "                         or tos.nimi_sv ilike ?"
                               "                         or t.nimi_fi ilike ?"
                               "                         or t.nimi_sv ilike ?)) ")
                          (repeat 8 termi)]
                         :results)))))

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
  "Hakee osoitehienoa palvelun tarvitsemat tiedot oppilaitoksista"
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
