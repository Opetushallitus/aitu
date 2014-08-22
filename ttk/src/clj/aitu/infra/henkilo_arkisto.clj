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

(ns aitu.infra.henkilo-arkisto
  (:require korma.db
            [korma.core :as sql]
            [schema.macros :as sm]
            [aitu.toimiala.henkilo :refer :all]
            [aitu.toimiala.skeema :refer [HenkiloTaiTiedot Henkilo]]
            [oph.common.util.util :refer [sisaltaako-kentat?]]
            [aitu.integraatio.sql.toimikunta :as toimikunta-kaytava]
            [aitu.auditlog :as auditlog]
            [clojure.string :refer [blank?]])
  (:use [aitu.integraatio.sql.korma]))

(defn ^:test-api tyhjenna!
  "Tyhjentää arkiston."
  []
  (sql/exec-raw "delete from jasenyys")
  (sql/exec-raw "delete from henkilo"))

(sm/defn lisaa!
  "Lisää henkilön arkistoon."
  [uusi-henkilo :- HenkiloTaiTiedot]
  (let [luotu-henkilo (sql/insert henkilo
                        (sql/values uusi-henkilo))]
    (auditlog/henkilo-operaatio! :lisays (:henkiloid luotu-henkilo))
    luotu-henkilo))

(defn ^:private pg-hae-jasenyydet-ehdoilla
  [ehdot]
  (let [toimikunta (str "%" (:toimikunta ehdot) "%")]
    (sql/select jasenyys
      (sql/fields :alkupvm :loppupvm :muutettuaika :henkiloid :rooli)
      (sql/with tutkintotoimikunta
        (sql/with toimikausi)
        (sql/fields
          :nimi_fi
          :nimi_sv
          :diaarinumero
          :toimikausi_alku
          :toimikausi_loppu
          [:muutettuaika :toimikunta_muutettuaika])
        (sql/where (and
                     (or (not= (:toimikausi ehdot) "nykyinen")
                         {:toimikausi.voimassa true})
                     (or (blank? (:toimikunta ehdot))
                         {:tutkintotoimikunta.nimi_fi [ilike toimikunta]}
                         {:tutkintotoimikunta.nimi_sv [ilike toimikunta]})))))))

(defn ^:private pg-hae-henkilot-ehdoilla
  [ehdot]
  (let [nimi (str "%" (:nimi ehdot) "%")]
    (->
      (sql/select henkilo
        (sql/with jarjesto
          (sql/fields [:nimi_fi :jarjesto_nimi_fi]
                      [:nimi_sv :jarjesto_nimi_sv]
                      [:keskusjarjestotieto :keskusjarjesto])
          (sql/with keskusjarjesto
            (sql/fields [:nimi_fi :keskusjarjesto_nimi]))
          (sql/where (or (blank? (:nimi ehdot))
                         {:henkilo.etunimi [ilike nimi]}
                         {:henkilo.sukunimi [ilike nimi]}))))
      piilota-salaiset)))

(defn yhdista-henkilot-ja-jasenyydet
  [henkilot jasenyydet]
  (let [jasenyydet-map (group-by :henkiloid jasenyydet)]
    (for [hlo henkilot]
      (assoc hlo :jasenyydet (jasenyydet-map (:henkiloid hlo) [])))))

; Henkilöt ja toimikuntajäsenyydet haetaan erikseen ja yhdistetään
; siksi, että Korma hakee many-to-many suhteet laiskasti ja
; jokaisen entityn kohdalta erikseen, josta seuraa n+1 hakua
(defn hae-kaikki
  "Hakee kaikki henkilöt."
  []
  (yhdista-henkilot-ja-jasenyydet (pg-hae-henkilot-ehdoilla {})
                                  (pg-hae-jasenyydet-ehdoilla {:toimikausi "kaikki"})))

(defn hae-nykyiset
  "Hakee kaikki henkilöt nykyisen toimikauden toimikunnista"
  []
  (let [yhdistetyt-henkilot
        (yhdista-henkilot-ja-jasenyydet (pg-hae-henkilot-ehdoilla {})
                                        (pg-hae-jasenyydet-ehdoilla {:toimikausi "nykyinen"}))]
    (remove #(empty? (:jasenyydet %)) yhdistetyt-henkilot)))

(defn hae
  "Hakee henkilön id:n perusteella"
  [id]
  (->
    (sql/select henkilo
      (sql/where {:henkiloid id}))
    piilota-salaiset
    first))

(defn hae-hlo-ja-ttk
  "Hakee henkilön ja toimikuntien jäsenyystiedot id:n perusteella"
  [id]
  (some->
    (sql/select henkilo
      (sql/with jarjesto
        (sql/fields [:nimi_fi :jarjesto_nimi_fi]
                    [:nimi_sv :jarjesto_nimi_sv]
                    [:keskusjarjestotieto :keskusjarjesto])
        (sql/with keskusjarjesto
          (sql/fields [:nimi_fi :keskusjarjesto_nimi])))
      (sql/with jasenyys
        (sql/fields :alkupvm :loppupvm :muutettuaika :rooli :edustus :toimikunta))
      (sql/where {:henkiloid id}))
    piilota-salaiset
    first
    (update-in [:jasenyys] #(for [jasen %] (assoc jasen :ttk (toimikunta-kaytava/hae (:toimikunta jasen)))))))

(sm/defn paivita!
  "Päivittää henkilön tiedot"
  [paivitettava-henkilo :- Henkilo]
  (auditlog/henkilo-operaatio! :paivitys (:henkiloid paivitettava-henkilo))
  (sql/update henkilo
    (sql/set-fields paivitettava-henkilo)
    (sql/where {:henkiloid (:henkiloid paivitettava-henkilo)})))

(defn ^:test-api poista!
  "Poistaa henkilön tiedot"
  [id]
  (sql/delete henkilo
    (sql/where {:henkiloid id})))

(defn hae-hlo-nimella
  "Hakee henkilöitä nimellä"
  [etunimi sukunimi]
  (->
    (sql/select henkilo
      (sql/where {:etunimi etunimi
                  :sukunimi sukunimi}))
    piilota-salaiset))

(defn hae-hlo-nimen-osalla
  "Hakee henkilöitä nimen osalla"
  [termi]
  (for [henkilo (sql/select henkilo
                  (sql/modifier "DISTINCT")
                  (sql/fields :etunimi :sukunimi)
                  (sql/order :etunimi)
                  (sql/order :sukunimi))
        :when (sisaltaako-kentat? henkilo [:etunimi :sukunimi] termi)]
    {:nimi (str (:etunimi henkilo) " " (:sukunimi henkilo))
     :osat henkilo}))

(defn hae-ehdoilla [ehdot]
  (let [henkilot (pg-hae-henkilot-ehdoilla ehdot)
        jasenyydet (pg-hae-jasenyydet-ehdoilla ehdot)
        yhdistetyt (yhdista-henkilot-ja-jasenyydet henkilot jasenyydet)]
    (if (or (:toimikausi ehdot)
            (:toimikunta ehdot))
      (remove #(empty? (:jasenyydet %)) yhdistetyt)
      yhdistetyt)))
