(ns aitu.integraatio.sql.asettamispaatos-test
  (:require [clojure.test :refer [deftest testing is are use-fixtures]]
            [clj-time.core :as time]
            [aitu.infra.paatos-arkisto :as arkisto :refer :all]
            [korma.core :as sql]
            [aitu.integraatio.sql.test-util :refer [tietokanta-fixture]]
            [aitu.integraatio.sql.test-data-util :as data, :refer :all]))

(use-fixtures :each tietokanta-fixture)

; {:kieli :fi, :header {:teksti PÄÄTÖS, :paivays 12/12/2012, :diaarinumero 6510/6502}, :data {:paivays 12/12/2012, :esittelija {:asema esittelijä, asema, :nimi Esittelijä E}, :hyvaksyja {:asema Suuri Johtaja, :nimi Jaana Johtaja}, :jakelu (), :tiedoksi (), :paatosteksti Diipa daapa daa. Laalaa. Kthxbye., :toimikunta {:jasenyys (), :nayttotutkinto ({:nimi_fi Audiovisuaalisen viestinnän perustutkinto, :nimi_sv Audiovisuaalisen viestinnän perustutkinto (sv), :tutkintotunnus 324601, :opintoala_nimi_fi Viestintä ja informaatiotieteet, :opintoala_nimi_sv Mediekultur och informationsvetenskaper}), :nimi_fi Aavasaksalainen testitoimikunta, :loppupvm 31.07.2018, :nimi_sv Aakkosissa Aavasaksa asettuu alkuun, :kielisyys fi, :kieli suomi, :jasen (), :toimiala (Audiovisuaalisen viestinnän perustutkinto), :alkupvm 01.08.2016, :tilikoodi 8086, :toimialue Toimitustoimitus}}}

;{:kieli :fi, :header {:teksti PÄÄTÖS, :paivays 12/12/2012, :diaarinumero 6510/6502}, :data {:paivays 12/12/2012, 
; :esittelija {:asema esittelijä, asema, :nimi Esittelijä E}, :hyvaksyja {:asema Suuri Johtaja, :nimi Jaana Johtaja}, 
; :jakelu (Pirkko Päätetty), :tiedoksi (), :paatosteksti Diipa daapa daa. Laalaa. Kthxbye., 
; :toimikunta {:jasenyys ({:jasenyys_id 11, :sahkoposti sahkoposti, :loppupvm #object[org.joda.time.LocalDate 0x3c82849 2017-05-30], 
; :sahkoposti_julkinen false, :aidinkieli fi, :henkiloid 11, :sukunimi Esitetty, :rooli jasen, :status esitetty, :alkupvm #object[org.joda.time.LocalDate 0x7eeaa2c3 2016-04-30], :jarjesto_nimi_fi nil, :edustus opettaja, :nimityspaiva nil, :etunimi Erkka, :jarjesto_nimi_sv nil} {:jasenyys_id 12, :sahkoposti sahkoposti, :loppupvm #object[org.joda.time.LocalDate 0x5f71e489 2017-05-30], :sahkoposti_julkinen false, :aidinkieli fi, :henkiloid 12, :sukunimi Päätetty, :rooli jasen, :status nimitetty, :alkupvm #object[org.joda.time.LocalDate 0x70878f43 2016-04-30], :jarjesto_nimi_fi
; nil, :edustus opettaja, :nimityspaiva nil, :etunimi Pirkko, :jarjesto_nimi_sv nil}), :nayttotutkinto ({:nimi_fi Audiovisuaalisen viestinnän perustutkinto, :nimi_sv Audiovisuaalisen viestinnän perustutkinto (sv), :tutkintotunnus 324601, :opintoala_nimi_fi Viestintä ja informaatiotieteet, :opintoala_nimi_sv Mediekultur och informationsvetenskaper}), :nimi_fi Aavasaksalainen testitoimikunta, :loppupvm 31.07.2018, :nimi_sv Aakkosissa Aavasaksa asettuu alkuun, :kielisyys fi, :kieli suomi, :jasen ({:edustus Opettajien edustaja, :jasen [{:nimi Pirkko Päätetty, :jarjesto nil, :edustus opettaja}]}), :toimiala (Audiovisuaalisen viestinnän perustutkinto), :alkupvm 01.08.2016, :tilikoodi 8086, :toimialue Toimitustoimitus}}}

(deftest ^:integraatio asettamispaatokset
  (let [esitetty-hlo (lisaa-henkilo! {:lisatiedot "fubar" :etunimi "Erkka" :sukunimi "Esitetty"})
        esitetty-jasenyys (lisaa-jasen! {:toimikunta "Gulo gulo" :henkiloid (:henkiloid esitetty-hlo)
                                         :status "esitetty" :vapaateksti_kokemus "foo"
                                         :esittaja -1})
        paatetty-hlo (lisaa-henkilo! {:lisatiedot "fubarbar" :etunimi "Pirkko" :sukunimi "Päätetty"})
        paatetty-jasenyys (lisaa-jasen! {:toimikunta "Gulo gulo" :henkiloid (:henkiloid paatetty-hlo)
                                         :status "nimitetty" :vapaateksti_kokemus "foo"
                                         :esittaja -1})]
        (let [diaarinumero "6510/6502"
              data {:paivays "12/12/2012"
                    :esittelija {:asema "esittelijä, asema"
                                 :nimi "Esittelijä E"}
                    :hyvaksyja {:asema "Suuri Johtaja"
                                :nimi "Jaana Johtaja"}
                    :jakelu nil 
                    :tiedoksi nil
                    :paatosteksti "Diipa daapa daa. Laalaa. Kthxbye."}
            pdf (arkisto/luo-asettamispaatos-data :fi diaarinumero data)
            pdf-data (:data pdf)]
          (is (= (:jakelu pdf-data) '("Pirkko Päätetty")))
          (is (empty? (:tiedoksi pdf-data)))
 )))         
;          (println "pdf " pdf))))
