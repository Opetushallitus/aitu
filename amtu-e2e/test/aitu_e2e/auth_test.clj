(ns aitu-e2e.auth-test
  (:require [clojure.test :refer [deftest is testing]]
            [aitu-e2e.util :refer :all]
            [aitu-e2e.aitu-util :refer :all]
            [aitu-e2e.data-util :refer [with-data with-cleaned-data]]
            [clj-webdriver.taxi :as w]
            [aitu-e2e.toimikuntasivu-test :refer [toimikuntasivu
                                                  toimikuntasivu-testidata]]
            [aitu-e2e.jarjestamissopimussivu-test :refer [sopimussivu]]
            [aitu-e2e.henkilosivu-test :refer [henkilosivu]]))

(defn elementti-nakyy [css-selektori]
  (= (count (filter w/displayed? (w/find-elements {:css css-selektori}))) 1))

(defn toimikunnan-muokkaus-nappi-nakyy []
  (elementti-nakyy "button.muokkaa-toimikuntaa"))

(defn toimikunnan-toimialan-muokkaus-nappi-nakyy []
  (elementti-nakyy "button.muokkaa-toimialaa"))

(defn toimikunnan-jasenten-muokkaus-nappi-nakyy []
  (elementti-nakyy "button.muokkaa-jasenia"))

(defn toimikunnan-sopimusten-lisays-nakyy []
  (elementti-nakyy "button.lisaa-sopimus"))

(defn linkki-sopimussivulle-nakyy [sopimusnumero]
  (elementti-nakyy (str "a[href=\"#/sopimus/" sopimusnumero "/tiedot\"]")))

(defn sopimuksen-muokkaus-nappi-nakyy []
  (elementti-nakyy "button.muokkaa-sopimusta"))

(defn sopimuksen-poisto-nappi-nakyy []
  (elementti-nakyy "button.poista-sopimus"))

(defn sopimuksen-tutkintojen-muokkaus-nappi-nakyy []
  (elementti-nakyy "button.poista-sopimus"))

(defn henkilon-muokkaus-nappi-nakyy []
  (elementti-nakyy "button.edit-icon"))

(defn testidata-toimikunnan-jasen []
  (-> toimikuntasivu-testidata
      (assoc-in [:henkilot 0 :kayttaja_oid] "OID.T-800")
      (assoc-in [:jasenet 0 :rooli] "asiantuntija")))

(defn testidata-toimikunnan-muokkausjasen []
  (-> toimikuntasivu-testidata
      (assoc-in [:henkilot 0 :kayttaja_oid] "OID.T-800")
      (assoc-in [:jasenet 0 :rooli] "puheenjohtaja")))

(deftest ^:cas toimikuntasivu-auth-testi
  (testing "Toimikuntasivu auth test:"
    (testing "Ylläpitäjä näkee kaikki muokkaustoiminnallisuudet ja linkit sopimussivuille näkyvät:"
      (with-data toimikuntasivu-testidata
        (with-webdriver
          (avaa-kayttajana
            (toimikuntasivu "98/11/543")
            "T-1001"
            (is (toimikunnan-muokkaus-nappi-nakyy))
            (is (toimikunnan-toimialan-muokkaus-nappi-nakyy))
            (is (toimikunnan-jasenten-muokkaus-nappi-nakyy))
            (is (toimikunnan-sopimusten-lisays-nakyy))
            (is (linkki-sopimussivulle-nakyy 1230))))))
    (testing "Käyttäjä, joka ei ole toimikunnan jäsen, ei näe muokkaustoiminnallisuuksia eikä linkkejä sopimussivuille:"
      (with-data toimikuntasivu-testidata
        (with-webdriver
          (avaa-kayttajana
            (toimikuntasivu "98/11/543")
            "T-800"
            (is (not (toimikunnan-muokkaus-nappi-nakyy)))
            (is (not (toimikunnan-toimialan-muokkaus-nappi-nakyy)))
            (is (not (toimikunnan-jasenten-muokkaus-nappi-nakyy)))
            (is (not (toimikunnan-sopimusten-lisays-nakyy)))
            (is (not (linkki-sopimussivulle-nakyy 1230))))))))
    (testing "Käyttäjä, joka on toimikunnan jäsen, ei näe muokkaustoiminnallisuuksia. Sopimusnumerot näkyvät linkkeinä:"
      (with-data (testidata-toimikunnan-jasen)
        (with-webdriver
          (avaa-kayttajana
            (toimikuntasivu "98/11/543")
            "T-800"
            (is (not (toimikunnan-muokkaus-nappi-nakyy)))
            (is (not (toimikunnan-toimialan-muokkaus-nappi-nakyy)))
            (is (not (toimikunnan-jasenten-muokkaus-nappi-nakyy)))
            (is (not (toimikunnan-sopimusten-lisays-nakyy)))
            (is (linkki-sopimussivulle-nakyy 1230))))))
    (testing "Toimikunnan muokkausjäsen näkee sopimuksen lisäystoiminnon ja sopimusnumerot linkkeinä:"
      (with-data (testidata-toimikunnan-muokkausjasen)
        (with-webdriver
          (avaa-kayttajana
            (toimikuntasivu "98/11/543")
            "T-800"
            (is (not (toimikunnan-muokkaus-nappi-nakyy)))
            (is (not (toimikunnan-toimialan-muokkaus-nappi-nakyy)))
            (is (not (toimikunnan-jasenten-muokkaus-nappi-nakyy)))
            (is (toimikunnan-sopimusten-lisays-nakyy))
            (is (linkki-sopimussivulle-nakyy 1230)))))))

(deftest ^:cas sopimussivu-auth-testi
  (testing "Sopimussivu auth test:"
    (testing "Ylläpitäjä pääsee sopimussivulle ja näkee kaikki muokkaustoiminnallisuudet"
      (with-data toimikuntasivu-testidata
        (with-webdriver
          (avaa-kayttajana
            (sopimussivu 1230)
            "T-1001"
            (is (sopimuksen-muokkaus-nappi-nakyy))
            (is (sopimuksen-poisto-nappi-nakyy))
            (is (sopimuksen-tutkintojen-muokkaus-nappi-nakyy)))))
    (testing "Käyttäjä, joka ei ole toimikunnan jäsen, ei pääse sopimussivulle"
      (with-data toimikuntasivu-testidata
        (with-webdriver
          (avaa-kayttajana
            (sopimussivu 1230)
            "T-800"
            (is (not (nil? (re-find #"Virhe metodissa" (viestin-teksti)))))))))
    (testing "Käyttäjä, joka on toimikunnan jäsen pääsee sopimussivulle, mutta ei näe muokkaustoiminnallisuuksia"
      (with-data (testidata-toimikunnan-jasen)
        (with-webdriver
          (avaa-kayttajana
            (sopimussivu 1230)
            "T-800"
            (is (not (sopimuksen-muokkaus-nappi-nakyy)))
            (is (not (sopimuksen-poisto-nappi-nakyy)))
            (is (not (sopimuksen-tutkintojen-muokkaus-nappi-nakyy)))))))
    (testing "Käyttäjä, joka on toimikunnan muokkausjäsen pääsee sopimussivulle ja näkee muokkaustoiminnallisuudet"
      (with-data (testidata-toimikunnan-muokkausjasen)
        (with-webdriver
          (avaa-kayttajana
            (sopimussivu 1230)
            "T-800"
            (is (sopimuksen-muokkaus-nappi-nakyy))
            (is (sopimuksen-poisto-nappi-nakyy))
            (is (sopimuksen-tutkintojen-muokkaus-nappi-nakyy)))))))))

(deftest ^:cas henkilosivu-auth-testi
  (testing "Henkilösivu auth test:"
    (testing "Ylläpitäjä näkee henkilön muokkaustoiminnallisuuden"
      (with-data toimikuntasivu-testidata
        (with-webdriver
          (avaa-kayttajana
            (henkilosivu 999)
            "T-1001"
            (is (henkilon-muokkaus-nappi-nakyy))))))
    (testing "Käyttäjä, joka ei ole saman toimikunnan muokkausjäsen, ei näe henkilön muokkaustoiminnallisuutta"
      (with-data (testidata-toimikunnan-jasen)
        (with-webdriver
          (avaa-kayttajana
            (henkilosivu 999)
            "T-800"
            (is (not (henkilon-muokkaus-nappi-nakyy)))))))
    (testing "Käyttäjä joka on toimikunnan muokkausjäsen, näkee muokkaustoiminnallisuuden muiden toimikunnan jäsenten sivuilla"
      (with-data (testidata-toimikunnan-muokkausjasen)
        (with-webdriver
          (avaa-kayttajana
            (henkilosivu 999)
            "T-800"
            (is (henkilon-muokkaus-nappi-nakyy))))))
    (testing "Käyttäjä näkee muokkaustoiminnallisuuden omalla henkilösivullaan"
      (with-data (testidata-toimikunnan-jasen)
        (with-webdriver
          (avaa-kayttajana
            (henkilosivu 998)
            "T-800"
            (is (henkilon-muokkaus-nappi-nakyy))))))))

(deftest ^:cas oph-katselija-auth-testi
  (testing "Toimikuntasivu: OPH-katselija ei näe muokkaustoiminnallisuuksia, mutta näkee linkit sopimussivuille."
    (with-data toimikuntasivu-testidata
      (with-webdriver
        (avaa-kayttajana
          (toimikuntasivu "98/11/543")
          "oph-katselija"
          (is (not (toimikunnan-muokkaus-nappi-nakyy)))
          (is (not (toimikunnan-toimialan-muokkaus-nappi-nakyy)))
          (is (not (toimikunnan-jasenten-muokkaus-nappi-nakyy)))
          (is (not (toimikunnan-sopimusten-lisays-nakyy)))
          (is (linkki-sopimussivulle-nakyy 1230))))))
  (testing "Sopimussivu: OPH-katselija ei näe muokkaustoiminnallisuuksia"
    (with-data toimikuntasivu-testidata
      (with-webdriver
        (avaa-kayttajana
          (sopimussivu 1230)
          "oph-katselija"
          (is (not (sopimuksen-muokkaus-nappi-nakyy)))
          (is (not (sopimuksen-poisto-nappi-nakyy)))
          (is (not (sopimuksen-tutkintojen-muokkaus-nappi-nakyy)))))))
  (testing "Henkilösivu: OPH-katselija ei näe muokkaustoiminnallisuuksia"
    (with-data toimikuntasivu-testidata
      (with-webdriver
        (avaa-kayttajana
          (henkilosivu 999)
          "oph-katselija"
          (is (not (henkilon-muokkaus-nappi-nakyy))))))))

(def etusivu "/fi/#/")

(deftest ^:cas impersonointi-auth-test
  (testing "Impersonointi auth test:"
    (testing "ylläpitäjällä on impersonointi valikko"
      (with-webdriver
        (avaa-kayttajana
          etusivu
          "T-1001"
          (is (w/exists? {:css "#current-user>li a[ng-click=\"valitse()\"]"})))))
    (testing "käyttäjällä ei ole impersonointivalikkoa"
      (with-webdriver
        (avaa-kayttajana
          etusivu
          "T-800"
          (is (not (w/exists? {:css "#current-user>li a[ng-click=\"valitse()\"]"}))))))))

(defn kirjaudu-ulos-toisessa-ikkunassa []
  (let [paaikkuna (w/window)
        _ (w/execute-script (str "window.open('" @cas-url "/logout')"))
        cas-ikkuna (first (disj (set (w/windows))
                                paaikkuna))]
    (w/switch-to-window cas-ikkuna)
    (w/close)
    (w/switch-to-window paaikkuna)))

(defn navigoi-tutkinnot-sivulle []
  (w/click {:text "Tutkinnot"}))

(deftest ^:no-ie ^:cas ajax-uudelleenohjaus-test
  (testing "Käyttäjä, jonka istunto on suljettu, ohjataan sisäänkirjautumiseen AJAX-pyynnön yhteydessä"
    (with-webdriver
      (avaa etusivu)
      (kirjaudu-ulos-toisessa-ikkunassa)
      (navigoi-tutkinnot-sivulle)
      (odota-kunnes (casissa?)))))
