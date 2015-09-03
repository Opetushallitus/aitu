;; Copyright (c) 2015 The Finnish National Board of Education - Opetushallitus
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

(ns aitu.infra.jasenesitykset-arkisto
  (:require [korma.core :as sql]
            [aitu.integraatio.sql.korma :refer [ilike]]
            [oph.common.util.util :refer [sisaltaako-kentat?]]))

(defn ^:private rajaa-kentilla [query kentat teksti]
  (let [ehdot (for [kentta kentat]
                {kentta [ilike (str "%" teksti "%")]})]
    (sql/where query
      (apply or ehdot))))

(defn hae [{:keys [asiantuntijaksi ehdokas jarjesto tila toimikunta]}]
  (->
    (sql/select* :jasenyys)
    (sql/join :henkilo (= :henkilo.henkiloid :jasenyys.henkiloid))
    (sql/join :tutkintotoimikunta (= :tutkintotoimikunta.tkunta :jasenyys.toimikunta))
    (sql/join [:jarjesto :esittaja_jarjesto] (= :jasenyys.esittaja :esittaja_jarjesto.jarjestoid))
    (sql/fields :jasenyys.toimikunta :jasenyys.status :jasenyys.esittaja
                :henkilo.etunimi :henkilo.sukunimi
                [:tutkintotoimikunta.nimi_fi :tutkintotoimikunta_nimi_fi] [:tutkintotoimikunta.nimi_sv :tutkintotoimikunta_nimi_sv] [:tutkintotoimikunta.diaarinumero :tutkintotoimikunta_diaarinumero]
                [:esittaja_jarjesto.nimi_fi :esittaja_jarjesto_nimi_fi] [:esittaja_jarjesto.nimi_sv :esittaja_jarjesto_nimi_sv])
    (sql/where (not= :jasenyys.esittaja nil))
    (cond->
      (seq asiantuntijaksi) (sql/where {:jasenyys.asiantuntijaksi (= asiantuntijaksi "true")})
      (seq ehdokas) (rajaa-kentilla [:henkilo.etunimi :henkilo.sukunimi] ehdokas)
      (seq jarjesto) (rajaa-kentilla [:esittaja_jarjesto.nimi_fi :esittaja_jarjesto.nimi_sv] jarjesto)
      (seq tila) (sql/where {:jasenyys.status tila})
      (seq toimikunta) (rajaa-kentilla [:tutkintotoimikunta.nimi_fi :tutkintotoimikunta.nimi_sv] toimikunta))
    sql/exec))
