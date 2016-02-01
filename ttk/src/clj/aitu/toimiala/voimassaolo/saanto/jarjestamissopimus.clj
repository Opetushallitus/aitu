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

(ns aitu.toimiala.voimassaolo.saanto.jarjestamissopimus
  (:require [oph.common.util.util :as util]
            (aitu.toimiala.voimassaolo.saanto [toimikunta :as toimikunta]
                                              [tutkinto :as tutkinto])))

(defn sopimuksen-voimassaoloajalla?
  [jarjestamissopimus]
  (let [alkupvm (:alkupvm jarjestamissopimus)
        loppupvm (or (:loppupvm jarjestamissopimus) util/time-forever)]
    (and (util/pvm-mennyt-tai-tanaan? alkupvm) (util/pvm-tuleva-tai-tanaan? loppupvm))))

(defn sopimuksen-loppupvm-menneisyydessa?
  [jarjestamissopimus]
  (let [loppupvm (or (:loppupvm jarjestamissopimus) util/time-forever)]
    (not (util/pvm-tuleva-tai-tanaan? loppupvm))))

(defn joku-tutkinto-voimassa?
  [tutkinnot]
  (or (empty? tutkinnot)
      (not (every? false? (map tutkinto/tutkinto-voimassa? tutkinnot)))))

(defn onko-sopimus-voimassa-tai-vanhentunut?
  "Onko järjestämissopimus voimassa tai vanhentunut?"
  [jarjestamissopimus sopimuksen-voimassaolo-fn]
  (and
    (not (:poistettu jarjestamissopimus))
    (toimikunta/toimikunta-voimassa? (:toimikunta jarjestamissopimus))
    (sopimuksen-voimassaolo-fn jarjestamissopimus)
    (joku-tutkinto-voimassa? (map :tutkintoversio (:sopimus_ja_tutkinto jarjestamissopimus)))))

(defn sopimus-voimassa?
  "Onko järjestämissopimus voimassa?"
  [jarjestamissopimus]
  (onko-sopimus-voimassa-tai-vanhentunut?  jarjestamissopimus sopimuksen-voimassaoloajalla?))

(defn sopimus-vanhentunut?
  "Onko järjestämissopimus vanhentunut?"
  [jarjestamissopimus]
  (onko-sopimus-voimassa-tai-vanhentunut? jarjestamissopimus sopimuksen-loppupvm-menneisyydessa?))

(defn taydenna-sopimuksen-voimassaolo
  "Lisää järjestämissopimukseen voimassaolo-tieto."
  [jarjestamissopimus]
  (assoc jarjestamissopimus :voimassa (sopimus-voimassa? jarjestamissopimus)
                            :vanhentunut (sopimus-vanhentunut? jarjestamissopimus)))
