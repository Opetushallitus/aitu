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

(ns aitu.infra.suoritus-excel-test
  (:require [clojure.test :refer [deftest testing is use-fixtures ]]
            [aitu.infra.suoritus-excel :refer :all]
            [aitu.infra.suorittaja-arkisto :as suorittaja-arkisto]
            [aitu.infra.suoritus-arkisto :as suoritus-arkisto]            
            [aitu.integraatio.sql.test-util :refer :all]))

(use-fixtures :each tietokanta-fixture)

; TODO: ei ole tosiasiallisesti integraatio-testi, mutta sekaantuu niihin Traviksella ja fixture aiheuttaa ongelmia siellä.
(deftest ^:integraatio parse-osatunnus-toimii
  (is (= "1234" (parse-osatunnus "fo (1234)")))
  (is (= "12" (parse-osatunnus "kung (fu) (12)")))
  (is (thrown? IllegalArgumentException (parse-osatunnus "Kreegah bundolo")))
  (is (thrown? IllegalArgumentException (parse-osatunnus "Fo (12"))))

(deftest ^:integraatio opiskelija-duplikaattien-tarkistus
  (let [kaikki (suorittaja-arkisto/hae-kaikki)]
    (is (true? (opiskelija-olemassa? {:etunimi "Orvokki" :sukunimi "Opiskelija" :hetu nil :oid "fan.far.12345"} kaikki)))
    (is (false? (opiskelija-olemassa? {:etunimi "Orvokki" :sukunimi "Opiskelija" :hetu nil :oid "dadaa"} kaikki)))
    (is (thrown? IllegalArgumentException (opiskelija-olemassa? {:etunimi "Hanhikki" :sukunimi "Opiskelija" :hetu nil :oid "fan.far.12345"}))
        )))

(def suoritus
  (let [suorituskerta-map {:suorittaja -1
                           :rahoitusmuoto 1 
                           :tutkinto "327128"
                           :opiskelijavuosi 1  
                           :koulutustoimija "1060155-5";  
                           :jarjestamismuoto "oppilaitosmuotoinen"  
                           }
        suoritus-map {:suorittaja_id -1
                      :arvosana 3
                      :todistus false
                      :tutkinnonosa -10003
                      :arvosanan_korotus false  
                      :osaamisen_tunnustaminen false  
                      :kieli "fi"  
                      }]
    (merge suorituskerta-map
           {:osat [suoritus-map]})))

;(deftest ^:integraatio suoritus-duplikaattien-tarkistus
;  (let [l (suoritus-arkisto/lisaa! suoritus)
;        kaikki (suoritus-arkisto/hae-suoritukset)]
;     (println kaikki)))
    

                 