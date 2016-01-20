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

(ns aitu.toimiala.toimikunta
  (:require [aitu.toimiala.henkilo :as henkilo]
            [aitu.toimiala.tutkinto :as tutkinto]
            [aitu.toimiala.jarjestamissopimus :as sopimus]
            [aitu.toimiala.voimassaolo.toimikunta :as voimassaolo]))

; TODO miksi some? milloin on hyvä että toimikunta on nil? 
(defn taydenna-toimikunta
  "Täydentää toimikunnan tiedot, kuten voimassaolo"
  [toimikunta]
  (-> toimikunta
    voimassaolo/taydenna-toimikunnan-ja-liittyvien-tietojen-voimassaolo
    (henkilo/piilota-salaiset :jasenyys)))
