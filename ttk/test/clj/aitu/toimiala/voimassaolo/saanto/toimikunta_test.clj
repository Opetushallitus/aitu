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

(ns aitu.toimiala.voimassaolo.saanto.toimikunta-test
    (:require [clojure.test :refer [deftest is are testing]]
              [clj-time.core :as time]
              [aitu.toimiala.voimassaolo.saanto.toimikunta :refer :all]
              [aitu.test-timeutil :refer :all]))

(deftest toimikunta-voimassa?-test
 (testing
   "toimikunta voimassa?:"
   (are [kuvaus toimikunta odotettu-tulos]
        (is (= (toimikunta-voimassa? toimikunta) odotettu-tulos) kuvaus)
        "ei ole alkanut" {:toimikausi_alku tulevaisuudessa
                          :toimikausi_loppu tulevaisuudessa} false
        "on alkanut tänään" {:toimikausi_alku tanaan
                             :toimikausi_loppu tulevaisuudessa} true
        "loppupäivä on tulevaisuudessa" {:toimikausi_alku menneisyydessa
                                         :toimikausi_loppu tulevaisuudessa} true
        "loppupäivä on tänään" {:toimikausi_alku menneisyydessa
                                :toimikausi_loppu tanaan} true
        "loppupäivä on menneisyydessä" {:toimikausi_alku menneisyydessa
                                        :toimikausi_loppu menneisyydessa} false)))
