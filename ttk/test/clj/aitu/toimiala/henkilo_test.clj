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

(ns aitu.toimiala.henkilo-test
    (:require [clojure.test :refer [deftest is testing]]
              [aitu.toimiala.henkilo :refer :all]
              [aitu.integraatio.sql.test-data-util :refer [default-henkilo]]))

(deftest piilota-salaiset-henkiloilta-yllapitaja-test
  (testing "Testataan kenttien säilyminen ylläpitäjä tason käyttäjällä"
    (let [henkilo (assoc default-henkilo :sahkoposti_julkinen true)]
      (with-redefs [aitu.toimiala.kayttajaoikeudet/yllapitaja? (constantly true)]
        (is (= [henkilo henkilo] (piilota-salaiset-henkiloilta [henkilo henkilo])))))))

(deftest piilota-salaiset-henkiloilta-kayttaja-test
  (testing "Testataan kenttien poistuminen normaalilla käyttäjällä"
    (let [henkilo (assoc default-henkilo :sahkoposti_julkinen true)]
      (with-redefs [aitu.toimiala.kayttajaoikeudet/yllapitaja? (constantly false)]
        (let [rajoitettu-henkilo (dissoc henkilo :osoite :postinumero :postitoimipaikka :puhelin)]
          (is (= [rajoitettu-henkilo rajoitettu-henkilo]
                 (piilota-salaiset-henkiloilta [henkilo henkilo]))))))))

(deftest piilota-salaiset-avaimelta-kayttaja-test
  (testing "Testataan kenttien poistuminen normaalilla käyttäjällä sisemmältä avaimelta"
    (let [henkilo (assoc default-henkilo :sahkoposti_julkinen true)
          avainrakenne {:jasenyys [henkilo henkilo]}]
      (with-redefs [aitu.toimiala.kayttajaoikeudet/yllapitaja? (constantly false)]
        (let [rajoitettu-henkilo (dissoc henkilo :osoite :postinumero :postitoimipaikka :puhelin)]
          (is (= [rajoitettu-henkilo rajoitettu-henkilo]
                 (:jasenyys (piilota-salaiset avainrakenne :jasenyys)))))))))

(deftest poista-salaiset-henkilolta-ei-sahkopostia-test
  (testing "Testataan kenttien poistaminen käyttäjällä"
    (let [henkilo (assoc default-henkilo :sahkoposti_julkinen true)
          rajoitettu-henkilo (dissoc henkilo :osoite :postinumero :postitoimipaikka :puhelin)]
      (is (= rajoitettu-henkilo (poista-salaiset-henkilolta henkilo))))))

(deftest poista-salaiset-henkilolta-vain-sahkoposti-test
  (testing "Testataan kenttien poistaminen käyttäjällä"
    (let [henkilo (assoc default-henkilo :osoite_julkinen true :puhelin_julkinen true)
          rajoitettu-henkilo (dissoc henkilo :sahkoposti)]
      (is (= rajoitettu-henkilo (poista-salaiset-henkilolta henkilo))))))
