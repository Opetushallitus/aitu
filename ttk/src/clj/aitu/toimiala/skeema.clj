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

(ns aitu.toimiala.skeema
  (:require [schema.core :as s]
            [ring.swagger.schema :refer [defmodel]]
            [aitu.toimiala.util :refer [optional-keys]]))

(defmodel TermiParams {:termi s/Str
                       (s/optional-key :_) s/Str})

(defmodel TermiJaToimikausiParams (assoc TermiParams :toimikausi (s/enum "nykyinen" "kaikki")))

(defmodel AuditTiedot {:muutettu_kayttaja s/Str
                       :luotu_kayttaja s/Str
                       :muutettuaika org.joda.time.DateTime
                       :luotuaika org.joda.time.DateTime})

(def Kieli (s/enum "fi" "sv" "se" "en" "2k"))

(defmodel Kayttaja (merge {:etunimi s/Str
                           :sukunimi s/Str
                           :oid s/Str
                           :rooli (s/enum "YLLAPITAJA" "KAYTTAJA")
                           :voimassa Boolean
                           :uid (s/maybe s/Str)}
                          AuditTiedot))

(defmodel AuditTiedotJaKayttajat (assoc AuditTiedot
                                        :muutettu_kayttaja Kayttaja
                                        :luotu_kayttaja Kayttaja))


(def Sukupuoli (s/enum "mies" "nainen"))

(def Rooli (s/enum "asiantuntija" "jasen" "puheenjohtaja" "sihteeri" "ulkopuolinensihteeri" "varapuheenjohtaja"))

(def Edustus (s/enum "muu" "opettaja" "tyonantaja" "tyontekija" "itsenainen" "asiantuntija"))

(defmodel JasenyysTiedot {:jasenyys_id s/Int
                          :alkupvm org.joda.time.LocalDate
                          :loppupvm org.joda.time.LocalDate
                          :rooli Rooli
                          :edustus Edustus})

(defmodel HenkilonTiedot {:etunimi s/Str
                          :sukunimi s/Str
                          :organisaatio (s/maybe s/Str)
                          :aidinkieli (s/maybe Kieli)
                          :sukupuoli (s/maybe Sukupuoli)
                          (s/optional-key :sahkoposti) (s/maybe s/Str)
                          :sahkoposti_julkinen (s/maybe Boolean)
                          (s/optional-key :puhelin) (s/maybe s/Str)
                          :puhelin_julkinen (s/maybe Boolean)
                          (s/optional-key :osoite) (s/maybe s/Str)
                          :osoite_julkinen (s/maybe Boolean)
                          (s/optional-key :postinumero) (s/maybe s/Str)
                          (s/optional-key :postitoimipaikka) (s/maybe s/Str)
                          :jarjesto (s/maybe s/Int)
                          :lisatiedot (s/maybe s/Str)
                          :nayttomestari (s/maybe Boolean)
                          (s/optional-key :kayttaja_oid) (s/maybe s/Str)})

(defmodel ToimikunnanJasen (merge JasenyysTiedot
                             {:etunimi s/Str
                              :sukunimi s/Str
                              :henkiloid s/Int
                              (s/optional-key :sahkoposti) (s/maybe s/Str)
                              (s/optional-key :sahkoposti_julkinen) (s/maybe Boolean)
                              :aidinkieli (s/maybe s/Str)
                              :jarjesto_nimi_fi (s/maybe s/Str)
                              :jarjesto_nimi_sv (s/maybe s/Str)
                              :voimassa Boolean}))

(defmodel HenkiloTaiTiedot (assoc HenkilonTiedot (s/optional-key :henkiloid) s/Int))

(defmodel Henkilo (assoc HenkilonTiedot :henkiloid s/Int))

(defmodel HenkilonJulkisetTiedot (dissoc HenkilonTiedot :sahkoposti :puhelin :osoite :postinumero :postitoimipaikka))

(defmodel SisaltaaHenkilonTiedot (assoc HenkilonTiedot s/Keyword s/Any))

(defmodel SisaltaaHenkilonJulkisetTiedot (assoc HenkilonJulkisetTiedot s/Keyword s/Any))


(declare SopimusJaTutkinto)

(defmodel OppilaitosLinkki {:nimi s/Str
                            :oppilaitoskoodi s/Str})

(defmodel OppilaitoksenTiedot (merge OppilaitosLinkki
                                     {:postinumero (s/maybe s/Str)
                                      :osoite (s/maybe s/Str)
                                      :alue (s/maybe s/Str)
                                      :puhelin (s/maybe s/Str)
                                      :www_osoite (s/maybe s/Str)
                                      :postitoimipaikka (s/maybe s/Str)
                                      :kieli (s/maybe Kieli)
                                      :sahkoposti (s/maybe s/Str)}))

(defmodel OppilaitosTiedot (merge OppilaitoksenTiedot AuditTiedot))

(defmodel JarjestamissopimusTiedot (merge {:jarjestamissopimusid s/Int
                                           :alkupvm (s/maybe org.joda.time.LocalDate)
                                           :loppupvm (s/maybe org.joda.time.LocalDate)
                                           :sopimusnumero s/Str
                                           (s/optional-key :vanhentunut) Boolean
                                           (s/optional-key :voimassa) Boolean
                                           (s/optional-key :poistettu) Boolean
                                           :toimikunta s/Str
                                           (s/optional-key :sopijatoimikunta) s/Str
                                           :oppilaitos s/Str
                                           :tutkintotilaisuuksista_vastaava_oppilaitos (s/maybe s/Str)
                                           (s/optional-key :vastuuhenkilo) (s/maybe s/Str)
                                           (s/optional-key :puhelin) (s/maybe s/Str)
                                           (s/optional-key :sahkoposti) (s/maybe s/Str)}
                                          (optional-keys AuditTiedot)))

(defmodel JarjestamissuunnitelmaLinkki {:jarjestamissuunnitelma_id s/Int
                                   :jarjestamissuunnitelma_filename s/Str})

(defmodel SopimuksenLiiteLinkki {:sopimuksen_liite_id s/Int
                            :sopimuksen_liite_filename s/Str})

(defmodel TutkintoversioTiedot (merge {:peruste (s/maybe s/Str)
                                       :voimassa_alkupvm org.joda.time.LocalDate
                                       :uusin_versio_id s/Int
                                       :hyvaksytty Boolean
                                       :nimi_sv (s/maybe s/Str)
                                       :tutkintoversio_id s/Int
                                       :voimassa_loppupvm org.joda.time.LocalDate
                                       :koodistoversio s/Int
                                       :opintoala s/Str
                                       :nimi_fi s/Str
                                       :versio s/Int
                                       :tyyppi (s/maybe s/Str)
                                       :siirtymaajan_loppupvm org.joda.time.LocalDate
                                       (s/optional-key :siirtymaaika_paattyy) org.joda.time.LocalDate
                                       (s/optional-key :voimassa) Boolean
                                       :tutkintotaso (s/maybe s/Str)
                                       :tutkintotunnus s/Str}
                                      AuditTiedot))

;; TODO: osaamisala ja osaamisala_id sisältää saman tiedon: käytetäänkö molempia?
(defmodel SopimusJaTutkintoJaOsaamisala (merge {:nimi_fi s/Str
                                                :nimi_sv (s/maybe s/Str)
                                                :voimassa_alkupvm org.joda.time.LocalDate
                                                :sopimus_ja_tutkinto s/Int
                                                :voimassa_loppupvm org.joda.time.LocalDate
                                                :osaamisala s/Int
                                                :versio s/Int
                                                :tutkintoversio (s/maybe s/Int)
                                                :osaamisalatunnus s/Str
                                                :toimipaikka (s/maybe s/Str)
                                                :osaamisala_id s/Int
                                                :kuvaus (s/maybe s/Str)}
                                               AuditTiedot))

  ;; TODO: tutkinnonosa ja tutkinnonosa_id sisältää saman tiedon: käytetäänkö molempia?
(defmodel SopimusJaTutkintoJaTutkinnonosa (merge {:tutkinnonosa s/Int
                                                  :nimi_fi s/Str
                                                  :nimi_sv (s/maybe s/Str)
                                                  :sopimus_ja_tutkinto s/Int
                                                  :versio s/Int
                                                  :tutkinnonosa_id s/Int
                                                  :osatunnus s/Str
                                                  :toimipaikka (s/maybe s/Str)
                                                  :kuvaus (s/maybe s/Str)}
                                                 AuditTiedot))

(defmodel SopimusJaTutkinto (merge {:sopimus_ja_tutkinto_id s/Int
                                    :jarjestamissopimus (s/maybe JarjestamissopimusTiedot)
                                    :jarjestamissopimusid s/Int
                                    (s/optional-key :kieli) (s/maybe Kieli)
                                    (s/optional-key :vastuuhenkilo) (s/maybe s/Str)
                                    (s/optional-key :puhelin) (s/maybe s/Str)
                                    (s/optional-key :sahkoposti) (s/maybe s/Str)
                                    (s/optional-key :nayttomestari) (s/maybe Boolean)
                                    (s/optional-key :lisatiedot) (s/maybe s/Str)
                                    (s/optional-key :vastuuhenkilo_vara) (s/maybe s/Str)
                                    (s/optional-key :puhelin_vara) (s/maybe s/Str)
                                    (s/optional-key :sahkoposti_vara) (s/maybe s/Str)
                                    (s/optional-key :nayttomestari_vara) (s/maybe Boolean)
                                    (s/optional-key :lisatiedot_vara) (s/maybe s/Str)
                                    :poistettu Boolean
                                    :tutkintoversio TutkintoversioTiedot
                                    :jarjestamissuunnitelmat [JarjestamissuunnitelmaLinkki]
                                    :liitteet [SopimuksenLiiteLinkki]
                                    :sopimus_ja_tutkinto_ja_osaamisala [SopimusJaTutkintoJaOsaamisala]
                                    :sopimus_ja_tutkinto_ja_tutkinnonosa [SopimusJaTutkintoJaTutkinnonosa]}
                                   (optional-keys AuditTiedot)))

(defmodel Jarjestamissopimus (merge JarjestamissopimusTiedot
                                    {:oppilaitos OppilaitosTiedot
                                     :tutkintotilaisuuksista_vastaava_oppilaitos (s/maybe OppilaitosTiedot)
                                     (s/optional-key :sopimus_ja_tutkinto) [SopimusJaTutkinto]}))


(defmodel TutkinnonTiedot {:tutkintotunnus s/Str
                           :opintoala s/Str
                           (s/optional-key :opintoala_nimi_fi) s/Str
                           (s/optional-key :opintoala_nimi_sv) s/Str
                           :nimi_fi s/Str
                           :nimi_sv (s/maybe s/Str)
                           :tyyppi (s/maybe (s/enum "01" "02" "03" "04" "05" "06" "07" "08" "09" "10" "11" "12" "13"))
                           :tutkintotaso (s/maybe (s/enum "erikoisammattitutkinto" "ammattitutkinto" "perustutkinto"))
                           :uusin_versio_id (s/maybe s/Int)})

(defmodel Tutkinto (merge TutkinnonTiedot AuditTiedot))

(defmodel ToimikunnanTiedot {:tkunta s/Str
                             :nimi_fi s/Str
                             (s/optional-key :nimi_sv) (s/maybe s/Str)
                             :sahkoposti (s/maybe s/Str)
                             :diaarinumero s/Str
                             :tilikoodi s/Str
                             (s/optional-key :toimikausi_id) s/Int
                             (s/optional-key :alkupvm) org.joda.time.LocalDate
                             (s/optional-key :loppupvm) org.joda.time.LocalDate
                             :toimikausi_alku org.joda.time.LocalDate
                             :toimikausi_loppu org.joda.time.LocalDate
                             (s/optional-key :toimiala) s/Str
                             (s/optional-key :voimassa) Boolean
                             (s/optional-key :vanhentunut) Boolean
                             :kielisyys Kieli})

(defmodel SisaltaaToimikunnanTiedot (assoc ToimikunnanTiedot s/Keyword s/Any))

(defmodel Toimikunta (merge ToimikunnanTiedot AuditTiedot))

(defmodel JarjestamissopimusJaToimikunnat (merge (assoc Jarjestamissopimus
                                                        :toimikunta Toimikunta
                                                        (s/optional-key :sopijatoimikunta) Toimikunta)
                                                 (optional-keys AuditTiedotJaKayttajat)))

(defmodel ToimikuntaLaajatTiedot (merge Toimikunta
                                   {:jasenyys [ToimikunnanJasen]
                                    :nayttotutkinto [Tutkinto]
                                    :jarjestamissopimus [(s/recursive #'Jarjestamissopimus)]}))

(defmodel OppilaitosLaajatTiedot (merge OppilaitoksenTiedot
                                        AuditTiedot
                                        {:jarjestamissopimus [JarjestamissopimusJaToimikunnat]
                                         :voimassa Boolean}))

(defmodel Lokalisoitu
  {:fi s/Str
   :sv s/Str})

(defmodel Osoitepalvelu-Jasenyys
  {:etunimi s/Str
   :sukunimi s/Str
   :sahkoposti (s/maybe s/Str)
   :osoite (s/maybe s/Str)
   :postinumero (s/maybe s/Str)
   :postitoimipaikka (s/maybe s/Str)
   :aidinkieli Kieli
   :rooli Rooli
   :edustus (s/maybe Edustus)
   :voimassa Boolean})

(defmodel Osoitepalvelu-Toimikunta
  {:id s/Str
   :nimi Lokalisoitu
   :kielisyys Kieli
   :sahkoposti (s/maybe s/Str)
   :toimikausi (s/enum :mennyt :voimassa :tuleva)
   :jasenyydet [Osoitepalvelu-Jasenyys]})

(defmodel Osoitepalvelu-Tutkinto
  {:tutkintotunnus s/Str
   :opintoalatunnus s/Str
   :vastuuhenkilo (s/maybe s/Str)
   :sahkoposti_vastuuhenkilo (s/maybe s/Str)
   :varavastuuhenkilo (s/maybe s/Str)
   :sahkoposti_varavastuuhenkilo (s/maybe s/Str)})

(defmodel Osoitepalvelu-Sopimus
  {:toimikunta s/Str
   :sahkoposti (s/maybe s/Str)
   :vastuuhenkilo (s/maybe s/Str)
   :tutkinnot [Osoitepalvelu-Tutkinto]})

(defmodel Osoitepalvelu-Oppilaitos
  {:oppilaitoskoodi s/Str
   :nimi Lokalisoitu
   :oid (s/maybe s/Str)
   :sahkoposti (s/maybe s/Str)
   :osoite (s/maybe s/Str)
   :postinumero (s/maybe s/Str)
   :postitoimipaikka (s/maybe s/Str)
   :sopimukset [Osoitepalvelu-Sopimus]})

(defmodel Osoitepalvelu
  {:toimikunnat [Osoitepalvelu-Toimikunta]
   :oppilaitokset [Osoitepalvelu-Oppilaitos]})
