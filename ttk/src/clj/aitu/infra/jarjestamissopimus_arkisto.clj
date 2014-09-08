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

(ns aitu.infra.jarjestamissopimus-arkisto
  (:import org.apache.commons.io.FileUtils)
  (:require  korma.db
             [korma.core :as sql]
             [aitu.util :refer [select-and-rename-keys]]
             [aitu.toimiala.jarjestamissopimus :as domain]
             [aitu.infra.sopimus-ja-tutkinto-arkisto :as sopimus-ja-tutkinto-arkisto]
             [aitu.integraatio.sql.oppilaitos :as oppilaitos-kaytava]
             [aitu.integraatio.sql.koulutustoimija :as koulutustoimija-kaytava]
             [aitu.integraatio.sql.jarjestamissopimus :as sopimus-kaytava]
             [aitu.integraatio.sql.toimikunta :as toimikunta-kaytava]
             [aitu.infra.kayttaja-arkisto :as kayttaja-arkisto]
             [aitu.toimiala.voimassaolo.jarjestamissopimus :as voimassaolo]
             [aitu.toimiala.voimassaolo.saanto.osoitepalvelu-jarjestamissopimus :as osoitepalvelu-voimassaolo]
             [clj-time.core :as time]
             [aitu.auditlog :as auditlog])
  (:use [aitu.integraatio.sql.korma]))

(defn ^:private aseta-oppilaitos-kentta [sopimus]
  (assoc sopimus :oppilaitos (:tutkintotilaisuuksista_vastaava_oppilaitos sopimus)))

(defn ^:test-api poista!
  "Poistaa järjestämissopimuksen."
  [jarjestamissopimusid]

  (let [sopimus (sopimus-kaytava/hae jarjestamissopimusid)
        sopimusid (:jarjestamissopimusid sopimus)
        diaarinumero (:sopimusnumero sopimus)]
    (auditlog/jarjestamissopimus-poisto! sopimusid diaarinumero))

  (sql/delete sopimus-ja-tutkinto
    (sql/where {:jarjestamissopimusid jarjestamissopimusid}))
  (sql/delete jarjestamissopimus
    (sql/where {:jarjestamissopimusid jarjestamissopimusid})))

(defn ^:integration-api aseta-sopimuksen-voimassaolo!
  [jarjestamissopimusid voimassa]
  (sql/update jarjestamissopimus
    (sql/set-fields {:voimassa voimassa})
    (sql/where {:jarjestamissopimusid jarjestamissopimusid})))

(declare hae-kaikki hae-ja-liita-tutkinnonosiin-asti)

(defn ^:integration-api paivita-sopimusten-voimassaolo!
  []
  (doseq [{:keys [jarjestamissopimusid voimassa]} (hae-kaikki)]
    (aseta-sopimuksen-voimassaolo! jarjestamissopimusid voimassa)))

(defn ^:integration-api paivita-sopimuksen-voimassaolo!
  "Päivittää annetulla id:llä olevan sopimuksen voimassaolotiedon"
  [jarjestamissopimusid]
  (let [sopimus (voimassaolo/taydenna-sopimuksen-ja-liittyvien-tietojen-voimassaolo
                  (hae-ja-liita-tutkinnonosiin-asti jarjestamissopimusid))]
    (aseta-sopimuksen-voimassaolo! jarjestamissopimusid (:voimassa sopimus))))

(defn merkitse-sopimus-poistetuksi!
  "Asettaa sopimukselle poistettu -flagin"
  [jarjestamissopimusid]

  (let [sopimus (sopimus-kaytava/hae jarjestamissopimusid)
        sopimusid (:jarjestamissopimusid sopimus)
        diaarinumero (:sopimusnumero sopimus)]
    (auditlog/jarjestamissopimus-poisto! sopimusid diaarinumero))

  (sql/update jarjestamissopimus
    (sql/set-fields {:poistettu true})
    (sql/where {:jarjestamissopimusid jarjestamissopimusid}))
  (sql/update sopimus-ja-tutkinto
    (sql/set-fields {:poistettu true})
    (sql/where {:jarjestamissopimusid jarjestamissopimusid})))

(defn lisaa!
  "Lisää uuden jarjestamissopimuksen."
  [sopimus]
  {:pre [(domain/jarjestamissopimus? sopimus)]}

  (let [sopimus (sql/insert jarjestamissopimus
                  (sql/values (aseta-oppilaitos-kentta sopimus)))
        sopimusid (:jarjestamissopimusid sopimus)
        diaarinumero (:sopimusnumero sopimus)]
    (auditlog/jarjestamissopimus-lisays! sopimusid diaarinumero)
    sopimus))

(defn ^:private paivita-sopimuksen-tutkinnon-osat!
  "Päivittää tutkinnon osat sopimukselle"
  [sopimus_ja_tutkinto sopimus_ja_tutkinto_id]
  (auditlog/sopimuksen-tutkinnon-osat-paivitys!
    sopimus_ja_tutkinto_id
    (map #(-> % :sopimus_ja_tutkinto_ja_tutkinnonosa :tutkinnonosa_id) sopimus_ja_tutkinto))
  (sql/delete sopimus-ja-tutkinto-ja-tutkinnonosa
    (sql/where {:sopimus_ja_tutkinto sopimus_ja_tutkinto_id}))
  (doseq [tutkinnonosa (:sopimus_ja_tutkinto_ja_tutkinnonosa sopimus_ja_tutkinto)]
    (sql/insert sopimus-ja-tutkinto-ja-tutkinnonosa
      (sql/values {:sopimus_ja_tutkinto sopimus_ja_tutkinto_id
                   :tutkinnonosa (:tutkinnonosa_id tutkinnonosa)
                   :toimipaikka (:toimipaikka tutkinnonosa)}))))

(defn ^:private paivita-sopimuksen-osaamisalat!
  "Päivittää tutkinnon osaamisalat sopimukselle"
  [sopimus_ja_tutkinto sopimus_ja_tutkinto_id]
  (auditlog/sopimuksen-osaamisalat-paivitys!
    sopimus_ja_tutkinto_id
    (map #(-> % :sopimus_ja_tutkinto_ja_osaamisala :osaamisala_id) sopimus_ja_tutkinto))
  (sql/delete sopimus-ja-tutkinto-ja-osaamisala
    (sql/where {:sopimus_ja_tutkinto sopimus_ja_tutkinto_id}))
  (doseq [osaamisala (:sopimus_ja_tutkinto_ja_osaamisala sopimus_ja_tutkinto)]
    (sql/insert sopimus-ja-tutkinto-ja-osaamisala
      (sql/values {:sopimus_ja_tutkinto sopimus_ja_tutkinto_id
                   :osaamisala (:osaamisala_id osaamisala)
                   :toimipaikka (:toimipaikka osaamisala)}))))

(defn tarkista-sopimus-ja-tutkinto-paivitys
  "Tarkistaa että sopimus_ja_tutkinto_id löytyy kannasta oikealle sopimukselle"
  [jarjestamissopimusid sopimus_ja_tutkinto_id]
  (->
    (sql/select sopimus-ja-tutkinto
      (sql/where {:sopimus_ja_tutkinto_id sopimus_ja_tutkinto_id
                  :jarjestamissopimusid jarjestamissopimusid}))
    count
    (= 1)))

(defn ^:private paivitettava-sopimus-ja-tutkinto
  [sopimus_ja_tutkinto]
  {:vastuuhenkilo (:vastuuhenkilo sopimus_ja_tutkinto)
   :puhelin (:puhelin sopimus_ja_tutkinto)
   :sahkoposti (:sahkoposti sopimus_ja_tutkinto)
   :nayttomestari (:nayttomestari sopimus_ja_tutkinto)
   :lisatiedot (:lisatiedot sopimus_ja_tutkinto)
   :vastuuhenkilo_vara (:vastuuhenkilo_vara sopimus_ja_tutkinto)
   :puhelin_vara (:puhelin_vara sopimus_ja_tutkinto)
   :sahkoposti_vara (:sahkoposti_vara sopimus_ja_tutkinto)
   :nayttomestari_vara (:nayttomestari_vara sopimus_ja_tutkinto)
   :lisatiedot_vara (:lisatiedot_vara sopimus_ja_tutkinto)
   :kieli (:kieli sopimus_ja_tutkinto)})

(defn paivita!
  "Päivittää sopimuksen tiedot"
  [sopimus sopimuksen_tutkinnot]
  {:pre [(domain/jarjestamissopimus? sopimus)]}
  (auditlog/jarjestamissopimus-paivitys! (:jarjestamissopimusid sopimus)
    (:sopimusnumero sopimus))
  (sql/update jarjestamissopimus
    (sql/set-fields (aseta-oppilaitos-kentta sopimus))
    (sql/where {:jarjestamissopimusid (:jarjestamissopimusid sopimus)}))
  (doseq [sopimus_ja_tutkinto sopimuksen_tutkinnot]
    (let [jarjestamissopimusid (:jarjestamissopimusid sopimus)
          sopimus_ja_tutkinto_id (:sopimus_ja_tutkinto_id sopimus_ja_tutkinto)]
      (if (tarkista-sopimus-ja-tutkinto-paivitys jarjestamissopimusid sopimus_ja_tutkinto_id)
        (do
          (sql/update sopimus-ja-tutkinto
            (sql/set-fields (paivitettava-sopimus-ja-tutkinto sopimus_ja_tutkinto))
            (sql/where {:sopimus_ja_tutkinto_id sopimus_ja_tutkinto_id}))
          (paivita-sopimuksen-tutkinnon-osat! sopimus_ja_tutkinto sopimus_ja_tutkinto_id)
          (paivita-sopimuksen-osaamisalat! sopimus_ja_tutkinto sopimus_ja_tutkinto_id))
        (throw (Exception. (str "Sopimuksen tutkintojen paivitys ei ole sallittu."
                                "jarjestamissopimusid: " jarjestamissopimusid
                                " sopimus_ja_tutkinto_id: " sopimus_ja_tutkinto_id))))))
  (paivita-sopimuksen-voimassaolo! (:jarjestamissopimusid sopimus)))

(defn ^:private liita-perustiedot-sopimukseen
  [jarjestamissopimus]
  (some-> jarjestamissopimus
      (update-in [:toimikunta] toimikunta-kaytava/hae)
      (update-in [:sopijatoimikunta] toimikunta-kaytava/hae)
      (update-in [:koulutustoimija] koulutustoimija-kaytava/hae)
      (update-in [:tutkintotilaisuuksista_vastaava_oppilaitos] oppilaitos-kaytava/hae)
      (update-in [:muutettu_kayttaja] kayttaja-arkisto/hae)
      (update-in [:luotu_kayttaja] kayttaja-arkisto/hae)))

(defn ^:private liita-tutkinnot-sopimukseen
  [jarjestamissopimus]
  (let [id (:jarjestamissopimusid jarjestamissopimus)
        sopimus-ja-tutkinto-rivit (sopimus-ja-tutkinto-arkisto/hae-jarjestamissopimukseen-liittyvat id)]
    (some-> jarjestamissopimus
      (assoc :sopimus_ja_tutkinto sopimus-ja-tutkinto-rivit))))

(defn ^:private liita-tutkinnot-ja-tutkinnonosat-sopimukseen
  [jarjestamissopimus]
  (let [id (:jarjestamissopimusid jarjestamissopimus)
        sopimus-ja-tutkinto-rivit (sopimus-ja-tutkinto-arkisto/hae-sopimukseen-liittyvat-tutkinnonosiin-asti id)]
    (some-> jarjestamissopimus
      (assoc :sopimus_ja_tutkinto sopimus-ja-tutkinto-rivit))))

(defn liita-oppilaitoksen-toimipaikat
  "Liittää toimipaikat oppilaitokselle"
  [jarjestamissopimus]
  (let [oppilaitoskoodi (get-in jarjestamissopimus [:tutkintotilaisuuksista_vastaava_oppilaitos :oppilaitoskoodi])]
    (assoc-in jarjestamissopimus [:tutkintotilaisuuksista_vastaava_oppilaitos :toimipaikka] (oppilaitos-kaytava/hae-oppilaitoksen-toimipaikat oppilaitoskoodi))))

(defn ^:private rajaa-tutkinnon-kentat
  "Valitsee sopimuksen tutkinnoista osoitepalvelun tarvitsemat kentät"
  [tutkinto]
  (select-and-rename-keys tutkinto [[:opintoala_tkkoodi :opintoalatunnus]
                                    :tutkintotunnus
                                    :vastuuhenkilo
                                    [:sahkoposti :sahkoposti_vastuuhenkilo]
                                    [:vastuuhenkilo_vara :varavastuuhenkilo]
                                    [:sahkoposti_vara :sahkoposti_varavastuuhenkilo]]))

(defn hae-kaikki
  []
  (->> (sql/select jarjestamissopimus
         (sql/where {:poistettu false}))
    (map liita-perustiedot-sopimukseen)
    (map liita-tutkinnot-sopimukseen)
    (map voimassaolo/taydenna-sopimuksen-ja-liittyvien-tietojen-voimassaolo)))

(defn hae-kaikki-osoitepalvelulle
  "Hakee kaikki järjestämissopimukset ja niihin liittyvät tutkinnot osoitepalvelua varten"
  []
  (remove nil? (for [sopimus (sql/select jarjestamissopimus
                              (sql/with sopimus-ja-tutkinto
                                (sql/with tutkintoversio
                                  (sql/with nayttotutkinto
                                    (sql/with opintoala)))))]
                (some-> sopimus
                  (update-in [:toimikunta] toimikunta-kaytava/hae)
                  osoitepalvelu-voimassaolo/taydenna-sopimuksen-voimassaolo
                  (#(when (:voimassa %) %))
                  (select-and-rename-keys [[:sopimus_ja_tutkinto :tutkinnot]
                                           :toimikunta
                                           :vastuuhenkilo
                                           :sahkoposti
                                           :tutkintotilaisuuksista_vastaava_oppilaitos
                                           :koulutustoimija])
                  (update-in [:toimikunta] :tkunta)
                  (update-in [:tutkinnot] #(map rajaa-tutkinnon-kentat %))))))

(defn hae
  "Hakee järjestämissopimuksen ja siihen liittyvät tiedot"
  [jarjestamissopimusid]
  (-> (sopimus-kaytava/hae jarjestamissopimusid)
    liita-perustiedot-sopimukseen
    liita-tutkinnot-sopimukseen))

(defn hae-ja-liita-tutkinnonosiin-asti
  "Hakee järjestämissopimuksen ja siihen liittyvät tiedot tutkinnonosiin asti"
  [jarjestamissopimusid]
  (-> (sopimus-kaytava/hae jarjestamissopimusid)
    liita-perustiedot-sopimukseen
    liita-tutkinnot-ja-tutkinnonosat-sopimukseen
    liita-oppilaitoksen-toimipaikat))

(defn hae-toimikunnan-sopimukset
  "Hakee toimikunnan järjestämissopimukset ja liittää niihin tarvittavat tiedot"
  [toimikunta]
  (->> (sopimus-kaytava/hae-toimikunnan-sopimukset toimikunta)
    (mapv liita-perustiedot-sopimukseen)
    (mapv liita-tutkinnot-sopimukseen)))

(defn hae-oppilaitoksen-sopimukset
  "Hakee oppilaitoksen järjestämissopimukset ja liittää niihin tarvittavat tiedot"
  [oppilaitoskoodi]
  (->> (sopimus-kaytava/hae-oppilaitoksen-sopimukset oppilaitoskoodi)
    (mapv liita-perustiedot-sopimukseen)
    (mapv liita-tutkinnot-sopimukseen)))

(defn hae-koulutustoimijan-sopimukset
  "Hakee koulutustoimijan järjestämissopimukset ja liittää niihin tarvittavat tiedot"
  [y-tunnus]
  (->> (sopimus-kaytava/hae-koulutustoimijan-sopimukset y-tunnus)
    (mapv liita-perustiedot-sopimukseen)
    (mapv liita-tutkinnot-sopimukseen)))

(defn uniikki-sopimusnumero? [sopimusnumero jarjestamissopimusid]
  (-> (sql/select jarjestamissopimus
        (sql/where {:sopimusnumero sopimusnumero
                    :jarjestamissopimusid [not= jarjestamissopimusid]}))
      (count)
      (= 0)))

(defn poista-tutkinnot-sopimukselta!
  "Poistaa tutkintoja järjestämissopimukselta asettamalla poistettu = true"
  [jarjestamissopimusid tutkintoversiot]
  (auditlog/sopimuksen-tutkinnot-operaatio! :poisto jarjestamissopimusid tutkintoversiot)
  (doseq [tutkintoversio tutkintoversiot]
    (sql/update sopimus-ja-tutkinto
      (sql/set-fields {:poistettu true})
      (sql/where {:tutkintoversio tutkintoversio :jarjestamissopimusid jarjestamissopimusid})))
  ;; Triggeröidään muutos myös sopimustaulussa että muokkaajan tiedot tallentuvat myös sinne
  (sql/update jarjestamissopimus
    (sql/set-fields {:muutettuaika (time/now)})
    (sql/where {:jarjestamissopimusid jarjestamissopimusid})))

(defn ^:test-api poista-kaikki-tutkinnot-sopimukselta!
  "Poistaa kaikki tutkinnot, tutkinnon osat ja osaamisalat sopimukselta."
  [jarjestamissopimusid]
  (let [sopimus-ja-tutkinto-entiteetit (sql/select sopimus-ja-tutkinto
                                         (sql/where {:jarjestamissopimusid jarjestamissopimusid}))]
    (doseq [sopimus-ja-tutkinto-entiteetti sopimus-ja-tutkinto-entiteetit]
      (sql/delete sopimus-ja-tutkinto-ja-tutkinnonosa
        (sql/where {:sopimus_ja_tutkinto (:sopimus_ja_tutkinto_id sopimus-ja-tutkinto-entiteetti)}))
      (sql/delete sopimus-ja-tutkinto-ja-osaamisala
        (sql/where {:sopimus_ja_tutkinto (:sopimus_ja_tutkinto_id sopimus-ja-tutkinto-entiteetti)}))))
  (sql/delete sopimus-ja-tutkinto
    (sql/where {:jarjestamissopimusid jarjestamissopimusid})))

(defn lisaa-tutkinnot-sopimukselle!
  "Lisää tutkinnot sopimukselle"
  [jarjestamissopimusid tutkintoversiot]
  (let [tutkintoversiot (map #(if (map? %) % {:tutkintoversio %})
                             tutkintoversiot)]
    (auditlog/sopimuksen-tutkinnot-operaatio! :lisays jarjestamissopimusid (map :tutkintoversio tutkintoversiot))
    (let [sopimus-tutkinto-liitokset
          (doall
            (for [tutkintoversio tutkintoversiot]
              (sql/insert sopimus-ja-tutkinto
                (sql/values (assoc tutkintoversio
                                   :jarjestamissopimusid jarjestamissopimusid)))))]
      ;; Triggeröidään muutos myös sopimustaulussa että muokkaajan tiedot tallentuvat myös sinne
      (sql/update jarjestamissopimus
        (sql/set-fields {:muutettuaika (time/now)})
        (sql/where {:jarjestamissopimusid jarjestamissopimusid}))
      sopimus-tutkinto-liitokset)))

(defn hae-sopimuksen-tutkinnot
  "Hakee tutkinnot sopimukselle"
  [jarjestamissopimusid]
  (sql/select sopimus-ja-tutkinto
    (sql/where {:jarjestamissopimusid jarjestamissopimusid :poistettu false})))

(defn paivita-tutkinnot!
  "Päivittää tutkinnot järjestämissopimukselle"
  [jarjestamissopimusid sopimus_ja_tutkinto]
  (let [vanha-sopimus-ja-tutkinto (hae-sopimuksen-tutkinnot jarjestamissopimusid)
        vanhat-tutkintoversiot (set (map :tutkintoversio vanha-sopimus-ja-tutkinto))
        uudet-tutkintoversiot (set (map :tutkintoversio_id sopimus_ja_tutkinto))
        poistettavat (clojure.set/difference vanhat-tutkintoversiot uudet-tutkintoversiot)
        lisattavat (clojure.set/difference uudet-tutkintoversiot vanhat-tutkintoversiot)]
    (auditlog/sopimuksen-tutkinnot-operaatio! :paivitys jarjestamissopimusid uudet-tutkintoversiot)
    (poista-tutkinnot-sopimukselta! jarjestamissopimusid poistettavat)
    (lisaa-tutkinnot-sopimukselle! jarjestamissopimusid lisattavat)
    (paivita-sopimuksen-voimassaolo! jarjestamissopimusid)))

(defn lisaa-suunnitelma-tutkinnolle!
  "Lisää järjestämissuunnitelman tutkinnolle"
  [sopimus_ja_tutkinto suunnitelma]
  (let [suunnitelma-byte-array (FileUtils/readFileToByteArray (:tempfile suunnitelma))
        suunnitelma-file-name (:filename suunnitelma)
        suunnitelma-content-type (:content-type suunnitelma)
        lisatty-suunnitelma (sql/insert jarjestamissuunnitelma
                              (sql/values {:jarjestamissuunnitelma suunnitelma-byte-array
                                           :jarjestamissuunnitelma_filename suunnitelma-file-name
                                           :jarjestamissuunnitelma_content_type suunnitelma-content-type
                                           :sopimus_ja_tutkinto sopimus_ja_tutkinto}))]
    (auditlog/tutkinnon-suunnitelma-operaatio! :lisays (:jarjestamissuunnitelma_id lisatty-suunnitelma) sopimus_ja_tutkinto)
    (dissoc lisatty-suunnitelma :jarjestamissuunnitelma)))

(defn poista-suunnitelma!
  "Asettaa poistettu flagin järjestämissuunnitelmalle"
  [jarjestamissuunnitelma_id]
  (auditlog/tutkinnon-suunnitelma-operaatio! :poisto jarjestamissuunnitelma_id)
  (sql/update jarjestamissuunnitelma
    (sql/set-fields {:poistettu true})
    (sql/where {:jarjestamissuunnitelma_id jarjestamissuunnitelma_id})))

(defn ^:test-api lisaa-suunnitelma-kantaan!
  "Lisää järjestämissuunnitelman kantaan."
  [jarjestamissopimusid tutkintoversio jarjestamissuunnitelma_content_type jarjestamissuunnitelma_filename]
  (let [sopimus-ja-tutkinto-lista (hae-sopimuksen-tutkinnot jarjestamissopimusid)
        sopimus-ja-tutkinto (first (filter #(= (:tutkintoversio %) tutkintoversio) sopimus-ja-tutkinto-lista))
        sopimus_ja_tutkinto_id (:sopimus_ja_tutkinto_id sopimus-ja-tutkinto)]
    (sql/insert jarjestamissuunnitelma
      (sql/values {:sopimus_ja_tutkinto sopimus_ja_tutkinto_id
                   :jarjestamissuunnitelma_filename jarjestamissuunnitelma_filename
                   :jarjestamissuunnitelma_content_type jarjestamissuunnitelma_content_type}))))

(defn ^:test-api poista-sopimuksen-suunnitelmat!
  "Poistaa suunnitelmat sopimukselta kokonaan tietokannasta."
  [jarjestamissopimusid]
  (doseq [sopimus_ja_tutkinto (hae-sopimuksen-tutkinnot jarjestamissopimusid)]
    (sql/delete jarjestamissuunnitelma
      (sql/where {:sopimus_ja_tutkinto (:sopimus_ja_tutkinto_id sopimus_ja_tutkinto)}))))

(defn hae-suunnitelma
  "Hakuu järjestämissuunnitelman"
  [jarjestamissuunnitelma_id]
  {:post [(not (nil? %))]}
  (first
    (sql/select jarjestamissuunnitelma
      (sql/fields :jarjestamissuunnitelma
                  :jarjestamissuunnitelma_filename
                  :jarjestamissuunnitelma_content_type)
      (sql/where {:jarjestamissuunnitelma_id jarjestamissuunnitelma_id}))))

(defn hae-jarjestamissopimusid-sopimuksen-tutkinnolle
  "Hakee järjestämissopimusid:n sopimuksen tutkinnolle"
  [sopimus_ja_tutkinto_id]
  (some-> (sql/select sopimus-ja-tutkinto
            (sql/where {:sopimus_ja_tutkinto_id sopimus_ja_tutkinto_id}))
    first
    :jarjestamissopimusid))

(defn hae-jarjestamissopimusid-jarjestamissuunnitelmalle
  "Hakee järjestämissopimusid:n järjestämissuunnitelmalle"
  [jarjestamissuunnitelma_id]
  (some->
    (sql/select jarjestamissuunnitelma
      (sql/fields :sopimus_ja_tutkinto)
      (sql/where {:jarjestamissuunnitelma_id jarjestamissuunnitelma_id}))
    first
    :sopimus_ja_tutkinto
    hae-jarjestamissopimusid-sopimuksen-tutkinnolle))

(defn hae-liite
  "Hakee sopimuksen liitteen"
  [sopimuksen_liite_id]
  {:post [(not (nil? %))]}
  (first
    (sql/select sopimuksen-liite
      (sql/fields :sopimuksen_liite
                  :sopimuksen_liite_filename
                  :sopimuksen_liite_content_type)
      (sql/where {:sopimuksen_liite_id sopimuksen_liite_id}))))

(defn lisaa-liite-tutkinnolle!
  "Lisää sopimuksen liitteen tutkinnolle"
  [sopimus_ja_tutkinto_id liite]
  (let [liite-byte-array (FileUtils/readFileToByteArray (:tempfile liite))
        liite-file-name (:filename liite)
        liite-content-type (:content-type liite)
        lisatty-liite (sql/insert sopimuksen-liite
                        (sql/values {:sopimuksen_liite liite-byte-array
                                     :sopimuksen_liite_filename liite-file-name
                                     :sopimuksen_liite_content_type liite-content-type
                                     :sopimus_ja_tutkinto sopimus_ja_tutkinto_id}))]
    (auditlog/tutkinnon-liite-operaatio! :lisays (:sopimuksen_liite_id lisatty-liite) sopimus_ja_tutkinto_id)
    (dissoc lisatty-liite :sopimuksen_liite)))

(defn poista-liite!
  "Asettaa poistettu flagin sopimuksen liitteelle"
  [sopimuksen_liite_id]
  (auditlog/tutkinnon-liite-operaatio! :poisto sopimuksen_liite_id)
  (sql/update sopimuksen-liite
    (sql/set-fields {:poistettu true})
    (sql/where {:sopimuksen_liite_id sopimuksen_liite_id})))


(defn hae-jarjestamissopimusid-sopimuksen-liitteelle
  "Hakee järjestämissopimusid:n sopimuksen liitteelle"
  [sopimuksen_liite_id]
  (some->
    (sql/select sopimuksen-liite
      (sql/fields :sopimus_ja_tutkinto)
      (sql/where {:sopimuksen_liite_id sopimuksen_liite_id}))
    first
    :sopimus_ja_tutkinto
    hae-jarjestamissopimusid-sopimuksen-tutkinnolle))

(defn hae-jarjestamissopimuksen-toimikunta
  "Hakee toimikunnan tkunta-tunnisteen järjestämissopimusid:llä"
  [jarjestamissopimusid]
  (some->
    (sql/select jarjestamissopimus
      (sql/fields :toimikunta)
      (sql/where {:jarjestamissopimusid jarjestamissopimusid}))
    first
    :toimikunta))

(defn hae-sopimukset-csv
  "Hakee toimikunnan sopimukset"
  [{:keys [toimikunta koulutustoimija oppilaitos tutkinto voimassa]}]
  (sql/select :jarjestamissopimus
    (sql/join :inner :sopimus_ja_tutkinto (and (= :jarjestamissopimus.jarjestamissopimusid :sopimus_ja_tutkinto.jarjestamissopimusid)
                                               (= :sopimus_ja_tutkinto.poistettu false)))
    (sql/join :inner :tutkintoversio (= :sopimus_ja_tutkinto.tutkintoversio :tutkintoversio.tutkintoversio_id))
    (sql/join :inner :nayttotutkinto (= :tutkintoversio.tutkintotunnus :nayttotutkinto.tutkintotunnus))
    (sql/join :inner :tutkintotoimikunta (= :jarjestamissopimus.toimikunta :tutkintotoimikunta.tkunta))
    (sql/join :left :koulutustoimija (= :jarjestamissopimus.koulutustoimija :koulutustoimija.ytunnus))
    (sql/fields :jarjestamissopimus.alkupvm :jarjestamissopimus.loppupvm :jarjestamissopimus.sopimusnumero
                [:koulutustoimija.nimi_fi :koulutustoimija_fi] [:koulutustoimija.nimi_sv :koulutustoimija_sv]
                :tutkintoversio.peruste
                [:tutkintotoimikunta.nimi_fi :toimikunta_fi] [:tutkintotoimikunta.nimi_sv :toimikunta_sv]
                [:nayttotutkinto.nimi_fi :tutkinto_fi] [:nayttotutkinto.nimi_sv :tutkinto_sv])
    (sql/where (merge {:jarjestamissopimus.voimassa voimassa}
                      (when toimikunta {:jarjestamissopimus.toimikunta toimikunta})
                      (when koulutustoimija {:jarjestamissopimus.koulutustoimija koulutustoimija})
                      (when oppilaitos {:jarjestamissopimus.tutkintotilaisuuksista_vastaava_oppilaitos oppilaitos})
                      (when tutkinto {:nayttotutkinto.tutkintotunnus tutkinto})))))
