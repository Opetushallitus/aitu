
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

(defproject solita/opetushallitus-aitu-e2e "0.28.0-SNAPSHOT"
  :description "aitu-common-e2e"
  :url "https://github.com/Opetushallitus/aitu/tree/master/aitu-common-e2e"
  :license {:name "European Union Public License - v1.1 or later"
            :url "https://joinup.ec.europa.eu/software/page/eupl/licence-eupl"
            :distribution :repo}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [clj-webdriver "0.7.2" :exclusions [org.seleniumhq.selenium/selenium-java
                                                     org.seleniumhq.selenium/selenium-server
                                                     org.seleniumhq.selenium/selenium-remote-driver]]
                 [org.seleniumhq.selenium/selenium-java "2.48.2"]
                 [org.seleniumhq.selenium/selenium-server "2.48.2"]
                 [org.seleniumhq.selenium/selenium-remote-driver "2.48.2"]
                 [clj-http "1.1.2"]
                 [cheshire "5.3.1"]
                 [com.paulhammant/ngwebdriver "0.9.1" :exclusions [org.seleniumhq.selenium/selenium-java]]
                 [clj-time "0.7.0"]])