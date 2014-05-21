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

(ns aitu-e2e.tiedote-test
  (:require [clojure.test :refer [deftest is testing]]
            [clj-webdriver.taxi :as w]
            [aitu-e2e.util :refer :all]
            [aitu-e2e.data-util :refer [with-cleaned-data]]))

(defn avaa-etusivu-suomeksi [] (avaa (str "/fi/#/")))
(defn avaa-etusivu-ruotsiksi [] (avaa (str "/sv/#/")))
(defn muokkaa-tiedotetta []
  (w/click "button[ng-click=\"muokkaa()\"]")
  (odota-angular-pyyntoa))
(defn julkaise-tiedote []
  (w/click {:css "button[ng-click=\"julkaise()\"]"})
  (odota-angular-pyyntoa))
(defn poista-tiedote []
  (w/click {:css "button[ng-click=\"poista()\"]"})
  (odota-angular-pyyntoa))

(defn tyhjenna-textarea [ng-model-nimi]
  (w/execute-script (str "$('textarea[ng-model=\"" ng-model-nimi "\"]').val('').trigger('textarea')")))

(defn syota-tiedotekenttaan [ng-model-nimi arvo]
  (tyhjenna-textarea ng-model-nimi)
  (w/input-text (str "textarea[ng-model=\"" ng-model-nimi "\"]") arvo))

(defn tiedote-teksti-tasmaa [teksti] (= teksti (w/text {:css "section.tiedote p"})))
(defn tiedote-teksti-olemassa [] (w/exists? {:css "section.tiedote p"}))

(deftest tiedote-teksti-nakyvissa-test
  (testing "Etusivulle mentäessä tiedote näkyvissä on näkyvissä"
    (with-webdriver
      (with-cleaned-data {:tiedote [{:teksti_fi "suomi"
                                     :teksti_sv "ruotsi"}]}
        (testing "kun ollaan suomenkielisellä etusivulla"
          (avaa-etusivu-suomeksi)
          (is (tiedote-teksti-tasmaa "suomi")))
        (testing "kun ollaan ruotsinkielisellä etusivulla"
          (avaa-etusivu-ruotsiksi)
          (is (tiedote-teksti-tasmaa "ruotsi")))))))

(deftest ^:ie-epastabiili tiedoteen-muokkaus-test
  (testing "Tiedotteen muokkaus onnistuu"
    (with-webdriver
      (with-cleaned-data {:tiedote [{:teksti_fi "suomi"}]}
        (avaa-etusivu-suomeksi)
        (muokkaa-tiedotetta)
        (syota-tiedotekenttaan "tiedote.teksti_fi" "Uusi tiedote")
        (julkaise-tiedote)
        (is (= (viestin-teksti) "Tiedote julkaistu"))
        (is (tiedote-teksti-tasmaa "Uusi tiedote"))))))

(deftest ^:ie-epastabiili tiedoteen-lisays-test
  (testing "Tiedotteen lisays onnistuu"
    (with-webdriver
      (with-cleaned-data {}
        (avaa-etusivu-suomeksi)
        (is (not (tiedote-teksti-olemassa)))
        (muokkaa-tiedotetta)
        (syota-tiedotekenttaan "tiedote.teksti_fi" "Uusi tiedote")
        (julkaise-tiedote)
        (is (= (viestin-teksti) "Tiedote julkaistu"))
        (is (tiedote-teksti-tasmaa "Uusi tiedote"))))))

(deftest ^:ie-epastabiili tietotteen-poisto-test
  (testing "Etusivulle mentäessä tiedote näkyvissä on näkyvissä"
    (with-webdriver
      (with-cleaned-data {:tiedote [{:teksti_fi "suomenkielinen tiedot"
                                     :teksti_sv "ruotsi"}]}
        (avaa-etusivu-suomeksi)
        (muokkaa-tiedotetta)
        (poista-tiedote)
        (is (= (viestin-teksti) "Tiedote poistettu"))
        (is (not (tiedote-teksti-olemassa)))))))
