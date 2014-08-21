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
             [aitu.util :refer [select-and-rename-keys]]
             [oph.common.util.util :refer [sisaltaako-kentat?]]
             [aitu.integraatio.sql.koulutustoimija :as koulutustoimija-kaytava]
             [aitu.infra.jarjestamissopimus-arkisto :as sopimus-arkisto]
             [oph.korma.korma :refer :all]
             )
  (:use [aitu.integraatio.sql.korma]))

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
  (for [koulutustoimija (hae-julkiset-tiedot)
        :when (sisaltaako-kentat? koulutustoimija [:nimi_fi :nimi_sv] termi)]
    (select-keys koulutustoimija [:ytunnus :nimi_fi :nimi_sv])))

(defn hae-alalla
  "Hakee kaikki tietyn alan koulutustoimijat. Ala sisältää opintoalan :tutkinnon :osaamisalan ja tutkinnon osan."
  [ala]
  (let [termi (str "%" ala "%")]
    (sql/select koulutustoimija
      (sql/fields :ytunnus :nimi_fi :nimi_sv :muutettu_kayttaja :luotu_kayttaja :muutettuaika :luotuaika :sahkoposti :puhelin :osoite :postinumero :postitoimipaikka :www_osoite
                  [(sql/subselect jarjestamissopimus
                     (sql/aggregate (count :*) :count)
                     (sql/where {:jarjestamissopimus.koulutustoimija :koulutustoimija.ytunnus
                                 :jarjestamissopimus.voimassa true})) :sopimusten_maara])
      (sql/where (or (clojure.string/blank? ala)
                     (sql/sqlfn exists (sql/subselect jarjestamissopimus
                                         (sql/join :inner sopimus-ja-tutkinto
                                                   (= :jarjestamissopimus.jarjestamissopimusid :sopimus_ja_tutkinto.jarjestamissopimusid))
                                         (sql/join :inner tutkintoversio
                                                   (= :sopimus_ja_tutkinto.tutkintoversio :tutkintoversio.tutkintoversio_id))
                                         (sql/join :inner nayttotutkinto
                                                   (= :tutkintoversio.tutkintotunnus :nayttotutkinto.tutkintotunnus))
                                         (sql/join :left opintoala
                                                   (= :nayttotutkinto.opintoala :opintoala.opintoala_tkkoodi))
                                         (sql/join :left tutkinto-ja-tutkinnonosa
                                                   (= :tutkintoversio.tutkintoversio_id :tutkinto_ja_tutkinnonosa.tutkintoversio))
                                         (sql/join :left tutkinnonosa
                                                   (= :tutkinto_ja_tutkinnonosa.tutkinnonosa :tutkinnonosa.tutkinnonosa_id))
                                         (sql/join :left osaamisala
                                                   (= :tutkintoversio.tutkintoversio_id :osaamisala.tutkintoversio))
                                         (sql/where (and {:jarjestamissopimus.koulutustoimija :koulutustoimija.ytunnus}
                                                         (or {:opintoala.selite_fi [ilike termi]}
                                                             {:opintoala.selite_sv [ilike termi]}
                                                             {:osaamisala.nimi_fi [ilike termi]}
                                                             {:osaamisala.nimi_sv [ilike termi]}
                                                             {:tutkinnonosa.nimi_fi [ilike termi]}
                                                             {:tutkinnonosa.nimi_sv [ilike termi]}
                                                             {:nayttotutkinto.nimi_fi [ilike termi]}
                                                             {:nayttotutkinto.nimi_sv [ilike termi]})))))))
      (sql/order :ytunnus :ASC))))
