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

(ns aitu.auditlog
  "Audit lokituksen abstrahoiva rajapinta.
  Aitun tapauksessa halutaan lokittaa päivitysoperaatiot. Tiedon selaamisella ei ole niin suurta väliä."
  (:require
    [oph.korma.korma-auth :as ka]
    [clojure.tools.logging :as log]
    [oph.log :as aitulog]
    [oph.common.infra.common-audit-log :as common-audit-log]))

(def operaatiot {:poisto "poisto"
                 :lisays "lisäys"
                 :paivitys "päivitys"})

;; OPH-1966
(defn ^:private ->common-audit-log-json-entry
  "Logittaa OPH:n projektien yhteiseen audit-logiin"
  [tieto oid tieto-id operaatio tiedot-map]
  {:pre [(bound? #'ka/*current-user-oid*)]}
  (let [data  {:operation   operaatio
               :user        {:oid @ka/*current-user-oid*}
               :resource    (name tieto)
               :resourceOid oid
               :id          (str tieto-id)
               :delta       (reduce-kv
                              (fn [result k v] (conj result {:op (operaatio operaatiot) :path (name k) :value v}))
                              []
                              tiedot-map)}]
    (common-audit-log/->audit-log-entry data)
    ))

(defn ^:private kirjoita!
  ([tieto oid tieto-id operaatio]
    (kirjoita! tieto oid tieto-id operaatio {}))
  ([tieto oid tieto-id operaatio tiedot-map]
    {:pre [(bound? #'ka/*current-user-uid*),
           (contains? operaatiot operaatio)
           (keyword? tieto)
           (map? tiedot-map)]}
    (binding [aitulog/*lisaa-uid-ja-request-id?* false]
      (let [log-entry (->common-audit-log-json-entry tieto oid tieto-id operaatio tiedot-map)]
        (log/info log-entry)
        ))))

(defn jarjestamissopimus-paivitys!
  [sopimusid diaarinumero]
  (kirjoita! :järjestämissopimus nil sopimusid :paivitys
    {:sopimusid sopimusid
     :diaarinumero diaarinumero}))

(defn jarjestamissopimus-lisays!
  [sopimusid diaarinumero]
  (kirjoita! :järjestämissopimus nil sopimusid :lisays
    {:sopimusid sopimusid
     :diaarinumero diaarinumero}))

(defn jarjestamissopimus-poisto!
  [sopimusid diaarinumero]
  (kirjoita! :järjestämissopimus nil sopimusid :poisto
    {:sopimusid sopimusid
     :diaarinumero diaarinumero}))

(defn tutkintotoimikunta-operaatio!
  [operaatio tkunta diaarinumero]
  (kirjoita! :tutkintotoimikunta nil tkunta operaatio
             {:tkunta tkunta
              :diaarinumero diaarinumero}))

(defn toimikunnan-tutkinnot-paivitys!
  [toimikunta tutkintotunnukset]
  (kirjoita! :toimikunnan-tutkinto nil toimikunta :paivitys
             {:toimikunta toimikunta
              :tutkintotunnukset tutkintotunnukset}))

(defn jasenyys-operaatio!
  [operaatio tkunta jasenyys_id]
  (kirjoita! :jäsenyys nil tkunta operaatio
             {:tkunta tkunta
              :jasenyys_id jasenyys_id}))

(defn henkilo-operaatio!
  [operaatio & henkilo_id]
  (kirjoita! :henkilö nil henkilo_id operaatio
             {:henkilo_id henkilo_id}))

(defn sopimuksen-tutkinnot-operaatio!
  [operaatio jarjestamissopimusid tutkintoversiot]
  (kirjoita! :sopimuksen-tutkinto nil jarjestamissopimusid operaatio
             {:jarjestamissopimusid jarjestamissopimusid
              :tutkintoversiot tutkintoversiot}))

(defn tutkinnon-suunnitelma-operaatio!
  [operaatio jarjestamissuunnitelma_id & sopimus_ja_tutkinto]
  (kirjoita! :tutkinnon-suunnitelma nil jarjestamissuunnitelma_id operaatio
             (merge {:jarjestamissuunnitelma_id jarjestamissuunnitelma_id}
                    (when sopimus_ja_tutkinto {:sopimus_ja_tutkinto sopimus_ja_tutkinto}))))

(defn tutkinnon-liite-operaatio!
  [operaatio sopimuksen_liite_id & sopimus_ja_tutkinto]
  (kirjoita! :tutkinnon-liite nil sopimuksen_liite_id operaatio
             (merge {:sopimuksen_liite_id sopimuksen_liite_id}
                    (when sopimus_ja_tutkinto {:sopimus_ja_tutkinto sopimus_ja_tutkinto}))))

(defn sopimuksen-tutkinnon-osat-paivitys!
  [sopimus_ja_tutkinto_id tutkinnon_osa_idt]
  (kirjoita! :sopimuksen-tutkinnon-osat nil sopimus_ja_tutkinto_id :paivitys
             {:sopimus_ja_tutkinto_id sopimus_ja_tutkinto_id
              :tutkinnon_osa_idt tutkinnon_osa_idt}))

(defn sopimuksen-osaamisalat-paivitys!
  [sopimus_ja_tutkinto_id osaamisala_idt]
  (kirjoita! :sopimuksen-osaamisalat nil sopimus_ja_tutkinto_id :paivitys
             {:sopimus_ja_tutkinto_id sopimus_ja_tutkinto_id
              :osaamisala_idt osaamisala_idt}))

(defn ohjeen-paivitys!
  [ohjetunniste]
  (kirjoita! :ohje nil ohjetunniste :paivitys {:ohjetunniste ohjetunniste}))

(defn tiedote-operaatio!
  [operaatio tiedoteid]
  {:pre [(contains? operaatiot operaatio)]}
  (kirjoita! :tiedote nil tiedoteid operaatio {:tiedoteid tiedoteid}))

(defn suorittaja-operaatio!
  [oid suorittajaid operaatio tiedot]
  {:pre [(contains? operaatiot operaatio)]}
  (kirjoita! :suorittaja oid suorittajaid operaatio tiedot))

(defn suoritus-operaatio!
  [suoritus-id operaatio tiedot]
  {:pre [(contains? operaatiot operaatio)]}
  (kirjoita! :suoritus nil suoritus-id operaatio tiedot))

(defn suorituskerta-operaatio!
  [suorituskerta-id operaatio tiedot]
  {:pre [(contains? operaatiot operaatio)]}
  (kirjoita! :suorituskerta nil suorituskerta-id operaatio tiedot))

(defn lisaa-arvioija!
  [arvioija-id tiedot]
  (kirjoita! :arvioija nil arvioija-id :lisays tiedot))

(defn lue-suoritukset-excel!
  []
  (kirjoita! :suoritus nil nil :lisays {}))
