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

(ns aitu.rest_api.js-log
  (:require [compojure.core :as c]
            [aitu.compojure-util :as cu]
            [clojure.string :as str]
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
      (str/replace "\n" " " )
      (str/replace "\r" " ")
      (.substring 0 ln))))

(c/defroutes reitit
  (cu/defapi :logitus nil :post "/virhe" [virheenUrl userAgent virheviesti stackTrace cause]
    (let [rivinvaihto "\n"]
      (log/info (str rivinvaihto
                     "--- Javascript virhe ---" rivinvaihto
                     "Virheen url: " virheenUrl rivinvaihto
                     "User agent string: " userAgent rivinvaihto
                     "Virheviesti: " virheviesti rivinvaihto
                     "Stacktrace: " stackTrace rivinvaihto
                     "Aiheuttaja: " cause rivinvaihto
                     "------------------------")))
    {:status 200}))
