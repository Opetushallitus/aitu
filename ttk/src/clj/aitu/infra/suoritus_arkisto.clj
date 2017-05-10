;; Copyright (c) 2015 The Finnish National Board of Education - Opetushallitus
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

(ns aitu.infra.suoritus-arkisto
  (:require  [clojure.string :as s]
             [korma.core :as sql]
             [aitu.auditlog :as auditlog]
             [clj-time.coerce :refer [to-sql-date]]
             [oph.korma.common :as sql-util]
             [aitu.infra.arvioija-arkisto :as arvioija-arkisto]
             [aitu.infra.tutkinto-arkisto :as tutkinto-arkisto]
             [oph.common.util.http-util :refer [parse-iso-date]]
             [oph.common.util.util :refer [erottele-lista]]
             [oph.korma.common :refer [to-hki-local-date]]
             [oph.korma.korma-auth :as ka]
             [aitu.integraatio.sql.korma :refer :all]))

(defn ->int [str-or-int]
  (when (not (nil? str-or-int))
    (if (integer? str-or-int)
      str-or-int
      (Integer/parseInt str-or-int))))

(defn hae
  [suorituskerta-id]
  (sql-util/select-unique :suorituskerta
    (sql/where {:suorituskerta_id suorituskerta-id})))


(defn hae-suoritukset
  ([suorituskerta-id]
    (sql/select suoritus
      (sql/join :tutkinnonosa (= :tutkinnonosa.tutkinnonosa_id :tutkinnonosa))
      (sql/fields :suoritus_id :arvosana :suorituskerta :tutkinnonosa :arvosanan_korotus :osaamisen_tunnustaminen :kieli :todistus :osaamisala :kokotutkinto
                  [:tutkinnonosa.osatunnus :osatunnus]
                  [:tutkinnonosa.nimi_fi :nimi]
 ; TODO: nimi_fi ei ole oikeasti hyvä juttu välttämättä..
                )
      (sql/where {:suorituskerta suorituskerta-id})))
    ([]
      (sql/select :suorituskerta
        (sql/join :suorittaja (= :suorittaja.suorittaja_id :suorittaja))
        (sql/join :suoritus (= :suoritus.suorituskerta :suorituskerta_id))
        (sql/fields :suoritus.tutkinnonosa :suoritus.arvosanan_korotus :suoritus.osaamisen_tunnustaminen :suoritus.kieli :suoritus.todistus :suoritus.osaamisala :suoritus.arvosana :suoritus.kokotutkinto
                    :suorituskerta.suorituskerta_id :tutkinto :rahoitusmuoto :suorittaja :koulutustoimija :tila :paikka :jarjestelyt :jarjestamismuoto :valmistava_koulutus
                    :suorituskerta.suoritusaika_alku :suorituskerta.suoritusaika_loppu :suorituskerta.arviointikokouksen_pvm :suorituskerta.toimikunta
                    :suorituskerta.liitetty_pvm :suorituskerta.tutkintoversio_suoritettava :suorituskerta.:kouljarjestaja
                  ))))

(defn hae-kaikki-suoritukset [koulutustoimija]
 (sql/select suorituskerta
   (sql/join suoritus (= :suoritus.suorituskerta :suorituskerta_id))
   (sql/fields :suorituskerta_id :tutkinto :rahoitusmuoto :suorittaja :koulutustoimija :jarjestelyt :paikka :valmistava_koulutus :suoritusaika_alku :suoritusaika_loppu
               :arviointikokouksen_pvm :toimikunta :liitetty_pvm :tutkintoversio_suoritettava :kouljarjestaja
               [:suoritus.suoritus_id :suoritus_id]
               [:suoritus.kokotutkinto :kokotutkinto]
               [:suoritus.arvosana :arvosana]
               [:suoritus.tutkinnonosa :tutkinnonosa]
               [:suoritus.arvosanan_korotus :arvosanan_korotus]
               [:suoritus.osaamisen_tunnustaminen :osaamisen_tunnustaminen]
               [:suoritus.kieli :kieli]
               [:suoritus.todistus :todistus]
               [:suoritus.osaamisala :osaamisala]
               [:suoritus.lisatiedot :lisatiedot])
   (sql/where (= :koulutustoimija koulutustoimija))))

(defn ^:private toimikunta-valinta->toimikunta [t]
  (if (= "Ei valittu" t) nil t))

(defn ^:private add-joins [query]
  (-> query
    (sql/join suoritus)
    (sql/join :tutkinnonosa (= :suoritus.tutkinnonosa :tutkinnonosa.tutkinnonosa_id))
    (sql/join :left :osaamisala (= :suoritus.osaamisala :osaamisala.osaamisala_id))
    (sql/join :suorittaja (= :suorittaja.suorittaja_id :suorituskerta.suorittaja))
    (sql/join :nayttotutkinto (= :nayttotutkinto.tutkintotunnus :suorituskerta.tutkinto))
    (sql/join :tutkintoversio (= :tutkintoversio.tutkintoversio_id :suorituskerta.tutkintoversio_id))
    (sql/join :left suoritettava-versio (= :suoritettava-versio.tutkintoversio_id :suorituskerta.tutkintoversio_suoritettava))
    (sql/join :left suoritettava-tutkinto (= :suoritettava-tutkinto.tutkintotunnus :suoritettava-versio.tutkintotunnus))
    (sql/join :koulutustoimija (= :koulutustoimija.ytunnus :suorituskerta.koulutustoimija))
    (sql/join :left :tutkintotoimikunta {:tutkintotoimikunta.tkunta :suorituskerta.toimikunta})
    (sql/join :left :suorituskerta_arvioija (= :suorituskerta_arvioija.suorituskerta_id :suorituskerta_id))
    (sql/join :left :arvioija (= :arvioija.arvioija_id :suorituskerta_arvioija.arvioija_id))
    ))

(defn ^:private add-toimikunta-where-ehto [query toimikunta]
  (let [where-fn (cond
                   ;; Toimikunta-hakuehtoa ei ole annettu
                   (s/blank? toimikunta) nil
                   ;; Toimikunta ei ole asetettu suoritukselle
                   (nil? (toimikunta-valinta->toimikunta toimikunta)) #(sql/where % {:toimikunta nil})
                   ;; Suoritus on tehty sellaisesta tutkinnonosasta, joka kuuluu tutkintoon, joka kuuluu valitun toimikunnan toimialaan
                   :else #(sql/where % {:nayttotutkinto.tutkintotunnus [in (sql/subselect :toimikuntien_tutkinnot
                                                                             (sql/fields :tutkintotunnus)
                                                                             (sql/where {:toimikunta toimikunta}))]}))]
    (if (fn? where-fn) (where-fn query) query)))

#_(defn ^:private add-toimikunta-plus-suoritettavatutkinto-where-ehto [query toimikunta suoritettavatutkinto]
   (let [toimikunta-query (cond
                            ;; Toimikunta-hakuehtoa ei ole annettu
                            (s/blank? toimikunta)                              nil
                            ;; Toimikunta ei ole asetettu suoritukselle
                            (nil? (toimikunta-valinta->toimikunta toimikunta)) {:suorituskerta.toimikunta nil}
                           ;; suoritus on tehty sellaisesta tutkinnonosasta, joka kuuluu tutkintoon, joka kuuluu valitun toimikunnan toimialaan
                            :else                                              {:nayttotutkinto.tutkintotunnus [in (sql/subselect :toimikuntien_tutkinnot
                                                                                                                     (sql/fields :tutkintotunnus)
                                                                                                                    (sql/where {:toimikunta toimikunta}))]})
         suoritettavatutkinto-query (when (not (s/blank? suoritettavatutkinto))
                                      {:suorituskerta.tutkintoversio_suoritettava (Integer/parseInt suoritettavatutkinto)})
         where-fn (cond
                    (and
                      (not (s/blank? toimikunta))
                      (not (s/blank? suoritettavatutkinto))) #(sql/where % (or toimikunta-query suoritettavatutkinto-query))             ;; TODO: Varmista, että tämä "or" on nimenomaan Korman "or". Testi tälle!
                    toimikunta-query                         #(sql/where % toimikunta-query)
                   suoritettavatutkinto-query               #(sql/where % suoritettavatutkinto-query))]
     (if (fn? where-fn) (where-fn query) query)))

(defn ^:private hae-kaikki-where-ehdot [query
                                   luotupvm_alku luotupvm_loppu hyvaksymispvm_alku hyvaksymispvm_loppu
                                   jarjestamismuoto koulutustoimija tutkinto tutkinnonosa
                                   suoritettavatutkinto suorittaja suorittajaid toimikunta osaamisala tila]
  (-> query
    (cond->
      (seq luotupvm_alku) (sql/where {:suorituskerta.luotuaika [>= (to-sql-date (parse-iso-date luotupvm_alku))]})
      (seq luotupvm_loppu) (sql/where {:suorituskerta.luotuaika [<= (to-sql-date (parse-iso-date luotupvm_loppu))]})
      (seq hyvaksymispvm_alku) (sql/where {:suorituskerta.hyvaksymisaika [>= (to-sql-date (parse-iso-date hyvaksymispvm_alku))]})
      (seq hyvaksymispvm_loppu) (sql/where {:suorituskerta.hyvaksymisaika [<= (to-sql-date (parse-iso-date hyvaksymispvm_loppu))]})
      (seq jarjestamismuoto) (sql/where {:suorituskerta.jarjestamismuoto jarjestamismuoto})
      (seq koulutustoimija) (sql/where {:suorituskerta.koulutustoimija koulutustoimija})
      (seq tila) (sql/where {:suorituskerta.tila tila})
      (seq tutkinto) (sql/where {:suorituskerta.tutkintoversio_id (Integer/parseInt tutkinto)})
      (seq tutkinnonosa) (sql/where {:tutkinnonosa.tutkinnonosa_id (Integer/parseInt tutkinnonosa)})
      (seq osaamisala) (sql/where {:suoritus.osaamisala (Integer/parseInt osaamisala)})
      (seq suorittajaid) (sql/where {:suorittaja.suorittaja_id (Integer/parseInt suorittajaid)})
      ;; TODO: Tarviiko olla "not blank", vai voiko olla (seq suorittaja)? Eli voiko sisältää white spaceja?
      (not (s/blank? suorittaja)) (sql/where (or
                                               {:suorittaja.hetu suorittaja}
                                               {:suorittaja.oid suorittaja}
                                               {:suorittaja.etunimi [sql-util/ilike (str "%" suorittaja "%")]}
                                               {:suorittaja.sukunimi [sql-util/ilike (str "%" suorittaja "%")]}))
      (seq toimikunta) (add-toimikunta-where-ehto toimikunta)
      (seq suoritettavatutkinto) (sql/where {:suorituskerta.tutkintoversio_suoritettava (Integer/parseInt suoritettavatutkinto)})
      )
;    (add-toimikunta-plus-suoritettavatutkinto-where-ehto toimikunta suoritettavatutkinto)
    ))

(def ^:private hae-kaikki-valitut-sql-kentat
  [:suorituskerta_id
   :rahoitusmuoto :tila :ehdotusaika :hyvaksymisaika :liitetty_pvm :arviointikokouksen_pvm
   :suoritusaika_alku :suoritusaika_loppu
   :jarjestamismuoto :opiskelijavuosi
   :valmistava_koulutus :paikka :jarjestelyt
   [:suoritus.arvosana :arvosana]
   [:suorittaja.suorittaja_id :suorittaja_suorittaja_id]
   [:suorittaja.etunimi :suorittaja_etunimi]
   [:suorittaja.sukunimi :suorittaja_sukunimi]
   [:suorittaja.syntymapvm :suorittaja_syntymapvm]
   [:nayttotutkinto.nimi_fi :tutkinto_nimi_fi]
   [:nayttotutkinto.nimi_sv :tutkinto_nimi_sv]
   [:suoritettava-tutkinto.nimi_fi :suoritettavatutkinto_nimi_fi]
   [:suoritettava-tutkinto.nimi_sv :suoritettavatutkinto_nimi_sv]
   [:suoritettava-tutkinto.tutkintotunnus :suoritettavatutkinto_tutkintotunnus]
   [:tutkinnonosa.tutkinnonosa_id :tutkinnonosa_tutkinnonosa_id]
   [:tutkinnonosa.osatunnus :osatunnus]
   [:tutkinnonosa.nimi_sv :tutkinnonosa_nimi_sv]
   [:tutkinnonosa.nimi_fi :tutkinnonosa_nimi_fi]
   :koulutustoimija  ;; TODO: Tämä puuttuu "hae-yhteenveto-raportti"-versiosta. Duplikaatti kentän :koulutustoimija.ytunnus kanssa, mutta tarvitaanko tätä kenttää frontissa? Tarvii sitten muuttaa myös where-ehto.
   :koulutustoimija.ytunnus
   [:koulutustoimija.nimi_fi :koulutustoimija_nimi_fi]
   [:koulutustoimija.nimi_sv :koulutustoimija_nimi_sv]
   ])

(defn hae-kaikki
  [{:keys [ehdotuspvm_alku ehdotuspvm_loppu hyvaksymispvm_alku hyvaksymispvm_loppu jarjestamismuoto koulutustoimija
           rahoitusmuoto tila tutkinto suoritettavatutkinto suorituskertaid suorittaja suorittajaid toimikunta kieli todistus sukupuoli
           tutkinnonosa osaamisala luotupvm_alku luotupvm_loppu]}]
  (->
    (sql/select* suorituskerta)

    add-joins

    (hae-kaikki-where-ehdot
      luotupvm_alku luotupvm_loppu hyvaksymispvm_alku hyvaksymispvm_loppu
      jarjestamismuoto koulutustoimija tutkinto tutkinnonosa
      suoritettavatutkinto suorittaja suorittajaid toimikunta osaamisala tila)

    (cond->
      (seq suorituskertaid) (sql/where {:suorituskerta.suorituskerta_id (Integer/parseInt suorituskertaid)})
      (seq ehdotuspvm_alku) (sql/where {:ehdotusaika [>= (to-sql-date (parse-iso-date ehdotuspvm_alku))]})
      (seq ehdotuspvm_loppu) (sql/where {:ehdotusaika [<= (to-sql-date (parse-iso-date ehdotuspvm_loppu))]})
      (seq rahoitusmuoto) (sql/where {:rahoitusmuoto (Integer/parseInt rahoitusmuoto)})
      (seq kieli) (sql/where {:suoritus.kieli kieli})
      (= "todistus_ei" todistus) (sql/where {:suoritus.todistus false})
      (= "todistus_kylla" todistus) (sql/where {:suoritus.todistus true})
      (= "todistus_kokotutkinto" todistus) (sql/where {:suoritus.kokotutkinto true})
      (seq sukupuoli) (sql/where {:suorittaja.sukupuoli sukupuoli})
      )

    ;; "Hae-kaikki"-versio. Alla olevat kentät puuttuvat "hae-yhteenveto-raportti"-versiosta.
    (#(apply sql/fields %
      :tutkinto :tutkintoversio_id :tutkintoversio_suoritettava
      :toimikunta :kouljarjestaja
      :suorittaja
      [:osaamisala.nimi_fi :osaamisala_nimi_fi]
      [:osaamisala.nimi_sv :osaamisala_nimi_sv]
      [:osaamisala.osaamisalatunnus :osaamisala_tunnus]
      hae-kaikki-valitut-sql-kentat))

    (sql/order :suorituskerta_id :DESC)
    (sql/order :suoritus.suoritus_id :DESC)

    sql/exec))

; TODO: ei vielä toimi täysin oikein... .. .
; TODO: Grouppaus pitää oikeasti tapahtua *suoritettavan tutkinnon* kautta. Liittäminen päätellään siitä onko *tutkinto* eri kuin *suoritettava tutkinto*, eikä siitä onko liittämispvm null
(defn ^:private laske-tilastot
  ([rakenne suoritukset]
    (let [varsinaiset-suoritukset (filter #(nil? (:liitetty_pvm %)) suoritukset)
          todistuksia-osista (count (filter #(and (not (= "Koko tutkinto" (:kokotutkinto %)))
                                                  (= "Todistus" (:todistus %))) suoritukset)) ; TODO: Ei lasketa koko tutkinto -rivejä mukaan lukumäärään
          osasuoritukset (count (filter #(nil? (:liitetty_pvm %)) suoritukset))  ; Liitetyt eivät ole osa suoritettuja
          ]
      (assoc rakenne
             :suoritetut_kokotutkinnot (count (filter #(= "Koko tutkinto" (:kokotutkinto %)) suoritukset))
             :liitetyt_osat (count (filter #(not (nil? (:liitetty_pvm %))) suoritukset)) ; :liittaminen
             :suoritetut_osat osasuoritukset
             :tunnustetut_osat (count (filter #(= ",tunnustamalla" (:tunnustaminen %)) suoritukset))
             :haluaa_todistuksen todistuksia-osista
             :ei_halua_todistusta (- osasuoritukset todistuksia-osista))))
  ([rakenne]
    (laske-tilastot rakenne (mapcat :tutkinnonosat (mapcat :tutkinnot (:suorittajat rakenne))))))

(defn hae-yhteenveto-raportti
  [{{:keys [luotupvm_alku luotupvm_loppu hyvaksymispvm_alku hyvaksymispvm_loppu jarjestamismuoto koulutustoimija
            tila tutkinto suoritettavatutkinto tutkinnonosa osaamisala suorittaja suorittajaid toimikunta edelliset-kayttaja] :as params} :params}]
  (let [results-sql
        (->
          (sql/select* suorituskerta)
          add-joins

          (hae-kaikki-where-ehdot
            luotupvm_alku luotupvm_loppu hyvaksymispvm_alku hyvaksymispvm_loppu
              jarjestamismuoto koulutustoimija tutkinto tutkinnonosa
            suoritettavatutkinto suorittaja suorittajaid toimikunta osaamisala tila)

          (cond->
              ; hivenen ruma hack. Haetaan käyttäjän edellisen viiden minuutin aikana kirjaamat rivit, jotta saadaan se mitä äsken ladattiin haettua.
              ; mutta korrektimpaa olisi pitää kirjanpitoa latauksista ja käyttää sitä viitteenä. Tehdään jos tämä ei riitä.
              (not-empty edelliset-kayttaja) (sql/where {:suoritus.luotu_kayttaja @ka/*current-user-oid*
                                                         :suoritus.luotuaika [>= (sql/raw "(now() - interval '5 minutes')")]})
              )

            (#(apply sql/fields %
              [(sql/raw "case when suoritus.osaamisen_tunnustaminen is not null then ',tunnustamalla' else ' ' end") :tunnustaminen]
              [(sql/raw "case when suoritus.todistus then 'Todistus' else ' ' end") :todistus]
              [:suoritus.osaamisen_tunnustaminen :osaamisen_tunnustaminen]
              [(sql/raw "case when suoritus.kokotutkinto then 'Koko tutkinto' else ' ' end") :kokotutkinto]
              :nayttotutkinto.tutkintotunnus
              [:tutkintoversio.peruste :tutkinto_peruste]
              [:tutkintotoimikunta.nimi_fi :tutkintotoimikunta_nimi_fi]
              [:tutkintotoimikunta.nimi_sv :tutkintotoimikunta_nimi_sv]
              [:tutkintotoimikunta.diaarinumero :tutkintotoimikunta_diaarinumero]
              [:arvioija.etunimi :arvioija_etunimi]
              [:arvioija.sukunimi :arvioija_sukunimi]
              [(sql/raw "case arvioija.rooli when 'itsenainen' then 'itsenäinen' when 'tyonantaja' then 'työnantaja' when 'tyontekija' then 'työntekijä' else arvioija.rooli end") :arvioija_rooli]
              hae-kaikki-valitut-sql-kentat))

            ;; "hae-kaikki"-funkkarissa tässä: (sql/order :suorituskerta_id :DESC)  (sql/order :suoritus.suoritus_id :DESC)

            (sql/order :tutkintotoimikunta_nimi_fi :ASC)
            (sql/order :suorittaja_sukunimi :ASC)
            (sql/order :suorittaja_etunimi :ASC)
            (sql/order :tutkinto_nimi_fi :ASC)
            (sql/order :tutkinnonosa_nimi_fi :ASC)
            (sql/order :arvioija_sukunimi :ASC)
            (sql/order :arvioija_etunimi :ASC)
            sql/exec)
          results (->> results-sql
                    (map #(assoc % :liittaminen (if (not (nil? (:liitetty_pvm %))) ",liittämällä" " "))  )
                    (map #(assoc % :tavoitetutkinto (if (not (= (:suoritettavatutkinto_nimi_fi %) (:tutkinto_nimi_fi %))) (str "-- " (:suoritettavatutkinto_nimi_fi %)) " ")) ))

      rapsa (->> results
                 (erottele-lista :arvioijat [:arvioija_etunimi :arvioija_sukunimi :arvioija_rooli])
                 (erottele-lista :tutkinnonosat [:suorituskerta_id :rahoitusmuoto :tila :hyvaksymisaika :liitetty_pvm :liittaminen :suoritusaika_alku :suoritusaika_loppu :arviointikokouksen_pvm
                                                 :jarjestamismuoto :opiskelijavuosi  :valmistava_koulutus :paikka :jarjestelyt
                                                 :arvosana :tunnustaminen :todistus  :osaamisen_tunnustaminen  :kokotutkinto
                                                 :ehdotusaika :osatunnus :tutkinnonosa_nimi_fi :tutkinnonosa_nimi_sv
                                                 :arvioijat])
                 (erottele-lista :tutkinnot [:tutkintotunnus :tutkinto_nimi_fi :tutkinto_nimi_sv :tutkinto_peruste  :tavoitetutkinto :suoritettavatutkinto_nimi_fi :suoritettavatutkinto_nimi_sv :suoritettavatutkinto_tutkintotunnus
                                             :tutkinnonosat])
                 (erottele-lista :suorittajat [:suorittaja_etunimi :suorittaja_sukunimi :suorittaja_syntymapvm
                                               :tutkinnot])
                 (erottele-lista :koulutustoimijat [:ytunnus :koulutustoimija_nimi_fi :koulutustoimija_nimi_sv
                                                    :suorittajat])
                 )
      ; kaikki yhteensä per koulutustoimija
      koulutustoimija-tilasto (fn [koulutustoimija]
                                (laske-tilastot {} (mapcat :tutkinnonosat (mapcat :tutkinnot (:suorittajat koulutustoimija)))))
      k-t (fn [koulutustoimijat]
            (map #(merge % (koulutustoimija-tilasto %)) koulutustoimijat))
      tilastoitu (map #(update % :koulutustoimijat k-t) rapsa)
      ]
      ; kaikki yhteensä per toimikunta
      (map #(merge % (laske-tilastot {} (mapcat :tutkinnonosat (mapcat :tutkinnot (mapcat :suorittajat (mapcat :koulutustoimijat tilastoitu)))))) tilastoitu)
      )
  )


(defn hae-arvioijat [suorituskerta-id]
  (sql/select :arvioija
    (sql/fields :etunimi :sukunimi :rooli :nayttotutkintomestari :arvioija.arvioija_id)
    (sql/join :suorituskerta_arvioija (= :suorituskerta_arvioija.arvioija_id :arvioija_id))
    (sql/where {:suorituskerta_arvioija.suorituskerta_id suorituskerta-id})))

(defn hae-tiedot [suorituskerta-id]
  (let [perus (first (hae-kaikki {:suorituskertaid suorituskerta-id}))
        suoritukset (hae-suoritukset (->int suorituskerta-id))
        arvioijat (hae-arvioijat (->int suorituskerta-id))]
     (assoc perus :osat suoritukset :arvioijat arvioijat)))

(defn osa->suoritus-db [osa]
  {:suorituskerta (or (:suorituskerta osa) (:suorituskerta_id osa))
   :tutkinnonosa (or (:tutkinnonosa osa) (:tutkinnonosa_id osa))
   :arvosana (:arvosana osa)
   :kokotutkinto (or (:kokotutkinto osa) false)
   :arvosanan_korotus (:arvosanan_korotus osa)
   :osaamisen_tunnustaminen (parse-iso-date (:osaamisen_tunnustaminen osa))
   :osaamisala (or (:osaamisala osa) (:osaamisala_id osa))
   :kieli (:kieli osa)
   :todistus (:todistus osa)})

(defn kerta->suorituskerta-db [kerta]
  (let [tutkintotunnus (or (:tutkinto kerta) (:tutkintotunnus (tutkinto-arkisto/hae-versio (:tutkintoversio_id kerta))))]
    (-> kerta
      (select-keys [:jarjestamismuoto :valmistava_koulutus :paikka :jarjestelyt :koulutustoimija :suoritusaika_alku :suoritusaika_loppu :opiskelijavuosi
                    :suorittaja :rahoitusmuoto :tutkinto :arviointikokouksen_pvm :tutkintoversio_id :toimikunta
                    :tutkintoversio_suoritettava :liitetty_pvm :kouljarjestaja])
      (assoc :tutkinto tutkintotunnus)
      (update :opiskelijavuosi #(or (->int %) 1)) ; TODO: poistuu
      (update :jarjestamismuoto #(or % "oppilaitosmuotoinen")) ; TODO: poistuu
      (update :tutkintoversio_suoritettava #(or % (:tutkintoversio_id kerta)))
      (update :kouljarjestaja #(or % (:koulutustoimija kerta))) ; tut. järjestäjä = koulutustoimija, jos arvoa ei ole asetettu
      (update :suoritusaika_alku parse-iso-date)
      (update :liitetty_pvm parse-iso-date)
      (update :arviointikokouksen_pvm parse-iso-date)
      (update :suoritusaika_loppu parse-iso-date))))

(defn ^:private lisaa-suoritus! [osa]
  (sql/insert suoritus
   (sql/values (osa->suoritus-db osa))))

(defn lisaa-koko-tutkinnon-suoritus! [suoritusid tutkintoversio suorittaja]
  (auditlog/suoritus-operaatio! :lisays {:kokotutkinto tutkintoversio :suorittaja suorittaja})
  (sql/insert :tutkintosuoritus
    (sql/values {:suoritus_id  suoritusid
                 :tutkintoversio_id tutkintoversio
                 :suorittaja_id suorittaja})))

(defn hae-vastuutoimikunta
  "Palauttaa nil jos yksikäsitteistä vastuutoimikuntaa ei löydy tutkinnon ja kielisyyden perusteella."
  [tutkintotunnus kieli]
  (let [toimikunnat (sql/select :toimikuntien_tutkinnot
                      (sql/where (= :tutkintotunnus tutkintotunnus)))]
    (if (= 1 (count toimikunnat))
      (first toimikunnat)
      ; kielisyys
      (let [kielirajatut (filter #(= (:kielisyys %) kieli) toimikunnat)]
        (when (= 1 (count kielirajatut)) (first kielirajatut))))))


(defn lisaa!
  [suoritus]
  (auditlog/suoritus-operaatio! :lisays suoritus)
  (let [suorituskerta (sql/insert suorituskerta (sql/values (kerta->suorituskerta-db suoritus) ))]
    (doseq [osa (:osat suoritus)]
      (let [suor (lisaa-suoritus! (assoc osa :suorituskerta_id (:suorituskerta_id suorituskerta)))]
        (when (true? (:kokotutkinto osa))
          (lisaa-koko-tutkinnon-suoritus! (:suoritus_id suor) (:tutkintoversio_suoritettava suoritus) (:suorittaja suorituskerta)))))

    (doseq [arvioija (:arvioijat suoritus)]
      (let [arvioija-id (or (:arvioija_id arvioija)
                            (:arvioija_id (arvioija-arkisto/lisaa! arvioija)))]
        (sql/insert :suorituskerta_arvioija
          (sql/values {:suorituskerta_id (:suorituskerta_id suorituskerta)
                       :arvioija_id arvioija-id}))))
    suorituskerta))

(defn liita-suoritus!
  "Liitä tutkinnon osan suoritukseen suoritettava (toinen tutkinto, johon tämä osa ei kuulu) tutkinto"
  [{:keys [suorituskerta_id tutkintoversio_suoritettava liitetty_pvm] :as suoritustiedot}]
  (auditlog/suoritus-operaatio! :paivitys suoritustiedot)
  (sql-util/update-unique suorituskerta
    (sql/set-fields {:tutkintoversio_suoritettava tutkintoversio_suoritettava
                     :liitetty_pvm (parse-iso-date liitetty_pvm)})
    (sql/where {:suorituskerta_id (->int suorituskerta_id)})))

(defn lisaa-tai-paivita!
  [{:keys [arvioijat jarjestamismuoto valmistava_koulutus paikka jarjestelyt koulutustoimija opiskelijavuosi suorittaja rahoitusmuoto tutkinto tutkintoversio_id osat suorituskerta_id tutkintoversio_suoritettava]
    :as suoritustiedot}]
  (if (nil? suorituskerta_id)
    (lisaa! suoritustiedot)
    ; päivitys
    (do
;      (auditlog/suoritus-operaatio! :paivitys {:suorituskerta_id suorituskerta_id})
       (auditlog/suoritus-operaatio! :paivitys suoritustiedot)
      (sql-util/update-unique suorituskerta
         (sql/set-fields (kerta->suorituskerta-db suoritustiedot))
         (sql/where {:suorituskerta_id (->int suorituskerta_id)}))

      ; update arvioijat
      (sql/delete :suorituskerta_arvioija
        (sql/where {:suorituskerta_id suorituskerta_id}))

      (doseq [arvioija arvioijat]
        (let [arvioija-id (or (:arvioija_id arvioija)
                              (:arvioija_id (arvioija-arkisto/lisaa! arvioija)))]
          (sql/insert :suorituskerta_arvioija
            (sql/values {:suorituskerta_id suorituskerta_id
                         :arvioija_id arvioija-id}))))

      ; update osat
      ; poistetut osat, käsitellään ennen kuin lisätään uusia osia
      (let [ids (keep :suoritus_id osat)]
        (sql/delete :suoritus
          (sql/where {:suorituskerta suorituskerta_id
                      :suoritus_id [not-in ids]})))
      (doseq [osa osat]
        (if (nil? (:suoritus_id osa))
          (lisaa-suoritus! (assoc osa :suorituskerta suorituskerta_id))
          (sql-util/update-unique suoritus
            (sql/set-fields (osa->suoritus-db (assoc osa :suorituskerta suorituskerta_id)))
            (sql/where {:suoritus_id (:suoritus_id osa)}))))
      suoritustiedot)))

(defn laheta!
  [suoritukset]
  (auditlog/suoritus-operaatio! :paivitys {:suoritukset suoritukset
                                           :tila "ehdotettu"})
  (sql/update :suorituskerta
    (sql/set-fields {:tila "ehdotettu"
                     :ehdotusaika (sql/sqlfn now)
                     :hyvaksymisaika nil})
    (sql/where {:suorituskerta_id [in suoritukset]})))

(defn hae-tiedot-monta [suoritukset]
  (sql/select suorituskerta
    (sql/where {:suorituskerta_id [in suoritukset]})))

(defn hyvaksy!
  [{:keys [hyvaksymispvm suoritukset] :as suoritusdata}]
  (auditlog/suoritus-operaatio! :paivitys {:suoritukset suoritukset
                                           :tila "hyvaksytty"})
  (sql/update suorituskerta
    (sql/set-fields {:tila "hyvaksytty"
                     :hyvaksymisaika (or (to-hki-local-date (parse-iso-date hyvaksymispvm)) (sql/sqlfn now))}) ; (sql/sqlfn now)
    (sql/where {:suorituskerta_id [in suoritukset]})))

(defn poista!
  [suorituskerta-id]
  (auditlog/suoritus-operaatio! :poisto {:suoritus-id suorituskerta-id})
  ; TODO: delete-unique olisi siistimpi, mutta REST rajapinta olettaa ilmeisesti saavansa paluuarvona poistetun entityn
  (sql/delete :suorituskerta_arvioija
    (sql/where {:suorituskerta_id suorituskerta-id}))

  (sql/delete :suorituskerta
    (sql/where {:suorituskerta_id suorituskerta-id
                :tila "luonnos"})))

(defn palauta!
  [suoritukset]
  (auditlog/suoritus-operaatio! :paivitys {:suoritukset suoritukset
                                           :tila "luonnos"})
  (sql/update :suorituskerta
    (sql/set-fields {:tila "luonnos"
                     :hyvaksymisaika nil
                     :ehdotusaika nil})
    (sql/where {:suorituskerta_id [in suoritukset]})))
