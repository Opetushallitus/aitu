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

(ns aitu.infra.tiedote-arkisto
  (:require  [korma.core :as sql]
             [aitu.auditlog :as auditlog]
             [oph.korma.common :as sql-util])
  (:use [aitu.integraatio.sql.korma]))

(defn hae
  "Hakee tiedotteen."
  [tiedoteid]
  (sql-util/select-unique-or-nil tiedote
    (sql/where {:tiedoteid tiedoteid})))

(defn poista!
  "Poistaa tiedotteen."
  [tiedoteid]
  (auditlog/tiedote-operaatio! :poisto tiedoteid)
  (sql-util/delete-unique tiedote
    (sql/where {:tiedoteid tiedoteid})))

(defn poista-ja-lisaa!
  "Poistaa vanhan tiedotteen ja lisää uuden"
  [tiedoteid teksti]
  (auditlog/tiedote-operaatio! :lisays tiedoteid)
  (poista! tiedoteid)
  (sql/insert tiedote
    (sql/values (assoc teksti :tiedoteid tiedoteid))))
