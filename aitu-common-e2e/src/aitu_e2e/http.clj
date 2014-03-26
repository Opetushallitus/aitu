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

(ns aitu-e2e.http
  "Virheet loggaavat wrapperit clj-http:n funktioille."
  (:refer-clojure :exclude [get])
  (:require [clojure.string :refer [split-lines]]
            [clj-http.client :as hc]
            [aitu-e2e.util :as u]))

(defn ^:private wrap-poikkeusten-logitus [f]
  (fn [url & [headers]]
    (try
      (let [uid-headers (merge-with conj (or headers {}) {:headers {"uid" u/default-user}})]
        (f url uid-headers))
      (catch clojure.lang.ExceptionInfo e
        ;; Estetään muita säikeitä tulostamasta näiden viestien sekaan.
        (locking System/out
          (locking System/err
            (binding [*out* *err*]
              (println "========== Virhe palvelimella ==========")
              (doseq [line (-> e .getData :object :body split-lines)]
                (println ">" line))
              (println "========================================"))))
        (throw e)))))

(def get (wrap-poikkeusten-logitus hc/get))
(def post (wrap-poikkeusten-logitus hc/post))
(def put (wrap-poikkeusten-logitus hc/put))
(def delete (wrap-poikkeusten-logitus hc/delete))
