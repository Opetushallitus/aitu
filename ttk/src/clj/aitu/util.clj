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

(ns aitu.util
  "Yleisiä apufunktioita."
  (:require [cheshire.core :as cheshire]
            [clj-time.core :as time]
            [clj-time.format :as time-format]
            [clojure.string :as string]
            [org.httpkit.client :as http]
            [clojure.set :refer [union]]
            [clojure.walk :refer [keywordize-keys postwalk]]
            [clojure.tools.logging :as log]
            [schema.core :as s]
            [clojure-csv.core :refer [write-csv]]))
 
(defn update-in-if-exists [m [k & ks] f & args]
  (if (and (map? m) (contains? m k))
    (if ks
      (assoc m k (apply update-in-if-exists (get m k) ks f args))
      (assoc m k (apply f (get m k) args)))
    m))

(defn select-and-rename-keys
  "Poimii mapista annetut avaimet. Jos avain on muotoa [:a :b], vaihtaa samalla avaimen nimen :a -> :b."
  [map keys]
  (loop [ret {} keys (seq keys)]
    (if keys
      (let [key (first keys)
            [from to] (if (coll? key)
                        key
                        [key key])
            entry (. clojure.lang.RT (find map from))]
        (recur
          (if entry
            (conj ret [to (val entry)])
            ret)
          (next keys)))
      ret)))

(defn schema? [x]
  (instance? schema.core.Schema x))

(defn kaikki-optional [schema]
  (into {} (for [[k v] schema
                 :let [k (s/optional-key k)]]
             (cond
               (schema? v) [k v]
               (map? v) [k (kaikki-optional v)]
               :else [k v]))))

(defn keyword-vertailu
  "vertaa avaimia halutun järjestys-taulukon mukaisesti, tuntemattomat avaimet loppuun"
  [jarjestys a b]
  (compare [(.indexOf (reverse jarjestys) b) b]
           [(.indexOf (reverse jarjestys) a) a]))

(defn jarjesta-avaimet
  "Järjestää mapin taulukkona annetun järjestyksen mukaiseksi"
  [m jarjestys]
  (into (sorted-map-by #(keyword-vertailu jarjestys %1 %2)) m))

(defn otsikot-ja-sarakkeet-jarjestykseen [m kenttien-jarjestys]
  (into [(for [[k _] (jarjesta-avaimet (first m) kenttien-jarjestys)] (name k))]
        (for [rivi m]
          (for [[_ v] (jarjesta-avaimet rivi kenttien-jarjestys)]
            (str v)))))

(defn muodosta-csv [data kenttien-jarjestys]
  (write-csv (otsikot-ja-sarakkeet-jarjestykseen data kenttien-jarjestys) :delimiter \;))
