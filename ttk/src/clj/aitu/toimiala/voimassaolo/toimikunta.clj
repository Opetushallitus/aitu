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

(ns aitu.toimiala.voimassaolo.toimikunta
  (:require [clj-time.core :as time]
            [aitu.timeutil :as timeutil]
            [aitu.toimiala.voimassaolo.jarjestamissopimus :as sopimus-paivitys]
            [aitu.toimiala.voimassaolo.saanto.toimikunta :as toimikunta]
            [aitu.toimiala.voimassaolo.saanto.jasenyys :as jasenyys-saanto]))

(defn taydenna-toimikunnan-jarjestamissopimukset
  [toimikunta]
  (update-in toimikunta [:jarjestamissopimus] #(mapv sopimus-paivitys/taydenna-sopimuksen-ja-liittyvien-tietojen-voimassaolo %)))

(defn taydenna-jasenyyksien-voimassaolo
  [jasenyydet toimikunta-voimassa]
  (map #(jasenyys-saanto/taydenna-jasenyyden-voimassaolo % toimikunta-voimassa) jasenyydet))

(defn taydenna-toimikunnan-jasenyyksien-voimassaolo
  [toimikunta]
  (update-in toimikunta [:jasenyys] #(taydenna-jasenyyksien-voimassaolo % (:voimassa toimikunta))))

(defn taydenna-toimikunnan-ja-liittyvien-tietojen-voimassaolo
  "Täydentää toimikunnan ja siihen liittyvien tietojen voimassaolon"
  [toimikunta]
  (some-> toimikunta
          toimikunta/taydenna-toimikunnan-voimassaolo
          taydenna-toimikunnan-jarjestamissopimukset
          taydenna-toimikunnan-jasenyyksien-voimassaolo))
