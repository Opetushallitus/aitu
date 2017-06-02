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

(s/defschema AuditTiedot {:muutettu_kayttaja s/Str
                          :luotu_kayttaja s/Str
                          :muutettuaika org.joda.time.DateTime
                          :luotuaika org.joda.time.DateTime})

(def Kieli (s/enum "fi" "sv" "se" "en" "2k"))

(s/defschema Kayttaja (merge {:etunimi s/Str
                              :sukunimi s/Str
                              :oid s/Str
                              :rooli (s/enum "YLLAPITAJA" "KAYTTAJA" "PAIVITTAJA")
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

(s/defschema SisaltaaHenkilonTiedot (assoc HenkilonTiedot s/Keyword s/Any))

(s/defschema KoulutustoimijaLinkki {:nimi_fi s/Str
                                    :nimi_sv (s/maybe s/Str)
                                    :ytunnus s/Str})

(s/defschema KoulutustoimijanTiedot (merge KoulutustoimijaLinkki
                                          {:postinumero (s/maybe s/Str)
                                           :osoite (s/maybe s/Str)
                                           :postitoimipaikka (s/maybe s/Str)
                                           :puhelin (s/maybe s/Str)
                                           :www_osoite (s/maybe s/Str)
                                           :sahkoposti (s/maybe s/Str)}))

(s/defschema KoulutustoimijaLista (merge KoulutustoimijaLinkki
                                         {:sopimusten_maara s/Int}))

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

(s/defschema OppilaitosLista (merge OppilaitoksenTiedot
                                    AuditTiedot
                                    {:sopimusten_maara s/Int}))

(s/defschema TutkintoversioLinkki {:tutkintotunnus s/Str
                                   :nimi_fi s/Str
                                   :nimi_sv (s/maybe s/Str)
                                   :peruste (s/maybe s/Str)})

(s/defschema Tutkintolinkki {:tutkintotunnus s/Str
                             (s/optional-key :opintoala_nimi_fi) s/Str
                             (s/optional-key :opintoala_nimi_sv) s/Str
                             :nimi_fi s/Str
                             :nimi_sv (s/maybe s/Str)})

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
                             (dissoc :tkunta)
                             (assoc (s/optional-key :tkunta) s/Str)))

(s/defschema SisaltaaToimikunnanTiedot (assoc ToimikunnanTiedot s/Keyword s/Any))

(s/defschema JarjestamissopimusLista {:jarjestamissopimusid s/Int
                                      :alkupvm (s/maybe org.joda.time.LocalDate)
                                      :loppupvm (s/maybe org.joda.time.LocalDate)
                                      :sopimusnumero s/Str
                                      (s/optional-key :voimassa) Boolean
                                      :tutkinnot [TutkintoversioLinkki]})

(s/defschema JarjestamissopimusJaKoulutustoimija (assoc JarjestamissopimusLista
                                                        :koulutustoimija KoulutustoimijaLinkki))

(s/defschema ToimikuntaLaajatTiedot (merge ToimikunnanTiedot
                                           AuditTiedotJaKayttajat
                                           {:jasenyys [ToimikunnanJasen]
                                            :nayttotutkinto [Tutkintolinkki]
                                            :jarjestamissopimus [JarjestamissopimusJaKoulutustoimija]}))

(s/defschema OppilaitosLaajatTiedot (merge OppilaitoksenTiedot
                                          {:jarjestamissopimus [JarjestamissopimusLista]
                                           :koulutustoimija KoulutustoimijaLinkki}))

(s/defschema KoulutustoimijaLaajatTiedot (merge KoulutustoimijanTiedot
                                               {:jarjestamissopimus [JarjestamissopimusLista]
                                                :oppilaitokset [OppilaitosLinkki]}))

(s/defschema Suorittaja {(s/optional-key :hetu) s/Str
                         (s/optional-key :oid) s/Str
                         :etunimi s/Str
                         :sukunimi s/Str
                         (s/optional-key :rahoitusmuoto_id) s/Int})

(s/defschema Lokalisoitu
  {:fi s/Str
   :sv s/Str})

(s/defschema Osoitepalvelu-Jasenyys
  {:etunimi s/Str
   :sukunimi s/Str
   (s/optional-key :sahkoposti) (s/maybe s/Str)
   (s/optional-key :osoite) (s/maybe s/Str)
   (s/optional-key :postinumero) (s/maybe s/Str)
   (s/optional-key :postitoimipaikka) (s/maybe s/Str)
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
