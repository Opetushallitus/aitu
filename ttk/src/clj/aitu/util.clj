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

(ns aitu.util
  "Yleisiä apufunktioita."
  (:require [cheshire.core :as cheshire]
            [clj-time.core :as time]
            [clj-time.format :as time-format]
            [clojure.string :as string]
            [org.httpkit.client :as http]
            [clojure.set :refer [union]]
            [clojure.walk :refer [keywordize-keys postwalk]]
            [clojure.tools.logging :as log]
            [schema.core :as s]
            [clojure-csv.core :refer [write-csv]]))

(defn schema? [x]
  (instance? schema.core.Schema x))

(defn kaikki-optional [schema]
  (into {} (for [[k v] schema
                 :let [k (s/optional-key k)]]
             (cond
               (schema? v) [k v]
               (map? v) [k (kaikki-optional v)]
               :else [k v]))))

(defn keyword-vertailu
  "vertaa avaimia halutun järjestys-taulukon mukaisesti, tuntemattomat avaimet loppuun"
  [jarjestys a b]
  (compare [(.indexOf (reverse jarjestys) b) b]
           [(.indexOf (reverse jarjestys) a) a]))

(defn jarjesta-avaimet
  "Järjestää mapin taulukkona annetun järjestyksen mukaiseksi"
  [m jarjestys]
  (into (sorted-map-by #(keyword-vertailu jarjestys %1 %2)) m))

(defn otsikot-ja-sarakkeet-jarjestykseen [m kenttien-jarjestys]
  (into [(for [[k _] (jarjesta-avaimet (first m) kenttien-jarjestys)] k)]
        (for [rivi m]
          (for [[_ v] (jarjesta-avaimet rivi kenttien-jarjestys)]
            (str v)))))

(def sarakkeiden-otsikot {:aidinkieli "Äidinkieli"
                          :alkupvm "Alkupäivämäärä"
                          :diaarinumero "Diaarinumero"
                          :edustus "Edustus"
                          :etunimi "Etunimi"
                          :jarjesto_nimi_fi "Järjestö suomeksi"
                          :jarjesto_nimi_sv "Järjestö ruotsiksi"
                          :jasenyys_alku "Jäsenyyden alku"
                          :jasenyys_loppu "Jäsenyyden loppu"
                          :kieli "Kieli"
                          :kielisyys "Kielisyys"
                          :koulutustoimija_fi "Koulutustoimija suomeksi"
                          :koulutustoimija_sv "Koulutustoimija ruotsiksi"
                          :loppupvm "Loppupäivämäärä"
                          :nimi "Nimi"
                          :nimi_fi "Nimi suomeksi"
                          :nimi_sv "Nimi ruotsiksi"
                          :opintoala_fi "Opintoala suomeksi"
                          :opintoala_sv "Opintoala ruotsiksi"
                          :oppilaitos "Oppilaitos"
                          :oppilaitoskoodi "Oppilaitoskoodi"
                          :organisaatio "Organisaatio"
                          :osaamisalat "Osaamisalat"
                          :osoite "Osoite"
                          :peruste "Tutkinnon peruste"
                          :postinumero "Postinumero"
                          :postitoimipaikka "Postitoimipaikka"
                          :puhelin "Puhelinnumero"
                          :rooli "Rooli"
                          :sahkoposti "Sähköposti"
                          :siirtymaajan_loppupvm "Siirtymäajan loppupäivämäärä"
                          :sopimusnumero "Sopimusnumero"
                          :sopimusten_maara "Sopimusten määrä"
                          :sukunimi "Sukunimi"
                          :tilikoodi "Tilikoodi"
                          :toimikunta_fi "Toimikunta suomeksi"
                          :toimikunta_sv "Toimikunta ruotsiksi"
                          :tutkinto_fi "Tutkinto suomeksi"
                          :tutkinto_sv "Tutkinto ruotsiksi"
                          :tutkintotunnus "Tutkintotunnus"
                          :vastuuhenkilo "Vastuuhenkilö"
                          :vastuuhenkilo_sahkoposti "Vastuuhenkilön sähköposti"
                          :vastuuhenkilo_puhelin "Vastuuhenkilön puhelinnumero"
                          :voimassa "Voimassa"
                          :ytunnus "Y-tunnus"
                          :lukumaara "Lukumäärä"
                          :tutkintotaso "Tutkintotaso"
                          :opintoala "Opintoala"
                          :opintoalatunnus "Opintoalatunnus"
                          :koulutustoimija "Koulutustoimija"
                          :toimikausi_alku "Toimikausi alkupvm"
                          :toimikausi_loppu "Toimikausi loppupvm"
                          :toimikunta "Toimikunta"})

(def type-mapping
  "Arvojen konversio CSV-exporttia varten käyttäjäystävällisemmäksi"
  {false "Ei"
   true "Kyllä"})

(defn convert-values
  ([m value-map] (clojure.walk/postwalk #(get value-map % %) m))
  ([m] (convert-values m type-mapping))) 

(defn csv-rivi-soluiksi [csv-rivi]
  (string/split csv-rivi #"\;(?=([^\"]*\"[^\"]*\")*(?![^\"]*\"))"))

(defn pakota-numerokentat-csv-stringsoluiksi [csvdata]
  (str
    (string/join "\n"
                (for [rivi (string/split-lines csvdata)]
                  (string/join ";"
                               (for [solu (csv-rivi-soluiksi rivi)]
                                 (if (re-matches #"[0-9]+" solu)
                                   (str "=\"" solu "\"")
                                   solu)))))
    "\n"))

(defn muodosta-csv
  ([data kenttien-jarjestys sarakkeiden-otsikot]
   (pakota-numerokentat-csv-stringsoluiksi
     (write-csv (let [[otsikko-avaimet & arvot] (otsikot-ja-sarakkeet-jarjestykseen data kenttien-jarjestys)]
                  (into [(for [oa otsikko-avaimet]
                           (or (sarakkeiden-otsikot oa)
                               (do
                                 (log/error (str "CSV-tiedoston sarakkeen otsikkoa ei löytynyt avaimella "
                                                 "'" (name oa) "'"))
                                 (name oa))))]
                        arvot))
                :delimiter \;)))
  ([data kenttien-jarjestys]
    (muodosta-csv data kenttien-jarjestys sarakkeiden-otsikot)))
