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

(ns aitu-e2e.jarjestamissopimukset-lista-test
  (:require [clojure.test :refer [deftest is testing]]
            [clj-webdriver.taxi :as w]
            [aitu-e2e.util :refer :all]
            [aitu-e2e.aitu-util :refer :all]
            [aitu-e2e.data-util :refer [with-data
                                        menneisyydessa
                                        tulevaisuudessa]]
            [aitu-e2e.tutkintosivu-test :refer [tutkintosivu]]
            [aitu-e2e.datatehdas :as dt]
            [aitu-e2e.toimikuntasivu-test :refer [toimikuntasivu]]))

(def perustiedot {:toimikunnat [(dt/toimikunta-diaarinumerolla "98/11/543" "ILMA")]
                  :koulutustoimijat [{:ytunnus "0000000-0"
                                      :nimi_fi "Ankkalinnan kaupunki"}]
                  :oppilaitokset [{:oppilaitoskoodi "12345"
                                   :koulutustoimija "0000000-0"
                                   :nimi "Ankkalinnan aikuiskoulutuskeskus"}]
                  :koulutusalat [{:nimi "Tekniikan ja liikenteen ala"
                                  :koodi "KA1"}]
                  :opintoalat [{:nimi "Sähköala"
                                :koodi "OA1"
                                :koulutusala "KA1"}]
                  :tutkinnot [{:nimi "Ilmastointialan tutkinto"
                               :tutkintotunnus "TU1"
                               :opintoala "OA1"
                               :tutkintoversio_id 1}]
                  :jarjestamissopimukset [{:sopimusnumero "123"
                                           :jarjestamissopimusid 1230
                                           :toimikunta "ILMA"
                                           :sopijatoimikunta "ILMA"
                                           :koulutustoimija "0000000-0"
                                           :tutkintotilaisuuksista_vastaava_oppilaitos "12345"}]
                  :sopimus_ja_tutkinto [{:jarjestamissopimusid 1230
                                         :sopimus_ja_tutkinto [{:tutkintoversio_id 1
                                                                :alkupvm menneisyydessa
                                                                :loppupvm tulevaisuudessa}]}]})

(defn perustiedot-vanhalla-tutkinnolla []
  (update-in perustiedot
    [:tutkinnot 0]
    merge {
    :voimassa_alkupvm menneisyydessa
    :voimassa_loppupvm menneisyydessa
    :siirtymaajan_loppupvm menneisyydessa}))

(defn jarjestamissopimukset []
  (map w/text (w/find-elements (-> *ng*
                                 (.repeater "sopimus in sopimuksetJarjestetty")
                                 (.column "sopimus.sopimusnumero")))))

(def vanhojen-sopimusten-lista-selector ".vanhat-sopimukset>div")

(defn nykyisten-jarjestamissopimusten-lkm []
  (count (w/find-elements {:css ".nykyiset-sopimukset  tbody tr"})))

(defn vanhojen-jarjestamissopimusten-lkm []
  (count (w/find-elements {:css (str vanhojen-sopimusten-lista-selector " tbody tr")})))

(defn testaa [testattava-sivu testattava-sivu-fn]
  (testing testattava-sivu
    (with-webdriver
      (let [vanhentuva-sopimus (dt/setup-voimassaoleva-jarjestamissopimus "1234" "0000000-0" "12345"  "ILMA" 1)]
        (with-data (assoc-in (dt/merge-datamaps perustiedot vanhentuva-sopimus) [:sopimus_ja_tutkinto 0 :sopimus_ja_tutkinto 0 :loppupvm] menneisyydessa)
          (testing "kun vanhoja järjestämissopimuksia löytyy"
            (avaa (testattava-sivu-fn))
            (testing "Pitäisi näyttää järjestämissopimukset omissa taulukoissaan"
              (is (= (nykyisten-jarjestamissopimusten-lkm) 1))
              (is (= (vanhojen-jarjestamissopimusten-lkm) 1)))))))))

(defn testaa-vanha-toimikunta[]
  (testing "Vanhan toimikunnan sivu"

    (with-webdriver
      (let [vanha-sopimus (dt/setup-lakannut-jarjestamissopimus "1234" "0000000-0" "12345"  "ILMA" 1)]
        (with-data (dt/merge-datamaps vanha-sopimus
                     (update-in perustiedot [:toimikunnat 0] merge {:toimikausi 1
                                                                    :toimikausi_alku "2010-08-01"
                                                                    :toimikausi_loppu "2013-07-31"}))
          (avaa (toimikuntasivu "98/11/543"))
          (testing "pitäisi näyttää kaikki sopimukset nykyisten sopimusten listalla"
            (is (= (nykyisten-jarjestamissopimusten-lkm) 2))
            (is (= (vanhojen-jarjestamissopimusten-lkm) 0))
            (is (not (w/visible? (w/find-element {:css vanhojen-sopimusten-lista-selector}))))))))))

(deftest jarjestamissopimukset-lista-test
  (testaa "Tutkintosivu:" (fn[] (tutkintosivu "TU1")))
  (testaa "Toimikuntasivu:" (fn[] (toimikuntasivu "98/11/543")))
  (testaa-vanha-toimikunta))

(deftest tutkintojen-voimassaolon-vaikutus-toimikunnan-jarjestamissopimuksen-voimassaoloon-test
  (testing
    "tutkintojen voimassaolon vaikutus toimikunnan järjestämissopimuksen voimassaoloon:"
    (with-webdriver
      (testing
        "tutkinnon siirtymäaika on käynnissä:"
        (with-data (update-in perustiedot
                     [:tutkinnot 0]
                     merge {:voimassa_alkupvm menneisyydessa
                            :voimassa_loppupvm menneisyydessa
                            :siirtymaajan_loppupvm tulevaisuudessa})
          (avaa (toimikuntasivu "98/11/543"))
          (testing
            "pitäisi näyttää järjestämissopimus nykyisten sopimusten listassa"
            (is (= (nykyisten-jarjestamissopimusten-lkm) 1))
            (is (= (vanhojen-jarjestamissopimusten-lkm) 0))))))
    (with-webdriver
      (testing
        "tutkinto on vanhentunut:"
        (with-data (perustiedot-vanhalla-tutkinnolla)
          (avaa (toimikuntasivu "98/11/543"))
          (testing
            "pitäisi näyttää järjestämissopimus entisten sopimusten listassa"
            (is (= (nykyisten-jarjestamissopimusten-lkm) 0))
            (is (= (vanhojen-jarjestamissopimusten-lkm) 1))))))
    (with-webdriver
      (testing
        "jokin tutkinto on vielä voimassa:"
        (let [pt (perustiedot-vanhalla-tutkinnolla)
              sopimus-id (:jarjestamissopimusid (first (:sopimus_ja_tutkinto perustiedot)))]
          (with-data (dt/merge-datamaps pt {
                       :tutkinnot {:nimi "tutkinto 2"
                                   :tutkintotunnus "TU2"
                                   :opintoala "OA1"
                                   :tutkintoversio_id 2
                                   :voimassa_alkupvm menneisyydessa
                                   :voimassa_loppupvm menneisyydessa
                                   :siirtymaajan_loppupvm tulevaisuudessa}
                       :sopimus_ja_tutkinto  {:jarjestamissopimusid sopimus-id, :sopimus_ja_tutkinto [{:tutkintoversio_id 2}]}})
            (avaa (toimikuntasivu "98/11/543"))
            (testing
              "pitäisi näyttää järjestämissopimus nykyisten sopimusten listassa"
              (is (= (nykyisten-jarjestamissopimusten-lkm) 1))
              (is (= (vanhojen-jarjestamissopimusten-lkm) 0)))))))))

(deftest tutkintosivu-tutkinto-ei-ole-voimassa-jarjestamissopimusten-naytto-test
  (testing
    "tutkintosivu:"
    (testing
      "tutkinto ei ole voimassa:"
      (with-webdriver
        (let [vanhentuva-sopimus (dt/setup-voimassaoleva-jarjestamissopimus "1234" "0000000-0" "12345"  "ILMA" 1)]
          (with-data (assoc-in (dt/merge-datamaps (perustiedot-vanhalla-tutkinnolla) vanhentuva-sopimus) [:sopimus_ja_tutkinto 0 :sopimus_ja_tutkinto 0 :loppupvm] menneisyydessa)
            (avaa (tutkintosivu "TU1"))
            (testing "pitäisi näyttää kaikki sopimukset nykyisten sopimusten listalla"
              (is (= (nykyisten-jarjestamissopimusten-lkm) 2))
              (is (= (vanhojen-jarjestamissopimusten-lkm) 0))
              (is (not (w/visible? (w/find-element {:css vanhojen-sopimusten-lista-selector})))))))))))
