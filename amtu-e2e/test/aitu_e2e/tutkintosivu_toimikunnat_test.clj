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

(ns aitu-e2e.tutkintosivu-toimikunnat-test
  (:require [clojure.test :refer [deftest is testing]]
            [clojure.set :refer [subset?]]
            [clj-webdriver.taxi :as w]
            [aitu-e2e.datatehdas :as dt]
            [aitu-e2e.util :refer :all]
            [aitu-e2e.aitu-util :refer :all]
            [aitu-e2e.data-util :refer [with-data]]))

(defn tutkintosivu [tutkintotunnus] (str "/fi/#/tutkinto/" tutkintotunnus))

(defn nykyisten-toimikuntien-nimet []
  (map w/text (w/find-elements {:css ".e2e-toimikunta-nimi"})))

(defn vanhojen-toimikuntien-sarakkeen-arvot [sarake]
  (map w/text (w/find-elements {:css ".e2e-toimikunta-nimi-vanha"})))

(defn vanhojen-toimikuntien-nimet []
  (vanhojen-toimikuntien-sarakkeen-arvot "toimikunta.nimi"))

(defn vanhojen-toimikuntien-toimikaudet []
  (vanhojen-toimikuntien-sarakkeen-arvot "toimikunta.alkupvm"))

(deftest tutkintosivu-toimikunnat-test
  (with-webdriver
    (testing
      "tutkintosivu:"
      ;; Oletetaan, että
      (let [tutkinto-map (dt/setup-tutkinto-map "TU1" 1)]
        (with-data (dt/merge-datamaps tutkinto-map)
          (testing
            "yksi voimassaoleva tutkintotoimikunta:"
            (let [voimassaoleva-toimikunta (dt/toimikunta-nimella-vastuussa-tutkinnosta "Toimikunta" "TU1")]
              (with-data (dt/merge-datamaps voimassaoleva-toimikunta)
                ;; Kun
                (avaa-uudelleenladaten (tutkintosivu "TU1"))
                ;; Niin
                (testing "pitäisi näyttää tutkinnosta vastaavan toimikunnan nimen"
                  (is (= (nykyisten-toimikuntien-nimet) ["Toimikunta (2016)"])))
                (testing "pitäisi näyttää tyhjä vanhojen toimikuntien lista"
                  (is (= (vanhojen-toimikuntien-nimet) []))))))

          (testing
            "kaksi voimassaolevaa tutkintotoimikuntaa:"
            (let [tk1 (dt/toimikunta-nimella-vastuussa-tutkinnosta "Toimikunta 1" "TU1")
                  tk2 (dt/toimikunta-nimella-vastuussa-tutkinnosta "Toimikunta 2" "TU1")]
                  (with-data (dt/merge-datamaps tk1 tk2)
                    ;; Kun
                    (avaa-uudelleenladaten (tutkintosivu "TU1"))
                    ;; Niin
                    (testing "pitäisi näyttää tutkinnosta vastaavien toimikuntien nimet"
                      (is (= (set (nykyisten-toimikuntien-nimet)) #{"Toimikunta 1 (2016)" "Toimikunta 2 (2016)"})))
                    (testing "pitäisi näyttää tyhjä vanhojen toimikuntien lista"
                      (is (= (vanhojen-toimikuntien-nimet) []))))))

          (testing
            "voimassaoleva ja vanha tutkintotoimikunta:"
            (let [nykyinen-toimikunta (dt/toimikunta-nimella-vastuussa-tutkinnosta "Nykyinen toimikunta" "TU1")
                  vanha-toimikunta (dt/vanha-toimikunta-nimella-vastuussa-tutkinnosta "Vanha toimikunta" "TU1")]
              (with-data (dt/merge-datamaps nykyinen-toimikunta vanha-toimikunta)
                ;; Kun
                (avaa-uudelleenladaten (tutkintosivu "TU1"))
                ;; Niin
                (testing "pitäisi näyttää tutkinnosta vastaavan toimikunnan nimen"
                  (is (= (nykyisten-toimikuntien-nimet) ["Nykyinen toimikunta (2016)"])))
                (testing "pitäisi näyttää vanhan toimikunnan nimen ja toimikauden"
                  (is (= (vanhojen-toimikuntien-nimet) ["Vanha toimikunta (2010)"])))))))))))
