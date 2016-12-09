(ns aitu.rest-api.suoritus-test
  (:require
    [clojure.test :refer :all]
    [peridot.core :as peridot]
    [cheshire.core :as cheshire]
    [oph.korma.korma-auth :as auth]
    [aitu.integraatio.sql.test-data-util :refer :all]
    [aitu.rest-api.session-util :refer :all]))


(def suorituslista-result
  {:tila "luonnos", :opiskelijavuosi 8, :koulutustoimija_nimi_sv "", :hyvaksymisaika nil, 
  :tutkinto_nimi_fi "Käsityömestarin erikoisammattitutkinto", :suorittaja -1, 
  :ehdotusaika nil, :rahoitusmuoto 3, :tutkinto "927128", :koulutustoimija "0208430-8", :kouljarjestaja "0208430-8"
  :arviointikokouksen_pvm "2016-09-02"
  :suoritusaika_alku "2016-09-01"
  :toimikunta "Lynx lynx"
  :tutkinnonosa_nimi_fi "Käsityöyrityksen johtaminen"
  :tutkinnonosa_nimi_sv nil
  :tutkinnonosa_tutkinnonosa_id -10002
  :osaamisala_tunnus "9875" 
  :osaamisala_nimi_fi "Käsityöopettajan osaamisala (keksitty)"
  :osaamisala_nimi_sv nil
  :arvosana "hyvaksytty"
  :liitetty_pvm nil
  :tutkintoversio_suoritettava -20000
  :tutkintoversio_id nil
  :suoritusaika_loppu "2016-09-01"
  :suorittaja_sukunimi "Opiskelija", :tutkinto_nimi_sv "Käsityömestarin erikoisammattitutkinto (sv)", 
  :suorittaja_syntymapvm "1912-12-12"
  :suorittaja_suorittaja_id -1
  :jarjestamismuoto "oppisopimuskoulutus", :koulutustoimija_nimi_fi "Alkio-opiston kannatusyhdistys ry.",
  :valmistava_koulutus true
  :paikka "Yöttäjän harjoitusalue"
  :jarjestelyt "Valaistus ja veneet olivat riittävät arvoimiseen. Hytisimme uimarannalla yön pimeydessä ja jossain pöllö huhuili haikeasti. "
  :suorittaja_etunimi "Orvokki"})
 

(def suoritus-base
  {"osat" [{"arvosana" "hyvaksytty"
            "arvosanan_korotus" true
            "kieli" "fi"
            "todistus" true
            "osaamisen_tunnustaminen" nil
            "osaamisala_id" -20002
            "tutkinnonosa_id" -10002}]
   "suorittaja" -1
   "opiskelijavuosi" "8"
   "jarjestamismuoto" "oppisopimuskoulutus"
   "rahoitusmuoto" 3
   "suoritusaika_alku"  "01.09.2016"
   "suoritusaika_loppu"  "01.09.2016"
   :arviointikokouksen_pvm "02.09.2016"
   :toimikunta "Lynx lynx"
   :liitetty_pvm nil
   :tutkintoversio_suoritettava -20000
   :arvioijat [{:arvioija_id -1}]
   "koulutustoimija" "0208430-8"
   "valmistava_koulutus" true
   "paikka" "Yöttäjän harjoitusalue"
   "jarjestelyt" "Valaistus ja veneet olivat riittävät arvoimiseen. Hytisimme uimarannalla yön pimeydessä ja jossain pöllö huhuili haikeasti. "   
   "tutkinto" "927128"})

(def suoritus-erikoinen
  (-> suoritus-base
    (assoc :kouljarjestaja "0159216-7")
    (assoc :liitetty_pvm "05.05.2014")))    
           

(def suoritus-tunnustaminen
  {"osat" [{"arvosana" "hyvaksytty"
            "arvosanan_korotus" false
            "kieli" "fi"
            "todistus" true
            "osaamisen_tunnustaminen" "01.09.2016"
            "osaamisala_id" -20002
            "tutkinnonosa_id" -10002}]
   "suorittaja" -1
   "opiskelijavuosi" "8"
   "jarjestamismuoto" "oppisopimuskoulutus"
   "rahoitusmuoto" 3
   "suoritusaika_alku" nil
   "suoritusaika_loppu" nil
   :tutkintoversio_suoritettava -20000
   :arviointikokouksen_pvm nil
   :toimikunta "Lynx lynx"
   :arvioijat []
   "koulutustoimija" "0208430-8"
   "valmistava_koulutus" true
   "paikka" nil
   "jarjestelyt" nil  
   "tutkinto" "927128"})

(def suoritus-result-tunnustaminen
  {:tila "luonnos", :jarjestelyt nil
   :opiskelijavuosi 8, :koulutustoimija_nimi_sv "", 
   :suoritusaika_alku nil
   :suoritusaika_loppu nil
   :osat [{:osaamisen_tunnustaminen "2016-09-01", :kokotutkinto false, :arvosanan_korotus false, :osatunnus "990001", 
           :todistus true, :osaamisala -20002, :kieli "fi", :nimi "Käsityöyrityksen johtaminen", 
           :tutkinnonosa -10002, :suoritus_id 1, :arvosana "hyvaksytty", :suorituskerta 1}], 
   :arvioijat []
   :hyvaksymisaika nil, :tutkinto_nimi_fi "Käsityömestarin erikoisammattitutkinto",  :suorituskerta_id 1, 
   :suorittaja -1, :ehdotusaika nil, :rahoitusmuoto 3, :tutkinto "927128", :valmistava_koulutus true, 
   :koulutustoimija "0208430-8", :kouljarjestaja "0208430-8", :arviointikokouksen_pvm nil
   :toimikunta "Lynx lynx"
   :suorittaja_sukunimi "Opiskelija", 
   :suorittaja_syntymapvm "1912-12-12"
   :suorittaja_suorittaja_id -1
   :tutkinto_nimi_sv "Käsityömestarin erikoisammattitutkinto (sv)", 
   :jarjestamismuoto "oppisopimuskoulutus", 
   :koulutustoimija_nimi_fi "Alkio-opiston kannatusyhdistys ry.", 
   :paikka nil,
   :arvosana "hyvaksytty"
   :liitetty_pvm nil
   :tutkintoversio_suoritettava -20000
   :tutkintoversio_id nil
   :tutkinnonosa_nimi_fi "Käsityöyrityksen johtaminen"
   :tutkinnonosa_nimi_sv nil
   :tutkinnonosa_tutkinnonosa_id -10002
   :osaamisala_tunnus "9875" 
   :osaamisala_nimi_fi "Käsityöopettajan osaamisala (keksitty)"
   :osaamisala_nimi_sv nil
   :suorittaja_etunimi "Orvokki"})

(def suoritus-diff 
  {"arvioijat" [{:arvioija_id -2 :etunimi "Seppo" :sukunimi "Ilmarinen" :rooli "itsenainen" :nayttotutkintomestari true}]
   "rahoitusmuoto" 2})
 
(def suoritus-result
  {:tila "luonnos", :jarjestelyt "Valaistus ja veneet olivat riittävät arvoimiseen. Hytisimme uimarannalla yön pimeydessä ja jossain pöllö huhuili haikeasti. ", 
   :opiskelijavuosi 8, :koulutustoimija_nimi_sv "", 
   :suoritusaika_alku "2016-09-01"
   :suoritusaika_loppu "2016-09-01"   
   :osat [{:osaamisen_tunnustaminen nil, :kokotutkinto false, :arvosanan_korotus true, :osatunnus "990001", 
           :todistus true, :osaamisala -20002, :kieli "fi", :nimi "Käsityöyrityksen johtaminen", 
           :tutkinnonosa -10002, :suoritus_id 1, :arvosana "hyvaksytty", :suorituskerta 1}], 
   :arvioijat [{:arvioija_id -1 :etunimi "Väinö" :sukunimi "Väinämöinen" :rooli "opettaja" :nayttotutkintomestari false}]
   :hyvaksymisaika nil, :tutkinto_nimi_fi "Käsityömestarin erikoisammattitutkinto",  :suorituskerta_id 1, 
   :suorittaja -1, :ehdotusaika nil, :rahoitusmuoto 3, :tutkinto "927128", :valmistava_koulutus true, 
   :koulutustoimija "0208430-8", :kouljarjestaja "0208430-8", :arviointikokouksen_pvm "2016-09-02"
   :toimikunta "Lynx lynx"
   :suorittaja_sukunimi "Opiskelija", 
   :suorittaja_suorittaja_id -1,
   :tutkinto_nimi_sv "Käsityömestarin erikoisammattitutkinto (sv)", 
   :jarjestamismuoto "oppisopimuskoulutus", 
   :koulutustoimija_nimi_fi "Alkio-opiston kannatusyhdistys ry.", 
   :paikka "Yöttäjän harjoitusalue",
   :arvosana "hyvaksytty"
   :tutkinnonosa_nimi_fi "Käsityöyrityksen johtaminen"
   :tutkinnonosa_nimi_sv nil
   :tutkinnonosa_tutkinnonosa_id -10002
   :liitetty_pvm nil
   :tutkintoversio_suoritettava -20000
   :tutkintoversio_id nil
   :osaamisala_tunnus "9875"
   :osaamisala_nimi_fi "Käsityöopettajan osaamisala (keksitty)"
   :osaamisala_nimi_sv nil  
   :suorittaja_syntymapvm "1912-12-12"
   :suorittaja_etunimi "Orvokki"})

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
             kirjaa (mock-json-post s "/api/suoritus" (cheshire/generate-string suoritus-data))
             suorituksia (mock-request s "/api/suoritus" :get {})
             suorituslista-resp (body-json (:response suorituksia))
             skerta-id (some :suorituskerta_id suorituslista-resp)]
         (testifn s skerta-id)
         (mock-request s (str "/api/suoritus/" skerta-id) :delete nil)
        )))))
  ([testifn]
    (base-testi testifn suoritus-base)))

(deftest ^:integraatio suoritus-tunnustaminen-flow
  (base-testi (fn [s skerta-id]
                (let [suoritustiedot (mock-request s (str "/api/suoritus/" skerta-id) :get {})
                      suoritus-resp (body-json (:response suoritustiedot))
                      ; update
                      paivitys-map (assoc (merge suoritus-resp suoritus-diff) :suorituskerta_id  skerta-id )
                      kirjaa-paivitys (mock-json-post s "/api/suoritus" (cheshire/generate-string paivitys-map))
                      suoritustiedot-paivitys (mock-request s (str "/api/suoritus/" skerta-id) :get {})]
                  (is (= (rip-suoritusid suoritus-result-tunnustaminen) (rip-suoritusid suoritus-resp)))
                  )) suoritus-tunnustaminen))

(deftest ^:integraatio suoritus-flow
  (with-peridot (fn [crout]
    (run-with-db (constantly true)
      #(let [s (peridot/session crout)
             suorittajat (mock-request s "/api/suorittaja" :get {})
             ei-suorituksia (mock-request s "/api/suoritus" :get {})
             kirjaa (mock-json-post s "/api/suoritus" (cheshire/generate-string suoritus-base))
             suorituksia (mock-request s "/api/suoritus" :get {})
             suorituslista-resp (body-json (:response suorituksia))
             skerta-id (some :suorituskerta_id suorituslista-resp)
             suoritustiedot (mock-request s (str "/api/suoritus/" skerta-id) :get {})
             suoritus-resp (body-json (:response suoritustiedot))
             ; update
             paivitys-map (assoc (merge suoritus-resp suoritus-diff) :suorituskerta_id  skerta-id )
             kirjaa-paivitys (mock-json-post s "/api/suoritus" (cheshire/generate-string paivitys-map))
             suoritustiedot-paivitys (mock-request s (str "/api/suoritus/" skerta-id) :get {})
             poisto (mock-request s (str "/api/suoritus/" skerta-id) :delete nil)
             ]
        (is (= '() (body-json (:response ei-suorituksia))))
        (is (= '("Orvokki", "Lieto") (map :etunimi (body-json (:response suorittajat)))))
        (is (= (list suorituslista-result) (rip-skertaid suorituslista-resp)))
        (is (= (rip-suoritusid suoritus-result) (rip-suoritusid suoritus-resp)))
        )))))

(deftest ^:integraatio suoritus-haku
  (base-testi (fn [s skerta-id]
    (let [haku-map {:koulutustoimija "0208430-8"
                    :tutkinto "927128"}
          suorituksia (mock-request s "/api/suoritus" :get haku-map)
          suorituslista-resp (body-json (:response suorituksia))
          ei-suorituksia (mock-request s "/api/suoritus" :get (assoc haku-map :rahoitusmuoto 4))]
      (is (= '() (body-json (:response ei-suorituksia))))
      (is (= (list suorituslista-result) (rip-skertaid suorituslista-resp)))
      ))))

(deftest ^:integraatio suoritus-haku-suorittajalla
  (base-testi (fn [s skerta-id]
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
  (base-testi (fn [s skerta-id]
                (let [hyvaksy-req {:hyvaksymispvm "2016-11-16"
                                   :suoritukset [skerta-id]}
                      hyv-resp (mock-json-post s "/api/suoritus/hyvaksy" (cheshire/generate-string hyvaksy-req))
                      hyv-json (body-json (:response hyv-resp))]
                  (is (= (:tila hyv-json) "hyvaksytty"))
                  (is (= (:hyvaksymisaika hyv-json) "2016-11-16"))
                  ))))

(deftest ^:integraatio suoritus-liitetty-sopimus
  (base-testi (fn [s skerta-id]
                (let [suoritustiedot (mock-request s (str "/api/suoritus/" skerta-id) :get {})
                      suoritus-resp (body-json (:response suoritustiedot))]
                  (is (= "0159216-7" (:kouljarjestaja suoritus-resp)))
                  (is (= "2014-05-05" (:liitetty_pvm suoritus-resp)))
        )) suoritus-erikoinen))
