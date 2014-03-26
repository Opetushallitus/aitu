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

(ns aitu.timeutil
  (:require [clj-time.core :as ctime]
            [clj-time.coerce :as tcoerce]))

; työn alla..
; mutta tarkoitus on kerätä tänne kamaa, jolla voidaan käsitellä versiointia ja voimassaoloa järkevästi
(def time-forever (ctime/local-date 2199 1 1))

(defn pvm-menneisyydessa?
  [pvm]
  {:pre [(not (nil? pvm))]}
  (let [nytpvm (ctime/today)]
    (ctime/after? nytpvm pvm)))


(defn pvm-tulevaisuudessa?
  [pvm]
  {:pre [(not (nil? pvm))]}
  (let [nytpvm (ctime/today)]
    (ctime/before? nytpvm pvm)))

(def pvm-mennyt-tai-tanaan?
  (complement pvm-tulevaisuudessa?))

(def pvm-tuleva-tai-tanaan?
  (complement pvm-menneisyydessa?))

(defn merkitse-voimassaolevat
  [entity avain f]
  (update-in entity [avain]
    #(vec
       (for [arvo %]
         (assoc arvo :voimassa (f entity arvo))))))

(defn voimassa?
  [e]
  (let [now (ctime/now)]
    (and
      (ctime/after? now (:voimassaalku e))
      (ctime/after? (:voimassaloppu e) now))))

; tarkalleen yksi on voimassa tällä hetkellä
; (= 1 (count (filterv true? (mapv voimassa? datachunk))))
(defn get-voimassaoleva
  [d]
  (filterv voimassa? d))
