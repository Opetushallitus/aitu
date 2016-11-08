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
  :ehdotusaika nil, :rahoitusmuoto 3, :tutkinto "927128", :koulutustoimija "0208430-8",
  :arviointikokouksen_pvm "2016-09-02"
  :suoritusaika_alku "2016-09-01"
  :toimikunta "Lynx lynx"
  :arvosana "hyvaksytty"
  :suoritusaika_loppu "2016-09-01"
  :suorittaja_sukunimi "Opiskelija", :tutkinto_nimi_sv "Käsityömestarin erikoisammattitutkinto (sv)", 
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
   :arvioijat [{:arvioija_id -1}]
   "koulutustoimija" "0208430-8"
   "valmistava_koulutus" true
   "paikka" "Yöttäjän harjoitusalue"
   "jarjestelyt" "Valaistus ja veneet olivat riittävät arvoimiseen. Hytisimme uimarannalla yön pimeydessä ja jossain pöllö huhuili haikeasti. "   
   "tutkinto" "927128"})

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
   :koulutustoimija "0208430-8", :arviointikokouksen_pvm nil
   :toimikunta "Lynx lynx"
   :suorittaja_sukunimi "Opiskelija", 
   :tutkinto_nimi_sv "Käsityömestarin erikoisammattitutkinto (sv)", 
   :jarjestamismuoto "oppisopimuskoulutus", 
   :koulutustoimija_nimi_fi "Alkio-opiston kannatusyhdistys ry.", 
   :paikka nil,
   :arvosana "hyvaksytty"
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
   :koulutustoimija "0208430-8", :arviointikokouksen_pvm "2016-09-02"
   :toimikunta "Lynx lynx"
   :suorittaja_sukunimi "Opiskelija", 
   :tutkinto_nimi_sv "Käsityömestarin erikoisammattitutkinto (sv)", 
   :jarjestamismuoto "oppisopimuskoulutus", 
   :koulutustoimija_nimi_fi "Alkio-opiston kannatusyhdistys ry.", 
   :paikka "Yöttäjän harjoitusalue",
   :arvosana "hyvaksytty"
   :suorittaja_etunimi "Orvokki"})

(defn rip-suoritusid [suoritus]
  (let [osat (:osat suoritus)]
    (-> suoritus
      (assoc :osat (map #(dissoc % :suorituskerta :suoritus_id) osat))
      (dissoc :suorituskerta_id))))

(defn rip-skertaid [suorituslista]
  (map #(dissoc % :suorituskerta_id) suorituslista))
  
(deftest ^:integraatio suoritus-tunnustaminen-flow
  (let [crout (init-peridot!)]
    (run-with-db (constantly true)
      #(let [s (peridot/session crout)
             kirjaa (mock-json-post s "/api/suoritus" (cheshire/generate-string suoritus-tunnustaminen))
             suorituksia (mock-request s "/api/suoritus" :get {})
             suorituslista-resp (body-json (:response suorituksia))
             skerta-id (some :suorituskerta_id suorituslista-resp)
             suoritustiedot (mock-request s (str "/api/suoritus/" skerta-id) :get {})
             suoritus-resp (body-json (:response suoritustiedot))
             _ (println suoritus-resp)
             ; update
             paivitys-map (assoc (merge suoritus-resp suoritus-diff) :suorituskerta_id  skerta-id )
             kirjaa-paivitys (mock-json-post s "/api/suoritus" (cheshire/generate-string paivitys-map))
             suoritustiedot-paivitys (mock-request s (str "/api/suoritus/" skerta-id) :get {})
             poisto (mock-request s (str "/api/suoritus/" skerta-id) :delete nil)
             ]
        (is (= (rip-suoritusid suoritus-result-tunnustaminen) (rip-suoritusid suoritus-resp)))
        ))))

(deftest ^:integraatio suoritus-flow
  (let [crout (init-peridot!)]
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
        ))))

(deftest ^:integraatio suoritus-haku
  (let [haku-map {:koulutustoimija "0208430-8"
                  :tutkinto "927128"}
        crout (init-peridot!)]
    (run-with-db (constantly true)
      #(let [s (peridot/session crout)
             kirjaa (mock-json-post s "/api/suoritus" (cheshire/generate-string suoritus-base))
             suorituksia (mock-request s "/api/suoritus" :get haku-map)
             suorituslista-resp (body-json (:response suorituksia))
             skerta-id (some :suorituskerta_id suorituslista-resp)
             ei-suorituksia (mock-request s "/api/suoritus" :get (assoc haku-map :rahoitusmuoto 4))
             poisto (mock-request s (str "/api/suoritus/" skerta-id) :delete nil)
             ]
        (is (= '() (body-json (:response ei-suorituksia))))
        (is (= (list suorituslista-result) (rip-skertaid suorituslista-resp)))
        ))))
  
(deftest ^:integraatio suoritus-haku-suorittajalla
  (let [haku-vals ["Orvok" "kelija" "Orvokki", "fan.far.12345"]
        haku-notfound [ "Jörmungandr" "fan.far.1"]
        crout (init-peridot!)]
    (run-with-db (constantly true)
      #(let [s (peridot/session crout)
             kirjaa (mock-json-post s "/api/suoritus" (cheshire/generate-string suoritus-base))
             suorituksia (mock-request s "/api/suoritus" :get {})
             suorituslista-resp (body-json (:response suorituksia))
             skerta-id (some :suorituskerta_id suorituslista-resp)]
         (testing "testaan opiskelijalla hakua erilaisilla kriteereillä.." 
                  (doseq [crit haku-vals]
                    (let [suorituslista-resp  (body-json (:response (mock-request s "/api/suoritus" :get {:suorittaja crit})))]
                      (is (= (list suorituslista-result) (rip-skertaid suorituslista-resp)))                      
                    )))
         (testing "testataan opiskelijalla hakua"
                  (doseq [crit haku-notfound]
                    (let [ei-suorituksia  (mock-request s "/api/suoritus" :get {:suorittaja crit})]
                      (is (= '() (body-json (:response ei-suorituksia))))
                    )))
           
           (mock-request s (str "/api/suoritus/" skerta-id) :delete nil)
        ))))

