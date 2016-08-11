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
  [{:keys [ehdotuspvm_alku ehdotuspvm_loppu hyvaksymispvm_alku hyvaksymispvm_loppu jarjestamismuoto koulutustoimija rahoitusmuoto tila tutkinto suorituskertaid suorittaja]}]
  (->
    (sql/select* :suorituskerta)
    (sql/join :suorittaja (= :suorittaja.suorittaja_id :suorittaja))
    (sql/join :nayttotutkinto (= :nayttotutkinto.tutkintotunnus :tutkinto))
    (sql/join :koulutustoimija (= :koulutustoimija.ytunnus :koulutustoimija))
    (sql/fields :suorituskerta_id :tutkinto :rahoitusmuoto :suorittaja :koulutustoimija :tila :ehdotusaika :hyvaksymisaika
                :jarjestamismuoto :opiskelijavuosi
                :valmistava_koulutus :paikka :jarjestelyt
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
      (not (clojure.string/blank? suorittaja))   (sql/where (or {:suorittaja.hetu suorittaja}
                                                                {:suorittaja.oid suorittaja}
                                                                {:suorittaja.etunimi [sql-util/ilike (str "%" suorittaja "%")]}
                                                                {:suorittaja.sukunimi [sql-util/ilike (str "%" suorittaja "%")]}))

      (seq tila) (sql/where {:tila tila})
      (seq tutkinto) (sql/where {:tutkinto tutkinto}))
    sql/exec))


(defn hae-tiedot [suorituskerta-id]
  (let [perus (hae-kaikki {:suorituskertaid suorituskerta-id})
        suoritukset (hae-suoritukset (Integer/parseInt suorituskerta-id))]
     (assoc (first perus) :osat suoritukset)))

(defn osa->suoritus-db [osa]
  {:suorituskerta (:suorituskerta_id osa)
   :tutkinnonosa (:tutkinnonosa_id osa)
   :arvosana (:arvosana osa)
   :arvosanan_korotus (:arvosanan_korotus osa)
   :osaamisen_tunnustaminen (:osaamisen_tunnustaminen osa)
   :osaamisala (:osaamisala_id osa)
   :kieli (:kieli osa)
   :todistus (:todistus osa)})

(defn ^:private lisaa-suoritus! [osa]
  (sql/insert :suoritus
   (sql/values (osa->suoritus-db osa))))

(defn lisaa!
  [suoritus]
  (auditlog/suoritus-operaatio! :lisays suoritus)
  (let [suorituskerta (sql/insert :suorituskerta
                        (sql/values (-> suoritus
                                      (select-keys [:jarjestamismuoto :valmistava_koulutus :paikka :jarjestelyt :koulutustoimija :opiskelijavuosi :suorittaja :rahoitusmuoto :tutkinto])
                                      (assoc  :opiskelijavuosi (Integer/parseInt (:opiskelijavuosi suoritus))))))]
    (doseq [osa (:osat suoritus)]
      (lisaa-suoritus! (assoc osa :suorituskerta_id (:suorituskerta_id suorituskerta))))
    suorituskerta))

(defn lisaa-tai-paivita!
  [{:keys [jarjestamismuoto valmistava_koulutus paikka jarjestelyt koulutustoimija opiskelijavuosi suorittaja rahoitusmuoto tutkinto osat suorituskerta_id]
    :as suoritus}]
  (if (nil? suorituskerta_id)
    (lisaa! suoritus)
    ; päivitys
    (do
      (auditlog/suoritus-operaatio! :paivitys {:suorituskerta_id suorituskerta_id})
      (sql-util/update-unique :suorituskerta
         (sql/set-fields (-> suoritus
                           (select-keys [:jarjestamismuoto :valmistava_koulutus :paikka :jarjestelyt :koulutustoimija :opiskelijavuosi :suorittaja :rahoitusmuoto :tutkinto])
                           (assoc  :opiskelijavuosi (Integer/parseInt (:opiskelijavuosi suoritus)))))
         (sql/where {:suorituskerta_id suorituskerta_id}))
      ; update osat
      ; poistetut osat, käsitellään ennen kuin lisätään uusia osia
      (let [ids (keep :suoritus_id osat)]
        (sql/delete :suoritus
          (sql/where {:suorituskerta suorituskerta_id
                      :suoritus_id [not-in ids]})))
      (doseq [osa osat]
        (if (nil? (:suoritus_id osa))
          (lisaa-suoritus! (assoc osa :suorituskerta_id suorituskerta_id))
          (sql-util/update-unique :suoritus
            (sql/set-fields (osa->suoritus-db osa))
            (sql/where {:suoritus_id (:suoritus_id osa)}))))
      suoritus)))

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
  ; TODO: delete-unique olisi siistimpi, mutta REST rajapinta olettaa ilmeisesti saavansa paluuarvona poistetun entityn
  (sql/delete :suorituskerta
    (sql/where {:suorituskerta_id suorituskerta-id
                :tila "luonnos"})))
