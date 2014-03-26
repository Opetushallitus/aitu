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

(ns aitu-e2e.jarjestamissopimuksen-poistaminen-test
  (:require [clojure.test :refer [deftest is testing]]
            [aitu-e2e.jarjestamissopimussivu-test :refer [sopimussivu jarjestamissopimus-data]]
            [clj-webdriver.taxi :as w]
            [aitu-e2e.util :refer :all]
            [aitu-e2e.data-util :refer [with-data]]
            [aitu-e2e.toimikuntasivu-test :refer [toimikuntasivu]]))

(defn klikkaa-poista-nappia []
  (w/click "button[ng-click=\"poista()\"]"))

(defn avaa-jarjestamissopimuksen-sivu []
  (avaa (sopimussivu "1230")))

(defn odota-poistodialogia []
  (odota-dialogia #"Olet poistamassa"))

(defn nakyvat-sopimukset []
  (map w/text (-> *ng*
                  (.repeater "sopimus in sopimuksetJarjestetty")
                  (.column "sopimus.sopimusnumero")
                  (w/find-elements))))

(deftest sopimuksen-poistaminen-hyvaksy-dialogi-test
  (testing "Järjestämissopimuksen poistaminen onnistuu hyväksymällä dialogi"
    (with-webdriver
      (with-data jarjestamissopimus-data
        (avaa (toimikuntasivu "98/11/543"))
        (testing "Toimikunnan sivulla näkyy alussa oikeat sopimukset."
          (is (= (nakyvat-sopimukset) ["123" "321"])))
        (avaa-jarjestamissopimuksen-sivu)
        (klikkaa-poista-nappia)
        (odota-poistodialogia)
        (hyvaksyn-dialogin)
        (is (= (viestin-teksti) "Järjestämissopimuksen poistaminen onnistui"))
        (testing "Sopimuksen poistamisen jälkeen siirrytään toimikunnan sivulle"
          (is (= (sivun-otsikko) "TESTIALAN TUTKINTOTOIMIKUNTA")))
        (testing "Toimikunnan sivulla ei näy poistettua sopimusta listattuna"
          (is (= (nakyvat-sopimukset) ["321"])))))))

(deftest sopimuksen-poistaminen-peruuta-dialogi-test
  (testing "Järjestämissopimuksen poistaminen ei tapahdu, jos dialogin peruuttaa"
    (with-webdriver
      (with-data jarjestamissopimus-data
        (avaa-jarjestamissopimuksen-sivu)
        (klikkaa-poista-nappia)
        (odota-poistodialogia)
        (peruutan-dialogin)
        (testing "Jäädään järjestämissopimuksen sivulle"
          (is (= (sivun-otsikko) "JÄRJESTÄMISSOPIMUS")))
        (testing "Toimikunnan sivulla näkyy sopimus"
          (avaa (toimikuntasivu "98/11/543"))
          (is (= (nakyvat-sopimukset) ["123" "321"])))))))
