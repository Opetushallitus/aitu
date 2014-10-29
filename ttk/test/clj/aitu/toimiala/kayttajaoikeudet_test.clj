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

(ns aitu.toimiala.kayttajaoikeudet-test
  (:require [clojure.test :refer :all]
            [clojure.set :refer [union intersection]]
            [aitu.toimiala.kayttajaoikeudet :refer :all]
            [aitu.toimiala.kayttajaroolit :refer :all]
            [aitu.test-timeutil :refer :all]
            [aitu.integraatio.sql.test-util :refer [toimikunnan-jasenyys]]))

(defn saako-tehda?
  "Saako käyttäjä tehdä annetun toiminnon. Toiminnon kohde voi vaikuttaa kontekstisensitiivisiin oikeuksiin ellei käyttäjä ole ylläpitäjä-roolissa"
  [kayttaja-map toiminto kohdeid]
  {:pre [(contains? toiminnot toiminto)
         (some #{(:roolitunnus kayttaja-map)} (vals kayttajaroolit))]}
  (binding [*current-user-authmap* kayttaja-map]
    (let [auth-fn (get toiminnot toiminto)]
      (if (nil? kohdeid)
        ((eval auth-fn))
        ((eval auth-fn) kohdeid)))))

(defn kayttaja-map
  ([]
   (kayttaja-map (toimikunnan-jasenyys "123" "sihteeri")))
  ([jasenyys]
    {:oid "foo123"
     :henkiloid "henkiloid123"
     :roolitunnus (:kayttaja kayttajaroolit)
     :toimikunta #{jasenyys}}))

(def oph-katselija-kayttaja {:roolitunnus (:oph-katselija kayttajaroolit)})

(defn onnistuuko-operaatio-toimikunnalle?
  ([operaatio tkunta]
   (onnistuuko-operaatio-toimikunnalle? (kayttaja-map) operaatio tkunta))
  ([kayttaja operaatio tkunta]
   (saako-tehda? kayttaja operaatio tkunta)))

(deftest vain-lueteltu-oikeus-kelpaa []
   (is (thrown? Throwable
          (saako-tehda? (kayttaja-map) :trolol-laulanta nil))))

(deftest ei-ole-paallekkaisia-oikeus-tunnisteita []
  (is (empty? (intersection (set (keys kayttajatoiminnot)) (set (keys yllapitotoiminnot))))))

(deftest toiminnot-test
  (let [yllapitaja {:oid "l" :roolitunnus (:yllapitaja kayttajaroolit)}
       kayttaja (kayttaja-map)
       oph-katselija {:roolitunnus (:oph-katselija kayttajaroolit)}
       konteksti "123456"]
    (testing "Vain ylläpitäjä saa tehdä ylläpitotoimintoja"
      (doseq [oikeus (keys yllapitotoiminnot)]
        (is (saako-tehda? yllapitaja oikeus konteksti))
        (is (not (saako-tehda? kayttaja oikeus konteksti)))
        (is (not (saako-tehda? oph-katselija oikeus konteksti)))))
    (testing "Ylläpitäjä saa tehdä kaikki toiminnot"
      (doseq [oikeus (keys toiminnot)]
        (is (saako-tehda? yllapitaja oikeus konteksti))))))

(deftest toimikunnan-tietojen-paivitys-test
  (testing "Käyttäjä ei voi päivittää toimikuntien tietoja"
    (testing "Oman toimikunnan päivitys"
      (is (not (onnistuuko-operaatio-toimikunnalle?  :toimikunta_paivitys "123"))))
    (testing "Toisen toimikunnan päviitys"
      (is (not (onnistuuko-operaatio-toimikunnalle?  :toimikunta_paivitys "asdd")))))
  (testing "OPH-katselija ei voi päivittää toimikuntien tietoja"
    (is (not (onnistuuko-operaatio-toimikunnalle? oph-katselija-kayttaja :toimikunta_paivitys "123")))))

(deftest sopimuksen-lisays-test
  (testing "Sopimuksen lisäys onnistuu toimikunnan muokkausjäseneltä"
    (is (onnistuuko-operaatio-toimikunnalle? (kayttaja-map) :sopimus_lisays "123")))
  (testing "Sopimuksen lisäys ei onnistu toimikunnan katselujäseneltä"
    (is (not (onnistuuko-operaatio-toimikunnalle? (kayttaja-map (toimikunnan-jasenyys "123" "asiantuntija")) :sopimus_lisays "123"))))
  (testing "Sopimuksen lisäys ei onnistu toiselle toimikunnalle"
    (is (not (onnistuuko-operaatio-toimikunnalle? :sopimus_lisays "4321"))))
  (testing "OPH-katselija ei saa lisätä sopimuksia"
    (is (not (saako-tehda? oph-katselija-kayttaja :sopimus_lisays "123")))))
