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

(ns aitu.timeutil-test
  (:require [clojure.test :refer [deftest is are testing]]
            [aitu.timeutil :refer :all]
            [clj-time.core :as time]))

(deftest merkitse-voimassaolevat-test
  (testing
    "merkitse voimassaolevat:"
    (let [voimassaolon-paivitysfunktio (fn [entity arvo] :paivitetty)]
      (testing
        "päivittää kentän alkioiden voimassa-kentät:"
        (are [kuvaus entity kentta]
             (is (every? #{:paivitetty}
                         (map :voimassa
                              (get-in
                                (merkitse-voimassaolevat entity kentta voimassaolon-paivitysfunktio)
                                [kentta]))) kuvaus)
             "kentän vektori on tyhjä" {:kentta [{}]} :kentta
             "kentän vektorissa on yksi arvo" {:kentta [{}]} :kentta
             "kentän vektorissa on useampi arvo" {:kentta [{}, {}]} :kentta))
      (testing
        "palauttaa päivitetyn kentän vektorina:"
        (is (vector?
              (get-in
                (merkitse-voimassaolevat {:kentta [{}]} :kentta voimassaolon-paivitysfunktio)
                [:kentta]))
            "kenttä on vektori")))))

(deftest voimassa-test
  (testing "tarkalleen yksi on voimassa tällä hetkellä"
    (let [datachunk [{:id "1", :versio "1", :nimi "fo",
                      :voimassaalku (time/date-time 1986)
                      :voimassaloppu (time/date-time 2000)}
                     {:id "1", :versio "2", :nimi "foaa",
                      :voimassaalku (time/date-time 2000)
                      :voimassaloppu (time/date-time 2005)}
                     {:id "1", :versio "3", :nimi "fofa",
                      :voimassaalku (time/date-time 2005)
                      :voimassaloppu (time/date-time 2105)}]]
      (is (= 1 (count (filter voimassa? datachunk)))))))
