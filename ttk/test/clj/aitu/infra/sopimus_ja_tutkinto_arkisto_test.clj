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

(ns aitu.infra.sopimus-ja-tutkinto-arkisto-test
  (:require [clojure.test :refer [deftest testing is are]]
            [aitu.infra.sopimus-ja-tutkinto-arkisto :refer :all]))

(deftest liita-sopimus-ja-tutkinto-riviin-test
  (testing
    "liitä sopimus_ja_tutkinto-riviin:"
    (with-redefs [aitu.integraatio.sql.jarjestamissopimus/hae
                  (fn [jarjestamissopimusid] {:jarjestamissopimus-haettu-idlla jarjestamissopimusid})
                  aitu.integraatio.sql.jarjestamissopimus/hae-sopimus-ja-tutkinto-rivin-jarjestamissuunnitelmat
                  (fn [sopimus_ja_tutkinto_id] [{:jarjestamissuunnitelma-haettu-idlla sopimus_ja_tutkinto_id}])
                  aitu.integraatio.sql.jarjestamissopimus/hae-sopimus-ja-tutkinto-rivin-liitteet
                  (fn [sopimus_ja_tutkinto_id] [{:liitteet-haettu-idlla sopimus_ja_tutkinto_id}])]
      (let [rivi {:tutkintoversio "tutkintoversio1"
                  :jarjestamissopimusid "sopimus1"
                  :sopimus_ja_tutkinto_id "s_ja_t_id1"}
            tutkinnon-haku-fn (fn [tutkintoversio] {:tutkintoversio-haettu-tunnuksella tutkintoversio})]
        (testing
          "hakee tutkinnon"
          (is (= (get-in (liita-sopimus-ja-tutkinto-riviin tutkinnon-haku-fn rivi)
                         [:tutkintoversio :tutkintoversio-haettu-tunnuksella])
                 "tutkintoversio1")))
        (testing
          "hakee jarjestamissopimuksen"
          (is (= (get-in (liita-sopimus-ja-tutkinto-riviin tutkinnon-haku-fn rivi)
                         [:jarjestamissopimus :jarjestamissopimus-haettu-idlla])
                 "sopimus1")))
        (testing
          "hakee järjestämissuunnitelmat"
          (is (= (get-in (liita-sopimus-ja-tutkinto-riviin tutkinnon-haku-fn rivi)
                         [:jarjestamissuunnitelmat 0 :jarjestamissuunnitelma-haettu-idlla])
                 "s_ja_t_id1")))
        (testing
          "hakee sopimuksen liitteet"
          (is (= (get-in (liita-sopimus-ja-tutkinto-riviin tutkinnon-haku-fn rivi)
                         [:liitteet 0 :liitteet-haettu-idlla])
                 "s_ja_t_id1")))))))

(deftest liita-sopimus-ja-tutkinto-riveihin-test
  (testing
    "liitä sopimus_ja_tutkinto-riveihin:"
    (with-redefs [liita-sopimus-ja-tutkinto-riviin
                  (fn [haku-fn rivi] {:sopimus-ja-tutkinto-rivi-liitetty true})]
      (let [rivit [{:nimi "rivi1"}
                   {:nimi "rivi2"}]]
        (testing
          "liittää kaikki rivit"
          (is (every? true? (map :sopimus-ja-tutkinto-rivi-liitetty (liita-sopimus-ja-tutkinto-riveihin identity rivit)))))))))
