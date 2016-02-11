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

(ns aitu.rest-api.jarjestamissopimus
  (:require [oph.common.util.http-util :refer [file-download-response file-upload-response validoi parse-iso-date json-response csv-download-response sallittu-jos]]
            [aitu.rest-api.http-util :refer [sallittu-tiedostotyyppi? tarkasta_surrogaattiavaimen_vastaavuus_entiteetiin jos-lapaisee-virustarkistuksen]]
            [aitu.util :refer [muodosta-csv]]
            [oph.common.util.util :refer [->vector]]
            [aitu.infra.jarjestamissopimus-arkisto :as arkisto]
            [aitu.infra.i18n :as i18n]
            [valip.predicates :refer [present?]]
            [aitu.toimiala.skeema :as skeema]
            [compojure.api.core :refer [DELETE GET POST PUT defroutes]]
            [aitu.infra.validaatio :refer [validoi-alkupvm-sama-tai-ennen-loppupvm]]
            [aitu.toimiala.jarjestamissopimus :as jarjestamissopimus]
            [aitu.toimiala.voimassaolo.saanto.jarjestamissopimus :as voimassaolo]))

(defn validoi-uniikki-sopimusnumero [sopimus]
  (fn [sopimusnumero]
    (arkisto/uniikki-sopimusnumero? sopimusnumero (:jarjestamissopimusid sopimus))))

(defn luo-sopimukselle-validointisaannot [sopimus]
  [[:sopimusnumero present? :pakollinen]
   [:sopimusnumero (validoi-uniikki-sopimusnumero sopimus) :ei-uniikki]
   [:toimikunta present? :pakollinen]
   [:koulutustoimija present? :pakollinen]
   [:tutkintotilaisuuksista_vastaava_oppilaitos present? :pakollinen]
   [:alkupvm #(not (nil? %)) :pakollinen]
   [:alkupvm (validoi-alkupvm-sama-tai-ennen-loppupvm (:loppupvm sopimus)) :virheellinen-voimassaolon-alkupvm]])

(defn luo-sopimuksen-luonnille-validointisaannot [sopimus]
  (conj (luo-sopimukselle-validointisaannot sopimus)
        [:sopijatoimikunta present? :pakollinen]))

(defn paljas-tai-kentan-arvo
  [arvo-tai-rakenne kentta]
  (if (coll? arvo-tai-rakenne)
    (arvo-tai-rakenne kentta)
    arvo-tai-rakenne))

(defn salli-sopimuksen-paivitys?
  [jarjestamissopimusid]
  (some->
    (jarjestamissopimus/taydenna-sopimus (arkisto/hae jarjestamissopimusid))
    voimassaolo/taydenna-sopimuksen-voimassaolo
    :vanhentunut
    not))

(def rajattujen-sopimuskenttien-jarjestys
  [:sopimusnumero :toimikunta_fi :toimikunta_sv :tutkinto_fi :tutkinto_sv :peruste :koulutustoimija_fi :koulutustoimija_sv :alkupvm :loppupvm
   :vastuuhenkilo :vastuuhenkilo_sahkoposti :vastuuhenkilo_puhelin])

(def kaikkien-sopimuskenttien-jarjestys
  [:ytunnus :koulutustoimija_fi :koulutustoimija_sv :diaarinumero :toimikunta_fi :toimikunta_sv :tilikoodi :sopimusnumero :alkupvm :loppupvm
   :opintoalatunnus :opintoala_fi :opintoala_sv :tutkintotunnus :tutkinto_fi :tutkinto_sv :peruste :siirtymaajan_loppupvm :osaamisalat
   :oppilaitoskoodi :oppilaitos :kieli :vastuuhenkilo :vastuuhenkilo_sahkoposti :vastuuhenkilo_puhelin])

(defroutes raportti-reitit
  (GET "/csv" [voimassa :as req]
    :kayttooikeus :raportointi
    (let [voimassa (not= voimassa "false")]
      (csv-download-response (muodosta-csv (arkisto/hae-sopimukset-csv (assoc (:params req)
                                                                              :avaimet rajattujen-sopimuskenttien-jarjestys
                                                                              :voimassa voimassa))
                                           rajattujen-sopimuskenttien-jarjestys)
                             "sopimukset.csv")))
  (GET "/raportti" [toimikausi opintoala]
    :kayttooikeus :raportointi
    (let [hakuehdot {:toimikausi (Integer/parseInt toimikausi)
                     :opintoala (->vector opintoala)
                     :avaimet kaikkien-sopimuskenttien-jarjestys}
          raportti (sort-by (juxt :koulutustoimija_fi :toimikunta_fi) (arkisto/hae-sopimukset-csv hakuehdot))]
     (csv-download-response (muodosta-csv raportti kaikkien-sopimuskenttien-jarjestys)
                            "sopimukset.csv"))))

(defroutes reitit
  (POST "/:tkunta" [tkunta tutkintotunnus toimikunta sopijatoimikunta koulutustoimija tutkintotilaisuuksista_vastaava_oppilaitos sopimusnumero alkupvm loppupvm jarjestamissopimusid vastuuhenkilo sahkoposti puhelin voimassa]
    :kayttooikeus [:sopimus_lisays tkunta]
    (let [sopimus (merge {:sopimusnumero sopimusnumero
                          :toimikunta tkunta
                          :sopijatoimikunta (paljas-tai-kentan-arvo sopijatoimikunta :tkunta)
                          :koulutustoimija (paljas-tai-kentan-arvo koulutustoimija :ytunnus)
                          :tutkintotilaisuuksista_vastaava_oppilaitos (paljas-tai-kentan-arvo tutkintotilaisuuksista_vastaava_oppilaitos :oppilaitoskoodi)
                          :alkupvm (if alkupvm (parse-iso-date alkupvm) nil)
                          :loppupvm (if loppupvm (parse-iso-date loppupvm) nil)
                          :vastuuhenkilo vastuuhenkilo
                          :puhelin puhelin
                          :sahkoposti sahkoposti
                          :voimassa voimassa}
                         (when jarjestamissopimusid {:jarjestamissopimusid jarjestamissopimusid}))]
      (validoi sopimus (luo-sopimuksen-luonnille-validointisaannot sopimus) ((i18n/tekstit) :validointi)
               (let [uusi-sopimus (arkisto/lisaa! sopimus)]
                 (json-response uusi-sopimus)))))

  (PUT "/:jarjestamissopimusid" [jarjestamissopimusid sopimusnumero alkupvm loppupvm koulutustoimija tutkintotilaisuuksista_vastaava_oppilaitos toimikunta sopimus_ja_tutkinto vastuuhenkilo sahkoposti puhelin]
    :kayttooikeus [:sopimustiedot_paivitys jarjestamissopimusid]
    (let [jarjestamissopimus_id_int (Integer/parseInt jarjestamissopimusid)]
      (sallittu-jos (salli-sopimuksen-paivitys? jarjestamissopimus_id_int)
        (let [sopimus {:jarjestamissopimusid jarjestamissopimus_id_int
                       :sopimusnumero sopimusnumero
                       :alkupvm (when alkupvm (parse-iso-date alkupvm))
                       :loppupvm (when loppupvm (parse-iso-date loppupvm))
                       :koulutustoimija  (paljas-tai-kentan-arvo koulutustoimija :ytunnus)
                       :tutkintotilaisuuksista_vastaava_oppilaitos (paljas-tai-kentan-arvo tutkintotilaisuuksista_vastaava_oppilaitos :oppilaitoskoodi)
                       :toimikunta (paljas-tai-kentan-arvo toimikunta :tkunta)
                       :vastuuhenkilo vastuuhenkilo
                       :puhelin puhelin
                       :sahkoposti sahkoposti}]
          (validoi sopimus (luo-sopimukselle-validointisaannot sopimus) ((i18n/tekstit) :validointi)
            (arkisto/paivita! sopimus sopimus_ja_tutkinto)
            (json-response sopimus))))))

  (GET "/:jarjestamissopimusid" [jarjestamissopimusid]
    :kayttooikeus [:sopimustiedot_luku jarjestamissopimusid]
    (json-response (jarjestamissopimus/taydenna-sopimus (arkisto/hae-ja-liita-tutkinnonosiin-asti (Integer/parseInt jarjestamissopimusid)))))

  (POST "/:jarjestamissopimusid/tutkinnot" [jarjestamissopimusid sopimus_ja_tutkinto]
    :kayttooikeus [:sopimustiedot_paivitys jarjestamissopimusid]
    (let [jarjestamissopimusid_int (Integer/parseInt jarjestamissopimusid)]
      (sallittu-jos (salli-sopimuksen-paivitys? jarjestamissopimusid_int)
        (arkisto/paivita-tutkinnot! jarjestamissopimusid_int sopimus_ja_tutkinto)
        {:status 200})))

  (DELETE "/:jarjestamissopimusid" [jarjestamissopimusid]
    :kayttooikeus [:sopimustiedot_paivitys jarjestamissopimusid]
    (let [jarjestamissopimus_id_int (Integer/parseInt jarjestamissopimusid)]
      (sallittu-jos
        (salli-sopimuksen-paivitys? jarjestamissopimus_id_int)
        (arkisto/merkitse-sopimus-poistetuksi! jarjestamissopimus_id_int)
        {:status 200})))

  (POST "/:jarjestamissopimusid/suunnitelma/:sopimus_ja_tutkinto" [jarjestamissopimusid sopimus_ja_tutkinto file]
    :kayttooikeus [:sopimustiedot_paivitys jarjestamissopimusid]
    (sallittu-jos (sallittu-tiedostotyyppi? (:content-type file))
      (jos-lapaisee-virustarkistuksen file
        (let [jarjestamissopimusid_int (Integer/parseInt jarjestamissopimusid)
              sopimus_ja_tutkinto_id_int (Integer/parseInt sopimus_ja_tutkinto)
              jarjestamissopimusid_sopimus_ja_tutkinto (arkisto/hae-jarjestamissopimusid-sopimuksen-tutkinnolle sopimus_ja_tutkinto_id_int)
              _ (tarkasta_surrogaattiavaimen_vastaavuus_entiteetiin jarjestamissopimusid_sopimus_ja_tutkinto jarjestamissopimusid_int)]

          (sallittu-jos (salli-sopimuksen-paivitys? jarjestamissopimusid_int)
            (file-upload-response (arkisto/lisaa-suunnitelma-tutkinnolle! sopimus_ja_tutkinto_id_int file)))))))

  (DELETE "/:jarjestamissopimusid/suunnitelma/:jarjestamissuunnitelma_id" [jarjestamissopimusid jarjestamissuunnitelma_id]
    :kayttooikeus [:sopimustiedot_paivitys jarjestamissopimusid]
    (let [jarjestamissopimusid_int (Integer/parseInt jarjestamissopimusid)
          jarjestamissuunnitelma_id_int (Integer/parseInt jarjestamissuunnitelma_id)
          jarjestamissopimusid_jarjestamissuunnitelma (arkisto/hae-jarjestamissopimusid-jarjestamissuunnitelmalle jarjestamissuunnitelma_id_int)
          _ (tarkasta_surrogaattiavaimen_vastaavuus_entiteetiin jarjestamissopimusid_jarjestamissuunnitelma jarjestamissopimusid_int)]
      (sallittu-jos
        (salli-sopimuksen-paivitys? jarjestamissopimusid_int)
        (arkisto/poista-suunnitelma! jarjestamissuunnitelma_id_int)
        {:status 200})))

  (POST "/:jarjestamissopimusid/liite/:sopimus_ja_tutkinto" [jarjestamissopimusid sopimus_ja_tutkinto file]
    :kayttooikeus [:sopimustiedot_paivitys jarjestamissopimusid]
    (sallittu-jos (sallittu-tiedostotyyppi? (:content-type file))
      (jos-lapaisee-virustarkistuksen file
        (let [jarjestamissopimusid_int (Integer/parseInt jarjestamissopimusid)
              sopimus_ja_tutkinto_id_int (Integer/parseInt sopimus_ja_tutkinto)
              jarjestamissopimusid_sopimus_ja_tutkinto (arkisto/hae-jarjestamissopimusid-sopimuksen-tutkinnolle sopimus_ja_tutkinto_id_int)
              _ (tarkasta_surrogaattiavaimen_vastaavuus_entiteetiin jarjestamissopimusid_sopimus_ja_tutkinto jarjestamissopimusid_int)]

          (sallittu-jos (salli-sopimuksen-paivitys? jarjestamissopimusid_int)
            (file-upload-response (arkisto/lisaa-liite-tutkinnolle! sopimus_ja_tutkinto_id_int file)))))))

  (DELETE "/:jarjestamissopimusid/liite/:sopimuksen_liite_id" [jarjestamissopimusid sopimuksen_liite_id]
    :kayttooikeus [:sopimustiedot_paivitys jarjestamissopimusid]
    (let [jarjestamissopimusid_int (Integer/parseInt jarjestamissopimusid)
          sopimuksen_liite_id_int (Integer/parseInt sopimuksen_liite_id)
          jarjestamissopimusid_liite (arkisto/hae-jarjestamissopimusid-sopimuksen-liitteelle sopimuksen_liite_id_int)
          _ (tarkasta_surrogaattiavaimen_vastaavuus_entiteetiin jarjestamissopimusid_liite jarjestamissopimusid_int)]
      (sallittu-jos
        (salli-sopimuksen-paivitys? jarjestamissopimusid_int)
        (arkisto/poista-liite! sopimuksen_liite_id_int)
        {:status 200}))))

; Liitteiden lataukselle omat reitit. Ei csrf tarkistusta.
(defroutes liite-lataus-reitit
  (GET "/:jarjestamissopimusid/suunnitelma/:jarjestamissuunnitelma_id" [jarjestamissopimusid jarjestamissuunnitelma_id]
    :kayttooikeus [:suunnitelma_luku jarjestamissopimusid]
    (let [jarjestamissopimusid_int (Integer/parseInt jarjestamissopimusid)
          jarjestamissuunnitelma_id_int (Integer/parseInt jarjestamissuunnitelma_id)
          jarjestamissopimusid_jarjestamissuunnitelma (arkisto/hae-jarjestamissopimusid-jarjestamissuunnitelmalle jarjestamissuunnitelma_id_int)
          _ (tarkasta_surrogaattiavaimen_vastaavuus_entiteetiin jarjestamissopimusid_jarjestamissuunnitelma jarjestamissopimusid_int)
          suunnitelma (arkisto/hae-suunnitelma jarjestamissuunnitelma_id_int)
          binary-data (:jarjestamissuunnitelma suunnitelma)
          filename (:jarjestamissuunnitelma_filename suunnitelma)
          content-type (:jarjestamissuunnitelma_content_type suunnitelma)]
      (file-download-response binary-data filename content-type)))

  (GET "/:jarjestamissopimusid/liite/:sopimuksen_liite_id" [jarjestamissopimusid sopimuksen_liite_id]
    :kayttooikeus [:sopimuksen_liite_luku jarjestamissopimusid]
    (let [jarjestamissopimusid_int (Integer/parseInt jarjestamissopimusid)
          sopimuksen_liite_id_int (Integer/parseInt sopimuksen_liite_id)
          jarjestamissopimusid_liite (arkisto/hae-jarjestamissopimusid-sopimuksen-liitteelle sopimuksen_liite_id_int)
          _ (tarkasta_surrogaattiavaimen_vastaavuus_entiteetiin jarjestamissopimusid_liite jarjestamissopimusid_int)
          liite (arkisto/hae-liite sopimuksen_liite_id_int)
          binary-data (:sopimuksen_liite liite)
          filename (:sopimuksen_liite_filename liite)
          content-type (:sopimuksen_liite_content_type liite)]
      (file-download-response binary-data filename content-type))))
