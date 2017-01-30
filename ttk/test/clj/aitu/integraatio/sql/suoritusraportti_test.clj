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

(ns aitu.integraatio.sql.suoritusraportti-test
  (:require [clojure.test :refer [deftest testing is are use-fixtures]]
            [clojure.data]
            [korma.core :as sql]
            [aitu.integraatio.sql.test-util :refer [tietokanta-fixture]]
            [aitu.rest-api.suoritus :as suoritus-api]
            [aitu.integraatio.sql.test-data-util :refer :all]
            [aitu.infra.suoritus-excel :refer [lue-excel!]]
            [aitu.infra.suoritus-arkisto :as suoritus-arkisto]
            [dk.ative.docjure.spreadsheet :refer [load-workbook]]            
            ))

(use-fixtures :each tietokanta-fixture)

(defn localdate-coerce [raps]
  (clojure.walk/postwalk #(if (= org.joda.time.LocalDate (type %)) (suoritus-api/localdate->str %) %) raps))

(defn rip-id [raps]
  (clojure.walk/postwalk #(if (map? %) (dissoc % :suorituskerta_id) %) raps))

(defn paivita-suoritukset-toiselle-koulutustoimijalle! []
 (sql/exec-raw (str "update suorituskerta set koulutustoimija='KT1' where suorittaja= -2")))
 
(deftest ^:integraatio yhteenvetoraportti-test-peruscase
  (let [kt1 (lisaa-koulutustoimija! {:ytunnus "KT1" :nimi_fi "bar bar"}) 
        wb (load-workbook "test-resources/tutosat_monipuolinen.xlsx")
        ui-log (lue-excel! wb)  ; luodaan suorituksia 
        _ (paivita-suoritukset-toiselle-koulutustoimijalle!)
        rapsa (rip-id (suoritus-arkisto/hae-yhteenveto-raportti {}))
        rapsa-localized (localdate-coerce rapsa)  
     ;   _    (spit "test-resources/suoritusrapsa.edn" (with-out-str (pr rapsa-localized)))    
        oikea-tulos (read-string (slurp "test-resources/suoritusrapsa.edn"))]
    ;(println (clojure.data/diff rapsa-localized oikea-tulos))
    (is (= rapsa-localized oikea-tulos))

))
