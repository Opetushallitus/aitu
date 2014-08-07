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

(ns aitu.infra.organisaatiomuutos-arkisto
  (:require [korma.core :as sql]
            [oph.korma.korma :refer :all]
            [aitu.util :refer [update-in-if-exists select-and-rename-keys]]
            [aitu.integraatio.sql.oppilaitos :as oppilaitos-kaytava]
            [aitu.integraatio.sql.koulutustoimija :as koulutustoimija-kaytava]
            [clj-time.core :as time])
  (:use [aitu.integraatio.sql.korma]))

(defn lisaa-organisaatiomuutos! [tyyppi paivamaara & {:keys [koulutustoimija oppilaitos toimipaikka]}]
  (sql/insert organisaatiomuutos
    (sql/values {:tyyppi tyyppi
                 :paivamaara paivamaara
                 :koulutustoimija koulutustoimija
                 :oppilaitos oppilaitos
                 :toimipaikka toimipaikka})))

(defn ^:private erottele-organisaatiot
  "Erottaa organisaatiomuutoksesta organisaatioiden nimet ja tunnukset omiin mappeihinsa"
  [organisaatiomuutos]
  (let [koulutustoimija (when (:koulutustoimija organisaatiomuutos)
                          (select-and-rename-keys organisaatiomuutos [[:koulutustoimija :ytunnus] [:koulutustoimija_nimi_fi :nimi_fi] [:koulutustoimija_nimi_sv :nimi_sv]]))
        oppilaitos (when (:oppilaitos organisaatiomuutos)
                     (select-and-rename-keys organisaatiomuutos [[:oppilaitos :oppilaitoskoodi] [:oppilaitos_nimi :nimi]]))
        toimipaikka (when (:toimipaikka organisaatiomuutos)
                      (select-and-rename-keys organisaatiomuutos [[:toimipaikka :toimipaikkakoodi] [:toimipaikka_nimi :nimi]]))]
    (merge organisaatiomuutos
           {:koulutustoimija koulutustoimija
            :oppilaitos oppilaitos
            :toimipaikka toimipaikka})))

(defn hae-kaikki []
  (-> (sql/select organisaatiomuutos
        (sql/with koulutustoimija
          (sql/fields [:nimi_fi :koulutustoimija_nimi_fi]
                      [:nimi_sv :koulutustoimija_nimi_sv]))
        (sql/with oppilaitos
          (sql/fields [:nimi :oppilaitos_nimi]))
        (sql/with toimipaikka
          (sql/fields [:nimi :toimipaikka_nimi]))
        (sql/order :paivamaara)
        (sql/order :koulutustoimija)
        (sql/order :oppilaitos)
        (sql/order :toimipaikka)))
  (map erottele-organisaatiot))

(defn hae-tekemattomat []
  (->>
    (sql/select organisaatiomuutos
      (sql/with koulutustoimija
        (sql/fields [:nimi_fi :koulutustoimija_nimi_fi]
                    [:nimi_sv :koulutustoimija_nimi_sv]))
      (sql/with oppilaitos
        (sql/fields [:nimi :oppilaitos_nimi]))
      (sql/with toimipaikka
        (sql/fields [:nimi :toimipaikka_nimi]))
      (sql/where (= :tehty nil))
      (sql/order :paivamaara)
      (sql/order :koulutustoimija)
      (sql/order :oppilaitos)
      (sql/order :toimipaikka))
    (map erottele-organisaatiot)))

(defn merkitse-tehdyksi [organisaatiomuutosid]
  (sql/update organisaatiomuutos
    (sql/set-fields {:tehty (time/today)})
    (sql/where {:organisaatiomuutos_id organisaatiomuutosid})))
