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

(ns aitu.toimiala.voimassaolo.saanto.tutkinto
  (:require [clj-time.core :as time]
            [oph.common.util.util :as util]))

(defn tutkinto-voimassa?
  [tutkinto]
  (let [alkupvm (:voimassa_alkupvm tutkinto)
        siirtymaajan_loppupvm (:siirtymaajan_loppupvm tutkinto)]
    (and (util/pvm-mennyt-tai-tanaan? alkupvm) (util/pvm-tuleva-tai-tanaan? siirtymaajan_loppupvm))))

(defn taydenna-siirtymaajan-paattyminen
  [tutkinto]
  (let [{:keys [siirtymaajan_loppupvm]} tutkinto]
    (if (not (= siirtymaajan_loppupvm util/time-forever))
      (assoc tutkinto :siirtymaaika_paattyy siirtymaajan_loppupvm)
      tutkinto)))

(defn taydenna-tutkinnon-voimassaolo
  "Lisää tutkintoon voimassaolo-tieto"
  [tutkinto]
  (-> tutkinto
    taydenna-siirtymaajan-paattyminen
    (assoc :voimassa (tutkinto-voimassa? tutkinto))))
