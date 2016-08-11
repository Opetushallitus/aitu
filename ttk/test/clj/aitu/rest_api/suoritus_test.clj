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
  :ehdotusaika nil, :rahoitusmuoto 3, :tutkinto "327128", :koulutustoimija "0208430-8", 
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
            "osaamisen_tunnustaminen" false
            "osaamisala_id" -20002
            "tutkinnonosa_id" -10002}]
   "suorittaja" -1
   "opiskelijavuosi" "8"
   "jarjestamismuoto" "oppisopimuskoulutus"
   "rahoitusmuoto" 3
   "koulutustoimija" "0208430-8"
   "valmistava_koulutus" true
   "paikka" "Yöttäjän harjoitusalue"
   "jarjestelyt" "Valaistus ja veneet olivat riittävät arvoimiseen. Hytisimme uimarannalla yön pimeydessä ja jossain pöllö huhuili haikeasti. "   
   "tutkinto" "327128"})
 
(def suoritus-result
  {:tila "luonnos", :jarjestelyt "Valaistus ja veneet olivat riittävät arvoimiseen. Hytisimme uimarannalla yön pimeydessä ja jossain pöllö huhuili haikeasti. ", 
   :opiskelijavuosi 8, :koulutustoimija_nimi_sv "", 
   :osat [{:osaamisen_tunnustaminen false, :arvosanan_korotus true, :osatunnus "990001", 
           :todistus true, :osaamisala -20002, :kieli "fi", :nimi "Käsityöyrityksen johtaminen", 
           :tutkinnonosa -10002, :suoritus_id 1, :arvosana "hyvaksytty", :suorituskerta 1}], 
   :hyvaksymisaika nil, :tutkinto_nimi_fi "Käsityömestarin erikoisammattitutkinto",  :suorituskerta_id 1, 
   :suorittaja -1, :ehdotusaika nil, :rahoitusmuoto 3, :tutkinto "327128", :valmistava_koulutus true, 
   :koulutustoimija "0208430-8", :suorittaja_sukunimi "Opiskelija", 
   :tutkinto_nimi_sv "Käsityömestarin erikoisammattitutkinto (sv)", 
   :jarjestamismuoto "oppisopimuskoulutus", 
   :koulutustoimija_nimi_fi "Alkio-opiston kannatusyhdistys ry.", 
   :paikka "Yöttäjän harjoitusalue", 
   :suorittaja_etunimi "Orvokki"})

(defn rip-suoritusid [suoritus]
  (let [osat (:osat suoritus)]
    (-> suoritus
      (assoc :osat (map #(dissoc % :suorituskerta :suoritus_id) osat))
      (dissoc :suorituskerta_id))))

(defn rip-skertaid [suorituslista]
  (map #(dissoc % :suorituskerta_id) suorituslista))
  
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
             poisto (mock-request s (str "/api/suoritus/" skerta-id) :delete nil)
             ]
        (is (= '() (body-json (:response ei-suorituksia))))
        (is (= '("Orvokki", "Lieto") (map :etunimi (body-json (:response suorittajat)))))
        (is (= (list suorituslista-result) (rip-skertaid suorituslista-resp)))
        (is (= (rip-suoritusid suoritus-result) (rip-suoritusid suoritus-resp)))
        ))))

(deftest ^:integraatio suoritus-haku
  (let [haku-map {:koulutustoimija "0208430-8"
                  :tutkinto "327128"}
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
