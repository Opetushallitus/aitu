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
            [aitu.infra.oppilaitos-arkisto :as oppilaitos-arkisto]
            [aitu.infra.jarjestamissopimus-arkisto :as jarjestamissopimus-arkisto]
            [aitu.toimiala.tutkinto :refer [tutkinto? tutkintoversio?]]
            [aitu.toimiala.skeema :refer [SisaltaaToimikunnanTiedot HenkilonTiedot]]
            [aitu.toimiala.henkilo :as henkilo]
            [clj-time.core :as time]
            [korma.core :as sql]
            [schema.core :as s]
            [schema.macros :as sm]
            [aitu.test-timeutil :refer :all]
            [aitu.integraatio.sql.korma
             :refer [osaamisala sopimus-ja-tutkinto]]))

(defn default-tutkinto []
  {:post [(tutkinto? %)]}
  {:tutkintotunnus "123456"
   :nimi_fi "Autoalan perustutkinto"
   :opintoala "OAK"})

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

(def default-toimikunta
  {:tkunta "T12345"
   :diaarinumero "2013/01/001"
   :nimi_fi "Testitoimikunnan nimi"
   :nimi_sv "Testitoimikunnan nimi (sv)"
   :tilikoodi "1234"
   :toimiala "toimiala"
   :toimikausi_id 2
   :kielisyys "fi"
   :sahkoposti "toimikunta@email.fi"
   :toimikausi_alku (kuukausi-sitten)
   :toimikausi_loppu (vuoden-kuluttua)})

(sm/defn lisaa-toimikunta! :- SisaltaaToimikunnanTiedot
  ([] (lisaa-toimikunta! nil))
  ([toimikunta]
    (doto (merge default-toimikunta toimikunta)
      toimikunta-arkisto/lisaa!)))

(def default-henkilo {:etunimi "Taimi"
                      :sukunimi "Pilvilinna"
                      :organisaatio "organisaatio"
                      :aidinkieli "fi"
                      :sukupuoli "mies"
                      :sahkoposti "sahkoposti"
                      :puhelin "puhelin"
                      :puhelin_julkinen nil
                      :lisatiedot nil
                      :postitoimipaikka nil
                      :jarjesto nil
                      :sahkoposti_julkinen nil
                      :osoite_julkinen nil
                      :osoite nil
                      :nayttomestari nil
                      :postinumero nil})
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

(defn lisaa-oppilaitos! []
  (let [oppilaitos {:oppilaitoskoodi "ABC12"
                    :nimi "Oppilaitos"}]
    (oppilaitos-arkisto/lisaa! oppilaitos)
    oppilaitos))

(defn lisaa-jarjestamissopimus!
  ([oppilaitos]
    (let [toimikunta (lisaa-toimikunta!)]
      (jarjestamissopimus-arkisto/lisaa!
        {:jarjestamissopimusid 1
         :sopimusnumero "ABCDEF01234567890"
         :alkupvm (time/date-time 2011 1 1)
         :oppilaitos (:oppilaitoskoodi oppilaitos)
         :toimikunta (:tkunta toimikunta)
         :sopijatoimikunta (:tkunta toimikunta)})))
  ([]
    (lisaa-jarjestamissopimus! (lisaa-oppilaitos!))))

(defn lisaa-osaamisala! [osaamisalatunnus]
  (sql/insert osaamisala
    (sql/values (merge {:nimi osaamisalatunnus
                        :osaamisalatunnus osaamisalatunnus
                        :voimassa_alkupvm (time/date-time 2011 1 1)
                        :versio 1}))))

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
  (let [oppilaitos (lisaa-oppilaitos!)
        sopimus (lisaa-jarjestamissopimus! oppilaitos)
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
