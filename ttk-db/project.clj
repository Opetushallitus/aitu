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

(defproject ttk-db "0.1.0-SNAPSHOT"
  :description "AITU projektin tietokannan rakentaminen"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [stencil "0.3.2"]
                 [com.googlecode.flyway/flyway-core "2.2"]
                 [org.clojure/java.jdbc "0.3.0-alpha5"]
                 [postgresql "9.1-901.jdbc4"]
                 [org.clojure/tools.cli "0.3.0"]]
  :profiles {:uberjar {:aot :all}}
  :resource-paths ["resources"]
  :main ttk-db.core
  :jar-name "ttk-db.jar"
  :uberjar-name "ttk-db-standalone.jar")
