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

(ns aitu.integraatio.clamav
  (:require [clojure.tools.logging :as log]
            [org.httpkit.client :as http]
            [aitu.asetukset :refer [asetukset]]
            [clojure.tools.logging :as log]
            clojure.java.io))

(def ok-reply "Everything ok : true\n")
(def found-reply "Everything ok : false\n")

(defn tarkista-tiedosto [tiedostonimi tiedosto]
  (let [{:keys [host port]} (:clamav @asetukset)
       url (str "http://" host ":" port "/scan")
       {:keys [body status error]} @(http/post url {:method :post
                                                    :multipart [{:name "file"
                                                                 :content tiedosto
                                                                 :filename tiedostonimi}]
                                                    :headers {"Content-Type" "multipart/form-data"}
                                                    :query-params {:name tiedostonimi}})]
   (cond
     error (do
             (log/error "Virhe ClamAV-yhteydessä. Url: " url ", virhe: " error)
             :error)
     (and (= status 200)
          (= body ok-reply)) :passed
     :else (do
             (log/error "Virus havaittu tiedostossa " tiedostonimi)
             :failed))))
