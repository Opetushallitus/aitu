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
            aitu.infra.eraajo.eperusteet
            aitu.infra.eraajo.tutkinnot)
  (:import aitu.infra.eraajo.kayttajat.PaivitaKayttajatLdapistaJob
           aitu.infra.eraajo.organisaatiot.PaivitaOrganisaatiotJob
           aitu.infra.eraajo.sopimusten_voimassaolo.PaivitaSopimustenVoimassaoloJob
           aitu.infra.eraajo.eperusteet.PaivitaPerusteetJob
           aitu.infra.eraajo.tutkinnot.PaivitaTutkinnotJob))

(defn ajastus [asetukset tyyppi]
  (cron/schedule
    (cron/cron-schedule (get-in asetukset [:ajastus tyyppi]))))

(def ajastin (promise))

(defn ^:integration-api kaynnista-ajastimet!
  [kayttooikeuspalvelu asetukset]
  (log/info "Käynnistetään ajastetut eräajot")
  (when-not (realized? ajastin)
    (deliver ajastin (qs/initialize)))
  (log/info "Poistetaan vanhat jobit ennen uudelleenkäynnistystä")
  (qs/clear! @ajastin)
  (qs/start @ajastin)
  (log/info "Eräajomoottori käynnistetty")
  (let [ldap-job (j/build
                   (j/of-type PaivitaKayttajatLdapistaJob)
                   (j/with-identity "paivita-kayttajat-ldapista")
                   (j/using-job-data {"kayttooikeuspalvelu" kayttooikeuspalvelu}))
        ldap-trigger (t/build
                       (t/with-identity "ldap")
                       (t/start-now)
                       (t/with-schedule (ajastus asetukset :kayttooikeuspalvelu)))
        org-job (j/build
                  (j/of-type PaivitaOrganisaatiotJob)
                  (j/with-identity "paivita-organisaatiot")
                  (j/using-job-data {"asetukset" (:organisaatiopalvelu asetukset)}))
        org-trigger (t/build
                      (t/with-identity "organisaatio")
                      (t/start-now)
                      (t/with-schedule (ajastus asetukset :organisaatiopalvelu)))
        sopimus-job (j/build
                      (j/of-type PaivitaSopimustenVoimassaoloJob)
                      (j/with-identity "paivita-sopimusten-voimassaolo"))
        sopimus-trigger (t/build
                          (t/with-identity "sopimus")
                          (t/start-now)
                          (t/with-schedule (ajastus asetukset :sopimusten-voimassaolo)))
        tutkinnot-job (j/build
                        (j/of-type PaivitaTutkinnotJob)
                        (j/using-job-data {"asetukset" (:koodistopalvelu asetukset)})
                        (j/with-identity "paivita-tutkinnot"))
        tutkinnot-trigger (t/build
                            (t/with-identity "tutkinnot")
                            (t/start-now)
                            (t/with-schedule (ajastus asetukset :tutkinnot)))
        perusteet-job (j/build
                        (j/of-type PaivitaPerusteetJob)
                        (j/using-job-data {"asetukset" (:eperusteet-palvelu asetukset)})
                        (j/with-identity "paivita-perusteet"))
        perusteet-trigger (t/build
                            (t/with-identity "perusteet")
                            (t/start-now)
                            (t/with-schedule (ajastus asetukset :eperusteet)))]
    (qs/schedule @ajastin ldap-job ldap-trigger)
    (qs/schedule @ajastin org-job org-trigger)
    (qs/schedule @ajastin sopimus-job sopimus-trigger)
    (qs/schedule @ajastin tutkinnot-job tutkinnot-trigger)
    (qs/schedule @ajastin perusteet-job perusteet-trigger)))
