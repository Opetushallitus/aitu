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

(ns aitu.integraatio.koodistopalvelu-test
  (:require [clojure.test :refer :all]
            [clj-time.core :as time]
            [aitu.integraatio.koodistopalvelu :refer :all]
            [aitu.util :refer :all]))

;; Muoto jossa data tulee tutkinto-arkistosta
(def tutkinto {:tutkintotunnus "1"
               :voimassa_alkupvm (time/local-date 2014 1 1)
               :voimassa_loppupvm (time/local-date 2199 1 1)
               :tyyppi "03"
               :tutkintotaso "ammattitutkinto"
               :nimi_fi "Testauksen ammattitutkinto"
               :nimi_sv "Samma på svenska"
               :koodistoversio 1
               :opintoala_tkkoodi "OA1"
               :koulutusala_tkkoodi "KA1"})

;; Muoto jossa data tulee koodistopalvelusta
(def koodisto-uusi {:koulutusala_tkkoodi "5"
                    :voimassa_loppupvm (time/local-date 2199 1 1)
                    :tila "HYVAKSYTTY"
                    :nimi_fi "Kylmämestarin erikoisammattitutkinto"
                    :nimi_sv "Specialyrkesexamen för kylmästare"
                    :opintoala_tkkoodi "501"
                    :voimassa_alkupvm (time/local-date 2002 1 1)
                    :tyyppi "03"
                    :tutkintotaso "erikoisammattitutkinto"
                    :tutkintotunnus "357207"})

(def koodisto-muuttunut {:koulutusala_tkkoodi "KA1"
                         :voimassa_loppupvm (time/local-date 2199 1 1)
                         :nimi_fi "Testauksen ammattitutkinto"
                         :nimi_sv "Yrkesexamen för testning"
                         :opintoala_tkkoodi "OA1"
                         :voimassa_alkupvm (time/local-date 2014 1 1)
                         :tyyppi "03"
                         :tutkintotaso "ammattitutkinto"
                         :tutkintotunnus "1"})

(deftest koodi->kasite-test
   (let [koodi {:metadata [{:kieli "FI"
                            :nimi "Rakennusalan perustutkinto"
                            :kuvaus "Rakennusalan perustutkinto"}
                           {:kieli "EN"
                            :nimi "Construction, VQ"
                            :kuvaus "Construction, VQ"}
                           {:kieli "SV"
                            :nimi "Byggnadsbranschen, grundexamen"
                            :kuvaus "Byggnadsbranschen, grundexamen"}]
                :voimassaLoppuPvm "2013-12-31"
                :voimassaAlkuPvm "1997-01-01"
                :versio 1
                :koodiArvo "352201"
                :koodiUri "koulutus_352201"
                :koodisto {:organisaatioOid "1.2.246.562.10.00000000001"
                           :koodistoUri "koulutus"
                           :koodistoVersios [1]}}]
     (is (= (koodi->kasite koodi :tutkintotunnus)
            {:nimi_fi "Rakennusalan perustutkinto"
             :nimi_sv "Byggnadsbranschen, grundexamen"
             :kuvaus_fi "Rakennusalan perustutkinto"
             :kuvaus_sv "Byggnadsbranschen, grundexamen"
             :versio 1
             :koodiUri "koulutus_352201"
             :tutkintotunnus "352201"
             :voimassa_alkupvm (time/local-date 1997 1 1)
             :voimassa_loppupvm (time/local-date 2013 12 31)}))))

(deftest tutkintodata->vertailumuoto-test
  (let [vertailumuoto (tutkintodata->vertailumuoto [tutkinto])]
    (is (= vertailumuoto {:tutkinnot {"1" {:tutkintotunnus "1"
                                           :opintoala "OA1"
                                           :koulutusala "KA1"
                                           :voimassa_alkupvm (time/local-date 2014 1 1)
                                           :voimassa_loppupvm (time/local-date 2199 1 1)
                                           :tyyppi "03"
                                           :tutkintotaso "ammattitutkinto"
                                           :nimi_fi "Testauksen ammattitutkinto"
                                           :nimi_sv "Samma på svenska"
                                           :koodistoversio 1
                                           :tutkintonimikkeet #{}}}
                          :tutkintonimikkeet {}}))))

(deftest koodistodata->vertailumuoto-test
  (let [vertailumuoto (koodistodata->vertailumuoto 1 [koodisto-muuttunut])]
    (is (= vertailumuoto {:tutkinnot {"1" {:tutkintotunnus "1"
                                           :opintoala "OA1"
                                           :koulutusala "KA1"
                                           :voimassa_alkupvm (time/local-date 2014 1 1)
                                           :voimassa_loppupvm (time/local-date 2199 1 1)
                                           :tyyppi "03"
                                           :tutkintotaso "ammattitutkinto"
                                           :nimi_fi "Testauksen ammattitutkinto"
                                           :nimi_sv "Yrkesexamen för testning"
                                           :koodistoversio 1
                                           :tutkintonimikkeet #{}}}
                          :tutkintonimikkeet {}}))))

(deftest tutkinto-muutokset-test
  (let [{:keys [tutkinnot]} (tutkinto-muutokset [tutkinto] 1 [koodisto-uusi koodisto-muuttunut])]
    (is (= tutkinnot {"1" {:nimi_sv ["Yrkesexamen för testning" "Samma på svenska"]
                           :tutkintotunnus nil
                           :opintoala nil
                           :koulutusala nil
                           :voimassa_alkupvm nil
                           :voimassa_loppupvm nil
                           :tyyppi nil
                           :tutkintotaso nil
                           :nimi_fi nil
                           :koodistoversio nil
                           :tutkintonimikkeet nil}
                      "357207" [{:koulutusala "5"
                                 :voimassa_loppupvm (time/local-date 2199 1 1)
                                 :nimi_fi "Kylmämestarin erikoisammattitutkinto"
                                 :nimi_sv "Specialyrkesexamen för kylmästare"
                                 :opintoala "501"
                                 :koodistoversio 1
                                 :voimassa_alkupvm (time/local-date 2002 1 1)
                                 :tyyppi "03"
                                 :tutkintotaso "erikoisammattitutkinto"
                                 :tutkintonimikkeet #{}
                                 :tutkintotunnus "357207"} nil]}))))

(deftest muutokset-test
  (let [vanhat {1 {:arvo 1}
                2 {:arvo 1}
                3 {:arvo 1}}
        uudet {2 {:arvo 1}
               3 {:arvo 2}
               4 {:arvo 1}}]
    (is (= (muutokset uudet vanhat)
           {1 [nil {:arvo 1}]
            3 {:arvo [2 1]}
            4 [{:arvo 1} nil]}))))
