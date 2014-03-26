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

(ns aitu.infra.haku-arkisto
  (:require  [korma.core :as sql]
             [clojure.string :as s])
  (:use [aitu.integraatio.sql.korma]))

(def hakutiedot [{:url "/toimikunta/*/tiedot"
                  :kentta "diaarinumero"
                  :taulu "tutkintotoimikunta"}
                 {:url "/sopimus/*/tiedot"
                  :kentta "sopimusnumero"
                  :taulu "jarjestamissopimus"}
                 {:url "/jarjestaja/*/tiedot"
                  :kentta "oppilaitoskoodi"
                  :taulu "oppilaitos"}
                 {:url "/tutkinto/*"
                  :kentta "tutkintotunnus"
                  :taulu "nayttotutkinto"}])

(defn muodosta-select-lause
  "muodostaa select lauseen yhdelle taululle"
  [hakutieto tunnus]
  (str "select " (:kentta hakutieto) " as tunnus, '" (:url hakutieto) "' as url"
       " from " (:taulu hakutieto)
       " where " (:kentta hakutieto) " = '" tunnus "'"))

(defn muodosta-haku
  "yhdistää hakutiedot yhdeksi kyselyksi union all:lla"
  [tunnus]
  (s/join " union all " (map #(muodosta-select-lause % tunnus) hakutiedot)))

(defn hae-tunnuksella-ensimmainen
  "Hakee tiedot tunnuksella ja palauttaa ensimmäisen löydetyistä."
  [tunnus]
  (first (sql/exec-raw [(muodosta-haku tunnus)] :results)))
