(ns aitu.rest-api.koulutustoimija-test
  (:require
    [clojure.test :refer :all]
    [peridot.core :as peridot]
    [oph.korma.korma-auth :as auth]
    [korma.db :as db]
    [infra.test.data :as testdata]
    [aitu.integraatio.sql.test-util :refer [tietokanta-fixture]]
    [aitu.integraatio.sql.test-data-util :refer :all]
    [aitu.rest-api.session-util :refer :all]))

;(use-fixtures :each tietokanta-fixture)


; http://192.168.50.1:8080/api/koulutustoimija/haku/ala?tunnus=102754
; http://192.168.50.1:8080/api/koulutustoimija/haku/ala?sopimuksia=kylla&tunnus=100321



; Request URL:http://192.168.50.1:8080/api/koulutustoimija/haku/ala?sopimuksia=kylla&tunnus=324601

(defn run-with-db [dataf testf]
    (try
      (with-auth-user
        #(db/transaction
           (dataf)))
      (testf)
      (finally
        (with-auth-user
          #(db/transaction
             (testdata/tyhjenna-testidata! auth/default-test-user-oid))))))
  
  
; TODO: sql-koulutustoimija-test testissä sama datan alustus
(defn alusta-data []
  (lisaa-koulutus-ja-opintoala! {:koulutusalakoodi "KA1"}
                                {:opintoalakoodi "OA1"})
  (lisaa-tutkinto! {:opintoala "OA1"
                    :tutkintotunnus "T1"})
  (let [kt1 (lisaa-koulutustoimija! {:ytunnus "KT1"})
        o1 (lisaa-oppilaitos! {:koulutustoimija "KT1"})
        sop1 (lisaa-jarjestamissopimus! kt1 o1)
        tv1 (lisaa-tutkintoversio! {:tutkintotunnus "T1"})
        _ (lisaa-tutkinto-sopimukselle! sop1 (:tutkintoversio_id tv1))

        kt2 (lisaa-koulutustoimija! {:ytunnus "KT2" :nimi_fi "Testiopisto" })
        o2 (lisaa-oppilaitos! {:koulutustoimija "KT2"})
        sop2 (lisaa-jarjestamissopimus! kt2 o2)
        _ (lisaa-tutkinto-sopimukselle! sop2 -20000)]
    ))
  
;
;bodyzz  ()
; bodyz  ({:nimi_fi Testikoulutustoimijan nimi, :nimi_sv Testikoulutustoimijan nimi (sv), :ytunnus KT1, :sopimusten_maara 1})
(deftest ^:integraatio koulutustoimija-haku
  (let [crout (init-peridot!)]
    (run-with-db alusta-data
      #(let [ei-tuloksia (-> (peridot/session crout)
                           (mock-request "/api/koulutustoimija/haku/ala" :get {:sopimuksia "kylla"
                                                                               :tunnus "324601"}))
             tuloksia (-> (peridot/session crout)
                        (mock-request "/api/koulutustoimija/haku/ala" :get {:sopimuksia "kaikki"
                                                                            :tunnus "T1"}))
             kaikki (-> (peridot/session crout)
                      (mock-request "/api/koulutustoimija/haku/ala" :get {:sopimuksia "kaikki"
                                                                          :tunnus ""}))
             kaikki-voimassaolevat (-> (peridot/session crout)
                                     (mock-request "/api/koulutustoimija/haku/ala" :get {:sopimuksia "kylla"
                                                                                         :tunnus ""}))
             ]
        (is (= '() (body-json (:response ei-tuloksia))))
        (is (= '({:nimi_fi "Testikoulutustoimijan nimi", :nimi_sv "Testikoulutustoimijan nimi (sv)", :ytunnus "KT1", :sopimusten_maara 1}) (body-json (:response tuloksia))))
;was  209
;riedl  ({:nimi_fi Testikoulutustoimijan nimi, :nimi_sv Testikoulutustoimijan nimi (sv), :ytunnus KT1, :sopimusten_maara 1} {:nimi_fi Testiopisto, :nimi_sv Testikoulutustoimijan nimi (sv), :ytunnus KT2, :sopimusten_maara 1})
        (is (= 209 (count (body-json (:response kaikki)))))
        (is (= '({:nimi_fi "Testikoulutustoimijan nimi", :nimi_sv "Testikoulutustoimijan nimi (sv)", :ytunnus "KT1", :sopimusten_maara 1} 
                  {:nimi_fi "Testiopisto", :nimi_sv "Testikoulutustoimijan nimi (sv)", :ytunnus "KT2", :sopimusten_maara 1})
              (body-json (:response kaikki-voimassaolevat))))
;        (println "was " (count (body-json (:response kaikki))))
;        (println "riedl " (body-json (:response kaikki-voimassaolevat)))
          
        ;        (println "bodyzz " (body-json (:response ei-tuloksia)))
        ;        (println "bodyz " (body-json (:response tuloksia)))
         ))))
  ;      (is (= (:status (:response response)) 200)))))
  