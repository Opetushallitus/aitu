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
            [schema.core :as s]
            [aitu.toimiala.henkilo :refer :all]
            [aitu.toimiala.skeema :refer [HenkiloTaiTiedot Henkilo]]
            [oph.common.util.util :refer [sisaltaako-kentat?]]
            [oph.korma.common :as sql-util]
            [aitu.integraatio.sql.toimikunta :as toimikunta-kaytava]
            [aitu.auditlog :as auditlog]
            [clojure.string :refer [blank?]]
            [aitu.integraatio.sql.korma :refer :all]))

(defn ^:test-api tyhjenna!
  "Tyhjentää arkiston."
  []
  (sql/exec-raw "delete from jasenyys")
  (sql/exec-raw "delete from henkilo"))

(s/defn lisaa!
  "Lisää henkilön arkistoon."
  [uusi-henkilo :- HenkiloTaiTiedot]
  (let [luotu-henkilo (sql/insert henkilo
                        (sql/values uusi-henkilo))]
    (auditlog/henkilo-operaatio! :lisays (:henkiloid luotu-henkilo))
    luotu-henkilo))

(defn ^:private pg-hae-jasenyydet-ehdoilla
  [ehdot]
  (let [toimikunta (str "%" (:toimikunta ehdot) "%")]
    (->
      (sql/select* jasenyys)
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
        (sql/where (or (blank? (:toimikunta ehdot))
                       {:tutkintotoimikunta.nimi_fi [ilike toimikunta]}
                       {:tutkintotoimikunta.nimi_sv [ilike toimikunta]})))
      (cond->
        (some #{(:toimikausi ehdot)} ["nykyinen" "nykyinen_voimassa"]) (sql/where {:toimikausi.voimassa true})
        (= (:toimikausi ehdot) "nykyinen_voimassa") (sql/where (and (<= :jasenyys.alkupvm (sql/raw "current_date"))
                                                                    (<= (sql/raw "current_date") :jasenyys.loppupvm)))
        (= (:toimikausi ehdot) "tuleva") (sql/where (< (sql/raw "current_date") :toimikausi.alkupvm)))
      sql/exec)))

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
                         {:henkilo.sukunimi [ilike nimi]})))))))

(defn ^:private select-rajaa-jarjestolla [q jarjestoid]
  (sql/where q (or 
                 {:henkilo.jarjesto jarjestoid}
                 {:henkilo.jarjesto [in (sql/subselect :jarjesto
                                          (sql/fields :jarjestoid)
                                          (sql/where {:keskusjarjestoid jarjestoid}))]})))

(defn hae-jarjeston-esitetyt-henkilot
  [jarjestoid]  
  (sql/select :henkilo
    (sql/fields :henkiloid)    
    (select-rajaa-jarjestolla jarjestoid)
    (sql/where (sql/raw "(not exists (select 42 from jasenyys j where j.henkiloid = henkilo.henkiloid and j.status='nimitetty'))"))))
 

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

(defn hae-henkilot-toimikaudella
  "Hakee henkilöt, joilla on jäsenyys annetun toimikauden toimikunnassa"
  [toimikausi]
  (let [yhdistetyt-henkilot (yhdista-henkilot-ja-jasenyydet (pg-hae-henkilot-ehdoilla {})
                                                            (pg-hae-jasenyydet-ehdoilla {:toimikausi toimikausi}))]
    (remove (comp empty? :jasenyydet) yhdistetyt-henkilot)))

(defn hae-nykyiset
  "Hakee kaikki henkilöt nykyisen toimikauden toimikunnista"
  []
  (hae-henkilot-toimikaudella "nykyinen"))

(defn hae-nykyiset-voimassa
  "Hakee kaikki henkilöt joiden nykyisen toimikauden jäsenyys on voimassa"
  []
  (hae-henkilot-toimikaudella "nykyinen_voimassa"))

(defn hae-tulevat
  "Hakee kaikki henkilöt tulevien toimikausien toimikunnista"
  []
  (hae-henkilot-toimikaudella "tuleva"))

; TODO: unique? 
(defn hae
  "Hakee henkilön id:n perusteella"
  [id]
  (sql-util/select-unique-or-nil henkilo
      (sql/where {:henkiloid id})))

(defn ^:private eriyta-jarjesto
  "Eriyttää henkilön järjestötiedon omaan mappiin"
  [henkilo]
  (assoc henkilo :jarjesto (select-keys henkilo [:jarjesto :jarjesto_nimi_fi :jarjesto_nimi_sv])))

(defn hae-henkilo-jarjestolla
  [kayttajan-jarjesto]
  (->
    (sql/select* henkilo)
    (sql/with jarjesto
      (sql/fields [:nimi_fi :jarjesto_nimi_fi]
                  [:nimi_sv :jarjesto_nimi_sv]
                  [:keskusjarjestotieto :keskusjarjesto])
      (sql/with keskusjarjesto
        (sql/fields [:nimi_fi :keskusjarjesto_nimi])))
    (cond->
      (some? kayttajan-jarjesto) (select-rajaa-jarjestolla kayttajan-jarjesto))))

(defn hae-hlo-ja-ttk
  "Hakee henkilön ja toimikuntien jäsenyystiedot id:n perusteella"
  [id kayttajan-jarjesto]
  (some->
    (hae-henkilo-jarjestolla kayttajan-jarjesto)
    (sql/with jasenyys
      (sql/fields :alkupvm :loppupvm :muutettuaika :rooli :edustus :toimikunta :status))
    (sql/where {:henkiloid id})
    sql/exec
    first
    eriyta-jarjesto
    (update-in [:jasenyys] #(for [jasen %] (assoc jasen :ttk (toimikunta-kaytava/hae (:toimikunta jasen)))))))

(s/defn paivita!
  "Päivittää henkilön tiedot"
  [paivitettava-henkilo :- Henkilo]
  (auditlog/henkilo-operaatio! :paivitys (:henkiloid paivitettava-henkilo))
  (sql-util/update-unique henkilo
    (sql/set-fields paivitettava-henkilo)
    (sql/where {:henkiloid (:henkiloid paivitettava-henkilo)})))

(defn ^:test-api poista!
  "Poistaa henkilön tiedot"
  [id]
  (sql-util/delete-unique henkilo
    (sql/where {:henkiloid id})))

(defn hae-hlo-nimella
  "Hakee henkilöitä nimellä"
  [etunimi sukunimi kayttajan-jarjesto]
  (map eriyta-jarjesto
    (->
      (hae-henkilo-jarjestolla kayttajan-jarjesto)
      (sql/where {:etunimi etunimi
                  :sukunimi sukunimi})
      sql/exec)))

(defn hae-hlo-nimen-osalla
  "Hakee henkilöitä nimen osalla"
  [termi kayttajan-jarjesto]
  (for [henkilo (-> (sql/select* henkilo)
                    (sql/modifier "DISTINCT")
                    (sql/fields :etunimi :sukunimi)
                    (cond->
                      (some? kayttajan-jarjesto) (select-rajaa-jarjestolla kayttajan-jarjesto))
                    (sql/order :etunimi)
                    (sql/order :sukunimi)
                    sql/exec)
        :when (sisaltaako-kentat? henkilo [:etunimi :sukunimi] termi)]
    {:nimi (str (:etunimi henkilo) " " (:sukunimi henkilo))
     :osat henkilo}))

(defn hae-ehdoilla [ehdot]
  (let [henkilot (pg-hae-henkilot-ehdoilla ehdot)
        jasenyydet (pg-hae-jasenyydet-ehdoilla ehdot)
        yhdistetyt (yhdista-henkilot-ja-jasenyydet henkilot jasenyydet)
        rivi-per-jasenyys (mapcat (fn [h]
                                    (if (seq (:jasenyydet h))
                                      (for [j (:jasenyydet h)]
                                        (assoc h
                                               :toimikunta_fi (:nimi_fi j)
                                               :toimikunta_sv (:nimi_sv j)
                                               :rooli (:rooli j)
                                               :jasenyys_alku (:alkupvm j)
                                               :jasenyys_loppu (:loppupvm j)))
                                      [h]))
                                  yhdistetyt)
        karsitut-rivit (if (or (:toimikausi ehdot)
                               (:toimikunta ehdot))
                         (remove #(empty? (:jasenyydet %)) rivi-per-jasenyys)
                         rivi-per-jasenyys)]
    (if (:avaimet ehdot)
      (map #(select-keys % (:avaimet ehdot)) karsitut-rivit)
      karsitut-rivit)))
