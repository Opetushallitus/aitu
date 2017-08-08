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
    [oph.log :as aitulog]))

(def operaatiot {:poisto "poisto"
                 :lisays "lisäys"
                 :paivitys "päivitys"})

(defn ^:private kirjoita!
  ([tieto operaatio]
    (kirjoita! tieto operaatio {}))
  ([tieto operaatio tiedot-map]
    {:pre [(bound? #'ka/*current-user-uid*),
           (contains? operaatiot operaatio)
           (keyword? tieto)
           (map? tiedot-map)]}
    (let [uid ka/*current-user-uid*
          msg (str "uid: " uid " oper: " (operaatio operaatiot) " kohde: " (name tieto) " meta: (" tiedot-map ")")]
      (binding [aitulog/*lisaa-uid-ja-request-id?* false]
        (log/info msg)))))

(defn jarjestamissopimus-paivitys!
  [sopimusid diaarinumero]
  (kirjoita! :järjestämissopimus :paivitys
    {:sopimusid sopimusid
     :diaarinumero diaarinumero}))

(defn jarjestamissopimus-lisays!
  [sopimusid diaarinumero]
  (kirjoita! :järjestämissopimus :lisays
    {:sopimusid sopimusid
     :diaarinumero diaarinumero}))

(defn jarjestamissopimus-poisto!
  [sopimusid diaarinumero]
  (kirjoita! :järjestämissopimus :poisto
    {:sopimusid sopimusid
     :diaarinumero diaarinumero}))

(defn tutkintotoimikunta-operaatio!
  [operaatio tkunta diaarinumero]
  (kirjoita! :tutkintotoimikunta operaatio
             {:tkunta tkunta
              :diaarinumero diaarinumero}))

(defn toimikunnan-tutkinnot-paivitys!
  [toimikunta tutkintotunnukset]
  (kirjoita! :toimikunnan-tutkinto :paivitys
             {:toimikunta toimikunta
              :tutkintotunnukset tutkintotunnukset}))

(defn jasenyys-operaatio!
  [operaatio tkunta jasenyys_id]
  (kirjoita! :jäsenyys operaatio
             {:tkunta tkunta
              :jasenyys_id jasenyys_id}))

(defn henkilo-operaatio!
  [operaatio & henkilo_id]
  (kirjoita! :henkilö operaatio
             {:henkilo_id henkilo_id}))

(defn sopimuksen-tutkinnot-operaatio!
  [operaatio jarjestamissopimusid tutkintoversiot]
  (kirjoita! :sopimuksen-tutkinto operaatio
             {:jarjestamissopimusid jarjestamissopimusid
              :tutkintoversiot tutkintoversiot}))

(defn tutkinnon-suunnitelma-operaatio!
  [operaatio jarjestamissuunnitelma_id & sopimus_ja_tutkinto]
  (kirjoita! :tutkinnon-suunnitelma operaatio
             (merge {:jarjestamissuunnitelma_id jarjestamissuunnitelma_id}
                    (when sopimus_ja_tutkinto {:sopimus_ja_tutkinto sopimus_ja_tutkinto}))))

(defn tutkinnon-liite-operaatio!
  [operaatio sopimuksen_liite_id & sopimus_ja_tutkinto]
  (kirjoita! :tutkinnon-liite operaatio
             (merge {:sopimuksen_liite_id sopimuksen_liite_id}
                    (when sopimus_ja_tutkinto {:sopimus_ja_tutkinto sopimus_ja_tutkinto}))))

(defn sopimuksen-tutkinnon-osat-paivitys!
  [sopimus_ja_tutkinto_id tutkinnon_osa_idt]
  (kirjoita! :sopimuksen-tutkinnon-osat :paivitys
             {:sopimus_ja_tutkinto_id sopimus_ja_tutkinto_id
              :tutkinnon_osa_idt tutkinnon_osa_idt}))

(defn sopimuksen-osaamisalat-paivitys!
  [sopimus_ja_tutkinto_id osaamisala_idt]
  (kirjoita! :sopimuksen-osaamisalat :paivitys
             {:sopimus_ja_tutkinto_id sopimus_ja_tutkinto_id
              :osaamisala_idt osaamisala_idt}))

(defn ohjeen-paivitys!
  [ohjetunniste]
  (kirjoita! :ohje :paivitys {:ohjetunniste ohjetunniste}))

(defn tiedote-operaatio!
  [operaatio tiedoteid]
  {:pre [(contains? operaatiot operaatio)]}
  (kirjoita! :tiedote operaatio {:tiedoteid tiedoteid}))

(defn suorittaja-operaatio!
  [operaatio tiedot]
  {:pre [(contains? operaatiot operaatio)]}
  (kirjoita! :suorittaja operaatio tiedot))

(defn suoritus-operaatio!
  [operaatio tiedot]
  {:pre [(contains? operaatiot operaatio)]}
  (kirjoita! :suoritus operaatio tiedot))

(defn lisaa-arvioija!
  [tiedot]
  (kirjoita! :arvioija :lisays tiedot))

(defn lue-suoritukset-excel!
  []
  (kirjoita! :suoritus :lisays {}))