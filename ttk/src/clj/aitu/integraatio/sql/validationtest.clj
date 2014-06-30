(ns aitu.integraatio.sql.validationtest
  "Validation tests for relational DB"
  (:require [clojure.java.io :as io]
            [clojure.java.jdbc :as jdbc]
            [korma.core :as sql]
            [clojure.tools.logging :as log]
            [korma.db :as db]))

(defn load-validation-queries! [filepath]
  (let [r (io/resource filepath)]
    (with-open [istream (.openStream  r)]
      (solita.util.validationtest.SmokeTestSqlReader/readAll istream))))

(def default-query-list
  (delay (load-validation-queries! "validationtests.sql")))

(defn run-queries! [query-list]
    (log/info "Ajetaan tietokannan validointitestit: " (count query-list) " kappaletta")
    (into [] (doall (for [query query-list]
                      (let [tulos {:title (.getQueryTitle query)
                                   :results (sql/exec-raw [(.getQuerySql query)] :results)}]
                        (log/info "Kysely " (:title tulos) " ongelmia " (count (:results tulos)) " kpl")
                        tulos)))))
