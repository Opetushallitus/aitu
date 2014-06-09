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

(ns aitu.infra.koulutusala-arkisto
  (:require [korma.core :as sql]
            korma.db
            [aitu.integraatio.sql.koulutusala :as kaytava]
            [aitu.toimiala.koulutusala :as domain])
  (:use [aitu.integraatio.sql.korma]))

(defn ^:test-api tyhjenna!
  "Tyhjentää arkiston."
  []
  (sql/exec-raw "delete from koulutusala"))

(defn ^:test-api poista!
  "Poistaa koulutusalan arkistosta."
  [koodi]
  (sql/delete koulutusala
    (sql/where {:koulutusala_tkkoodi koodi})))

(defn ^:integration-api lisaa!
  "Lisää koulutusalan arkistoon."
  [uusi-koulutusala]
  {:pre [(domain/koulutusala? uusi-koulutusala)]}
  (sql/insert koulutusala
    (sql/values uusi-koulutusala)))

(defn ^:integration-api paivita!
  [ala]
  {:pre [(domain/koulutusala? ala)]}
  (sql/update koulutusala
    (sql/set-fields (dissoc ala :koulutusala_tkkoodi))
    (sql/where {:koulutusala_tkkoodi (:koulutusala_tkkoodi ala)})))

(defn hae-kaikki
  "Hakee kaikki koulutusalat."
  []
  (sql/select koulutusala))

(defn hae
  "Hakee koulutusalan koodin perusteella"
  [koodi]
  (kaytava/hae koodi))
