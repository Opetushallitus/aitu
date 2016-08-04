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

(ns aitu-e2e.henkilolista-test
  (:require [clojure.test :refer [deftest is testing]]
            [clj-webdriver.taxi :as w]
            [aitu-e2e.util :refer :all]
            [aitu-e2e.aitu-util :refer :all]
            [aitu-e2e.data-util :refer [with-data henkilo-tiedot]]))

(def henkilolista "/fi/#/henkilot")

(def testi-henkilot [{:henkiloid 999
                      :etunimi "Ahto"
                      :sukunimi "Simakuutio"}
                     {:henkiloid 998
                      :etunimi "Teemu"
                      :sukunimi "Teekkari"}])

(defn valitse-kaikki-toimikaudet []
  (w/click "#toimikausi_kaikki")
  (odota-angular-pyyntoa))

(defn valitse-nykyinen-toimikausi []
  (w/click "#toimikausi_nykyinen")
  (odota-angular-pyyntoa))

(defn nakyvat-henkilot []
  (map w/text (w/find-elements (-> *ng*
                                 (.repeater "hakutulos in hakutulokset")
                                 (.column "hakutulos.sukunimi")))))

(defn hae-nimella[nimi]
  (tyhjenna-input "search.nimi")
  (w/input-text (str "input[ng-model=\"search.nimi\"]") nimi))

(defn seuraava-sivu []
  (w/click {:css ".pagination li:nth-child(3) a"})
  (odota-angular-pyyntoa))
(defn edellinen-sivu []
  (w/click {:css ".pagination li:nth-child(2) a"})
  (odota-angular-pyyntoa))
(defn viimeinen-sivu []
  (w/click {:css ".pagination li:nth-child(4) a"})
  (odota-angular-pyyntoa))
(defn ensimmainen-sivu []
  (w/click {:css ".pagination li:nth-child(1) a"})
  (odota-angular-pyyntoa))

(defn nykyinen-sivu[] (w/attribute ".table.hakutulokset .table-header" "data-nykyinen-sivu"))

(deftest henkilolista-test
  (testing "henkilölista"
    (testing "pitäisi näyttää henkilöiden perustiedot, kun valittu kaikki toimikaudet"
      (with-webdriver
        ;;Oletetaan, että
        (with-data {:henkilot testi-henkilot}
          ;;Kun
          (avaa henkilolista)
          (valitse-kaikki-toimikaudet)
          ;;Niin
          (is (empty? (clojure.set/difference #{"Ahto Simakuutio" "Teemu Teekkari"} (set (nakyvat-henkilot))))))))
    (testing "pitäisi näyttää henkilöiden perustiedot, kun valittu nykyinen toimikausi"
      (with-webdriver
        ;;Oletetaan, että
        (with-data {:henkilot testi-henkilot
                    :toimikunnat [{:nimi_fi "Ilmastointialan tutkintotoimikunta"
                                   :diaarinumero "98/11/543"
                                   :toimiala "Toimiala 1"
                                   :tkunta "ILMA"
                                   :kielisyys "fi"
                                   :toimikausi 3}]
                    :jasenet [{:toimikunta "ILMA"
                               :henkilo {:henkiloid 998}
                               :diaarinumero "98/11/543"}
                              {:toimikunta "ILMA"
                               :henkilo {:henkiloid 999}
                               :diaarinumero "98/11/543"}]}
          ;;Kun
          (avaa henkilolista)
          (valitse-nykyinen-toimikausi)
          ;;Niin
          (is (= (nakyvat-henkilot) ["Ahto Simakuutio" "Teemu Teekkari"])))))
    (testing "on tyhjä, jos haku ei tuota osumia"
      (with-webdriver
        ;;Oletetaan, että
        (with-data {:henkilot testi-henkilot}
          ;;Kun
          (avaa henkilolista)
          (valitse-kaikki-toimikaudet)
          (w/input-text "input.search-with-autocomplete" "zaxscdvfbg")
          ;;Niin
          (is (= (nakyvat-henkilot) [])))))))

(deftest henkilolista-sivutus-test
  (testing "henkilölistan sivutus"
    (testing "ei ole aktiivinen jos tulokset mahtuvat yhdelle sivulle"
      (with-webdriver
        (with-data {:henkilot testi-henkilot}
            (avaa henkilolista)
            (valitse-kaikki-toimikaudet)
            (let [elements (w/find-elements {:css ".pagination li:not(.disabled)"})]
              (is (= (count elements) 0))))))
    (testing "jakaa 25 hakutulosta kolmelle sivulle kun sivulla näytettävä maksimi hakutulosmäärä on 10"
      (with-webdriver
        (with-data {:henkilot (take 25 (:default henkilo-tiedot))}
          (avaa henkilolista)
          (valitse-kaikki-toimikaudet)
          (is (= (count (nakyvat-henkilot)) 10))
          (seuraava-sivu)
          (is (= (count (nakyvat-henkilot)) 10))
          (seuraava-sivu)
          (is (>= (count (nakyvat-henkilot)) 5)))))
    (testing "toimii oikein liikuttaessa seuraavalle ja edelliselle sivulle"
      (with-webdriver
        (with-data {:henkilot (take 15 (:default henkilo-tiedot))}
          (avaa henkilolista)
          (valitse-kaikki-toimikaudet)
          (is (= (count (nakyvat-henkilot)) 10))
          (seuraava-sivu)
          (is (>= (count (nakyvat-henkilot)) 5))
          (edellinen-sivu)
          (is (= (count (nakyvat-henkilot)) 10)))))
    (testing "toimii oikein liikuttaessa viimeiselle ja ensimmäiselle sivulle"
      (with-webdriver
        (with-data {:henkilot (take 25 (:default henkilo-tiedot))}
          (avaa henkilolista)
          (valitse-kaikki-toimikaudet)
          (is (= (count (nakyvat-henkilot)) 10))
          (viimeinen-sivu)
          (is (>= (count (nakyvat-henkilot)) 5))
          (ensimmainen-sivu)
          (is (= (count (nakyvat-henkilot)) 10)))))
    (testing "näyttää ensimmäisen sivun kun hakukriteerit muuttuvat"
      (with-webdriver
        (with-data {:henkilot (take 25 (:default henkilo-tiedot))}
          (avaa henkilolista)
          (valitse-kaikki-toimikaudet)
          (seuraava-sivu)
          (is (= (nykyinen-sivu) "2"))
          (hae-nimella "etu1")
          (is (= (nykyinen-sivu) "1")))))))

