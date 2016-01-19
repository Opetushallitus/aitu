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

(ns aitu.integraatio.sql.henkilo-test
  (:require [clojure.test :refer [deftest testing is are use-fixtures]]
            [aitu.infra.henkilo-arkisto :as arkisto]
            [aitu.integraatio.sql.test-util :refer [tietokanta-fixture]]
            [aitu.integraatio.sql.test-data-util :refer :all]
            [aitu.test-timeutil :refer :all]
            [aitu.toimiala.henkilo :as henkilo]))

(use-fixtures :each tietokanta-fixture)

(defn hae-ja-taydenna
  [henkiloid]
  (henkilo/taydenna-henkilo (arkisto/hae-hlo-ja-ttk henkiloid nil)))

(deftest ^:integraatio hae-ja-taydenna-test
  (testing "hae-ja-taydenna palauttaa henkilön jolla on toimikunnan ja jäsenyyden tiedot täydennettynä"
    (let [toimikunta (lisaa-toimikunta! {:tkunta "TKUNTA"})
          henkilo (lisaa-henkilo!)
          jasenyys (lisaa-jasen! {:toimikunta (:tkunta toimikunta) :henkiloid (:henkiloid henkilo)})
          haettu-henkilo (hae-ja-taydenna (:henkiloid henkilo))]
        (is (contains? haettu-henkilo :jasenyys))
        (is (contains? (first (:jasenyys haettu-henkilo)) :voimassa))
        (is (contains? (:ttk (first (:jasenyys haettu-henkilo))) :voimassa)))))

(deftest ^:integraatio hae-nimella
  (let [a (arkisto/hae-hlo-nimella "Jäsen" "Jäsenjärjestö" -1)]
    (is (= (count a) 1))))
