;; Copyright (c) 2014 The Finnish National Board of Education - Opetushallitus
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

(defproject solita/opetushallitus-aitu-e2e "0.21.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [clj-webdriver "0.6.1" :exclusions [org.seleniumhq.selenium/selenium-java
                                                     org.seleniumhq.selenium/selenium-server
                                                     org.seleniumhq.selenium/selenium-remote-driver]]
                 [org.seleniumhq.selenium/selenium-java "2.45.0"]
                 [org.seleniumhq.selenium/selenium-server "2.45.0"]
                 [org.seleniumhq.selenium/selenium-remote-driver "2.45.0"]
                 [clj-http "0.9.2"]
                 [cheshire "5.3.1"]
                 [com.paulhammant/ngwebdriver "0.9.1" :exclusions [org.seleniumhq.selenium/selenium-java]]
                 [clj-time "0.7.0"]])
