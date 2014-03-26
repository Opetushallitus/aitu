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

(ns aitu.infra.validaatio-test
  (:require [clojure.test :refer [deftest testing is are]]
            [clj-time.core :as time]
            [aitu.infra.validaatio :refer :all]))

(deftest validoi-alkupvm-sama-tai-ennen-loppupvm-test
  (testing
    "validoi että alkupäivämäärä on sama tai ennen loppupäivämäärää:"
    (let [menneisyydessa (time/minus (time/today) (time/days 1))
          tanaan (time/today)
          tulevaisuudessa (time/plus (time/today) (time/days 1))]
      (are [kuvaus alkupvm loppupvm odotettu-tulos]
           (is (= ((validoi-alkupvm-sama-tai-ennen-loppupvm loppupvm) alkupvm) odotettu-tulos) kuvaus)
           "annettu loppupäivämäärä: on alkupäivämäärän jälkeen" tanaan tulevaisuudessa true
           "annettu loppupäivämäärä: on sama kuin alkupäivämäärä" tanaan tanaan true
           "annettu loppupäivämäärä: on ennen alkupäivämäärää" tanaan menneisyydessa false
           "annettu loppupäivämäärä: ei annettua alkupäivämäärää" nil menneisyydessa false
           "ei annettua loppupäivämäärää: on validi alkupäivämäärästä riippumatta" tulevaisuudessa nil true
           "ei annettua loppupäivämäärää: ei annettua alkupäivämäärää" nil nil true))))
