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
      (is (= true (:lisatiedot (first not-csv))))
      (is (= true (:vapaateksti_kokemus (first csv))))
      (is (= "foo" (:vapaateksti_kokemus (first not-csv)))))))

(defn ^:private lisaa-jasenesitys!
  ([status] 
    (lisaa-jasenesitys! status {}))
  ([status hlo]
    (let [henkilo (lisaa-henkilo! hlo)]
      (lisaa-jasen! {:toimikunta "Gulo gulo" :henkiloid (:henkiloid henkilo) 
                     :status status :vapaateksti_kokemus "foo"
                     :esittaja -1}))))
  
(deftest ^:integraatio testaa-yhteenveto-laskentalogiikka
  (testing "testataan yhteenveto-taulukon numeroiden laskennan logiikka, happy case"
    (let [_ (lisaa-jasenesitys! "esitetty")
          _ (lisaa-jasenesitys! "nimitetty")
          _ (lisaa-jasenesitys! "peruutettu")
          ; yksi esitetty, yksi nimitetty, yksi peruutettu
          yhteensa-jarjesto (arkisto/hae-yhteenveto -1 (hae-voimassaoleva-toimikausi) false)
          yhteensa-kaikki (arkisto/hae-yhteenveto nil (hae-voimassaoleva-toimikausi) false)
          yhteensa-j1 (first yhteensa-jarjesto)
          yhteensa-j1-kaikki (first yhteensa-kaikki)
          yhteensa-j2-kaikki (second yhteensa-kaikki)]

      (is (= yhteensa-j1 {:nimitetty_miehia 1, :nimitetty_sv 0, :nimitetty_naisia 0, :nimi_fi "Aavasaksalainen testitoimikunta", :nimitetty_fi 1, 
                    :nimi_sv "Aakkosissa Aavasaksa asettuu alkuun", :esitetty_fi 1, :esitetty_naisia 0, :esitetty_miehia 1, :diaarinumero "6510/6502", :esitetty_sv 0}))
      (is (= yhteensa-j1 yhteensa-j1-kaikki)) ; tulos on sama myös silloin kun haetaan kaikkia
      (is (= yhteensa-j2-kaikki {:nimitetty_miehia 0, :nimitetty_sv 0, :nimitetty_naisia 0, :nimi_fi "Lattaraudan taivutuksen testitoimikunta", :nimitetty_fi 0, 
                                 :nimi_sv "Ruotsalaisen lattaraudan testitoimikunta", :esitetty_fi 0, :esitetty_naisia 0, :esitetty_miehia 0, :diaarinumero "80186/8086", :esitetty_sv 0}))
   )))

(deftest ^:integraatio kaksikielisten-summaus
  (testing "kaksikieliset henkilöt pitäisi laskea sekä suomenkielisten että ruotsinkielisten yhteismäärään."
    (let [_ (lisaa-jasenesitys! "esitetty" {:aidinkieli "fi"})
          _ (lisaa-jasenesitys! "esitetty" {:aidinkieli "sv"})
          _ (lisaa-jasenesitys! "esitetty" {:aidinkieli "2k"})
          yhteensa (first (arkisto/hae-yhteenveto -1 (hae-voimassaoleva-toimikausi) false))]

      (is (= yhteensa  {:nimitetty_miehia 0, :nimitetty_sv 0, :nimitetty_naisia 0, :nimi_fi "Aavasaksalainen testitoimikunta", :nimitetty_fi 0, 
                    :nimi_sv "Aakkosissa Aavasaksa asettuu alkuun", :esitetty_fi 2, :esitetty_naisia 0, :esitetty_miehia 3, :diaarinumero "6510/6502", :esitetty_sv 2}))
   )))