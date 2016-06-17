(ns aitu.integraatio.sql.asettamispaatos-test
  (:require [clojure.test :refer [deftest testing is are use-fixtures]]
            [clj-time.core :as time]
            [aitu.infra.paatos-arkisto :as arkisto :refer :all]
            [korma.core :as sql]
            [aitu.integraatio.sql.test-util :refer [tietokanta-fixture]]
            [aitu.integraatio.sql.test-data-util :as data, :refer :all]))

(use-fixtures :each tietokanta-fixture)

(deftest ^:integraatio asettamispaatokset
  (let [esitetty-hlo (lisaa-henkilo! {:lisatiedot "fubar" :etunimi "Erkka" :sukunimi "Esitetty"})
        esitetty-jasenyys (lisaa-jasen! {:toimikunta "Gulo gulo" :henkiloid (:henkiloid esitetty-hlo)
                                         :status "esitetty" :vapaateksti_kokemus "foo"
                                         :esittaja -1})
        paatetty-hlo (lisaa-henkilo! {:lisatiedot "fubarbar" :etunimi "Pirkko" :sukunimi "Päätetty"})
        paatetty-jasenyys (lisaa-jasen! {:toimikunta "Gulo gulo" :henkiloid (:henkiloid paatetty-hlo)
                                         :status "nimitetty" :vapaateksti_kokemus "foo"
                                         :esittaja -1})]
        (let [paatosteksti "Diipa daapa daa. Laalaa. Kthxbye."
              diaarinumero "6510/6502"
              data {:paivays "12/12/2012"
                    :esittelija {:asema "esittelijä, asema"
                                 :nimi "Esittelijä E"}
                    :hyvaksyja {:asema "Suuri Johtaja"
                                :nimi "Jaana Johtaja"}
                    :jakelu nil 
                    :tiedoksi nil
                    :tyhjiariveja 0
                    :paatosteksti paatosteksti}
            pdf (arkisto/luo-asettamispaatos-data :fi diaarinumero data)
            pdf-data (:data pdf)]
          (testing "Vain nimitetyt jäsenet mainitaan jäsenlistassa"
            (is (= (:jakelu pdf-data) '("Pirkko Päätetty")))
            (is (empty? (:tiedoksi pdf-data))))
          (is (= (:paatosteksti pdf-data) paatosteksti))
          (is (= (:esittelija pdf-data) {:asema "esittelijä, asema" 
                                         :nimi "Esittelijä E"}))
 )))
