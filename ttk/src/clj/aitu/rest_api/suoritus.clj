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

(ns aitu.rest-api.suoritus
  (:import org.apache.commons.io.FileUtils
           (org.joda.time DateTimeZone))
  (:require [clojure.tools.logging :as log]
            [clojure.java.io :as io]
            [clj-time.format :refer [unparse formatter]]
            [clj-time.core :refer [now]]
            [compojure.api.core :refer [DELETE GET POST defroutes]]
            [aitu.infra.suoritus-arkisto :as arkisto]
            aitu.compojure-util
            [aitu.rest-api.http-util :refer [excel-mimetypes jos-lapaisee-virustarkistuksen pdf-response]]
            [aitu.infra.suoritus-excel :refer [lue-excel! luo-excel]]
            [aitu.infra.pdf-arkisto :as pdf-arkisto]
            [aitu.infra.i18n :as i18n]
            [aitu.util :refer [muodosta-csv lisaa-puuttuvat-avaimet]]
            [oph.common.util.http-util :refer [response-or-404 file-upload-response file-download-response sallittu-jos csv-download-response]]
            [dk.ative.docjure.spreadsheet :refer [load-workbook save-workbook-into-stream!]]
            [stencil.core :as stencil]))

(defroutes reitit-lataus
  (GET "/excel-luonti" [kieli]
    :kayttooikeus :arviointipaatos
    (let [wb (luo-excel kieli)
          bos (java.io.ByteArrayOutputStream.)
          _ (save-workbook-into-stream! bos wb)
          content-type "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
          filename "excel-suoritustiedot-template.xlsx"]
      (file-download-response (.toByteArray bos) filename content-type))))

(defn luontiaika []
  (unparse (formatter "dd.MM.yyyy 'klo' HH:mm" (DateTimeZone/forID "Europe/Helsinki")) (now)))

(defn localdate->str [locald]
  (unparse (formatter "dd.MM.yyyy" (org.joda.time.DateTimeZone/forID "Europe/Helsinki")) (clj-time.coerce/to-date-time locald)))

(defn lokalisoi-arvosana [s]
  ;; TODO: Tämä ei saa jostain syystä *locale*:a, jolloin (i18n/tekstit) ei toimi.
;  (if (= "hyvaksytty" s) (get-in (i18n/tekstit) [:arviointipaatokset :arvosana_hyvaksytty] s) s)
  (if (= "hyvaksytty" s) "Hyväksytty" s))

(defn map-update
  "Update key if the form is a map and key is mapped to non-nil value."
  [form key update-fn]
  (if (and (map? form) (not (nil? (get form key))))
    (update form key update-fn)
    form))

(defn koulutustoimija->toupper [form]
  (map-update form :koulutustoimija_nimi_fi clojure.string/upper-case))

(defn paivita-syntymapvm->str [form]
  (map-update form :suorittaja_syntymapvm localdate->str))  ;; TODO: Miksi vain :suorittaja_syntymapvm muutetaan, eikä muihin dateihin kosketa? Testeissä kuitenkin niin tehdään!

(defn paivita-arvosana [form]
  (map-update form :arvosana lokalisoi-arvosana))

(defn paivita-raportti [yhteenveto-raportti]
  (let [walk-fn (comp paivita-syntymapvm->str koulutustoimija->toupper paivita-arvosana)]
    (clojure.walk/postwalk walk-fn yhteenveto-raportti)))

(defroutes raportti-reitit
  (GET "/suoritusraportti" params
    :kayttooikeus :arviointipaatos
    (let [footer-string (slurp (io/resource "pdf-sisalto/mustache/suoritusraportti-footer.mustache") :encoding "UTF-8")
          data-string   (slurp (io/resource "pdf-sisalto/mustache/suoritusraportti.mustache") :encoding "UTF-8")
          data {:teksti (stencil/render-string data-string {:toimikunnat (paivita-raportti (arkisto/hae-yhteenveto-raportti params))
                                                            :raportti_luotu (luontiaika)})
                :footer (stencil/render-string footer-string {:raportti_luotu (luontiaika)})}
          pdf (binding [pdf-arkisto/*sisennys* 64.0]
                (pdf-arkisto/muodosta-pdf data))]
      (pdf-response pdf "suoritukset.pdf"))))

(defn muokkaus-sallittu? [suorituskertaid]
  (let [suorituskerta-id (arkisto/->int suorituskertaid)]
    (if (not (nil? suorituskerta-id))
      (let [suorituskerta (arkisto/hae suorituskerta-id)]
        (= "luonnos" (:tila suorituskerta)))
      true)))

(defn muokkaus-sallittu-kaikille? [suoritukset]
  (let [suoritustiedot (arkisto/hae-tiedot-monta suoritukset)]
    (not (some #(not (= "luonnos" (:tila %))) suoritustiedot))))

(defroutes reitit
  (GET "/" [& ehdot]
    :kayttooikeus :arviointipaatos
    (response-or-404 (arkisto/hae-kaikki ehdot)))
  (GET "/:suorituskerta-id" [suorituskerta-id]
    :kayttooikeus :arviointipaatos
    (response-or-404 (arkisto/hae-tiedot suorituskerta-id)))
  (DELETE "/:suorituskerta-id" [suorituskerta-id]
    :kayttooikeus :arviointipaatos
    (if (muokkaus-sallittu? suorituskerta-id)
      (response-or-404 (arkisto/poista! (arkisto/->int suorituskerta-id)))
      {:status 403}))
  (POST "/" [& suoritus]
    :kayttooikeus :arviointipaatos
    (if (muokkaus-sallittu? (:suorituskerta_id suoritus))
      (response-or-404 (arkisto/lisaa-tai-paivita! suoritus))
      {:status 403}))
  (POST "/laheta" [suoritukset]
    :kayttooikeus :arviointipaatos
    (response-or-404 (arkisto/laheta! suoritukset)))
  (POST "/hyvaksy" [& suoritukset]
    :kayttooikeus :arviointipaatos
    (if (muokkaus-sallittu-kaikille? (:suoritukset suoritukset))
      (response-or-404 (arkisto/hyvaksy! suoritukset))
      {:status 403}))
  (POST "/palauta" [suoritukset]
    :kayttooikeus :arviointipaatos
    (response-or-404 (arkisto/palauta! suoritukset)))

  (POST "/excel-lataus" [file]
    :kayttooikeus :arviointipaatos
    (log/info "Luetaan excel " (:filename file) " .. " (:content-type file))

    (sallittu-jos (contains? excel-mimetypes (:content-type file))
      ;    (jos-lapaisee-virustarkistuksen file ; TODO: jumittuuko testi tähän? Onko socketin timeout asetettu? Javassa ääretön defaulttina
      (let [b (FileUtils/readFileToByteArray (:tempfile file))
            wb (load-workbook (new java.io.ByteArrayInputStream b))
            respo (lue-excel! wb)]
        (file-upload-response respo)))))
