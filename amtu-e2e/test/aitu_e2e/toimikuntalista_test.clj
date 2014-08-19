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

(ns aitu-e2e.toimikuntalista-test
  (:require [clojure.test :refer [deftest is testing]]
            [clojure.set :refer [subset?]]
            [clj-webdriver.taxi :as w]
            [aitu-e2e.util :refer :all]
            [aitu-e2e.aitu-util :refer :all]
            [aitu-e2e.data-util :refer [with-data]]))

(def toimikuntalista "/fi/#/search-toimikunta")

(defn nakyvat-toimikunnat []
  (set (map w/text (w/find-elements (-> *ng*
                                      (.repeater "hakutulos in hakutulokset")
                                      (.column "hakutulos.nimi"))))))

(def test-toimikunnat {:toimikunnat [{:nimi_fi "Ilmastointialan tutkintotoimikunta"
                                      :nimi_sv "Examenskommission för ventilationsbranschen"
                                      :toimiala "Ilmastointiala"}
                                     {:nimi_fi "Musiikkialan tutkintotoimikunta"
                                      :nimi_sv "Examenskommission inom musik"
                                      :toimiala "Musiikkiala"}]})

(defn valitse-toimikausi []
  (w/click "#toimikausi_kaikki")
  (odota-angular-pyyntoa))

(defn hae-nimella [nimi]
  (tyhjenna-input "toimikuntaHakuehto.nimi")
  (w/input-text (str "input[ng-model=\"toimikuntaHakuehto.nimi\"]") nimi))

(deftest toimikuntalista-test []
  (with-webdriver
    (testing "toimikuntalista"
      (testing "pitäisi näyttaa lista toimikunnista"
        ;; Oletetaan, että
        (with-data test-toimikunnat
          ;; Kun
          (avaa toimikuntalista)
          (valitse-toimikausi)
          ;; Niin
          (is (subset? #{"Ilmastointialan tutkintotoimikunta (2013)"
                           "Musiikkialan tutkintotoimikunta (2013)"}
                       (nakyvat-toimikunnat))))))))

(deftest toimikuntahaku-test []
  (with-webdriver
    (with-data test-toimikunnat
      (testing "toimikuntahaku"
        (testing "pitäisi hakea suomenkielisellä nimellä"
          ;;Kun
          (avaa toimikuntalista)
          (valitse-toimikausi)
          (hae-nimella "ilmastointiala")
          ;;Niin
          (is (= #{"Ilmastointialan tutkintotoimikunta (2013)"} (nakyvat-toimikunnat))))
        (testing "pitäisi hakea ruotsinkielisellä nimellä"
          ;;Kun
          (avaa toimikuntalista)
          (valitse-toimikausi)
          (hae-nimella "ventilationsbransch")
          ;;Niin
          (is (= #{"Ilmastointialan tutkintotoimikunta (2013)"} (nakyvat-toimikunnat))))))))

(deftest toimikuntasivu-opintoala-tai-tutkinto-haku-test
  (testing "toimikuntasivu: opintoala tai tutkinto haku:"
    (with-webdriver
      (with-data
        {:toimikunta_ja_tutkinto [{:toimikunta "TTK1"
                                   :tutkintotunnus "TU1"}
                                  {:toimikunta "TTK2"
                                   :tutkintotunnus "TU2"}]
         :toimikunnat [{:nimi_fi "Toimikunta 1"
                                 :diaarinumero "98/11/001"
                                 :tkunta "TTK1"}
                       {:nimi_fi "Toimikunta 2"
                        :diaarinumero "98/11/002"
                        :tkunta "TTK2"}]
         :tutkinnot [{:nimi_fi "Alan 1 tutkinto"
                      :tutkintotunnus "TU1"
                      :opintoala "OA1"}
                     {:nimi_fi "Alan 2 tutkinto"
                      :tutkintotunnus "TU2"
                     :opintoala "OA2"}]
         :opintoalat [{:selite_fi "Opintoala 1"
                       :koodi "OA1"
                       :koulutusala "KA1"}
                      {:selite_fi "Opintoala 2"
                       :koodi "OA2"
                       :koulutusala "KA1"}]
         :koulutusalat [{:koodi "KA1"}]}
        (testing "pitäisi näyttää lista toimikunnista joilla on tietyn opintoalan tutkinto vastuulla"
          (avaa toimikuntalista)
          (valitse-toimikausi)
          (valitse-select2-optio "tutkintoHakuehto" "tunnus" "Opintoala 1")
          (odota-angular-pyyntoa)
          (is (= #{"Toimikunta 1 (2013)"} (nakyvat-toimikunnat))))
        (testing "pitäisi näyttää lista toimikunnista joilla on tietty tutkinto vastuulla"
          (avaa toimikuntalista)
          (valitse-toimikausi)
          (valitse-select2-optio "tutkintoHakuehto" "tunnus" "Alan 2 tutkinto")
          (odota-angular-pyyntoa)
          (is (= #{"Toimikunta 2 (2013)"} (nakyvat-toimikunnat))))))))
