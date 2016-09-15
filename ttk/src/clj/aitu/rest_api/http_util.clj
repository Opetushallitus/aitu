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
  (:require [aitu.integraatio.clamav :as clamav])
  (:import java.io.ByteArrayInputStream))

(def excel-mimetype "application/vnd.ms-excel")

(def allowed-mimetypes
  "Sallitut tiedostotyypit liitetiedostoille"
  #{"application/pdf"
    "image/gif" "image/jpeg" "image/png"
    "text/plain" "text/rtf"
    excel-mimetype "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
    "application/msword"
    })

(defn sallittu-tiedostotyyppi? [tyyppi]
  (contains? allowed-mimetypes tyyppi))

(defn pdf-response
  ([data]
    {:status 200
     :body data
     :headers {"Content-Type" "application/pdf"}})
  ([data filename]
    (assoc-in (pdf-response data) [:headers "Content-Disposition"] (str "attachment; filename=\"" filename "\""))))

(defn tarkasta_surrogaattiavaimen_vastaavuus_entiteetiin [surrogaattiavaimeen_liittyva_entity_id entity_id]
  (if (= surrogaattiavaimeen_liittyva_entity_id entity_id)
    true
    (throw (Exception. "Surrogaattiavain ei liity entiteettiin jota yritettiin päivittää."))))

(defmacro jos-lapaisee-virustarkistuksen [tiedosto & body]
  `(case (clamav/tarkista-tiedosto (:filename ~tiedosto) (:tempfile ~tiedosto))
     :passed (do ~@body)
     :failed {:status 200, :body {:result :failed}}
     :error {:status 200, :body {:result :error}}))
