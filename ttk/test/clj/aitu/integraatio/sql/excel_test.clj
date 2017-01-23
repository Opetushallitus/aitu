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

(ns aitu.integraatio.sql.excel-test
  (:require [clojure.test :refer [deftest testing is are use-fixtures]]
            [aitu.integraatio.sql.test-util :refer [tietokanta-fixture]]
            [aitu.integraatio.sql.test-data-util :refer :all]
            [aitu.infra.suoritus-excel :refer [parse-opiskelija nilstr lue-excel! paivita-opiskelija-tiedot!]]
            [aitu.infra.suoritus-arkisto :as suoritus-arkisto]
            [aitu.infra.suorittaja-arkisto :as suorittaja-arkisto]            
            [dk.ative.docjure.spreadsheet :refer [load-workbook]]
            ))

(use-fixtures :each tietokanta-fixture)

; ({:suorittaja -2, :rahoitusmuoto 2, :tutkinto "327128", :opiskelijavuosi 1, :koulutustoimija "1060155-5", :suoritusaika_alku "2016-09-01", :suoritusaika_loppu "2016-09-01", 
; :jarjestamismuoto "oppilaitosmuotoinen", :osat [{:suorittaja_id -2, :arvosana 2, :todistus true, :tutkinnonosa -10002, :arvosanan_korotus false, 
; :osaamisen_tunnustaminen false, :kieli "fi"}]})

(def luku-result
  ["Käsitellään arvioijat.." "Arvioijatietojen versionumero 749448266" "Arvioija on jo olemassa tietokannassa (Ilmarinen,Seppo)" 
   "Lisätään uusi arvioija (Kullervoinen,Kullervo)" "Lisätään uusi arvioija (Väinämöinen,Väinö)" "Käsitellään opiskelijat.." 
   "Opiskelijat ok. Käsitellään suoritukset.." "------------------------" "Suoritusrivejä 1 kpl. Suorituksia kirjattu 1 kpl." 
   "------------------------" "------------------ tarkempi loki ----" 
   "Käsitellään suoritus opiskelijalle Lemminkäinen Lieto (pfft.12345)" "Lisätään suoritus: Lemminkäinen Lieto (pfft.12345) Käsityöyrityksen johtaminen"
   "Suoritukset ok."])

(def luku-result-virheita
  ["Käsitellään arvioijat.."
 "Arvioijatietojen versionumero 749448266"
 "Arvioija on jo olemassa tietokannassa (Ilmarinen,Seppo)"
 "Lisätään uusi arvioija (Kullervoinen,Kullervo)"
 "Lisätään uusi arvioija (Väinämöinen,Väinö)"
 "Käsitellään opiskelijat.."
 "Opiskelijat ok. Käsitellään suoritukset.."
 "------------------------"
 "Suoritusrivejä 4 kpl. Suorituksia kirjattu 1 kpl."
 "------------------------"
 "Virhe suoritusten käsittelyssä, rivi: 6 . Tieto: tutkintotunnus . Tarkista solujen sisältö: java.lang.IllegalStateException: Cannot get a numeric value from a text cell"
 "Virhe suoritusten käsittelyssä, rivi: 7 . Tieto: tutkinnon osa . Tarkista solujen sisältö: java.lang.NullPointerException"
 "Virhe suoritusten käsittelyssä, rivi: 8 . Tieto: tutkintotunnus . Tarkista solujen sisältö: java.lang.IllegalStateException: Cannot get a numeric value from a text cell"
 "------------------ tarkempi loki ----"
 "Käsitellään suoritus opiskelijalle Lemminkäinen Lieto (pfft.12345)"
 "Lisätään suoritus: Lemminkäinen Lieto (pfft.12345) Käsityöyrityksen johtaminen"
 "Käsitellään suoritus opiskelijalle Opiskelija Orvokki (fan.far.12345)"
 "Virhe suoritusten käsittelyssä, rivi: 6 . Tieto: tutkintotunnus . Tarkista solujen sisältö: java.lang.IllegalStateException: Cannot get a numeric value from a text cell"
 "Käsitellään suoritus opiskelijalle Opiskelija Orvokki (fan.far.12345)"
 "Virhe suoritusten käsittelyssä, rivi: 7 . Tieto: tutkinnon osa . Tarkista solujen sisältö: java.lang.NullPointerException"
 "Käsitellään suoritus opiskelijalle Lemminkäinen Lieto (pfft.12345)"
 "Virhe suoritusten käsittelyssä, rivi: 8 . Tieto: tutkintotunnus . Tarkista solujen sisältö: java.lang.IllegalStateException: Cannot get a numeric value from a text cell"
 "Suoritukset ok."]
)

(deftest ^:integraatio excel-import-test
  (let [wb (load-workbook "test-resources/tutosat_perus.xlsx")
        ui-log (lue-excel! wb)]

    (is (= (first (map (juxt :suorittaja :rahoitusmuoto :tutkinto :koulutustoimija) (suoritus-arkisto/hae-kaikki {})))
           [-2 2 "927128" "1060155-5"]))
    (is (= ui-log luku-result))))

(deftest ^:integraatio parse-opiskelija-test
  (is (= {:nimi "a b", :oid nil, :hetu nil} (parse-opiskelija "a b ()")))
  (is (= {:nimi "a b", :oid "d", :hetu nil} (parse-opiskelija "a b (d)")))
  (is (= {:nimi "a b", :oid "d", :hetu "1234-x"} (parse-opiskelija "a b (d,1234-x)"))))

(deftest ^:integraatio paivita-opiskelija-test
  (let [ui-log (atom [])
        orvokki {:hetu "120303-112X"
                 :etunimi "Orvokki"
                 :sukunimi "Outolempi"}
        uusi (suorittaja-arkisto/lisaa! orvokki)
        ops (suorittaja-arkisto/hae-kaikki)
        orv-uusi {:hetu "120303-112X"
                  :etunimi "Otso"
                  :sukunimi "Outolempi"}]
    (println ops)
    (is (= false (paivita-opiskelija-tiedot! {:hetu "fu"} ops ui-log)))
    (is (= true (paivita-opiskelija-tiedot! orvokki ops ui-log)))
    (testing "Nimenmuutos päivittää tiedot"
       (is (= true (paivita-opiskelija-tiedot! orv-uusi ops ui-log)))
       (is (= ["Henkilön nimi on muuttunut, päivitetään nimi: {:etunimi \"Orvokki\", :sukunimi \"Outolempi\"} -> {:etunimi \"Otso\", :sukunimi \"Outolempi\"}"]
              @ui-log))       
       (is (= "Otso" (:etunimi (suorittaja-arkisto/hae (:suorittaja_id uusi))))))))

(deftest ^:integraatio  nilstr-test
  (is (nil? (nilstr "")))
  (is (nil? (nilstr nil)))
  (is (= "a" (nilstr "a"))))

(deftest ^:integraatio excel-import-test-virhekasittely
  (let [wb (load-workbook "test-resources/tutosat_pahastivialla.xlsx")
        ui-log (lue-excel! wb)]
    (is (= ui-log luku-result-virheita))))
    
