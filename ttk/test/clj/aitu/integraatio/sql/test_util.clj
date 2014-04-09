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

(ns aitu.integraatio.sql.test-util
  (:import java.util.Locale)
  (:require [korma.core :as sql]
            [korma.db :as db]
            [aitu.infra.i18n :as i18n]
            [oph.korma.korma-auth :as ka]
            [infra.test.data :as testdata]
            [aitu.asetukset :refer [lue-asetukset oletusasetukset]]
            [aitu.integraatio.sql.korma :refer [kayttaja]]
            [aitu.toimiala.kayttajaoikeudet :as kayttajaoikeudet]
            [aitu.toimiala.kayttajaoikeudet :as ko]
            [aitu.infra.kayttajaoikeudet-arkisto :as kayttajaoikeudet-arkisto]
            [aitu.integraatio.sql.test-data-util :refer [default-toimikunta]]))

(def testikayttaja-uid "MAN-O-TEST")
(def testikayttaja-oid "OID.MAN-O-TEST")
(def testi-locale (Locale. "fi"))

(defn luo-testikayttaja!
  []
  (testdata/luo-testikayttaja! testikayttaja-oid testikayttaja-uid))

(defn poista-testikayttaja!
  [] 
  (testdata/poista-testikayttaja! testikayttaja-oid))

(defn alusta-korma! []
  (let [dev-asetukset (assoc oletusasetukset :development-mode true)
        asetukset (lue-asetukset dev-asetukset)
        db-asetukset (merge-with #(or %2 %1)
                                 (:db asetukset)
                                 {:host (System/getenv "AMTU_DB_HOST")
                                  :port (System/getenv "AMTU_DB_PORT")})]
    (aitu.integraatio.sql.korma/luo-db db-asetukset)))

(defn tietokanta-fixture-oid
  "Annettu käyttäjätunnus sidotaan Kormalle testifunktion ajaksi."
  [f oid uid]
  (let [pool (alusta-korma!)]
    (luo-testikayttaja!) ; eri transaktio kuin loppuosassa!
    (binding [ka/*current-user-uid* uid ; testin aikana eri käyttäjä
              ka/*current-user-oid* (promise)
              i18n/*locale* testi-locale]
      (deliver ka/*current-user-oid* oid)
      ; avataan transaktio joka on voimassa koko kutsun (f) ajan
      (db/transaction
        (binding [kayttajaoikeudet/*current-user-authmap* (kayttajaoikeudet-arkisto/hae-oikeudet oid)]
          (try
            (f)
            (finally
              (testdata/tyhjenna-testidata! oid)
              (poista-testikayttaja!)))))
      (-> pool :pool :datasource .close))))

(defn tietokanta-fixture [f]
  (tietokanta-fixture-oid f testikayttaja-oid testikayttaja-uid))

(defmacro testidata-poistaen-kayttajana [oid & body]
  `(tietokanta-fixture-oid (fn [] ~@body) ~oid ~oid))

(defn with-user-rights [f]
  (binding [ko/*current-user-authmap* {:roolitunnus kayttajaoikeudet/kayttajarooli :toimikunta_jasen #{{:tkunta (:tkunta default-toimikunta) :rooli "sihteeri"}
                                                                                      {:tkunta "123" :rooli "sihteeri"}}}]
    (f)))
