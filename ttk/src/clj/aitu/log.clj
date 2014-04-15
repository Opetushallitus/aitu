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

(ns aitu.log
  (:require aitu.infra.print-wrapper
            oph.korma.korma-auth
            [clojure.tools.logging]
            [robert.hooke :refer [add-hook]]))

(def ^:dynamic *lisaa-uid-ja-request-id?* true)

(defn lisaa-uid-ja-requestid
  [f logger level throwable message]
  (let [uid (if (bound? #'oph.korma.korma-auth/*current-user-uid*)
              oph.korma.korma-auth/*current-user-uid*
              "-")
        requestid (if (bound? #'aitu.infra.print-wrapper/*requestid*)
                    aitu.infra.print-wrapper/*requestid*
                    "-")
        message-with-id (str "[User: " uid ", request: " requestid "] " message)]
    (if *lisaa-uid-ja-request-id?* 
      (f logger level throwable message-with-id)
      (f logger level throwable message))))
  
(defn lisaa-uid-ja-requestid-hook []
  (add-hook #'clojure.tools.logging/log* #'lisaa-uid-ja-requestid))
