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

(ns aitu.toimiala.tutkinto
  (:require [aitu.toimiala.voimassaolo.tutkinto :as voimassaolo]))

(defn tutkinto? [x]
  (and (contains? x :tutkintotunnus)
       (or (contains? x :nimi_fi) (contains? x :nimi_sv))
       (contains? x :opintoala)))

(defn tutkintoversio? [x]
  (and (contains? x :tutkintotunnus)
       (contains? x :versio)
       (contains? x :voimassa_alkupvm)
       (contains? x :voimassa_loppupvm)))

(defn taydenna-tutkinto
  "Täydentää tutkinnon tiedot, kuten voimassaolo"
  [tutkinto]
  (voimassaolo/taydenna-tutkinnon-ja-liittyvien-tietojen-voimassaolo tutkinto))
