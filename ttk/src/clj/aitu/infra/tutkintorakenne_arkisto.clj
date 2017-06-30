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

(ns aitu.infra.tutkintorakenne-arkisto
  (:require korma.db
            [korma.core :as sql]
            [flatland.useful.seq :refer [groupings]]
            [aitu.integraatio.sql.korma :refer :all]
            [oph.common.util.util :refer [select-and-rename-keys]]
            [clj-time.core :as ctime]))

(defn tutkinto-voimassa? [tutkinto]
  (ctime/after? (:siirtymaajan_loppupvm tutkinto) (ctime/today)))

(defn ^:private suodata-tyhjat-alat [koulutusalat]
  (->>
    (for [koulutusala koulutusalat]
      (update-in koulutusala [:opintoala] #(for [opintoala %
                                                 :when (seq (:nayttotutkinto opintoala))]
                                             (assoc opintoala :nayttotutkinto
                                               (filter tutkinto-voimassa? (:nayttotutkinto opintoala))))))
    (filter (comp seq :opintoala))))

(defn hae
  "Hakee tutkintorakenteen"
  [peruste?]
  (let [tutkinnot (sql/select tutkintoversio
                    (sql/join :inner nayttotutkinto {:tutkintoversio.tutkintotunnus :nayttotutkinto.tutkintotunnus})
                    (sql/join :inner opintoala {:nayttotutkinto.opintoala :opintoala.opintoala_tkkoodi})
                    (sql/join :inner koulutusala {:opintoala.koulutusala_tkkoodi :koulutusala.koulutusala_tkkoodi})
                    (sql/where (or peruste? {:nayttotutkinto.uusin_versio_id :tutkintoversio.tutkintoversio_id}))
                    (sql/fields :nayttotutkinto.nimi_fi :nayttotutkinto.nimi_sv :tutkintoversio.peruste :nayttotutkinto.tutkintotunnus :tutkintoversio.voimassa_alkupvm :tutkintoversio.voimassa_loppupvm :tutkintoversio.siirtymaajan_loppupvm :tutkintoversio.tutkintoversio_id
                                :opintoala.selite_fi :opintoala.selite_sv :opintoala.opintoala_tkkoodi
                                [:koulutusala.selite_fi :koulutusala_selite_fi] [:koulutusala.selite_sv :koulutusala_selite_sv] :koulutusala.koulutusala_tkkoodi)
                    (sql/order :nayttotutkinto.nimi_fi)
                    (sql/order :tutkintoversio.peruste))
        opintoalat (sort-by :opintoala_tkkoodi (for [[opintoala tutkinnot] (groupings #(select-keys % [:selite_fi :selite_sv :opintoala_tkkoodi :koulutusala_selite_fi :koulutusala_selite_sv :koulutusala_tkkoodi])
                                                                                      #(select-keys % [:nimi_fi :nimi_sv :peruste :tutkintotunnus :voimassa_alkupvm :voimassa_loppupvm :siirtymaajan_loppupvm :tutkintoversio_id])
                                                                                      tutkinnot)]
                                                 (assoc opintoala :nayttotutkinto tutkinnot)))
        koulutusalat (sort-by :koulutusala_tkkoodi (for [[koulutusala opintoalat] (groupings #(select-and-rename-keys % [[:koulutusala_selite_fi :selite_fi] [:koulutusala_selite_sv :selite_sv] :koulutusala_tkkoodi])
                                                                                             #(select-keys % [:selite_fi :selite_sv :opintoala_tkkoodi :nayttotutkinto])
                                                                                             opintoalat)]
                                                     (assoc koulutusala :opintoala opintoalat)))]
    (suodata-tyhjat-alat koulutusalat)))