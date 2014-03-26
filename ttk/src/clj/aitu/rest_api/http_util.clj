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
  (:require
    [cheshire.core :as cheshire]
    [valip.core :refer [validate]]
    [clj-time.format :refer [formatter formatters parse unparse parse-local-date with-locale]]
    [clj-time.coerce :refer [from-date to-date]]
    [aitu.util :refer [uusin-muokkausaika]]
    [schema.core :as s]))

(def ^:private http-date-format
  (->
    (formatter "EEE, dd MMM yyyy HH:mm:ss 'GMT'")
    (with-locale java.util.Locale/US)))

(defn parse-http-date
  [d]
  (parse http-date-format d))

(defn format-http-date
  [d]
  (unparse http-date-format d))

(defn get-cache-date
  [req]
  (parse-http-date (get-in req [:headers "if-modified-since"] "Thu, 01 Jan 1970 00:00:00 GMT")))

(defn try-parse-local-date
  [f d]
  (try
    (parse-local-date (formatter f) d)
    (catch IllegalArgumentException e
      nil)))

(defn parse-iso-date
  [d]
  (or
    (try-parse-local-date "yyyy-MM-dd'T'HH:mm:ss.sssZ" d)
    (try-parse-local-date "yyyy-MM-dd" d)
    (try-parse-local-date "dd.MM.yyyy" d)
    (throw (IllegalArgumentException. "Virheellinen pvm formaatti"))))

(defn get-cache-headers
  [last-modified]
  {"Cache-Control" "max-age=0, must-revalidate, proxy-revalidate"
   "Last-Modified" (format-http-date last-modified)
   "Content-Type" "application/json"})

(defn cachable-json-response
  "Cheshiren avulla REST response, jossa on mukana cache- ja json-headerit"
  ([req vseq]
    (let [cache-muokattu (get-cache-date req)
          vseq-muokattu (->
                          (uusin-muokkausaika vseq [:muutettuaika])
                          (.withMillisOfSecond 0))]
      (if (> 0 (compare cache-muokattu vseq-muokattu))
        {:status 200
         :body (cheshire/generate-string vseq)
         :headers (get-cache-headers vseq-muokattu)}
        {:status 304})))
  ([req vseq schema]
    (cachable-json-response req (s/validate schema vseq))))

(defn korvaa-virhetekstit [valip-errors virhetekstit]
  (into {} (for [[kentta virheet] valip-errors]
             [kentta (map #(virhetekstit % %) virheet)])))

(defn json-response
  ([data]
    (if (nil? data)
      {:status 404}
      {:status 200
       :body data
       :headers {"Content-Type" "application/json"}}))
  ([data schema]
    (json-response (s/validate (s/maybe schema) data))))

(defn file-download-response
  [data filename content-type]
  {:status 200
   :body (new java.io.ByteArrayInputStream data)
   :headers {"Content-type" content-type
             "Content-Disposition" (str "attachment; filename=\"" filename "\"")}})

(defn file-upload-response
  [data]
  (assoc (json-response data) :headers {"Content-Type" "text/html"}))

(defn validoi-entity-saannoilla
  [entity saannot]
  (apply validate entity saannot))

(defn luo-validoinnin-virhevastaus
  [virheet virhetekstit]
  {:status 400
   :headers {"Content-Type" "application/json"}
   :body (cheshire/generate-string
           {:errors (korvaa-virhetekstit virheet virhetekstit)})})

(defn validoi*
  [m saannot virhetekstit f]
  (if-let [valip-errors (validoi-entity-saannoilla m saannot)]
    (luo-validoinnin-virhevastaus valip-errors virhetekstit)
    (f)))

(defmacro validoi
  [m saannot virhetekstit & body]
  `(validoi* ~m ~saannot ~virhetekstit (fn [] ~@body)))

(defmacro sallittu-jos [ehto & body]
  ;; 403 olisi kuvaavampi koodi, mutta sillä ilmoitetaan myös CAS-istunnon
  ;; puuttumisesta, ja käyttöliittymä lataa sivun uudelleen 403:n saatuaan.
  ;; Speksi sanoo 403:sta "If the server does not wish to make this information
  ;; available to the client, the status code 404 (Not Found) can be used
  ;; instead.", joten käytetään 404:ää.
  `(if ~ehto (do ~@body) {:status 404}))
