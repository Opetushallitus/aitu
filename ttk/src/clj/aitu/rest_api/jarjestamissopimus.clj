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
            [aitu.rest-api.http-util :refer [parse-iso-date json-response sallittu-jos tarkasta_surrogaattiavaimen_vastaavuus_entiteetiin]]
            [aitu.infra.jarjestamissopimus-arkisto :as arkisto]
            [aitu.infra.i18n :as i18n]
            [aitu.rest-api.http-util :refer :all]
            [valip.predicates :refer [present?]]
            [aitu.toimiala.skeema :as skeema]
            [aitu.compojure-util :as cu]
            [aitu.infra.validaatio :refer [validoi-alkupvm-sama-tai-ennen-loppupvm]]
            [aitu.rest-api.http-util :refer :all]
            [aitu.toimiala.jarjestamissopimus :as jarjestamissopimus]))

(defn validoi-uniikki-sopimusnumero [sopimus]
  (fn [sopimusnumero]
    (arkisto/uniikki-sopimusnumero? sopimusnumero (:jarjestamissopimusid sopimus))))

(defn luo-sopimukselle-validointisaannot [sopimus]
  [[:sopimusnumero present? :pakollinen]
   [:sopimusnumero (validoi-uniikki-sopimusnumero sopimus) :ei-uniikki]
   [:toimikunta present? :pakollinen]
   [:oppilaitos present? :pakollinen]
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

(c/defroutes reitit
  (cu/defapi :sopimus_lisays nil :post "/" [tutkintotunnus toimikunta sopijatoimikunta oppilaitos tutkintotilaisuuksista_vastaava_oppilaitos sopimusnumero alkupvm loppupvm jarjestamissopimusid vastuuhenkilo sahkoposti puhelin]
      (let [sopimus (merge {:sopimusnumero sopimusnumero
                            :toimikunta (paljas-tai-kentan-arvo toimikunta :tkunta)
                            :sopijatoimikunta (paljas-tai-kentan-arvo sopijatoimikunta :tkunta)
                            :oppilaitos (paljas-tai-kentan-arvo oppilaitos :oppilaitoskoodi)
                            :tutkintotilaisuuksista_vastaava_oppilaitos (paljas-tai-kentan-arvo tutkintotilaisuuksista_vastaava_oppilaitos :oppilaitoskoodi)
                            :alkupvm (if alkupvm (parse-iso-date alkupvm) nil)
                            :loppupvm (if loppupvm (parse-iso-date loppupvm) nil)
                            :vastuuhenkilo vastuuhenkilo
                            :puhelin puhelin
                            :sahkoposti sahkoposti}
                           (when jarjestamissopimusid {:jarjestamissopimusid jarjestamissopimusid}))]
        (validoi sopimus (luo-sopimuksen-luonnille-validointisaannot sopimus) ((i18n/tekstit) :validointi)
                 (let [uusi-sopimus (arkisto/lisaa! sopimus)]
                   (json-response uusi-sopimus)))))

  (cu/defapi :sopimustiedot_paivitys nil :put "/:jarjestamissopimusid" [jarjestamissopimusid sopimusnumero alkupvm loppupvm oppilaitos tutkintotilaisuuksista_vastaava_oppilaitos toimikunta sopimus_ja_tutkinto vastuuhenkilo sahkoposti puhelin]
    (let [jarjestamissopimus_id_int (Integer/parseInt jarjestamissopimusid)]
      (sallittu-jos (salli-sopimuksen-paivitys? jarjestamissopimus_id_int)
        (let [sopimus {:jarjestamissopimusid jarjestamissopimus_id_int
                       :sopimusnumero sopimusnumero
                       :alkupvm (when alkupvm (parse-iso-date alkupvm))
                       :loppupvm (when loppupvm (parse-iso-date loppupvm))
                       :oppilaitos  (paljas-tai-kentan-arvo oppilaitos :oppilaitoskoodi)
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

  (cu/defapi :sopimustiedot_paivitys nil :post "/:jarjestamissopimusid/tutkinnot" [jarjestamissopimusid sopimus_ja_tutkinto]
    (let [jarjestamissopimusid_int (Integer/parseInt jarjestamissopimusid)]
      (sallittu-jos (salli-sopimuksen-paivitys? jarjestamissopimusid_int)
        (arkisto/paivita-tutkinnot! jarjestamissopimusid_int sopimus_ja_tutkinto)
        {:status 200})))

  (cu/defapi :sopimustiedot_paivitys nil :post "/:jarjestamissopimusid/suunnitelma/:sopimus_ja_tutkinto" [jarjestamissopimusid sopimus_ja_tutkinto file]
    (let [jarjestamissopimusid_int (Integer/parseInt jarjestamissopimusid)
          sopimus_ja_tutkinto_id_int (Integer/parseInt sopimus_ja_tutkinto)
          jarjestamissopimusid_sopimus_ja_tutkinto (arkisto/hae-jarjestamissopimusid-sopimuksen-tutkinnolle sopimus_ja_tutkinto_id_int)
          _ (tarkasta_surrogaattiavaimen_vastaavuus_entiteetiin jarjestamissopimusid_sopimus_ja_tutkinto jarjestamissopimusid_int)]
      (sallittu-jos
        (salli-sopimuksen-paivitys? jarjestamissopimusid_int)
        (file-upload-response (arkisto/lisaa-suunnitelma-tutkinnolle! sopimus_ja_tutkinto_id_int file)))))

  (cu/defapi :sopimustiedot_paivitys nil :delete "/:jarjestamissopimusid/suunnitelma/:jarjestamissuunnitelma_id" [jarjestamissopimusid jarjestamissuunnitelma_id]
    (let [jarjestamissopimusid_int (Integer/parseInt jarjestamissopimusid)
          jarjestamissuunnitelma_id_int (Integer/parseInt jarjestamissuunnitelma_id)
          jarjestamissopimusid_jarjestamissuunnitelma (arkisto/hae-jarjestamissopimusid-jarjestamissuunnitelmalle jarjestamissuunnitelma_id_int)
          _ (tarkasta_surrogaattiavaimen_vastaavuus_entiteetiin jarjestamissopimusid_jarjestamissuunnitelma jarjestamissopimusid_int)]
      (sallittu-jos
        (salli-sopimuksen-paivitys? jarjestamissopimusid_int)
        (arkisto/poista-suunnitelma! jarjestamissuunnitelma_id_int)
        {:status 200})))

  (cu/defapi :sopimustiedot_paivitys nil :delete "/:jarjestamissopimusid" [jarjestamissopimusid]
    (let [jarjestamissopimus_id_int (Integer/parseInt jarjestamissopimusid)]
      (sallittu-jos
        (salli-sopimuksen-paivitys? jarjestamissopimus_id_int)
        (arkisto/merkitse-sopimus-poistetuksi! jarjestamissopimus_id_int)
        {:status 200})))

  (cu/defapi :suunnitelma_luku nil :get "/:jarjestamissopimusid/suunnitelma/:jarjestamissuunnitelma_id" [jarjestamissopimusid jarjestamissuunnitelma_id]
    (let [jarjestamissopimusid_int (Integer/parseInt jarjestamissopimusid)
          jarjestamissuunnitelma_id_int (Integer/parseInt jarjestamissuunnitelma_id)
          jarjestamissopimusid_jarjestamissuunnitelma (arkisto/hae-jarjestamissopimusid-jarjestamissuunnitelmalle jarjestamissuunnitelma_id_int)
          _ (tarkasta_surrogaattiavaimen_vastaavuus_entiteetiin jarjestamissopimusid_jarjestamissuunnitelma jarjestamissopimusid_int)
          suunnitelma (arkisto/hae-suunnitelma jarjestamissuunnitelma_id_int)
          binary-data (:jarjestamissuunnitelma suunnitelma)
          filename (:jarjestamissuunnitelma_filename suunnitelma)
          content-type (:jarjestamissuunnitelma_content_type suunnitelma)]
      (file-download-response binary-data filename content-type)))

  (cu/defapi :sopimustiedot_paivitys nil :post "/:jarjestamissopimusid/liite/:sopimus_ja_tutkinto" [jarjestamissopimusid sopimus_ja_tutkinto file]
    (let [jarjestamissopimusid_int (Integer/parseInt jarjestamissopimusid)
          sopimus_ja_tutkinto_id_int (Integer/parseInt sopimus_ja_tutkinto)
          jarjestamissopimusid_sopimus_ja_tutkinto (arkisto/hae-jarjestamissopimusid-sopimuksen-tutkinnolle sopimus_ja_tutkinto_id_int)
          _ (tarkasta_surrogaattiavaimen_vastaavuus_entiteetiin jarjestamissopimusid_sopimus_ja_tutkinto jarjestamissopimusid_int)]
      (sallittu-jos
        (salli-sopimuksen-paivitys? jarjestamissopimusid_int)
        (file-upload-response (arkisto/lisaa-liite-tutkinnolle! sopimus_ja_tutkinto_id_int file)))))

  (cu/defapi :sopimustiedot_paivitys nil :delete "/:jarjestamissopimusid/liite/:sopimuksen_liite_id" [jarjestamissopimusid sopimuksen_liite_id]
    (let [jarjestamissopimusid_int (Integer/parseInt jarjestamissopimusid)
          sopimuksen_liite_id_int (Integer/parseInt sopimuksen_liite_id)
          jarjestamissopimusid_liite (arkisto/hae-jarjestamissopimusid-sopimuksen-liitteelle sopimuksen_liite_id_int)
          _ (tarkasta_surrogaattiavaimen_vastaavuus_entiteetiin jarjestamissopimusid_liite jarjestamissopimusid_int)]
      (sallittu-jos
        (salli-sopimuksen-paivitys? jarjestamissopimusid_int)
        (arkisto/poista-liite! sopimuksen_liite_id_int)
        {:status 200})))

  (cu/defapi :sopimuksen_liite_luku nil :get "/:jarjestamissopimusid/liite/:sopimuksen_liite_id" [jarjestamissopimusid sopimuksen_liite_id]
    (let [jarjestamissopimusid_int (Integer/parseInt jarjestamissopimusid)
          sopimuksen_liite_id_int (Integer/parseInt sopimuksen_liite_id)
          jarjestamissopimusid_liite (arkisto/hae-jarjestamissopimusid-sopimuksen-liitteelle sopimuksen_liite_id_int)
          _ (tarkasta_surrogaattiavaimen_vastaavuus_entiteetiin jarjestamissopimusid_liite jarjestamissopimusid_int)
          liite (db/transaction (arkisto/hae-liite jarjestamissopimusid_liite))
          binary-data (:sopimuksen_liite liite)
          filename (:sopimuksen_liite_filename liite)
          content-type (:sopimuksen_liite_content_type liite)]
      (file-download-response binary-data filename content-type))))
