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

(ns ttk-db.core
  (:gen-class)
  (:import [com.googlecode.flyway.core Flyway]
           [com.googlecode.flyway.core.util.jdbc DriverDataSource]
           [javax.sql DataSource]
           [java.net URLEncoder
                     URLDecoder]
           com.googlecode.flyway.core.api.FlywayException)
  (:require [clojure.java.io :as io]
            [clojure.java.jdbc :as jdbc]
            [stencil.core :as s]
            [clojure.tools.cli :refer [parse-opts]]
            [clojure.string :as str]))


(defn jdbc-do
  [& sql]
  (doseq [stm sql]
    (println (str stm ";")))
  (apply jdbc/do-commands sql))

(defn sql-resurssista
  "Palauttaa vektorin, jossa on SQL-statementteja.
   TODO: parseri ei ole mitenkään täydellinen.."
  [nimi]
   (str/split (slurp (io/resource nimi) :encoding "UTF-8") #";"))

; aitu.kayttaja
(defn run-sql
  [sql kayttaja-param]
  (jdbc-do (str "set session " kayttaja-param "='JARJESTELMA'"))
  (doseq [stmt sql]
    (try
      (jdbc-do stmt)
      (catch java.sql.SQLException e
        (throw (.getNextException e)))))
  (jdbc-do (str "set " kayttaja-param " to default")))

(defn aja-testidata-sql!
  [kayttaja-param tiedosto]
  (run-sql (sql-resurssista (str "sql/" tiedosto)) kayttaja-param))

(defn luo-testikayttajat!
  [kayttaja-param]
  (aja-testidata-sql! kayttaja-param "testikayttajat.sql"))

(defn luo-testidata!
  [kayttaja-param]
  (luo-testikayttajat! kayttaja-param)
  (aja-testidata-sql! kayttaja-param "testitoimikunnat.sql")
  (aja-testidata-sql! kayttaja-param "testidata.sql"))

(defn anonymisoi-henkilodata!
  [kayttaja-param]
  (println "Anonymisoidaanko henkilödata? K/E")
  (println "HUOM: EI SAA AJAA TUOTANNOSSA")
  (let [ok (read-line)]
    (if (= ok "K")
      (do
        (println "Anonymisoidaan")
        (aja-testidata-sql! kayttaja-param "anonymisointi.sql"))
      (println ok " -> Ei anonymisoida."))))


(defn aseta-oikeudet-sovelluskayttajille
  [options]
  {:pre [(contains? options :username)
         (contains? options :aituhaku-username)]}
  (jdbc-do
    (str "GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO " (:username options) )
    (str "GRANT SELECT, USAGE ON ALL SEQUENCES IN SCHEMA public TO " (:username options) )
    (str "GRANT SELECT ON ALL TABLES IN SCHEMA aituhaku TO " (:aituhaku-username options) )
    (str "GRANT USAGE ON SCHEMA aituhaku TO " (:aituhaku-username options) )))

(defn parse-uri
  "Parsitaan mappiin Posgren JDBC-URL.
   Postgren JDBC-ajuri palauttaa null Connection-olioita jos URL sisältää usernamen/passwordin."
  [uri]
  (let [prefix (first (str/split uri #"//"))
        etc (second (str/split uri #"//"))
        user (URLDecoder/decode (first (str/split etc #":")))
        passwd (URLDecoder/decode (first (str/split (second (str/split etc #":")) #"@")))
        postfix (second (str/split etc #"@"))]
     {:user user
      :passwd passwd
      :uri uri
      :postgre-uri (str "jdbc:" prefix "//" postfix)
      }))

(defn create-datasource!
  "Palauttaa Flyway DataSourcen, jota voidaan käyttää myös JDBC:n kanssa"
  [url]
  (let [jdbc-creds (parse-uri url)
        datasource (DriverDataSource.
                     nil (:postgre-uri jdbc-creds) (:user jdbc-creds) (:passwd jdbc-creds)
                     (into-array ["set session aitu.kayttaja='JARJESTELMA';"]))]
    datasource))

(defn alusta-flywaylla!
  "Alustaa tietokannan Flywayn avulla."
  [datasource options]
  (let [flyway (Flyway.)
        kantaversio (:target-version options)
        tyhjenna (:clear options)]
    (.setDataSource flyway datasource)
    (.setLocations flyway (into-array String ["/db/migration"]))
    (when tyhjenna (.clean flyway))
    (when kantaversio (.setTarget flyway kantaversio))
    (.migrate flyway)))

(def cli-options
  [[nil "--clear" "Tyhjennetään kanta ja luodaan skeema ja pohjadata uusiksi"
    :default false]
   [nil "--target-version VERSION" "Tehdään migrate annettuun versioon saakka"]
   ["-t" nil "Testidatan luonti"
    :id :testidata]
   [nil "--anonymisointi" "Henkilödatan anonymisointi"
    :id :anonymisointi]
   ["-u" "--username USER" "Tietokantakäyttäjä"
    :default "ttk_user"]
   [nil "--aituhaku-username USER" "Aituhaun tietokantakäyttäjä"]
   ["-V" "--uservariable VAR" "Tietokantasessiossa triggereissä käytetty käyttäjän muuttujan nimi"
    :default "aitu.kayttaja"]
   ["-h" "--help" "Käyttöohje"]])

(defn ohje [options-summary]
  (->> ["Käyttö: lein run [options] [jdbc-url]"
        ""
        "Käyttöönotettaessa uusi kanta on tyhjennettävä (--clear), jotta voidaan varmistua että kanta on"
        "tyhjä eikä sisällä ylimääräisiä tauluja tai dataa. jdbc-url:ssa käyttäjä ja salasana tulee olla URL enkoodattu"
        ""
        "Ilman jdbc-url osoitetta yritetään lukea osoite ttk.properties tiedoston perusteella."
        ""
        "Optiot:"
        options-summary
        ""]
       (str/join \newline)))

(defn error-msg [errors]
  (str "Virhe parametreissa:\n\n"
       (str/join \newline errors)))

(defn exit [status msg]
  (println msg)
  (System/exit status))

(defn properties->jdbc-url
 [reader]
 (let [props  (doto (java.util.Properties.)
                (.load reader))
       host (.getProperty props "db.host")
       schema-user (.getProperty props "db.user")
       schema-name (.getProperty props "db.name")
       schema-user-passwd (.getProperty props "db.password")
       port (.getProperty props "db.port")]
   (str "postgresql://" (URLEncoder/encode schema-user) ":" (URLEncoder/encode schema-user-passwd) "@" host ":" port "/" schema-name)))

(defn file->jdbc-url
  [polku]
  (with-open [reader (clojure.java.io/reader polku)]
    (properties->jdbc-url reader)))

(defn -main [& args]
  (let [{:keys [options arguments errors summary]} (parse-opts args cli-options)]
    (cond
      (:help options) (exit 0 (ohje summary))
      (> (count arguments) 1) (exit 1 (ohje summary))
      errors (exit 1 (error-msg errors)))
    (let [jdbc-url (or (first arguments) (file->jdbc-url "ttk-db.properties"))
          datasource (create-datasource! jdbc-url)
          migraatiopoikkeus (try
                              (alusta-flywaylla! datasource options)
                              nil
                              (catch FlywayException e
                                e))]
      ;; Annetaan käyttöoikeudet sovelluskäyttäjille, vaikka osa migraatioista
      ;; epäonnistuisi
      (try
        (jdbc/with-connection {:datasource datasource}
          (aseta-oikeudet-sovelluskayttajille options)
          (when (:anonymisointi options)
            (anonymisoi-henkilodata! (:uservariable options)))
          (when (:testidata options)
            (luo-testidata! (:uservariable options))))
        (finally
          (when migraatiopoikkeus
            (.printStackTrace migraatiopoikkeus System/out)
            (System/exit 1)))))))

