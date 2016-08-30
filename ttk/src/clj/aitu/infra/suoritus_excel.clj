;; Copyright (c) 2015 The Finnish National Board of Education - Opetushallitus
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

(ns aitu.infra.suoritus-excel
   (:require [aitu.infra.tutkinto-arkisto :as tutkinto-arkisto]
             [aitu.infra.tutkinnonosa-arkisto :as tutosa-arkisto]
             [aitu.infra.suorittaja-arkisto :as suorittaja-arkisto]
             [dk.ative.docjure.spreadsheet :refer :all]))

; [t], jossa t [tutkintotunnus (osa1 osa2..)]
; eli vektori, jonka sisällä on vektoreina tutkintotunnus + lista sen osista
(defn hae-osarakenne []
  (let [tutkinnot (map :tutkintotunnus (sort-by :tutkintotunnus (tutkinto-arkisto/hae-kaikki)))
      replacef #(-> %
                    (clojure.string/replace "\n" "")
                    (clojure.string/replace "\u2011" "-")
                    (clojure.string/replace "\u2013" "-"))]
  (into [] 
     (for [tutkinto tutkinnot]
       (let [osat (tutosa-arkisto/hae tutkinto)]
         [tutkinto (map #(str (replacef (:nimi_fi %)) " (" (:osatunnus %) ")") osat)
               ]
         )))))

(defn ^:private set-or-create-cell! [sheet n val type]
  (let [cellref (org.apache.poi.ss.util.CellReference. n)
        r (.getRow cellref)
        col (int (.getCol cellref))
        rows (row-seq sheet)
        row (nth rows r)
        cell (or (select-cell n sheet) (.createCell row col type))]
    (set-cell! cell val)))

(def osacolumns ["C", "D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"
                 "AA","AB","AC","AD","AE","AF","AG","AH","AI","AJ","AK","AL","AM","AN","AO","AP","AQ","AR","AS","AT","AU","AV","AW","AX","AY","AZ"
                 "BA","BB","BC","BD","BE","BF","BG","BH","BI","BJ","BK","BL","BM","BN","BO","BP","BQ","BR","BS","BT","BU","BV","BW","BX","BY","BZ"
                 "CA","CB","CC","CD","CE","CF","CG","CH","CI","CJ","CK","CL","CM","CN","CO","CP","CQ","CR","CS","CT","CU","CV","CW","CX","CY","CZ"
                 "DA","DB","DC","DD","DE","DF","DG","DH","DI","DJ","DK","DL","DM","DN","DO","DP","DQ","DR","DS","DT","DU","DV","DW","DX","DY","DZ"])
               
; TODO: 
;(user/with-testikayttaja 
;     (let [wb (luo-excel)]
;       (save-workbook! "tutosat_taydennetty.xlsx" wb)))

(defn ^:private map-tutkintorakenne! [sheet]
  (let [tutkintorakenne (hae-osarakenne)]
    (doall (map-indexed (fn [i tutkinto]
             (let [tutkintotunnus (first tutkinto)
                   tutkinnonosat (second tutkinto)
                   row (+ 2 i)]
               (set-or-create-cell! sheet (str "A" row) tutkintotunnus org.apache.poi.ss.usermodel.Cell/CELL_TYPE_STRING)
               (doall (map-indexed (fn [ind tutkinnonosa]
                                     (let [colstr (nth osacolumns ind)]
                                       (set-or-create-cell! sheet (str colstr row) tutkinnonosa org.apache.poi.ss.usermodel.Cell/CELL_TYPE_STRING)))
                                   tutkinnonosat))))
                        tutkintorakenne))))
      
(defn luo-excel []
  (let [import (load-workbook  "resources/tutosat_export_base.xlsx")
        tutosat (select-sheet "tutkinnonosat" import)]
    (map-tutkintorakenne! tutosat)
     import))
      
      
 
        
        