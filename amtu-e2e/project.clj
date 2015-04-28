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

(defproject aitu-e2e "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [solita/opetushallitus-aitu-e2e "0.21.0"]]
  :plugins [[test2junit "1.0.1"]]

  :test-selectors {:ie (complement (some-fn :no-ie :ie-epastabiili))
                   :ie-epastabiili :ie-epastabiili
                   :ie-kaikki (complement :no-ie)
                   :no-ie :no-ie
                   :no-cas (complement :cas)
                   :default (constantly true)})

(require '[robert.hooke :refer [add-hook]])
(require 'leiningen.test)
