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

(ns aitu.integraatio.sql.jarjestamissopimus
  (:require korma.db
            [korma.core :as sql]
            [aitu.integraatio.sql.korma :refer :all]))

(defn ^:private jarjestamissopimuksen-rajatut-kentat
  [query]
  (sql/fields query :jarjestamissopimusid :sopimusnumero :alkupvm :loppupvm :voimassa))

(defn ^:private jarjestamissopimuksen-kentat
  [query]
  (sql/fields query
              :jarjestamissopimusid :sopimusnumero :alkupvm :loppupvm :toimikunta
              :sopijatoimikunta :tutkintotilaisuuksista_vastaava_oppilaitos
              :vastuuhenkilo :puhelin :sahkoposti :koulutustoimija :poistettu
              :muutettu_kayttaja :luotu_kayttaja :muutettuaika :luotuaika :voimassa))

(defn hae
  "Hakee järjestämissopimus-taulun rivin jarjestamissopimusid:n perusteella"
  [jarjestamissopimusid]
  (first
    (sql/select
      jarjestamissopimus
      (jarjestamissopimuksen-kentat)
      (sql/where {:jarjestamissopimusid jarjestamissopimusid
                  :poistettu false}))))

(defn hae-toimikunnan-sopimukset
  "Hakee toimikuntaan liittyvät järjestämissopimus-taulun rivit"
  [toimikunta]
  (vec
    (sql/select
     jarjestamissopimus
     (sql/fields :jarjestamissopimusid :sopimusnumero :alkupvm :loppupvm :voimassa :koulutustoimija)
     (sql/where {:toimikunta toimikunta
                 :poistettu false}))))

(defn hae-oppilaitoksen-sopimukset
  "Hakee oppilaitokseen liittyvät järjestämissopimus-taulun rivit"
  [oppilaitoskoodi]
  (vec
    (sql/select
      jarjestamissopimus
      (jarjestamissopimuksen-rajatut-kentat)
      (sql/where {:tutkintotilaisuuksista_vastaava_oppilaitos oppilaitoskoodi
                  :poistettu false}))))

(defn hae-koulutustoimijan-sopimukset
  "Hakee koulutustoimijaan liittyvät järjestämissopimus-taulun rivit"
  [y-tunnus]
  (vec
    (sql/select jarjestamissopimus
      (jarjestamissopimuksen-rajatut-kentat)
      (sql/where {:koulutustoimija y-tunnus
                  :poistettu false}))))

(defn hae-sopimus-ja-tutkinto-rivin-jarjestamissuunnitelmat
  [sopimus-ja-tutkinto-id]
  (vec
    (sql/select
      jarjestamissuunnitelma
      (sql/fields :jarjestamissuunnitelma_id :jarjestamissuunnitelma_filename)
      (sql/where {:sopimus_ja_tutkinto sopimus-ja-tutkinto-id
                  :poistettu false}))))

(defn hae-sopimus-ja-tutkinto-rivin-liitteet
  [sopimus-ja-tutkinto-id]
  (vec
    (sql/select
      sopimuksen-liite
      (sql/fields :sopimuksen_liite_id :sopimuksen_liite_filename)
      (sql/where {:sopimus_ja_tutkinto sopimus-ja-tutkinto-id
                  :poistettu false}))))
