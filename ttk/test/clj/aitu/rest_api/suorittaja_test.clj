(ns aitu.rest-api.suorittaja-test
  (:require
    [clojure.test :refer :all]
    [peridot.core :as peridot]
    [cheshire.core :as cheshire]
    [oph.korma.korma-auth :as auth]
    [aitu.integraatio.sql.test-data-util :refer :all]
    [aitu.rest-api.session-util :refer :all]))

;GET 
; http://192.168.50.1:8080/api/suorittaja
;[{"suorittaja_id":-1,"etunimi":"Orvokki","sukunimi":"Opiskelija","hetu":null,"oid":"fan.far.12345","muutettuaika":"2016-08-09T10:17:52Z","muutettu_nimi":"Järjestelmä "},{"suorittaja_id":-2,"etunimi":"Lieto","sukunimi":"Lemminkäinen","hetu":null,"oid":"pfft.12345","muutettuaika":"2016-08-09T10:17:52Z","muutettu_nimi":"Järjestelmä "}]

(def suorittajat-alussa
  [{:suorittaja_id -1,
    :etunimi "Orvokki"
    :sukunimi "Opiskelija"
    :hetu nil
    :rahoitusmuoto_nimi "valtionosuus"
    :oid "fan.far.12345"
    :rahoitusmuoto_id 1
    ;"muutettuaika":"2016-08-09T10:17:52Z","muutettu_nimi":"Järjestelmä "}
    }
    {:suorittaja_id -2
     :etunimi "Lieto"
     :sukunimi "Lemminkäinen"
     :hetu nil
     :rahoitusmuoto_nimi "oppisopimus"
     :oid "pfft.12345"
     :rahoitusmuoto_id 2
     ;muutettuaika":"2016-08-09T10:17:52Z","muutettu_nimi":"Järjestelmä "}]
     }])



;PUT
; http://192.168.50.1:8080/api/suorittaja/-1
;{"suorittaja_id":-1,"etunimi":"Orvokki","sukunimi":"Opiskelija","hetu":null,"oid":"fan.far.12345","muutettuaika":"2016-08-09T10:17:52Z","muutettu_nimi":"Järjestelmä "}

;POST
; http://192.168.50.1:8080/api/suorittaja
(def uusi-suorittaja
  {"etunimi" "Eero"
   "sukunimi" "Jukola"
   "hetu" "121212-999R"
   "rahoitusmuoto_id" 3})

(def uusi-suorittaja-viallinen-hetu
  {"etunimi" "Eero"
   "sukunimi" "Jukola"
   "hetu" "987654-999X"
   "rahoitusmuoto_id" 3})

(defn rip-muutettukentat [s]
  (map #(dissoc % :muutettuaika :muutettu_nimi) s))

(deftest ^:integraatio suorittaja-flow
  (let [crout (init-peridot!)]
    (run-with-db (constantly true)
      #(let [s (peridot/session crout)
             suorittajat (mock-request s "/api/suorittaja" :get {})
             kirjaa (mock-json-post s "/api/suorittaja" (cheshire/generate-string uusi-suorittaja))
             kirjaus-respo (body-json (:response kirjaa))
             uusi-suorittaja-id (:suorittaja_id kirjaus-respo)
             poisto (mock-request s (str "/api/suorittaja/" uusi-suorittaja-id) :delete nil)
             ]
         (is (= suorittajat-alussa (rip-muutettukentat (body-json (:response suorittajat)))))
         (is (= "121212-999R" (:hetu kirjaus-respo)))
        ))))

(deftest ^:integraatio suorittaja-viallinen-hetu
  (testing "suorittajan syöttämisessä hetu-tarkistus toimii ja palauttaa virheilmoituksen"
    (let [crout (init-peridot!)]
      (run-with-db (constantly true)
        #(let [s (peridot/session crout)
               kirjaa (mock-json-post s "/api/suorittaja" (cheshire/generate-string uusi-suorittaja-viallinen-hetu))
               kirjaus-respo (:response kirjaa)
               ]
           (println "-... " kirjaus-respo)
           (is (= 400 (:status kirjaus-respo)))
           (is (= "{\"errors\":[\"hetu\",\"Viallinen henkilötunnus\"]}" (:body kirjaus-respo))))))))
;           (println kirjaus-respo))))))