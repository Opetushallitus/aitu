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

(ns oph.rest_api.js-log
  (:require [clojure.string :as str]
            [clojure.tools.logging :as log]))

;   "max length of message strings from the client side"
(def
  maxlength 1000)

(defn sanitize
  "replaces linefeeds with blanks and limits the length"
  [s]
  {:pre [clojure.core/string?]}
  (let [ln (min (.length s) maxlength)]
    (-> s
      (str/replace "\n" "!")
      (str/replace "\r" "!")
      (.substring 0 ln))))

(defn logita [virheenUrl userAgent virheviesti stackTrace cause]
  "Tarkoitus on wrapata tämä sopivaan compojure-reittiin"
  (let [rivinvaihto "\n"]
    (log/info (str rivinvaihto
                "--- Javascript virhe ---" rivinvaihto
                "Virheen url: " (sanitize virheenUrl) rivinvaihto
                "User agent string: " (sanitize userAgent) rivinvaihto
                "Virheviesti: " (sanitize virheviesti) rivinvaihto
                "Stacktrace: " (sanitize stackTrace) rivinvaihto
                "Aiheuttaja: " (sanitize cause) rivinvaihto
                "------------------------")))
  {:status 200})
