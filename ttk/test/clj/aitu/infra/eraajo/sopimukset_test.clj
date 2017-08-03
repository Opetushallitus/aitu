(ns aitu.infra.eraajo.sopimukset-test
  (:require [clojure.test :refer :all]
            [clj-time.core :as time]
            [korma.core :as sql]
            [clojure.data]
            [aitu.integraatio.sql.korma :refer :all]
            [aitu.integraatio.sql.test-util :refer [tietokanta-fixture]]
            [aitu.infra.eraajo.eperusteet :refer :all]
            [aitu.integraatio.sql.test-data-util :refer :all]
            [aitu.infra.jarjestamissopimus-arkisto :as arkisto]))

(use-fixtures :each tietokanta-fixture)

(defn lisaa-testidata! []
  (lisaa-koulutus-ja-opintoala! {:koulutusalakoodi "KA"}
                                {:opintoalakoodi "OA"})
  (lisaa-tutkinto! {:tutkintotunnus "123456" ; voimassaoleva tutkinto
                    :opintoala "OA"
                    :uusin_versio_id -1})
  (lisaa-tutkintoversio! {:tutkintoversio_id -2
                          :eperustetunnus 1
                          :tutkintotunnus "123456"
                          :peruste "1/042/2015"
                          :voimassa_alkupvm (time/local-date 2015 1 1)
                          :voimassa_loppupvm (time/local-date 2015 6 30)})
  (lisaa-tutkintoversio! {:tutkintoversio_id -1
                          :eperustetunnus 2
                          :tutkintotunnus "123456"
                          :peruste "2/042/2015"
                          :voimassa_alkupvm (time/local-date 2015 7 1)
                          :voimassa_loppupvm (time/local-date 2199 1 1)})

  (lisaa-tutkinto! {:tutkintotunnus "987654" ; lakannut tutkinto, siirtymäaika voimassa
                    :opintoala "OA"
                    :uusin_versio_id -3})
  (lisaa-tutkintoversio! {:tutkintoversio_id -3
                          :eperustetunnus 1
                          :tutkintotunnus "987654"
                          :peruste "1/AAA/2015"
                          :voimassa_alkupvm (time/local-date 2015 1 1)
                          :voimassa_loppupvm (time/local-date 2015 6 30)
                          :siirtymaajan_loppupvm (time/local-date 2199 1 1)})

    (lisaa-tutkinto! {:tutkintotunnus "234567" ; lakannut tutkinto, siirtymäaika loppunut
                      :opintoala "OA"
                      :uusin_versio_id -4})
  (lisaa-tutkintoversio! {:tutkintoversio_id -4
                          :eperustetunnus 1
                          :tutkintotunnus "987654"
                          :peruste "1/AAA/2015"
                          :voimassa_alkupvm (time/local-date 2015 1 1)
                          :voimassa_loppupvm (time/local-date 2015 6 30)
                          :siirtymaajan_loppupvm (time/local-date 2016 1 1)
                          })

  )

(defn paivita-voimaan! [jarjestamissopimusid]
  (sql/update jarjestamissopimus
              (sql/set-fields {:voimassa true
                               :alkupvm (time/local-date 2015 1 1)})
              (sql/where {:jarjestamissopimusid jarjestamissopimusid})))

(deftest ^:integraatio sopimus-paivitys-test
  (lisaa-testidata!)

  ;voimassa, tutkinto voimassa -> tälle ei tapahdu mitään
  (lisaa-jarjestamissopimus! {:jarjestamissopimusid 96
                              :voimassa true
                              :alkupvm (time/local-date 2015 1 1)
                              })
  (arkisto/lisaa-tutkinnot-sopimukselle! 96 [-1])

  ; ei-voimassa, tutkinto voimassa -> tämän pitäisi tulla voimassaolevaksi
  (lisaa-jarjestamissopimus! {:jarjestamissopimusid 97
                              :voimassa false
                              })  
  (arkisto/lisaa-tutkinnot-sopimukselle! 97 [-1])

  ; voimassa, tutkinto lakannut, siirtymäaika menossa -> tämän pitäisi pysyä voimassa
  (lisaa-jarjestamissopimus! {:jarjestamissopimusid 98
                              :voimassa true
                              })
  (arkisto/lisaa-tutkinnot-sopimukselle! 98 [-3])

  ; voimassa, tutkinto lakannut, siirtymäaika loppu -> tämän pitäisi lakata olemasta voimassa
  (lisaa-jarjestamissopimus! {:jarjestamissopimusid 99
                              :voimassa true
                              })
  (arkisto/lisaa-tutkinnot-sopimukselle! 99 [-4])
  
  (paivita-voimaan! 96)
  (paivita-voimaan! 98)
  (paivita-voimaan! 99)
  (sql/update jarjestamissopimus
              (sql/set-fields {:voimassa false
                               :alkupvm (time/local-date 2015 1 1)})
              (sql/where {:jarjestamissopimusid 97}))
  (sql/update sopimus-ja-tutkinto
              (sql/set-fields {:alkupvm (time/local-date 2015 1 1)})
              (sql/where (> :jarjestamissopimusid 0))
              )
 
  (let [sop1-ennen (arkisto/hae 96)
        sop2-ennen (arkisto/hae 97)
        sop3-ennen (arkisto/hae 98)
        sop4-ennen (arkisto/hae 99)]
    (is (true? (:voimassa sop1-ennen)))
    (is (false? (:voimassa sop2-ennen)))
    (is (true? (:voimassa sop3-ennen)))
    (is (true? (:voimassa sop4-ennen))))    

  ; ohitetaan testidatassa kiinteästi oleva sopimus, jotta testidatan siivous ei yritä siivota sitä pois (mikä epäonnistuisi)
  (arkisto/paivita-sopimuksen-voimassaolo! 96)
  (arkisto/paivita-sopimuksen-voimassaolo! 97)
  (arkisto/paivita-sopimuksen-voimassaolo! 98)
  (arkisto/paivita-sopimuksen-voimassaolo! 99)
  
  (let [sop1 (arkisto/hae 96)
        sop2 (arkisto/hae 97)
        sop3 (arkisto/hae 98)
        sop4 (arkisto/hae 99)]
 
    (is (true? (:voimassa sop1)))
    (is (true? (:voimassa sop2)))
    (is (true? (:voimassa sop3)))
    (is (false? (:voimassa sop4)))
))
