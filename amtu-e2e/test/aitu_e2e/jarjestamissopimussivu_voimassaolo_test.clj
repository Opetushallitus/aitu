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

(ns aitu-e2e.jarjestamissopimussivu-voimassaolo-test
  (:require [clojure.test :refer [deftest is testing]]
            [clj-webdriver.taxi :as w]
            [aitu-e2e.util :refer :all]
            [aitu-e2e.aitu-util :refer :all]
            [aitu-e2e.data-util :refer [with-data
                                        menneisyydessa
                                        tulevaisuudessa
                                        aseta-jarjestamissopimus-paattyneeksi]]))

(defn sopimussivu [jarjestamissopimusid] (str "/fi/#/sopimus/" jarjestamissopimusid "/tiedot"))

(def jarjestamissopimussivu-data
  {:toimikunnat [{:tkunta "TTK1"}]
   :koulutustoimijat [{:ytunnus "0000000-0"}]
   :oppilaitokset [{:oppilaitoskoodi "12345"
                    :koulutustoimija "0000000-0"}]
   :koulutusalat [{:koodi "KA1"}]
   :opintoalat [{:koodi "OA1"
                 :koulutusala "KA1"}]
   :tutkinnot [{:nimi "T1"
                :tutkintotunnus "TU1"
                :opintoala "OA1"
                :tutkintoversio_id 1}]
   :jarjestamissopimukset [{:jarjestamissopimusid 1230
                            :sopimusnumero "123"
                            :toimikunta "TTK1"
                            :sopijatoimikunta "TTK1"
                            :koulutustoimija "0000000-0"
                            :tutkintotilaisuuksista_vastaava_oppilaitos "12345"
                            :alkupvm menneisyydessa}]
   :sopimus_ja_tutkinto [{:jarjestamissopimusid 1230
                          :sopimus_ja_tutkinto [{:tutkintoversio_id 1}]}]})

(defn aseta-toimikunnan-toimikausi-paattyneeksi
  [data]
  (update-in data
             [:toimikunnat 0]
             assoc
             :toimikausi_alku menneisyydessa
             :toimikausi_loppu menneisyydessa))

(defn aseta-tutkinto-paattyneeksi
  [data]
  (update-in data
             [:tutkinnot 0]
             assoc
             :voimassaolon_alkupvm menneisyydessa
             :voimassaolon_loppupvm menneisyydessa
             :siirtymaajan_loppupvm menneisyydessa))

(defn poista-sopimuksen-tutkinnot
  [data]
  (assoc data
         :sopimus_ja_tutkinto []))

(deftest jarjestamissopimussivu-voimassaolo-test
  (testing
    "järjestämissopimussivu: voimassaolo:"
    (with-webdriver
      (testing
        "toimikunta ei ole voimassa:"
        (with-data (aseta-toimikunnan-toimikausi-paattyneeksi jarjestamissopimussivu-data)
          (avaa (sopimussivu 1230))
          (testing
            "sopimus ei ole voimassa"
            (is (= (sivun-otsikko) "JÄRJESTÄMISSOPIMUS (EI VOIMASSA)"))))))
    (with-webdriver
      (testing
        "ei olla sopimuksen voimassaoloajalla:"
        (with-data jarjestamissopimussivu-data
          (aseta-jarjestamissopimus-paattyneeksi
            (get-in jarjestamissopimussivu-data [:jarjestamissopimukset 0]))
          (avaa (sopimussivu 1230))
          (testing
            "sopimus ei ole voimassa"
            (is (= (sivun-otsikko) "JÄRJESTÄMISSOPIMUS (EI VOIMASSA)"))))))
    (with-webdriver
      (testing
        "tutkinto ei ole voimassa:"
        (with-data (aseta-tutkinto-paattyneeksi jarjestamissopimussivu-data)
          (avaa (sopimussivu 1230))
          (testing
            "sopimus ei ole voimassa"
            (is (= (sivun-otsikko) "JÄRJESTÄMISSOPIMUS (EI VOIMASSA)"))))))
    (with-webdriver
      (testing
        "sopimuksella ei ole tutkintoja:"
        (with-data (poista-sopimuksen-tutkinnot jarjestamissopimussivu-data)
          (avaa (sopimussivu 1230))
          (testing
            "sopimus on voimassa"
            (is (= (sivun-otsikko) "JÄRJESTÄMISSOPIMUS"))))))))
