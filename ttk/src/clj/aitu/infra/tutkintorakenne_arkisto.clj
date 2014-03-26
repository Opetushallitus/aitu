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

(ns aitu.infra.tutkintorakenne-arkisto
  (:require korma.db
            [korma.core :as sql]
            [aitu.integraatio.sql.korma :refer :all]
            [clj-time.core :as ctime]))

(defn tutkinto-voimassa? [tutkinto]
  (ctime/after? (:siirtymaajan_loppupvm tutkinto) (ctime/today)))

(defn ^:private suodata-tyhjat-alat [koulutusalat]
  (->>
    (for [koulutusala koulutusalat]
      (update-in koulutusala [:opintoala] #(for [opintoala %
                                                 :when (seq (:nayttotutkinto opintoala))]
                                             (assoc opintoala :nayttotutkinto
                                               (filter tutkinto-voimassa? (:nayttotutkinto opintoala))))))
    (filter (comp seq :opintoala))))

(defn hae
  "Hakee tutkintorakenteen"
  []
  (suodata-tyhjat-alat
    (sql/select koulutusala
      (sql/with opintoala
        (sql/with nayttotutkinto
          (sql/with uusin-versio))))))
