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

(ns aitu.toimiala.voimassaolo.oppilaitos
  (:require [aitu.toimiala.voimassaolo.jarjestamissopimus :as sopimus-paivitys]
            [aitu.toimiala.voimassaolo.saanto.oppilaitos :as oppilaitos]))

(defn ^:private taydenna-oppilaitoksen-jarjestamissopimukset
  [oppilaitos]
  (update-in oppilaitos [:jarjestamissopimus] #(mapv sopimus-paivitys/taydenna-sopimuksen-ja-liittyvien-tietojen-voimassaolo %)))

(defn taydenna-oppilaitoksen-ja-liittyvien-tietojen-voimassaolo
  "Täydentää oppilaitoksen ja siihen liittyvien tietojen voimassaolon"
  [oppilaitos]
  (some-> oppilaitos
          taydenna-oppilaitoksen-jarjestamissopimukset
          oppilaitos/taydenna-oppilaitoksen-voimassaolo))
