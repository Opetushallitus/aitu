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

(ns aitu.infra.enum-arkisto
  (:require  korma.db
             [korma.core :as sql]
             [clojure.set :refer [subset?]])
  (:use [aitu.integraatio.sql.korma]))

(defn hae-kaikki
  "Hakee kaikki nimet enum taulusta."
  [enum]
  {:pre [(subset? #{enum} #{"edustus" "alue" "rooli" "kieli" "sukupuoli"})]}
  (sql/exec-raw [(str "select nimi from " enum)] :results))
