(ns aitu.rest-api.suoritus-test
  (:require [clojure.test :refer :all]
            [peridot.core :as peridot]
            [cheshire.core :as cheshire]
            [oph.korma.korma-auth :as auth]
            [aitu.integraatio.sql.test-data-util :refer :all]
            [aitu.rest-api.session-util :refer :all]))


(def ^:private suoritus-base-osat
  {:arvosana "hyvaksytty"
   :arvosanan_korotus true
   :kieli "fi"
   :todistus true
   :osaamisen_tunnustaminen nil
   :osaamisala_id -20002
   :tutkinnonosa_id -10002})

(def ^:private suoritus-base
  {:osat [suoritus-base-osat]
   :suorittaja -1
   :opiskelijavuosi "8"
   :jarjestamismuoto "oppisopimuskoulutus"
   :rahoitusmuoto 3
   :suoritusaika_alku  "01.09.2016"
   :suoritusaika_loppu  "01.09.2016"
   :arviointikokouksen_pvm "02.09.2016"
   :toimikunta "Lynx lynx"
   :liitetty_pvm nil
   :tutkintoversio_suoritettava -20000
   :tutkintoversio_id -20000
   :arvioijat [{:arvioija_id -1} {:arvioija_id -2}]
   :koulutustoimija "0208430-8"
   :valmistava_koulutus true
   :paikka "Yöttäjän harjoitusalue"
   :jarjestelyt "Valaistus ja veneet olivat riittävät arvioimiseen. Hytisimme uimarannalla yön pimeydessä ja jossain pöllö huhuili haikeasti. "
   :tutkinto "927128"})

(def ^:private suoritus-erikoinen
  (merge suoritus-base {:kouljarjestaja "0159216-7"
                        :liitetty_pvm   "05.05.2014"}))

(def ^:private suoritus-tunnustaminen
  (-> suoritus-base
    (merge {:paikka nil
            :jarjestelyt nil
            :arviointikokouksen_pvm nil
            :suoritusaika_alku nil
            :suoritusaika_loppu nil
            :arvioijat []
            :osat [(merge suoritus-base-osat {:arvosanan_korotus false
                                              :osaamisen_tunnustaminen "01.09.2016"})]})
      (dissoc :liitetty_pvm :tutkintoversio_id)))

(def ^:private suorituslista-result
  (-> suoritus-base
    (merge
      {:tila "luonnos"
       :arvosana "hyvaksytty",
       :ehdotusaika nil
       :hyvaksymisaika nil
       :tutkinnonosa_tutkinnonosa_id -10002
       :opiskelijavuosi 8
       :kouljarjestaja "0208430-8"
       :koulutustoimija_nimi_fi "Alkio-opiston kannatusyhdistys ry."
       :koulutustoimija_nimi_sv ""
       :tutkinto_nimi_fi "Käsityömestarin erikoisammattitutkinto"
       :tutkinto_nimi_sv "Käsityömestarin erikoisammattitutkinto (sv)"
       :tutkinnonosa_nimi_fi "Käsityöyrityksen johtaminen"
       :tutkinnonosa_nimi_sv nil,
       :suoritettavatutkinto_tutkintotunnus "927128"
       :suoritettavatutkinto_nimi_fi "Käsityömestarin erikoisammattitutkinto"
       :suoritettavatutkinto_nimi_sv "Käsityömestarin erikoisammattitutkinto (sv)"
       :suorittaja_suorittaja_id -1
       :suorittaja_etunimi "Orvokki"
       :suorittaja_sukunimi "Opiskelija"
       :suorittaja_syntymapvm "12.12.1912"
       :osaamisala_nimi_fi "Käsityöopettajan osaamisala (keksitty)"
       :osaamisala_nimi_sv nil
       :osaamisala_tunnus "9875"})
    (dissoc :arvioijat :osat)
    ))

(def ^:private suoritus-result-osat
  (-> suoritus-base-osat
    (merge
      {:nimi "Käsityöyrityksen johtaminen"
       :kokotutkinto false
       :osatunnus "990001"
       :osaamisala -20002
       :tutkinnonosa -10002
       :suoritus_id 1
       :suorituskerta 1})
    (dissoc :osaamisala_id :tutkinnonosa_id)))

(def ^:private suoritus-result
  (merge suorituslista-result
    {:suorituskerta_id 1
     :osat [{:osaamisen_tunnustaminen nil
             :arvosanan_korotus true
             :osatunnus "990001"
             :todistus true
             :kokotutkinto false
             :osaamisala -20002
             :kieli "fi"
             :nimi "Käsityöyrityksen johtaminen"
             :tutkinnonosa -10002
             :suoritus_id 1
             :arvosana "hyvaksytty"
             :suorituskerta 1}]
     :arvioijat [{:arvioija_id -1
                  :etunimi "Väinö"
                  :sukunimi "Väinämöinen"
                  :rooli "opettaja"
                  :nayttotutkintomestari false}
                 {:arvioija_id -2
                  :etunimi "Seppo"
                  :sukunimi "Ilmarinen"
                  :rooli "itsenainen"
                  :nayttotutkintomestari true
                  }
                 ]}))

(def ^:private suoritus-result-tunnustaminen
  (merge suoritus-result
    {:paikka nil
     :arviointikokouksen_pvm nil
     :suoritusaika_alku nil
     :suoritusaika_loppu nil
     :osat [(merge suoritus-result-osat {:arvosanan_korotus false
                                         :osaamisen_tunnustaminen "01.09.2016"})]
     :arvioijat []
     :tutkintoversio_id nil
     :jarjestelyt nil}))

(def ^:private suoritus-diff
  {"arvioijat" [{:arvioija_id -2 :etunimi "Seppo" :sukunimi "Ilmarinen" :rooli "itsenainen" :nayttotutkintomestari true}]
   "rahoitusmuoto" 2})

(defn rip-suoritusid [suoritus]
  (let [osat (:osat suoritus)]
    (-> suoritus
      (assoc :osat (map #(dissoc % :suorituskerta :suoritus_id) osat))
      (dissoc :suorituskerta_id))))

(defn rip-skertaid [suorituslista]
  (map #(dissoc % :suorituskerta_id) suorituslista))

(defn base-testi
  "Wrapper happy-case testeille. Callback funktio testifn ottaa parametrina peridot-session handlen ja suorituskerta-id:n"
  ([testifn suoritus-data]
  (with-peridot (fn [crout]
    (run-with-db (constantly true)
      #(let [s (peridot/session crout)
             kirjaa (mock-json-post s "/api/suoritus" (generate-escaped-json-string suoritus-data))
             suorituksia (mock-request s "/api/suoritus" :get {})
             suorituslista-resp (body-json (:response suorituksia))
             skerta-id (some :suorituskerta_id suorituslista-resp)]
         (testifn s skerta-id)
;         (mock-request s (str "/api/suoritus/" skerta-id) :delete nil)
        )))))
  ([testifn]
    (base-testi testifn suoritus-base)))

(deftest ^:integraatio suoritus-tunnustaminen-flow
  (base-testi
    (fn [s skerta-id]
      (let [suoritustiedot (mock-request s (str "/api/suoritus/" skerta-id) :get {})
            suoritus-resp (body-json (:response suoritustiedot))
            ; update
            paivitys-map (-> suoritus-resp (merge suoritus-diff) (assoc :suorituskerta_id skerta-id))
            kirjaa-paivitys (mock-json-post s "/api/suoritus" (generate-escaped-json-string paivitys-map))
            suoritustiedot-paivitys (mock-request s (str "/api/suoritus/" skerta-id) :get {})]
        (is (= (rip-suoritusid suoritus-result-tunnustaminen) (rip-suoritusid suoritus-resp)))
        ))
    suoritus-tunnustaminen))

(deftest ^:integraatio suoritus-flow
  (with-peridot (fn [crout]
    (run-with-db (constantly true)
      #(let [s (peridot/session crout)
             suorittajat (mock-request s "/api/suorittaja" :get {})
             ei-suorituksia (mock-request s "/api/suoritus" :get {})
             kirjaa (mock-json-post s "/api/suoritus" (generate-escaped-json-string suoritus-base))
             suorituksia (mock-request s "/api/suoritus" :get {})
             suorituslista-resp (body-json (:response suorituksia))
             skerta-id (some :suorituskerta_id suorituslista-resp)
             suoritustiedot (mock-request s (str "/api/suoritus/" skerta-id) :get {})
             suoritus-resp (body-json (:response suoritustiedot))
             ; update
             paivitys-map (-> suoritus-resp (merge suoritus-diff) (assoc :suorituskerta_id skerta-id))
             kirjaa-paivitys (mock-json-post s "/api/suoritus" (generate-escaped-json-string paivitys-map))
             suoritustiedot-paivitys (mock-request s (str "/api/suoritus/" skerta-id) :get {})
             poisto (mock-request s (str "/api/suoritus/" skerta-id) :delete nil)
             ]
        (is (= '() (body-json (:response ei-suorituksia))))
        (is (= '("Orvokki", "Lieto") (map :etunimi (body-json (:response suorittajat)))))
        (is (= (list suorituslista-result) (rip-skertaid suorituslista-resp)))
        (is (= (rip-suoritusid suoritus-result) (rip-suoritusid suoritus-resp)))
        )))))

(deftest ^:integraatio suoritus-haku
  (base-testi
    (fn [s skerta-id]
      (let [haku-map {:koulutustoimija "0208430-8"
                      :tutkinto "-20000"}
            suorituksia (mock-request s "/api/suoritus" :get haku-map)
            suorituslista-resp (body-json (:response suorituksia))
            ei-suorituksia (mock-request s "/api/suoritus" :get (assoc haku-map :rahoitusmuoto 4))]
        (is (empty? (body-json (:response ei-suorituksia))))
        (is (= (list suorituslista-result) (rip-skertaid suorituslista-resp)))
        ))))

(deftest ^:integraatio suoritus-haku-suoritettava-tutkinto
  (let [suoritus-data (assoc suoritus-base :tutkintoversio_suoritettava -10000)]
    (base-testi
      (fn [s skerta-id]
        (let [haku-map {:koulutustoimija "0208430-8"
                        :suoritettavatutkinto "-10000"}
              suorituksia (mock-request s "/api/suoritus" :get haku-map)
              suorituslista-resp (body-json (:response suorituksia))
              ei-suorituksia (mock-request s "/api/suoritus" :get (assoc haku-map :tutkinto "-10000"))]
          (is (= '() (body-json (:response ei-suorituksia))))
          (is (= (count suorituslista-resp) 1))
          ))
      suoritus-data)))

(deftest ^:integraatio suoritus-haku-suorittajalla
  (base-testi
    (fn [s skerta-id]
      (let [haku-vals ["Orvok" "kelija" "Orvokki", "fan.far.12345"]
            haku-notfound [ "Jörmungandr" "fan.far.1"]]

        (testing "testaan opiskelijalla hakua erilaisilla kriteereillä.."
          (doseq [crit haku-vals]
            (let [suorituslista-resp  (body-json (:response (mock-request s "/api/suoritus" :get {:suorittaja crit})))]
              (is (= (list suorituslista-result) (rip-skertaid suorituslista-resp)))
              )))
        (testing "testataan opiskelijalla hakua"
          (doseq [crit haku-notfound]
            (let [ei-suorituksia  (mock-request s "/api/suoritus" :get {:suorittaja crit})]
              (is (= '() (body-json (:response ei-suorituksia))))
              )))))))

(deftest ^:integraatio testaa-tilasiirtymat
  (base-testi
    (fn [s skerta-id]
      (let [hyvaksy-req {:hyvaksymispvm "2016-11-16"
                         :suoritukset [skerta-id]}
            hyv-resp (mock-json-post s "/api/suoritus/hyvaksy" (generate-escaped-json-string hyvaksy-req))

            hyv-json (body-json (:response hyv-resp))]
        (is (= (:tila hyv-json) "hyvaksytty"))
        (is (= (:hyvaksymisaika hyv-json) "16.11.2016"))
        ))))

(deftest ^:integraatio testaa-tilat
  (base-testi
    (fn [s skerta-id]
      (let [hyvaksy-req {:hyvaksymispvm "2016-11-16"
                         :suoritukset [skerta-id]}
            suoritustiedot (mock-request s (str "/api/suoritus/" skerta-id) :get {})
            suoritus-resp (body-json (:response suoritustiedot))
            paivitys-map (assoc (merge suoritus-resp suoritus-diff) :suorituskerta_id  skerta-id )
            hyv-resp (mock-json-post s "/api/suoritus/hyvaksy" (generate-escaped-json-string hyvaksy-req))

            ; hyväksytty-tilassa olevaa ei pitäisi voida muokata
            kirjaa-paivitys (mock-json-post s "/api/suoritus" (generate-escaped-json-string paivitys-map))
            hyv-resp2 (mock-json-post s "/api/suoritus/hyvaksy" (generate-escaped-json-string hyvaksy-req))
            hyv-json  (:response hyv-resp2)]
        (is (= 403 (:status hyv-json)))
        (is (= 403 (:status (:response hyv-resp2))))
        ))))

(deftest ^:integraatio suoritus-liitetty-sopimus
  (base-testi
    (fn [s skerta-id]
      (let [suoritustiedot (mock-request s (str "/api/suoritus/" skerta-id) :get {})
            suoritus-resp (body-json (:response suoritustiedot))]
        (is (= "0159216-7" (:kouljarjestaja suoritus-resp)))
        (is (= "05.05.2014" (:liitetty_pvm suoritus-resp)))
        ))
    suoritus-erikoinen))
