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
            [clojure.tools.logging :as log]
            aitu.infra.eraajo.kayttajat)
  (:import aitu.infra.eraajo.kayttajat.PaivitaKayttajatLdapistaJob))


(defn kaynnista-ajastimet! [kayttooikeuspalvelu]
  (log/info "Käynnistetään ajastetut eräajot")
  (qs/initialize)
  (log/info "Poistetaan vanhat jobit ennen uudelleenkäynnistystä")
  (qs/clear!)
  (qs/start)
  (log/info "Eräajomoottori käynnistetty")
  (let [job (j/build
              (j/of-type PaivitaKayttajatLdapistaJob)
              (j/with-identity "paivita-kayttajat-ldapista")
              (j/using-job-data {"kayttooikeuspalvelu" kayttooikeuspalvelu}))
        trigger (t/build
                  (t/with-identity "5-min-valein")
                  (t/start-now)
                  (t/with-schedule (s/schedule
                                     (s/with-interval-in-minutes 5))))]
    (qs/schedule job trigger)))
