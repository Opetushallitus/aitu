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

(ns aitu.rest-api.http-util
  (:import java.io.ByteArrayInputStream))

(def allowed-mimetypes
  "Sallitut tiedostotyypit liitetiedostoille"
  #{"application/pdf"
    "image/gif" "image/jpeg" "image/png"
    "text/plain" "text/rtf"
    "application/vnd.ms-excel" "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
    "application/msword"
    })

(defn sallittu-tiedostotyyppi? [tyyppi]
  (contains? allowed-mimetypes tyyppi))

(defn textfile-download-response
  ([data filename content-type]
    (textfile-download-response data filename content-type {}))
  ([data filename content-type options]
    {:status 200
     :body (if-let [charset (:charset options)]
             (ByteArrayInputStream. (.getBytes (str data) charset))
             (str data))
     :headers {"Content-Type" content-type
               "Content-Disposition" (str "attachment; filename=\"" filename "\"")}}))

(defn tarkasta_surrogaattiavaimen_vastaavuus_entiteetiin [surrogaattiavaimeen_liittyva_entity_id entity_id]
  (if (= surrogaattiavaimeen_liittyva_entity_id entity_id)
    true
    (throw (Exception. "Surrogaattiavain ei liity entiteettiin jota yritettiin päivittää."))))
