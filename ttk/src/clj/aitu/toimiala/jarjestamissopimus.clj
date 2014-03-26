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

(ns aitu.toimiala.jarjestamissopimus
  (:require [aitu.toimiala.voimassaolo.jarjestamissopimus :as voimassaolo]
            [aitu.timeutil :as timeutil]))

(defn jarjestamissopimus? [x]
  "Onko järjestämissopimuksen tietosisältö oikeellinen?"
  (and (contains? x :oppilaitos)
       (contains? x :sopimusnumero)
       (contains? x :alkupvm)
       (contains? x :toimikunta)))

(defn liita-tutkinnon-nimi-ja-tunnus [sopimus-ja-tutkinto]
  (merge sopimus-ja-tutkinto (select-keys (:tutkintoversio sopimus-ja-tutkinto) [:nimi_fi :nimi_sv :tutkintotunnus :tutkintoversio_id])))

(defn taydenna-sopimus
  "Täydentää jarjestamissopimuksen tiedot, kuten voimassaolo"
  [sopimus]
  (some-> sopimus
    voimassaolo/taydenna-sopimuksen-ja-liittyvien-tietojen-voimassaolo
    (update-in [:sopimus_ja_tutkinto] #(map liita-tutkinnon-nimi-ja-tunnus %))))
