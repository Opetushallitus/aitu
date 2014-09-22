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

(ns aitu.infra.ttk-arkisto
  (:require [korma.core :as sql]
            [aitu.infra.jarjestamissopimus-arkisto :as sopimus-arkisto]
            [aitu.infra.kayttaja-arkisto :as kayttaja-arkisto]
            [aitu.infra.toimikausi-arkisto :as toimikausi-arkisto]
            [aitu.toimiala.skeema :as skeema]
            [aitu.toimiala.voimassaolo.saanto.toimikunta :as voimassaolo]
            [aitu.toimiala.voimassaolo.toimikunta :as toimikunnan-voimassaolo]
            [aitu.util :refer [select-and-rename-keys]]
            [oph.common.util.util :refer [sisaltaako-kentat?]]
            [schema.macros :as sm]
            [aitu.auditlog :as auditlog]
            [clojure.set :refer [rename-keys]]
            [oph.korma.korma :refer  :all ]
            [oph.common.util.util :refer :all]
            [clojure.string :refer [blank? split]]
            [clojure-csv.core :refer [write-csv]]
            [clojure.tools.logging :as log])
  (:use [aitu.integraatio.sql.korma]))

(defn hae-toimikunnan-diaarinumero
  "Hakee toimikunnan diaarinumeron"
  [tkunta]
  (some-> (sql/select tutkintotoimikunta
            (sql/fields :diaarinumero)
            (sql/where {:tkunta tkunta}))
    first
    :diaarinumero))

(defn hae-toimikunnan-tunniste
  "Hakee toimikunnan tkunta tunnisteen diaarinumerolla"
  [diaarinumero]
  (some-> (sql/select tutkintotoimikunta
            (sql/fields :tkunta)
            (sql/where {:diaarinumero diaarinumero}))
    first
    :tkunta))

(defn ^:test-api tyhjenna!
  "Tyhjentää arkiston."
  []
  (sql/exec-raw "delete from toimikunta_ja_tutkinto")
  (sql/exec-raw "delete from jasenyys")
  (sql/exec-raw "delete from sopimus_ja_tutkinto")
  (sql/exec-raw "delete from jarjestamissopimus")
  (sql/exec-raw "delete from tutkintotoimikunta"))

(sm/defn lisaa!
  "Lisää toimikunnan arkistoon."
  [toimikunta :- skeema/ToimikunnanTiedot]
  (auditlog/tutkintotoimikunta-operaatio! :lisays (:tkunta toimikunta) (:diaarinumero toimikunta))
  (sql/insert tutkintotoimikunta
    (sql/values toimikunta)))

(defn hae-kaikki
  "Hakee kaikki toimikunnat."
  []
  (map voimassaolo/taydenna-toimikunnan-voimassaolo
       (sql/select tutkintotoimikunta
         (sql/with toimikausi)
         (sql/order :nimi_fi))))

(defn hae-ehdoilla
  "Hakee kaikki tietystä tutkinnosta tai opintoalasta vastuussa olevat toimikunnat"
  [ehdot]
  (let [nimi (str "%" (:nimi ehdot) "%")
        kielisyydet (split (:kielisyys ehdot "") #",")
        toimikunnat (->> (sql/select tutkintotoimikunta
                           (sql/with toimikausi)
                           (sql/where (and
                                        (or (blank? (:tunnus ehdot))
                                            (sql/sqlfn exists (sql/subselect toimikunta-ja-tutkinto
                                                                (sql/with nayttotutkinto
                                                                  (sql/with opintoala))
                                                                (sql/where (and {:toimikunta_ja_tutkinto.toimikunta :tutkintotoimikunta.tkunta}
                                                                                (or {:nayttotutkinto.tutkintotunnus (:tunnus ehdot)}
                                                                                    {:opintoala.opintoala_tkkoodi (:tunnus ehdot)}))))))
                                        (or (not= (:toimikausi ehdot) "nykyinen")
                                            {:toimikausi.voimassa true})
                                        (or (blank? (:nimi ehdot))
                                            {:nimi_fi [ilike nimi]}
                                            {:nimi_sv [ilike nimi]})
                                        (or (blank? (:kielisyys ehdot))
                                            {:kielisyys [in kielisyydet]})))
                           (sql/order :nimi_fi))
                      (map voimassaolo/taydenna-toimikunnan-voimassaolo))]
    (if (:avaimet ehdot)
      (map #(select-keys % (:avaimet ehdot)) toimikunnat)
      toimikunnat)))

(defn hae-nykyiset
  "Hakee toimikunnat voimassa olevalta toimikaudelta"
  []
  (map voimassaolo/taydenna-toimikunnan-voimassaolo
       (sql/select tutkintotoimikunta
         (sql/with toimikausi)
         (sql/order :nimi_fi)
         (sql/where {:toimikausi.voimassa true}))))

(defn hae
  "Hakee toimikunnan diaarinumeron perusteella"
  [diaarinumero]
  (let [toimikunta (first
                     (sql/select
                       tutkintotoimikunta
                       (sql/with toimikausi
                         (sql/fields :alkupvm :loppupvm :voimassa))
                       (sql/with jasenyys
                         (sql/fields :alkupvm :loppupvm :rooli :edustus :jasenyys_id :henkiloid)
                         (sql/with henkilo
                           (sql/fields :etunimi :sukunimi :sahkoposti :sahkoposti_julkinen :aidinkieli)
                           (sql/with jarjesto
                             (sql/fields [:nimi_fi :jarjesto_nimi_fi] [:nimi_sv :jarjesto_nimi_sv]))))
                       (sql/with nayttotutkinto
                         (sql/with opintoala
                           (sql/fields [:selite_fi :opintoala_nimi_fi] [:selite_sv :opintoala_nimi_sv])))
                       (sql/where {:diaarinumero diaarinumero})))
        jarjestamissopimukset (sopimus-arkisto/hae-toimikunnan-sopimukset (:tkunta toimikunta))]
    (some-> toimikunta
            (assoc :jarjestamissopimus jarjestamissopimukset)
            (update-in [:muutettu_kayttaja] kayttaja-arkisto/hae)
            (update-in [:luotu_kayttaja] kayttaja-arkisto/hae))))

(defn ^:private rajaa-jasenyyden-kentat
  "Valitsee jäsenyydestä osoitepalvelun tarvitsemat kentät"
  [jasenyys]
  (select-keys jasenyys [:etunimi :sukunimi :sahkoposti :aidinkieli :rooli
                         :edustus :osoite :postinumero :postitoimipaikka :voimassa]))

(defn ^:private rajaa-toimikunnan-kentat
  "Valitsee toimikunnasta osoitepalvelun tarvitsemat kentät"
  [toimikunta]
  (let [nimi (select-and-rename-keys toimikunta [[:nimi_fi :fi] [:nimi_sv :sv]])
        toimikunta (select-and-rename-keys toimikunta [:toimikausi
                                                       [:jasenyys :jasenyydet]
                                                       [:tkunta :id]
                                                       :kielisyys
                                                       :sahkoposti])]
    (assoc toimikunta :nimi nimi)))

(defn hae-osoitepalvelulle
  "Hakee toimikuntien tiedot osoitepalvelua varten"
  []
  (for [toimikunta (sql/select
                      tutkintotoimikunta
                      (sql/with jasenyys
                        (sql/fields :alkupvm :loppupvm :rooli :edustus)
                        (sql/with henkilo
                          (sql/fields :etunimi :sukunimi :sahkoposti :aidinkieli
                                      :osoite :postinumero :postitoimipaikka))))
        :let [taydennetty-toimikunta (toimikunnan-voimassaolo/taydenna-toimikunnan-ja-liittyvien-tietojen-voimassaolo toimikunta)
              toimikausi (cond
                           (:voimassa taydennetty-toimikunta) :voimassa
                           (:vanhentunut taydennetty-toimikunta) :mennyt
                           :else :tuleva)]]
    (-> taydennetty-toimikunta
      (assoc :toimikausi toimikausi)
      rajaa-toimikunnan-kentat
      (update-in [:jasenyydet] #(map rajaa-jasenyyden-kentat %)))))

(sm/defn paivita!
  "Päivittää toimikunnan tiedot"
  [diaarinumero, ttk :- skeema/ToimikunnanTiedot]
  (auditlog/tutkintotoimikunta-operaatio! :paivitys (:tkunta ttk) diaarinumero)
  (let [ttk-ro (dissoc ttk :tilikoodi)]
    (sql/update tutkintotoimikunta
      (sql/set-fields ttk-ro)
      (sql/where {:diaarinumero diaarinumero}))))

(defn ^:test-api poista-jasen!
  "Poistaa toimikunnan jäsenen"
  [tkunta henkiloid]
  (sql/delete jasenyys
    (sql/where {:toimikunta tkunta :henkiloid henkiloid})))

(defn ^:test-api poista-jasenet!
  "Poistaa kaikki toimikunnan jäset"
  [tkunta]
  (sql/delete jasenyys
    (sql/where {:toimikunta tkunta})))

(defn ^:test-api poista!
  "Poistaa toimikunnan"
  [tkunta]
  (sql/delete tutkintotoimikunta
    (sql/where {:tkunta tkunta})))

(defn lisaa-jasen!
  "Lisää toimikunnan jäsenen"
  [toimikunnan_henkilo]
  (auditlog/jasenyys-operaatio! :lisays (:tkunta toimikunnan_henkilo) (:jasenyys_id toimikunnan_henkilo))
  (sql/insert jasenyys
    (sql/values toimikunnan_henkilo)))

(defn hae-jasen
  "Hakee toimikunnan jäsenen jasenyysid:n perusteella"
  [jasenyysid]
  (first
    (sql/select jasenyys
      (sql/where {:jasenyys_id jasenyysid}))))

(defn ^:private poista-jasenyys!
  [tkunta jasenyys_id]
  (auditlog/jasenyys-operaatio! :poisto tkunta jasenyys_id)
  (sql/delete jasenyys
    (sql/where {:toimikunta tkunta :jasenyys_id jasenyys_id})))

(defn ^:private paivita-jasenyys!
  [tkunta jasen]
  (auditlog/jasenyys-operaatio! :paivitys tkunta (:jasenyys_id jasen))
  (sql/update jasenyys
    (sql/set-fields jasen)
    (sql/where {:toimikunta tkunta
                :jasenyys_id (:jasenyys_id jasen)})))

(defn paivita-tai-poista-jasenyys!
  "Päivittää tai poistaa jäsenyyden tiedot"
  [diaarinumero jasen]
  (let [tkunta (hae-toimikunnan-tunniste diaarinumero)]
    (if (:poistettu jasen)
      (do
        (auditlog/jasenyys-operaatio! :poisto tkunta (:jasenyys_id jasen))
        (poista-jasenyys! tkunta (:jasenyys_id jasen)))
      (do
        (auditlog/jasenyys-operaatio! :paivitys tkunta (:jasenyys_id jasen))
        (paivita-jasenyys! tkunta (dissoc jasen :poistettu))))))

(defn hae-jasenyydet
  "Hakee kaikki henkilön jäsenyydet tietyssä toimikunnassa"
  [henkiloid toimikunta]
  (sql/select jasenyys
    (sql/where {:henkiloid henkiloid
                :toimikunta toimikunta})))

(defn hae-toimikuntien-jasenyydet
  "Hakee kaikki toimikunnat joissa henkilö jäsenenä"
  [henkiloid]
  (sql/select jasenyys
    (sql/fields :toimikunta :alkupvm :loppupvm)
    (sql/with tutkintotoimikunta
      (sql/fields :toimikausi_alku :toimikausi_loppu))
    (sql/where {:henkiloid henkiloid})))

(defn hae-toimikuntien-henkilot
  "Hakee toimikuntien henkilot"
  [toimikunnat]
  (sql/select jasenyys
    (sql/fields :henkiloid :toimikunta :alkupvm :loppupvm)
    (sql/with tutkintotoimikunta
      (sql/fields :toimikausi_alku :toimikausi_loppu))
    (sql/where {:toimikunta [in toimikunnat]})))

(defn ^:test-api lisaa-tutkinto!
  "Lisää toimikunnan tutkinnon"
  [toimikunnan-tutkinto]
  (sql/insert toimikunta-ja-tutkinto
    (sql/values toimikunnan-tutkinto)))

(defn ^:test-api poista-tutkinto!
  "Poistaa toimikunnan tutkinnon"
  [tkunta tutkintotunnus]
  (sql/delete toimikunta-ja-tutkinto
    (sql/where {:toimikunta tkunta :tutkintotunnus tutkintotunnus})))

(defn ^:test-api ^:private poista-kaikki-tutkinnot!
  "Poistaa toimikunnan kaikki tutkinnot"
  [tkunta]
  (sql/delete toimikunta-ja-tutkinto
    (sql/where {:toimikunta tkunta})))

(defn hae-termilla
  "Suodattaa hakutuloksia hakutermillä"
  [termi]
  (for [ttk (hae-nykyiset)
        :when (sisaltaako-kentat? ttk [:nimi_fi :nimi_sv] termi)]
    (select-keys ttk [:tkunta :diaarinumero :nimi_fi :nimi_sv])))

(defn paivita-tutkinnot!
  "Päivittää toimikunnan tutkinnot"
  [tkunta tutkinnot]
  (auditlog/toimikunnan-tutkinnot-paivitys! tkunta (map :tutkintotunnus tutkinnot))
  (poista-kaikki-tutkinnot! tkunta)
  (doseq [tutkinto tutkinnot]
    (sql/insert toimikunta-ja-tutkinto
      (sql/values {:toimikunta tkunta
                   :tutkintotunnus (:tutkintotunnus tutkinto)}))))

(defn hae-jasenet
  "Hakee toimikunnan jäsenet"
  ([tkunta]
    (hae-jasenet tkunta {}))
  ([tkunta ehdot]
    (sql/select jasenyys
      (sql/with henkilo
        (sql/with jarjesto))
      (sql/fields :henkilo.etunimi :henkilo.sukunimi :henkilo.sahkoposti [:henkilo.aidinkieli :kielisyys]
                  :jasenyys.rooli :jasenyys.edustus [:jarjesto.nimi_fi :jarjesto_nimi_fi] [:jarjesto.nimi_sv :jarjesto_nimi_sv])
      (sql/where {:toimikunta tkunta
                  :loppupvm [(if (:voimassa ehdot true) >= <) (sql/sqlfn now)]})
      (sql/order :henkilo.sukunimi)
      (sql/order :henkilo.etunimi))))

(defn ^:private laske-kielisyydet [toimikunnat]
  (let [kielisyydet (frequencies (map :kielisyys toimikunnat))]
    (concat (for [kieli ["fi" "sv" "2k" "se"]]
              (get kielisyydet kieli 0))
            [(count toimikunnat)])))

(def ^:private tyhja-rivi [[]])

(defn ^:private tilastot-opintoaloittain [toimikunnat-opintoaloittain]
  (concat [["Opintoala" "Suomenkieliset toimikunnat" "Ruotsinkieliset toimikunnat" "Kaksikieliset toimikunnat" "Saamenkieliset toimikunnat" "Toimikunnat yhteensä"
            "Miesjäsenet" "Naisjäsenet" "Jäsenet yhteensä"]]
          (for [[opintoala toimikunnat] (sort-by key toimikunnat-opintoaloittain)
                :let [jasenet (apply merge-with + (map :jasenet toimikunnat))
                      miehet (get jasenet "mies" 0)
                      naiset (get jasenet "nainen" 0)]]
            (concat [opintoala]
                    (laske-kielisyydet toimikunnat)
                    [miehet naiset (+ miehet naiset)]))
          [(concat ["Yhteensä"]
                   (laske-kielisyydet (mapcat val toimikunnat-opintoaloittain))
                   (let [jasenet (apply merge-with + (map :jasenet (mapcat val toimikunnat-opintoaloittain)))
                         miehet (get jasenet "mies" 0)
                         naiset (get jasenet "nainen" 0)]
                     [miehet naiset (+ miehet naiset)]))]))

(defn ^:private tilastot-toimikunnittain [toimikunnat]
  (concat [["Toimikunta" "Miesjäsenet" "Naisjäsenet" "Jäsenet yhteensä" "Tutkintoja"]]
          (for [toimikunta (sort-by :nimi_fi toimikunnat)
                :let [jasenet (:jasenet toimikunta)
                      miehet (get jasenet "mies" 0)
                      naiset (get jasenet "nainen" 0)]]
            [(:nimi_fi toimikunta) miehet naiset (+ miehet naiset) (count (:tutkinnot toimikunta))])
          (let [jasenet (apply merge-with + (map :jasenet toimikunnat))
                miehet (get jasenet "mies" 0)
                naiset (get jasenet "nainen" 0)]
            [["Yhteensä" miehet naiset (+ miehet naiset)]])))

(defn sopimukset-tutkinnoittain [tutkinnot]
  (concat [["Tutkinto" "sopimuksia"]]
          (sort-by key tutkinnot)
          [["Yhteensä" (reduce + 0 (vals tutkinnot))]]))

(defn ^:private muuta-kaikki-stringeiksi [rivit]
  (clojure.walk/postwalk (fn [x]
                           (if (coll? x)
                             x
                             (str x)))
                         rivit))

(defn hae-tilastot-toimikunnista [toimikausi-id]
  (let [toimikunta->opintoalat (apply merge-with clojure.set/union (for [{:keys [toimikunta selite_fi]} (sql/select toimikunta-ja-tutkinto
                                                                                                          (sql/with nayttotutkinto
                                                                                                            (sql/with opintoala))
                                                                                                          (sql/with tutkintotoimikunta)
                                                                                                          (sql/fields :toimikunta :opintoala.selite_fi)
                                                                                                          (sql/where {:tutkintotoimikunta.toimikausi_id toimikausi-id}))]
                                                                     {toimikunta #{selite_fi}}))
        toimikunta->tutkinnot (apply merge-with clojure.set/union (for [{:keys [toimikunta nimi_fi]} (sql/select toimikunta-ja-tutkinto
                                                                                                        (sql/with nayttotutkinto)
                                                                                                        (sql/with tutkintotoimikunta)
                                                                                                        (sql/fields :toimikunta :nayttotutkinto.nimi_fi)
                                                                                                        (sql/where {:tutkintotoimikunta.toimikausi_id toimikausi-id}))]
                                                                    {toimikunta #{nimi_fi}}))
        toimikunta->jasenet (map-values #(frequencies (map :sukupuoli %))
                                        (group-by :toimikunta (sql/select jasenyys
                                                                (sql/with henkilo)
                                                                (sql/with tutkintotoimikunta)
                                                                (sql/fields :toimikunta :henkilo.sukupuoli)
                                                                (sql/where {:tutkintotoimikunta.toimikausi_id toimikausi-id}))))
        toimikunnat (->>
                      (sql/select tutkintotoimikunta
                        (sql/with toimikausi)
                        (sql/fields :toimikausi.voimassa :diaarinumero :nimi_fi :nimi_sv :kielisyys :toimikausi_id :tkunta)
                        (sql/where {:tutkintotoimikunta.toimikausi_id toimikausi-id}))
                      (map #(assoc % :opintoalat (toimikunta->opintoalat (:tkunta %))
                                     :tutkinnot (toimikunta->tutkinnot (:tkunta %))
                                     :jasenet (toimikunta->jasenet (:tkunta %)))))
        opintoaloittain (apply merge-with concat (for [toimikunta toimikunnat
                                                       :when (:voimassa toimikunta)
                                                       opintoala (:opintoalat toimikunta)]
                                                   {opintoala [toimikunta]}))
        tutkinnot (into {} (for [{:keys [nimi_fi sopimukset]} (sql/select sopimus-ja-tutkinto
                                                                (sql/with tutkintoversio
                                                                  (sql/with nayttotutkinto))
                                                                (sql/with jarjestamissopimus)
                                                                (sql/where {:jarjestamissopimus.voimassa true})
                                                                (sql/fields :nayttotutkinto.nimi_fi)
                                                                (sql/aggregate (count :*) :sopimukset :nayttotutkinto.nimi_fi))]
                             [nimi_fi sopimukset]))
        raportti (concat (tilastot-toimikunnittain toimikunnat)
                         tyhja-rivi
                         (tilastot-opintoaloittain opintoaloittain)
                         tyhja-rivi
                         (sopimukset-tutkinnoittain tutkinnot))]
    (write-csv (muuta-kaikki-stringeiksi raportti) :delimiter \;)))

(defn ^:private hae-jasenyydet-ehdoilla [{:keys [nykyinen-toimikausi rooli edustus jarjesto kieli yhteystiedot]}]
  (group-by :toimikausi_id (->
                             (sql/select* jasenyys)
                             (sql/with henkilo
                               (sql/join {:table :jarjesto} (= :henkilo.jarjesto :jarjesto.jarjestoid)))
                             (sql/with tutkintotoimikunta)
                             (sql/where (and {:alkupvm [<= (sql/sqlfn now)]}
                                             (or {:loppupvm [>= (sql/sqlfn now)]}
                                                 {:loppupvm nil})))
                             (sql/order :henkilo.sukunimi)
                             (sql/order :henkilo.etunimi)
                             (sql/fields [:tutkintotoimikunta.nimi_fi :toimikunta] :tutkintotoimikunta.toimikausi_id
                                         :henkilo.etunimi :henkilo.sukunimi :rooli :edustus :henkilo.aidinkieli [:jarjesto.nimi_fi :jarjesto])
                             (cond->
                               (seq rooli)         (sql/where {:rooli [in rooli]})
                               (seq edustus)       (sql/where {:edustus [in edustus]})
                               (seq jarjesto)      (sql/where {:henkilo.jarjesto [in jarjesto]})
                               (seq kieli)         (sql/where {:henkilo.aidinkieli [in kieli]})
                               nykyinen-toimikausi (sql/where {:toimikausi.voimassa true})
                               yhteystiedot        (sql/fields :henkilo.sahkoposti :henkilo.puhelin :henkilo.osoite :henkilo.postinumero :henkilo.postitoimipaikka))
                             sql/exec)))

(defn ^:private henkilo->rivi [henkilo]
  ((juxt :sukunimi :etunimi :toimikunta :rooli :edustus :aidinkieli :jarjesto
         :sahkoposti :puhelin :osoite :postinumero :postitoimipaikka)
    henkilo))

(defn ^:private henkilot->rivit [toimikausi henkilot]
  (concat [[(str "Toimikausi " (:alkupvm toimikausi) " - " (:loppupvm toimikausi))]]
          (map henkilo->rivi henkilot)
          tyhja-rivi))

(defn ^:private toimikunta->rivi [toimikunta]
  ((juxt :nimi_fi :nimi_sv :diaarinumero :tilikoodi) toimikunta))

(defn ^:private toimikunnat->rivit [toimikausi toimikunnat]
  (concat [[(str "Toimikausi " (:alkupvm toimikausi) " - " (:loppupvm toimikausi))]]
          (map toimikunta->rivi toimikunnat)
          tyhja-rivi))

(defn ^:private sopimus->rivit [sopimus]
  (for [sopimus-ja-tutkinto (sort-by (comp :nimi_fi :tutkintoversio) (:sopimus_ja_tutkinto sopimus))
        :let [tutkinto (:tutkintoversio sopimus-ja-tutkinto)]]
    [(get-in sopimus [:koulutustoimija :nimi_fi]) (get-in sopimus [:tutkintotilaisuuksista_vastaava_oppilaitos :nimi])
     (:nimi_fi tutkinto) (:peruste tutkinto) (:sopimusnumero sopimus) (:loppupvm sopimus)
     (:vastuuhenkilo tutkinto) (:sahkoposti tutkinto) (:puhelin tutkinto) (:nayttomestari tutkinto)]))

(defn sopimukset->rivit [toimikausi sopimukset]
  (concat [[(str "Toimikausi " (:alkupvm toimikausi) " - " (:loppupvm toimikausi))]]
          (mapcat sopimus->rivit sopimukset)
          tyhja-rivi))

(defn hae-toimikuntaraportti [{:keys [nykyinen-toimikausi jasenet] :as hakuehdot}]
  (let [jasenet true
        toimikaudet (if nykyinen-toimikausi
                      (filter :voimassa (toimikausi-arkisto/hae-kaikki))
                      (toimikausi-arkisto/hae-kaikki))
        toimikunnat (group-by :toimikausi_id (if nykyinen-toimikausi
                                               (hae-nykyiset)
                                               (hae-kaikki)))
        jasenyydet (hae-jasenyydet-ehdoilla hakuehdot)
        sopimukset (group-by (comp :toimikausi_id :toimikunta) (sopimus-arkisto/hae-kaikki))
        raportti (concat [["Toimikunnat"]]
                         (apply concat (for [toimikausi toimikaudet
                                             :let [toimikausi-id (:toimikausi_id toimikausi)]]
                                         (toimikunnat->rivit toimikausi (sort-by :nimi_fi (get toimikunnat toimikausi-id)))))
                         (when jasenet
                           (apply concat
                                  [["Jäsenet"]]
                                  (for [toimikausi toimikaudet
                                        :let [toimikausi-id (:toimikausi_id toimikausi)]]
                                    (henkilot->rivit toimikausi (get jasenyydet toimikausi-id)))))
                         [["Sopimukset"]]
                         (apply concat (for [toimikausi toimikaudet
                                             :let [toimikausi-id (:toimikausi_id toimikausi)]]
                                         (sopimukset->rivit toimikausi (sort-by (comp :nimi_fi :koulutustoimija) (get sopimukset toimikausi-id))))))]
    (write-csv (muuta-kaikki-stringeiksi raportti) :delimiter \;)))
