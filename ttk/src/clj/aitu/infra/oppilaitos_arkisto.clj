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
  (:require  [clojure.string :refer [blank?]]
             [korma.core :as sql]
             [oph.common.util.util :refer [select-and-rename-keys sisaltaako-kentat?]]
             [aitu.integraatio.sql.korma :refer :all]
             [aitu.integraatio.sql.oppilaitos :as oppilaitos-kaytava]
             [aitu.integraatio.sql.koulutustoimija :as koulutustoimija-kaytava]
             [aitu.infra.jarjestamissopimus-arkisto :as sopimus-arkisto]))

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

;; TODO: Tämä on moninpaikoin samanlainen funktio kuin koulutustoimija_arkistossa olevasta vastaavasta funktiosta. Nämä olisi hyvä yhdistää.
(defn hae-ehdoilla
  "Hakee kaikki hakuehtoja vastaavat oppilaitokset. Ala sisältää opintoalan, tutkinnon, osaamisalan ja tutkinnon osan."
  [ehdot]
  {:pre [(#{"kylla" "ei" "kaikki"} (:sopimuksia ehdot))]}

  (let [sop-voimassa-kylla?  (= "kylla" (:sopimuksia ehdot))
        sop-voimassa-ei?     (= "ei" (:sopimuksia ehdot))
        tunnus-ehto-annettu? (not (blank? (:tunnus ehdot)))
        nimi-ehto-annettu?   (not (blank? (:nimi ehdot)))
        nimi                 (str "%" (:nimi ehdot) "%")

        query (if (and (not tunnus-ehto-annettu?) sop-voimassa-ei?)
                ;; TODO: Liittyy caseen: "Ei rajata tunnus-ehdolla, ei-voimassaolevat sopimukset"   => integroi tämä allaolevaan
                ;; Tässä casessa halutaan sellaiset sopimukset, joilla on aiemmin ollut voimassaolevia sopimuksia, mutta ei ole nyt. Pitäisi siis olla kaksi where-lausetta (exists ja not-exists).
                (->
                  (sql/select* oppilaitos)
                  (sql/join :inner :jarjestamissopimus ; inner -> ei oppilaitoksia, joilla ei ole koskaan ollut sopimusta.
                    (and (= :oppilaitos.oppilaitoskoodi :jarjestamissopimus.tutkintotilaisuuksista_vastaava_oppilaitos)
                         (= :jarjestamissopimus.poistettu false)))
                  (sql/join :inner sopimus-ja-tutkinto
                    (= :jarjestamissopimus.jarjestamissopimusid :sopimus_ja_tutkinto.jarjestamissopimusid))
                  (sql/join :inner tutkintoversio
                    (= :sopimus_ja_tutkinto.tutkintoversio :tutkintoversio.tutkintoversio_id))
                  (sql/fields :oppilaitoskoodi :nimi :kieli :koulutustoimija :alue :muutettu_kayttaja :luotu_kayttaja :muutettuaika
                              :luotuaika :sahkoposti :puhelin :osoite :postinumero :postitoimipaikka :www_osoite)
                  (sql/aggregate (sum (sql/raw "case WHEN (jarjestamissopimus.voimassa and (current_date <= tutkintoversio.siirtymaajan_loppupvm)) THEN 1 ELSE 0 END")) :sopimusten_maara)
;                  (sql/aggregate (sum (sql/raw "case WHEN (jarjestamissopimus.voimassa = false or (current_date > tutkintoversio.siirtymaajan_loppupvm)) THEN 1 ELSE 0 END")) :eivoimassalkm)
                  (sql/group :oppilaitoskoodi :nimi :kieli :koulutustoimija :alue :muutettu_kayttaja :luotu_kayttaja :muutettuaika
                             :luotuaika :sahkoposti :puhelin :osoite :postinumero :postitoimipaikka :www_osoite)
                  (sql/having (= (sql/raw "sum(case WHEN (jarjestamissopimus.voimassa and (current_date <= tutkintoversio.siirtymaajan_loppupvm)) THEN 1 ELSE 0 END)") 0))
                  (sql/order :nimi :ASC)
                  )
                ;; Muut
                (->
                  (sql/select* oppilaitos)
                  (sql/join :inner [:jarjestamissopimus :js] ; inner -> ei oppilaitoksia, joilla ei ole koskaan ollut sopimusta.
                    (and (= :oppilaitos.oppilaitoskoodi :js.tutkintotilaisuuksista_vastaava_oppilaitos)
                         (= :js.poistettu false)))
                  (sql/fields :oppilaitoskoodi :nimi :kieli :koulutustoimija :alue :muutettu_kayttaja :luotu_kayttaja :muutettuaika
                              :luotuaika :sahkoposti :puhelin :osoite :postinumero :postitoimipaikka :www_osoite)
                  (sql/aggregate (count :js.jarjestamissopimusid) :sopimusten_maara)
                  (sql/group :oppilaitoskoodi :nimi :kieli :koulutustoimija :alue :muutettu_kayttaja :luotu_kayttaja :muutettuaika
                             :luotuaika :sahkoposti :puhelin :osoite :postinumero :postitoimipaikka :www_osoite)
                  (sql/where (sql/sqlfn exists (sql/subselect jarjestamissopimus
                                                 (sql/join :inner sopimus-ja-tutkinto
                                                   (= :jarjestamissopimus.jarjestamissopimusid :sopimus_ja_tutkinto.jarjestamissopimusid))
                                                 (sql/join :inner tutkintoversio
                                                   (= :sopimus_ja_tutkinto.tutkintoversio :tutkintoversio.tutkintoversio_id))

                                                 (sql/where {:js.jarjestamissopimusid :jarjestamissopimus.jarjestamissopimusid})

                                                 (cond->
                                                   tunnus-ehto-annettu? (->
                                                                          (sql/join :inner nayttotutkinto
                                                                            (= :tutkintoversio.tutkintotunnus :nayttotutkinto.tutkintotunnus))
                                                                          (sql/join :left opintoala
                                                                            (= :nayttotutkinto.opintoala :opintoala.opintoala_tkkoodi))
                                                                          (sql/join :left tutkinnonosa
                                                                            (= :tutkintoversio.tutkintoversio_id :tutkinnonosa.tutkintoversio))
                                                                          (sql/join :left osaamisala
                                                                            (= :tutkintoversio.tutkintoversio_id :osaamisala.tutkintoversio))
                                                                          (sql/where (or {:nayttotutkinto.tutkintotunnus (:tunnus ehdot)}
                                                                                         {:opintoala.opintoala_tkkoodi   (:tunnus ehdot)}
                                                                                         {:tutkinnonosa.osatunnus        (:tunnus ehdot)}
                                                                                         {:osaamisala.osaamisalatunnus   (:tunnus ehdot)}
                                                                                         )))
                                                   sop-voimassa-kylla?
                                                   ;; alkupvm <= current date <= loppupvm  &&  current date <= siirtymaajan_loppupvm
                                                   ;; tutkintoversio.siirtymaajan_loppupvm kenttä on tietokannassa NOT NULL
                                                   (sql/where (and {:jarjestamissopimus.voimassa true}
                                                                   (<= :sopimus_ja_tutkinto.alkupvm (sql/raw "current_date"))
                                                                   (<= (sql/raw "current_date") (sql/sqlfn coalesce :sopimus_ja_tutkinto.loppupvm (sql/raw "current_date")))
                                                                   (<= (sql/raw "current_date") :tutkintoversio.siirtymaajan_loppupvm)
                                                                   ))
                                                   sop-voimassa-ei?
                                                   ;; alkupvm > current_date || current date > loppupvm || current date > siirtymaajan_loppupvm
                                                   (sql/where (or {:jarjestamissopimus.voimassa false}
                                                                  (> :sopimus_ja_tutkinto.alkupvm (sql/raw "current_date"))
                                                                  (> (sql/raw "current_date") (sql/sqlfn coalesce :sopimus_ja_tutkinto.loppupvm (sql/raw "current_date")))
                                                                  (> (sql/raw "current_date") :tutkintoversio.siirtymaajan_loppupvm)
                                                                  ))))))
                  (cond->
                    nimi-ehto-annettu? (sql/where {:nimi [ilike nimi]})
                    )

                  (sql/order :nimi :ASC)
                  ))
        oppilaitokset (sql/exec query)
        ]
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
