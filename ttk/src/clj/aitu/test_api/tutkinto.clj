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

(ns aitu.test-api.tutkinto
  "Testien tarvitsemat REST-rajapinnat jos niitä on. Näitä ei tarvita normaalista käyttöliittymästä koskaan"
  (:require [compojure.core :as c]
            [clj-time.coerce :as time-coerce]
            [oph.common.util.http-util :refer [parse-iso-date]]
            [aitu.infra.tutkinto-arkisto :as tutkinto-arkisto]
            [korma.db :as db]))

(c/defroutes reitit
  (c/DELETE "/" []
    (db/transaction
      (tutkinto-arkisto/tyhjenna!)
      {:status 200}))
  (c/DELETE "/:tutkintotunnus" [tutkintotunnus]
    (db/transaction
      (tutkinto-arkisto/poista! tutkintotunnus)
      {:status 200}))
  (c/POST "/" [tutkintotunnus tutkintoversio_id versio koodistoversio nimi_fi nimi_sv opintoala tyyppi tutkintotaso peruste siirtymaajan_loppupvm voimassa_alkupvm voimassa_loppupvm uusin_versio_id]
    (db/transaction
      (tutkinto-arkisto/lisaa-tutkinto-ja-versio! (merge {:tutkintotunnus tutkintotunnus
                                                          :nimi_fi nimi_fi
                                                          :nimi_sv nimi_sv
                                                          :opintoala opintoala
                                                          :tyyppi tyyppi
                                                          :voimassa_alkupvm (parse-iso-date voimassa_alkupvm)
                                                          :voimassa_loppupvm (parse-iso-date voimassa_loppupvm)
                                                          :versio versio
                                                          :koodistoversio koodistoversio
                                                          :tutkintoversio_id tutkintoversio_id}
                                                         (when siirtymaajan_loppupvm
                                                           {:siirtymaajan_loppupvm (parse-iso-date siirtymaajan_loppupvm)})
                                                         (when peruste
                                                           {:peruste peruste})
                                                         (when uusin_versio_id
                                                           {:uusin_versio_id uusin_versio_id})
                                                         (when tutkintotaso
                                                           {:tutkintotaso tutkintotaso})))
      {:status 200}))
  (c/POST "/tutkinnonosa" [tutkintoversio osatunnus nimi jarjestysnumero]
    (db/transaction
      (tutkinto-arkisto/lisaa-tutkinnon-osa! tutkintoversio jarjestysnumero {:osatunnus osatunnus
                                                                             :nimi_fi nimi}))
    {:status 200})
  (c/POST "/osaamisala" [tutkintoversio osaamisalatunnus nimi]
    (db/transaction
      (tutkinto-arkisto/lisaa-osaamisala! {:osaamisalatunnus osaamisalatunnus
                                           :tutkintoversio tutkintoversio
                                           :nimi_fi nimi}))
    {:status 200})
  (c/DELETE "/tutkinnonosa/:osatunnus" [osatunnus]
    (db/transaction
      (tutkinto-arkisto/poista-tutkinnon-osa! osatunnus)
      {:status 200}))
  (c/DELETE "/osaamisala/:osaamisalatunnus" [osaamisalatunnus]
    (db/transaction
      (tutkinto-arkisto/poista-osaamisala! osaamisalatunnus)
      {:status 200})))

