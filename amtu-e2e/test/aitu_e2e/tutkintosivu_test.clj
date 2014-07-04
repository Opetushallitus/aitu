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

(ns aitu-e2e.tutkintosivu-test
  (:require [clojure.test :refer [deftest is testing]]
            [clj-webdriver.taxi :as w]
            [aitu-e2e.util :refer :all]
            [aitu-e2e.aitu-util :refer :all]
            [aitu-e2e.data-util :refer [with-data]]
            [aitu-e2e.datatehdas :as dt]))

(defn tutkintosivu [tutkintotunnus] (str "/fi/#/tutkinto/" tutkintotunnus))

(defn jarjestamissopimukset []
  (map w/text (w/find-elements-under ".nykyiset-sopimukset"
                                     (-> *ng*
                                       (.repeater "sopimus in sopimuksetJarjestetty")
                                       (.column "sopimus.sopimusnumero")))))

(defn sopimuksen-tutkinnonperusteet []
  (map w/text (w/find-elements-under ".nykyiset-sopimukset td[ng-show=\"naytaPerusteSarake\"]"
                                     (-> *ng*
                                       (.repeater "sopimusJaTutkinto in sopimus.sopimus_ja_tutkinto")
                                       (.column "sopimusJaTutkinto.tutkintoversio.peruste")))))

(deftest tutkintosivu-test
  (testing "tutkintosivu"
    (with-webdriver
      ;; Oletetaan, että
      (let [koulutustoimija (dt/setup-koulutustoimija)
            oppilaitos (dt/setup-oppilaitos (:ytunnus koulutustoimija))
            oppilaitostunnus (:oppilaitoskoodi oppilaitos)
            y-tunnus (:ytunnus koulutustoimija)
            js (dt/setup-voimassaoleva-jarjestamissopimus "123" y-tunnus oppilaitostunnus "ILMA" 1)
            toimikunta-map (dt/setup-toimikunta "ILMA")]
        (with-data (merge toimikunta-map {:koulutusalat [{:selite_fi "Tekniikan ja liikenteen ala"
                                                :koodi "KA1"}]
                                :opintoalat [{:selite_fi "Sähköala"
                                              :koodi "OA1"
                                              :koulutusala "KA1"}]
                                :tutkintotyypit [{:tyyppi "99"
                                                  :selite_fi "Paras tutkinto"}]
                                :perusteet [{:diaarinumero "7/8/9"}]
                                :tutkinnot [{:nimi_fi "Ilmastointialan tutkinto"
                                             :tutkintotunnus "TU1"
                                             :opintoala "OA1"
                                             :tyyppi "99"
                                             :peruste "7/8/9"
                                             :tutkintoversio_id 1
                                             :uusin_versio_id 2}]
                                :tutkintoversiot [{:peruste "1/2/3"
                                                   :tutkintoversio_id 2
                                                   :versio 2
                                                   :koodistoversio 2
                                                   :tutkintotunnus "TU1"}]
                                :oppilaitokset [oppilaitos]
                                :koulutustoimijat [koulutustoimija]
                                :jarjestamissopimukset [(:jarjestamissopimukset js)]
                                :sopimus_ja_tutkinto [(:sopimus_ja_tutkinto js)]})
          ;; Kun
          (avaa (tutkintosivu "TU1"))
          (testing "pitäisi näyttää tutkinnon nimi ja tutkintokoodi otsikkona"
            ;; Niin
            (is (= (sivun-otsikko) "ILMASTOINTIALAN TUTKINTO - TU1")))
          (testing "pitäisi näyttää koulutusalan nimi"
            ;; Niin
            (is (= (elementin-teksti "tutkinto.opintoala.koulutusala.selite") "Tekniikan ja liikenteen ala")))
          (testing "pitäisi näyttää opintoalan nimi"
            ;; Niin
            (is (= (elementin-teksti "tutkinto.opintoala.selite") "Sähköala")))
          (testing "pitäisi näyttää tutkintotyypin nimi"
            ;; Niin
            (is (= (elementin-teksti "tutkinto.tyyppi_selite") "Paras tutkinto")))
          (testing "pitäisi näyttää järjestämissopimukset"
            ;; Niin
            (is (= (first (jarjestamissopimukset)) "123" )))
          (testing "pitäisi näyttää uusin tutkinnonperuste"
            ;;Niin
            (is (= (elementin-teksti "tutkinto.peruste") "1/2/3")))
          (testing "pitäisi näyttää tutkinnonperuste järjestämissopimuksille"
            ;; Niin
            (is (= (sopimuksen-tutkinnonperusteet) ["7/8/9"]))))))))
