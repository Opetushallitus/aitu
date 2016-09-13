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
             [clojure.tools.logging :as log]
             [aitu.infra.tutkinnonosa-arkisto :as tutosa-arkisto]
             [aitu.infra.suorittaja-arkisto :as suorittaja-arkisto]
             [aitu.infra.suoritus-arkisto :as suoritus-arkisto]
             [aitu.auditlog :as auditlog]
             [dk.ative.docjure.spreadsheet :refer :all]))

; [t], jossa t [tutkintotunnus (osa1 osa2..)]
; eli vektori, jonka sisällä on vektoreina tutkintotunnus + lista sen osista
; järjestetty tutkinnon nimen mukaan
(defn hae-osarakenne [] ; (juxt :tutkintotunnus :peruste)
  (let [tutkinnot (sort-by (juxt :nimi_fi :peruste :tutkintoversio_id) (tutkinto-arkisto/hae-kaikki-suoritettavat-versiot)) ; TODO: nimi_fi parametroitava
      replacef #(-> %
                    (clojure.string/replace "\n" "")
                    (clojure.string/replace "\u2011" "-")
                    (clojure.string/replace "\u2013" "-"))]
  (into [] 
     (for [tutkinto tutkinnot]
       (let [osat (tutosa-arkisto/hae-versiolla (:tutkintoversio_id tutkinto))]
         [tutkinto (map #(str (replacef (:nimi_fi %)) " (" (:osatunnus %) ")") osat) ; TODO: nimi_fi parametroitava
               ]
         )))))

(defn ^:private set-or-create-cell! 
  ([sheet n val type]
    (let [cellref (org.apache.poi.ss.util.CellReference. n)
          r (.getRow cellref)
          col (int (.getCol cellref))
          row (or (.getRow sheet r) (.createRow sheet r))
          cell (or (select-cell n sheet) (.createCell row col type))]
      (set-cell! cell val)))
  ([sheet n val]
    (set-or-create-cell! sheet n val org.apache.poi.ss.usermodel.Cell/CELL_TYPE_STRING)))

(def osacolumns ["C", "D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"
                 "AA","AB","AC","AD","AE","AF","AG","AH","AI","AJ","AK","AL","AM","AN","AO","AP","AQ","AR","AS","AT","AU","AV","AW","AX","AY","AZ"
                 "BA","BB","BC","BD","BE","BF","BG","BH","BI","BJ","BK","BL","BM","BN","BO","BP","BQ","BR","BS","BT","BU","BV","BW","BX","BY","BZ"
                 "CA","CB","CC","CD","CE","CF","CG","CH","CI","CJ","CK","CL","CM","CN","CO","CP","CQ","CR","CS","CT","CU","CV","CW","CX","CY","CZ"
                 "DA","DB","DC","DD","DE","DF","DG","DH","DI","DJ","DK","DL","DM","DN","DO","DP","DQ","DR","DS","DT","DU","DV","DW","DX","DY","DZ"])

; Tutkinnot tulevat aakkosjärjestykseen nimen mukaan tutkinnot-välilehdelle, jotta niiden valitseminen on käyttäjälle loogista
(defn ^:private map-tutkinnot! [tutkinnot-sheet tut-aakkosjarjestys]
  (doall (map-indexed (fn [i tutkinto]
           (let [tutkinto-map (first tutkinto)
                 tutkintotunnus (:tutkintotunnus tutkinto-map)
                 tutkinto-nimi (:nimi_fi tutkinto-map) ; TODO: nimi_sv, param
                 tutkinto-yhd (str (:tutkintotunnus tutkinto-map) " (" (:peruste tutkinto-map) ")" " (" (:tutkintoversio_id tutkinto-map) ")")
                 row (+ 2 i)]
             (set-or-create-cell! tutkinnot-sheet (str "A" row) (str tutkinto-nimi " " tutkinto-yhd))
             (set-or-create-cell! tutkinnot-sheet (str "B" row) tutkintotunnus)
             (set-or-create-cell! tutkinnot-sheet (str "C" row) (:peruste tutkinto-map))
             (set-or-create-cell! tutkinnot-sheet (str "D" row) (:tutkintoversio_id tutkinto-map))
             (set-or-create-cell! tutkinnot-sheet (str "E" row) tutkinto-nimi)
             )) tut-aakkosjarjestys)))
                          
(defn ^:private map-tutkintorakenne! [tutosat-sheet tutkinnot-sheet]
  (let [tutkintorakenne (hae-osarakenne)
        versio-jarj (sort-by #(:tutkintoversio_id (first %)) tutkintorakenne) ]
    (map-tutkinnot! tutkinnot-sheet tutkintorakenne)
    (doall (map-indexed (fn [i tutkinto]
             (let [tutkinto-map (first tutkinto)
                   tutkinnonosat (second tutkinto)
                   row (+ 2 i)]

               (set-or-create-cell! tutosat-sheet (str "A" row) (:tutkintoversio_id tutkinto-map))
               (doall (map-indexed (fn [ind tutkinnonosa]
                                     (let [colstr (nth osacolumns ind)]
                                       (set-or-create-cell! tutosat-sheet (str colstr row) tutkinnonosa)))
                                   tutkinnonosat))))
                        versio-jarj))))
      
(defn ^:private map-opiskelijat! [sheet]
  (let [suorittajat (->>  (suorittaja-arkisto/hae-kaikki)
           (map #(assoc % :nimi (str (:etunimi %) " " (:sukunimi %) " (" (:oid %) ")"))  )
           (sort-by :nimi))]
    (doall (map-indexed (fn [r opiskelija]
                          (let [row (+ 3 r)] 
                            (set-or-create-cell! sheet (str "A" row) (:nimi opiskelija))
                            (set-or-create-cell! sheet (str "B" row) (str (:suorittaja_id opiskelija)))
                            (set-or-create-cell! sheet (str "C" row) (:oid opiskelija))
                            (set-or-create-cell! sheet (str "D" row) (:hetu opiskelija))
                            (set-or-create-cell! sheet (str "E" row) (:rahoitusmuoto_nimi opiskelija))
                          )) suorittajat))))
               


(defn ^:private get-cell-str [rowref col]
  (let [cell (.getCell rowref col)]
    (when (not (nil? cell)) (.getStringCellValue cell))))
    
(defn ^:private get-cell-num [rowref col]
  (let [cell (.getCell rowref col)]
    (when (not (nil? cell)) (.getNumericCellValue cell))))
    
(defn ^:private tarkista-opiskelija-tiedot [oid hetu rahoitusmuoto]
  (when (nil? rahoitusmuoto) (throw (IllegalArgumentException. "Rahoitusmuoto ei voi olla tyhjä")))
  (when (and (nil? oid) (nil? hetu)) (throw (IllegalArgumentException. "Hetu tai Oid pitää olla.")))
  (when (and (not (nil? hetu)) (not (= 11 (count hetu)))) (throw (IllegalArgumentException. "Hetun pitää olla 11 merkkiä pitkä."))))

(defn opiskelija-olemassa? [tiedot kaikki-seq]  
  (let [vertailu-kentat [:hetu :oid]
        vrt (select-keys tiedot vertailu-kentat)
        op (filter #(= (select-keys % vertailu-kentat) vrt) kaikki-seq)]
    (if (empty? op) 
      false ; Opiskelijaa ei löytynyt hetulla / oid:lla
      (let [uusi (select-keys tiedot [:etunimi :sukunimi])
            aiempi  (select-keys (first op) [:etunimi :sukunimi]) ]
      (if (= aiempi uusi)
        true ; Opiskelija löytyi, samat nimitiedot
        (throw (IllegalArgumentException. (str "Samalla hetu/oid tunnisteella on eri niminen henkilö. Uusi henkilö : " uusi " ja vanha: " aiempi))))))))
 

(defn ^:private luo-opiskelijat! [sheet]
  (let [rivit (row-seq sheet)
        opiskelijat (nthrest rivit 1)
        db-opiskelijat (suorittaja-arkisto/hae-kaikki)]
    (doseq [opiskelija opiskelijat]
      (let [id (get-cell-str opiskelija 1)
            nimi (get-cell-str opiskelija 0)]
        (when (and (empty? id) (not (empty? nimi)))
          (let [oid (get-cell-str opiskelija 2)
                hetu (get-cell-str opiskelija 3)
                rahoitusmuoto (get-cell-num opiskelija 5)]
            (let [nimet (clojure.string/split nimi #" ")]
              (log/info "käsitellään uusi opiskelija " nimi)
              (tarkista-opiskelija-tiedot oid hetu rahoitusmuoto)
              (let [uusi-opiskelija {:etunimi (first nimet)
                                    :sukunimi (second nimet)
                                    :hetu hetu
                                    :rahoitusmuoto_id (int rahoitusmuoto)
                                    :oid oid}]
                (when (not (opiskelija-olemassa? uusi-opiskelija db-opiskelijat))
                  (suorittaja-arkisto/lisaa! uusi-opiskelija)
                  ; TODO: jos sama opiskelija on kaksi kertaa excelissä, siitä tulee SQL exception
                                            )))))))))

(defn parse-osatunnus [osa]
  (let [start (.lastIndexOf osa "(")
        end (.lastIndexOf osa ")")]
    (when (or (= start -1)
              (= end -1))
      (throw (IllegalArgumentException. (str "Virheellinen osatunnus: " osa))))
    (.substring osa (+ 1 start) end)))
        
(defn parse-boolean [str]
  (let [m {"Kyllä" true
           "Ei" false}
        v (get m str)]
    (when (nil? v)
      (throw (IllegalArgumentException. (str "Virheellinen totuusarvo: " str))))
    v))

; suorittaja :tutkinto :tutkinnonosa :suorituspvm
(defn suoritus-olemassa? [tiedot kaikki-seq]
  (let [vertailu-kentat [:tutkinto :suorittaja :tutkinnonosa]
        vrt (select-keys tiedot vertailu-kentat)
        op (filter #(= (select-keys % vertailu-kentat) vrt) kaikki-seq)]
    (if (empty? op)
      false ; Identtistä Suoritusta ei löytynyt
      (let [tarkista-kentat [:arvosana :osaamisen_tunnustaminen :jarjestelyt :paikka :kieli :koulutustoimija]
            uusi (select-keys tiedot tarkista-kentat)
            aiemmat (filter #(not= (select-keys % tarkista-kentat) uusi) op)]
        (if (empty? aiemmat)
          true ; Aikaisempi kirjaus, samat tiedot
          (throw (IllegalArgumentException. (str "Samanlainen kirjaus suorituksesta löytyi, mutta eri tiedoilla, Uusi: " uusi ".. ja vanhat: " aiemmat))))))))

(defn ^:private tarkista-suorittaja-id [cell]
  (try 
    (int (.getNumericCellValue cell))
    (catch IllegalStateException e
      (throw (IllegalArgumentException. "Suorittaja-id solun arvoa ei voitu tulkita.")))))


(defn ^:private luo-suoritukset! [sheet]
  (let [rivit (row-seq sheet)
        suoritukset (nthrest rivit 4)
        suorittajamap (group-by :suorittaja_id (suorittaja-arkisto/hae-kaikki))
        osamap (group-by :osatunnus (tutosa-arkisto/hae-kaikki-uusimmat))]
 
    ; TODO: poista duplikaatit, jotta ei luoda samoja rivejä uudelleen tietokantaan jos sama tiedosto importataan kaksi kertaa
    (doseq [suoritus suoritukset]
      (let [nimisolu (.getCell suoritus 1)
            nimi (when (not (nil? nimisolu)) (.getStringCellValue nimisolu))]
        (when (not (empty? nimi))
          (log/info ".. " nimi)
          (let [suorittaja-id (tarkista-suorittaja-id (.getCell suoritus 2))
                tutkintotunnus (.getStringCellValue (.getCell suoritus 4))
                osatunnus (parse-osatunnus (.getStringCellValue (.getCell suoritus 5)))
                ; pvm
                ; suoritus-alkupvm 
                arvosana (int (.getNumericCellValue (.getCell suoritus 7)))
                todistus (parse-boolean (.getStringCellValue (.getCell suoritus 8)))
                suorituskerta-map {:suorittaja suorittaja-id
                                   :rahoitusmuoto (:rahoitusmuoto_id (first (get suorittajamap suorittaja-id)))
                                   :tutkinto tutkintotunnus
                                   :opiskelijavuosi 1 ; TODO
                                   :koulutustoimija "1060155-5"; !!  TODO
                                   :suoritusaika_alku "2016-09-01" ; !! TODO
                                   :suoritusaika_loppu "2016-09-01" ; !! TODO
                                   :jarjestamismuoto "oppilaitosmuotoinen" ; TODO oppilaitosmuotoinen'::character varying, 'oppisopimuskoulutus
                                   }
                suoritus-map {:suorittaja_id suorittaja-id
                              :arvosana arvosana
                              :todistus todistus
                              :tutkinnonosa (:tutkinnonosa_id (first (get osamap osatunnus)))
                              :arvosanan_korotus false ; TODO !! 
                              :osaamisen_tunnustaminen false ; TODO !!
                              :kieli "fi" ; TODO !! 
                              }
                suoritus-full (merge suorituskerta-map
                                     {:osat [suoritus-map]})]
            
            (log/info "Lisätään suorituskerta .." suorituskerta-map)
            (log/info "Lisätään suoritus .." suoritus-map)
            (suoritus-arkisto/lisaa! suoritus-full)
      ))))))

;(user/with-testikayttaja 
;     (let [wb (load-workbook "tutosat_taydennetty2.xlsx")]
;       (lue-excel! wb)))

(defn lue-excel! [excel-wb]
  (auditlog/lue-suoritukset-excel!)
  (let [suoritukset-sheet (select-sheet "Suoritukset" excel-wb)]
    
    (log/info "Käsitellään opiskelijat")
    (luo-opiskelijat! (select-sheet "Opiskelijat" excel-wb))
    (log/info "Opiskelijat luettu")
    
    (log/info "Käsitellään suoritukset")
    (luo-suoritukset! suoritukset-sheet)
    (log/info "Suoritukset käsitelty")))

; TODO: 
;(user/with-testikayttaja 
;     (let [wb (luo-excel)]
;       (save-workbook! "tutosat_taydennetty.xlsx" wb)))
  
(defn luo-excel []
  (let [export (load-workbook  "resources/tutosat_export_base.xlsx")
        tutosat (select-sheet "tutkinnonosat" export)
        tutkinnot (select-sheet "tutkinnot" export)
        opiskelijat (select-sheet "Opiskelijat" export)]
     (map-tutkintorakenne! tutosat tutkinnot)
    (map-opiskelijat! opiskelijat)
     export))