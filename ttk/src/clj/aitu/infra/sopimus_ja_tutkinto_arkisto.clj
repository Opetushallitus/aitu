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

(ns aitu.infra.sopimus-ja-tutkinto-arkisto
  (:require korma.db
            [korma.core :as sql]
            [aitu.integraatio.sql.jarjestamissopimus :as sopimus-kaytava]
            [aitu.integraatio.sql.sopimus-ja-tutkinto :as kaytava]
            [aitu.integraatio.sql.tutkinto :as tutkinto-kaytava])
  (:use [aitu.integraatio.sql.korma]))

(defn liita-sopimus-ja-tutkinto-riviin
  [tutkintoversion-haku-fn sopimus-ja-tutkinto]
  (let [{:keys [jarjestamissopimusid sopimus_ja_tutkinto_id tutkintoversio]} sopimus-ja-tutkinto]
    (-> sopimus-ja-tutkinto
      (assoc :tutkintoversio (tutkintoversion-haku-fn tutkintoversio))
      (assoc :jarjestamissopimus (sopimus-kaytava/hae jarjestamissopimusid))
      (assoc :jarjestamissuunnitelmat
             (sopimus-kaytava/hae-sopimus-ja-tutkinto-rivin-jarjestamissuunnitelmat sopimus_ja_tutkinto_id))
      (assoc :liitteet (sopimus-kaytava/hae-sopimus-ja-tutkinto-rivin-liitteet sopimus_ja_tutkinto_id)))))

(defn liita-sopimus-ja-tutkinto-riveihin
  [tutkintoversion-haku-fn rivit]
  (mapv (partial liita-sopimus-ja-tutkinto-riviin tutkintoversion-haku-fn) rivit))

(defn hae-jarjestamissopimukseen-liittyvat
  "Hakee järjestämissopimukseen liittyvät sopimus-ja-tutkinto-tiedot"
  [jarjestamissopimusid]
  (let [sopimus-ja-tutkinto-rivit (kaytava/hae-jarjestamissopimukseen-liittyvat-rivit jarjestamissopimusid)]
    (liita-sopimus-ja-tutkinto-riveihin tutkinto-kaytava/hae-tutkintoversio sopimus-ja-tutkinto-rivit)))

(defn hae-sopimukseen-liittyvat-tutkinnonosiin-asti
  "Hakee järjestämissopimukseen liittyvät sopimus-ja-tutkinto-tiedot mukaanlukien tutkinnot ja tutkinnonosat"
  [jarjestamissopimusid]
  (let [sopimus-ja-tutkinto-rivit (kaytava/hae-jarjestamissopimukseen-liittyvat-rivit jarjestamissopimusid)]
    (liita-sopimus-ja-tutkinto-riveihin tutkinto-kaytava/hae-tutkintoversio-ja-tutkinnonosat sopimus-ja-tutkinto-rivit)))

(defn hae-tutkintotunnukseen-liittyvat
  "Hakee tutkintotunnukseen liittyvät sopimus-ja-tutkinto-tiedot"
  [tutkintotunnus]
  (let [sopimus-ja-tutkinto-rivit (kaytava/hae-tutkintotunnukseen-liittyvat-rivit tutkintotunnus)]
    (liita-sopimus-ja-tutkinto-riveihin tutkinto-kaytava/hae-tutkintoversio sopimus-ja-tutkinto-rivit)))
