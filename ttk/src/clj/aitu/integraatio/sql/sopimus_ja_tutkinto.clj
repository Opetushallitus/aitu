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

(ns aitu.integraatio.sql.sopimus-ja-tutkinto
  (:require korma.db
            [korma.core :as sql]
            [aitu.integraatio.sql.korma :refer :all]))

(defn hae-jarjestamissopimukseen-liittyvat-rivit
  "Hakee järjestämissopimukseen liittyvät sopimus-ja-tutkinto-taulun rivit"
  [jarjestamissopimusid]
  (vec
    (sql/select
      sopimus-ja-tutkinto
      (sql/with sopimus-ja-tutkinto-ja-tutkinnonosa
        (sql/with tutkinnonosa
          (sql/fields :osatunnus :nimi_fi :nimi_sv :tutkinnonosa_id)))
      (sql/with sopimus-ja-tutkinto-ja-osaamisala
        (sql/with osaamisala
          (sql/fields :osaamisalatunnus :nimi_fi :nimi_sv :osaamisala_id :tutkintoversio)))
      (sql/where {:jarjestamissopimusid jarjestamissopimusid
                  :poistettu false}))))

(defn hae-tutkintotunnukseen-liittyvat-rivit
  "Hakee tutkintotunnukseen liittyvät sopimus-ja-tutkinto-taulun rivit"
  [tutkintotunnus]
  (vec
    (sql/select
      sopimus-ja-tutkinto
      (sql/with tutkintoversio
        (sql/fields :tutkintotunnus))
      (sql/where {:tutkintoversio.tutkintotunnus tutkintotunnus
                  :poistettu false}))))
