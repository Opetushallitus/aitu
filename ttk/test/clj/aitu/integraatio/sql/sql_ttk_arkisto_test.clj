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

(ns aitu.integraatio.sql.sql-ttk-arkisto-test
  (:require [clojure.test :refer [deftest testing is are use-fixtures]]
            [korma.core :as sql]
            [aitu.infra.ttk-arkisto :as arkisto]
            [aitu.infra.henkilo-arkisto :as henkilo-arkisto]
            [aitu.toimiala.toimikunta :as toimikunta]
            [aitu.integraatio.sql.test-util :refer [tietokanta-fixture]]
            [clj-time.core :as time]
            [aitu.integraatio.sql.test-data-util :refer :all]
            [aitu.test-timeutil :refer :all]))

(use-fixtures :each tietokanta-fixture)

(defn hae-ja-taydenna
  [diaarinumero]
  (toimikunta/taydenna-toimikunta (arkisto/hae diaarinumero)))

(deftest ^:integraatio sql-crud-lisaa!
  "Testaa crud operaatiot tietokantaan. Aiheuttaa kannan tyhjennyksen tällä hetkellä."
  []
  (let [ttk-count (count (arkisto/hae-kaikki))]
    (lisaa-toimikunta!)
    (is (= (count (arkisto/hae-kaikki)) (inc ttk-count)))))

(deftest ^:integraatio hae-ttk-test
  []
  (testing "pitäisi palauttaa nil olemattomalla diaarinumerolla"
    (is (nil? (hae-ja-taydenna "123/456/7890")))))

(deftest ^:integraatio poista-jasenyys-test!
  "Testaa jäsenyyden poistamisen"
  []
  (let [tkunta "T12345"
        diaarinumero "2013/01/001"]
    (lisaa-toimikunta! {:diaarinumero diaarinumero, :tkunta tkunta})
    (lisaa-henkilo! {:henkiloid -1234})
    (let [alkuperainen-jasen (lisaa-jasen! {:henkiloid -1234, :toimikunta tkunta, :loppupvm (vuoden-kuluttua)})
          alkuperainen-tilanne (hae-ja-taydenna diaarinumero)
          poistettava-jasen (assoc alkuperainen-jasen :poistettu true)
          _ (arkisto/paivita-tai-poista-jasenyys! diaarinumero poistettava-jasen)
          uusi-tilanne (hae-ja-taydenna diaarinumero)]
      (testing "henkilö on alussa jäsenenä toimikunnassa"
        (is (= (-> alkuperainen-tilanne :jasenyys first :henkiloid)
                   (:henkiloid alkuperainen-jasen))))
      (testing "poiston jälkeen henkilö ei ole jäsenenä toimikunnassa"
        (is (nil? (some-> uusi-tilanne :jasenyys first :henkiloid)))))))

(deftest ^:integraatio paivita-jasenen-tietoja-test!
  "Testaa jäsenyyden edustus ja rooli tietojen päivittämisen"
  []
  (let [tkunta "T12345"
        diaarinumero "2013/01/001"]
    (lisaa-toimikunta! {:tkunta tkunta, :diaarinumero diaarinumero})
    (lisaa-henkilo! {:henkiloid -1234})
    (let [alkuperainen-jasen (lisaa-jasen! {:toimikunta tkunta, :henkiloid -1234, :edustus "opettaja", :rooli "jasen", :alkupvm (kuukausi-sitten), :loppupvm (vuoden-kuluttua)})
          paivitettava-jasen (assoc alkuperainen-jasen :edustus "asiantuntija" :rooli "sihteeri")
          _ (arkisto/paivita-tai-poista-jasenyys! diaarinumero paivitettava-jasen)
          paivitetty-jasen (-> (hae-ja-taydenna diaarinumero)
                             :jasenyys
                             first)]
      (testing "päivityksen jälkeen edustus on muuttunut"
        (is (= (:edustus paivitettava-jasen) (:edustus paivitetty-jasen))))
      (testing "päivitysen jälkeen rooli on muuttunut"
        (is (= (:rooli paivitettava-jasen) (:rooli paivitetty-jasen)))))))

(deftest ^:integraatio hae-tutkinnolla-test
  []
  (lisaa-toimikunta! {:tkunta "T12345"
                      :tilikoodi "1234"
                      :diaarinumero "2013/01/001"
                      :toimikausi_id 1})
  (lisaa-toimikunta! {:tkunta "T56789"
                      :tilikoodi "5678"
                      :diaarinumero "2013/01/002"
                      :toimikausi_id 2})
  (lisaa-koulutus-ja-opintoala! {:koulutusalakoodi "KA1"} {:opintoalakoodi "OA1", :koulutusalakoodi "KA1", :selite_fi "opintoala1"})
  (lisaa-koulutus-ja-opintoala! {:koulutusalakoodi "KA2"} {:opintoalakoodi "OA2", :koulutusalakoodi "KA2", :selite_fi "opintoala2"})
  (lisaa-tutkinto! {:tutkintotunnus "T1"
                    :nimi_fi "tutkinto1"
                    :opintoala "OA1"})
  (lisaa-tutkinto! {:tutkintotunnus "T2"
                    :nimi_fi "tutkinto2"
                    :opintoala "OA2"})
  (arkisto/lisaa-tutkinto! {:toimikunta "T12345"
                            :tutkintotunnus "T1"})
  (arkisto/lisaa-tutkinto! {:toimikunta "T56789"
                            :tutkintotunnus "T2"})
  (testing "pitäisi löytää kaksi toimikuntaa tyhjällä haulla"
    (is (= (count (arkisto/hae-tutkinnolla "" "kaikki")) 2)))
  (testing "pitäisi löytää toimikunta nykyiseltä toimikaudelta"
    (is (= (map :tkunta (arkisto/hae-tutkinnolla "" "nykyinen")) ["T56789"])))
  (testing "pitäisi löytää toimikunta tutkinnon nimellä"
    (is (= (map :tkunta (arkisto/hae-tutkinnolla "tutkinto1" "kaikki")) ["T12345"])))
  (testing "pitäisi löytää toimikunta opintoalan nimellä"
    (is (= (map :tkunta (arkisto/hae-tutkinnolla "opintoala1" "kaikki")) ["T12345"]))))

(deftest ^:integraatio hae-termilla-test
  []
  (testing "pitäisi löytää toimikunta haettaessa suomenkielisellä nimellä"
    (let [termi-fi "Testitoimikunnan nimi"
          termi-sv "Testitoimikunnan nimi (sv)"
          osumia-alussa-fi (count (arkisto/hae-termilla termi-fi))
          osumia-alussa-sv (count (arkisto/hae-termilla termi-sv))
          _ (lisaa-toimikunta! {:nimi_fi termi-fi, :nimi_sv termi-sv})
          osumat-fi (arkisto/hae-termilla termi-fi)
          osumat-sv (arkisto/hae-termilla termi-sv)]
      (testing "pitäisi löytää toimikunta haettaessa suomenkielisellä nimellä"
        (is (> (count osumat-fi) osumia-alussa-fi)))
      (testing "pitäisi löytää toimikunta haettaessa ruotsinkielisellä nimellä"
        (is (> (count osumat-sv) osumia-alussa-sv)))
      (testing "osumat pitäisi sisältää toimikuntien nimet"
        (is (every? #(and (contains? % :nimi_fi) (contains? % :nimi_sv)) osumat-fi))
        (is (every? #(and (contains? % :nimi_fi) (contains? % :nimi_sv)) osumat-sv))))))

(deftest ^:integraatio lisaa-uusi-jasenyys-test!
  "Testaa jäsenyyden lisääminen"
  []
  (lisaa-toimikunta! {:tkunta "T12345", :diaarinumero "2013/01/001"})
  (lisaa-henkilo! {:henkiloid -1234})
  (let [alkuperainen-tilanne (hae-ja-taydenna "2013/01/001")]
    (testing "lisäyksen jälkeen jäsenyys on liitettynä toimikuntaan"
      ;; kun
      (lisaa-jasen! {:toimikunta "T12345", :henkiloid -1234})
      ;; niin
      (let [uusi-tilanne (hae-ja-taydenna "2013/01/001")]
        (is (= ( + 1 (count (:jasenyys alkuperainen-tilanne)))
               (count (:jasenyys uusi-tilanne)))) ))))

(deftest ^:integraatio muokkaa-toimikunnan-perustietoja-test!
  "Testaa toimikunnan perustietojen muokkaaminen"
  []
  (let [alkuperainen-toimikunta (lisaa-toimikunta! {:diaarinumero "2013/01/001", :kielisyys "fi"})]
    ;; Kun
    (arkisto/paivita! (:diaarinumero alkuperainen-toimikunta) (assoc alkuperainen-toimikunta :kielisyys "sv"))
    ;; Niin
    (let [uusi-tilanne (hae-ja-taydenna "2013/01/001")]
      (is (not= (:kielisyys uusi-tilanne) (:kielisyys alkuperainen-toimikunta))))))

(deftest ^:integraatio muokkaa-toimikunnan-sahkoposti-test!
  "Testaa toimikunnan sähköpostiosoitteen muokkaaminen"
  []
  (let [alkuperainen-toimikunta (lisaa-toimikunta! {:diaarinumero "2013/01/001", :sahkoposti "vanha@posti.fi"})]
    ;; Kun
    (arkisto/paivita! (:diaarinumero alkuperainen-toimikunta) (assoc alkuperainen-toimikunta :sahkoposti "uusi@mail.com"))
    ;; Niin
    (let [uusi-tilanne (hae-ja-taydenna "2013/01/001")]
      (is (not= (:sahkoposti uusi-tilanne) (:sahkoposti alkuperainen-toimikunta))))))

(deftest ^:integraatio jasenyyden-voimassaolo-vanhalla-toimikunnalla-test!
  "Testaa jäsenyyden voimassaolon vanhalla toimikunnalla"
  []
  (lisaa-toimikunta! {:tkunta "T12345", :diaarinumero "2013/01/001", :toimikausi_alku menneisyydessa, :toimikausi_loppu menneisyydessa})
  (lisaa-henkilo! {:henkiloid -1111})
  (let [jasen (lisaa-jasen! {:toimikunta "T12345", :henkiloid -1111, :alkupvm menneisyydessa, :loppupvm tulevaisuudessa})]
    ;; Niin
    (let [haettu-toimikunta (hae-ja-taydenna "2013/01/001")]
      (is (false? (:voimassa (first (:jasenyys haettu-toimikunta))))))))

(deftest ^:integraatio jasenyyden-voimassaolo-voimassaolevalla-toimikunnalla-test!
  "Testaa jäsenyyden voimassaolon voimassaolevalla toimikunnalla"
  []
  (lisaa-toimikunta! {:tkunta "T12345", :diaarinumero "2013/01/001", :toimikausi_alku menneisyydessa, :toimikausi_loppu tulevaisuudessa})
  (lisaa-henkilo! {:henkiloid -1111})
  (let [jasen (lisaa-jasen! {:toimikunta "T12345", :henkiloid -1111, :alkupvm menneisyydessa, :loppupvm tulevaisuudessa})]
    ;; Niin
    (let [haettu-toimikunta (hae-ja-taydenna "2013/01/001")]
      (is (true? (:voimassa (first (:jasenyys haettu-toimikunta))))))))
