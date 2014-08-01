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

(ns aitu.infra.koulutustoimija-arkisto
  (:require  [korma.core :as sql]
             [aitu.util :refer [sisaltaako-kentat? select-and-rename-keys]]
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
  "Hakee kaikki tietyn alan koulutustoimijat. Ala sisältää opintoalan, tutkinnon, osaamisalan ja tutkinnon osan."
  [ala]
  (if (clojure.string/blank? ala)
    (hae-julkiset-tiedot)
    (let [termi (str "%" ala "%")]
      (map sql-timestamp->joda-datetime
           (sql/exec-raw [(str "select ytunnus, nimi_fi, nimi_sv, muutettu_kayttaja, luotu_kayttaja, muutettuaika, luotuaika, "
                               "sahkoposti, puhelin, osoite, postinumero, postitoimipaikka, www_osoite, "
                               "(select count(*) from jarjestamissopimus where koulutustoimija = ytunnus and voimassa) as sopimusten_maara "
                               "from koulutustoimija kt "
                               "where exists (select 1 from jarjestamissopimus js "
                               "              join sopimus_ja_tutkinto st on js.jarjestamissopimusid = st.jarjestamissopimusid "
                               "              join tutkintoversio tv on st.tutkintoversio = tv.tutkintoversio_id "
                               "              join nayttotutkinto t on tv.tutkintotunnus = t.tutkintotunnus "
                               "              left join opintoala oa on t.opintoala = oa.opintoala_tkkoodi "
                               "              left join tutkinto_ja_tutkinnonosa tjt on tv.tutkintoversio_id = tjt.tutkintoversio "
                               "              left join tutkinnonosa tos on tjt.tutkinnonosa = tos.tutkinnonosa_id "
                               "              left join osaamisala osala on tv.tutkintoversio_id = osala.tutkintoversio "
                               "              where js.koulutustoimija = kt.ytunnus "
                               "                    and (oa.selite_fi ilike ? "
                               "                         or oa.selite_sv ilike ? "
                               "                         or osala.nimi_fi ilike ? "
                               "                         or osala.nimi_sv ilike ? "
                               "                         or tos.nimi_fi ilike ? "
                               "                         or tos.nimi_sv ilike ?"
                               "                         or t.nimi_fi ilike ?"
                               "                         or t.nimi_sv ilike ?)) ")
                          (repeat 8 termi)]
                         :results)))))
