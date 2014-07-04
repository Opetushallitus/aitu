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

(ns aitu-e2e.jarjestamissopimuksen-luontisivu-test
  (:require [clojure.test :refer [deftest is testing]]
            [aitu-e2e.jarjestamissopimussivu-test :refer [jarjestamissopimus-data]]
            [aitu-e2e.toimikuntasivu-test :refer [toimikuntasivu]]
            [clj-webdriver.taxi :as w]
            [aitu-e2e.util :refer :all]
            [aitu-e2e.aitu-util :refer :all]
            [aitu-e2e.data-util :refer [with-data with-cleaned-data]]))

(defn avaa-sopimuksen-luontisivu-toimikunnalle [diaarinumero]
  (avaa (toimikuntasivu diaarinumero))
  (w/click "button[ng-click=\"siirrySopimuksenLuontiin()\"]")
  (odota-angular-pyyntoa))

(deftest ^:no-ie jarjestamissopimuksen-luontisivu-test
  (testing
    "järjestämissopimuksen luontisivu:"
    (testing
      "sivu aukeaa kun painaa lisää nappia toimikunnan sivulla"
      (with-webdriver
        (with-data jarjestamissopimus-data
          (avaa-sopimuksen-luontisivu-toimikunnalle "98/11/543")
          (w/visible? (w/find-element {:css "button[ng-click=\"tallenna()\"]"})))))
    (testing
      "luonti onnistuu kun pakollisiin kenttiin syöttää validit arvot"
      (with-webdriver
        (with-cleaned-data jarjestamissopimus-data
          (avaa-sopimuksen-luontisivu-toimikunnalle "98/11/543")
          (syota-kenttaan "sopimus.sopimusnumero" "8742")
          (syota-pvm "sopimus.alkupvm" "24.03.1980")
          (syota-pvm "sopimus.loppupvm" "01.01.2199")
          (valitse-select2-optio "sopimus.koulutustoimija" "ytunnus", "Hanhivaaran kaupunki")
          (valitse-select2-optio "sopimus.tutkintotilaisuuksista_vastaava_oppilaitos" "oppilaitoskoodi" "Hanhivaaran urheiluopisto")
          (odota-angular-pyyntoa)
          (tallenna)
          (is (= (viestin-teksti) "Järjestämissopimuksen luonti onnistui"))
          (tallenna)
          (is (= (viestin-teksti) "Tutkintojen muokkaus onnistui"))
          (is (= (elementin-teksti "sopimus.sopimusnumero") "8742"))
          (is (= (elementin-teksti "sopimus.alkupvm") "24.03.1980 - 01.01.2199"))
          (is (= (elementin-teksti "sopimus.sopijatoimikunta.nimi") "Testialan tutkintotoimikunta (2013)"))
          (is (= (elementin-teksti "sopimus.toimikunta.nimi") "Testialan tutkintotoimikunta (2013)"))
          (is (= (elementin-teksti "sopimus.koulutustoimija.nimi") "Hanhivaaran kaupunki"))
          (is (= (elementin-teksti "sopimus.tutkintotilaisuuksista_vastaava_oppilaitos.nimi") "Hanhivaaran urheiluopisto")))))
    (testing
      "luonti ei onnistu, jos sopimusnumero on käytössä"
      (with-webdriver
        (with-cleaned-data jarjestamissopimus-data
          (avaa-sopimuksen-luontisivu-toimikunnalle "98/11/543")
          (syota-kenttaan "sopimus.sopimusnumero" "123")
          (syota-pvm "sopimus.alkupvm" "24.03.1980")
          (syota-pvm "sopimus.loppupvm" "01.01.2199")
          (valitse-select2-optio "sopimus.koulutustoimija" "ytunnus", "Hanhivaaran kaupunki")
          (valitse-select2-optio "sopimus.tutkintotilaisuuksista_vastaava_oppilaitos" "oppilaitoskoodi" "Hanhivaaran urheiluopisto")
          (odota-angular-pyyntoa)
          (tallenna)
          (is (= (viestin-teksti) "Järjestämissopimuksen luonti ei onnistunut"))
          (-> (viestit-virheellisista-kentista) first (= "Sopimusnumero : Arvon tulee olla uniikki") is))))
    (testing
      "luonti toiselle toimikunnalle onnistuu"
      (with-webdriver
        (with-cleaned-data (update-in jarjestamissopimus-data
                                      [:toimikunnat]
                                      conj
                                      {:nimi_fi "Toinen toimikunta"
                                       :diaarinumero "99/12/544"
                                       :toimikausi 2})
          (avaa-sopimuksen-luontisivu-toimikunnalle "98/11/543")
          (syota-kenttaan "sopimus.sopimusnumero" "8742")
          (syota-pvm "sopimus.alkupvm" "24.03.1980")
          (syota-pvm "sopimus.loppupvm" "01.01.2199")
          (valitse-select2-optio "sopimus.toimikunta" "tkunta", "Toinen toimikunta")
          (valitse-select2-optio "sopimus.koulutustoimija" "ytunnus", "Hanhivaaran kaupunki")
          (valitse-select2-optio "sopimus.tutkintotilaisuuksista_vastaava_oppilaitos" "oppilaitoskoodi" "Hanhivaaran urheiluopisto")
          (odota-angular-pyyntoa)
          (tallenna)
          (is (= (viestin-teksti) "Järjestämissopimuksen luonti onnistui"))
          (tallenna)
          (is (= (viestin-teksti) "Tutkintojen muokkaus onnistui"))
          (is (= (elementin-teksti "sopimus.sopijatoimikunta.nimi") "Toinen toimikunta (2013)"))
          (is (= (elementin-teksti "sopimus.toimikunta.nimi") "Toinen toimikunta (2013)")))))
    (testing
      "luonti ei onnistu, jos pakollisia tietoja puuttuu"
      (with-webdriver
        (with-data jarjestamissopimus-data
          (avaa-sopimuksen-luontisivu-toimikunnalle "98/11/543")
          (syota-pvm "sopimus.alkupvm" " ")
          (odota-angular-pyyntoa)
          (is (not (tallennus-nappi-aktiivinen?))))))))

