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

(ns aitu.log-util
  (:require
    [clojure.tools.logging :as log]
    [clojure.test :refer [is]]))

(defn log-through
  "palauttaa logiin kirjoitetun viestin levelin ja sisällön kun kutsutaan funktiota f"
  [f]
  (let [log (atom [])]
    (with-redefs [log/log* (fn [_ level _ msg]
                             (swap! log conj [level msg]))]
      (reset! log [])      
      (f)
      @log)))

(defn log-validate
  [f expected-msg]
  (is (= expected-msg (log-through f))))
