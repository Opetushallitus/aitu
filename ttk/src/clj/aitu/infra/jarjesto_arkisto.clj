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

(defn hae-termilla [termi]
  (for [jarjesto (hae-kaikki)
        :when (sisaltaako-kentat? jarjesto [:nimi_fi :nimi_sv] termi)]
    {:jarjesto (:jarjestoid jarjesto)
     :jarjesto_nimi_fi (str (:nimi_fi jarjesto) (when (:keskusjarjestotieto jarjesto) " (*)"))
     :jarjesto_nimi_sv (str (:nimi_sv jarjesto) (when (:keskusjarjestotieto jarjesto) " (*)"))}))

(defn ^:test-api poista!
  [jarjestoid]
  (sql/delete jarjesto
    (sql/where {:jarjestoid jarjestoid})))

(defn ^:test-api lisaa!
  [uusi-jarjesto]
  (sql/insert jarjesto
    (sql/values uusi-jarjesto)))
