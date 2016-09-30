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
             [aitu.infra.arvioija-arkisto :as arvioija-arkisto]
             [oph.common.util.http-util :refer [parse-iso-date]]
             [aitu.integraatio.sql.korma :refer :all]))


(defn ->int [str-or-int]
  (if (integer? str-or-int) 
    str-or-int
    (Integer/parseInt str-or-int)))
  
(defn hae
  [suorituskerta-id]
  (sql-util/select-unique :suorituskerta
    (sql/where {:suorituskerta_id suorituskerta-id})))


(defn hae-suoritukset 
  ([suorituskerta-id]
    (sql/select :suoritus
      (sql/join :tutkinnonosa (= :tutkinnonosa.tutkinnonosa_id :tutkinnonosa))
      (sql/fields :suoritus_id :arvosana :suorituskerta :tutkinnonosa :arvosanan_korotus :osaamisen_tunnustaminen :kieli :todistus :osaamisala
                  [:tutkinnonosa.osatunnus :osatunnus]
                  [:tutkinnonosa.nimi_fi :nimi]
 ; TODO: nimi_fi ei ole oikeasti hyvä juttu välttämättä..
                )
      (sql/where {:suorituskerta suorituskerta-id})))
    ([]
      (sql/select :suorituskerta
        (sql/join :suorittaja (= :suorittaja.suorittaja_id :suorittaja))
        (sql/join :suoritus (= :suoritus.suorituskerta :suorituskerta_id))
        (sql/fields :suoritus.tutkinnonosa :suoritus.arvosanan_korotus :suoritus.osaamisen_tunnustaminen :suoritus.kieli :suoritus.todistus :suoritus.osaamisala :suoritus.arvosana
                    :suorituskerta.suorituskerta_id :tutkinto :rahoitusmuoto :suorittaja :koulutustoimija :tila :paikka :jarjestelyt :jarjestamismuoto :valmistava_koulutus
                    :suorituskerta.suoritusaika_alku :suorituskerta.suoritusaika_loppu
                  ))))

(defn hae-kaikki-suoritukset [koulutustoimija]
 (sql/select suorituskerta
   (sql/join :suoritus (= :suoritus.suorituskerta :suorituskerta_id))
   (sql/fields :suorituskerta_id :tutkinto :rahoitusmuoto :suorittaja :koulutustoimija :jarjestelyt :paikka :valmistava_koulutus :suoritusaika_alku :suoritusaika_loppu :arviointikokouksen_pvm
               [:suoritus.suoritus_id :suoritus_id]
               [:suoritus.arvosana :arvosana]
               [:suoritus.tutkinnonosa :tutkinnonosa]
               [:suoritus.arvosanan_korotus :arvosanan_korotus]
               [:suoritus.osaamisen_tunnustaminen :osaamisen_tunnustaminen]
               [:suoritus.kieli :kieli]
               [:suoritus.todistus :todistus]
               [:suoritus.osaamisala :osaamisala]
               [:suoritus.lisatiedot :lisatiedot])
   (sql/where (= :koulutustoimija koulutustoimija))))
 

(defn hae-kaikki
  [{:keys [ehdotuspvm_alku ehdotuspvm_loppu hyvaksymispvm_alku hyvaksymispvm_loppu jarjestamismuoto koulutustoimija rahoitusmuoto tila tutkinto suorituskertaid suorittaja]}]
  (->
    (sql/select* suorituskerta)
    (sql/join :suorittaja (= :suorittaja.suorittaja_id :suorittaja))
    (sql/join :nayttotutkinto (= :nayttotutkinto.tutkintotunnus :tutkinto))
    (sql/join :koulutustoimija (= :koulutustoimija.ytunnus :koulutustoimija))
    (sql/fields :suorituskerta_id :tutkinto :rahoitusmuoto :suorittaja :koulutustoimija :tila :ehdotusaika :hyvaksymisaika
                :suoritusaika_alku :suoritusaika_loppu
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

(defn hae-arvioijat [suorituskerta-id]
  (sql/select :arvioija
    (sql/fields :nimi :rooli :nayttotutkintomestari :arvioija.arvioija_id)
    (sql/join :suorituskerta_arvioija (= :suorituskerta_arvioija.arvioija_id :arvioija_id))
    (sql/where {:suorituskerta_arvioija.suorituskerta_id suorituskerta-id})))
               
(defn hae-tiedot [suorituskerta-id]
  (let [perus (first (hae-kaikki {:suorituskertaid suorituskerta-id}))
        suoritukset (hae-suoritukset (->int suorituskerta-id))
        arvioijat (hae-arvioijat (->int suorituskerta-id))]
     (-> perus
       (assoc :osat suoritukset)
       (assoc :arvioijat arvioijat))))

(defn osa->suoritus-db [osa]
  {:suorituskerta (or (:suorituskerta osa) (:suorituskerta_id osa))
   :tutkinnonosa (or (:tutkinnonosa osa) (:tutkinnonosa_id osa))
   :arvosana (:arvosana osa)
   :arvosanan_korotus (:arvosanan_korotus osa)
   :osaamisen_tunnustaminen (:osaamisen_tunnustaminen osa)
   :osaamisala (or (:osaamisala osa) (:osaamisala_id osa))
   :kieli (:kieli osa)
   :todistus (:todistus osa)})

(defn kerta->suorituskerta-db [kerta]
  (-> kerta
    (select-keys [:jarjestamismuoto :valmistava_koulutus :paikka :jarjestelyt :koulutustoimija :suoritusaika_alku :suoritusaika_loppu :opiskelijavuosi :suorittaja :rahoitusmuoto :tutkinto :arviointikokouksen_pvm])
    (update :opiskelijavuosi ->int)
    (update :suoritusaika_alku parse-iso-date)
    (update :arviointikokouksen_pvm parse-iso-date)
    (update :suoritusaika_loppu parse-iso-date)))

(defn ^:private lisaa-suoritus! [osa]
  (sql/insert :suoritus
   (sql/values (osa->suoritus-db osa))))

(defn lisaa!
  [suoritus]
  (auditlog/suoritus-operaatio! :lisays suoritus)
  (let [suorituskerta (sql/insert suorituskerta
                        (sql/values (kerta->suorituskerta-db  suoritus)))]
    (doseq [osa (:osat suoritus)]
      (lisaa-suoritus! (assoc osa :suorituskerta_id (:suorituskerta_id suorituskerta))))
    (doseq [arvioija (:arvioijat suoritus)]
      (sql/insert :suorituskerta_arvioija
        (sql/values {:suorituskerta_id (:suorituskerta_id suorituskerta)
                     :arvioija_id (:arvioija_id arvioija)})))
    suorituskerta))

(defn lisaa-tai-paivita!
  [{:keys [arvioijat jarjestamismuoto valmistava_koulutus paikka jarjestelyt koulutustoimija opiskelijavuosi suorittaja rahoitusmuoto tutkinto osat suorituskerta_id]
    :as suoritus}]
  (if (nil? suorituskerta_id)
    (lisaa! suoritus)
    ; päivitys
    (do
;      (auditlog/suoritus-operaatio! :paivitys {:suorituskerta_id suorituskerta_id})
       (auditlog/suoritus-operaatio! :paivitys suoritus)
      (sql-util/update-unique suorituskerta
         (sql/set-fields (kerta->suorituskerta-db  suoritus))
         (sql/where {:suorituskerta_id (->int suorituskerta_id)}))
      
      ; update arvioijat
      (sql/delete :suorituskerta_arvioija
        (sql/where {:suorituskerta_id suorituskerta_id}))
      
      (doseq [arvioija arvioijat]
        (let [arvioija-id (or (:arvioija_id arvioija)
                              (:arvioija_id (arvioija-arkisto/lisaa! arvioija)))]
          (sql/insert :suorituskerta_arvioija
            (sql/values {:suorituskerta_id suorituskerta_id
                         :arvioija_id arvioija-id}))))
      
      ; update osat
      ; poistetut osat, käsitellään ennen kuin lisätään uusia osia
      (let [ids (keep :suoritus_id osat)]
        (sql/delete :suoritus
          (sql/where {:suorituskerta suorituskerta_id
                      :suoritus_id [not-in ids]})))
      (doseq [osa osat]
        (if (nil? (:suoritus_id osa))
          (lisaa-suoritus! (assoc osa :suorituskerta suorituskerta_id))
          (sql-util/update-unique :suoritus
            (sql/set-fields (osa->suoritus-db (assoc osa :suorituskerta suorituskerta_id)))
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
  (sql/delete :suorituskerta_arvioija
    (sql/where {:suorituskerta_id suorituskerta-id}))
  
  (sql/delete :suorituskerta
    (sql/where {:suorituskerta_id suorituskerta-id
                :tila "luonnos"})))
