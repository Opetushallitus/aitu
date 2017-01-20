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
            [oph.korma.common :as sql-util]
            [clojure.string :refer [blank?]]
            [aitu.toimiala.voimassaolo.saanto.tutkinto :as voimassaolo]
            [aitu.integraatio.sql.korma :refer :all]))

(defn hae-versio [versioid]
  (sql-util/select-unique tutkintoversio
                          (sql/where {:tutkintoversio_id versioid})))
    
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
  (sql-util/select-unique tutkintoversio
    (sql/where (= :tutkintoversio_id (sql/subselect nayttotutkinto
                                       (sql/fields :uusin_versio_id)
                                       (sql/where {:tutkintotunnus tutkintotunnus}))))))

(defn hae-tutkintoversio-perusteella
  [tutkintotunnus peruste]
  (sql-util/select-unique-or-nil tutkintoversio
    (sql/where {:peruste peruste
                :tutkintotunnus tutkintotunnus})))

(defn ^:integration-api paivita-tutkinto!
  "Jos peruste on muuttunut, tekee uuden version tutkinnosta. Jos peruste on sama, päivittää olemassaolevaa.
   Palauttaa tutkintoversion id:n."
  [tutkinto]
  (let [tutkintotiedot (select-keys tutkinto [:nimi_fi :nimi_sv :opintoala :tyyppi :tutkintotaso])
        versiotiedot (select-keys tutkinto [:voimassa_alkupvm :voimassa_loppupvm :koodistoversio 
                                            :siirtymaajan_loppupvm  :jarjestyskoodistoversio :peruste :eperustetunnus])
        tutkintotunnus (:tutkintotunnus tutkinto)
        vanha-versio (or (hae-tutkintoversio-perusteella tutkintotunnus (:peruste tutkinto))
                         (hae-tutkintoversio-perusteella tutkintotunnus nil))
        uusi-versio (->
                      (merge (hae-uusin-tutkintoversio tutkintotunnus) versiotiedot)
                      (update-in [:versio] inc)
                      (dissoc :tutkintoversio_id))]
    (when (seq tutkintotiedot)
      (paivita! tutkintotunnus tutkintotiedot))
    (if (and vanha-versio
             (or (= (:peruste vanha-versio) (:peruste versiotiedot))
                 (= (:voimassa_alkupvm vanha-versio) (:voimassa_alkupvm versiotiedot)))) ; vanha peruste on nil, mutta voimassaolo täsmää -> sama versio
      (do
        (paivita-tutkintoversio! (assoc versiotiedot :tutkintoversio_id (:tutkintoversio_id vanha-versio)))
        (:tutkintoversio_id vanha-versio))
      (let [tutkintoversio-id (:tutkintoversio_id (lisaa-tutkintoversio! uusi-versio))]
        (paivita! tutkintotunnus {:uusin_versio_id tutkintoversio-id})
        tutkintoversio-id))))

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
  (sql-util/select-unique tutkinnonosa
    (sql/where (and (= :osatunnus osatunnus)
                    (= :versio (sql/subselect tutkinnonosa
                                 (sql/aggregate (max :versio) :max_versio)
                                 (sql/where {:osatunnus osatunnus})))))))

(defn ^:private hae-tutkinnonosan-id
  [osatunnus tutkintoversio_id]
  (sql-util/select-unique-or-nil tutkinnonosa
    (sql/fields :tutkinnonosa.tutkinnonosa_id)
    (sql/where {:osatunnus osatunnus
                :tutkintoversio tutkintoversio_id})))

(defn ^:integration-api lisaa-tutkinnon-osa!
  "Lisää tutkinnon osan"
  [tutkintoversio_id jarjestysnumero osa]
  (sql/insert tutkinnonosa
    (sql/values (assoc osa
                       :jarjestysnumero jarjestysnumero
                       :tutkintoversio tutkintoversio_id))))

(defn ^:test-api poista-tutkinnon-osa!
  "Poistaa tutkinnon osan"
  [osatunnus]
  (sql/delete tutkinnonosa
    (sql/where {:osatunnus osatunnus})))

(defn hae-osaamisalat
  "Hakee jokaisen osaamisalan uusimman version. Jos tutkintotunnus on annettu, hakee vain sen tutkinnon osaamisalat."
  ([]
   (sql/select osaamisala
     (sql/where (= :osaamisala_id (sql/subselect [osaamisala :oa2]
                                    (sql/aggregate (max :osaamisala_id) :max_id)
                                    (sql/where {:oa2.osaamisalatunnus :osaamisala.osaamisalatunnus}))))))
  ([tutkintoversio_id]
    (first 
      (sql/select tutkintoversio
                  (sql/join nayttotutkinto)
                  (sql/with osaamisala
                    (sql/fields :nimi_fi :nimi_sv :osaamisalatunnus :osaamisala_id))
                  (sql/fields 
                    :tutkintoversio.tutkintotunnus :nayttotutkinto.opintoala :tutkintoversio_id :peruste
                    )
                  (sql/where {:tutkintoversio_id tutkintoversio_id})))))

(defn ^:integration-api lisaa-osaamisala!
  "Lisää osaamisalan"
  [oala]
  (sql/insert osaamisala
    (sql/values oala)))

(defn ^:integration-api paivita-osaamisalan-tutkinnonosat!
  [osatunnus->id osaamisala-id osaamisala]
  (doseq [osa (:osat osaamisala)]
    (sql-util/insert-or-update osaamisala-ja-tutkinnonosa [:osaamisala :tutkinnonosa] {:tutkinnonosa (osatunnus->id (:tutkinnonosa osa))
                                                                                       :jarjestysnumero (:jarjestys osa)
                                                                                       :osaamisala osaamisala-id})))

(defn ^:integration-api paivita-osaamisala!
  "Lisää uuden version osaamisalasta"
  [osatunnus->id oala]
  (let [uusi-ala (sql-util/insert-or-update osaamisala [:osaamisalatunnus :tutkintoversio] oala)]
    (paivita-osaamisalan-tutkinnonosat! osatunnus->id (:osaamisala_id uusi-ala) oala)))

(defn ^:test-api poista-osaamisala!
  "Poistaa osaamisalan"
  [osaamisalatunnus]
  (sql/delete osaamisala
    (sql/where {:osaamisalatunnus osaamisalatunnus})))

(defn ^:integration-api lisaa-tai-paivita-tutkintonimike!
  [tutkintoversio-id nimike]
  (sql-util/insert-or-update tutkintonimike
                             :nimiketunnus
                             nimike)
  (sql-util/insert-if-not-exists tutkintonimike-ja-tutkintoversio
                                 {:tutkintonimike (:nimiketunnus nimike)
                                  :tutkintoversio tutkintoversio-id}))

(defn ^:integration-api poista-tutkinnon-tutkintonimikkeet!
  [tutkintoversio-id]
  (sql/delete tutkintonimike-ja-tutkintoversio
    (sql/where {:tutkintoversio tutkintoversio-id})))

(defn hae-kaikki
  "Hakee kaikkien tutkintojen uusimman version."
  []
  (sql/select nayttotutkinto
    (sql/with uusin-versio)
    (sql/with opintoala
      (sql/fields [:selite_fi :opintoala_nimi_fi] [:selite_sv :opintoala_nimi_sv]))
    (sql/with tutkintotoimikunta)))

(defn hae-kaikki-suoritettavat-versiot
  "Hakee kaikkien tutkintojen kaikki versiot, joihin voi vielä kirjata suorituksia."
  []
  (sql/select nayttotutkinto
    (sql/join tutkintoversio)
    (sql/where 
      (and
        (> :tutkintoversio.siirtymaajan_loppupvm (sql/raw "current_date"))
        (not (= :tutkintoversio.peruste nil))))
    (sql/fields :tutkintoversio.tutkintoversio_id :tutkintotunnus :nimi_fi :nimi_sv :tutkintoversio.siirtymaajan_loppupvm :tutkintoversio.voimassa_loppupvm :tutkintoversio.peruste)
    ))

(defn hae-tutkintoversiot-ja-osaamisalat
  "Hakee kaikkien tutkintoversiot, joihin voi vielä kirjata suorituksia."
  []
  (sql/select tutkintoversio
    (sql/with osaamisala
      (sql/fields :nimi_fi :nimi_sv :osaamisalatunnus :osaamisala_id))
    (sql/fields :tutkintoversio_id)
    (sql/where (> :siirtymaajan_loppupvm (sql/raw "current_date")))
    (sql/order :tutkintoversio_id)))

(defn hae-tutkinnot-ja-osaamisalat
  "Hakee kaikkien tutkintojen sekä niihin liittyvien osaamisalojen uusimman version"
  []
  (sql/select nayttotutkinto
    (sql/with uusin-versio
      (sql/with osaamisala
        (sql/fields :nimi_fi :nimi_sv :osaamisalatunnus)))
    (sql/with opintoala
      (sql/fields [:selite_fi :opintoala_nimi_fi] [:selite_sv :opintoala_nimi_sv]))
    (sql/with tutkintotoimikunta)))

(defn hae-tutkinnot-ja-osaamisalat-haku
  "Hakee tutkintojen tiedot voimassaolevalle ja mahdolliselle voimaantulevalle tutkintoversiolle. hae-tutkinnot-ja-osaamisalat hakee vain uusimmat versiot."
  []
  (sql/select tutkintoversio
   (sql/join nayttotutkinto)
;   (sql/join :toimikunta_ja_tutkinto (= :toimikunta_ja_tutkinto.tutkintotunnus :tutkintoversio.tutkintotunnus))
;   (sql/join tutkintotoimikunta (= :toimikunta_ja_tutkinto.toimikunta :tutkintotoimikunta.tkunta))
   (sql/join :opintoala (= :nayttotutkinto.opintoala :opintoala.opintoala_tkkoodi  ))
   (sql/with osaamisala
     (sql/fields :nimi_fi :nimi_sv :osaamisalatunnus))
   (sql/where (or 
                    (> :voimassa_loppupvm (sql/raw "current_date"))
                    (> :voimassa_alkupvm (sql/raw "current_date"))
                    (= :tutkintoversio_id :nayttotutkinto.uusin_versio_id)
                    ))
   (sql/fields [:opintoala.selite_fi :opintoala_nimi_fi] 
               [:opintoala.selite_sv :opintoala_nimi_sv]
               :tutkintoversio_id :voimassa_alkupvm :voimassa_loppupvm
               :siirtymaajan_loppupvm :eperustetunnus
               :peruste :tutkintotunnus
               [:nayttotutkinto.nimi_fi :nimi_fi]
               [:nayttotutkinto.nimi_sv :nimi_sv]
               :nayttotutkinto.opintoala
               :nayttotutkinto.tyyppi :nayttotutkinto.tutkintotaso)))
;   (sql/fields [:opintoala.selite_fi :opintoala_nimi_fi] 
;               [:opintoala.selite_sv :opintoala_nimi_sv]
               
            ;   ))   ;    (sql/with tutkintotoimikunta)                                       
   ;    ))

(defn hae-tutkinnot-koodistopalvelulle
  "Hakee kaikkien tutkintojen uusimman version koodistopalveluintegraatiolle."
  []
  (sql/select nayttotutkinto
    (sql/with uusin-versio
      (sql/with tutkintonimike))
    (sql/with opintoala
      (sql/fields :koulutusala_tkkoodi))))

(defn liita-sopimus-ja-tutkinto-riviin
  [sopimus-ja-tutkinto]
  (let [{:keys [jarjestamissopimusid]} sopimus-ja-tutkinto]
    (-> sopimus-ja-tutkinto
      (assoc :jarjestamissopimus (sopimus-arkisto/hae-rajatut-tiedot jarjestamissopimusid)))))

(defn liita-sopimus-ja-tutkinto-riveihin
  [rivit]
  (mapv liita-sopimus-ja-tutkinto-riviin rivit))

(defn hae-tutkintotunnukseen-liittyvat-sopimus-ja-tutkinto-rivit
  [tutkintotunnus]
  (liita-sopimus-ja-tutkinto-riveihin (sopimus-ja-tutkinto-arkisto/hae-tutkintotunnukseen-liittyvat tutkintotunnus)))

(defn hae-tutkinto
  "Hakee tutkinnon uusimman version"
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

(defn hae-tutkintoversiot
  "Hakee kaikki tietyn tutkinnon tutkintoversiot"
  [tutkintotunnus]
  (sql/select tutkintoversio
    (sql/where {:tutkintotunnus tutkintotunnus})))

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
                    (sql/join :inner :opintoala (= :nayttotutkinto.opintoala :opintoala.opintoala_tkkoodi))
                    (sql/join :inner :toimikunta_ja_tutkinto (= :toimikunta_ja_tutkinto.tutkintotunnus :nayttotutkinto.tutkintotunnus))
                    (sql/join :inner :tutkintotoimikunta (= :tutkintotoimikunta.tkunta :toimikunta_ja_tutkinto.toimikunta))
                    (sql/join :inner :toimikausi (= :toimikausi.toimikausi_id :tutkintotoimikunta.toimikausi_id))
                    (sql/join :left [(sql/subselect :jarjestamissopimus
                                       (sql/join :inner :sopimus_ja_tutkinto (and (= :sopimus_ja_tutkinto.jarjestamissopimusid :jarjestamissopimus.jarjestamissopimusid)
                                                                                  (= :sopimus_ja_tutkinto.poistettu false)))
                                       (sql/join :inner :koulutustoimija (= :jarjestamissopimus.koulutustoimija :koulutustoimija.ytunnus))
                                       (sql/fields :sopimus_ja_tutkinto.tutkintoversio :jarjestamissopimus.toimikunta
                                                   :sopimus_ja_tutkinto.kieli :koulutustoimija.ytunnus
                                                   [:koulutustoimija.nimi_fi :koulutustoimija_fi] :jarjestamissopimus.voimassa)) :sopimus]
                              (and (= :sopimus.tutkintoversio :tutkintoversio.tutkintoversio_id)
                                   (= :sopimus.toimikunta :tutkintotoimikunta.tkunta)
                                   (= :sopimus.voimassa true)))
                    (sql/fields [:nayttotutkinto.opintoala :opintoalatunnus] [:opintoala.selite_fi :opintoala_fi]
                                :nayttotutkinto.tutkintotunnus :nayttotutkinto.tutkintotaso
                                [:nayttotutkinto.nimi_fi :tutkinto_fi] [:nayttotutkinto.nimi_sv :tutkinto_sv]
                                :tutkintoversio.peruste :sopimus.kieli :sopimus.ytunnus :sopimus.koulutustoimija_fi
                                [:tutkintotoimikunta.diaarinumero :toimikunta] [:tutkintotoimikunta.nimi_fi :toimikunta_fi]
                                :tutkintotoimikunta.toimikausi_alku :tutkintotoimikunta.toimikausi_loppu :tutkintotoimikunta.tilikoodi)
                    (sql/aggregate (count :sopimus.ytunnus) :lukumaara)
                    (sql/group :nayttotutkinto.opintoala :nayttotutkinto.tutkintotunnus :tutkintoversio.peruste
                               :sopimus.kieli :sopimus.koulutustoimija_fi :tutkintotoimikunta.nimi_fi :tutkintotoimikunta.tkunta :opintoala.selite_fi :sopimus.ytunnus)
                    (sql/order :tutkintotoimikunta.toimikausi_loppu :desc)
                    (sql/order :nayttotutkinto.opintoala)
                    (sql/order :nayttotutkinto.nimi_fi)
                    (sql/order :sopimus.koulutustoimija_fi))]
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

(defn hae-perusteen-tutkinnonosat [tutkintoversio-id]
  (sql/select tutkinnonosa
    (sql/where {:tutkintoversio tutkintoversio-id})))

(defn ^:integration-api paivita-osaamisalat!
  "Päivittää tutkinnon perusteen osaamisalat"
  [peruste]
  (let [tutkintoversio-id (:tutkintoversio_id (hae-tutkintoversio-perusteella (:tutkinto peruste) (:diaarinumero peruste)))
        osatunnus->id (into {} (for [osa (hae-perusteen-tutkinnonosat tutkintoversio-id)]
                                 [(:osatunnus osa) (:tutkinnonosa_id osa)]))]
    (sql/delete osaamisala-ja-tutkinnonosa
      (sql/where {:osaamisala [in (sql/subselect osaamisala
                                    (sql/fields :osaamisala_id)
                                    (sql/where {:tutkintoversio tutkintoversio-id
                                                :osaamisalatunnus [not-in (map :osaamisalatunnus (:osaamisalat peruste))]}))]}))
    (sql/delete osaamisala
      (sql/where {:tutkintoversio tutkintoversio-id
                  :osaamisalatunnus [not-in (map :osaamisalatunnus (:osaamisalat peruste))]}))
    (doseq [oala (:osaamisalat peruste)]
      (paivita-osaamisala! osatunnus->id (assoc (select-keys oala [:nimi_fi :nimi_sv :osaamisalatunnus])
                                                :tutkintoversio tutkintoversio-id)))))

(defn ^:integration-api paivita-tutkinnonosat!
  "Päivittää tutkinnon perusteen tutkinnonosat"
  [peruste]
  (let [tutkintoversio-id (:tutkintoversio_id (hae-tutkintoversio-perusteella (:tutkinto peruste) (:diaarinumero peruste)))]
    (sql/delete osaamisala-ja-tutkinnonosa
      (sql/where {:tutkinnonosa [in (sql/subselect tutkinnonosa
                                      (sql/fields :tutkinnonosa_id)
                                      (sql/where {:tutkintoversio tutkintoversio-id
                                                  :osatunnus [not-in (map :osatunnus (:tutkinnonosat peruste))]}))]}))
    (sql/delete sopimus-ja-tutkinto-ja-tutkinnonosa
      (sql/where {:tutkinnonosa [in (sql/subselect tutkinnonosa
                                      (sql/fields :tutkinnonosa_id)
                                      (sql/where {:tutkintoversio tutkintoversio-id
                                                  :osatunnus [not-in (map :osatunnus (:tutkinnonosat peruste))]}))]}))
    (sql/delete tutkinnonosa
      (sql/where {:tutkintoversio tutkintoversio-id
                  :osatunnus [not-in (map :osatunnus (:tutkinnonosat peruste))]}))
    (doseq [osa (:tutkinnonosat peruste)]
      (sql-util/insert-or-update tutkinnonosa [:osatunnus :tutkintoversio] (assoc (select-keys osa [:osatunnus :nimi_fi :nimi_sv :jarjestysnumero])
                                                                                  :tutkintoversio tutkintoversio-id)))))

(defn hae-viimeisin-eperusteet-paivitys []
  (:paivitetty (sql-util/select-unique-or-nil eperusteet-log
                 (sql/order :id :desc)
                 (sql/limit 1)
                 (sql/fields :paivitetty))))

(defn ^:integration-api tallenna-viimeisin-eperusteet-paivitys! [ajankohta]
  (sql/insert eperusteet-log
    (sql/values {:paivitetty ajankohta})))
