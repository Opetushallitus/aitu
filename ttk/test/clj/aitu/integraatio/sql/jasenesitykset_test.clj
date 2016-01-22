(ns aitu.integraatio.sql.jasenesitykset-test
  (:require [clojure.test :refer [deftest testing is are use-fixtures]]
            [aitu.infra.jasenesitykset-arkisto :as arkisto]
            [aitu.integraatio.sql.test-util :refer [tietokanta-fixture]]
            [aitu.integraatio.sql.test-data-util :refer :all]))

(use-fixtures :each tietokanta-fixture)

(deftest ^:integraatio hae-csv-toimii
  (testing "hakee jäsenesitysten tiedot csv-muodossa"
    (let [henkilo (lisaa-henkilo! {:lisatiedot "fubar"})
          jasenyys (lisaa-jasen! {:toimikunta "Gulo gulo" :henkiloid (:henkiloid henkilo) 
                                  :status "esitetty" :vapaateksti_kokemus "foo"
                                  :esittaja -1})
          csv (arkisto/hae -1 {} true)
          not-csv (arkisto/hae -1 {:henkilotiedot "true"} false)]
      (println csv)
      (println not-csv)
      (is (= true (:lisatiedot (first not-csv))))
      (is (= true (:vapaateksti_kokemus (first csv))))
      (is (= "foo" (:vapaateksti_kokemus (first not-csv)))))))