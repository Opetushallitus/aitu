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

(ns aitu-e2e.kielenvaihto-test
  (:require [clojure.test :refer [deftest is testing]]
            [clj-webdriver.taxi :as w]
            [aitu-e2e.util :refer :all]
            [aitu-e2e.aitu-util :refer :all])
  (:import org.openqa.selenium.TimeoutException))

(defn avaa-etusivu-suomeksi [] (avaa (str "/fi/#/")))
(defn avaa-etusivu-ruotsiksi [] (avaa (str "/sv/#/")))

(defn suomeksi-linkki-nakyvissa [] (w/exists? {:css "#fi a:not(.ng-hide)"}))
(defn suomeksi-span-nakyvissa [] (w/exists? {:css "#fi span:not(.ng-hide)"}))
(defn ruotsiksi-linkki-nakyvissa [] (w/exists? {:css "#sv a:not(.ng-hide)"}))
(defn ruotsiksi-span-nakyvissa [] (w/exists? {:css "#sv span:not(.ng-hide)"}))

(defn vaihda-kielta-suomeksi []
  (w/click {:css "#fi a"})
  (odota-angular-pyyntoa))

(defn vaihda-kielta-ruotsiksi []
  (w/click {:css "#sv a"})
  (odota-angular-pyyntoa))

(deftest kielenvaihto-test
  (testing "kielenvaihto"
    (testing "mentäessä suomenkieliselle etusivulle Suomeksi -linkki ei ole näkyvissä"
      (with-webdriver
        (avaa-etusivu-suomeksi)
        (is (not (suomeksi-linkki-nakyvissa)))
        (is (suomeksi-span-nakyvissa))
        (is (not(ruotsiksi-span-nakyvissa)))
        (is (ruotsiksi-linkki-nakyvissa))))
    (testing "mentäessä ruotsinkieliselle etusivulle På Svenska -linkki ei ole näkyvissä"
      (with-webdriver
        (avaa-etusivu-ruotsiksi)
        (is (suomeksi-linkki-nakyvissa))
        (is (not (suomeksi-span-nakyvissa)))
        (is (ruotsiksi-span-nakyvissa))
        (is (not (ruotsiksi-linkki-nakyvissa)))))
    (testing "kielen vaihtaminen suomesta ruotsiin onnistuu"
      (with-webdriver
        (avaa-etusivu-suomeksi)
        (is (not (suomeksi-linkki-nakyvissa)))
        (vaihda-kielta-ruotsiksi)
        (is (suomeksi-linkki-nakyvissa))))
    (testing "kielen vaihtaminen ruotsista suomeen onnistuu"
      (with-webdriver
        (avaa-etusivu-ruotsiksi)
        (is (not (ruotsiksi-linkki-nakyvissa)))
        (vaihda-kielta-suomeksi)
        (is (ruotsiksi-linkki-nakyvissa))))))
