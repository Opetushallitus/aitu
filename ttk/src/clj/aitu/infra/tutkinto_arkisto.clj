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

(ns aitu.infra.tutkinto-arkisto
  (:require [korma.core :as sql]
            [aitu.toimiala.tutkinto :as toimiala]
            [aitu.infra.jarjestamissopimus-arkisto :as sopimus-arkisto]
            [aitu.infra.opintoala-arkisto :as opintoala-arkisto]
            [aitu.infra.sopimus-ja-tutkinto-arkisto :as sopimus-ja-tutkinto-arkisto]
            [oph.common.util.util :refer [sisaltaako-kentat?]]
            [clojure.string :refer [blank?]]
            [aitu.toimiala.voimassaolo.saanto.tutkinto :as voimassaolo])
  (:use [aitu.integraatio.sql.korma]))

(defn ^:test-api tyhjenna!
  "Tyhjentää arkiston."
  []
  (sql/exec-raw "delete from nayttotutkinto"))

(defn ^:integration-api lisaa!
  "Lisää tutkinnon arkistoon."
  [tutkinto]
  {:pre [(toimiala/tutkinto? tutkinto)]}
  (sql/insert nayttotutkinto
    (sql/values tutkinto)))

(defn ^:integration-api lisaa-tutkintoversio!
  "Lisää tutkintoversion arkistoon"
  [versio]
  {:pre [(toimiala/tutkintoversio? versio)]}
  (sql/insert tutkintoversio
    (sql/values versio)))

(defn ^:integration-api paivita-tutkintoversio!
  [versio]
  (sql/update tutkintoversio
    (sql/set-fields (dissoc versio :tutkintoversio_id))
    (sql/where {:tutkintoversio_id (:tutkintoversio_id versio)})))

(defn ^:integration-api paivita!
  "Päivittää tutkinnon tiedot"
  [tutkintotunnus tutkinto]
  (sql/update nayttotutkinto
    (sql/set-fields (dissoc tutkinto :tutkintotunnus))
    (sql/where {:tutkintotunnus tutkintotunnus})))

(defn ^:private hae-uusin-tutkintoversio
  [tutkintotunnus]
  (first (sql/select tutkintoversio
           (sql/where (= :tutkintoversio_id (sql/subselect nayttotutkinto
                                              (sql/fields :uusin_versio_id)
                                              (sql/where {:tutkintotunnus tutkintotunnus})))))))

(defn ^:integration-api paivita-tutkinto!
  "Tekee uuden version tutkinnosta ja palauttaa tutkintoversion id:n"
  [tutkinto]
  (let [tutkintotiedot (select-keys tutkinto [:nimi_fi :nimi_sv :opintoala :tyyppi :tutkintotaso])
        versiotiedot (select-keys tutkinto [:voimassa_alkupvm :voimassa_loppupvm :koodistoversio :jarjestyskoodistoversio])
        tutkintotunnus (:tutkintotunnus tutkinto)
        vanha-versio (hae-uusin-tutkintoversio tutkintotunnus)
        uusi-versio (->
                      (merge vanha-versio versiotiedot)
                      (update-in [:versio] inc)
                      (dissoc :tutkintoversio_id))]
    (when (seq tutkintotiedot)
      (paivita! tutkintotunnus tutkintotiedot))
    (let [tutkintoversio-id (:tutkintoversio_id (lisaa-tutkintoversio! uusi-versio))]
      (paivita! tutkintotunnus {:uusin_versio_id tutkintoversio-id})
      tutkintoversio-id)))

(defn ^:integration-api lisaa-tutkinto-ja-versio!
  "Lisää arkistoon tutkinnon ja sille tutkintoversion. Palauttaa lisätyn tutkintoversion id:n."
  [tutkinto-ja-versio]
  (let [tutkinto (select-keys tutkinto-ja-versio [:tutkintotunnus :opintoala :tyyppi :tutkintotaso :nimi_fi :nimi_sv :uusin_versio_id])
        versio (select-keys tutkinto-ja-versio [:tutkintotunnus :tutkintoversio_id :versio :koodistoversio :peruste
                                                :siirtymaajan_loppupvm :voimassa_alkupvm :voimassa_loppupvm])]
    (lisaa! tutkinto)
    (let [versio (lisaa-tutkintoversio! versio)
          tutkintotunnus (:tutkintotunnus tutkinto)
          tutkinto (merge {:uusin_versio_id (:tutkintoversio_id versio)} tutkinto)]
      (paivita! tutkintotunnus tutkinto)
      (:tutkintoversio_id versio))))

(defn ^:private hae-tutkinnonosan-uusin-versio
  [osatunnus]
  (first (sql/select tutkinnonosa
           (sql/where (and (= :osatunnus osatunnus)
                           (= :versio (sql/subselect tutkinnonosa
                                        (sql/aggregate (max :versio) :max_versio)
                                        (sql/where {:osatunnus osatunnus}))))))))

(defn ^:integration-api lisaa-tutkinnon-osa!
  "Lisää tutkinnon osan"
  [tutkintoversio_id jarjestysnumero osa]
  (let [osa (sql/insert tutkinnonosa
              (sql/values osa))]
    (sql/insert tutkinto-ja-tutkinnonosa
      (sql/values {:tutkintoversio tutkintoversio_id
                   :tutkinnonosa (:tutkinnonosa_id osa)
                   :jarjestysnumero jarjestysnumero}))))

(defn ^:integration-api paivita-tutkinnon-osa!
  "Tekee uuden version tutkinnonosasta"
  [tutkintoversio_id jarjestysnumero osa]
  (let [vanha-osa (hae-tutkinnonosan-uusin-versio (:osatunnus osa))
        uusi-osa (->
                   (merge vanha-osa osa)
                   (update-in [:versio] inc)
                   (dissoc :tutkinnonosa_id))]
    (lisaa-tutkinnon-osa! tutkintoversio_id jarjestysnumero uusi-osa)))

(defn ^:test-api poista-tutkinnon-osa!
  "Poistaa tutkinnon osan"
  [osatunnus]
  (sql/delete tutkinto-ja-tutkinnonosa
    (sql/where {:tutkinnonosa [in (sql/subselect tutkinnonosa
                                    (sql/fields :tutkinnonosa_id)
                                    (sql/where {:osatunnus osatunnus}))]}))
  (sql/delete tutkinnonosa
    (sql/where {:osatunnus osatunnus})))

(defn ^:private hae-osaamisalan-uusin-versio
  [osaamisalatunnus]
  (first (sql/select osaamisala
           (sql/where (and (= :osaamisalatunnus osaamisalatunnus)
                           (= :versio (sql/subselect osaamisala
                                        (sql/aggregate (max :versio) :max_versio)
                                        (sql/where {:osaamisalatunnus osaamisalatunnus}))))))))

(defn ^:integration-api lisaa-osaamisala!
  "Lisää osaamisalan"
  [oala]
  (sql/insert osaamisala
    (sql/values oala)))

(defn ^:integration-api paivita-osaamisala!
  "Lisää uuden version osaamisalasta"
  [oala]
  (let [vanha-ala (hae-osaamisalan-uusin-versio (:osaamisalatunnus oala))
        uusi-ala (->
                   (merge vanha-ala oala)
                   (update-in [:versio] inc)
                   (dissoc :osaamisala_id))]
    (lisaa-osaamisala! uusi-ala)))

(defn ^:test-api poista-osaamisala!
  "Poistaa osaamisalan"
  [osaamisalatunnus]
  (sql/delete osaamisala
    (sql/where {:osaamisalatunnus osaamisalatunnus})))

(defn hae-kaikki
  "Hakee kaikkien tutkintojen uusimman version."
  []
  (sql/select nayttotutkinto
    (sql/with uusin-versio)
    (sql/with opintoala
      (sql/fields [:selite_fi :opintoala_nimi_fi] [:selite_sv :opintoala_nimi_sv]))
    (sql/with tutkintotoimikunta)))

(defn hae-tutkinnot-tutkinnonosat-osaamisalat
  "Hakee kaikkien tutkintojen sekä niihin liittyvien tutkinnonosien ja osaamisalojen uusimman version."
  []
  (sql/select nayttotutkinto
    (sql/with uusin-versio
      (sql/with tutkinto-ja-tutkinnonosa
        (sql/with tutkinnonosa))
      (sql/with osaamisala))
    (sql/with opintoala
      (sql/fields :koulutusala_tkkoodi))))

(defn liita-sopimus-ja-tutkinto-riviin
  [sopimus-ja-tutkinto]
  (let [{:keys [jarjestamissopimusid]} sopimus-ja-tutkinto]
    (-> sopimus-ja-tutkinto
      (assoc :jarjestamissopimus (sopimus-arkisto/hae jarjestamissopimusid)))))

(defn liita-sopimus-ja-tutkinto-riveihin
  [rivit]
  (mapv liita-sopimus-ja-tutkinto-riviin rivit))

(defn hae-tutkintotunnukseen-liittyvat-sopimus-ja-tutkinto-rivit
  [tutkintotunnus]
  (liita-sopimus-ja-tutkinto-riveihin (sopimus-ja-tutkinto-arkisto/hae-tutkintotunnukseen-liittyvat tutkintotunnus)))

(defn hae-tutkinto
  [tutkintotunnus]
  (first
    (sql/select
      nayttotutkinto
      (sql/with uusin-versio)
      (sql/with tutkintotyyppi
        (sql/fields [:selite_fi :tyyppi_selite_fi]
                    [:selite_sv :tyyppi_selite_sv]))
      (sql/with tutkintotoimikunta
        (sql/with toimikausi))
      (sql/where {:tutkintotunnus tutkintotunnus}))))

(defn hae
  "Hakee tutkinnon tunnuksen perusteella"
  [tutkintotunnus]
  (let [tutkinto (hae-tutkinto tutkintotunnus)
        opintoala (opintoala-arkisto/hae (:opintoala tutkinto))
        sopimus-ja-tutkinto-rivit (hae-tutkintotunnukseen-liittyvat-sopimus-ja-tutkinto-rivit tutkintotunnus)]
    (some-> tutkinto
            (assoc :opintoala opintoala)
            (assoc :sopimus_ja_tutkinto sopimus-ja-tutkinto-rivit))))

(defn hae-opintoalat-tutkinnot
  "Hakee listan kaikista opintoaloista ja tutkinnoista annetun termin perusteella"
  [termi]
  (let [opintoalat (sql/select opintoala
                     (sql/fields [:opintoala_tkkoodi :tunnus] [:selite_fi :nimi_fi] [:selite_sv :nimi_sv]))
        tutkinnot (sql/select nayttotutkinto
                    (sql/fields [:tutkintotunnus :tunnus] :nimi_fi :nimi_sv))
        kaikki (concat opintoalat tutkinnot)]

    (->> kaikki
      (filter #(sisaltaako-kentat? % [:nimi_fi :nimi_sv] termi))
      (sort-by :nimi_fi))))

(defn hae-opintoalat-tutkinnot-osaamisalat-tutkinnonosat
  "Hakee listan kaikista opintoaloista, tutkinnoista, osaamisaloista ja tutkinnon osista annetun termin perusteella."
  [termi]
  (let [opintoalat (sql/select opintoala
                     (sql/fields [:opintoala_tkkoodi :tunnus] [:selite_fi :nimi_fi] [:selite_sv :nimi_sv]))
        osaamisalat (sql/select osaamisala
                      (sql/fields [:osaamisalatunnus :tunnus] :nimi_fi :nimi_sv))
        tutkinnonosat (sql/select tutkinnonosa
                        (sql/fields [:osatunnus :tunnus] :nimi_fi :nimi_sv))
        tutkinnot (sql/select nayttotutkinto
                    (sql/fields [:tutkintotunnus :tunnus] :nimi_fi :nimi_sv))
        kaikki (for [ala (concat opintoalat osaamisalat tutkinnonosat tutkinnot)]
                 (assoc ala :termi (:nimi_fi ala)))]
    (->> kaikki
      (filter #(sisaltaako-kentat? % [:nimi_fi :nimi_sv] termi))
      (sort-by :termi))))

(defn hae-toimikunnan-toimiala [tkunta]
  (sql/select toimikunta-ja-tutkinto
    (sql/with nayttotutkinto
      (sql/with opintoala))
    (sql/fields [:opintoala.selite_fi :opintoala_fi]
                [:opintoala.selite_sv :opintoala_sv]
                [:nayttotutkinto.nimi_fi :tutkinto_fi]
                [:nayttotutkinto.nimi_sv :tutkinto_sv])
    (sql/where {:toimikunta tkunta})
    (sql/order :opintoala.selite_fi)
    (sql/order :nayttotutkinto.nimi_fi)))

(defn hae-ehdoilla [ehdot]
  (let [nimi (str "%" (:nimi ehdot) "%")
        tutkinnot (map voimassaolo/taydenna-tutkinnon-voimassaolo
                       (sql/select nayttotutkinto
                         (sql/with uusin-versio)
                         (sql/with opintoala
                           (sql/fields [:selite_fi :opintoala_fi] [:selite_sv :opintoala_sv]))
                         (sql/where (or (blank? (:nimi ehdot))
                                        {:nimi_fi [ilike nimi]}
                                        {:nimi_sv [ilike nimi]}))))
        palautettavat-tutkinnot (if (not= "kaikki" (:voimassa ehdot))
                                  (filter :voimassa tutkinnot)
                                  tutkinnot)]
    (if (:avaimet ehdot)
      (map #(select-keys % (:avaimet ehdot)) palautettavat-tutkinnot)
      palautettavat-tutkinnot)))

(defn hae-raportti [ehdot]
  (let [tutkinnot (sql/select :tutkintoversio
                    (sql/join :inner :nayttotutkinto (= :nayttotutkinto.tutkintotunnus :tutkintoversio.tutkintotunnus))
                    (sql/join :inner :toimikunta_ja_tutkinto (= :toimikunta_ja_tutkinto.tutkintotunnus :nayttotutkinto.tutkintotunnus))
                    (sql/join :inner :tutkintotoimikunta (= :tutkintotoimikunta.tkunta :toimikunta_ja_tutkinto.toimikunta))
                    (sql/join :inner :toimikausi (= :toimikausi.toimikausi_id :tutkintotoimikunta.toimikausi_id))
                    (sql/join :left [(sql/subselect :jarjestamissopimus
                                       (sql/join :inner :sopimus_ja_tutkinto (and (= :sopimus_ja_tutkinto.jarjestamissopimusid :jarjestamissopimus.jarjestamissopimusid)
                                                                                  (= :sopimus_ja_tutkinto.poistettu false)))
                                       (sql/fields :sopimus_ja_tutkinto.tutkintoversio :jarjestamissopimus.toimikunta
                                                   :sopimus_ja_tutkinto.kieli :jarjestamissopimus.koulutustoimija
                                                   :jarjestamissopimus.voimassa)) :sopimus]
                              (and (= :sopimus.tutkintoversio :tutkintoversio.tutkintoversio_id)
                                   (= :sopimus.toimikunta :tutkintotoimikunta.tkunta)
                                   (= :sopimus.voimassa true)))
                    (sql/fields :nayttotutkinto.opintoala :nayttotutkinto.tutkintotunnus :nayttotutkinto.tutkintotaso
                                [:nayttotutkinto.nimi_fi :tutkinto_fi] [:nayttotutkinto.nimi_sv :tutkinto_sv]
                                :tutkintoversio.peruste :sopimus.kieli :sopimus.koulutustoimija
                                [:tutkintotoimikunta.diaarinumero :toimikunta] :tutkintotoimikunta.toimikausi_alku :tutkintotoimikunta.toimikausi_loppu)
                    (sql/aggregate (count :sopimus.koulutustoimija) :lukumaara)
                    (sql/group :nayttotutkinto.opintoala :nayttotutkinto.tutkintotunnus :tutkintoversio.peruste
                               :sopimus.kieli :sopimus.koulutustoimija :tutkintotoimikunta.tkunta)
                    (sql/order :tutkintotoimikunta.toimikausi_loppu :desc)
                    (sql/order :nayttotutkinto.opintoala)
                    (sql/order :nayttotutkinto.nimi_fi)
                    (sql/order :sopimus.koulutustoimija))]
    (if (:avaimet ehdot)
      (map #(select-keys % (:avaimet ehdot)) tutkinnot)
      tutkinnot)))

(defn ^:test-api poista-tutkintoversio!
  "Poistaa tutkintoversion arkistosta"
  [tutkintoversio_id]
  (sql/delete tutkintoversio
    (sql/where {:tutkintoversio_id tutkintoversio_id})))

(defn ^:test-api poista!
  "Poistaa tutkinnon arkistosta."
  [tutkintotunnus]
  (sql/delete tutkintoversio
    (sql/where {:tutkintotunnus tutkintotunnus}))
  (sql/delete nayttotutkinto
    (sql/where {:tutkintotunnus tutkintotunnus})))
