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

(ns aitu.infra.opintoala-arkisto
  (:require [aitu.infra.koulutusala-arkisto :as koulutusala-arkisto]
            [oph.korma.common :as sql-util]
            [aitu.toimiala.opintoala :as domain]
            [korma.core :as sql]
            [aitu.integraatio.sql.korma :refer :all]))

(defn ^:test-api tyhjenna!
  "Tyhjentää arkiston."
  []
  (sql/exec-raw "delete from opintoala"))

(defn ^:test-api poista!
  "Poistaa opintoalan arkistosta."
  [koodi]
  (sql/delete opintoala
    (sql/where {:opintoala_tkkoodi koodi})))

(defn ^:integration-api lisaa!
  "Lisää opintoalan arkistoon."
  [uusi-opintoala]
  {:pre [(domain/opintoala? uusi-opintoala)]}
  (sql/insert opintoala
    (sql/values uusi-opintoala)))

(defn ^:integration-api paivita!
  [ala]
;  {:pre [(domain/opintoala? ala)]}
  (if (domain/opintoala? ala)
    (sql/update opintoala
     (sql/set-fields (dissoc ala :opintoala_tkkoodi))
     (sql/where {:opintoala_tkkoodi (:opintoala_tkkoodi ala)}))
    (clojure.pprint/pprint ala)))

(defn hae-voimassaolevat
  "Hakee voimassaolevat opintoalat"
  []
  (sql/select opintoala
              (sql/where {:voimassa_loppupvm [>= (sql/sqlfn now)]})))

(defn hae-kaikki
  "Hakee kaikki opintoalat."
  []
  (sql/select opintoala))

(defn ^:private hae-opintoala
  "Hakee opintoala-taulun rivin koodin perusteella"
  [koodi]
  (sql-util/select-unique-or-nil opintoala
    (sql/where {:opintoala_tkkoodi koodi})))

(defn hae
  "Hakee opintoalan koodin perusteella"
  [koodi]
  (let [opintoala (hae-opintoala koodi)
        koulutusala (koulutusala-arkisto/hae (:koulutusala_tkkoodi opintoala))]
    (some-> opintoala
      (assoc :koulutusala koulutusala))))

(defn hae-termilla
  "Hakee opintoalat joiden nimestä löytyy annettu termi"
  [termi]
  (let [nimi (str "%" termi "%")]
    (sql/select opintoala
      (sql/where (and
                   {:voimassa_loppupvm [>= (sql/sqlfn now)]}
                   (or {:selite_fi [ilike nimi]}
                       {:selite_sv [ilike nimi]})))
      (sql/fields :opintoala_tkkoodi :selite_fi :selite_sv))))
