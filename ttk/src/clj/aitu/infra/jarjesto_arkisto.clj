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

(ns aitu.infra.jarjesto-arkisto
  (:require  korma.db
             [korma.core :as sql]
             [oph.common.util.util :refer [sisaltaako-kentat?]])
  (:use [aitu.integraatio.sql.korma]))

(defn hae-kaikki []
  (sql/select jarjesto
    (sql/order :nimi_fi)))

(defn hae-termilla [termi kayttajan-jarjesto]
  (let [kayttajan-keskusjarjestoid (sql/subselect jarjesto
                                     (sql/fields (sql/sqlfn "COALESCE" :keskusjarjestoid :jarjestoid))
                                     (sql/where {:jarjestoid kayttajan-jarjesto}))]
    (->
      (sql/select* :jarjesto)
      (sql/fields [:jarjestoid :jarjesto] [:nimi_fi :jarjesto_nimi_fi] [:nimi_sv :jarjesto_nimi_sv])
      (cond->
        termi (sql/where (or {:nimi_fi [ilike (str "%" termi "%")]}
                             {:nimi_sv [ilike (str "%" termi "%")]}))
        kayttajan-jarjesto (sql/where (or {:jarjestoid kayttajan-keskusjarjestoid}
                                          {:keskusjarjestoid kayttajan-keskusjarjestoid})))
      (sql/order :nimi_fi)
      sql/exec)))

(defn hae-keskusjarjestot []
  (sql/select jarjesto
    (sql/where {:keskusjarjestotieto true})))

(defn ^:test-api poista!
  [jarjestoid]
  (sql/delete jarjesto
    (sql/where {:jarjestoid jarjestoid})))

(defn ^:integration-api paivita!
  [data]
  (sql/update jarjesto
    (sql/set-fields (dissoc data :jarjestoid))
    (sql/where {:jarjestoid (:jarjestoid data)})))

(defn ^:integration-api lisaa!
  [uusi-jarjesto]
  (sql/insert jarjesto
    (sql/values uusi-jarjesto)))
