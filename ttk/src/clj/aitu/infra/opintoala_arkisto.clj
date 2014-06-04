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
            [aitu.integraatio.sql.opintoala :as opintoala-kaytava]
            [aitu.toimiala.opintoala :as domain]
            [korma.core :as sql])
  (:use [aitu.integraatio.sql.korma]))

(defn ^:test-api tyhjenna!
  "Tyhjentää arkiston."
  []
  (sql/exec-raw "delete from opintoala"))

(defn ^:test-api poista!
  "Poistaa opintoalan arkistosta."
  [koodi]
  (sql/delete opintoala
    (sql/where {:opintoala_tkkoodi koodi})))

(defn lisaa!
  "Lisää opintoalan arkistoon."
  [uusi-opintoala]
  (if (domain/opintoala? uusi-opintoala)
    (sql/insert opintoala
      (sql/values uusi-opintoala))
    (do (println uusi-opintoala) (assert false))))

(defn paivita!
  [ala]
  #_{:pre [(domain/opintoala? ala)]}
  (when-not (domain/opintoala? ala)
    (println ala)
    (assert false))
  (sql/update opintoala
   (sql/set-fields (dissoc ala :opintoala_tkkoodi))
   (sql/where {:opintoala_tkkoodi (:opintoala_tkkoodi ala)})))

(defn hae-kaikki
  "Hakee kaikki opintoalat."
  []
  (sql/select opintoala))

(defn hae
  "Hakee opintoalan koodin perusteella"
  [koodi]
  (let [opintoala (opintoala-kaytava/hae koodi)
        koulutusala (koulutusala-arkisto/hae (:koulutusala_tkkoodi opintoala))]
    (some-> opintoala
            (assoc :koulutusala koulutusala))))
