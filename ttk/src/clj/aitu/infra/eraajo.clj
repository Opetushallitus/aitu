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
            aitu.infra.eraajo.organisaatiot
            aitu.infra.eraajo.sopimusten-voimassaolo
            aitu.infra.eraajo.eperusteet)
  (:import aitu.infra.eraajo.kayttajat.PaivitaKayttajatLdapistaJob
           aitu.infra.eraajo.organisaatiot.PaivitaOrganisaatiotJob
           aitu.infra.eraajo.sopimusten_voimassaolo.PaivitaSopimustenVoimassaoloJob
           aitu.infra.eraajo.eperusteet.PaivitaPerusteetJob))

(defn ^:integration-api kaynnista-ajastimet! [kayttooikeuspalvelu organisaatiopalvelu-asetukset]
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
        ldap-trigger-5min (t/build
                            (t/with-identity "5-min-valein")
                            (t/start-now)
                            (t/with-schedule (s/schedule
                                               (s/with-interval-in-minutes 5))))
        org-job (j/build
                  (j/of-type PaivitaOrganisaatiotJob)
                  (j/with-identity "paivita-organisaatiot")
                  (j/using-job-data {"asetukset" organisaatiopalvelu-asetukset}))
        org-trigger-daily (t/build
                            (t/with-identity "daily3")
                            (t/start-now)
                            (t/with-schedule (cron/schedule
                                               (cron/cron-schedule "0 0 3 * * ?"))))
        sopimus-job (j/build
                      (j/of-type PaivitaSopimustenVoimassaoloJob)
                      (j/with-identity "paivita-sopimusten-voimassaolo"))
        sopimus-trigger-daily (t/build
                                (t/with-identity "daily4")
                                (t/start-now)
                                (t/with-schedule (cron/schedule
                                                   (cron/cron-schedule "0 0 4 * * ?"))))
        perusteet-job (j/build
                        (j/of-type PaivitaSopimustenVoimassaoloJob)
                        (j/with-identity "paivita-sopimusten-voimassaolo"))
        perusteet-trigger-daily (t/build
                                  (t/with-identity "daily6")
                                  (t/start-now)
                                  (t/with-schedule (cron/schedule
                                                     (cron/cron-schedule "0 0 6 * * ?"))))]
    (qs/schedule ldap-job ldap-trigger-5min)
    (qs/schedule org-job org-trigger-daily)
    (qs/schedule sopimus-job sopimus-trigger-daily)
    (qs/schedule perusteet-job perusteet-trigger-daily)))
