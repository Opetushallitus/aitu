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
  (:require [clojure.test :refer :all]
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
            [aitu.infra.oppilaitos-arkisto :as oppilaitos-arkisto]
            [aitu.auditlog :as auditlog]))

(use-fixtures :each tietokanta-fixture)

(defn arbitrary-sopimus
  "Luo järjestämissopimuksen."
  []
  {:post [(jarjestamissopimus? %)]}
  {:toimikunta "TKUN"
   :sopijatoimikunta "TKUN"
   :koulutustoimija "KT1"
   :tutkintotilaisuuksista_vastaava_oppilaitos "OP1"
   :alkupvm (parse-local-date (formatters :year-month-day) "2013-02-01")
   :loppupvm (parse-local-date (formatters :year-month-day) "2099-02-01")
   :sopimusnumero "1234"})

(defn lisaa-testidata! []
  (sql/exec-raw (str "insert into koulutustoimija("
                     "ytunnus,"
                     "nimi_fi"
                     ")values("
                     "'KT1',"
                     "'Koulutustoimijan nimi'"
                     ")"))
  (sql/exec-raw (str "insert into oppilaitos("
                     "oppilaitoskoodi,"
                     "nimi,"
                     "koulutustoimija"
                     ")values("
                     "'OP1',"
                     "'Oppilaitoksen nimi',"
                     "'KT1'"
                     ")"))
  (sql/exec-raw (str "insert into tutkintotoimikunta("
                     "tkunta,"
                     "nimi_fi,nimi_sv,"
                     "kielisyys,"
                     "toimikausi_id,"
                     "toimikausi_alku,"
                     "toimikausi_loppu"
                     ")values("
                     "'TKUN',"
                     "'nimi','nimi',"
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

(deftest ^:integraatio paivita-tutkinnot!-auditlog-test
  (testing "paivita-tutkinnot! kirjaa sopimuksen ja tutkinnot auditlogiin"
    (lisaa-koulutus-ja-opintoala!)
    (lisaa-tutkinto! {})
    (doseq [id [1 2 3]]
      (lisaa-tutkintoversio! {:tutkintoversio_id id}))
    (lisaa-jarjestamissopimus! {:jarjestamissopimusid 99})
    (let [log (atom [])]
      (with-redefs [auditlog/sopimuksen-tutkinnot-operaatio! #(swap! log conj %&)]
        (arkisto/paivita-tutkinnot! 99 [{:tutkintoversio_id 1}
                                        {:tutkintoversio_id 2}
                                        {:tutkintoversio_id 3}])
        (is (= [:paivitys 99 #{1 2 3}] (first @log)))))))

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
            ;; uudet-sopimus-tutkinto-liitokset:ssa opintoala OA1 on poistettu
            ;; tutkinnosta TU1 ja lisätty tutkintoon TU2
            oa1? #(= (:nimi_fi %) "OA1")
            oa1
            (->> sopimus-tutkinto-liitokset
              (mapcat :sopimus_ja_tutkinto_ja_osaamisala)
              (filter oa1?)
              first),
            uudet-sopimus-tutkinto-liitokset
            (-> sopimus-tutkinto-liitokset
              (update-in [0 :sopimus_ja_tutkinto_ja_osaamisala]
                         (partial remove oa1?))
              (update-in [1 :sopimus_ja_tutkinto_ja_osaamisala] conj oa1))]
        ;; Kun
        (arkisto/paivita! sopimus uudet-sopimus-tutkinto-liitokset)
        ;; Niin
        (is (= (sopimuksen-tutkintojen-osaamisalat sopimus)
               {"TU1" #{"OA2"}
                "TU2" #{"OA1" "OA3"}}))))))

(deftest ^:integraatio lisaa-tutkinnot-sopimukselle!-tutkintoversio-id-test
  (lisaa-koulutus-ja-opintoala!)
  (lisaa-tutkinto! {})
  (doseq [id [1 2 3]]
    (lisaa-tutkintoversio! {:tutkintoversio_id id}))
  (lisaa-jarjestamissopimus! {:jarjestamissopimusid 99})
  (arkisto/lisaa-tutkinnot-sopimukselle! 99 [1 2 3])
  (is (= #{1 2 3} (set (map :tutkintoversio (arkisto/hae-sopimuksen-tutkinnot 99))))))

(deftest ^:integraatio lisaa-tutkinnot-sopimukselle!-tutkintoversio-map-test
  (lisaa-koulutus-ja-opintoala!)
  (lisaa-tutkinto! {})
  (doseq [id [1 2 3]]
    (lisaa-tutkintoversio! {:tutkintoversio_id id}))
  (lisaa-jarjestamissopimus! {:jarjestamissopimusid 99})
  (arkisto/lisaa-tutkinnot-sopimukselle! 99 [{:tutkintoversio 1
                                              :kieli "fi"}
                                             {:tutkintoversio 2
                                              :kieli "2k"}
                                             {:tutkintoversio 3
                                              :kieli "sv"}])
  (is (= #{{:tutkintoversio 1
            :kieli "fi"}
           {:tutkintoversio 2
            :kieli "2k"}
           {:tutkintoversio 3
            :kieli "sv"}}
         (->> (arkisto/hae-sopimuksen-tutkinnot 99)
           (map #(select-keys % [:tutkintoversio :kieli]))
           set))))

(deftest ^:integraatio lisaa-tutkinnot-sopimukselle!-auditlog-test
  (testing "lisaa-tutkinnot-sopimukselle! kirjaa sopimuksen ja tutkinnot auditlogiin"
    (lisaa-koulutus-ja-opintoala!)
    (lisaa-tutkinto! {})
    (doseq [id [1 2 3]]
      (lisaa-tutkintoversio! {:tutkintoversio_id id}))
    (lisaa-jarjestamissopimus! {:jarjestamissopimusid 99})
    (let [log (atom [])]
      (with-redefs [auditlog/sopimuksen-tutkinnot-operaatio! #(swap! log conj %&)]
        (arkisto/lisaa-tutkinnot-sopimukselle! 99 [1 2 3])
        (is (= [[:lisays 99 [1 2 3]]] @log))))))

(deftest ^:integraatio poista-tutkinnot-sopimukselta!-tutkintoversio-id-test
  (lisaa-koulutus-ja-opintoala!)
  (lisaa-tutkinto! {})
  (doseq [id [1 2 3]]
    (lisaa-tutkintoversio! {:tutkintoversio_id id}))
  (lisaa-jarjestamissopimus! {:jarjestamissopimusid 99})
  (arkisto/lisaa-tutkinnot-sopimukselle! 99 [1 2 3])
  (arkisto/poista-tutkinnot-sopimukselta! 99 [2 3])
  (is (= #{1} (set (map :tutkintoversio (arkisto/hae-sopimuksen-tutkinnot 99))))))

(deftest ^:integraatio poista-tutkinnot-sopimukselta!-auditlog-test
  (testing "poista-tutkinnot-sopimukselta! kirjaa sopimuksen ja tutkinnot auditlogiin"
    (lisaa-koulutus-ja-opintoala!)
    (lisaa-tutkinto! {})
    (doseq [id [1 2 3]]
      (lisaa-tutkintoversio! {:tutkintoversio_id id}))
    (lisaa-jarjestamissopimus! {:jarjestamissopimusid 99})
    (arkisto/lisaa-tutkinnot-sopimukselle! 99 [1 2 3])
    (let [log (atom [])]
      (with-redefs [auditlog/sopimuksen-tutkinnot-operaatio! #(swap! log conj %&)]
        (arkisto/poista-tutkinnot-sopimukselta! 99 [1 2 3])
        (is (= [[:poisto 99 [1 2 3]]] @log))))))
