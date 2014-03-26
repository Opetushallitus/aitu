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

(ns aitu.infra.peruste-arkisto
  (:require [korma.core :as sql])
  (:use [aitu.integraatio.sql.korma]))

(defn ^:test-api lisaa!
  "Lisää tutkinnon perusteen arkistoon."
  [uusi-peruste]
  (sql/insert peruste
    (sql/values uusi-peruste)))

(defn ^:test-api poista!
  "Poistaa tutkinnon perusteen arkistosta."
  [poistettava-peruste]
  (sql/delete peruste
    (sql/where (select-keys poistettava-peruste [:diaarinumero]))))
