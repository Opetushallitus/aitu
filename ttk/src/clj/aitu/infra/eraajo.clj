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

(ns aitu.infra.eraajo
  "Säännöllisin väliajoin suoritettavat toiminnot."
  (:require [clojurewerkz.quartzite.scheduler :as qs]
            [clojurewerkz.quartzite.jobs :as j]
            [clojurewerkz.quartzite.triggers :as t]
            [clojurewerkz.quartzite.schedule.daily-interval :as s]
            [clojurewerkz.quartzite.schedule.cron :as cron]
            [clojure.tools.logging :as log]
            [clj-time.core :as time :refer [minutes seconds from-now]]
            aitu.infra.eraajo.kayttajat
            aitu.infra.eraajo.organisaatiot)
  (:import aitu.infra.eraajo.kayttajat.PaivitaKayttajatLdapistaJob
           aitu.infra.eraajo.organisaatiot.PaivitaOrganisaatiotJob))

(defn ^:private seuraava-kellonaika 
  "Palauttaa seuraavan ajanhetken jolloin kello on annetun verran"
  [h m s]
  (let [hetki-tanaan (-> 
                       (time/now)
                       (time/to-time-zone (time/time-zone-for-id "Europe/Helsinki"))
                       (.withHourOfDay h)
                       (.withMinuteOfHour m)
                       (.withSecondOfMinute s)
                       (.withMillisOfSecond 0))
        nyt (time/now)]
    (if (time/after? hetki-tanaan nyt)
      hetki-tanaan
      (time/plus hetki-tanaan (time/days 1)))))

(defn kaynnista-ajastimet! [kayttooikeuspalvelu organisaatiopalvelu-asetukset]
  (log/info "Käynnistetään ajastetut eräajot")
  (qs/initialize)
  (log/info "Poistetaan vanhat jobit ennen uudelleenkäynnistystä")
  (qs/clear!)
  (qs/start)
  (log/info "Eräajomoottori käynnistetty")
  (let [ldap-job (j/build
                   (j/of-type PaivitaKayttajatLdapistaJob)
                   (j/with-identity "paivita-kayttajat-ldapista")
                   (j/using-job-data {"kayttooikeuspalvelu" kayttooikeuspalvelu}))
        trigger-5min (t/build
                       (t/with-identity "5-min-valein")
                       (t/start-now)
                       (t/with-schedule (s/schedule
                                          (s/with-interval-in-minutes 5))))
        org-job (j/build
                  (j/of-type PaivitaOrganisaatiotJob)
                  (j/with-identity "paivita-organisaatiot")
                  (j/using-job-data {"asetukset" organisaatiopalvelu-asetukset}))
        trigger-daily (t/build
                        (t/with-identity "daily")
                        (t/start-at (seuraava-kellonaika 3 0 0))
                        (t/with-schedule (s/schedule
                                            (s/with-interval-in-days 1))))]
    (qs/schedule ldap-job trigger-5min)
    (qs/schedule org-job trigger-daily)))
