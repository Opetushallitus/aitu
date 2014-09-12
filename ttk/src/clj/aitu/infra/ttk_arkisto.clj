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
            [clojure.string :refer [blank? split]])
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
         (sql/with toimikausi))))

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
              (or (kielisyydet kieli) 0))
            [(count toimikunnat)])))

(def ^:private tyhja-rivi [[]])

(defn ^:private kielisyydet-toimikausittain [toimikunnat-toimikausittain toimikaudet]
  (concat [["Toimikausi" "suomenkielinen" "ruotsinkielinen" "kaksikielinen" "saamenkielinen" "yhteensä"]]
          (for [[toimikausi-id ttkt] toimikunnat-toimikausittain
                :let [toimikausi (get toimikaudet toimikausi-id)]]
            (cons (str (:alkupvm toimikausi) " - " (:loppupvm toimikausi)) (laske-kielisyydet ttkt)))
          [(cons "Yhteensä" (laske-kielisyydet toimikunnat))]))

(defn ^:private kielisyydet-opintoaloittain [toimikunnat-opintoaloittain]
  (concat [["Opintoala" "suomenkielinen" "ruotsinkielinen" "kaksikielinen" "saamenkielinen" "yhteensä"]]
          (for [[opintoala ttkt] toimikunnat-opintoaloittain]
            (cons opintoala (laske-kielisyydet ttkt)))
          [(cons "Yhteensä" (laske-kielisyydet (filter :voimassa toimikunnat)))]))

(defn ^:private hae-tilastot-toimikunnista []
  (let [toimikunta->opintoalat (apply merge-with clojure.set/union (for [{:keys [toimikunta selite_fi]} (sql/select sopimus-ja-tutkinto
                                                                                                          (sql/with jarjestamissopimus)
                                                                                                          (sql/with tutkintoversio
                                                                                                            (sql/with nayttotutkinto
                                                                                                              (sql/with opintoala)))
                                                                                                          (sql/fields :opintoala.selite_fi :jarjestamissopimus.toimikunta))]
                                                                     {toimikunta #{selite_fi}}))
        toimikunnat (->>
                      (sql/select tutkintotoimikunta
                        (sql/with toimikausi)
                        (sql/fields :toimikausi.voimassa :diaarinumero :nimi_fi :nimi_sv :kielisyys :toimikausi_id :tkunta))
                      (map #(assoc % :opintoalat (toimikunta->opintoalat (:tkunta %)))))

        toimikaudet (map-by :toimikausi_id (sql/select toimikausi))
        toimikausittain (group-by :toimikausi_id toimikunnat)
        opintoaloittain (apply merge-with concat (for [toimikunta toimikunnat
                                                       :when (:voimassa toimikunta)
                                                       opintoala (:opintoalat toimikunta)]
                                                   {opintoala [toimikunta]}))]
    (concat (kielisyydet-toimikausittain toimikausittain toimikaudet)
            tyhja-rivi
            (kielisyydet-opintoaloittain opintoaloittain))))

(defn hae-tilastot []
  )
