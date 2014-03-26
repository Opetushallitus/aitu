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

(ns aitu.infra.status
  "Tietoa järjestelmän tilasta."
  (:require [korma.core :as sql]))

(defn tietokantaversio []
  (try
    ;; Odotetaan vastausta 1000 ms
    (deref
      (future
        (-> (sql/exec-raw (str "select script from schema_version "
                              "where version_rank = "
                              "(select max(version_rank) from schema_version);")
                         :results)
          first
          :script))
      1000
      nil)
    (catch Throwable _
      nil)))

(defn status []
  {:tietokantaversio (tietokantaversio)})
