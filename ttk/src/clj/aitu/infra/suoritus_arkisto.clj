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
             [aitu.auditlog :as auditlog]
             [clj-time.coerce :refer [to-sql-date]]
             [oph.korma.common :as sql-util]
             [oph.common.util.http-util :refer [parse-iso-date]]))

(defn hae
  [suorituskerta-id]
  (sql-util/select-unique :suorituskerta
    (sql/where {:suorituskerta_id suorituskerta-id})))


(defn hae-suoritukset [suorituskerta-id]
  (sql/select :suoritus
    (sql/join :tutkinnonosa (= :tutkinnonosa.tutkinnonosa_id :tutkinnonosa))
    (sql/fields :suoritus_id :arvosana :suorituskerta :tutkinnonosa :arvosanan_korotus :osaamisen_tunnustaminen :kieli :todistus :osaamisala
                [:tutkinnonosa.osatunnus :osatunnus]
                [:tutkinnonosa.nimi_fi :nimi]
 ; TODO: nimi_fi ei ole oikeasti hyvä juttu välttämättä..
                )
     (sql/where {:suorituskerta suorituskerta-id})))

(defn hae-kaikki
  [{:keys [ehdotuspvm_alku ehdotuspvm_loppu hyvaksymispvm_alku hyvaksymispvm_loppu jarjestamismuoto koulutustoimija rahoitusmuoto tila tutkinto suorituskertaid]}]
  (->
    (sql/select* :suorituskerta)
    (sql/join :suorittaja (= :suorittaja.suorittaja_id :suorittaja))
    (sql/join :nayttotutkinto (= :nayttotutkinto.tutkintotunnus :tutkinto))
    (sql/join :koulutustoimija (= :koulutustoimija.ytunnus :koulutustoimija))
    (sql/fields :suorituskerta_id :tutkinto :rahoitusmuoto :suorittaja :koulutustoimija :tila :ehdotusaika :hyvaksymisaika
                :jarjestamismuoto :opiskelijavuosi
                [:suorittaja.etunimi :suorittaja_etunimi]
                [:suorittaja.sukunimi :suorittaja_sukunimi]
                [:nayttotutkinto.nimi_fi :tutkinto_nimi_fi]
                [:nayttotutkinto.nimi_sv :tutkinto_nimi_sv]
                [:koulutustoimija.nimi_fi :koulutustoimija_nimi_fi]
                [:koulutustoimija.nimi_sv :koulutustoimija_nimi_sv])
    (sql/order :suorituskerta_id :DESC)
    (cond->
      (seq suorituskertaid) (sql/where {:suorituskerta.suorituskerta_id (Integer/parseInt suorituskertaid)})
      (seq ehdotuspvm_alku) (sql/where {:ehdotusaika [>= (to-sql-date (parse-iso-date ehdotuspvm_alku))]})
      (seq ehdotuspvm_loppu) (sql/where {:ehdotusaika [<= (to-sql-date (parse-iso-date ehdotuspvm_loppu))]})
      (seq hyvaksymispvm_alku) (sql/where {:hyvaksymisaika [>= (to-sql-date (parse-iso-date hyvaksymispvm_alku))]})
      (seq hyvaksymispvm_loppu) (sql/where {:hyvaksymisaika [<= (to-sql-date (parse-iso-date hyvaksymispvm_loppu))]})
      (seq jarjestamismuoto) (sql/where {:jarjestamismuoto jarjestamismuoto})
      (seq koulutustoimija) (sql/where {:koulutustoimija koulutustoimija})
      (seq rahoitusmuoto) (sql/where {:rahoitusmuoto (Integer/parseInt rahoitusmuoto)})
      (seq tila) (sql/where {:tila tila})
      (seq tutkinto) (sql/where {:tutkinto tutkinto}))
    sql/exec))

(defn hae-tiedot [suorituskerta-id]
  (let [perus (hae-kaikki {:suorituskertaid suorituskerta-id})
        suoritukset (hae-suoritukset (Integer/parseInt suorituskerta-id))]
     (assoc (first perus) :osat suoritukset)))


(defn lisaa!
  [{:keys [jarjestamismuoto koulutustoimija opiskelijavuosi suorittaja rahoitusmuoto tutkinto osat]
    :as suoritus}]
  (auditlog/suoritus-operaatio! :lisays suoritus)
  (let [suorituskerta (sql/insert :suorituskerta
                        (sql/values {:tutkinto        tutkinto
                                     :rahoitusmuoto   rahoitusmuoto
                                     :suorittaja      suorittaja
                                     :jarjestamismuoto jarjestamismuoto
                                     :opiskelijavuosi (Integer/parseInt opiskelijavuosi)
                                     :koulutustoimija koulutustoimija}))]
    (doseq [osa osat]
      (sql/insert :suoritus
        (sql/values {:suorituskerta (:suorituskerta_id suorituskerta)
                     :tutkinnonosa (:tutkinnonosa_id osa)
                     :arvosana (:arvosana osa)
                     :arvosanan_korotus (:korotus osa)
                     :osaamisen_tunnustaminen (:tunnustaminen osa)
                     :kieli (:kieli osa)
                     :todistus (:todistus osa)})))
    suorituskerta))

(defn laheta!
  [suoritukset]
  (auditlog/suoritus-operaatio! :paivitys {:suoritukset suoritukset
                                           :tila "ehdotettu"})
  (sql/update :suorituskerta
    (sql/set-fields {:tila "ehdotettu"
                     :ehdotusaika (sql/sqlfn now)})
    (sql/where {:suorituskerta_id [in suoritukset]})))

(defn hyvaksy!
  [suoritukset]
  (auditlog/suoritus-operaatio! :paivitys {:suoritukset suoritukset
                                           :tila "hyvaksytty"})
  (sql/update :suorituskerta
    (sql/set-fields {:tila "hyvaksytty"
                     :hyvaksymisaika (sql/sqlfn now)})
    (sql/where {:suorituskerta_id [in suoritukset]})))

(defn poista!
  [suorituskerta-id]
  (auditlog/suoritus-operaatio! :poisto {:suoritus-id suorituskerta-id})
  (sql/delete :suorituskerta
    (sql/where {:suorituskerta_id suorituskerta-id
                :tila "luonnos"})))
