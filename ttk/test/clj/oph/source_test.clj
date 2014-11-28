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

(ns oph.source-test
  "Tarkistuksia lähdekoodille."
  (:import java.io.PushbackReader)
  (:require [clojure.test :refer [deftest testing is]]
            [clojure.java.io :refer [file reader]]
            [clojure.walk :as cw]
            [clojure.string :refer [trim]]))

(defn tiedostot [hakemisto polku-re ohita]
  (let [ohita (set (map file ohita))]
    (for [polku (file-seq (file hakemisto))
          :when (not (or (.isDirectory polku)
                         (ohita polku)))
          :when (re-matches polku-re (str polku))]
      polku)))

(defn vastaavat-tiedostot [hakemisto polku-re f & {:keys [ohita]
                                                     :or {ohita #{}}}]
  (apply concat
    (for [polku (tiedostot hakemisto polku-re ohita)]
       (with-open [r (reader polku)]
         (f r)))))

(defn vastaavat-rivit [hakemisto polku-re mallit & {:keys [ohita]
                                                    :or {ohita #{}}}]
  (apply concat
         (for [polku (tiedostot hakemisto polku-re ohita)]
           (with-open [r (reader polku)]
             (doall
               (for [[nro rivi] (map vector
                                     (iterate inc 1)
                                     (line-seq r))
                     :when (some #(re-find % rivi) mallit)]
                 (str polku ":" nro ": " (trim rivi))))))))

(defn vastaavat-muodot [hakemisto ehto & {:keys [ohita polku-re]
                                          :or {ohita #{}
                                               polku-re #".*\.clj"}}]
    (apply concat
           (for [polku (tiedostot hakemisto polku-re ohita)]
             (with-open [r (PushbackReader. (reader polku))]
               (doall
                 (for [muoto (repeatedly #(read r false ::eof))
                       :while (not= muoto ::eof)
                       :when (ehto muoto)]
                   (str polku ": " muoto)))))))


(defn pre-post [muoto]
  (when (= 'defn (nth muoto 0))
    (some #(and (map? %)
                (or (contains? % :pre)
                    (contains? % :post))
                %)
          muoto)))

(defn pre-post-vaarassa-paikassa? [muoto]
  (when-let [pp (pre-post muoto)]
    (not (or (and (symbol? (nth muoto 1))
                  (vector? (nth muoto 2))
                  (= pp (nth muoto 3)))
             (and (symbol? (nth muoto 1))
                  (string? (nth muoto 2))
                  (vector? (nth muoto 3))
                  (= pp (nth muoto 4)))))))

(defn pre-post-ei-vektori? [muoto]
  (when-let [pp (pre-post muoto)]
    (not (every? vector? (vals pp)))))

(defn get-meta [o]
  "http://stackoverflow.com/questions/12432561/how-to-get-the-metadata-of-clojure-function-arguments"
  (->> *ns* ns-map (filter (fn [[_ v]] (and (var? v) (= o (var-get v))))) first second meta))

(defn defn-without-meta? [muoto kwset]
  "tarkistaa että muoto ei sisällä määriteltyjä keywordeja, esim. :test-api metatietoa"
  (and
    (= 'defn (nth muoto 0))
    (empty? (clojure.set/intersection (set (keys (meta (nth muoto 1)))) kwset))))

(defn ei-audit-logitettava-funktio? [muoto]
  "test-api ja integraatioiden käyttämät arkistofunktiot eivät ole auditlokituksen piirissä"
  (defn-without-meta? muoto #{:test-api :integration-api}))

(defn sivuvaikutuksellinen-funktio? [muoto]
  "Jos funktion nimi loppuu huutomerkkiin, tulkitaan että sillä on sivuvaikutuksia."
  (and
    (= 'defn (nth muoto 0))
    (let [fn-name (name (nth muoto 1))]
      (.endsWith fn-name "!"))))

(defn audit-log-kutsu-puuttuu? [muoto]
  "tarkistaa puuttuuko audit-log kutsu muodosta jossa sellainen pitäisi olla"
  (let [sisaltaa-audit-kutsun? (some #(and (symbol? %)
                                        (= "auditlog" (.getNamespace %)))
                                 (flatten muoto))]
    (when (and
            (ei-audit-logitettava-funktio? muoto)
            (sivuvaikutuksellinen-funktio? muoto)
            (not sisaltaa-audit-kutsun?))
      (println "! AUDITLOG kutsu puuttuu: " (nth muoto 1))
      (str (nth muoto 1)))))

(deftest audit-log-kutsut-ovat-olemassa
  (is (empty? (vastaavat-muodot "src/clj" audit-log-kutsu-puuttuu? :ohita ["src/clj/aitu/auditlog.clj"]))))

(deftest js-debug-test
  (is (empty? (vastaavat-rivit "resources/public/js"
                               #".*\.js"
                               [#"console\.log"
                                #"debugger"
                                (re-pattern (str \u00a0)) ; non-breaking space
                                ]
                               :ohita ["resources/public/js/vendor/angular.js"
                                       "resources/public/js/vendor/stacktrace.js"]))))
 
(deftest properties-encoding-test
  "etsitään merkkejä jotka eivät ole ns. printable charactereita. Ääkköset ovat näitä enkoodaussyistä"
  (is (empty? (vastaavat-rivit "resources/i18n"
                               #".*\.properties"
                               [#"[^\p{Print}\p{Space}]+"]))))

(defn properties-duplicat-keys? [r]
  (let [dup (doto (oph.util.DuplicateAwareProperties.)
                  (.load r)
                  )
        duplicates (.getDuplicates dup)]
    duplicates))

(deftest properties-duplicate-keys-test
  "Etsitään properties tiedostoista tupla-avaimia"
  (is (empty? (vastaavat-tiedostot "resources/i18n" #".*\.properties"
                properties-duplicat-keys?))))

(deftest pre-post-oikeassa-paikassa-test
  (is (empty? (vastaavat-muodot "src/clj" pre-post-vaarassa-paikassa?))))

(deftest pre-post-vektori-test
  (is (empty? (vastaavat-muodot "src/clj" pre-post-ei-vektori?))))

(defn load-props [filename]
  (with-open [fs (java.io.FileInputStream. filename)]
    (let [ prop (java.util.Properties.)]
      (.load prop fs)
      (into {} prop))))

(deftest kielikaannokset-loytyvat
  (testing "Tarkistetaan että jokaista suomen kielistä lokalisointiavainta vastaa ruotsinkielinen lokalisointiavain"
    (let [suomi-avaimet  (set (keys (load-props "resources/i18n/tekstit.properties")))
          ruotsi-avaimet (set (keys (load-props "resources/i18n/tekstit_sv.properties")))]
      (is (empty? (clojure.set/difference suomi-avaimet ruotsi-avaimet))))))
