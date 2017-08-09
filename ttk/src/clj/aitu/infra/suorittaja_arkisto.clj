;; Copyright (c) 2015 The Finnish National Board of Education - Opetushallitus
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

(ns aitu.infra.suorittaja-arkisto
  (:require [clojure.string :as s]
            [korma.core :as sql]
            [oph.korma.common :as sql-util]
            [aitu.auditlog :as auditlog]))

(defn hetu-kaytossa?
  "Onko hetu käytössä jollain toisella opiskelijalla?"
  [suorittajaid hetu]
  (if-not (s/blank? hetu)
    (pos? (count (sql/select :suorittaja
                   (sql/where (and (= :hetu hetu)
                                   (not= :suorittaja_id suorittajaid))))))
    false))

(defn hae
  [suorittajaid]
  (sql-util/select-unique :suorittaja
    (sql/join :kayttaja (= :kayttaja.oid :muutettu_kayttaja))
    (sql/join :left :rahoitusmuoto (= :rahoitusmuoto.rahoitusmuoto_id :rahoitusmuoto))
    (sql/fields :suorittaja_id :etunimi :sukunimi :hetu :oid :muutettuaika
                [:rahoitusmuoto.rahoitusmuoto :rahoitusmuoto_nimi]
                [:rahoitusmuoto.rahoitusmuoto_id :rahoitusmuoto_id]
                [(sql/sqlfn "concat" :kayttaja.etunimi " " :kayttaja.sukunimi) :muutettu_nimi])
    (sql/where {:suorittaja_id suorittajaid})))

(defn hae-kaikki
  []
  (sql/select :suorittaja
    (sql/join :kayttaja (= :kayttaja.oid :muutettu_kayttaja))
    (sql/join :left :rahoitusmuoto (= :rahoitusmuoto.rahoitusmuoto_id :rahoitusmuoto))
    (sql/fields :suorittaja_id :etunimi :sukunimi :hetu :oid :muutettuaika
                [:rahoitusmuoto.rahoitusmuoto :rahoitusmuoto_nimi]
                [:rahoitusmuoto.rahoitusmuoto_id :rahoitusmuoto_id]
                [(sql/sqlfn "concat" :kayttaja.etunimi " " :kayttaja.sukunimi) :muutettu_nimi])
    (sql/order :suorittaja_id :DESC)))

(defn lisaa!
  [form]
  (let [suorittaja (sql/insert :suorittaja
                     (sql/values (-> form
                                   (assoc :rahoitusmuoto (:rahoitusmuoto_id form))
                                   (select-keys  [:rahoitusmuoto :etunimi :sukunimi :hetu :oid]))))]
    (auditlog/suorittaja-operaatio! (:oid form) (:suorittaja_id suorittaja) :lisays (dissoc form :hetu :oid))
    suorittaja
    ))

(defn poista!
  [suorittajaid]
  (auditlog/suorittaja-operaatio! nil suorittajaid :poisto {:suorittajaid suorittajaid})
  (sql/delete :suorittaja
    (sql/where {:suorittaja_id suorittajaid})))

(defn tallenna!
  [suorittajaid suorittaja]
  (auditlog/suorittaja-operaatio! nil suorittajaid :paivitys (assoc suorittaja :suorittaja_id suorittajaid))
  (sql/update :suorittaja
    (sql/set-fields (-> suorittaja
                      (assoc :rahoitusmuoto (:rahoitusmuoto_id suorittaja))
                      (select-keys [:rahoitusmuoto :etunimi :sukunimi :hetu :oid])))
    (sql/where {:suorittaja_id suorittajaid})))
