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
            [aitu.toimiala.util :refer [optional-keys]]))

(s/defschema TermiParams {:termi s/Str
                          (s/optional-key :_) s/Str})

(s/defschema TermiJaToimikausiParams (assoc TermiParams :toimikausi (s/enum "nykyinen" "kaikki")))

(s/defschema AuditTiedot {:muutettu_kayttaja s/Str
                          :luotu_kayttaja s/Str
                          :muutettuaika org.joda.time.DateTime
                          :luotuaika org.joda.time.DateTime})

(def Kieli (s/enum "fi" "sv" "se" "en" "2k"))

(s/defschema Kayttaja (merge {:etunimi s/Str
                              :sukunimi s/Str
                              :oid s/Str
                              :rooli (s/enum "YLLAPITAJA" "KAYTTAJA")
                              :voimassa Boolean
                              :uid (s/maybe s/Str)
                              :jarjesto (s/maybe s/Int)}
                            AuditTiedot))

(s/defschema AuditTiedotJaKayttajat (assoc AuditTiedot
                                          :muutettu_kayttaja Kayttaja
                                          :luotu_kayttaja Kayttaja))


(def Sukupuoli (s/enum "mies" "nainen"))

(def Rooli (s/enum "asiantuntija" "jasen" "puheenjohtaja" "sihteeri" "ulkopuolinensihteeri" "varapuheenjohtaja"))

(def Edustus (s/enum "muu" "opettaja" "tyonantaja" "tyontekija" "itsenainen" "asiantuntija"))

(def Jasenyydenstatus (s/enum "nimitetty" "esitetty" "peruutettu"))

(s/defschema JasenyysTiedot {:jasenyys_id s/Int
                             :alkupvm org.joda.time.LocalDate
                             :loppupvm org.joda.time.LocalDate
                             :rooli Rooli
                             :edustus Edustus
                             :status Jasenyydenstatus
                             :nimityspaiva (s/maybe org.joda.time.LocalDate)})

(s/defschema HenkilonTiedot {:etunimi s/Str
                            :sukunimi s/Str
                            (s/optional-key :organisaatio) (s/maybe s/Str)
                            :aidinkieli Kieli
                            :sukupuoli Sukupuoli
                            (s/optional-key :sahkoposti) (s/maybe s/Str)
                            (s/optional-key :sahkoposti_julkinen) (s/maybe Boolean)
                            (s/optional-key :puhelin) (s/maybe s/Str)
                            (s/optional-key :puhelin_julkinen) (s/maybe Boolean)
                            (s/optional-key :osoite) (s/maybe s/Str)
                            (s/optional-key :osoite_julkinen) (s/maybe Boolean)
                            (s/optional-key :postinumero) (s/maybe s/Str)
                            (s/optional-key :postitoimipaikka) (s/maybe s/Str)
                            (s/optional-key :jarjesto) (s/maybe s/Any)
                            (s/optional-key :lisatiedot) (s/maybe s/Str)
                            (s/optional-key :nayttomestari) (s/maybe Boolean)
                            (s/optional-key :syntymavuosi) (s/maybe s/Int)
                            (s/optional-key :kokemusvuodet) (s/maybe s/Int)
                            (s/optional-key :kayttaja_oid) (s/maybe s/Str)})

(s/defschema ToimikunnanJasen (merge JasenyysTiedot
                               {:etunimi s/Str
                                :sukunimi s/Str
                                :henkiloid s/Int
                                (s/optional-key :sahkoposti) (s/maybe s/Str)
                                (s/optional-key :sahkoposti_julkinen) (s/maybe Boolean)
                                :aidinkieli (s/maybe s/Str)
                                :jarjesto_nimi_fi (s/maybe s/Str)
                                :jarjesto_nimi_sv (s/maybe s/Str)
                                :voimassa Boolean}))

(s/defschema HenkiloTaiTiedot (assoc HenkilonTiedot (s/optional-key :henkiloid) s/Int))

(s/defschema Henkilo (assoc HenkilonTiedot :henkiloid s/Int))

(s/defschema HenkilonJulkisetTiedot (dissoc HenkilonTiedot :sahkoposti :puhelin :osoite :postinumero :postitoimipaikka))

(s/defschema SisaltaaHenkilonTiedot (assoc HenkilonTiedot s/Keyword s/Any))

(s/defschema SisaltaaHenkilonJulkisetTiedot (assoc HenkilonJulkisetTiedot s/Keyword s/Any))


(declare SopimusJaTutkinto)

(s/defschema KoulutustoimijaLinkki {:nimi_fi s/Str
                                   :nimi_sv s/Str
                                   :ytunnus s/Str})

(s/defschema KoulutustoimijanTiedot (merge KoulutustoimijaLinkki
                                          {:postinumero (s/maybe s/Str)
                                           :osoite (s/maybe s/Str)
                                           :postitoimipaikka (s/maybe s/Str)
                                           :puhelin (s/maybe s/Str)
                                           :www_osoite (s/maybe s/Str)
                                           :sahkoposti (s/maybe s/Str)}))

(s/defschema Koulutustoimija (merge KoulutustoimijanTiedot AuditTiedot))

(s/defschema KoulutustoimijaLista (assoc Koulutustoimija :sopimusten_maara s/Int))

(s/defschema OppilaitosLinkki {:nimi s/Str
                              :oppilaitoskoodi s/Str})

(s/defschema OppilaitoksenTiedot (merge OppilaitosLinkki
                                       {:postinumero (s/maybe s/Str)
                                        :osoite (s/maybe s/Str)
                                        :alue (s/maybe s/Str)
                                        :puhelin (s/maybe s/Str)
                                        :www_osoite (s/maybe s/Str)
                                        :postitoimipaikka (s/maybe s/Str)
                                        :kieli (s/maybe Kieli)
                                        :sahkoposti (s/maybe s/Str)
                                        :koulutustoimija s/Str}))

(s/defschema ToimipaikkaLinkki {:nimi_fi s/Str
                               :nimi_sv s/Str
                               :toimipaikkakoodi s/Str})

(s/defschema OppilaitosTiedot (merge OppilaitoksenTiedot AuditTiedot))

(s/defschema OppilaitosLista (assoc OppilaitosTiedot :sopimusten_maara s/Int))

(s/defschema JarjestamissopimusPerustiedot {:jarjestamissopimusid s/Int
                                            :alkupvm (s/maybe org.joda.time.LocalDate)
                                            :loppupvm (s/maybe org.joda.time.LocalDate)
                                            :sopimusnumero s/Str
                                            (s/optional-key :voimassa) Boolean})

(s/defschema JarjestamissopimusTiedot (merge JarjestamissopimusPerustiedot
                                             {:toimikunta s/Str
                                              (s/optional-key :sopijatoimikunta) s/Str
                                              :koulutustoimija s/Str
                                              :tutkintotilaisuuksista_vastaava_oppilaitos (s/maybe s/Str)
                                              (s/optional-key :vastuuhenkilo) (s/maybe s/Str)
                                              (s/optional-key :puhelin) (s/maybe s/Str)
                                              (s/optional-key :sahkoposti) (s/maybe s/Str)
                                              (s/optional-key :vanhentunut) Boolean
                                              (s/optional-key :poistettu) Boolean}
                                             (optional-keys AuditTiedot)))

(s/defschema JarjestamissuunnitelmaLinkki {:jarjestamissuunnitelma_id s/Int
                                     :jarjestamissuunnitelma_filename s/Str})

(s/defschema SopimuksenLiiteLinkki {:sopimuksen_liite_id s/Int
                                   :sopimuksen_liite_filename s/Str})

(s/defschema TutkintoversioLinkki {:tutkintotunnus s/Str
                                   :nimi_fi s/Str
                                   :nimi_sv (s/maybe s/Str)
                                   :peruste (s/maybe s/Str)})

(s/defschema TutkintoversioTiedot (merge TutkintoversioLinkki
                                         {:voimassa_alkupvm org.joda.time.LocalDate
                                          :uusin_versio_id s/Int
                                          :hyvaksytty Boolean
                                          :tutkintoversio_id s/Int
                                          :voimassa_loppupvm org.joda.time.LocalDate
                                          :koodistoversio s/Int
                                          :opintoala s/Str
                                          :versio s/Int
                                          :jarjestyskoodistoversio (s/maybe s/Int)
                                          :osajarjestyskoodisto (s/maybe s/Str)
                                          :tyyppi (s/maybe s/Str)
                                          :siirtymaajan_loppupvm org.joda.time.LocalDate
                                          (s/optional-key :siirtymaaika_paattyy) org.joda.time.LocalDate
                                          (s/optional-key :voimassa) Boolean
                                          :tutkintotaso (s/maybe s/Str)}
                                         AuditTiedot))

;; TODO: osaamisala ja osaamisala_id sisältää saman tiedon: käytetäänkö molempia?
(s/defschema SopimusJaTutkintoJaOsaamisala (merge {:nimi_fi s/Str
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
(s/defschema SopimusJaTutkintoJaTutkinnonosa (merge {:tutkinnonosa s/Int
                                                    :nimi_fi s/Str
                                                    :nimi_sv (s/maybe s/Str)
                                                    :sopimus_ja_tutkinto s/Int
                                                    :versio s/Int
                                                    :tutkinnonosa_id s/Int
                                                    :osatunnus s/Str
                                                    :toimipaikka (s/maybe s/Str)
                                                    :kuvaus (s/maybe s/Str)}
                                                   AuditTiedot))

(s/defschema SopimusJaTutkinto (merge {:sopimus_ja_tutkinto_id s/Int
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

(s/defschema Jarjestamissopimus (merge JarjestamissopimusTiedot
                                      {:koulutustoimija Koulutustoimija
                                       :tutkintotilaisuuksista_vastaava_oppilaitos (s/maybe OppilaitosTiedot)
                                       (s/optional-key :sopimus_ja_tutkinto) [SopimusJaTutkinto]}))

(s/defschema Tutkintolinkki {:tutkintotunnus s/Str
                             (s/optional-key :opintoala_nimi_fi) s/Str
                             (s/optional-key :opintoala_nimi_sv) s/Str
                             :nimi_fi s/Str
                             :nimi_sv (s/maybe s/Str)})

(s/defschema TutkinnonTiedot (merge Tutkintolinkki
                                    {:tyyppi (s/maybe (s/enum "01" "02" "03" "04" "05" "06" "07" "08" "09" "10" "11" "12" "13"))
                                     :tutkintotaso (s/maybe (s/enum "erikoisammattitutkinto" "ammattitutkinto" "perustutkinto"))
                                     :uusin_versio_id (s/maybe s/Int)
                                     :opintoala s/Str}))

(s/defschema Tutkinto (merge TutkinnonTiedot AuditTiedot))

(s/defschema ToimikunnanHakuTiedot {:tkunta s/Str
                                    :diaarinumero s/Str
                                    :toimikausi_alku org.joda.time.LocalDate
                                    :toimikausi_loppu org.joda.time.LocalDate
                                    :nimi_fi s/Str
                                    (s/optional-key :nimi_sv) (s/maybe s/Str)
                                    (s/optional-key :voimassa) Boolean
                                    (s/optional-key :vanhentunut) Boolean})

(s/defschema Toimikuntalista (assoc ToimikunnanHakuTiedot
                                    :tilikoodi s/Str
                                    :kielisyys Kieli))

(s/defschema ToimikunnanTiedot (merge Toimikuntalista
                                      {:sahkoposti (s/maybe s/Str)
                                       (s/optional-key :toimikausi_id) s/Int
                                       (s/optional-key :alkupvm) org.joda.time.LocalDate
                                       (s/optional-key :loppupvm) org.joda.time.LocalDate
                                       (s/optional-key :toimiala) s/Str}))

(s/defschema UusiToimikunta (-> ToimikunnanTiedot
                             (dissoc ToimikunnanTiedot :tkunta)
                             (assoc (s/optional-key :tkunta) s/Str)))

(s/defschema SisaltaaToimikunnanTiedot (assoc ToimikunnanTiedot s/Keyword s/Any))

(s/defschema Toimikunta (merge ToimikunnanTiedot AuditTiedot))

(s/defschema JarjestamissopimusLista (assoc JarjestamissopimusPerustiedot
                                            :tutkinnot [TutkintoversioLinkki]))

(s/defschema JarjestamissopimusJaKoulutustoimija (assoc JarjestamissopimusLista
                                                        :koulutustoimija KoulutustoimijaLinkki))

(s/defschema ToimikuntaLaajatTiedot (merge Toimikunta
                                     {:jasenyys [ToimikunnanJasen]
                                      :nayttotutkinto [Tutkintolinkki]
                                      :jarjestamissopimus [JarjestamissopimusJaKoulutustoimija]
                                      :luotu_kayttaja Kayttaja
                                      :muutettu_kayttaja Kayttaja}))

(s/defschema OppilaitosLaajatTiedot (merge OppilaitoksenTiedot
                                          {:jarjestamissopimus [JarjestamissopimusLista]
                                           :koulutustoimija KoulutustoimijaLinkki}))

(s/defschema KoulutustoimijaLaajatTiedot (merge KoulutustoimijanTiedot
                                               {:jarjestamissopimus [JarjestamissopimusLista]
                                                :oppilaitokset [OppilaitosLinkki]}))

(s/defschema Lokalisoitu
  {:fi s/Str
   :sv s/Str})

(s/defschema Osoitepalvelu-Jasenyys
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

(s/defschema Osoitepalvelu-Toimikunta
  {:id s/Str
   :nimi Lokalisoitu
   :kielisyys Kieli
   :sahkoposti (s/maybe s/Str)
   :toimikausi (s/enum :mennyt :voimassa :tuleva)
   :jasenyydet [Osoitepalvelu-Jasenyys]})

(s/defschema Osoitepalvelu-Tutkinto
  {:tutkintotunnus s/Str
   :opintoalatunnus s/Str
   :vastuuhenkilo (s/maybe s/Str)
   :sahkoposti_vastuuhenkilo (s/maybe s/Str)
   :varavastuuhenkilo (s/maybe s/Str)
   :sahkoposti_varavastuuhenkilo (s/maybe s/Str)})

(s/defschema Osoitepalvelu-Sopimus
  {:toimikunta s/Str
   :sahkoposti (s/maybe s/Str)
   :vastuuhenkilo (s/maybe s/Str)
   :tutkinnot [Osoitepalvelu-Tutkinto]})

(s/defschema Osoitepalvelu-Oppilaitos
  {:oppilaitoskoodi s/Str
   :nimi Lokalisoitu
   :oid (s/maybe s/Str)
   :sahkoposti (s/maybe s/Str)
   :osoite (s/maybe s/Str)
   :postinumero (s/maybe s/Str)
   :postitoimipaikka (s/maybe s/Str)
   :sopimukset [Osoitepalvelu-Sopimus]})

(s/defschema Osoitepalvelu
  {:toimikunnat [Osoitepalvelu-Toimikunta]
   :oppilaitokset [Osoitepalvelu-Oppilaitos]})

(s/defschema Organisaatiomuutos
  (merge {:organisaatiomuutosid s/Int
          :koulutustoimija (s/maybe KoulutustoimijaLinkki)
          :oppilaitos (s/maybe OppilaitosLinkki)
          :toimipaikka (s/maybe ToimipaikkaLinkki)
          :tehty (s/maybe org.joda.time.LocalDate)
          :paivamaara org.joda.time.LocalDate
          :tyyppi s/Keyword}
         AuditTiedot))
