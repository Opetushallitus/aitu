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

(ns aitu.toimiala.voimassaolo.saanto.toimikunta
  (:require [clj-time.core :as time]
            [oph.common.util.util :as util]))

(defn toimikunta-voimassa?
  "Onko toimikunta voimassa?"
  [toimikunta]
  (let [alkupvm (:toimikausi_alku toimikunta)
        loppupvm (:toimikausi_loppu toimikunta)]
    (and (util/pvm-mennyt-tai-tanaan? alkupvm) (util/pvm-tuleva-tai-tanaan? loppupvm))))

(defn toimikunta-vanhentunut?
  "Onko toimikunta vanhentunut? Toimikunta on vanhentunut jos loppupvm on menneisyydessä."
  [toimikunta]
  (let [loppupvm (:toimikausi_loppu toimikunta)]
    (not (util/pvm-tuleva-tai-tanaan? loppupvm))))

(defn taydenna-toimikunnan-voimassaolo
  "Lisää toimikuntaan voimassaolo-tieto"
  [toimikunta]
  (assoc toimikunta :voimassa (toimikunta-voimassa? toimikunta)
                    :vanhentunut (toimikunta-vanhentunut? toimikunta)))
