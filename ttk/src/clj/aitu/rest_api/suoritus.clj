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
            [clj-time.format :refer [unparse formatter]]
            [clj-time.core :refer [now]]
            [compojure.api.core :refer [DELETE GET POST defroutes]]
            [aitu.infra.suoritus-arkisto :as arkisto]
            aitu.compojure-util
            [aitu.rest-api.http-util :refer [excel-mimetypes jos-lapaisee-virustarkistuksen]]
            [aitu.infra.suoritus-excel :refer [lue-excel! luo-excel]]
            [aitu.infra.suoritus-raportti :refer [yhteenveto-raportti-excel]]
            [aitu.util :refer [muodosta-csv lisaa-puuttuvat-avaimet]]
            [oph.common.util.http-util :refer [response-or-404 file-upload-response file-download-response sallittu-jos csv-download-response]]
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

(def kenttien-jarjestys [:diaarinumero :toimikunta_fi
                         :ytunnus :koulutustoimija_fi
                         :tutkintotunnus :tutkinto_fi :peruste
                         :osatunnus :tutkinnonosa_fi
                         :suorittaja_sukunimi :suorittaja_etunimi :arvosana :kokotutkinto :todistus
                         :arvioija_sukunimi :arvioija_etunimi :arvioija_rooli])

(defn lisaa-luontiaika [csv]
  (str "Raportti luotu "
       (unparse (formatter "dd.MM.yyyy 'klo' HH:mm" (DateTimeZone/forID "Europe/Helsinki")) (now))
       \newline
       csv))

(defroutes raportti-reitit
  (GET "/suoritusraportti" params
       :kayttooikeus :raportti
    (-> (arkisto/hae-yhteenveto-raportti params)
        (yhteenveto-raportti-excel)
        (lisaa-puuttuvat-avaimet kenttien-jarjestys)
        (muodosta-csv kenttien-jarjestys)
        (lisaa-luontiaika)
        (csv-download-response "suoritukset.csv"))))

; TODO: OPH-1916 tilan huomiointi operaatioissa - voiko hyväksyttyä päivittää? ei voi.
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
  (POST "/hyvaksy" [& suoritukset]
    :kayttooikeus :arviointipaatos
    (response-or-404 (arkisto/hyvaksy! suoritukset)))
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
