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

(ns aitu.integraatio.sql.oppilaitos
  (:require korma.db
            [korma.core :as sql])
  (:use [aitu.integraatio.sql.korma]))

(defn hae
  "Hakee oppilaitoksen oppilaitoskoodin perusteella"
  [oppilaitoskoodi]
  (first
    (sql/select
      oppilaitos
      (sql/fields :oppilaitoskoodi :nimi :kieli
                  :sahkoposti :puhelin :osoite :postinumero :postitoimipaikka :www_osoite :alue :koulutustoimija)
      (sql/where {:oppilaitoskoodi oppilaitoskoodi}))))

(defn hae-oppilaitoksen-toimipaikat
  "Hakee oppilaitoksen toimipaikat"
  [oppilaitoskoodi]
  (sql/select
    toimipaikka
    (sql/fields :toimipaikkakoodi :nimi :kieli :muutettu_kayttaja :luotu_kayttaja :muutettuaika :luotuaika
                :sahkoposti :puhelin :osoite :postinumero :postitoimipaikka :www_osoite :oppilaitos)
    (sql/where {:oppilaitos oppilaitoskoodi})))
