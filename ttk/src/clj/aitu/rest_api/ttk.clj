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

(ns aitu.rest-api.ttk
  (:require [compojure.core :refer [GET PUT POST defroutes]]
            schema.core
            [aitu.infra.ttk-arkisto :as arkisto]
            [aitu.infra.tutkinto-arkisto :as tutkinto-arkisto]
            [aitu.infra.paatos-arkisto :as paatos-arkisto]
            [aitu.rest-api.http-util :refer [csv-download-response pdf-response]]
            [valip.predicates :refer [present?]]
            [aitu.infra.i18n :as i18n]
            [aitu.infra.validaatio :as val]
            [aitu.toimiala.toimikunta :as toimikunta]
            [aitu.toimiala.skeema :refer :all]
            [aitu.asetukset :refer [asetukset]]
            [oph.common.util.http-util :refer [validoi validoi-entity-saannoilla
                     luo-validoinnin-virhevastaus cachable-json-response
                     json-response parse-iso-date sallittu-jos cachable-json-response]]
            [aitu.compojure-util :as cu]
            [compojure.api.sweet :refer :all]
            [aitu.util :refer [muodosta-csv ->vector]]
            [clojure.string :as s]))

(defn salli-toimikunnan-paivitys? [diaarinumero]
  (some->
    (toimikunta/taydenna-toimikunta (arkisto/hae diaarinumero))
    :vanhentunut
    not))

(defn toimikunnan-jasenen-validaatiosaannot
  [tutkintotoimikunta jasen entiset-jasenyydet]
  [[:rooli present? :pakollinen]
   [:edustus present? :pakollinen]
   [:toimikunta present? :pakollinen]
   [:alkupvm #(not (nil? %)) :pakollinen]
   [:loppupvm #(not (nil? %)) :pakollinen]
   [:alkupvm (val/validoi-alkupvm-sama-tai-ennen-loppupvm (:loppupvm jasen)) :virheellinen-jasenyyden-voimassaolo]
   [:alkupvm (val/validoi-pvm-valissa
               (:toimikausi_alku tutkintotoimikunta)
               (:toimikausi_loppu tutkintotoimikunta)) :virheellinen-voimassaolon-alkupvm]
   [:loppupvm (val/validoi-pvm-valissa
               (:toimikausi_alku tutkintotoimikunta)
               (:toimikausi_loppu tutkintotoimikunta)) :virheellinen-voimassaolon-loppupvm]
   [:alkupvm (val/validoi-ei-sisalla-aikavaleja entiset-jasenyydet (:loppupvm jasen)) :virheellinen-jasenyyden-voimassaolo]])


(defn toimikunnan-jasenyys [toimikunta jasen]
  (let [alkupvm (:alkupvm jasen)
        loppupvm (:loppupvm jasen)]
    {:toimikunta (:tkunta toimikunta)
     :jasenyys_id (:jasenyys_id jasen)
     :henkiloid (:henkiloid jasen)
     :poistettu (:poistettu jasen)
     :alkupvm (when alkupvm (parse-iso-date alkupvm))
     :loppupvm (when loppupvm (parse-iso-date loppupvm))
     :rooli (:rooli jasen)
     :edustus (:edustus jasen)}))

(defn validoi-paivitettavat-jasenyydet [toimikunta jasenyydet]
  (apply merge-with concat
    (for [jasenyys jasenyydet
          :let [muut-henkilon-jasenyydet (filter
                                           #(and (= (:henkiloid %) (:henkiloid jasenyys))
                                                 (not (= (:jasenyys_id %) (:jasenyys_id jasenyys))))
                                           jasenyydet)
                virheet (validoi-entity-saannoilla
                          jasenyys
                          (toimikunnan-jasenen-validaatiosaannot
                            toimikunta
                            jasenyys
                            muut-henkilon-jasenyydet))]]
      virheet)))

(defn paivita-jasenyydet [diaarinumero jasenyydet]
  (doseq [jasenyys jasenyydet]
    (arkisto/paivita-tai-poista-jasenyys! diaarinumero jasenyys))
  {:status 200})

(def toimikuntakenttien-jarjestys [:nimi_fi :nimi_sv :diaarinumero :tilikoodi :voimassa :kielisyys :sahkoposti])

(def toimialakenttien-jarjestys [:opintoala_fi :opintoala_sv :tutkinto_fi :tutkinto_sv])

(def sopimuskenttien-jarjestys
  [:sopimusnumero :tutkinto_nimi_fi :tutkinto_nimi_sv :peruste :koulutustoimija_nimi_fi :koulutustoimija_nimi_sv :alkupvm :loppupvm])

(def jasenkenttien-jarjestys
  [:sukunimi :etunimi :rooli :edustus :jarjesto_nimi_fi :jarjesto_nimi_sv :kielisyys :sahkoposti :puhelin :organisaatio :osoite :postinumero :postitoimipaikka])

(def jasenraporttikenttien-jarjestys
  [:sukunimi :etunimi :toimikunta :tilikoodi :rooli :edustus :aidinkieli :jarjesto
   :sahkoposti :puhelin :organisaatio :osoite :postinumero :postitoimipaikka])

(def raporttikenttien-jarjestys [:diaarinumero :toimikunta_fi :toimikunta_sv :tilikoodi :kielisyys
                                 :opintoalatunnus :opintoala_fi :opintoala_sv :tutkintotunnus :tutkinto_fi :tutkinto_sv])

(defroutes paatos-reitit
  (GET "/paatospohja-oletukset" []
    (cu/autorisoitu-transaktio :paatos nil
      (json-response (:paatospohja-oletukset @asetukset))))
  (POST ["/:diaarinumero/asettamispaatos" :diaarinumero #"[0-9/]+"] [diaarinumero kieli paivays esittelijan_asema esittelija hyvaksyjan_asema hyvaksyja jakelu tiedoksi jarjestaytyminen lataa]
    (cu/autorisoitu-transaktio :paatos nil
      (let [data {:paivays paivays
                  :esittelija {:asema (s/split-lines esittelijan_asema)
                               :nimi esittelija}
                  :hyvaksyja {:asema (s/split-lines hyvaksyjan_asema)
                              :nimi hyvaksyja}
                  :jakelu (s/split-lines jakelu)
                  :tiedoksi (s/split-lines tiedoksi)
                  :jarjestaytyminen jarjestaytyminen}
            pdf (paatos-arkisto/luo-asettamispaatos (keyword kieli) diaarinumero data)]
        (if lataa
          (pdf-response pdf (str "asettamispaatos_" (s/replace diaarinumero \/ \_) ".pdf"))
          (pdf-response pdf)))))
  (POST ["/:diaarinumero/taydennyspaatos" :diaarinumero #"[0-9/]+"] [diaarinumero kieli jasenet paivays esittelijan_asema esittelija hyvaksyjan_asema hyvaksyja jakelu tiedoksi lataa]
    (cu/autorisoitu-transaktio :paatos nil
      (let [data {:paivays paivays
                  :esittelija {:asema (s/split-lines esittelijan_asema)
                               :nimi esittelija}
                  :hyvaksyja {:asema (s/split-lines hyvaksyjan_asema)
                              :nimi hyvaksyja}
                  :jakelu (s/split-lines jakelu)
                  :tiedoksi (s/split-lines tiedoksi)
                  :jasenet jasenet}
            pdf (paatos-arkisto/luo-taydennyspaatos (keyword kieli) diaarinumero data)]
        (if lataa
          (pdf-response pdf (str "taydennyspaatos_" (s/replace diaarinumero \/ \_) ".pdf"))
          (pdf-response pdf)))))
  (POST ["/:diaarinumero/muutospaatos" :diaarinumero #"[0-9/]+"] [diaarinumero kieli jasenet korvattu paivays esittelijan_asema esittelija hyvaksyjan_asema hyvaksyja jakelu tiedoksi lataa]
    (cu/autorisoitu-transaktio :paatos nil
      (let [data {:paivays paivays
                  :esittelija {:asema (s/split-lines esittelijan_asema)
                               :nimi esittelija}
                  :hyvaksyja {:asema (s/split-lines hyvaksyjan_asema)
                              :nimi hyvaksyja}
                  :jakelu (s/split-lines jakelu)
                  :tiedoksi (s/split-lines tiedoksi)
                  :jasenet jasenet
                  :korvattu korvattu}
            pdf (paatos-arkisto/luo-muutospaatos (keyword kieli) diaarinumero data)]
        (if lataa
          (pdf-response pdf (str "muutospaatos_" (s/replace diaarinumero \/ \_) ".pdf"))
          (pdf-response pdf))))))

(defroutes raportti-reitit
  (GET "/tilastoraportti" [toimikausi]
    (cu/autorisoitu-transaktio :raportti nil
      (csv-download-response (arkisto/hae-tilastot-toimikunnista (Integer/parseInt toimikausi))
                             "toimikunnat.csv")))
  (GET "/jasenraportti" [toimikausi rooli edustus jarjesto kieli yhteystiedot opintoala]
    (let [hakuehdot {:toimikausi (Integer/parseInt toimikausi)
                     :rooli (->vector rooli)
                     :edustus (->vector edustus)
                     :jarjesto (map #(Integer/parseInt %) (->vector jarjesto))
                     :kieli (->vector kieli)
                     :yhteystiedot (Boolean/parseBoolean yhteystiedot)
                     :opintoala (->vector opintoala)}]
      (cu/autorisoitu-transaktio :raportti nil
        (csv-download-response (muodosta-csv (arkisto/hae-jasenyydet-ehdoilla hakuehdot) jasenraporttikenttien-jarjestys)
                               "jasenet.csv"))))
  (GET "/raportti" [toimikausi kieli opintoala]
    (let [hakuehdot {:toimikausi (Integer/parseInt toimikausi)
                     :kieli (->vector kieli)
                     :opintoala (->vector opintoala)}]
      (cu/autorisoitu-transaktio :raportti nil
        (csv-download-response (muodosta-csv (arkisto/hae-toimikuntaraportti hakuehdot) raporttikenttien-jarjestys)
                               "toimikunnat.csv"))))
  (GET "/csv" req
    (cu/autorisoitu-transaktio :toimikunta_haku nil
      (csv-download-response (muodosta-csv (arkisto/hae-ehdoilla (assoc (:params req) :avaimet toimikuntakenttien-jarjestys))
                                           toimikuntakenttien-jarjestys)
                             "toimikunnat.csv")))
  (GET "/:tkunta/toimiala" [tkunta]
    (cu/autorisoitu-transaktio :toimikunta_haku nil
      (csv-download-response (muodosta-csv (tutkinto-arkisto/hae-toimikunnan-toimiala tkunta)
                                           toimialakenttien-jarjestys)
                             "toimiala.csv")))
  (GET "/:tkunta/jasenet" [tkunta]
    (cu/autorisoitu-transaktio :toimikunta_haku nil
      (csv-download-response (muodosta-csv (arkisto/hae-jasenet tkunta)
                                           jasenkenttien-jarjestys)
                             "jasenet.csv")))
  (GET "/:tkunta/aiemmat-jasenet" [tkunta]
    (cu/autorisoitu-transaktio :toimikunta_haku nil
      (csv-download-response (muodosta-csv (arkisto/hae-jasenet tkunta {:voimassa false})
                                           jasenkenttien-jarjestys)
                             "aiemmat-jasenet.csv"))))

(defroutes private-reitit
  (PUT ["/:diaarinumero/jasenet" :diaarinumero #"[0-9/]+"] [diaarinumero jasenet]
    (cu/autorisoitu-transaktio :toimikuntajasen_yllapito nil
      (sallittu-jos (salli-toimikunnan-paivitys? diaarinumero)
        (let [toimikunta (arkisto/hae diaarinumero)
              jasenyydet (map
                           (partial toimikunnan-jasenyys toimikunta)
                           jasenet)]
          (if-let [validaatioVirheet (validoi-paivitettavat-jasenyydet
                                       toimikunta
                                       (filter #(not (:poistettu %)) jasenyydet))]
            (luo-validoinnin-virhevastaus
              validaatioVirheet
              ((i18n/tekstit) :validointi))
            (paivita-jasenyydet diaarinumero jasenyydet))))))

   (PUT ["/:diaarinumero" :diaarinumero #"[0-9/]+"] [diaarinumero kielisyys toimikausi_alku toimikausi_loppu nimi_fi nimi_sv toimiala tilikoodi tkunta sahkoposti]
    (cu/autorisoitu-transaktio :toimikunta_paivitys tkunta
      (sallittu-jos (salli-toimikunnan-paivitys? diaarinumero)
        (let [paivitettava {:diaarinumero diaarinumero
                            :kielisyys kielisyys
                            :nimi_fi nimi_fi
                            :nimi_sv nimi_sv
                            :toimikausi_alku (when toimikausi_alku (parse-iso-date toimikausi_alku))
                            :toimikausi_loppu (when toimikausi_loppu (parse-iso-date toimikausi_loppu))
                            :toimiala toimiala
                            :tilikoodi tilikoodi
                            :tkunta tkunta
                            :sahkoposti sahkoposti}]
          (validoi paivitettava
                   [[:tilikoodi present? :pakollinen]
                    [:toimikausi_alku #(not (nil? %)) :pakollinen]
                    [:toimikausi_loppu #(not (nil? %)) :pakollinen]
                    [:kielisyys present? :pakollinen]]
                   ((i18n/tekstit) :validointi)
                   (arkisto/paivita! diaarinumero paivitettava)
                   {:status 200})))))

  (POST "/" [tkunta diaarinumero nimi_fi nimi_sv tilikoodi toimiala toimikausi toimikausi_alku toimikausi_loppu kielisyys sahkoposti]
    (cu/autorisoitu-transaktio :toimikunta_luonti nil
      (arkisto/lisaa! (merge {:diaarinumero diaarinumero
                              :nimi_fi nimi_fi
                              :nimi_sv nimi_sv
                              :tilikoodi tilikoodi
                              :toimiala (or toimiala "Valtakunnallinen")
                              :toimikausi_id toimikausi
                              :kielisyys kielisyys
                              :toimikausi_alku (parse-iso-date toimikausi_alku)
                              :toimikausi_loppu (parse-iso-date toimikausi_loppu)
                              :sahkoposti sahkoposti}
                             (when tkunta
                               {:tkunta tkunta})))
      {:status 200}))

  (POST ["/:diaarinumero/jasenet" :diaarinumero #"[0-9/]+"] [diaarinumero henkilo rooli alkupvm loppupvm edustus]
    (cu/autorisoitu-transaktio :toimikuntajasen_yllapito nil
      (sallittu-jos (salli-toimikunnan-paivitys? diaarinumero)
        (let [tutkintotoimikunta (arkisto/hae diaarinumero)
              jasen {:toimikunta (:tkunta tutkintotoimikunta)
                     :henkiloid (:henkiloid henkilo)
                     :rooli rooli
                     :edustus edustus
                     :alkupvm (when alkupvm (parse-iso-date alkupvm))
                     :loppupvm (when loppupvm (parse-iso-date loppupvm))}
              entiset-jasenyydet (arkisto/hae-jasenyydet (:henkiloid jasen) (:tkunta tutkintotoimikunta))]
          (validoi jasen
            (toimikunnan-jasenen-validaatiosaannot
              tutkintotoimikunta
              jasen
              entiset-jasenyydet)
            ((i18n/tekstit) :validointi)
            (arkisto/lisaa-jasen! jasen)
            {:status 200})))))
  (POST "/:tkunta/tutkinnot" [tkunta nayttotutkinto]
    (cu/autorisoitu-transaktio :toimikunta_paivitys tkunta
      (sallittu-jos (salli-toimikunnan-paivitys? (arkisto/hae-toimikunnan-diaarinumero tkunta))
        (arkisto/paivita-tutkinnot! tkunta nayttotutkinto)
        {:status 200}))))

(defroutes* reitit
  (GET* "/" [tunnus toimikausi :as req]
    :summary "Hakee toimikunnat, jotka ovat vastuussa tietystä tutkinnosta tai opintoalasta"
    :return [Toimikunta]
    (cu/autorisoitu-transaktio :toimikunta_haku nil
      (cachable-json-response req (arkisto/hae-ehdoilla {:tunnus tunnus
                                                         :toimikausi toimikausi}))))

  (GET* ["/:diaarinumero" :diaarinumero #"[0-9/]+"] [diaarinumero]
    :summary "Hakee toimikunnan diaarinumeron perusteella"
    :return ToimikuntaLaajatTiedot
    (cu/autorisoitu-transaktio :toimikunta_haku nil
      (json-response (toimikunta/taydenna-toimikunta (arkisto/hae diaarinumero)))))

  (GET* "/haku" [:as req]
    :summary "Hakee toimikunnat, joiden nimi sisältää annetun termin"
    :query [params TermiParams]
    :return [Toimikunta]
    (cu/autorisoitu-transaktio :toimikunta_haku nil
      (cachable-json-response req (arkisto/hae-termilla (:termi params)))))
  private-reitit
  paatos-reitit)
