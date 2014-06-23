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

(defproject ttk "0.1.0-SNAPSHOT"
  :description "Tutkintotoimikuntarekisteri"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [ring/ring-core "1.2.1"]
                 [http-kit "2.1.10"]
                 [compojure "1.1.5"]
                 [ring/ring-json "0.2.0"]
                 [ring/ring-headers "0.1.0"]
                 [cheshire "5.2.0"]
                 [metosin/compojure-api "0.8.2"]
                 [metosin/ring-swagger-ui "2.0.10-1"]

                 [org.clojars.noidi/clj-cas-client
                  "0.0.6-4ae43963cb458579a3813f9dda4fba52ad4d9607-ring-1.2.1"
                  :exclusions [ring]]
                 [cas-single-sign-out "0.1.2"]
                 [org.clojars.pntblnk/clj-ldap "0.0.7"]

                 [org.clojure/java.jdbc "0.3.0-alpha5"]
                 [org.postgresql/postgresql "9.3-1101-jdbc41"]
                 [com.jolbox/bonecp "0.8.0.RELEASE"]
                 [korma "0.3.0-RC6"]
                 [clj-time "0.6.0"]
                 
                 [org.clojure/tools.logging "0.2.6"]
                 [org.slf4j/slf4j-log4j12 "1.7.1"]
                 [log4j/log4j "1.2.17" :exclusions [javax.mail/mail
                                                    javax.jms/jms
                                                    com.sun.jmdk/jmxtools
                                                    com.sun.jmx/jmxri]]

                 [stencil "0.3.2"]
                 ;; lein-cljsbuild lataa käännöksen ajaksi vanhemman version,
                 ;; mistä seuraa ongelmia. Tällä pakotetaan käyttöön stencilin
                 ;; vaatima versio.
                 [org.clojure/core.cache "0.6.2"]
                 [slingshot "0.10.3"]

                 [com.cemerick/valip "0.3.2"]
                 [clojurewerkz/quartzite "1.1.0"]
                 [org.clojure/data.zip "0.1.1"]
                 [robert/hooke "1.3.0"]
                 [prismatic/schema "0.2.0"]]
  :plugins [[lein-cljsbuild "0.3.2"]
            [lein-cloverage "1.0.2"]
            [test2junit "1.0.1"]
            [codox "0.6.6"]]
  :profiles {:dev {:source-paths ["dev"]
                   :resource-paths ["test-resources"]
                   :dependencies [[org.clojure/tools.namespace "0.2.4"]
                                  [clj-webdriver "0.6.0"]
                                  [clj-http "0.7.6"]
                                  [ring-mock "0.1.5"]]}
             :uberjar {:main aitu.palvelin
                       :aot [aitu.palvelin]}}
  :source-paths ["src/clj"]
  :java-source-paths ["src/java"]
  :javac-options ["-target" "1.7" "-source" "1.7"]
  :test-paths ["test/clj"]
  :test-selectors {:kaikki (constantly true)
                   :default (complement :integraatio)
                   :integraatio :integraatio}
  :jar-name "ttk.jar"
  :uberjar-name "ttk-standalone.jar"
  :main aitu.palvelin
  :repl-options {:init-ns user}
  :cljsbuild {:builds [{:source-paths ["src/cljs"]
                        :compiler {:output-to "resources/public/ttk-cljs.js"
                                   :optimizations :whitespace
                                   :pretty-print true}}]})
