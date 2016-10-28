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
  (:import org.apache.commons.io.FileUtils)
  (:require [aitu.infra.suoritus-arkisto :as arkisto]
            aitu.compojure-util
            [clojure.tools.logging :as log]
            [compojure.api.core :refer [DELETE GET POST defroutes]]
            [aitu.rest-api.http-util :refer [excel-mimetypes jos-lapaisee-virustarkistuksen]]
            [aitu.infra.suoritus-excel :refer [lue-excel! luo-excel]]
            [oph.common.util.http-util :refer [response-or-404 file-upload-response file-download-response sallittu-jos]]
            [dk.ative.docjure.spreadsheet :refer [load-workbook save-workbook-into-stream!]]))

(defroutes reitit-lataus
  (GET "/excel-luonti" [kieli]
    :kayttooikeus :arviointipaatos
    (let [wb (luo-excel kieli)
          bos (java.io.ByteArrayOutputStream.)
          _ (save-workbook-into-stream! bos wb)
          content-type "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
          filename "excel-suoritustiedot-template.xlsx"]
      (file-download-response (.toByteArray bos) filename content-type))))
  
; TODO: tilan huomiointi operaatioissa - voiko hyväksyttyä päivittää? ei voi.
(defroutes reitit
  (GET "/" [& ehdot]
    :kayttooikeus :arviointipaatos
    (response-or-404 (arkisto/hae-kaikki ehdot)))
  (GET "/:suorituskerta-id" [suorituskerta-id]
       :kayttooikeus :arviointipaatos
       (response-or-404 (arkisto/hae-tiedot suorituskerta-id)))
  (DELETE "/:suorituskerta-id" [suorituskerta-id]
    :kayttooikeus :arviointipaatos
    (let [suorituskerta-id (Integer/parseInt suorituskerta-id)
          suorituskerta (arkisto/hae suorituskerta-id)]
      (if (= "luonnos" (:tila suorituskerta))
        (response-or-404 (arkisto/poista! suorituskerta-id))
        {:status 403})))
  (POST "/" [& suoritus]
    :kayttooikeus :arviointipaatos
    (response-or-404 (arkisto/lisaa-tai-paivita! suoritus)))
  (POST "/laheta" [suoritukset]
    :kayttooikeus :arviointipaatos
    (response-or-404 (arkisto/laheta! suoritukset)))
  (POST "/hyvaksy" [suoritukset]
    :kayttooikeus :arviointipaatos
    (response-or-404 (arkisto/hyvaksy! suoritukset)))

  (POST "/excel-lataus" [file]
    :kayttooikeus :arviointipaatos
    (log/info "Luetaan excel " (:filename file) " .. " (:content-type file))
    
    ;    (sallittu-jos (contains? excel-mimetypes (:content-type file))
;    (jos-lapaisee-virustarkistuksen file ; TODO: jumittuuko testi tähän? Onko socketin timeout asetettu? Javassa ääretön defaulttina
      (let [b (FileUtils/readFileToByteArray (:tempfile file))
            wb (load-workbook (new java.io.ByteArrayInputStream b))
            respo (lue-excel! wb)]
     (file-upload-response respo))))
