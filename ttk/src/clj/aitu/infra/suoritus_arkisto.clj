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

(ns aitu.infra.suoritus-arkisto
  (:require  [korma.core :as sql]
             [aitu.auditlog :as auditlog]))

(defn hae-kaikki
  []
  (sql/select :suorituskerta
    (sql/join :suorittaja (= :suorittaja.suorittaja_id :suorittaja))
    (sql/join :nayttotutkinto (= :nayttotutkinto.tutkintotunnus :tutkinto))
    (sql/join :koulutustoimija (= :koulutustoimija.ytunnus :koulutustoimija))
    (sql/fields :tutkinto :rahoitusmuoto :suorittaja :koulutustoimija :tila
                [:suorittaja.etunimi :suorittaja_etunimi]
                [:suorittaja.sukunimi :suorittaja_sukunimi]
                [:nayttotutkinto.nimi_fi :tutkinto_nimi_fi]
                [:nayttotutkinto.nimi_sv :tutkinto_nimi_sv]
                [:koulutustoimija.nimi_fi :koulutustoimija_nimi_fi]
                [:koulutustoimija.nimi_sv :koulutustoimija_nimi_sv])
    (sql/order :suorituskerta_id :DESC)))

(defn lisaa!
  [{:keys [suorittaja rahoitusmuoto tutkinto]
    :as suoritus}]
  (auditlog/suoritus-operaatio! :lisays suoritus)
  (sql/insert :suorituskerta
    (sql/values {:tutkinto tutkinto
                 :rahoitusmuoto rahoitusmuoto
                 :suorittaja suorittaja
                 :koulutustoimija "0177736-4"})))           ; FIXME

(defn ^:private paivita-tila!
  [suoritukset tila]
  (auditlog/suoritus-operaatio! :paivitys {:suoritukset suoritukset
                                           :tila tila})
  (sql/update :suorituskerta
    (sql/set-fields {:tila "ehdotettu"})
    (sql/where {:suorituskerta_id [in suoritukset]
                :koulutustoimija "0177736-4"})))            ; FIXME varmista että sama kuin käyttäjällä (on oikeus)

(defn laheta!
  [suoritukset]
  (paivita-tila! suoritukset "ehdotettu"))
