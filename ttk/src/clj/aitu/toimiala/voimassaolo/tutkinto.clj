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

(ns aitu.toimiala.voimassaolo.tutkinto
  (:require [aitu.toimiala.voimassaolo.jarjestamissopimus :as sopimus-paivitys]
            [aitu.toimiala.voimassaolo.saanto.tutkinto :as tutkinto]))

(defn taydenna-sopimus-ja-tutkinto-rivi
  [sopimus-ja-tutkinto]
  (update-in sopimus-ja-tutkinto [:jarjestamissopimus] sopimus-paivitys/taydenna-sopimukseen-liittyvien-tietojen-voimassaolo))

(defn taydenna-tutkinnon-sopimus-ja-tutkinto-rivit
  [tutkinto]
  (update-in tutkinto [:sopimus_ja_tutkinto] #(mapv taydenna-sopimus-ja-tutkinto-rivi %)))

(defn taydenna-tutkinnon-ja-liittyvien-tietojen-voimassaolo
  "Täydentää tutkinnon ja siihen liittyvien tietojen voimassaolon"
  [tutkinto]
  (some-> tutkinto
          tutkinto/taydenna-tutkinnon-voimassaolo
          taydenna-tutkinnon-sopimus-ja-tutkinto-rivit))
