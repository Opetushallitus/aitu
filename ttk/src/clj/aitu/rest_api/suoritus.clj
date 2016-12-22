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
  (str "Raportti luotu " (unparse (formatter "dd.MM.yyyy 'klo' HH:mm" (DateTimeZone/forID "Europe/Helsinki")) (now))))

(defroutes raportti-reitit
  (GET "/suoritusraportti" params
    :kayttooikeus :raportti
    (let [data {:teksti (stencil/render-string (slurp (io/resource "pdf-sisalto/mustache/suoritusraportti.mustache"))
                                               {:toimikunnat    (arkisto/hae-yhteenveto-raportti params)
                                                :raportti_luotu (luontiaika)})
                :footer ""}
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

    ;    (sallittu-jos (contains? excel-mimetypes (:content-type file))
    ;    (jos-lapaisee-virustarkistuksen file ; TODO: jumittuuko testi tähän? Onko socketin timeout asetettu? Javassa ääretön defaulttina
    (let [b (FileUtils/readFileToByteArray (:tempfile file))
          wb (load-workbook (new java.io.ByteArrayInputStream b))
          respo (lue-excel! wb)]
      (file-upload-response respo))))
