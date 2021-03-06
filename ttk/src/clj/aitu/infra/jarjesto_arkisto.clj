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
             [oph.korma.common :as sql-util]
             [oph.common.util.util :refer [sisaltaako-kentat?]]
             [oph.korma.common :refer [rajaa-kentilla]]
             [aitu.integraatio.sql.korma :refer :all]))

(defn hae-kaikki []
  (sql/select jarjesto
    (sql/order :nimi_fi)))

(defn hae-termilla [termi kayttajan-jarjesto]
  (->
    (sql/select* :jarjesto)
    (sql/fields [:jarjestoid :jarjesto] [:nimi_fi :jarjesto_nimi_fi] [:nimi_sv :jarjesto_nimi_sv])
    (cond->
      termi (rajaa-kentilla [:nimi_fi :nimi_sv] termi)
      kayttajan-jarjesto (sql/where (or {:jarjestoid kayttajan-jarjesto}
                                        {:keskusjarjestoid kayttajan-jarjesto})))
    (sql/order :nimi_fi)
    sql/exec))

(defn hae-keskusjarjestot []
  (sql/select jarjesto
    (sql/where {:keskusjarjestotieto true})))

(defn ^:test-api poista!
  [jarjestoid]
  (sql-util/delete-unique jarjesto
    (sql/where {:jarjestoid jarjestoid})))

(defn ^:integration-api paivita!
  [data]
  (sql-util/update-unique jarjesto
    (sql/set-fields (dissoc data :jarjestoid))
    (sql/where {:jarjestoid (:jarjestoid data)})))

(defn ^:integration-api lisaa!
  [uusi-jarjesto]
  (sql/insert jarjesto
    (sql/values uusi-jarjesto)))
