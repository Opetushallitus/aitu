;; Copyright (c) 2013 The Finnish National Board of Education - Opetushallitus
;;
;; This program is free software:  Licensed under the EUPL :Version 1.1 or - as
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

(ns aitu.infra.koulutustoimija-arkisto
  (:require  [korma.core :as sql]
             [oph.common.util.util :refer [select-and-rename-keys sisaltaako-kentat?]]
             [aitu.integraatio.sql.koulutustoimija :as koulutustoimija-kaytava]
             [aitu.infra.jarjestamissopimus-arkisto :as sopimus-arkisto]
             [clojure.string :refer [blank?]]
             [oph.korma.common :as sql-util]
             [aitu.integraatio.sql.korma :refer :all]))

(defn ^:integration-api lisaa!
  [kt]
  (sql/insert koulutustoimija
    (sql/values kt)))

(defn ^:integration-api paivita!
  [kt]
  (sql/update koulutustoimija
    (sql/set-fields (dissoc kt :ytunnus))
    (sql/where {:ytunnus (:ytunnus kt)})))

(defn ^:test-api poista!
  [y-tunnus]
  (sql/delete koulutustoimija
    (sql/where {:ytunnus y-tunnus})))

(defn ^:integration-api laske-voimassaolo! []
  (sql/update koulutustoimija
    (sql/set-fields {:voimassa false})
    (sql/where {:lakkautuspaiva [< (sql/raw "current_date")]})))

(defn hae
  "Hakee yhden koulutustoimijan julkiset tiedot"
  [y-tunnus]
  (let [koulutustoimija (koulutustoimija-kaytava/hae y-tunnus)
        sopimukset (sopimus-arkisto/hae-koulutustoimijan-sopimukset y-tunnus)
        oppilaitokset (koulutustoimija-kaytava/hae-koulutustoimijan-oppilaitokset y-tunnus)]
    (some-> koulutustoimija
      (assoc :jarjestamissopimus sopimukset
             :oppilaitokset oppilaitokset))))

(defn hae-julkiset-tiedot
  "Hakee kaikkien koulutustoimijoiden julkiset tiedot"
  []
  (sql/select koulutustoimija
   (sql/fields :ytunnus :nimi_fi :nimi_sv :muutettu_kayttaja :luotu_kayttaja :muutettuaika :luotuaika
               :sahkoposti :puhelin :osoite :postinumero :postitoimipaikka :www_osoite
               (sql/raw "(select count(*) from jarjestamissopimus where koulutustoimija = ytunnus and voimassa) as sopimusten_maara"))
   (sql/order :nimi_fi)))

(defn hae-kaikki []
  (sql/select koulutustoimija))

(defn hae-termilla
  "Suodattaa hakutuloksia termillä"
  [termi]
  (let [termi (str \% termi \%)]
    (sql/select koulutustoimija
      (sql/fields :ytunnus :nimi_fi :nimi_sv)
      (sql/where (and {:voimassa true}
                      (or {:nimi_fi [ilike termi]}
                          {:nimi_sv [ilike termi]})))
      (sql/order :nimi_fi))))

(defn hae-ehdoilla
  "Hakee kaikki hakuehtoja vastaavat koulutustoimijat. Ala sisältää opintoalan, tutkinnon, osaamisalan ja tutkinnon osan."
  [ehdot]

  (let [sop-voimassa-kylla?  (= "kylla" (:sopimuksia ehdot))
        sop-voimassa-ei?     (= "ei" (:sopimuksia ehdot))
        tunnus-ehto-puuttuu? (blank? (:tunnus ehdot))
        nimi-ehto-puuttuu?   (blank? (:nimi ehdot))
        nimi                 (str "%" (:nimi ehdot) "%")
        ;;
        ;; TODO: Tämä kysely ei toimi oikein, kun on tunnus-ehto (esim "Tuotekehitystyön EAT", jonka tunnus '358902').
        ;;       Alla oleva Exists-subquery kai vaan katsoo, että koulutustoimijalla ON JOKIN ehdot täyttävä sopimus,
        ;;       ja sitten koko query palauttaa (virheellisesti) KAIKKI sopimukset filtteröimättä niitä tunnuksen mukaan.
        ;;       Pitäisi _ehkä_ tehdä samat joinit kuin subqueryssa ja lisätä sum-aggregaattiin samat tunnusta tarkastelevat where-ehdot kuin subqueryssa...
        ;;
        koulutustoimijat (->
                           (sql/select* koulutustoimija)
                           (sql/join :inner :jarjestamissopimus ; inner -> ei koulutustoimijoita, joilla ei ole koskaan ollut sopimusta.
                             (and (= :koulutustoimija.ytunnus :jarjestamissopimus.koulutustoimija)
                               (= :jarjestamissopimus.poistettu false)))
                           (sql/join :inner sopimus-ja-tutkinto
                             (= :jarjestamissopimus.jarjestamissopimusid :sopimus_ja_tutkinto.jarjestamissopimusid))
                           (sql/join :inner tutkintoversio
                             (= :sopimus_ja_tutkinto.tutkintoversio :tutkintoversio.tutkintoversio_id))
                           (sql/fields :ytunnus :nimi_fi :nimi_sv)


                           (sql/aggregate (sum (sql/raw "case WHEN (jarjestamissopimus.voimassa and (current_date <= tutkintoversio.siirtymaajan_loppupvm)) THEN 1 ELSE 0 END")) :sopimusten_maara)
;                           (sql/aggregate (sum (sql/raw "case WHEN (jarjestamissopimus.voimassa = false or (current_date > tutkintoversio.siirtymaajan_loppupvm)) THEN 1 ELSE 0 END")) :eivoimassalkm)

                           (sql/group :ytunnus :nimi_fi :nimi_sv)

                           (cond->
                             (not tunnus-ehto-puuttuu?) (sql/where (sql/sqlfn exists (sql/subselect jarjestamissopimus
                                                                                       (sql/join :inner sopimus-ja-tutkinto
                                                                                         (= :jarjestamissopimus.jarjestamissopimusid :sopimus_ja_tutkinto.jarjestamissopimusid))
                                                                                       (sql/join :inner tutkintoversio
                                                                                         (= :sopimus_ja_tutkinto.tutkintoversio :tutkintoversio.tutkintoversio_id))
                                                                                       (sql/join :inner nayttotutkinto
                                                                                         (= :tutkintoversio.tutkintotunnus :nayttotutkinto.tutkintotunnus))
                                                                                       (sql/join :left opintoala
                                                                                         (= :nayttotutkinto.opintoala :opintoala.opintoala_tkkoodi))
                                                                                       (sql/join :left tutkinnonosa
                                                                                         (= :tutkintoversio.tutkintoversio_id :tutkinnonosa.tutkintoversio))
                                                                                       (sql/join :left osaamisala
                                                                                         (= :tutkintoversio.tutkintoversio_id :osaamisala.tutkintoversio))
                                                                                       (sql/where (and {:jarjestamissopimus.koulutustoimija :koulutustoimija.ytunnus}
                                                                                                    (or {:opintoala.opintoala_tkkoodi   (:tunnus ehdot)}
                                                                                                      {:osaamisala.osaamisalatunnus   (:tunnus ehdot)}
                                                                                                      {:tutkinnonosa.osatunnus        (:tunnus ehdot)}
                                                                                                      {:nayttotutkinto.tutkintotunnus (:tunnus ehdot)})))
                                                                                       (cond->
                                                                                         sop-voimassa-kylla?
                                                                                         ;; alkupvm <= current date <= loppupvm  &&  current date <= siirtymaajan_loppupvm
                                                                                         ;; tutkintoversio.siirtymaajan_loppupvm kenttä on tietokannassa NOT NULL
                                                                                         (sql/where (and (<= :sopimus_ja_tutkinto.alkupvm (sql/raw "current_date"))
                                                                                                                (<= (sql/raw "current_date") (sql/sqlfn coalesce :sopimus_ja_tutkinto.loppupvm (sql/raw "current_date")))
                                                                                                                (<= (sql/raw "current_date") :tutkintoversio.siirtymaajan_loppupvm)
                                                                                                                ))
                                                                                         sop-voimassa-ei?
                                                                                         ;; alkupvm <= current_date && (current date > loppupvm || current date > siirtymaajan_loppupvm)
                                                                                         (sql/where (and (<= :sopimus_ja_tutkinto.alkupvm (sql/raw "current_date"))
                                                                                                      (or (> (sql/raw "current_date") (sql/sqlfn coalesce :sopimus_ja_tutkinto.loppupvm (sql/raw "current_date")))
                                                                                                        (> (sql/raw "current_date") :tutkintoversio.siirtymaajan_loppupvm)
                                                                                                        )))
                                                                                         ))))
                             (not nimi-ehto-puuttuu?) (sql/where (or {:nimi_fi [ilike nimi]}
                                                                     {:nimi_sv [ilike nimi]}))
                             ; nämä ehtot vain jos ei ole rajattu tunnuksella
                             ;; TODO: Miksi eivät ole käytössä, jos on annettu tunnus-ehto? Muuten "Voimassa"-valinta ei vaikuta ollenkaan näytettäviin tutkintoihin.
                             (and tunnus-ehto-puuttuu? sop-voimassa-kylla?) (sql/having (> (sql/raw "sum(case WHEN (jarjestamissopimus.voimassa and (current_date <= tutkintoversio.siirtymaajan_loppupvm)) THEN 1 ELSE 0 END)") 0))
                             (and tunnus-ehto-puuttuu? sop-voimassa-ei?)    (sql/having (= (sql/raw "sum(case WHEN (jarjestamissopimus.voimassa and (current_date <= tutkintoversio.siirtymaajan_loppupvm)) THEN 1 ELSE 0 END)") 0))
                             )
                           (sql/order :nimi_fi :ASC)
                           sql/exec
                           )
        ]
    (if (:avaimet ehdot)
      (map #(select-keys % (:avaimet ehdot)) koulutustoimijat)
      koulutustoimijat)))

(defn hae-nimet
  []
  (sql/select :koulutustoimija
    (sql/fields :ytunnus :nimi_fi :nimi_sv)
    (sql/order :nimi_fi)))

(defn hae-tiedot
  [ytunnus]
  (sql-util/select-unique-or-nil :koulutustoimija
    (sql/where {:ytunnus ytunnus})))
