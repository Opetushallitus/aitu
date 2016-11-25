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
            [aitu.infra.suoritus-excel :refer [lue-excel!]]
            [aitu.infra.suoritus-arkisto :as suoritus-arkisto]
            [dk.ative.docjure.spreadsheet :refer [load-workbook]]
            ))

(use-fixtures :each tietokanta-fixture)

; ({:suorittaja -2, :rahoitusmuoto 2, :tutkinto "327128", :opiskelijavuosi 1, :koulutustoimija "1060155-5", :suoritusaika_alku "2016-09-01", :suoritusaika_loppu "2016-09-01", 
; :jarjestamismuoto "oppilaitosmuotoinen", :osat [{:suorittaja_id -2, :arvosana 2, :todistus true, :tutkinnonosa -10002, :arvosanan_korotus false, 
; :osaamisen_tunnustaminen false, :kieli "fi"}]})

(def luku-result
  ["Käsitellään arvioijat.." "Arvioija on jo olemassa tietokannassa (Ilmarinen,Seppo)" 
   "Lisätään uusi arvioija (Kullervoinen,Kullervo)" "Lisätään uusi arvioija (Väinämöinen,Väinö)" "Käsitellään opiskelijat.." 
   "Opiskelijat ok. Käsitellään suoritukset.." "------------------------" "Suoritusrivejä 213 kpl. Suorituksia kirjattu 1 kpl." 
   "------------------------" "------------------ tarkempi loki ----" 
   "Käsitellään suoritus opiskelijalle " "Lisätään suoritus: Lemminkäinen Lieto (pfft.12345) Käsityöyrityksen johtaminen"
   "Suoritukset ok."])

(deftest ^:integraatio excel-import-test
  (let [wb (load-workbook "test-resources/tutosat_perus.xlsx")
        ui-log (lue-excel! wb)]

    (is (= (first (map (juxt :suorittaja :rahoitusmuoto :tutkinto :koulutustoimija) (suoritus-arkisto/hae-kaikki {})))
           [-2 2 "927128" "1060155-5"]))
    (is (= ui-log luku-result))))
  
