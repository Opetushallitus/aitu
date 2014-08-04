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

(ns oph.common.util.poikkeus 
  "Funktioita poikkeusten käsittelyyn."
  (:import (java.io StringWriter
                    PrintWriter))
  (:require [clojure.tools.logging :as log]))

(defn throwable->string [t]
  (let [string-writer (StringWriter.)]
    (.printStackTrace t (PrintWriter. string-writer))
    (.toString string-writer)))

(defn throwable->simplestring [t]
  (let [msg (.getMessage t)
        type (.toString (.getClass t))]
    (str "msg: " msg "\n" "type: " type)))

(defn wrap-poikkeusten-logitus
  [handler]
  (fn [request]
    (try
      (handler request)
      (catch Throwable t
        (log/error t (.getMessage t))
        {:status 500
         :headers {"Content-Type" "text/plain; charset=utf-8"}
         :body (throwable->simplestring t)}))))
