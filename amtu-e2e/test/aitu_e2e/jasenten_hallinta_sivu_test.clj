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

(ns aitu-e2e.jasenten-hallinta-sivu-test
  (:require [clojure.test :refer [deftest is testing]]
            [clj-webdriver.taxi :as w]
            [aitu-e2e.util :refer :all]
            [aitu-e2e.aitu-util :refer :all]
            [aitu-e2e.data-util :refer [with-data]]
            [clojure.set :refer [subset?]]))

(def nayta-vanhat-selector "a[ng-click=\"toggleNaytaVanhat()\"]")

(defn jasenten-hallinta-sivu [diaarinumero] (str "/fi/#/toimikunta/" diaarinumero "/jasenet"))

(defn muokattavat-jasenet []
  (map w/text (w/find-elements (-> *ng*
                                 (.repeater "henkilo in toimikunta.jasenyys")
                                 (.column "henkilo.sukunimi")))))

(defn jasenien-nimet [listan-nimi]
  (set (map w/text (w/find-elements {:css (str listan-nimi " tbody td:nth-child(2)")}))))

(defn yliviivatut [] (w/find-elements {:css ".removed"}))
(defn poista-jasen []
  (w/click {:css "button[ng-click=\"poistaJasen(henkilo)\"]"})
  (odota-angular-pyyntoa))
(defn palauta-jasen []
  (w/click {:css "button[ng-click=\"peruutaPoisto(henkilo)\"]"})
  (odota-angular-pyyntoa))
(defn paina-tallenna-nappia []
  (w/click {:css "button[ng-click=\"tallenna(toimikunta.jasenyys)\"]"}))
(defn tallenna-jasenet []
  (paina-tallenna-nappia)
  (odota-angular-pyyntoa))

(def testi-toimikunnat [{:nimi_fi "Ilmastointialan tutkintotoimikunta"
                        :diaarinumero "98/11/543"
                        :tkunta "ILMA"
                        :toimikausi 2}])
(def testi-henkilot [{:henkiloid 998
                      :sukunimi "Ankka"
                      :etunimi "Aku"},
                     {:henkiloid 999
                      :sukunimi "Hiiri"
                      :etunimi "Mikki"}])
(def testi-jasenet [{:toimikunta "ILMA"
                     :diaarinumero "98/11/543"
                     :rooli "jasen"
                     :henkilo {:henkiloid 998}},
                    {:toimikunta "ILMA"
                     :diaarinumero "98/11/543"
                     :henkilo {:henkiloid 999}}])

(defn testi-jasen-maaratty-alku-ja-loppupvm
  [alkupvm loppupvm]
  (->
    testi-jasenet
    first
    (assoc
      :alkupvm alkupvm
      :loppupvm loppupvm)))

(defn kirjoita-pvm-valitsin-kenttaan [henkilon-nimi indeksi kentta arvo]
  (let [selektori (str "$('td:contains(\"" henkilon-nimi "\")').eq(" indeksi ").parents('tr').find('fieldset[valittu-pvm=\"" kentta "\"] input[type=\"text\"]')")]
    (aseta-inputtiin-arvo-jquery-selektorilla selektori arvo)
    (odota-angular-pyyntoa)))

(deftest jasenten-hallinta-sivu-test
  (testing "Toimikunnan jäsenten hallinta sivu"
    (with-webdriver
      (testing "jäsensivun toiminnot"
        ;; Oletetaan, että
        (with-data {:toimikunnat testi-toimikunnat
                    :henkilot testi-henkilot
                    :jasenet testi-jasenet}
          ;; Kun
          (avaa (jasenten-hallinta-sivu "98/11/543"))
          ;; Niin
          (testing "pitäisi näyttää toimikunnan nimi otsikkossa"
            (is (= (elementin-teksti "toimikunta.nimi") "JÄSENTEN HALLINTA - ILMASTOINTIALAN TUTKINTOTOIMIKUNTA")))
          (testing "pitäisi näyttää nykyiset jäsenet"
            (is (= #{"Aku Ankka" "Mikki Hiiri"}
                   (set (muokattavat-jasenet)))))
          (testing "jäsenen poisto ja palautus"
            (testing "poista nappi pitäisi merkitä jäsen poistetuksi"
              (poista-jasen)
              (is (= (count (yliviivatut)) 1))
              (testing "palauta nappi pitäisi merkitä poistettu jäsen palautetuksi"
                (palauta-jasen)
                (is (= (count (yliviivatut)) 0))))))))
    (with-webdriver
      (testing "jäsenen poisto ja muutoksen tallennus"
        ;; Oletetaan, että
        (with-data {:toimikunnat testi-toimikunnat
                    :henkilot testi-henkilot
                    :jasenet testi-jasenet}
          ;; Kun
          (avaa (jasenten-hallinta-sivu "98/11/543"))
          (poista-jasen)
          (paina-tallenna-nappia)
          (odota-dialogia #"Oletko varma")
          (hyvaksyn-dialogin)
          (is (= (viestin-teksti) "Toimikunnan jäseniä muokattu"))
          (testing "jäsen poistuu toimikunnasta"
            (is (= #{"Mikki Hiiri"}
                   (jasenien-nimet ".nykyiset-jasenyydet")))
            (is (= #{}
                  (jasenien-nimet ".aiemmat-jasenyydet")))))))
    (with-webdriver
      (testing "jäsenen roolin muutoksen tallennus"
        ;; Oletetaan, että
        (with-data {:toimikunnat testi-toimikunnat
                    :henkilot testi-henkilot
                    :jasenet (vector (first testi-jasenet))}
          ;; Kun
          (avaa (jasenten-hallinta-sivu "98/11/543"))
          (w/select-option {:css "select"} {:text "sihteeri"})
          (tallenna-jasenet)
          (is (= (viestin-teksti) "Toimikunnan jäseniä muokattu"))
          (testing "voimassaolevat jäsenet pitäisi näyttää muokattu jäsen uusilla tiedoilla"
            (is (re-find #"Aku Ankka sihteeri" (listarivi ".nykyiset-jasenyydet" 0)))))))))

(deftest ^:no-ie jasenten-hallinta-sivu-test-voimassaolo-muutokset
  (testing "Toimikunnan jäsenten hallinta sivu"
    (with-webdriver
      (testing "Jäsenen voimassaoloaikaa voi muuttaa listalta"
        (with-data {:toimikunnat testi-toimikunnat
                    :henkilot testi-henkilot
                    :jasenet (vector (first testi-jasenet))}
          (avaa (jasenten-hallinta-sivu "98/11/543"))
          (kirjoita-pvm-valitsin-kenttaan "Aku Ankka" 0 "henkilo.alkupvm" "01.12.2013")
          (tallenna-jasenet)
          (is (= (viestin-teksti) "Toimikunnan jäseniä muokattu"))))
      (testing "Jos jäsen on toimikunnassa kahteen kertaan, voi voimassaoloaikaa muokata siten että voimassaoloaika ei mene päällekkäin toisen saman henkilön jäsenyyden kanssa."
        (with-data {:toimikunnat testi-toimikunnat
                    :henkilot testi-henkilot
                    :jasenet [(testi-jasen-maaratty-alku-ja-loppupvm "01.08.2013" "20.08.2013")
                              (testi-jasen-maaratty-alku-ja-loppupvm "01.09.2013" "20.09.2013")]}
          (avaa (jasenten-hallinta-sivu "98/11/543"))
          (kirjoita-pvm-valitsin-kenttaan "Aku Ankka" 0 "henkilo.alkupvm" "02.08.2013")
          (tallenna-jasenet)
          (is (= (viestin-teksti) "Toimikunnan jäseniä muokattu"))))
      (testing "Jos jäsen on toimikunnassa kahteen kertaan, voimassaolon muokkaus ei onnistu jos päällekkäisiä voimassaoloja."
        (with-data {:toimikunnat testi-toimikunnat
                    :henkilot testi-henkilot
                    :jasenet [(testi-jasen-maaratty-alku-ja-loppupvm "2013-08-01" "2013-08-20")
                              (testi-jasen-maaratty-alku-ja-loppupvm "2013-09-01" "2013-09-20")]}
          (avaa (jasenten-hallinta-sivu "98/11/543"))
          (kirjoita-pvm-valitsin-kenttaan "Aku Ankka" 0 "henkilo.alkupvm" "01.09.2013")
          (kirjoita-pvm-valitsin-kenttaan "Aku Ankka" 0 "henkilo.loppupvm" "20.09.2013")
          (kirjoita-pvm-valitsin-kenttaan "Aku Ankka" 1 "henkilo.alkupvm" "01.09.2013")
          (kirjoita-pvm-valitsin-kenttaan "Aku Ankka" 1 "henkilo.loppupvm" "20.09.2013")
          (tallenna-jasenet)
          (is (= (viestin-teksti) "Toimikunnan jäsenten muokkaus ei onnistunut")))))))
