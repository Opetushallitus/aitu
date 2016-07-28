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
            "tutkinnonosa_id" -10002}]
   "suorittaja" -1
   "opiskelijavuosi" "8"
   "jarjestamismuoto" "oppisopimuskoulutus"
   "rahoitusmuoto" 3
   "koulutustoimija" "0208430-8"
   "tutkinto" "327128"})

(def suoritus-result
  {:tila "luonnos", :opiskelijavuosi 8, :koulutustoimija_nimi_sv "", :hyvaksymisaika nil, 
  :tutkinto_nimi_fi "Käsityömestarin erikoisammattitutkinto", :suorittaja -1, 
  :ehdotusaika nil, :rahoitusmuoto 3, :tutkinto "327128", :koulutustoimija "0208430-8", 
  :suorittaja_sukunimi "Opiskelija", :tutkinto_nimi_sv "Käsityömestarin erikoisammattitutkinto (sv)", 
  :jarjestamismuoto "oppisopimuskoulutus", :koulutustoimija_nimi_fi "Alkio-opiston kannatusyhdistys ry.", 
  :suorittaja_etunimi "Orvokki"})
   

(deftest ^:integraatio suoritus-flow
  (let [crout (init-peridot!)
        skerta (fn [suoritus] (dissoc suoritus :suorituskerta_id))]
    (run-with-db (constantly true)
      #(let [s (peridot/session crout)
             suorittajat (mock-request s "/api/suorittaja" :get {})
             ei-suorituksia (mock-request s "/api/suoritus" :get {})
             kirjaa (mock-json-post s "/api/suoritus" (cheshire/generate-string suoritus-base))
             suorituksia (mock-request s "/api/suoritus" :get {})
             suoritus-resp (body-json (:response suorituksia))
             ]
        (is (= '() (body-json (:response ei-suorituksia))))
        (is (= '("Orvokki", "Lieto") (map :etunimi (body-json (:response suorittajat)))))
        (is (= (list suoritus-result) (map skerta suoritus-resp)))
        ))))
