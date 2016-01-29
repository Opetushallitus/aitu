;; Copyright (c) 2015 The Finnish National Board of Education - Opetushallitus
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

(ns aitu.infra.jasenesitykset-arkisto
  (:require [korma.core :as sql]
            [oph.korma.common :as sql-util]
            [aitu.auditlog :as auditlog]
            [aitu.integraatio.sql.korma :refer [ilike]]
            [oph.common.util.util :refer [sisaltaako-kentat?]]
            [oph.korma.common :refer [rajaa-kentilla]]))

(defn hae [kayttajan-jarjesto {:keys [asiantuntijaksi ehdokas henkilotiedot jarjesto tila toimikunta]} csv]
  (->
    (sql/select* :jasenyys)
    (sql/join :henkilo (= :henkilo.henkiloid :jasenyys.henkiloid))
    (sql/join :tutkintotoimikunta (= :tutkintotoimikunta.tkunta :jasenyys.toimikunta))
    (sql/join [:jarjesto :esittaja_jarjesto] (= :jasenyys.esittaja :esittaja_jarjesto.jarjestoid))
    (sql/join [:jarjesto :esittaja_keskusjarjesto] (and {:esittaja_keskusjarjesto.keskusjarjestoid nil}
                                                        (or {:esittaja_keskusjarjesto.jarjestoid :jasenyys.esittaja}
                                                            {:esittaja_keskusjarjesto.jarjestoid :esittaja_jarjesto.keskusjarjestoid})))
    (sql/join [:kayttaja :esittaja_kayttaja] (= :jasenyys.luotu_kayttaja :esittaja_kayttaja.oid))
    (sql/join :left [:henkilo :esittaja_henkilo] (= :esittaja_kayttaja.oid :esittaja_henkilo.kayttaja_oid))
    (sql/fields :jasenyys.toimikunta :jasenyys.status :jasenyys.esittaja :jasenyys.luotuaika :jasenyys.muutettuaika :jasenyys.rooli :jasenyys.edustus :jasenyys.nimityspaiva
                :jasenyys.asiantuntijaksi :jasenyys.jasenyys_id :jasenyys.luotu_kayttaja
                :henkilo.henkiloid :henkilo.etunimi :henkilo.sukunimi
                [:tutkintotoimikunta.nimi_fi :tutkintotoimikunta_nimi_fi] [:tutkintotoimikunta.nimi_sv :tutkintotoimikunta_nimi_sv] [:tutkintotoimikunta.diaarinumero :tutkintotoimikunta_diaarinumero]
                [:esittaja_henkilo.henkiloid :esittaja_henkilo_henkiloid] [:esittaja_kayttaja.etunimi :esittaja_henkilo_etunimi] [:esittaja_kayttaja.sukunimi :esittaja_henkilo_sukunimi]
                [:esittaja_jarjesto.nimi_fi :esittaja_jarjesto_nimi_fi] [:esittaja_jarjesto.nimi_sv :esittaja_jarjesto_nimi_sv]
                [:esittaja_keskusjarjesto.nimi_fi :esittaja_keskusjarjesto_nimi_fi] [:esittaja_keskusjarjesto.nimi_sv :esittaja_keskusjarjesto_nimi_sv])
    (cond->
      (not csv) (sql/fields :jasenyys.vapaateksti_kokemus)
      csv (sql/fields (sql/raw "jasenyys.vapaateksti_kokemus IS NOT NULL AS vapaateksti_kokemus"))
      (java.lang.Boolean/valueOf ^String henkilotiedot) (sql/fields :henkilo.sukupuoli :henkilo.aidinkieli :henkilo.syntymavuosi :henkilo.sahkoposti :henkilo.sahkoposti_julkinen
                                                                    :henkilo.puhelin :henkilo.puhelin_julkinen :henkilo.osoite :henkilo.postinumero :henkilo.postitoimipaikka :henkilo.osoite_julkinen
                                                                    :henkilo.nayttomestari :henkilo.kokemusvuodet
                                                                    (sql/raw "henkilo.lisatiedot IS NOT NULL AS lisatiedot")))
    (sql/where (not= :jasenyys.esittaja nil))
    (cond->
      kayttajan-jarjesto (sql/where (or {:jasenyys.esittaja kayttajan-jarjesto}
                                        {:jasenyys.esittaja [in (sql/subselect :jarjesto
                                                                  (sql/fields :jarjestoid)
                                                                  (sql/where {:keskusjarjestoid kayttajan-jarjesto}))]}))
      (seq asiantuntijaksi) (sql/where {:jasenyys.asiantuntijaksi (= asiantuntijaksi "true")})
      (seq ehdokas) (rajaa-kentilla [:henkilo.etunimi :henkilo.sukunimi (sql/raw "henkilo.etunimi || ' ' || henkilo.sukunimi") (sql/raw "henkilo.sukunimi || ' ' || henkilo.etunimi")] ehdokas)
      (seq jarjesto) (sql/where (or {:esittaja_jarjesto.jarjestoid (Integer/parseInt jarjesto)}
                                    {:esittaja_keskusjarjesto.jarjestoid (Integer/parseInt jarjesto)}))
      (empty? tila) (sql/where {:jasenyys.status [in ["esitetty", "nimitetty"]]})
      (seq tila) (sql/where {:jasenyys.status tila})
      (seq toimikunta) (sql/where {:tutkintotoimikunta.tkunta toimikunta}))
    (sql/order (sql/raw "jasenyys.luotuaika::date") :desc)
    (sql/order :henkilo.sukunimi :asc)
    sql/exec))

(defn ^:private subselect-laske-jasenesitykset [tila sukupuoli kielet]
  (sql/subselect :jasenyys
    (sql/aggregate (count :*) :cnt)
    (sql/join :henkilo (= :henkilo.henkiloid :jasenyys.henkiloid))
    (sql/where {:toimikunta :tutkintotoimikunta.tkunta})
    (cond->
      tila (sql/where {:status tila})
      sukupuoli (sql/where {:henkilo.sukupuoli sukupuoli})
      kielet (sql/where {:henkilo.aidinkieli [in kielet]}))))

(defn ^:private hae-jarjeston-alijarjestot [jarjesto]
  (let [jarjestot (sql/select :jarjesto
                    (sql/fields :jarjestoid)
                    (sql/where {:keskusjarjestoid jarjesto}))]
    (map :jarjestoid jarjestot)))

(defn hae-yhteenveto [jarjesto toimikausi vain_jasenesityksia_sisaltavat]
  (let [jarjesto-ja-alijarjestot (conj (hae-jarjeston-alijarjestot jarjesto) jarjesto)]
    (->
      (sql/select* :tutkintotoimikunta)
      (sql/fields :tutkintotoimikunta.diaarinumero :tutkintotoimikunta.nimi_fi :tutkintotoimikunta.nimi_sv
                  [(subselect-laske-jasenesitykset "esitetty" "mies" nil) :esitetty_miehia]
                  [(subselect-laske-jasenesitykset "esitetty" "nainen" nil) :esitetty_naisia]
                  [(subselect-laske-jasenesitykset "nimitetty" "mies" nil) :nimitetty_miehia]
                  [(subselect-laske-jasenesitykset "nimitetty" "nainen" nil) :nimitetty_naisia]
                  [(subselect-laske-jasenesitykset "esitetty" nil ["fi", "2k"]) :esitetty_fi]
                  [(subselect-laske-jasenesitykset "esitetty" nil ["sv", "2k"]) :esitetty_sv]
                  [(subselect-laske-jasenesitykset "nimitetty" nil ["fi", "2k"]) :nimitetty_fi]
                  [(subselect-laske-jasenesitykset "nimitetty" nil ["sv", "2k"]) :nimitetty_sv])

      (cond->
        toimikausi (sql/where {:tutkintotoimikunta.toimikausi_id toimikausi})
        ; Rajaa toimikunnat toimikuntiin, joihin järjestöllä tai sen alijärjestöillä on jäsenesityksiä
        jarjesto (sql/where {:tutkintotoimikunta.tkunta [in (sql/subselect :jasenyys
                                                              (sql/modifier "DISTINCT")
                                                              (sql/fields :jasenyys.toimikunta)
                                                              (sql/where {:jasenyys.esittaja [in jarjesto-ja-alijarjestot]}))]})

        vain_jasenesityksia_sisaltavat (sql/where {:tutkintotoimikunta.tkunta [in (sql/subselect :jasenyys
                                                                                    (sql/modifier "DISTINCT")
                                                                                    (sql/fields :jasenyys.toimikunta)
                                                                                    (sql/where {:jasenyys.esittaja [not= nil]}))]}))
      (sql/order :tutkintotoimikunta.nimi_fi)
      sql/exec)))

(defn poista! [jasenyys_id]
  (auditlog/jasenyys-operaatio! :poisto nil jasenyys_id)
  (sql-util/delete-unique :jasenyys
    (sql/where {:jasenyys_id jasenyys_id})))