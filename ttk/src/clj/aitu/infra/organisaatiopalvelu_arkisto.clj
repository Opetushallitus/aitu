;; Copyright (c) 2015 The Finnish National Board of Education - Opetushallitus
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

(ns aitu.infra.organisaatiopalvelu-arkisto
  (:require [korma.core :as sql]
            [oph.korma.korma :refer :all])
  (:use [aitu.integraatio.sql.korma]))

(defn hae-viimeisin-paivitys
  []
  (:paivitetty
    (first
      (sql/select organisaatiopalvelu_log
        (sql/order :paivitetty :desc)
        (sql/limit 1)
        (sql/fields :paivitetty)))))

(defn ^:integration-api tallenna-paivitys!
  [ajankohta]
  (sql/insert organisaatiopalvelu_log
    (sql/values {:paivitetty ajankohta})))
