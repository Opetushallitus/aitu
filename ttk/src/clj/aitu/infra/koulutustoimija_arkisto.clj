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
  
  (let [nimi (str "%" (:nimi ehdot) "%")
        sop-kylla (= (:sopimuksia ehdot) "kylla")
        sop-ei (= (:sopimuksia ehdot) "ei")
        koulutustoimijat (->
                           (sql/select* koulutustoimija)
                           (sql/join :inner :jarjestamissopimus ; inner -> ei koulutustoimijoita, joilla ei ole koskaan ollut sopimusta.
                                     (and (= :koulutustoimija.ytunnus :jarjestamissopimus.koulutustoimija)
                                          (= :jarjestamissopimus.poistettu false)))
                           (sql/fields :ytunnus :nimi_fi :nimi_sv)
                           (sql/aggregate (sum (sql/raw "case WHEN jarjestamissopimus.voimassa THEN 1 ELSE 0 END")) :sopimusten_maara)
;                           (sql/aggregate (sum (sql/raw "case WHEN jarjestamissopimus.voimassa=false THEN 1 ELSE 0 END")) :eivoimassalkm)
                           (sql/group :ytunnus :nimi_fi :nimi_sv)
                           (cond->
                             (not (blank? (:tunnus ehdot))) (sql/where (sql/sqlfn exists (sql/subselect jarjestamissopimus
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
                                                                                                           (or {:opintoala.opintoala_tkkoodi (:tunnus ehdot)}
                                                                                                               {:osaamisala.osaamisalatunnus (:tunnus ehdot)}
                                                                                                               {:tutkinnonosa.osatunnus (:tunnus ehdot)}
                                                                                                               {:nayttotutkinto.tutkintotunnus (:tunnus ehdot)})))

                                                                                               (cond->   
                                                                                                 sop-kylla
                                                                                                   (sql/where (and (<= :sopimus_ja_tutkinto.alkupvm (sql/raw "current_date"))                                                                                                                   
                                                                                                                   (<= (sql/raw "current_date") (sql/sqlfn coalesce :sopimus_ja_tutkinto.loppupvm (sql/raw "current_date")) ))) ; alkupvm <= current date <= loppupvm
                                                                                                  sop-ei
                                                                                                     (sql/where (and (<= :sopimus_ja_tutkinto.alkupvm (sql/raw "current_date"))
                                                                                                                  (> (sql/raw "current_date") (sql/sqlfn coalesce :sopimus_ja_tutkinto.loppupvm (sql/raw "current_date")) ))) ; alkupvm <= current_date && current date > loppupvm
                                                                                                   ))))
                             (not (blank? (:nimi ehdot))) (sql/where (or {:nimi_fi [ilike nimi]}
                                                                         {:nimi_sv [ilike nimi]}))
                             ; tämä ehto vain jos ei ole rajattu tunnuksella
                             (and (blank? (:tunnus ehdot)) sop-kylla) (sql/having (> (sql/raw "sum(case WHEN jarjestamissopimus.voimassa THEN 1 ELSE 0 END)") 0))
                             (and (blank? (:tunnus ehdot)) sop-ei) (sql/having (= (sql/raw "sum(case WHEN jarjestamissopimus.voimassa THEN 1 ELSE 0 END)") 0))
                              )
                           (sql/order :nimi_fi :ASC)
                           sql/exec)]
    (if (:avaimet ehdot)
      (map #(select-keys % (:avaimet ehdot)) koulutustoimijat)
      koulutustoimijat)))

(defn hae-nimet
  []
  (sql/select :koulutustoimija
    (sql/fields :ytunnus :nimi_fi :nimi_sv)
    (sql/order :nimi_fi)))
