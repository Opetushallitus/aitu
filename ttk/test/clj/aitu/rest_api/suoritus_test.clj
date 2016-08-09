(ns aitu.rest-api.suoritus-test
  (:require
    [clojure.test :refer :all]
    [peridot.core :as peridot]
    [cheshire.core :as cheshire]
    [oph.korma.korma-auth :as auth]
    [aitu.integraatio.sql.test-data-util :refer :all]
    [aitu.rest-api.session-util :refer :all]))
 
;{"osat":[{"arvosana":"hyvaksytty","korotus":false,"kieli":"fi","todistus":false,"tunnustaminen":false,
;          "tutkinnonosa_id":-10003,"suoritus_id":27},{"arvosana":"hyvaksytty","korotus":false,"kieli":"fi",
;                                                      "todistus":false,"tunnustaminen":false,"tutkinnonosa_id":-10001,
;                                                      "suoritus_id":28}],"rahoitusmuoto":2,"suorittaja":-2,
; "koulutustoimija":"2059910-2","opiskelijavuosi":"2","jarjestamismuoto":"oppisopimuskoulutus","tutkinto":"327128",
; "suorituskerta_id":16}

;{"osat":[{"arvosana":"hyvaksytty","korotus":false,"kieli":"fi","todistus":false,"tunnustaminen":false,
;          "tutkinnonosa_id":-10002}],"suorittaja":-1,"opiskelijavuosi":"8","jarjestamismuoto":"oppisopimuskoulutus",
; "rahoitusmuoto":3,"koulutustoimija":"0208430-8","tutkinto":"327128"}

(def suoritus-base
  {"osat" [{"arvosana" "hyvaksytty"
            "korotus" false
            "kieli" "fi"
            "todistus" false
            "tunnustaminen" false
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


; {:tila luonnos, :jarjestelyt Valaistus ja veneet olivat riittävät arvoimiseen. Hytisimme uimarannalla yön pimeydessä ja jossain pöllö huhuili haikeasti. , :opiskelijavuosi 8, :koulutustoimija_nimi_sv , :osat [{:osaamisen_tunnustaminen false, :arvosanan_korotus false, :osatunnus 990001, :todistus false, :osaamisala -20002, :kieli fi, :nimi Käsityöyrityksen johtaminen, :tutkinnonosa -10002, :suoritus_id 2, :arvosana hyvaksytty, :suorituskerta 2}], :hyvaksymisaika nil, :tutkinto_nimi_fi Käsityömestarin erikoisammattitutkinto, :suorituskerta_id 2, :suorittaja -1, :ehdotusaika nil, :rahoitusmuoto 3, :tutkinto 327128, :valmistava_koulutus true, :koulutustoimija 0208430-8, :suorittaja_sukunimi Opiskelija, :tutkinto_nimi_sv Käsityömestarin erikoisammattitutkinto (sv), :jarjestamismuoto oppisopimuskoulutus, :koulutustoimija_nimi_fi Alkio-opiston kannatusyhdistys ry., :paikka Yöttäjän harjoitusalue, :suorittaja_etunimi Orvokki}
 
(def suoritus-result
  {:tila "luonnos", :jarjestelyt "Valaistus ja veneet olivat riittävät arvoimiseen. Hytisimme uimarannalla yön pimeydessä ja jossain pöllö huhuili haikeasti. ", 
   :opiskelijavuosi 8, :koulutustoimija_nimi_sv "", 
   :osat [{:osaamisen_tunnustaminen false, :arvosanan_korotus false, :osatunnus "990001", 
           :todistus false, :osaamisala -20002, :kieli "fi", :nimi "Käsityöyrityksen johtaminen", 
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
