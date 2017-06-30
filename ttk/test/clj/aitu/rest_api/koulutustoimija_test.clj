(ns aitu.rest-api.koulutustoimija-test
  (:require
    [clojure.test :refer :all]
    [peridot.core :as peridot]
    [oph.korma.korma-auth :as auth]
    [aitu.integraatio.sql.sql-koulutustoimija-arkisto-test :refer [kt-testidata!]]
    [aitu.integraatio.sql.test-data-util :refer :all]
    [aitu.rest-api.session-util :refer :all]))

; http://192.168.50.1:8080/api/koulutustoimija/haku/ala?tunnus=102754
; http://192.168.50.1:8080/api/koulutustoimija/haku/ala?sopimuksia=kylla&tunnus=100321
; Request URL:http://192.168.50.1:8080/api/koulutustoimija/haku/ala?sopimuksia=kylla&tunnus=324601

;;
;; ******** TODO: Nämä pitää katselmoida, kun Tunnus-hakuehdolla haku korjataan ********
;;
(deftest ^:integraatio koulutustoimija-haku
  (let [crout (init-peridot!)]
    (run-with-db kt-testidata!
      #(let [ei-tuloksia (-> (peridot/session crout)
                           (mock-request "/api/koulutustoimija/haku/ala" :get {:sopimuksia "kylla"
                                                                               :tunnus "924601"}))
             tuloksia (-> (peridot/session crout)
                        (mock-request "/api/koulutustoimija/haku/ala" :get {:sopimuksia "kaikki"
                                                                            :tunnus "T1"}))
             tutkinto-ei-sopimuksia (-> (peridot/session crout)
                                      (mock-request "/api/koulutustoimija/haku/ala" :get {:sopimuksia "ei"
                                                                                          :tunnus "T1"}))

             kaikki (-> (peridot/session crout)
                      (mock-request "/api/koulutustoimija/haku/ala" :get {:sopimuksia "kaikki"
                                                                          :tunnus ""}))
             kaikki-voimassaolevat (-> (peridot/session crout)
                                     (mock-request "/api/koulutustoimija/haku/ala" :get {:sopimuksia "kylla"
                                                                                         :tunnus ""}))
             ]
        (is (= '()
              (body-json (:response ei-tuloksia))))

        (is (= '({:nimi_fi "bar bar"          :nimi_sv "Testikoulutustoimijan nimi (sv)", :ytunnus "KT1", :sopimusten_maara 1}
                 {:nimi_fi "Testiopisto KT4", :nimi_sv "Testikoulutustoimijan nimi (sv)", :ytunnus "KT4", :sopimusten_maara 0}
                 {:nimi_fi "Testiopisto KT5", :nimi_sv "Testikoulutustoimijan nimi (sv)", :ytunnus "KT5", :sopimusten_maara 2})  ;; TODO: Ennen löysi :sopimusten_maara:ksi "1", nyt "2" -> Onko oikein?
              (body-json (:response tuloksia))))

        (is (= '({:nimi_fi "Testiopisto KT4" :nimi_sv "Testikoulutustoimijan nimi (sv)", :ytunnus "KT4", :sopimusten_maara 0}
                 {:nimi_fi "Testiopisto KT5" :nimi_sv "Testikoulutustoimijan nimi (sv)", :ytunnus "KT5", :sopimusten_maara 2})    ;; TODO: Ennen löysi :sopimusten_maara:ksi "1", nyt "2" -> Onko oikein?
               (body-json (:response tutkinto-ei-sopimuksia))))

        (is (= 5 (count (body-json (:response kaikki)))))

        (is (= '({:nimi_fi "bar bar",         :nimi_sv "Testikoulutustoimijan nimi (sv)", :ytunnus "KT1", :sopimusten_maara 1}
                 {:nimi_fi "Testiopisto BAR", :nimi_sv "Testikoulutustoimijan nimi (sv)", :ytunnus "KT2", :sopimusten_maara 1}
                 {:nimi_fi "Testiopisto KT5", :nimi_sv "Testikoulutustoimijan nimi (sv)", :ytunnus "KT5", :sopimusten_maara 2}   ;; TODO: Ennen löysi :sopimusten_maara:ksi "1", nyt "2" -> Onko oikein?
                 {:nimi_fi "Urheilupainotteinen koulutuskuntayhtymä", :nimi_sv nil, :ytunnus "1060155-5", :sopimusten_maara 1}
                 )
              (body-json (:response kaikki-voimassaolevat))))
         ))))
