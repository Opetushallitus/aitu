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

(ns aitu.integraatio.sql.jarjestamissopimus-arkisto-test
  (:import java.io.File
           org.apache.commons.io.FileUtils)
  (:require [clojure.test :refer [deftest testing is are use-fixtures]]
            [clojure.walk :refer [postwalk]]
            [aitu.infra.jarjestamissopimus-arkisto :as arkisto]
            [aitu.toimiala.jarjestamissopimus :refer :all]
            [korma.core :as sql]
            [clj-time.format :refer [formatters parse-local-date]]
            [aitu.integraatio.sql.test-util :refer [tietokanta-fixture]]
            [aitu.integraatio.sql.test-data-util :refer :all]
            [aitu.integraatio.sql.korma
             :refer [jarjestamissopimus osaamisala
                     sopimus-ja-tutkinto
                     sopimus-ja-tutkinto-ja-osaamisala
                     tutkintoversio]]
            [aitu.infra.oppilaitos-arkisto :as oppilaitos-arkisto]))

(use-fixtures :each tietokanta-fixture)

(defn arbitrary-sopimus
  "Luo järjestämissopimuksen."
  []
  {:post [(jarjestamissopimus? %)]}
  {:toimikunta "TKUN"
   :sopijatoimikunta "TKUN"
   :oppilaitos "OP1"
   :alkupvm (parse-local-date (formatters :year-month-day) "2013-02-01")
   :loppupvm (parse-local-date (formatters :year-month-day) "2099-02-01")
   :sopimusnumero "1234"})

(defn lisaa-testidata! []
  (sql/exec-raw (str "insert into oppilaitos("
                     "oppilaitoskoodi,"
                     "nimi"
                     ")values("
                     "'OP1',"
                     "'Oppilaitoksen nimi'"
                     ")"))
  (sql/exec-raw (str "insert into tutkintotoimikunta("
                     "tkunta,"
                     "nimi_fi,"
                     "kielisyys,"
                     "toimikausi_id,"
                     "toimikausi_alku,"
                     "toimikausi_loppu"
                     ")values("
                     "'TKUN',"
                     "'nimi',"
                     "'fi',"
                     "2,"
                     "'2013-01-01',"
                     "'2099-01-01'"
                     ")"))
  (lisaa-koulutus-ja-opintoala!)
  (lisaa-tutkinto! {:tutkintotunnus "12345"})
  (lisaa-tutkintoversio! {:tutkintotunnus "12345"
                          :tutkintoversio_id 12345})
  (lisaa-tutkinto! {:tutkintotunnus "23456"})
  (lisaa-tutkintoversio! {:tutkintotunnus "23456"
                          :tutkintoversio_id 23456})
  (lisaa-tutkinto! {:tutkintotunnus "34567"})
  (lisaa-tutkintoversio! {:tutkintotunnus "34567"
                          :tutkintoversio_id 34567}))

(deftest ^:integraatio lisaa-sopimus-test!
  "Testaa että sopimuksen lisääminen onnistuu."
  (lisaa-testidata!)
  (let [jarjestamissopimusid (:jarjestamissopimusid (arkisto/lisaa! (arbitrary-sopimus)))
        sopimus (arkisto/hae jarjestamissopimusid)]
    (is (= (:sopimusnumero sopimus) "1234"))))

(deftest ^:integraatio paivita-sopimus-test-idt-muokattu!
  "Testaa että sopimuksen päivittäminen onnistuu, jos sopimus_ja_tutkinto_id:t ovat sopimukselle kuuluvia. Päivittäminen ei onnistu jos id:t eivät kuulu sopimukselle."
  (lisaa-testidata!)
  (let [jarjestamissopimusid (:jarjestamissopimusid (arkisto/lisaa! (arbitrary-sopimus)))
        _ (arkisto/lisaa-tutkinnot-sopimukselle! jarjestamissopimusid [12345, 23456])
        sopimus (arkisto/hae jarjestamissopimusid)
        paivitettava_sopimus (assoc (arbitrary-sopimus) :jarjestamissopimusid jarjestamissopimusid)]
    (is (thrown? Throwable
          (arkisto/paivita! paivitettava_sopimus [{:sopimus_ja_tutkinto_id 32123} {:sopimus_ja_tutkinto_id 54322}])))
    (arkisto/paivita! paivitettava_sopimus [{:sopimus_ja_tutkinto_id (get-in sopimus [:sopimus_ja_tutkinto 0 :sopimus_ja_tutkinto_id])}
                                            {:sopimus_ja_tutkinto_id (get-in sopimus [:sopimus_ja_tutkinto 1 :sopimus_ja_tutkinto_id])}])))

(deftest ^:integraatio paivita-tutkinnot-test!
  "Testaa tutkintojen lisäyksen sopimukselle"
  (lisaa-testidata!)
  (let [jarjestamissopimusid (:jarjestamissopimusid (arkisto/lisaa! (arbitrary-sopimus)))
        _ (arkisto/lisaa-tutkinnot-sopimukselle! jarjestamissopimusid [12345, 23456])
        _ (arkisto/paivita-tutkinnot! jarjestamissopimusid [{:tutkintoversio_id 34567}])
        sopimus (arkisto/hae jarjestamissopimusid)
        sopimuksen-tutkintojen-lkm (count (:sopimus_ja_tutkinto sopimus))
        sopimuksen-tutkintotunnus (-> sopimus :sopimus_ja_tutkinto (first) :tutkintoversio :tutkintotunnus)]
      (is (= sopimuksen-tutkintojen-lkm 1))
      (is (= sopimuksen-tutkintotunnus "34567"))))

(deftest ^:integraatio lisaa-ja-poista-suunnitelma-sopimuksen-tutkinnolle!
  "Lisää järjestämissuunnitelman sopimuksen tutkinnolle"
  (lisaa-testidata!)
  (let [jarjestamissopimusid (:jarjestamissopimusid (arkisto/lisaa! (arbitrary-sopimus)))
        _ (arkisto/lisaa-tutkinnot-sopimukselle!
            jarjestamissopimusid [12345])]
    (let [suunnitelma-file (new File "test-file.pdf")
          _ (.createNewFile suunnitelma-file)
          suunnitelma {:tempfile suunnitelma-file :filename "test-file.pdf" :content-type "Application/pdf"}
          sopimus-ja-tutkinto-id (-> (arkisto/hae jarjestamissopimusid) :sopimus_ja_tutkinto (first) :sopimus_ja_tutkinto_id)
          _ (arkisto/lisaa-suunnitelma-tutkinnolle! sopimus-ja-tutkinto-id suunnitelma)
          sopimus-ja-tutkinto-suunnitelma (-> (arkisto/hae jarjestamissopimusid) :sopimus_ja_tutkinto (first) (:jarjestamissuunnitelmat) (first))
          sopimus-ja-tutkinto-suunnitelma-id (:jarjestamissuunnitelma_id sopimus-ja-tutkinto-suunnitelma)
          _ (arkisto/poista-suunnitelma! sopimus-ja-tutkinto-suunnitelma-id)
          sopimus-ja-tutkinto-ei-suunnitelmaa (-> (arkisto/hae jarjestamissopimusid) :sopimus_ja_tutkinto (first))
          _ (FileUtils/forceDelete suunnitelma-file)]
      (is (= (:jarjestamissuunnitelma_filename sopimus-ja-tutkinto-suunnitelma) "test-file.pdf"))
      (is (= (count (:jarjestamissuunnitelmat sopimus-ja-tutkinto-ei-suunnitelmaa)) 0)))))

(deftest ^:integraatio uniikki-sopimusnumero-test
  (lisaa-testidata!)
  (let [jarjestamissopimusid (:jarjestamissopimusid (arkisto/lisaa! (arbitrary-sopimus)))]
    (testing "Palauttaa true kun annettu sopimusnumero uniikki"
      (is (arkisto/uniikki-sopimusnumero? "4321" nil)))
    (testing "Palauttaa true kun annettu sopimusnumero on käytössä annetulla jarjestamissopimusid:llä"
      (is (arkisto/uniikki-sopimusnumero? "1234" jarjestamissopimusid)))
    (testing "Palauttaa false kun annettu sopimusnumero ei-uniikki"
      (is (false? (arkisto/uniikki-sopimusnumero? "1234" nil))))))

(deftest ^:integraatio merkitse-poistetuksi-test!
  (lisaa-testidata!)
  (let [jarjestamissopimusid (:jarjestamissopimusid (arkisto/lisaa! (arbitrary-sopimus)))
       jarjestamissopimus (arkisto/hae jarjestamissopimusid)
       _ (arkisto/merkitse-sopimus-poistetuksi! jarjestamissopimusid)
       jarjestamissopimus-poistamisen-jalkeen (arkisto/hae jarjestamissopimusid)]
    (is (not (nil? jarjestamissopimus)))
    (is (nil? jarjestamissopimus-poistamisen-jalkeen))))

(defn sopimuksen-tutkintojen-osaamisalat [sopimus]
  (let [rivit (sql/select sopimus-ja-tutkinto-ja-osaamisala
                (sql/with sopimus-ja-tutkinto
                  (sql/with tutkintoversio))
                (sql/with osaamisala)
                (sql/fields :tutkintoversio.tutkintotunnus
                            :osaamisala.osaamisalatunnus))]
    (reduce (fn [tutkinto->osaamisalat rivi]
              (update-in tutkinto->osaamisalat [(:tutkintotunnus rivi)]
                         (fnil conj #{}) (:osaamisalatunnus rivi)))
            {}
            rivit)))

(defn seq->vec [x]
  (postwalk #(if (seq? %) (vec %) %) x))

(defn hae-taydellinen-sopimus
  "Palauttaa samanlaisen järjestämissopimusta esittävän tietorakenteen, kuin
REST-API tuottaa. Toistaiseksi arkiston funktiot ottavat vastaan tämän
tietorakenteen osia."
  [id]
  (taydenna-sopimus (arkisto/hae-ja-liita-tutkinnonosiin-asti id)))

(deftest ^:integraatio paivita-test
  (testing "paivita!"
    (testing "päivittää osaamisalat, joita sopimus koskee kunkin tutkinnon osalta"
      ;; Oletetaan, että
      (let [sopimus
            (lisaa-osaamisaloja-koskeva-sopimus! {"TU1" #{"OA1" "OA2"}
                                                  "TU2" #{"OA3"}}),
            sopimus-tutkinto-liitokset
            (-> (hae-taydellinen-sopimus (:jarjestamissopimusid sopimus))
              :sopimus_ja_tutkinto
              seq->vec),
            ;; uudet-sopimus-tutkinto-liitokset:ssa opintoalan OA1 on poistettu
            ;; tutkinnosta TU1 ja lisätty tutkintoon TU2
            oa1
            (-> sopimus-tutkinto-liitokset
              (get 0) :sopimus_ja_tutkinto_ja_osaamisala last),
            uudet-sopimus-tutkinto-liitokset
            (-> sopimus-tutkinto-liitokset
              (update-in [0 :sopimus_ja_tutkinto_ja_osaamisala] butlast)
              (update-in [1 :sopimus_ja_tutkinto_ja_osaamisala] conj oa1))]
        ;; Kun
        (arkisto/paivita! sopimus uudet-sopimus-tutkinto-liitokset)
        ;; Niin
        (is (= (sopimuksen-tutkintojen-osaamisalat sopimus)
               {"TU1" #{"OA2"}
                "TU2" #{"OA1" "OA3"}}))))))
