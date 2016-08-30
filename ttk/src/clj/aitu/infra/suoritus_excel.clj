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

(defn ^:private set-or-create-cell! 
  ([sheet n val type]
    (let [cellref (org.apache.poi.ss.util.CellReference. n)
          r (.getRow cellref)
          col (int (.getCol cellref))
          rows (row-seq sheet)
          row (nth rows r)
          cell (or (select-cell n sheet) (.createCell row col type))]
      (set-cell! cell val)))
  ([sheet n val]
    (set-or-create-cell! sheet n val org.apache.poi.ss.usermodel.Cell/CELL_TYPE_STRING)))

(def osacolumns ["C", "D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"
                 "AA","AB","AC","AD","AE","AF","AG","AH","AI","AJ","AK","AL","AM","AN","AO","AP","AQ","AR","AS","AT","AU","AV","AW","AX","AY","AZ"
                 "BA","BB","BC","BD","BE","BF","BG","BH","BI","BJ","BK","BL","BM","BN","BO","BP","BQ","BR","BS","BT","BU","BV","BW","BX","BY","BZ"
                 "CA","CB","CC","CD","CE","CF","CG","CH","CI","CJ","CK","CL","CM","CN","CO","CP","CQ","CR","CS","CT","CU","CV","CW","CX","CY","CZ"
                 "DA","DB","DC","DD","DE","DF","DG","DH","DI","DJ","DK","DL","DM","DN","DO","DP","DQ","DR","DS","DT","DU","DV","DW","DX","DY","DZ"])

(defn ^:private map-tutkintorakenne! [sheet]
  (let [tutkintorakenne (hae-osarakenne)]
    (doall (map-indexed (fn [i tutkinto]
             (let [tutkintotunnus (first tutkinto)
                   tutkinnonosat (second tutkinto)
                   row (+ 2 i)]
               (set-or-create-cell! sheet (str "A" row) tutkintotunnus)
               (doall (map-indexed (fn [ind tutkinnonosa]
                                     (let [colstr (nth osacolumns ind)]
                                       (set-or-create-cell! sheet (str colstr row) tutkinnonosa)))
                                   tutkinnonosat))))
                        tutkintorakenne))))
      
(defn ^:private map-opiskelijat! [sheet]
  (let [suorittajat (->>  (suorittaja-arkisto/hae-kaikki)
           (map #(assoc % :nimi (str (:etunimi %) " " (:sukunimi %) " (" (:oid %) ")"))  )
           (sort-by :nimi))]
    (doseq [opiskelija suorittajat]
      (let [xls-row [(:nimi opiskelija)
                     (str (:suorittaja_id opiskelija))
                     (:oid opiskelija)
                     (:hetu opiskelija)
                     (:rahoitusmuoto_nimi opiskelija)]]
                          
      (add-row! sheet xls-row)))))
;    (doall (map-indexed (fn [r opiskelija]
;                          (println " .. " opiskelija )
;                          (let [row (+ 3 r)] 
;                            (set-or-create-cell! sheet (str "A" row) (:nimi opiskelija))
;                            (set-or-create-cell! sheet (str "B" row) (str (:suorittaja_id opiskelija)))
;                            (set-or-create-cell! sheet (str "C" row) (:oid opiskelija))
                           ; (set-or-create-cell! sheet (str "D" row) (:hetu opiskelija))
;                            (set-or-create-cell! sheet (str "E" row) (:rahoitusmuoto_nimi opiskelija))
;                          )) suorittajat))))
               
; TODO: 
;(user/with-testikayttaja 
;     (let [wb (luo-excel)]
;       (save-workbook! "tutosat_taydennetty.xlsx" wb)))
  
(defn luo-excel []
  (let [export (load-workbook  "resources/tutosat_export_base.xlsx")
        tutosat (select-sheet "tutkinnonosat" export)
        opiskelijat (select-sheet "Opiskelijat" export)]
    ; (map-tutkintorakenne! tutosat)
    (map-opiskelijat! opiskelijat)
     export))

(defn ^:private get-cell-str [rowref col]
  (let [cell (.getCell rowref col)]
    (when (not (nil? cell)) (.getStringCellValue cell))))
    
(defn ^:private get-cell-num [rowref col]
  (let [cell (.getCell rowref col)]
    (when (not (nil? cell)) (.getNumericCellValue cell))))
    
(defn luo-opiskelijat! [sheet]
  (let [rivit (row-seq sheet)
        opiskelijat (nthrest rivit 1)]
    (doseq [opiskelija opiskelijat]
      (let [id (get-cell-str opiskelija 1)
            nimi (get-cell-str opiskelija 0)
            oid (get-cell-str opiskelija 2)
            hetu (get-cell-str opiskelija 3)
            rahoitusmuoto (get-cell-num opiskelija 5)]
        (when (and (empty? id) (not (empty? nimi)))
;          (println "uusi opiskelija!! " nimi " . " rahoitusmuoto)
          (let [nimet (clojure.string/split nimi #" ")]
            (suorittaja-arkisto/lisaa! {:etunimi (first nimet)
                                        :sukunimi (second nimet)
                                        :hetu hetu
                                        :rahoitusmuoto_id (int rahoitusmuoto)
                                        :oid oid})
                                        ))))))
    
(defn lue-excel! []
  (let [import (load-workbook "tutosat_taydennetty2.xlsx")
        suoritukset-sheet (select-sheet "Suoritukset" import)
        rivit (row-seq suoritukset-sheet)
        suoritukset (nthrest rivit 3)]
    (luo-opiskelijat! (select-sheet "Opiskelijat" import))
    
    (doseq [suoritus suoritukset]
      (let [nimisolu (.getCell suoritus 1)
            nimi (when (not (nil? nimisolu)) (.getStringCellValue nimisolu))]
        (when (not (empty? nimi))
;          (println "..  " nimi " .. ")
          (let [suorittaja-id (.getNumericCellValue (.getCell suoritus 2))]
;            (println ".. " suorittaja-id)))))))
))))))