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

(ns aitu-e2e.oppilaitossivu-test
  (:require [clojure.set :refer [subset?]]
            [clojure.test :refer [deftest is testing]]
            [clj-webdriver.taxi :as w]
            [aitu-e2e.util :refer :all]
            [aitu-e2e.aitu-util :refer :all]
            [aitu-e2e.data-util :refer [with-data
                                        menneisyydessa
                                        tulevaisuudessa
                                        menneisyydessa-kayttoliittyman-muodossa
                                        tulevaisuudessa-kayttoliittyman-muodossa]]
            [aitu-e2e.datatehdas :as dt]
            [clj-time.core :as time]
            [clj-time.format :as time-format]))

(defn oppilaitossivu [id] (str "/fi/#/oppilaitos/" id "/tiedot"))

(def nayta-vanhat-selector "a[ng-click='naytaVanhatSopimukset = !naytaVanhatSopimukset']")

(defn sopimuslista [lista]
  (into #{}
        (for [elements (w/find-elements {:css (str lista " tbody tr")})]
          (clojure.string/split (w/text elements) #"\n"))))

(defn oppilaitoksen-tieto [binding]
  (-> *ng*
      (.binding binding)
      (w/find-elements)
      (first)
      (w/text)))

(deftest oppilaitossivu-test []
  (with-webdriver
    ;; Oletetaan, että
    (let [puhelin "040123456"
          sahkoposti "oppilaitos@oppilaitos.fi"
          osoite "Oppilaitoskatu 1"
          postinumero "12345"
          postitoimipaikka "Postitoimipaikka"
          koulutustoimija (dt/setup-koulutustoimija)
          oppilaitos (merge (dt/setup-oppilaitos (:ytunnus koulutustoimija)) {:puhelin puhelin
                                                                              :sahkoposti sahkoposti
                                                                              :osoite osoite
                                                                              :postinumero postinumero
                                                                              :postitoimipaikka postitoimipaikka})
          tunnus (:oppilaitoskoodi oppilaitos)]
      (with-data {:koulutustoimijat [koulutustoimija]
                  :oppilaitokset [oppilaitos]}
        (testing "pitäisi näyttaa tiedot oppilaitoksesta"
          ;; Kun
          (avaa (oppilaitossivu tunnus))
          ;; Niin
          (is (= (sivun-otsikko) (clojure.string/upper-case (:nimi oppilaitos))))
          (is (= (oppilaitoksen-tieto "oppilaitos.puhelin") puhelin))
          (is (= (oppilaitoksen-tieto "oppilaitos.sahkoposti") sahkoposti))
          (is (= (oppilaitoksen-tieto "oppilaitos.osoite") osoite))
          (is (= (oppilaitoksen-tieto "oppilaitos.postitoimipaikka") (str postinumero " " postitoimipaikka))))))))

(defn oppilaitossivu-sopimukset-data [y-tunnus oppilaitostunnus]
  (let [koulutustoimija (dt/setup-koulutustoimija y-tunnus)
        oppilaitos (dt/setup-oppilaitos oppilaitostunnus y-tunnus)
        tutkintotunnus "TU1"
        tutkintoversio 1
        tutkinto-map (dt/setup-tutkinto-map tutkintotunnus tutkintoversio)
        toimikuntatunnus "ILMA"
        sopimus1 (dt/setup-voimassaoleva-jarjestamissopimus koulutustoimija oppilaitostunnus toimikuntatunnus tutkintoversio)
        sopimus2 (dt/setup-voimassaoleva-jarjestamissopimus koulutustoimija oppilaitostunnus toimikuntatunnus tutkintoversio)
        ]
    (dt/merge-datamaps sopimus1 sopimus2 tutkinto-map
      {:oppilaitokset [oppilaitos]
       :koulutustoimijat [koulutustoimija]
       :toimikunnat [{:tkunta toimikuntatunnus}]})))

   (deftest oppilaitossivu-sopimukset-test []
     (with-webdriver
       ;; Oletetaan, että
       (let [oppilaitostunnus "12345"
             testidata (oppilaitossivu-sopimukset-data "0000000-0" oppilaitostunnus)
             testitutkinto_nimi (:nimi_fi (first (:tutkinnot testidata)))
             vanhentuva-sopimus (get-in testidata [:jarjestamissopimukset 1])
             vanhentuva-sopnro (:sopimusnumero vanhentuva-sopimus)
             ei-vanhentuva-sopimus (get-in testidata [:jarjestamissopimukset 0])
             ei-vanhentuva-sopnro (:sopimusnumero ei-vanhentuva-sopimus)]
         (with-data (assoc-in testidata [:sopimus_ja_tutkinto 1 :sopimus_ja_tutkinto 0 :loppupvm] menneisyydessa)
           (testing "pitäisi näyttaa listoissa uudet ja vanhat oppilaitoksen sopimukset"
             ;; Kun
             (avaa (oppilaitossivu oppilaitostunnus))
             (w/click nayta-vanhat-selector)
             ;; Niin
             (is (= #{[ei-vanhentuva-sopnro testitutkinto_nimi (str menneisyydessa-kayttoliittyman-muodossa " – " tulevaisuudessa-kayttoliittyman-muodossa)]}
                    (sopimuslista ".nykyiset-sopimukset")))
             (is (= #{[vanhentuva-sopnro testitutkinto_nimi (str menneisyydessa-kayttoliittyman-muodossa " – " menneisyydessa-kayttoliittyman-muodossa)]}
                    (sopimuslista ".vanhat-sopimukset"))))))))
