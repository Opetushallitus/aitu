(ns aitu-e2e.auth-test
  (:require [clojure.test :refer [deftest is testing]]
            [aitu-e2e.util :refer :all]
            [aitu-e2e.data-util :refer [with-data with-cleaned-data]]
            [clj-webdriver.taxi :as w]
            [aitu-e2e.toimikuntasivu-test :refer [toimikuntasivu
                                                  toimikuntasivu-testidata]]
            [aitu-e2e.jarjestamissopimussivu-test :refer [sopimussivu]]))

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

(defn testidata-toimikunnan-jasen []
  (assoc-in toimikuntasivu-testidata [:henkilot 0 :kayttaja_oid] "OID.T-800"))

(defn testidata-toimikunnan-muokkausjasen []
  (-> toimikuntasivu-testidata
      (assoc-in [:henkilot 0 :kayttaja_oid] "OID.T-800")
      (assoc-in [:jasenet 0 :rooli] "puheenjohtaja")))

#_(deftest ^:cas toimikuntasivu-auth-testi
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

#_(deftest ^:cas sopimussivu-auth-testi
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

#_(deftest toimikunnan-sopimusten-lisays-auth-test
  (testing "Toimikunnan sopimusten lisääminen"
    ))

#_(deftest toimikunnan-sopimusten-luku-auth-test
  (testing "Toimikunnan sopimusten luku"
    ))

#_(deftest toimikunnan-sopimusten-muokkaus-auth-test
  (testing "Toimikunnan sopimusten muokkaus"
    ))