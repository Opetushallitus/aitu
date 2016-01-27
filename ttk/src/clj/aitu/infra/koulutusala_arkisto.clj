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
            [oph.korma.common :as sql-util]
            [aitu.toimiala.koulutusala :as domain]
            [aitu.integraatio.sql.korma :refer :all]))

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

(defn hae-koulutusalat-ja-opintoalat
  "Hakee koulutusalat ja niiden opintoalat"
  []
  (let [opintoalat (sql/select opintoala
                     (sql/join :inner :koulutusala (= :opintoala.koulutusala_tkkoodi :koulutusala.koulutusala_tkkoodi))
                     (sql/fields :opintoala_tkkoodi :opintoala.koulutusala_tkkoodi
                                 [:opintoala.selite_fi :opintoala_nimi_fi] [:opintoala.selite_sv :opintoala_nimi_sv]
                                 [:koulutusala.selite_fi :koulutusala_nimi_fi] [:koulutusala.selite_sv :koulutusala_nimi_sv])
                     (sql/where {:voimassa_alkupvm [<= (sql/raw "current_date")]
                                 :voimassa_loppupvm [>= (sql/raw "current_date")]}))
        opintoalat-koulutusaloittain (group-by #(select-keys % [:koulutusala_tkkoodi :koulutusala_nimi_fi :koulutusala_nimi_sv]) opintoalat)]
    (sort-by :koulutusala_tkkoodi (for [[koulutusala opintoalat] opintoalat-koulutusaloittain
                                        :let [opintoalat (map #(select-keys % [:opintoala_tkkoodi :opintoala_nimi_fi :opintoala_nimi_sv]) opintoalat)]]
                                    (assoc koulutusala :opintoalat opintoalat)))))

(defn hae
  "Hakee koulutusala-taulun rivin koodin perusteella"
  [koodi]
  (sql-util/select-unique-or-nil koulutusala
    (sql/where {:koulutusala_tkkoodi koodi})))
 