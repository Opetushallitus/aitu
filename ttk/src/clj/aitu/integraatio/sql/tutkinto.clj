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

(ns aitu.integraatio.sql.tutkinto
  (:require korma.db
            [korma.core :as sql]
            [aitu.integraatio.sql.korma :refer :all]))

(defn hae
  "Hae näyttötutkinto tutkintotunnuksen perusteella"
  [tutkintotunnus]
  (first
    (sql/select
      nayttotutkinto
      (sql/where {:tutkintotunnus tutkintotunnus}))))

(defn hae-tutkintoversio
  "Hae tutkintoversio tutkintoversio-id:n perusteella"
  [tutkintoversio_id]
  (first
   (sql/select
     tutkintoversio
     (sql/with nayttotutkinto
       (sql/fields :opintoala :nimi_fi :nimi_sv :tyyppi :tutkintotaso :uusin_versio_id))
     (sql/where {:tutkintoversio_id tutkintoversio_id}))))

(defn hae-tutkintoversio-ja-tutkinnonosat
  "Hakee tutkintoversion ja siihen liittyvät tutkinnonosat ja osaamisalat"
  [tutkintoversio_id]
  (first
    (sql/select tutkintoversio
      (sql/with nayttotutkinto)
      (sql/with tutkinto-ja-tutkinnonosa
        (sql/with tutkinnonosa))
      (sql/with osaamisala)
      (sql/where {:tutkintoversio_id tutkintoversio_id}))))
