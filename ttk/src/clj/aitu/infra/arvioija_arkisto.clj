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

(ns aitu.infra.arvioija-arkisto
  (:require  [korma.core :as sql]
             [aitu.auditlog :as auditlog]
             [oph.korma.common :as sql-util]
             [aitu.integraatio.sql.korma :refer :all]))

(defn hae-nimella
  "Hakee arvioijia nimen perusteella."
  [nimi]
  (sql/select arvioija
    (sql/fields :nimi :rooli :nayttotutkintomestari)              
    (sql/where {:nimi [sql-util/ilike (str "%" nimi "%")]})))

(defn lisaa!
  "lisää uuden arvioijan"
  [uusi-arvioija]
  (auditlog/lisaa-arvioija! (:nimi uusi-arvioija))
   (sql/insert arvioija
     (sql/values uusi-arvioija)))
