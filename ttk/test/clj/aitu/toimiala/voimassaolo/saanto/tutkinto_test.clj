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

(ns aitu.toimiala.voimassaolo.saanto.tutkinto-test
  (:require [clojure.test :refer [deftest is are testing]]
            [clj-time.core :as time]
            [aitu.toimiala.voimassaolo.saanto.tutkinto :refer :all]
            [oph.common.util.util :refer [time-forever]]))

(deftest tutkinto-voimassa?-test
 (testing
   "onko tutkinto voimassa:"
   (let [menneisyydessa (time/minus (time/today) (time/days 1))
         tanaan (time/today)
         tulevaisuudessa (time/plus (time/today) (time/days 1))]
     (are [kuvaus tutkinto odotettu-tulos] (is (= (tutkinto-voimassa? tutkinto) odotettu-tulos) kuvaus)
          "tutkinnon voimassaolo ei ole alkanut" {:voimassa_alkupvm tulevaisuudessa} false
          "tutkinnon voimassaolo on alkanut tänään" {:voimassa_alkupvm tanaan
                                                     :siirtymaajan_loppupvm tulevaisuudessa} true
          "tutkinnon voimassaolon siirtymäaika ei ole päättynyt" {:voimassa_alkupvm menneisyydessa
                                                                  :siirtymaajan_loppupvm tulevaisuudessa} true
          "tutkinnon voimassaolon siirtymäaika päättyy tänään" {:voimassa_alkupvm menneisyydessa
                                                                :siirtymaajan_loppupvm tanaan} true
          "tutkinnon voimassaolon siirtymäaika on päättynyt" {:voimassa_alkupvm menneisyydessa
                                                              :siirtymaajan_loppupvm menneisyydessa} false))))

(deftest taydenna-siirtymaajan-paattyminen-test
  (testing
    "siirtymäajan päättymisen päivittäminen:"
    (testing
      "tutkinnolla ei ole siirtymäajan loppupäivämäärää:"
      (let [ei-loppupaivamaaraa time-forever
            tutkinto {:siirtymaajan_loppupvm ei-loppupaivamaaraa}]
        (testing
          "ei lisää siirtymäajan päättymispäivää"
          (is (nil? (:siirtymaaika_paattyy (taydenna-siirtymaajan-paattyminen tutkinto)))))))
    (testing
      "tutkinnolla on siirtymäajan loppupäivämäärä:"
      (let [loppupvm (time/date-time 2013 2 1)
            tutkinto {:siirtymaajan_loppupvm loppupvm}]
        (testing
          "lisää tutkinnon siirtymäajan loppupäivämäärän siirtymäajan päättymispäivänä"
          (is (= (:siirtymaaika_paattyy (taydenna-siirtymaajan-paattyminen tutkinto))
                 loppupvm)))))))

(deftest taydenna-tutkinnon-voimassaolo-test
 (testing
   "täydennä tutkinnon voimassaolo:"
   (let [menneisyydessa (time/minus (time/today) (time/days 1))
         tulevaisuudessa (time/plus (time/today) (time/days 1))
         ei-voimassaoleva-tutkinto {:voimassa_alkupvm tulevaisuudessa}
         voimassaoleva-tutkinto {:voimassa_alkupvm menneisyydessa
                                 :siirtymaajan_loppupvm tulevaisuudessa}]
     (testing
       "tutkinto ei ole voimassa:"
       (is (false? (:voimassa (taydenna-tutkinnon-voimassaolo ei-voimassaoleva-tutkinto))) "ei voimassa"))
     (testing
       "tutkinto on voimassa:"
       (is (true? (:voimassa (taydenna-tutkinnon-voimassaolo voimassaoleva-tutkinto))) "on voimassa")))))
