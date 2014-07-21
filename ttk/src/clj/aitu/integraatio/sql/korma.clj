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

(ns aitu.integraatio.sql.korma
  (:import java.sql.Date
           org.joda.time.LocalDate)
  (:require  korma.db
             [aitu.infra.i18n :as i18n]
             [korma.core :as sql]
             [oph.korma.korma-auth :as korma-auth]
             [clj-time.coerce :as time-coerce]
             [clj-time.core :as time]))

(defn korma-asetukset
  "Muuttaa asetustiedoston db-avaimen arvon Korman odottamaan muotoon."
  [db-asetukset]
  (clojure.set/rename-keys db-asetukset {:name :db}))

(defn bonecp-datasource
  "BoneCP based connection pool"
  [db-asetukset]
  (let [korma-postgres (korma.db/postgres (korma-asetukset db-asetukset))
        bonecp-ds  (doto (com.jolbox.bonecp.BoneCPDataSource.)
                     (.setJdbcUrl (str "jdbc:" (:subprotocol korma-postgres) ":" (:subname korma-postgres)))
                     (.setUsername (:user korma-postgres))
                     (.setPassword (:password korma-postgres))
                     (.setConnectionTestStatement "select 42")
                     (.setConnectionTimeoutInMs 2000)
                     (.setDefaultAutoCommit false)
                     (.setMaxConnectionsPerPartition 10)
                     (.setMinConnectionsPerPartition 5)
                     (.setPartitionCount 1)
                     (.setConnectionHook korma-auth/customizer-impl-bonecp))]
    bonecp-ds))

(defn luo-db [db-asetukset]
  (korma.db/default-connection
    (korma.db/create-db {:make-pool? false :datasource (bonecp-datasource db-asetukset)})))

(defn convert-instances-of [c f m]
  (clojure.walk/postwalk #(if (instance? c %) (f %) %) m))

(defn joda-datetime->sql-timestamp [m]
  (convert-instances-of org.joda.time.DateTime
                        time-coerce/to-sql-time
                        m))

(defn sql-timestamp->joda-datetime [m]
  (convert-instances-of java.sql.Timestamp
                        time-coerce/from-sql-time
                        m))

(defn ^:private to-local-date-default-tz
  [date]
  (let [dt (time-coerce/to-date-time date)]
    (time-coerce/to-local-date (time/to-time-zone dt (time/default-time-zone)))))

(defn sql-date->joda-date [m]
  (convert-instances-of java.sql.Date
                        to-local-date-default-tz
                        m))

(defn joda-date->sql-date [m]
  (convert-instances-of org.joda.time.LocalDate
                        time-coerce/to-sql-date
                        m))

(declare toimikausi henkilo tutkintotoimikunta jasenyys
         nayttotutkinto opintoala koulutusala jarjestamissopimus
         sopimus-ja-tutkinto sopimus-ja-tutkinto-ja-tutkinnonosa
         sopimus-ja-tutkinto-ja-osaamisala oppilaitos kieli
         toimikunta-ja-tutkinto tutkintotyyppi peruste jarjesto
         keskusjarjesto tutkinnonosa osaamisala jarjestamissuunnitelma
         toimipaikka tutkintoversio uusin-versio tutkinto-ja-tutkinnonosa
         sopimuksen-liite koulutustoimija)

(defmacro defentity
  "Wrapperi Korman defentitylle, lisää yleiset prepare/transform-funktiot."
  [ent & body]
  `(sql/defentity ~ent
     (sql/prepare joda-date->sql-date)
     (sql/prepare joda-datetime->sql-timestamp)
     (sql/transform sql-date->joda-date)
     (sql/transform sql-timestamp->joda-datetime)
     ~@body))

(defn entity-alias [entity alias]
  (assoc entity :name alias
                :alias alias))

;; Korma ei salli useampaa kuin yhtä linkkiä samojen entityjen välillä.
;; Tämä makro tekee kopion entitystä uudelle nimelle.
(defmacro defalias [alias entity]
  `(def ~alias (entity-alias ~entity ~(name alias))))

(sql/defentity toimikausi
  (sql/pk :toimikausi_id))

(defentity keskusjarjesto
  (sql/table :jarjesto :keskusjarjesto)
  (sql/pk :jarjestoid))

(defentity jarjesto
  (sql/pk :jarjestoid)
  (sql/belongs-to keskusjarjesto
    {:fk :keskusjarjestoid}))

(defentity henkilo
  (sql/pk :henkiloid)
  (sql/has-one kieli
    {:fk :aidinkieli})
  (sql/has-many jasenyys
    {:fk :henkiloid})
  (sql/belongs-to jarjesto
    {:fk :jarjesto}))

(defentity kayttaja
  (sql/pk :oid))

(defentity koulutustoimija
  (sql/table :koulutustoimija)
  (sql/pk :ytunnus)
  (sql/has-many oppilaitos
    {:fk :koulutustoimija}))

(defentity oppilaitos
  (sql/table :oppilaitos)
  (sql/pk :oppilaitoskoodi)
  (sql/has-many jarjestamissopimus
    {:fk :oppilaitos})
  (sql/has-many toimipaikka
    {:fk :toimipaikka})
  (sql/belongs-to koulutustoimija
    {:fk :koulutustoimija}))

(defentity toimipaikka
  (sql/pk :toimipaikkakoodi)
  (sql/belongs-to oppilaitos
    {:fk :oppilaitos}))

(defentity tutkintotoimikunta
  (sql/pk :tkunta)
  (sql/belongs-to toimikausi
    {:fk :toimikausi_id})
  (sql/has-many jasenyys
    {:fk :toimikunta})
  (sql/has-many jarjestamissopimus
    {:fk :toimikunta})
  (sql/has-one kieli
    {:fk :kielisyys})
  (sql/many-to-many nayttotutkinto :toimikunta_ja_tutkinto {:lfk :toimikunta :rfk :tutkintotunnus}))

(defalias sopijatoimikunta tutkintotoimikunta)

(defentity rooli
  (sql/pk :nimi))

(defentity edustus
  (sql/pk :nimi))

(defentity osaamisala
  (sql/pk :osaamisala_id)
  (sql/belongs-to tutkintoversio
    {:fk :tutkintoversio})
  (sql/has-many sopimus-ja-tutkinto-ja-osaamisala
    {:fk :osaamisala}))

(defentity tutkinnonosa
  (sql/pk :tutkinnonosa_id)
  (sql/has-many tutkinto-ja-tutkinnonosa
    {:fk tutkinnonosa})
  (sql/has-many sopimus-ja-tutkinto-ja-tutkinnonosa
    {:fk :tutkinnonosa})
  (sql/many-to-many tutkintoversio
    :tutkinto_ja_tutkinnonosa {:lfk :tutkinnonosa, :rfk :tutkintoversio}))

(defentity tutkinto-ja-tutkinnonosa
  (sql/table :tutkinto_ja_tutkinnonosa)
  (sql/belongs-to tutkintoversio
    {:fk :tutkintoversio})
  (sql/belongs-to tutkinnonosa
    {:fk :tutkinnonosa}))

(defentity jasenyys
  (sql/belongs-to henkilo
    {:fk :henkiloid})
  (sql/belongs-to tutkintotoimikunta
    {:fk :toimikunta})
  (sql/has-one rooli
    {:fk :rooli})
  (sql/has-one edustus
    {:fk :edustus}))

(defentity toimikunta-ja-tutkinto
  (sql/table :toimikunta_ja_tutkinto)
  (sql/belongs-to tutkintotoimikunta
    {:fk :toimikunta})
  (sql/belongs-to nayttotutkinto
    {:fk :tutkintotunnus}))

(defentity nayttotutkinto
  (sql/pk :tutkintotunnus)
  (sql/many-to-many tutkintotoimikunta :toimikunta_ja_tutkinto {:lfk :tutkintotunnus :rfk :toimikunta})
  (sql/belongs-to opintoala
    {:fk :opintoala})
  (sql/belongs-to tutkintotyyppi
    {:fk :tyyppi})
  (sql/has-many tutkintoversio
    {:fk :tutkintotunnus})
  (sql/belongs-to uusin-versio
    {:fk :uusin_versio_id}))

(defentity tutkintoversio
  (sql/pk :tutkintoversio_id)
  (sql/belongs-to nayttotutkinto
    {:fk :tutkintotunnus})
  (sql/has-many sopimus-ja-tutkinto
    {:fk :tutkintoversio})
  (sql/has-many tutkinto-ja-tutkinnonosa
    {:fk :tutkintoversio})
  (sql/many-to-many tutkinnonosa
    :tutkinto_ja_tutkinnonosa {:lfk :tutkintoversio, :rfk :tutkinnonosa})
  (sql/has-many osaamisala
    {:fk :tutkintoversio}))

(defalias uusin-versio tutkintoversio)

(defentity jarjestamissopimus
  (sql/table :jarjestamissopimus)
  (sql/pk :jarjestamissopimusid)
  (sql/belongs-to koulutustoimija
    {:fk :koulutustoimija})
  (sql/belongs-to oppilaitos
    {:fk :tutkintotilaisuuksista_vastaava_oppilaitos})
  (sql/belongs-to tutkintotoimikunta
    {:fk :toimikunta})
  (sql/belongs-to sopijatoimikunta
    {:fk :sopijatoimikunta})
  (sql/has-many sopimus-ja-tutkinto
    {:fk :jarjestamissopimusid}))

(defentity sopimus-ja-tutkinto
  (sql/table :sopimus_ja_tutkinto)
  (sql/pk :sopimus_ja_tutkinto_id)
  (sql/belongs-to jarjestamissopimus
    {:fk :jarjestamissopimusid})
  (sql/belongs-to tutkintoversio
    {:fk :tutkintoversio})
  (sql/has-many jarjestamissuunnitelma
    {:fk :sopimus_ja_tutkinto})
  (sql/has-many sopimuksen-liite
    {:fk :sopimus_ja_tutkinto})
  (sql/has-many sopimus-ja-tutkinto-ja-tutkinnonosa
    {:fk :sopimus_ja_tutkinto})
  (sql/has-many sopimus-ja-tutkinto-ja-osaamisala
    {:fk :sopimus_ja_tutkinto}))

(defentity sopimus-ja-tutkinto-ja-tutkinnonosa
  (sql/table :sopimus_ja_tutkinto_ja_tutkinnonosa)
  (sql/belongs-to tutkinnonosa
    {:fk :tutkinnonosa})
  (sql/belongs-to sopimus-ja-tutkinto
    {:fk :sopimus_ja_tutkinto}))

(defentity sopimus-ja-tutkinto-ja-osaamisala
  (sql/table :sopimus_ja_tutkinto_ja_osaamisala)
  (sql/belongs-to osaamisala
    {:fk :osaamisala})
  (sql/belongs-to sopimus-ja-tutkinto
    {:fk :sopimus_ja_tutkinto}))

(defentity jarjestamissuunnitelma
  (sql/table :jarjestamissuunnitelma)
  (sql/belongs-to sopimus-ja-tutkinto
    {:fk :sopimus_ja_tutkinto}))

(defentity sopimuksen-liite
  (sql/table :sopimuksen_liite)
  (sql/belongs-to sopimus-ja-tutkinto
    {:fk :sopimus_ja_tutkinto}))

(defentity opintoala
  (sql/pk :opintoala_tkkoodi)
  (sql/belongs-to koulutusala
    {:fk :koulutusala_tkkoodi})
  (sql/has-many nayttotutkinto
    {:fk :opintoala}))

(defentity koulutusala
  (sql/pk :koulutusala_tkkoodi)
  (sql/has-many opintoala
    {:fk :koulutusala_tkkoodi}))

(defentity tutkintotyyppi
  (sql/pk :tyyppi))

(defentity peruste
  (sql/pk :diaarinumero))

(defentity tiedote)

(defentity ohje
  (sql/pk :ohjetunniste))

(defentity haku)

(def validationquery "select count(*) from tutkintotoimikunta")

(defn validate-connection!
  []
  (first
    (sql/select validationquery)))
