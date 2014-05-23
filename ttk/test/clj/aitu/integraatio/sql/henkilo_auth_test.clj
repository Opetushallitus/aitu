(ns aitu.integraatio.sql.henkilo-auth-test
  (:require [aitu.compojure-util :as cu]
            [clojure.test :refer [deftest testing is are use-fixtures]]
            [aitu.integraatio.sql.test-data-util :refer :all]
            [aitu.integraatio.sql.test-util :refer :all]
            [oph.korma.korma-auth :as ka]
            [aitu.toimiala.kayttajaroolit :refer :all]
            [aitu.test-timeutil :refer :all]))

(use-fixtures :each tietokanta-fixture)

(defn lisaa-testidata! []
  (lisaa-henkilo! {:henkiloid -1})
  (lisaa-henkilo! {:henkiloid -2})
  (lisaa-henkilo! {:henkiloid -3})
  (lisaa-henkilo! {:henkiloid -4})
  (lisaa-toimikunta!)
  (lisaa-jasen! {:toimikunta (:tkunta default-toimikunta) :henkiloid -1})
  (lisaa-jasen! {:toimikunta (:tkunta default-toimikunta) :henkiloid -4 :loppupvm menneisyydessa}))

(def yllapitaja-auth-map {:roolitunnus (:yllapitaja kayttajaroolit)})

(def oph-katselija-auth-map {:roolitunnus (:oph-katselija kayttajaroolit)})

(defn kayttaja-auth-map
  ([]
   (kayttaja-auth-map (toimikunnan-jasenyys (:tkunta default-toimikunta) "sihteeri")))
  ([jasenyys]
  {:henkiloid -3
   :roolitunnus (:kayttaja kayttajaroolit)
   :toimikunta #{jasenyys}}))

(defn autorisoi-henkilon-paivitys [henkiloid]
  (cu/autorisoi :henkilo_paivitys henkiloid true))

(defn henkilon-paivitys-onnistuu [henkiloid]
  (is (autorisoi-henkilon-paivitys henkiloid)))

(defn henkilon-paivitys-ei-onnistu [henkiloid]
  (is (thrown? Throwable
               (autorisoi-henkilon-paivitys henkiloid))))

(defn autorisoi-henkilon-lisays []
  (cu/autorisoi :henkilo_lisays nil true))

(defn henkilon-lisays-onnistuu []
  (is (autorisoi-henkilon-lisays)))

(defn henkilon-lisays-ei-onnistu []
  (is (thrown? Throwable
               (autorisoi-henkilon-lisays))))

(deftest ^:integraatio henkilon-lisays []
  (testing "Ylläpitäjä voi lisätä henkilön"
    (with-user-rights
      yllapitaja-auth-map
      #(henkilon-lisays-onnistuu)))
  (testing "Käyttäjä ei voi lisätä henkilöä"
    (with-user-rights
      (kayttaja-auth-map)
      #(henkilon-lisays-ei-onnistu)))
  (testing "OPH-katselija ei voi lisätä henkilöä"
    (with-user-rights
      oph-katselija-auth-map
      #(henkilon-lisays-ei-onnistu))))

(deftest ^:integraatio henkilon-paivitys []
  (lisaa-testidata!)
  (testing "Ylläpitäjä voi päivittää henkilön tietoja"
    (with-user-rights
      yllapitaja-auth-map
      #(henkilon-paivitys-onnistuu -1)))
  (testing "OPH-katselija ei voi päivittää henkilön tietoja"
    (with-user-rights
      oph-katselija-auth-map
      #(henkilon-paivitys-ei-onnistu -2)))
  (testing "Saman toimikunnan muokkausjäsen voi päivittää henkilön tietoja"
    (with-user-rights
      (kayttaja-auth-map)
      #(henkilon-paivitys-onnistuu -1)))
  (testing "Käyttäjä voi päivittää omia tietojaan"
    (with-user-rights
      (kayttaja-auth-map)
      #(henkilon-paivitys-onnistuu -3)))
  (testing "Käyttäjä ei voi päivittää henkilön tietoja, jos käyttäjä ei ole muokkausjäsen samassa toimikunnassa missä henkilöllä on jäsenyys"
    (with-user-rights
      (kayttaja-auth-map)
      #(henkilon-paivitys-ei-onnistu -2)))
  (testing "Toimikunnan muokkausjäsen ei voi päivittää toimikunnan toisen henkilön tietoja jos toimikunta ei ole voimassa"
    (with-user-rights
      (kayttaja-auth-map (vanhentuneen-toimikunnan-jasenyys (:tkunta default-toimikunta) "sihteeri"))
      #(henkilon-paivitys-ei-onnistu -1)))
  (testing "Toimikunnan entinen muokkausjäsen voi päivittää toimikunnan toisen henkilön tietoja"
    (with-user-rights
      (kayttaja-auth-map (voimassaolevan-toimikunnan-vanhentunut-jasenyys (:tkunta default-toimikunta) "sihteeri"))
      #(henkilon-paivitys-ei-onnistu -1)))
  (testing "Toimikunnan muokkausjäsen ei voi päivittää toimikunnan entisen jäsenen tietoja"
    (with-user-rights
      (kayttaja-auth-map)
      #(henkilon-paivitys-ei-onnistu -4))))


