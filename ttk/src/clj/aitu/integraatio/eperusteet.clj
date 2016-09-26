;; Copyright (c) 2015 The Finnish National Board of Education - Opetushallitus
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

(ns aitu.integraatio.eperusteet
  (:require [clj-time.coerce :as c]
            [clj-time.core :as time]
            [oph.common.util.util :refer :all])
  (:import org.joda.time.DateTimeZone))

(defn lataa-kaikki-sivut [url options]
  (loop [vanha-data []
         sivu 0]
    (let [{:keys [data sivuja]} (get-json-from-url url (assoc-in options [:query-params :sivu] sivu))
          data (concat vanha-data data)]
      (if (>= (inc sivu) sivuja)
        data
        (recur data (inc sivu))))))

(defn ^:private to-local-date
  [date]
  (when-let [dt (c/to-date-time date)]
    (c/to-local-date (time/to-time-zone dt (DateTimeZone/forID "Europe/Helsinki")))))

(defn muotoile-peruste [peruste]
  {:diaarinumero (:diaarinumero peruste)
   :eperustetunnus (:id peruste)
   :voimassa_alkupvm (to-local-date (:voimassaoloAlkaa peruste))
   :voimassa_loppupvm (or (to-local-date (:voimassaoloLoppuu peruste)) (time/local-date 2199 1 1))
   :siirtymaajan_loppupvm (or (to-local-date (:siirtymaPaattyy peruste)) (time/local-date 2199 1 1))
   :tutkinnot (map :koulutuskoodiArvo (:koulutukset peruste))})

(defn hae-perusteet [viimeisin-haku asetukset]
  (for [peruste (map muotoile-peruste (lataa-kaikki-sivut (str (:url asetukset) "api/perusteet") {:query-params {:muokattu (c/to-long viimeisin-haku)}}))
        tutkintotunnus (:tutkinnot peruste)]
    [tutkintotunnus (dissoc peruste :tutkinnot)]))
