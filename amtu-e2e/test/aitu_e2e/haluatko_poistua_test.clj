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

(ns aitu-e2e.haluatko-poistua-test
  "Testit 'Haluatko varmasti poistua sivulta?' -popupille."
  (:import org.openqa.selenium.UnhandledAlertException)
  (:require [clojure.test :refer [deftest testing is]]
            [clj-webdriver.taxi :as w]
            [aitu-e2e.util :refer :all]
            [aitu-e2e.data-util :refer [with-data]]
            [aitu-e2e.henkilosivu-test :refer [henkilosivu]]))

(defn alan-muokata-tietoja []
  (w/click "button[ng-click=\"muokkaa()\"]")
  (odota-angular-pyyntoa))

(defn olen-muokkaamassa-henkilon-tietoja []
  (avaa (henkilosivu -1))
  (alan-muokata-tietoja))

(defn navigoin-pois-sivulta-osoiterivia-kayttaen []
  (try
    (avaa-uudelleenladaten "/fi/#/")
    ;; Jos dialogi on jo näkyvissä, tulee Angularin odottamisyrityksestä
    ;; UnhandledAlertException, mutta se ei haittaa, koska meitä kiinnostaa vain
    ;; dialogin ilmestyminen, eikä muun sivun stabiilius.
    (catch UnhandledAlertException _)))

(defn navigoin-pois-sivulta-navigointipalkkia-kayttaen []
  (w/click {:text "Tutkinnot"})
  (try
    (odota-angular-pyyntoa)
    (catch UnhandledAlertException _)))

(defn selaimen-haluatko-poistua-dialogi-nakyvissa? []
  ;; Regexiä pitää viilata, jos halutaan testata muilla/muunkielisillä
  ;; selaimilla, kuin englanninkielisellä tai suomenkielisellä Firefoxilla.
  (dialogi-nakyvissa? #"confirm|Tämä sivu haluaa sinun vahvistavan"))

(defn oma-haluatko-poistua-dialogi-nakyvissa? []
  (dialogi-nakyvissa? #"Haluatko varmasti poistua"))

(defmacro with-henkilo
  [& body]
  `(with-data
    {:henkilot [{:henkiloid -1
                :etunimi "Ahto"
                :sukunimi "Simakuutio"}]}
    ~@body))

(deftest ^:no-ie navigoi-pois-osoiterivia-kayttaen-test
  (testing "Selaimen varmistusdialogi ilmestyy, jos muokkaan tietoja ja navigoin pois sivulta osoiteriviä käyttäen"
    (with-webdriver
      (with-henkilo
        ;; Oletetaan, että
        (olen-muokkaamassa-henkilon-tietoja)
        ;; Kun
        (navigoin-pois-sivulta-osoiterivia-kayttaen)
        ;; Niin
        (is (selaimen-haluatko-poistua-dialogi-nakyvissa?))))))

(deftest tallenna-muutokset-ja-navigoi-pois-osoiterivia-kayttaen-test
  (testing "Selaimen varmistusdialogi ei ilmesty, jos tallennan muutokset ennen pois navigoimista"
    (with-webdriver
      (with-henkilo
        ;; Oletetaan, että
        (olen-muokkaamassa-henkilon-tietoja)
        (tallenna)
        ;; Kun
        (navigoin-pois-sivulta-osoiterivia-kayttaen)
        ;; Niin
        (is (not (selaimen-haluatko-poistua-dialogi-nakyvissa?)))))))

(deftest tallenna-muutokset-ja-navigoi-pois-navigointipalkkia-kayttaen-test
  (testing "Oma varmistusdialogi ei ilmesty, jos tallennan muutokset ennen pois navigoimista"
    (with-webdriver
      (with-henkilo
        ;; Oletetaan, että
        (olen-muokkaamassa-henkilon-tietoja)
        (tallenna)
        ;; Kun
        (navigoin-pois-sivulta-navigointipalkkia-kayttaen)
        ;; Niin
        (is (not (oma-haluatko-poistua-dialogi-nakyvissa?)))))))

(deftest navigoi-pois-navigointipalkkia-kayttaen-test
  (testing "Oma varmistusdialogi ilmestyy, jos muokkaan tietoja ja navigoin pois sivulta navigointipalkkia käyttäen"
    (with-webdriver
      (with-henkilo
        ;; Oletetaan, että
        (olen-muokkaamassa-henkilon-tietoja)
        ;; Kun
        (navigoin-pois-sivulta-navigointipalkkia-kayttaen)
        ;; Niin
        (is (oma-haluatko-poistua-dialogi-nakyvissa?))))))

(deftest peruuta-oma-varmistusdialogi-test
  (testing "Pysyn samalla sivulla, jos valitsen omasta varmistusdialogista 'peruuta'"
    (with-webdriver
      (with-henkilo
        ;; Oletetaan, että
        (olen-muokkaamassa-henkilon-tietoja)
        (let [url (w/current-url)]
          (navigoin-pois-sivulta-navigointipalkkia-kayttaen)
          ;; Kun
          (peruutan-dialogin)
          ;; Niin
          (is (= (w/current-url) url)))))))

(deftest hyvaksy-oma-varmistusdialogi-test
  (testing "Siirryn eri sivulle, jos valitsen omasta varmistusdialogista 'OK'"
    (with-webdriver
      (with-henkilo
        ;; Oletetaan, että
        (olen-muokkaamassa-henkilon-tietoja)
        (let [url (w/current-url)]
          (navigoin-pois-sivulta-navigointipalkkia-kayttaen)
          ;; Kun
          (hyvaksyn-dialogin)
          ;; Niin
          (is (not= (w/current-url) url)))))))

(deftest tallenna-muutokset-test
  (testing "Oma varmistusdialogi ei ilmesty, kun tallennan muutokset"
    (with-webdriver
      (with-henkilo
        ;; Oletetaan, että
        (olen-muokkaamassa-henkilon-tietoja)
        ;; Kun
        (tallenna)
        ;; Niin
        (is (not (oma-haluatko-poistua-dialogi-nakyvissa?)))))))

(deftest muokkaa-uudelleen-ja-navigoi-pois-navigointipalkkia-kayttaen-test
  (testing "Oma varmistusdialogi ilmestyy, jos palaan muokkaamaan tietoja ennen pois navigoimista"
    (with-webdriver
      (with-henkilo
        ;; Oletetaan, että
        (olen-muokkaamassa-henkilon-tietoja)
        (tallenna)
        (alan-muokata-tietoja)
        ;; Kun
        (navigoin-pois-sivulta-navigointipalkkia-kayttaen)
        ;; Niin
        (is (oma-haluatko-poistua-dialogi-nakyvissa?))))))

(deftest navigoi-pois-navigointipalkkia-ja-osoiterivia-kayttaen-test
  (testing "Selaimen varmistusdialogi ei ilmesty, jos navigoin pois navigointipalkilla ja sitten osoitepalkilla"
    (with-webdriver
      (with-henkilo
        ;; Oletetaan, että
        (olen-muokkaamassa-henkilon-tietoja)
        (navigoin-pois-sivulta-navigointipalkkia-kayttaen)
        (hyvaksyn-dialogin)
        ;; Kun
        (navigoin-pois-sivulta-osoiterivia-kayttaen)
        ;; Niin
        (is (not (selaimen-haluatko-poistua-dialogi-nakyvissa?)))))))
