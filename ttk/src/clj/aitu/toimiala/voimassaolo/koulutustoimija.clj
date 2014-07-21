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

(ns aitu.toimiala.voimassaolo.koulutustoimija
  (:require [aitu.toimiala.voimassaolo.jarjestamissopimus :as sopimus-paivitys]))

(defn ^:private taydenna-koulutustoimijan-jarjestamissopimukset
  [koulutustoimija]
  (update-in koulutustoimija [:jarjestamissopimus] #(mapv sopimus-paivitys/taydenna-sopimuksen-ja-liittyvien-tietojen-voimassaolo %)))

(defn taydenna-koulutustoimijan-ja-liittyvien-tietojen-voimassaolo
  "Täydentää koulutustoimijan ja siihen liittyvien tietojen voimassaolon"
  [koulutustoimija]
  (some-> koulutustoimija
    taydenna-koulutustoimijan-jarjestamissopimukset))
