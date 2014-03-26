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

(ns aitu.infra.validaatio
  (:require [clj-time.core :as time]))

(defn validoi-pvm-ennen
  "Palauttaa predikaattifunktion, joka testaa onko validoitava arvo ennen pvm:ää"
  [pvm]
  (fn [arvo]
    (time/before? arvo pvm)))

(defn validoi-pvm-ei-ennen
  "Palauttaa predikaattifunktion, joka testaa onko validoitava arvo sama kuin pvm tai pvm:n jälkeen."
  [pvm]
  (fn [arvo]
    (not (time/before? arvo pvm))))

(defn validoi-alkupvm-sama-tai-ennen-loppupvm
  "Palauttaa predikaattifunktion, joka testaa onko validoitava alkupäivä sama tai ennen loppupäivää siinä tapauksessa, että loppupäivä on annettu"
  [loppupvm]
  (fn [alkupvm]
    (if loppupvm
      (if alkupvm
        (not (time/after? alkupvm loppupvm))
        false)
      true)))

(defn validoi-pvm-valissa
  "Palauttaa predikaattifunktion, joka testaa onko validoitava arvo annettujen päivämäärien välissä"
  [alkupvm loppupvm]
  (fn [arvo]
    (if arvo (time/within? alkupvm loppupvm arvo) true)))


(defn validoi-ei-paallekkain
  "Palauttaa predikaattifunktion, joka testaa ettei validoitava arvo ole päällekkäin annettujen päivämäärävälien kanssa"
  [valit]
  (fn [arvo]
    (if arvo (not-any? #(time/within? (:alkupvm %) (:loppupvm %) arvo) valit) true)))

(defn validoi-ei-sisalla-aikavaleja
  "Palautta predikaattifunktion, joka testaa ettei validoitava aikaväli mene päällekkäin annettujen aikavälien kanssa"
  [valit loppupvm]
  (fn [alkupvm]
    (if (and alkupvm loppupvm)
      (not-any? #(time/overlaps? (:alkupvm %) (:loppupvm %) alkupvm loppupvm) valit)
      true)))
