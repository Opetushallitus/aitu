;; Copyright (c) 2015 The Finnish National Board of Education - Opetushallitus
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

(ns aitu.infra.pdf-arkisto-test
  (:require [clojure.test :refer :all]
            [aitu.infra.pdf-arkisto :refer :all]))

(deftest lisaa-elementti-test
  (testing "Ensimmäiseen elementtiin tulee sivunumero"
    (let [uusi-elementti {:x 1 :y 2}]
      (is (= (lisaa-elementti [] uusi-elementti)
             [{:x 1 :y 2 :sivu 1}]))))

  (testing "Kun uusi elementti mahtuu sivulle niin tulee samalle sivulle kun edellinen"
    (let [edellinen [{:sivu 1 :x 0 :y ensimmainen-rivi}]
          uusi-elementti {:x 0 :y -12}
          elementit (lisaa-elementti edellinen uusi-elementti)]
      (is (= (count elementit) 2))
      (is (every? #(= 1 (:sivu %)) elementit))))

  (testing "Kun uusi elementti ei mahdu sivulle niin se tulee seuraavalle sivulle toiselle riville samalle sisennykselle"
    (let [edellinen [{:sivu 1 :x 50 :y footer-tila}]
          uusi-elementti {:x 0 :y -12}
          elementit (lisaa-elementti edellinen uusi-elementti)]
      (is (= (count elementit) 2))
      (is (= (-> elementit last)
             {:sivu 2 :x 50 :y (- ensimmainen-rivi 24)}))))

  (testing "Kun uusi elementti ei mahdu sivulle niin se tulee seuraavalle sivulle"
    (let [edellinen [{:sivu 1 :x 0 :y footer-tila}]
          uusi-elementti {:x 0 :y -12}
          elementit (lisaa-elementti edellinen uusi-elementti)]
      (is (= (-> elementit first :sivu) 1))
      (is (= (-> elementit last :sivu) 2)))))

(deftest yhdista-sanat-test
  (with-redefs [aitu.infra.pdf-arkisto/tekstin-pituus (fn [fontti fonttikoko teksti] (count teksti))]
    (let [sanat ["eka " "toka " "kolmas " "neljas"]]
      (testing "Jos vapaata tilaa vähemmän kun minkään sanan pituus niin jokainen tulee omalle rivilleen"
        (is (= (yhdista-sanat sanat "fontti" "fonttikoko" 4)
               sanat)))

      (testing "Jos vapaata tilaa sanojen yhdistämiseen niin yhdistää niin kauan kun vapaata tilaa enemmän kun sanojen pituus"
        (is (= (yhdista-sanat sanat "fontti" "fonttikoko" 14)
               ["eka toka " "kolmas neljas"])))

      (testing "Jos vapaata tilaa riittävästi niin yhdistää kaikki sanat"
        (is (= (yhdista-sanat sanat "fontti" "fonttikoko" 30)
               ["eka toka kolmas neljas"]))))))
