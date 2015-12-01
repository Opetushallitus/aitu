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

(ns aitu.integraatio.sql.koulutustoimija
  (:require korma.db
            [korma.core :as sql])
  (:use [aitu.integraatio.sql.korma]))

(defn hae
  "Hakee koulutustoimijan y-tunnuksen perusteella"
  [y-tunnus]
  (first
    (sql/select
      koulutustoimija
      (sql/fields :ytunnus :nimi_fi :nimi_sv
                  :sahkoposti :puhelin :osoite :postinumero :postitoimipaikka :www_osoite)
      (sql/where {:ytunnus y-tunnus}))))

(defn hae-koulutustoimijan-oppilaitokset
  [y-tunnus]
  (sql/select
    oppilaitos
    (sql/fields :oppilaitoskoodi :nimi)
    (sql/where {:koulutustoimija y-tunnus})))

(defn hae-linkki
  "Hakee koulutustoimijalinkin (y-tunnus ja nimi) y-tunnuksen perusteella"
  [y-tunnus]
  (some-> (hae y-tunnus)
    (select-keys [:ytunnus :nimi_fi :nimi_sv])))
