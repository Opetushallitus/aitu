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
            [aitu.util :refer [sisaltaako-kentat? select-and-rename-keys]]
            [schema.macros :as sm]
            [aitu.auditlog :as auditlog]
            [clojure.set :refer [rename-keys]])
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

(defn hae-tutkinnolla
  "Hakee kaikki tietystä tutkinnosta tai opintoalasta vastuussa olevat toimikunnat"
  [tutkinto nykyinen]
  (map voimassaolo/taydenna-toimikunnan-voimassaolo
       (let [termi (str "%" tutkinto "%")]
         (map (comp sql-date->joda-date sql-timestamp->joda-datetime)
              ;; Ei käytä Kormaa, koska Korma ei salli joinien tekemistä pelkästään where-ehdon tekemiseen
              ;; vaan pakottaa tekemään selectin jokaisesta joinatusta taulusta
              (sql/exec-raw [(str "select ttk.*, tk.alkupvm, tk.loppupvm, tk.voimassa from tutkintotoimikunta ttk "
                                  "join toimikausi tk on ttk.toimikausi_id = tk.toimikausi_id "
                                  "where (exists (select 1 from toimikunta_ja_tutkinto tt "
                                  "              join nayttotutkinto t on tt.tutkintotunnus = t.tutkintotunnus "
                                  "              join opintoala oa on t.opintoala = oa.opintoala_tkkoodi "
                                  "              where tt.toimikunta = ttk.tkunta "
                                  "              and (t.nimi_fi ilike ? "
                                  "                   or t.nimi_sv ilike ? "
                                  "                   or oa.selite_fi ilike ? "
                                  "                   or oa.selite_sv ilike ?)) "
                                  "       or ? = '%%')"
                                  (when (= nykyinen "nykyinen")
                                    "and tk.voimassa "))
                             (repeat 5 termi)]
                            :results)))))

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

(defn hae-sopimukset
  "Hakee toimikunnan sopimukset"
  [tkunta]
  (sql/select :jarjestamissopimus
    (sql/join :inner :sopimus_ja_tutkinto (and (= :jarjestamissopimus.jarjestamissopimusid :sopimus_ja_tutkinto.jarjestamissopimusid)
                                               (= :sopimus_ja_tutkinto.poistettu false)))
    (sql/join :inner :tutkintoversio (= :sopimus_ja_tutkinto.tutkintoversio :tutkintoversio.tutkintoversio_id))
    (sql/join :inner :nayttotutkinto (= :tutkintoversio.tutkintotunnus :nayttotutkinto.tutkintotunnus))
    (sql/join :left :koulutustoimija (= :jarjestamissopimus.koulutustoimija :koulutustoimija.ytunnus))
    (sql/fields :jarjestamissopimus.alkupvm :jarjestamissopimus.loppupvm :jarjestamissopimus.sopimusnumero
                [:koulutustoimija.nimi_fi :koulutustoimija_nimi_fi] [:koulutustoimija.nimi_sv :koulutustoimija_nimi_sv]
                :tutkintoversio.peruste
                [:nayttotutkinto.nimi_fi :tutkinto_nimi_fi] [:nayttotutkinto.nimi_sv :tutkinto_nimi_sv])
    (sql/where {:jarjestamissopimus.toimikunta tkunta
                :voimassa true})))
