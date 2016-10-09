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

(ns infra.test.data
  (:require [korma.core :as sql]
            [korma.db :as db]
            [aitu.infra.i18n :as i18n]
            [oph.korma.korma-auth :as ka]
            [aitu.asetukset :refer [lue-asetukset]]
            [aitu.integraatio.sql.korma :refer [kayttaja]]
            [aitu.toimiala.kayttajaroolit :refer [kayttajaroolit]]))

(def taulut ["suorituskerta_arvioija"
             "arvioija"
             "suoritus"
             "suorituskerta"
             "jarjestamissuunnitelma"
             "sopimus_ja_tutkinto_ja_osaamisala"
             "sopimuksen_liite"
             "jarjestamissuunnitelma"
             "sopimus_ja_tutkinto"
             "jarjestamissopimus"
             "toimipaikka"
             "oppilaitos"
             "koulutustoimija"
             "jasenyys"
             "jarjesto"
             "toimikunta_ja_tutkinto"
             "tutkinnonosa"
             "tutkintoversio"
             "nayttotutkinto"
             "peruste"
             "osaamisala"
             "opintoala"
             "koulutusala"
             "tutkintotyyppi"
             "henkilo"
             "tutkintotoimikunta"
             "toimikausi"
             "ohje"
             "kayttaja"
             "tiedote"])

(defn ^:test-api tyhjenna-testidata!
  [oid]
  (doseq [taulu taulut]
    (sql/exec-raw (str "delete from " taulu " where luotu_kayttaja = '" oid "'"))))

(defn ^:test-api luo-testikayttaja!
  ([testikayttaja-oid testikayttaja-uid roolitunnus]
  (binding [ka/*current-user-uid* ka/jarjestelmakayttaja
            ka/*current-user-oid* (promise)]
    (deliver ka/*current-user-oid* ka/jarjestelmakayttaja)
    (when-not (first (sql/select kayttaja
                                 (sql/where {:oid testikayttaja-oid})))
      (db/transaction
        (sql/insert kayttaja
                    (sql/values
                      {:voimassa true
                       :sukunimi "Leiningen"
                       :etunimi "Testi"
                       :rooli roolitunnus
                       :uid testikayttaja-uid
                       :oid testikayttaja-oid}))))))
  ([testikayttaja-oid testikayttaja-uid]
    (luo-testikayttaja! testikayttaja-oid testikayttaja-uid (:yllapitaja kayttajaroolit))))

(defn ^:test-api poista-testikayttaja!
  [testikayttaja-oid]
  (sql/exec-raw (str "delete from kayttaja where oid = '" testikayttaja-oid "'")))
