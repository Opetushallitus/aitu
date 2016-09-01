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
             [oph.common.util.util :refer [map-values select-and-rename-keys update-in-if-exists max-date min-date map-by]]
             [oph.common.util.http-util :refer [parse-iso-date]]
             [oph.korma.common :as sql-util]
             [aitu.toimiala.jarjestamissopimus :as domain]
             [aitu.infra.sopimus-ja-tutkinto-arkisto :as sopimus-ja-tutkinto-arkisto]
             [aitu.integraatio.sql.oppilaitos :as oppilaitos-kaytava]
             [aitu.integraatio.sql.koulutustoimija :as koulutustoimija-kaytava]
             [aitu.integraatio.sql.jarjestamissopimus :as sopimus-kaytava]
             [aitu.integraatio.sql.toimikunta :as toimikunta-kaytava]
             [aitu.infra.kayttaja-arkisto :as kayttaja-arkisto]
             [aitu.toimiala.voimassaolo.jarjestamissopimus :as voimassaolo]
             [aitu.toimiala.voimassaolo.saanto.jarjestamissopimus :as voimassaolo-saanto]
             [aitu.toimiala.voimassaolo.saanto.osoitepalvelu-jarjestamissopimus :as osoitepalvelu-voimassaolo]
             [clj-time.core :as time]
             [aitu.auditlog :as auditlog]
             [aitu.integraatio.sql.korma :refer :all]))

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
  (let [tutkinnot (sql/select sopimus-ja-tutkinto
                              (sql/where {:jarjestamissopimusid jarjestamissopimusid}))
        alkupvm (when-let [paivat (seq (keep :alkupvm tutkinnot))]
                  (apply min-date paivat))
        loppupvm (when (and (seq tutkinnot)
                            (every? :loppupvm tutkinnot))
                   (apply max-date (keep :loppupvm tutkinnot)))]
    (sql/update jarjestamissopimus
                (sql/set-fields {:alkupvm alkupvm, :loppupvm loppupvm})
      (sql/where {:jarjestamissopimusid jarjestamissopimusid}))
    (let [sopimus (voimassaolo-saanto/taydenna-sopimuksen-voimassaolo
                    (voimassaolo/taydenna-sopimukseen-liittyvien-tietojen-voimassaolo
                      (hae-ja-liita-tutkinnonosiin-asti jarjestamissopimusid)))]
      (aseta-sopimuksen-voimassaolo! jarjestamissopimusid (:voimassa sopimus)))))

(defn merkitse-sopimus-poistetuksi!
  "Asettaa sopimukselle poistettu -flagin"
  [jarjestamissopimusid]

  (let [sopimus (sopimus-kaytava/hae jarjestamissopimusid)
        sopimusid (:jarjestamissopimusid sopimus)
        diaarinumero (:sopimusnumero sopimus)]
    (auditlog/jarjestamissopimus-poisto! sopimusid diaarinumero))

  (sql/update jarjestamissopimus
    (sql/set-fields {:poistettu true
                     :voimassa false})
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
    (paivita-sopimuksen-voimassaolo! sopimusid)
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
   :kieli (:kieli sopimus_ja_tutkinto)
   :alkupvm (:alkupvm sopimus_ja_tutkinto)
   :loppupvm (:loppupvm sopimus_ja_tutkinto)})

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
      (update-in-if-exists [:toimikunta] toimikunta-kaytava/hae)
      (update-in-if-exists [:sopijatoimikunta] toimikunta-kaytava/hae)
      (update-in-if-exists [:koulutustoimija] koulutustoimija-kaytava/hae-linkki)
      (update-in-if-exists [:tutkintotilaisuuksista_vastaava_oppilaitos] oppilaitos-kaytava/hae)
      (update-in-if-exists [:muutettu_kayttaja] kayttaja-arkisto/hae)
      (update-in-if-exists [:luotu_kayttaja] kayttaja-arkisto/hae)))

(defn ^:private liita-tutkinnot-sopimukseen
  [jarjestamissopimus]
  (let [id (:jarjestamissopimusid jarjestamissopimus)
        sopimus-ja-tutkinto-rivit (sopimus-ja-tutkinto-arkisto/hae-jarjestamissopimukseen-liittyvat id)]
    (some-> jarjestamissopimus
      (assoc :sopimus_ja_tutkinto sopimus-ja-tutkinto-rivit))))

(defn ^:private liita-tutkinnot-sopimukseen-rajatut-tiedot
  [jarjestamissopimus]
  (let [id (:jarjestamissopimusid jarjestamissopimus)
        sopimus-ja-tutkinto-rivit (sopimus-ja-tutkinto-arkisto/hae-jarjestamissopimuksen-tutkinnot-rajatut-tiedot id)]
    (some-> jarjestamissopimus
      (assoc :tutkinnot sopimus-ja-tutkinto-rivit))))

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
    (map voimassaolo/taydenna-sopimukseen-liittyvien-tietojen-voimassaolo)
    (map voimassaolo-saanto/taydenna-sopimuksen-voimassaolo)))

(defn hae-tutkinnot-koulutustoimijoittain
  "Hakee koulutustoimijoittain listan tutkinnoista joihin koulutustoimijalla on voimassaoleva sopimus"
  []
  (->> (sql/select sopimus-ja-tutkinto
         (sql/with jarjestamissopimus)
         (sql/with tutkintoversio)
         (sql/fields :tutkintoversio.tutkintotunnus :jarjestamissopimus.koulutustoimija)
         (sql/where {:jarjestamissopimus.voimassa true}))
    (group-by :koulutustoimija)
    (map-values (comp set (partial map :tutkintotunnus)))))

(defn hae-tutkinnot-koulutustoimijoittain-jarjestamissopimusten-voimassaolon-kanssa
  []
  (let [sopimukset-ja-tutkinnot (sql/select sopimus-ja-tutkinto
                                  (sql/with jarjestamissopimus)
                                  (sql/with tutkintoversio)
                                  (sql/fields :tutkintoversio.tutkintotunnus :jarjestamissopimus.koulutustoimija )
                                  (sql/aggregate (min :jarjestamissopimus.alkupvm) :alkupvm :tutkintoversio.tutkintotunnus :jarjestamissopimus.koulutustoimija)
                                  (sql/aggregate (max :jarjestamissopimus.loppupvm) :loppupvm :tutkintoversio.tutkintotunnus :jarjestamissopimus.koulutustoimija)
                                  (sql/where {:jarjestamissopimus.voimassa true})
                                  (sql/group :tutkintoversio.tutkintotunnus :jarjestamissopimus.koulutustoimija))
        koulutustoimija->tutkinnot (group-by :koulutustoimija sopimukset-ja-tutkinnot)]
    (map-values #(map (fn [m] (dissoc m :koulutustoimija)) %) koulutustoimija->tutkinnot)))

(defn hae-kaikki-osoitepalvelulle
  "Hakee kaikki järjestämissopimukset ja niihin liittyvät tutkinnot osoitepalvelua varten"
  []
  (let [sopimuksen-tutkinnot (group-by :jarjestamissopimusid (sql/select sopimus-ja-tutkinto
                                                               (sql/with tutkintoversio
                                                                 (sql/with nayttotutkinto
                                                                   (sql/with opintoala)))))
        toimikunnat (map-by :tkunta (sql/select tutkintotoimikunta))]
    (remove nil? (for [sopimus (sql/select jarjestamissopimus
                                 (sql/where {:jarjestamissopimus.poistettu false}))]
                   (some-> sopimus
                     (assoc :tutkinnot (get sopimuksen-tutkinnot (:jarjestamissopimusid sopimus)))
                     (update :toimikunta toimikunnat)
                     osoitepalvelu-voimassaolo/taydenna-sopimuksen-voimassaolo
                     (#(when (:voimassa %) %))
                     (select-keys [:tutkinnot
                                   :toimikunta
                                   :vastuuhenkilo
                                   :sahkoposti
                                   :tutkintotilaisuuksista_vastaava_oppilaitos
                                   :koulutustoimija])
                     (update :toimikunta :tkunta)
                     (update :tutkinnot #(map rajaa-tutkinnon-kentat %)))))))

(defn hae
  "Hakee järjestämissopimuksen ja siihen liittyvät tiedot"
  [jarjestamissopimusid]
  (-> (sopimus-kaytava/hae jarjestamissopimusid)
    liita-perustiedot-sopimukseen
    liita-tutkinnot-sopimukseen))

(defn hae-rajatut-tiedot
  "Hakee järjestämissopimuksen ja siihen liittyvät tiedot"
  [jarjestamissopimusid]
  (-> (sopimus-kaytava/hae jarjestamissopimusid)
    liita-perustiedot-sopimukseen
    liita-tutkinnot-sopimukseen-rajatut-tiedot))

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
    (mapv liita-tutkinnot-sopimukseen-rajatut-tiedot)))

(defn hae-oppilaitoksen-sopimukset
  "Hakee oppilaitoksen järjestämissopimukset ja liittää niihin tarvittavat tiedot"
  [oppilaitoskoodi]
  (->> (sopimus-kaytava/hae-oppilaitoksen-sopimukset oppilaitoskoodi)
    (mapv liita-tutkinnot-sopimukseen-rajatut-tiedot)))

(defn hae-koulutustoimijan-sopimukset
  "Hakee koulutustoimijan järjestämissopimukset ja liittää niihin tarvittavat tiedot"
  [y-tunnus]
  (->> (sopimus-kaytava/hae-koulutustoimijan-sopimukset y-tunnus)
   (mapv liita-tutkinnot-sopimukseen-rajatut-tiedot)))

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
  [jarjestamissopimusid lisattavat]
  (let [lisattavat (map #(if (map? %) % {:tutkintoversio %})
                             lisattavat)]
    (auditlog/sopimuksen-tutkinnot-operaatio! :lisays jarjestamissopimusid (map :tutkintoversio lisattavat))
    (let [sopimus-tutkinto-liitokset
          (doall
            (for [lisattava lisattavat]
              (sql/insert sopimus-ja-tutkinto
                (sql/values (assoc (select-keys lisattava [:alkupvm :loppupvm :tutkintoversio :kieli])
                                   :jarjestamissopimusid jarjestamissopimusid)))))]
      (paivita-sopimuksen-voimassaolo! jarjestamissopimusid)
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
    (lisaa-tutkinnot-sopimukselle! jarjestamissopimusid (filter (comp lisattavat :tutkintoversio) (map #(clojure.set/rename-keys % {:tutkintoversio_id :tutkintoversio}) sopimus_ja_tutkinto)))
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
  (sql-util/update-unique jarjestamissuunnitelma
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
  (sql-util/select-unique jarjestamissuunnitelma
    (sql/fields :jarjestamissuunnitelma
                :jarjestamissuunnitelma_filename
                :jarjestamissuunnitelma_content_type)
    (sql/where {:jarjestamissuunnitelma_id jarjestamissuunnitelma_id})))

(defn hae-jarjestamissopimusid-sopimuksen-tutkinnolle
  "Hakee järjestämissopimusid:n sopimuksen tutkinnolle"
  [sopimus_ja_tutkinto_id]
  (:jarjestamissopimusid (sql-util/select-unique sopimus-ja-tutkinto
                                   (sql/where {:sopimus_ja_tutkinto_id sopimus_ja_tutkinto_id}))))

(defn hae-jarjestamissopimusid-jarjestamissuunnitelmalle
  "Hakee järjestämissopimusid:n järjestämissuunnitelmalle"
  [jarjestamissuunnitelma_id]
  (hae-jarjestamissopimusid-sopimuksen-tutkinnolle
    (:sopimus_ja_tutkinto (sql-util/select-unique jarjestamissuunnitelma
       (sql/fields :sopimus_ja_tutkinto)
       (sql/where {:jarjestamissuunnitelma_id jarjestamissuunnitelma_id})))))

(defn hae-liite
  "Hakee sopimuksen liitteen"
  [sopimuksen_liite_id]
  (sql-util/select-unique sopimuksen-liite
    (sql/fields :sopimuksen_liite
      :sopimuksen_liite_filename
      :sopimuksen_liite_content_type)
    (sql/where {:sopimuksen_liite_id sopimuksen_liite_id})))

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
  (hae-jarjestamissopimusid-sopimuksen-tutkinnolle
    (:sopimus_ja_tutkinto
      (sql-util/select-unique sopimuksen-liite
        (sql/fields :sopimus_ja_tutkinto)
        (sql/where {:sopimuksen_liite_id sopimuksen_liite_id})))))

(defn hae-jarjestamissopimuksen-toimikunta
  "Hakee toimikunnan tkunta-tunnisteen järjestämissopimusid:llä"
  [jarjestamissopimusid]
  (:toimikunta (sql-util/select-unique jarjestamissopimus
                 (sql/fields :toimikunta)
                 (sql/where {:jarjestamissopimusid jarjestamissopimusid}))))

(defn hae-sopimukset-csv
  "Hakee järjestämissopimukset hakuehdoilla"
  [{:keys [toimikunta koulutustoimija oppilaitos tutkinto opintoala toimikausi voimassa avaimet]}]
  (let [rivit (->
                (sql/select* :jarjestamissopimus)
                (sql/join :inner :sopimus_ja_tutkinto (and (= :jarjestamissopimus.jarjestamissopimusid :sopimus_ja_tutkinto.jarjestamissopimusid)
                                                           (= :sopimus_ja_tutkinto.poistettu false)))
                (sql/join :inner :tutkintoversio (= :sopimus_ja_tutkinto.tutkintoversio :tutkintoversio.tutkintoversio_id))
                (sql/join :inner :nayttotutkinto (= :tutkintoversio.tutkintotunnus :nayttotutkinto.tutkintotunnus))
                (sql/join :inner :opintoala (= :nayttotutkinto.opintoala :opintoala.opintoala_tkkoodi))
                (sql/join :inner :tutkintotoimikunta (= :jarjestamissopimus.toimikunta :tutkintotoimikunta.tkunta))
                (sql/join :left :koulutustoimija (= :jarjestamissopimus.koulutustoimija :koulutustoimija.ytunnus))
                (sql/join :left :oppilaitos (= :jarjestamissopimus.tutkintotilaisuuksista_vastaava_oppilaitos :oppilaitos.oppilaitoskoodi))
                (sql/join :left [(sql/subselect :sopimus_ja_tutkinto_ja_osaamisala
                                   (sql/join :inner :osaamisala (= :osaamisala :osaamisala.osaamisala_id))
                                   (sql/fields :sopimus_ja_tutkinto [(sql/sqlfn string_agg :osaamisala.nimi_fi ", ") :osaamisalat])
                                   (sql/group :sopimus_ja_tutkinto)) :osaamisalat]
                          (= :osaamisalat.sopimus_ja_tutkinto :sopimus_ja_tutkinto.sopimus_ja_tutkinto_id))
                (sql/fields :jarjestamissopimus.alkupvm :jarjestamissopimus.loppupvm :jarjestamissopimus.sopimusnumero
                            :koulutustoimija.ytunnus  [:koulutustoimija.nimi_fi :koulutustoimija_fi] [:koulutustoimija.nimi_sv :koulutustoimija_sv] [:koulutustoimija.sahkoposti :koulutustoimija_sahkoposti]
                            [(sql/raw "case when tutkintoversio.siirtymaajan_loppupvm = '2199-01-01' then null else tutkintoversio.siirtymaajan_loppupvm end") :siirtymaajan_loppupvm]
                            :tutkintotoimikunta.diaarinumero [:tutkintotoimikunta.nimi_fi :toimikunta_fi] [:tutkintotoimikunta.nimi_sv :toimikunta_sv] :tutkintotoimikunta.tilikoodi
                            [:opintoala.opintoala_tkkoodi :opintoalatunnus] [:opintoala.selite_fi :opintoala_fi] [:opintoala.selite_sv :opintoala_sv]
                            :nayttotutkinto.tutkintotunnus [:nayttotutkinto.nimi_fi :tutkinto_fi] [:nayttotutkinto.nimi_sv :tutkinto_sv] :tutkintoversio.peruste
                            :oppilaitos.oppilaitoskoodi [:oppilaitos.nimi :oppilaitos] :sopimus_ja_tutkinto.vastuuhenkilo
                            [:sopimus_ja_tutkinto.sahkoposti :vastuuhenkilo_sahkoposti]
                            [:sopimus_ja_tutkinto.puhelin :vastuuhenkilo_puhelin]
                            :sopimus_ja_tutkinto.kieli :osaamisalat.osaamisalat
                            )
                (cond->
                  (not (nil? voimassa)) (sql/where {:jarjestamissopimus.voimassa voimassa})
                  toimikunta            (sql/where {:jarjestamissopimus.toimikunta toimikunta})
                  koulutustoimija       (sql/where {:jarjestamissopimus.koulutustoimija koulutustoimija})
                  oppilaitos            (sql/where {:jarjestamissopimus.tutkintotilaisuuksista_vastaava_oppilaitos oppilaitos})
                  tutkinto              (sql/where {:nayttotutkinto.tutkintotunnus tutkinto})
                  (seq opintoala)       (sql/where {:nayttotutkinto.opintoala [in opintoala]})
                  toimikausi            (sql/where {:tutkintotoimikunta.toimikausi_id toimikausi}))
                sql/exec)]
    (if avaimet
      (map #(select-keys % avaimet) rivit)
      rivit)))
