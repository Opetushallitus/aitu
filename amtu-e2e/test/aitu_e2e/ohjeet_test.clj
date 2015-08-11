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

(ns aitu-e2e.ohjeet-test
  (:require [clojure.test :refer [deftest is testing]]
            [clj-webdriver.taxi :as w]
            [aitu-e2e.util :refer :all]
            [aitu-e2e.aitu-util :refer :all]
            [aitu-e2e.data-util :refer [with-cleaned-data]]
            [aitu-e2e.datatehdas :refer [setup-toimikunta]]))

(def ohjeen-teksti-suomeksi "Suomeksi")
(def ohjeen-teksti-ruotsiksi "Ruotsiksi")

(defn toimikuntasivun-kieliversio [diaarinumero kieli] (str "/" kieli "/#/toimikunta/" diaarinumero "/tiedot"))

(defn tallenna-ohje []
  (w/click "button[ng-click=\"tallennaOhje()\"]"))

(defn nakyva-ohjeteksti []
  (map w/text (w/find-elements {:css ".e2e-kappale"})))

(deftest ^:no-ie ohjeen-muokkaus-test
  (with-webdriver
    (with-cleaned-data (setup-toimikunta)
      (avaa (toimikuntasivun-kieliversio "13/04/1" "fi"))
      (testing "Ohjeen muokkaaminen onnistuu ja muokattu teksti näkyy suomeksi"
        (w/click ".ohje .avaa")
        (odota-angular-pyyntoa)
        (w/click {:text "[Muokkaa ohjetta]"})
        (kirjoita-tekstialueelle "ohje.teksti_fi" ohjeen-teksti-suomeksi)
        (kirjoita-tekstialueelle "ohje.teksti_sv" ohjeen-teksti-ruotsiksi)
        (tallenna-ohje)
        (odota-angular-pyyntoa)
        (is (= (nakyva-ohjeteksti) [ohjeen-teksti-suomeksi])))
      (testing "Ruotsinkielinen teksti näkee kun vaihtaa kielen ruotsiksi"
        (avaa (toimikuntasivun-kieliversio "13/04/1" "sv"))
        (w/click ".ohje .avaa")
        (odota-angular-pyyntoa)
        (is (= (nakyva-ohjeteksti) [ohjeen-teksti-ruotsiksi]))))))
