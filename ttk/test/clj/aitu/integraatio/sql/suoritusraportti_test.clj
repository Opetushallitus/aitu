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
        rapsa-localized (localdate-coerce (rip-id (suoritus-arkisto/hae-yhteenveto-raportti {})))
   ;     _    (spit "test-resources/suoritusrapsa.edn" (with-out-str (pr rapsa-localized)))
        oikea-tulos (read-string (slurp "test-resources/suoritusrapsa.edn" :encoding "UTF-8"))]
    (is (= rapsa-localized oikea-tulos))
    (testing "yhteenvetoraportti, edelliset 5 minuuttia"
      (let [rapsa-latest (localdate-coerce (rip-id  (suoritus-arkisto/hae-yhteenveto-raportti {:params {:edelliset-kayttaja "true"}})))]
        (is (=  rapsa-latest oikea-tulos))))
    (testing "yhteenvetoraportti, OR-ehto toimikunta ja suoritettava tutkinto"
      (let [tuntematon-toimikunta (localdate-coerce (rip-id  (suoritus-arkisto/hae-yhteenveto-raportti {:params {:toimikunta "vuffmiau"}})))
            molemmat-vaarin (localdate-coerce (rip-id  (suoritus-arkisto/hae-yhteenveto-raportti {:params {:toimikunta "vuffmiau" :suoritettavatutkinto "-200"}})))
            tuloksia-or (localdate-coerce (rip-id  (suoritus-arkisto/hae-yhteenveto-raportti {:params {:toimikunta "vuffmiau" :suoritettavatutkinto "-20000"}})))
            ]
        (is (empty? tuntematon-toimikunta))
        (is (empty? molemmat-vaarin))
        (is (=  tuloksia-or oikea-tulos))))
    ))

(defn paivita-suorituksien-toimikunnat! []
 (sql/exec-raw (str "update suorituskerta set toimikunta='Gulo gulo', tutkinto='924601' where suorittaja=-2 and jarjestelyt='asfasfasfa'")))

(defn paivita-toimikunnan-tutkinto! []
 (sql/exec-raw (str "insert into toimikunta_ja_tutkinto (toimikunta, tutkintotunnus) values ('TK1', '927128')")))

(deftest ^:integraatio suorituskerrat-test-hae-kaikki
  (let [tk1 (lisaa-toimikunta! {:tkunta "TK1" :nimi_fi "Testitoimikunta TK1"})
        wb (load-workbook "test-resources/tutosat_monipuolinen.xlsx")
        ui-log (lue-excel! wb)  ; luodaan suorituksia
        _ (paivita-suorituksien-toimikunnat!)
        _ (paivita-toimikunnan-tutkinto!)
        ]

    (testing "Toimikunta-hakuehtoon valittuna arvo"
      (let [suorituskerrat-tk1 (suoritus-arkisto/hae-kaikki {:toimikunta "TK1"})
            suorituskerrat-gulo (suoritus-arkisto/hae-kaikki {:toimikunta "Gulo gulo"})]
        (is (count suorituskerrat-gulo) 1)
        (is (count suorituskerrat-tk1) 4)
        ))

    (testing "Toimikunta-hakuehtoon valittuna 'Ei valittu'-arvo"
      (let [suorituskerrat (suoritus-arkisto/hae-kaikki {:params {:toimikunta nil}})]
        (is (count suorituskerrat) 4)
        ))

    (testing "Ei toimikunta-hakuehtoa"
      (let [suorituskerrat (suoritus-arkisto/hae-kaikki {:params {}})]
        (is (count suorituskerrat) 5)
        ))
    ))

