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
  (:require [compojure.core :as c]
            [korma.db :as db]
            [oph.common.util.http-util :refer [file-download-response file-upload-response validoi parse-iso-date json-response sallittu-jos]]
            [aitu.rest-api.http-util :refer [sallittu-tiedostotyyppi? tarkasta_surrogaattiavaimen_vastaavuus_entiteetiin csv-download-response]]
            [aitu.util :refer [muodosta-csv]]
            [aitu.infra.jarjestamissopimus-arkisto :as arkisto]
            [aitu.infra.i18n :as i18n]
            [valip.predicates :refer [present?]]
            [aitu.toimiala.skeema :as skeema]
            [aitu.compojure-util :as cu]
            [aitu.infra.validaatio :refer [validoi-alkupvm-sama-tai-ennen-loppupvm]]
            [aitu.toimiala.jarjestamissopimus :as jarjestamissopimus]))

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
    :vanhentunut
    not))

(def rajattujen-sopimuskenttien-jarjestys
  [:sopimusnumero :toimikunta_fi :toimikunta_sv :tutkinto_fi :tutkinto_sv :peruste :koulutustoimija_fi :koulutustoimija_sv :alkupvm :loppupvm])

(def kaikkien-sopimuskenttien-jarjestys
  [:koulutustoimija_fi :koulutustoimija_sv :toimikunta_fi :toimikunta_sv :sopimusnumero :alkupvm :loppupvm :tutkinto_fi :tutkinto_sv :peruste :siirtymaajan_loppupvm
   :oppilaitos :kieli :vastuuhenkilo :vastuuhenkilo_sahkoposti :vastuuhenkilo_puhelin])

(c/defroutes raportti-reitit
  (cu/defapi :yleinen-rest-api nil :get "/csv" [voimassa :as req]
    (let [voimassa (not= voimassa "false")]
      (csv-download-response (muodosta-csv (arkisto/hae-sopimukset-csv (assoc (:params req)
                                                                              :avaimet rajattujen-sopimuskenttien-jarjestys
                                                                              :voimassa voimassa))
                                           rajattujen-sopimuskenttien-jarjestys)
                             "sopimukset.csv")))
  (cu/defapi :yleinen-rest-api nil :get "/raportti" req
    (let [raportti (sort-by (juxt :koulutustoimija_fi :toimikunta_fi) (arkisto/hae-sopimukset-csv {:voimassa true
                                                                                                   :avaimet kaikkien-sopimuskenttien-jarjestys}))]
      (csv-download-response (muodosta-csv raportti kaikkien-sopimuskenttien-jarjestys)
                             "sopimukset.csv"))))

(c/defroutes reitit
  (cu/defapi :sopimus_lisays tkunta :post "/:tkunta" [tkunta tutkintotunnus toimikunta sopijatoimikunta koulutustoimija tutkintotilaisuuksista_vastaava_oppilaitos sopimusnumero alkupvm loppupvm jarjestamissopimusid vastuuhenkilo sahkoposti puhelin voimassa]
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

  (cu/defapi :sopimustiedot_paivitys jarjestamissopimusid :put "/:jarjestamissopimusid" [jarjestamissopimusid sopimusnumero alkupvm loppupvm koulutustoimija tutkintotilaisuuksista_vastaava_oppilaitos toimikunta sopimus_ja_tutkinto vastuuhenkilo sahkoposti puhelin]
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

  (cu/defapi :sopimustiedot_luku jarjestamissopimusid :get "/:jarjestamissopimusid" [jarjestamissopimusid]
      (json-response (jarjestamissopimus/taydenna-sopimus (arkisto/hae-ja-liita-tutkinnonosiin-asti (Integer/parseInt jarjestamissopimusid)))))

  (cu/defapi :sopimustiedot_paivitys jarjestamissopimusid :post "/:jarjestamissopimusid/tutkinnot" [jarjestamissopimusid sopimus_ja_tutkinto]
    (let [jarjestamissopimusid_int (Integer/parseInt jarjestamissopimusid)]
      (sallittu-jos (salli-sopimuksen-paivitys? jarjestamissopimusid_int)
        (arkisto/paivita-tutkinnot! jarjestamissopimusid_int sopimus_ja_tutkinto)
        {:status 200})))

  (cu/defapi :sopimustiedot_paivitys jarjestamissopimusid :delete "/:jarjestamissopimusid" [jarjestamissopimusid]
    (let [jarjestamissopimus_id_int (Integer/parseInt jarjestamissopimusid)]
      (sallittu-jos
        (salli-sopimuksen-paivitys? jarjestamissopimus_id_int)
        (arkisto/merkitse-sopimus-poistetuksi! jarjestamissopimus_id_int)
        {:status 200})))

  (cu/defapi :sopimustiedot_paivitys jarjestamissopimusid :post "/:jarjestamissopimusid/suunnitelma/:sopimus_ja_tutkinto" [jarjestamissopimusid sopimus_ja_tutkinto file]
    (sallittu-jos (sallittu-tiedostotyyppi? (:content-type file))

      (let [jarjestamissopimusid_int (Integer/parseInt jarjestamissopimusid)
            sopimus_ja_tutkinto_id_int (Integer/parseInt sopimus_ja_tutkinto)
            jarjestamissopimusid_sopimus_ja_tutkinto (arkisto/hae-jarjestamissopimusid-sopimuksen-tutkinnolle sopimus_ja_tutkinto_id_int)
            _ (tarkasta_surrogaattiavaimen_vastaavuus_entiteetiin jarjestamissopimusid_sopimus_ja_tutkinto jarjestamissopimusid_int)]

        (sallittu-jos (salli-sopimuksen-paivitys? jarjestamissopimusid_int)
          (file-upload-response (arkisto/lisaa-suunnitelma-tutkinnolle! sopimus_ja_tutkinto_id_int file))))))

  (cu/defapi :sopimustiedot_paivitys jarjestamissopimusid :delete "/:jarjestamissopimusid/suunnitelma/:jarjestamissuunnitelma_id" [jarjestamissopimusid jarjestamissuunnitelma_id]
    (let [jarjestamissopimusid_int (Integer/parseInt jarjestamissopimusid)
          jarjestamissuunnitelma_id_int (Integer/parseInt jarjestamissuunnitelma_id)
          jarjestamissopimusid_jarjestamissuunnitelma (arkisto/hae-jarjestamissopimusid-jarjestamissuunnitelmalle jarjestamissuunnitelma_id_int)
          _ (tarkasta_surrogaattiavaimen_vastaavuus_entiteetiin jarjestamissopimusid_jarjestamissuunnitelma jarjestamissopimusid_int)]
      (sallittu-jos
        (salli-sopimuksen-paivitys? jarjestamissopimusid_int)
        (arkisto/poista-suunnitelma! jarjestamissuunnitelma_id_int)
        {:status 200})))

  (cu/defapi :sopimustiedot_paivitys jarjestamissopimusid :post "/:jarjestamissopimusid/liite/:sopimus_ja_tutkinto" [jarjestamissopimusid sopimus_ja_tutkinto file]
    (sallittu-jos (sallittu-tiedostotyyppi? (:content-type file))

      (let [jarjestamissopimusid_int (Integer/parseInt jarjestamissopimusid)
        sopimus_ja_tutkinto_id_int (Integer/parseInt sopimus_ja_tutkinto)
        jarjestamissopimusid_sopimus_ja_tutkinto (arkisto/hae-jarjestamissopimusid-sopimuksen-tutkinnolle sopimus_ja_tutkinto_id_int)
        _ (tarkasta_surrogaattiavaimen_vastaavuus_entiteetiin jarjestamissopimusid_sopimus_ja_tutkinto jarjestamissopimusid_int)]

        (sallittu-jos (salli-sopimuksen-paivitys? jarjestamissopimusid_int)
          (file-upload-response (arkisto/lisaa-liite-tutkinnolle! sopimus_ja_tutkinto_id_int file))))))

  (cu/defapi :sopimustiedot_paivitys jarjestamissopimusid :delete "/:jarjestamissopimusid/liite/:sopimuksen_liite_id" [jarjestamissopimusid sopimuksen_liite_id]
    (let [jarjestamissopimusid_int (Integer/parseInt jarjestamissopimusid)
          sopimuksen_liite_id_int (Integer/parseInt sopimuksen_liite_id)
          jarjestamissopimusid_liite (arkisto/hae-jarjestamissopimusid-sopimuksen-liitteelle sopimuksen_liite_id_int)
          _ (tarkasta_surrogaattiavaimen_vastaavuus_entiteetiin jarjestamissopimusid_liite jarjestamissopimusid_int)]
      (sallittu-jos
        (salli-sopimuksen-paivitys? jarjestamissopimusid_int)
        (arkisto/poista-liite! sopimuksen_liite_id_int)
        {:status 200}))))

; Liitteiden lataukselle omat reitit. Ei csrf tarkistusta.
(c/defroutes liite-lataus-reitit
  (cu/defapi :suunnitelma_luku jarjestamissopimusid :get "/:jarjestamissopimusid/suunnitelma/:jarjestamissuunnitelma_id" [jarjestamissopimusid jarjestamissuunnitelma_id]
    (let [jarjestamissopimusid_int (Integer/parseInt jarjestamissopimusid)
          jarjestamissuunnitelma_id_int (Integer/parseInt jarjestamissuunnitelma_id)
          jarjestamissopimusid_jarjestamissuunnitelma (arkisto/hae-jarjestamissopimusid-jarjestamissuunnitelmalle jarjestamissuunnitelma_id_int)
          _ (tarkasta_surrogaattiavaimen_vastaavuus_entiteetiin jarjestamissopimusid_jarjestamissuunnitelma jarjestamissopimusid_int)
          suunnitelma (arkisto/hae-suunnitelma jarjestamissuunnitelma_id_int)
          binary-data (:jarjestamissuunnitelma suunnitelma)
          filename (:jarjestamissuunnitelma_filename suunnitelma)
          content-type (:jarjestamissuunnitelma_content_type suunnitelma)]
      (file-download-response binary-data filename content-type)))

  (cu/defapi :sopimuksen_liite_luku jarjestamissopimusid :get "/:jarjestamissopimusid/liite/:sopimuksen_liite_id" [jarjestamissopimusid sopimuksen_liite_id]
    (let [jarjestamissopimusid_int (Integer/parseInt jarjestamissopimusid)
          sopimuksen_liite_id_int (Integer/parseInt sopimuksen_liite_id)
          jarjestamissopimusid_liite (arkisto/hae-jarjestamissopimusid-sopimuksen-liitteelle sopimuksen_liite_id_int)
          _ (tarkasta_surrogaattiavaimen_vastaavuus_entiteetiin jarjestamissopimusid_liite jarjestamissopimusid_int)
          liite (arkisto/hae-liite sopimuksen_liite_id_int)
          binary-data (:sopimuksen_liite liite)
          filename (:sopimuksen_liite_filename liite)
          content-type (:sopimuksen_liite_content_type liite)]
      (file-download-response binary-data filename content-type))))
