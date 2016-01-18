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

(ns aitu.integraatio.sql.test-data-util
  (:require [aitu.infra.ttk-arkisto :as toimikunta-arkisto]
            [aitu.infra.henkilo-arkisto :as henkilo-arkisto]
            [aitu.infra.tutkinto-arkisto :as tutkinto-arkisto]
            [aitu.infra.ttk-arkisto :as ttk-arkisto]
            [aitu.infra.oppilaitos-arkisto :as oppilaitos-arkisto]
            [aitu.infra.koulutustoimija-arkisto :as koulutustoimija-arkisto]
            [aitu.infra.jarjestamissopimus-arkisto :as jarjestamissopimus-arkisto]
            [aitu.toimiala.tutkinto :refer [tutkinto? tutkintoversio?]]
            [aitu.toimiala.skeema :refer [SisaltaaToimikunnanTiedot HenkilonTiedot]]
            [aitu.toimiala.henkilo :as henkilo]
            [clj-time.core :as time]
            [korma.core :as sql]
            [schema.core :as s]
            [aitu.test-timeutil :refer :all]
            [aitu.integraatio.sql.korma
             :refer [osaamisala toimikausi]]))

(defn default-tutkinto []
  {:post [(tutkinto? %)]}
  {:tutkintotunnus "123456"
   :nimi_fi "Autoalan perustutkinto"
   :opintoala "OAK"
   :tyyppi "02"
   :tutkintotaso "perustutkinto"})

(defn default-tutkintoversio []
  {:post [(tutkintoversio? %)]}
  {:tutkintotunnus "123456"
   :versio 1
   :koodistoversio 1
   :voimassa_alkupvm (time/now)
   :voimassa_loppupvm (time/now)})

(defn lisaa-tutkinto! [tutkinto]
  (tutkinto-arkisto/lisaa! (merge (default-tutkinto) tutkinto)))

(defn lisaa-tutkintoversio! [versio]
  (tutkinto-arkisto/lisaa-tutkintoversio! (merge (default-tutkintoversio)
                                                 versio)))

(def default-koulutusala
  {:koulutusalakoodi "KAK"
   :selite_fi "koulutusala"})

(def default-opintoala
  {:opintoalakoodi "OAK"
   :selite_fi "opintoala"})

(defn lisaa-koulutus-ja-opintoala!
  ([] (lisaa-koulutus-ja-opintoala! default-koulutusala default-opintoala))
  ([koulutusala opintoala]
    (let [koulutusala (merge default-koulutusala koulutusala)
          opintoala (merge default-opintoala opintoala)]

      (sql/exec-raw [(str "insert into koulutusala("
                     "koulutusala_tkkoodi,"
                     "selite_fi,"
                     "voimassa_alkupvm"
                     ")values("
                     "?,"
                     "?,"
                     "'2013-01-01'"
                     ")")
                    (map koulutusala [:koulutusalakoodi :selite_fi])])
      (sql/exec-raw [(str "insert into opintoala("
                          "opintoala_tkkoodi,"
                          "koulutusala_tkkoodi,"
                          "selite_fi,"
                          "voimassa_alkupvm"
                          ")values("
                          "?,"
                          "?,"
                          "?,"
                          "'2013-01-01'"
                          ")")
                     [(:opintoalakoodi opintoala)
                      (:koulutusalakoodi koulutusala)
                      (:selite_fi opintoala)]]))))

(let [seuraava-indeksi (atom 0)]
  (defn default-toimikunta []
    (let [i (swap! seuraava-indeksi inc)]
      {:tkunta (str i)
       :diaarinumero (str i)
       :nimi_fi "Testitoimikunnan nimi"
       :nimi_sv "Testitoimikunnan nimi (sv)"
       :tilikoodi (str i)
       :toimiala "toimiala"
       :toimikausi_id 2
       :kielisyys "fi"
       :sahkoposti "toimikunta@email.fi"
       :toimikausi_alku (kuukausi-sitten)
       :toimikausi_loppu (vuoden-kuluttua)})))

(s/defn lisaa-toimikunta! :- SisaltaaToimikunnanTiedot
  ([] (lisaa-toimikunta! nil))
  ([toimikunta]
    (doto (merge (default-toimikunta) toimikunta)
      toimikunta-arkisto/lisaa!)))

(def default-henkilo {:etunimi "Taimi"
                      :sukunimi "Pilvilinna"
                      :organisaatio "organisaatio"
                      :aidinkieli "fi"
                      :sukupuoli "mies"
                      :sahkoposti "sahkoposti"
                      :puhelin "puhelin"
                      :puhelin_julkinen false
                      :lisatiedot ""
                      :postitoimipaikka "Tampere"
                      :jarjesto nil
                      :sahkoposti_julkinen false
                      :osoite_julkinen false
                      :osoite "on"
                      :nayttomestari false
                      :postinumero "33720"
                      })
(s/validate HenkilonTiedot default-henkilo)

(defn lisaa-henkilo!
  ([] (lisaa-henkilo! {}))
  ([henkilo]
    (-> (merge default-henkilo henkilo)
      henkilo-arkisto/lisaa!)))

(def default-jasen
  {:henkiloid -1234
   :toimikunta "T12345"
   :rooli "jasen"
   :edustus "opettaja"
   :alkupvm (kuukausi-sitten)
   :loppupvm (vuoden-kuluttua)})

(defn lisaa-jasen!
  [jasen]
  (-> (merge default-jasen jasen)
    toimikunta-arkisto/lisaa-jasen!))

(defn lisaa-jarjesto!
  [jarjesto]
  (toimikunta-arkisto/lisaa-jarjesto! jarjesto))

(let [seuraava-indeksi (atom 0)]
  (defn default-koulutustoimija []
    (let [i (swap! seuraava-indeksi inc)]
      {:ytunnus (str i)
       :nimi_fi "Testikoulutustoimijan nimi"
       :nimi_sv "Testikoulutustoimijan nimi (sv)"
       :sahkoposti "koulutustoimija@email.fi"})))

(defn lisaa-koulutustoimija!
  ([] (lisaa-koulutustoimija! nil))
  ([koulutustoimija]
    (doto (merge (default-koulutustoimija) koulutustoimija)
      koulutustoimija-arkisto/lisaa!)))

(let [seuraava-indeksi (atom 0)]
  (defn default-oppilaitos []
    (let [i (swap! seuraava-indeksi inc)]
      {:oppilaitoskoodi (str i)
       :nimi "Testioppilaitoksen nimi"
       :sahkoposti "oppilaitos@email.fi"})))

(defn lisaa-oppilaitos! [oppilaitos]
  (doto (merge (default-oppilaitos) oppilaitos)
    oppilaitos-arkisto/lisaa!))

(let [seuraava-indeksi (atom 0)]
  (defn default-jarjestamissopimus []
    (let [i (swap! seuraava-indeksi inc)]
      {:jarjestamissopimusid i
       :sopimusnumero (str "ABCDEF01234567890" i)
       :alkupvm (time/date-time 2011 1 1)
       :voimassa true})))

(defn lisaa-jarjestamissopimus!
  ([koulutustoimija oppilaitos toimikunta jarjestamissopimus]
    (jarjestamissopimus-arkisto/lisaa!
      (merge (default-jarjestamissopimus)
             {:koulutustoimija (:ytunnus koulutustoimija)
              :tutkintotilaisuuksista_vastaava_oppilaitos (:oppilaitoskoodi oppilaitos)
              :toimikunta (:tkunta toimikunta)
              :sopijatoimikunta (:tkunta toimikunta)}
             jarjestamissopimus)))
  ([koulutustoimija oppilaitos jarjestamissopimus]
    (lisaa-jarjestamissopimus! koulutustoimija oppilaitos (lisaa-toimikunta!) jarjestamissopimus))
  ([koulutustoimija oppilaitos]
    (lisaa-jarjestamissopimus! koulutustoimija oppilaitos {}))
  ([jarjestamissopimus]
    (let [koulutustoimija (lisaa-koulutustoimija!)
          oppilaitos (lisaa-oppilaitos! {:koulutustoimija (:ytunnus koulutustoimija)})]
      (lisaa-jarjestamissopimus! koulutustoimija oppilaitos jarjestamissopimus)))
  ([]
    (lisaa-jarjestamissopimus! {})))

(defn lisaa-osaamisala! [osaamisalatunnus]
  (sql/insert osaamisala
    (sql/values {:nimi_fi osaamisalatunnus
                 :osaamisalatunnus osaamisalatunnus
                 :voimassa_alkupvm (time/date-time 2011 1 1)
                 :versio 1})))

(defn lisaa-toimipaikka! [oppilaitos]
  (oppilaitos-arkisto/lisaa-toimipaikka!
    {:toimipaikkakoodi "T123456"
     :oppilaitos (:oppilaitoskoodi oppilaitos)
     :nimi "Toimipaikka"}))

(defn lisaa-tutkinto-ja-versio! [tutkintotunnus]
  (lisaa-tutkinto! {:tutkintotunnus tutkintotunnus})
  (:tutkintoversio_id (lisaa-tutkintoversio! {:tutkintotunnus tutkintotunnus})))

(defn lisaa-tutkinto-sopimukselle! [sopimus tutkintoversio-id]
  (first (jarjestamissopimus-arkisto/lisaa-tutkinnot-sopimukselle!
           (:jarjestamissopimusid sopimus) [tutkintoversio-id])))

(defn lisaa-osaamisala-sopimus-tutkinto-liitokseen [sopimus-tutkinto-liitos osaamisala-id toimipaikkakoodi]
  (update-in sopimus-tutkinto-liitos [:sopimus_ja_tutkinto_ja_osaamisala]
             conj {:osaamisala_id osaamisala-id
                   :toimipaikka toimipaikkakoodi}))

(defn lisaa-osaamisaloja-koskeva-sopimus! [tutkinto->osaamisalat]
  (lisaa-koulutus-ja-opintoala!) ; lisaa-tutkinto! tarvitsee tämän
  (let [koulutustoimija (lisaa-koulutustoimija!)
        oppilaitos (lisaa-oppilaitos! {:koulutustoimija (:ytunnus koulutustoimija)})
        sopimus (lisaa-jarjestamissopimus! koulutustoimija oppilaitos)
        {:keys [toimipaikkakoodi]} (lisaa-toimipaikka! oppilaitos)
        sopimus-tutkinto-liitokset
        (for [[tutkintotunnus osaamisalat] tutkinto->osaamisalat]
          (let [tutkintoversio-id (lisaa-tutkinto-ja-versio! tutkintotunnus)
                sopimus-tutkinto-liitos (lisaa-tutkinto-sopimukselle! sopimus tutkintoversio-id)
                osaamisala-idt (doall (map (comp :osaamisala_id lisaa-osaamisala!) osaamisalat))]
            (reduce #(lisaa-osaamisala-sopimus-tutkinto-liitokseen %1 %2 toimipaikkakoodi)
                    sopimus-tutkinto-liitos
                    osaamisala-idt)))]
    (jarjestamissopimus-arkisto/paivita! sopimus sopimus-tutkinto-liitokset)
    sopimus))

(defn hae-vanha-toimikausi
  []
  (let [id (:toimikausi_id (first (filter #(false? (:voimassa %)) (ttk-arkisto/hae-toimikaudet))))]
  id))

(defn hae-voimassaoleva-toimikausi
  []
  (let [id (:toimikausi_id (first (filter #(true? (:voimassa %)) (ttk-arkisto/hae-toimikaudet))))]
  id))

(def default-toimikausi (let [seuraava-indeksi (atom 0)]
                          (fn []
                            {:toimikausi_id (+ 1000 (swap! seuraava-indeksi inc))
                             :alkupvm (time/local-date 2011 1 1)
                             :loppupvm (time/local-date 2211 1 1)
                             :voimassa true})))

(defn lisaa-toimikausi! [t]
  (sql/insert toimikausi (sql/values (merge (default-toimikausi) t))))
