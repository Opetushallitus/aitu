(ns aitu.infra.eraajo.sopimukset-test
  (:require [clojure.test :refer :all]
            [clj-time.core :as time]
            [korma.core :as sql]
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
                               :alkupvm (time/now)})
              (sql/where {:jarjestamissopimusid jarjestamissopimusid})))

(deftest ^:integraatio sopimus-paivitys-test
  (lisaa-testidata!)

  ;voimassa, tutkinto voimassa -> tälle ei tapahdu mitään
  (lisaa-jarjestamissopimus! {:jarjestamissopimusid 99
                              :voimassa true
                              :alkupvm (time/now)
                              })
  (arkisto/lisaa-tutkinnot-sopimukselle! 99 [-1])

  ; ei-voimassa, tutkinto voimassa -> tämän pitäisi tulla voimassaolevaksi
  (lisaa-jarjestamissopimus! {:jarjestamissopimusid 98
                              :voimassa false
                              })  
  (arkisto/lisaa-tutkinnot-sopimukselle! 98 [-1])

  ; voimassa, tutkinto lakannut, siirtymäaika menossa -> tämän pitäisi pysyä voimassa
  (lisaa-jarjestamissopimus! {:jarjestamissopimusid 97
                              :voimassa true
                              })
  (arkisto/lisaa-tutkinnot-sopimukselle! 97 [-3])

  ; voimassa, tutkinto lakannut, siirtymäaika loppu -> tämän pitäisi lakata olemasta voimassa
  (lisaa-jarjestamissopimus! {:jarjestamissopimusid 96
                              :voimassa true
                              })
  (arkisto/lisaa-tutkinnot-sopimukselle! 96 [-4])

  (paivita-voimaan! 99)
  (paivita-voimaan! 97)
  (paivita-voimaan! 96)
  
  (let [sopimus (arkisto/hae 99)
        sopimus2 (arkisto/hae 98)
        sopi3 (arkisto/hae 97)
        sopi4 (arkisto/hae 96)]
    (is (true? (:voimassa sopimus)))
    (is (false? (:voimassa sopimus2)))
    (is (true? (:voimassa sopi3)))
    (is (true? (:voimassa sopi4)))
;    (clojure.pprint/pprint sopimus2)
    )
  ; ohitetaan testidatassa kiinteästi oleva sopimus, jotta testidatan siivous ei yritä siivota sitä pois (mikä epäonnistuisi)
  (doseq [{:keys [jarjestamissopimusid voimassa]} (arkisto/hae-kaikki)]
    (when (> jarjestamissopimusid 0)
      (arkisto/aseta-sopimuksen-voimassaolo! jarjestamissopimusid voimassa)))

  ;  (arkisto/paivita-sopimusten-voimassaolo!)
  (let [sopimus (arkisto/hae 99)
        sopimus2 (arkisto/hae 98)
        sopi3 (arkisto/hae 97)
        sopi4 (arkisto/hae 96)]
    (is (true? (:voimassa sopimus)))
;    (is (true? (:voimassa sopimus2)))
    (is (true? (:voimassa sopi3)))
    (is (false? (:voimassa sopi4))))
)
