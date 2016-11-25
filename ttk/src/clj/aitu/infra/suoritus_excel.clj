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
             [aitu.infra.arvioija-arkisto :as arvioija-arkisto]
             [aitu.infra.koulutustoimija-arkisto :as koulutustoimija-arkisto]
             [aitu.auditlog :as auditlog]
             [dk.ative.docjure.spreadsheet :refer :all]
             [sade.validators :as sade-validators]))

(defn ^:private parse-kieli [kieli]
  (get {"Suomi" "fi"
        "Ruotsi" "sv"
        "Saame" "se"
        "Englanti" "en"}
       kieli))

(defn ^:private get-cell-str [^org.apache.poi.ss.usermodel.Row rowref col]
  (let [cell (.getCell rowref col)]
    (when (not (nil? cell)) (.getStringCellValue cell))))
    
(defn ^:private get-cell-num [^org.apache.poi.ss.usermodel.Row rowref col]
  (let [cell (.getCell rowref col)]
    (when (not (nil? cell)) (.getNumericCellValue cell))))  
        
(defn excel->boolean [s]
  (let [m {"Kyllä" true
           "Ei" false}
        v (get m s)]
    (when (nil? v)
      (throw (IllegalArgumentException. (str "Virheellinen totuusarvo: " s))))
    v))

; TODO: clojuren idiomi mallintaa bijektio? tämä on kömpelöä tarpeettomasti

(defn bool->excel [b]
  (get {true "Kyllä"
        false "Ei"}
       b))

(defn ^:private edustus->excel [rooli]
  (let [m {"tyontekija" "työntekijät"
           "tyonantaja" "työnantajat"
           "opettaja" "opetusala"
           "itsenainen" "itsenäiset ammatinharjoittajat"}
        v (get m rooli)]
    (when (nil? v)
      (throw (IllegalArgumentException. (str "Virheellinen edustettava taho: " rooli))))
    v))

(defn ^:private excel->edustus [edustus]
  (let [m {"työntekijät" "tyontekija"
           "työnantajat"  "tyonantaja"
           "opetusala" "opettaja"
           "itsenäiset ammatinharjoittajat" "itsenainen"}
        v (get m edustus)]
    (when (nil? v)
      (throw (IllegalArgumentException. (str "Virheellinen edustettava taho: " edustus))))
    v))
  
(defn ^:private date->LocalDate [date]
  (when (not (nil? date))
    (org.joda.time.LocalDate. date)))

(defn ^:private date->iso-date [date]
  (when (not (nil? date))
    (let [dformat (java.text.SimpleDateFormat. "yyyy-MM-dd")]
      (.format dformat date))))

(defn ^:private excel->arvosana [excel-arvosana]
  (let [m {"Hyväksytty" "hyvaksytty"
           "1"  "1"
           "2" "2"
           "3" "3"
           "1.0" "1"
           "2.0" "2"
           "3.0" "3"}
        v (get m excel-arvosana)]
    (when (nil? v)
      (throw (IllegalArgumentException. (str "Virheellinen arvosana: " excel-arvosana))))
    v))

(defn ^:private get-excel-arvosana 
  "Tulkitaan monimutkaisesti, koska Excelin tyyppijärjestelmä ei osaa päättää onko solu tekstiä vai ei. Joskus on, joskus ei ole, vaikka miten sanoisi Format Cell -> Text"
  [^org.apache.poi.ss.usermodel.Row rowref col]
  (let [cell (.getCell rowref col)]
    (when (not (nil? cell))
      (if (= (.getCellType cell) org.apache.poi.ss.usermodel.Cell/CELL_TYPE_NUMERIC)
        (excel->arvosana (str (.getNumericCellValue cell)))
        (excel->arvosana (.getStringCellValue cell))))))
                 
(defn ^:private get-excel-tutperuste
  "Tulkitaan monimutkaisesti, koska Excelin tyyppijärjestelmä ei osaa päättää onko solu tekstiä vai ei. Joskus on, joskus ei ole, vaikka miten sanoisi Format Cell -> Text"
  [^org.apache.poi.ss.usermodel.Row rowref col]
  (let [cell (.getCell rowref col)]
    (when (not (nil? cell))
      (let [id (if (or (= (.getCellType cell) org.apache.poi.ss.usermodel.Cell/CELL_TYPE_NUMERIC)
                       (= (.getCellType cell) org.apache.poi.ss.usermodel.Cell/CELL_TYPE_FORMULA))
                 (int (.getNumericCellValue cell))
                 (Integer/parseInt (.getStringCellValue cell)))]
        (if (= 0 id)
          nil
          id)))))

; [t], jossa t [tutkintotunnus (osa1 osa2..)]
; eli vektori, jonka sisällä on vektoreina tutkintotunnus + lista sen osista
; järjestetty tutkinnon nimen mukaan
(defn hae-osarakenne [kieli] ; (juxt :tutkintotunnus :peruste)
  
  (let [rakenne (tutkinto-arkisto/hae-kaikki-suoritettavat-versiot)
        tutkinnot (if (= "fi" kieli)
                    (sort-by (juxt :nimi_fi :peruste :tutkintoversio_id) rakenne)
                    (sort-by (juxt :nimi_sv :peruste :tutkintoversio_id) rakenne))
      replacef #(-> %
                    (clojure.string/replace "\n" "")
                    (clojure.string/replace "\u2011" "-")
                    (clojure.string/replace "\u2013" "-"))
      nimifn #(if (= "fi" kieli) (:nimi_fi %) (or (:nimi_sv %) (:nimi_fi %))) ]
  (into [] 
     (for [tutkinto tutkinnot]
       (let [osat (sort-by nimifn (tutosa-arkisto/hae-versiolla (:tutkintoversio_id tutkinto)))]
         [tutkinto (map #(str (replacef (nimifn %)) " (" (:osatunnus %) ")") osat)]
         )))))

(defn ^:private set-or-create-cell! 
  ([^org.apache.poi.ss.usermodel.Sheet sheet ^String n val type]
    (let [cellref (org.apache.poi.ss.util.CellReference. n)
          r (.getRow cellref)
          col (int (.getCol cellref))
          ^org.apache.poi.ss.usermodel.Row
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
(defn ^:private map-tutkinnot!
  [tutkinnot-sheet tut-aakkosjarjestys kieli]
  (doall (map-indexed (fn [i tutkinto]
                        (let [tutkinto-map (first tutkinto)
                              tutkintotunnus (:tutkintotunnus tutkinto-map)
                              tutkinto-nimi (if (= "fi" kieli) (:nimi_fi tutkinto-map) (:nimi_sv tutkinto-map))
                              tutkinto-yhd (str (:tutkintotunnus tutkinto-map) " (" (:peruste tutkinto-map) ")" " (" (:tutkintoversio_id tutkinto-map) ")")
                              row (+ 2 i)]
                          (set-or-create-cell! tutkinnot-sheet (str "A" row) (str tutkinto-nimi " " tutkinto-yhd))
                          (set-or-create-cell! tutkinnot-sheet (str "B" row) tutkintotunnus)
                          (set-or-create-cell! tutkinnot-sheet (str "C" row) (:peruste tutkinto-map))
                          (set-or-create-cell! tutkinnot-sheet (str "D" row) (:tutkintoversio_id tutkinto-map))
                          (set-or-create-cell! tutkinnot-sheet (str "E" row) tutkinto-nimi)))
                      tut-aakkosjarjestys)))

(defn ^:private map-osaamisalat!
  [oalat-sheet kieli]
  (let [tutkinnot (tutkinto-arkisto/hae-tutkintoversiot-ja-osaamisalat)]
    (doall (map-indexed (fn [i tutkinto]
                          (let [nimi-fn (fn [o] (if (= "fi" kieli) (:nimi_fi o) (or (:nimi_sv o) (:nimi_fi o))))
                                osaamisalat (sort-by nimi-fn (:osaamisala tutkinto))
                                tutkintoversio (:tutkintoversio_id tutkinto)
                                row (+ 2 i)]
                            (set-or-create-cell! oalat-sheet (str "A" row) tutkintoversio)
                            (doall (map-indexed (fn [ind osaamisala]
                                                  (let [colstr (nth osacolumns ind)]
                                                    (set-or-create-cell! oalat-sheet (str colstr row) (str (nimi-fn osaamisala) " (" (:osaamisala_id osaamisala) ")"))))
                                                osaamisalat))))
                        tutkinnot))))
                              
(defn ^:private map-tutkintorakenne!
  ([tutosat-sheet tutkinnot-sheet kieli]
   (let [tutkintorakenne (hae-osarakenne kieli)
         versio-jarj (sort-by #(:tutkintoversio_id (first %)) tutkintorakenne)]
     (map-tutkinnot! tutkinnot-sheet tutkintorakenne kieli)
     (doall (map-indexed (fn [i tutkinto]
                           (let [tutkinto-map (first tutkinto)
                                 tutkinnonosat (second tutkinto)
                                 row (+ 2 i)]
                             (set-or-create-cell! tutosat-sheet (str "A" row) (:tutkintoversio_id tutkinto-map))
                             ; rivi jää kokonaan tyhjäksi jos tutkintoon ei kuulu tutkinnonosia
                             (doall (map-indexed (fn [ind tutkinnonosa]
                                                   (let [colstr (nth osacolumns ind)]
                                                     (set-or-create-cell! tutosat-sheet (str colstr row) tutkinnonosa)))
                                                 tutkinnonosat))))
                         versio-jarj))))
  ([tutosat-sheet tutkinnot-sheet]
   (map-tutkintorakenne! tutosat-sheet tutkinnot-sheet "fi")))


(defn ^:private map-arvioijat! [sheet]
  (let [arvioijat (arvioija-arkisto/hae-kaikki)]
    (doall (map-indexed (fn [r arvioija]
                          (let [row (+ 3 r)]
                            (set-or-create-cell! sheet (str "B" row) (:nimi arvioija))
                            (set-or-create-cell! sheet (str "C" row) (edustus->excel (:rooli arvioija)))
                            (set-or-create-cell! sheet (str "D" row) (bool->excel (:nayttotutkintomestari arvioija)))))
                        arvioijat))))

(defn ^:private hae-arvioija-id
  [nimi excel-arvioijat db-arvioijat]
  (let [a (first (filter #(= nimi  (str (:etunimi %) " " (:sukunimi %))) excel-arvioijat))
        a-db (first (filter #(= a (select-keys % [:etunimi :sukunimi :nayttotutkintomestari :rooli])) db-arvioijat))]
    (:arvioija_id a-db))) 
    
(defn ^:private arvioijatiedot
  "Tulkitsee arvioijalistan Excelistä ja palauttaa joukkona arvioijat"
  [sheet ui-log]
  (let [rivi (atom 3) ; Excelissä rivi 3 on ensimmäinen rivi.
        rivit (row-seq sheet)
        arvioijat (nthrest rivit 1)
        excel-arvioijat (atom #{})]
    (try
      (doseq [arvioija arvioijat]
        (let [sukunimi (get-cell-str arvioija 1)
              etunimi (get-cell-str arvioija 0)]
          (when (not (empty? sukunimi))
            (let [rooli (excel->edustus (get-cell-str arvioija 2))
                  ntm (excel->boolean (get-cell-str arvioija 3))
                  uusi-arvioija {:etunimi               etunimi
                                 :sukunimi              sukunimi
                                 :rooli                 rooli
                                 :nayttotutkintomestari ntm}]
              (swap! excel-arvioijat #(conj % uusi-arvioija)))))
        (swap! rivi inc))
      (catch Exception e
        (swap! ui-log conj (str "Poikkeus arvioijien käsittelyssä. Rivi: " @rivi ". Tarkista solujen sisältö: " e))
        (throw e)))
    @excel-arvioijat))

; TODO: refaktoroi
(defn ^:private luo-arvioijat!
  "Luo tietokantaan ne arvioijat excelistä, jotka ovat uusia."
  [sheet ui-log]
  (let [rivi (atom 3) ; Excelissä rivi 3 on ensimmäinen rivi.
        rivit (row-seq sheet)
        arvioijat (nthrest rivit 1)
        db-arvioijat (set (map #(select-keys % [:etunimi :sukunimi :rooli :nayttotutkintomestari]) (arvioija-arkisto/hae-kaikki)))]
    (try
      (doseq [arvioija arvioijat]
        (let [sukunimi (get-cell-str arvioija 1)
              etunimi (get-cell-str arvioija 0)]
          (when (not (empty? sukunimi))
            (let [rooli (excel->edustus (get-cell-str arvioija 2))
                  ntm (excel->boolean (get-cell-str arvioija 3))
                  uusi-arvioija {:etunimi               etunimi
                                 :sukunimi              sukunimi
                                 :rooli                 rooli
                                 :nayttotutkintomestari ntm}]
              (if (contains? db-arvioijat uusi-arvioija)
                (swap! ui-log conj (str "Arvioija on jo olemassa tietokannassa (" sukunimi "," etunimi ")"))
                (do
                  (log/info (str "Lisätään uusi arvioija (" sukunimi "," etunimi ")"))
                  (swap! ui-log conj (str "Lisätään uusi arvioija (" sukunimi "," etunimi ")"))
                  (arvioija-arkisto/lisaa! uusi-arvioija))))))
        (swap! rivi inc))
      (catch Exception e
        (swap! ui-log conj (str "Poikkeus arvioijien käsittelyssä. Rivi: " @rivi ". Tarkista solujen sisältö: " e))
        (throw e)))))
    
  

(defn ^:private map-opiskelijat! [sheet]
  (let [suorittajat (->> (suorittaja-arkisto/hae-kaikki)
                         (map #(assoc % :nimi (str (:etunimi %) " " (:sukunimi %) " (" (:oid %) ")")))
                         (sort-by :nimi))]
    (doall (map-indexed (fn [r opiskelija]
                          (let [row (+ 3 r)]
                            (set-or-create-cell! sheet (str "A" row) (str (:sukunimi opiskelija) " " (:etunimi opiskelija) " (" (:oid opiskelija) ")")) ; Excel-concatenate ei toimi jostain syystä..
                            (set-or-create-cell! sheet (str "B" row) (:sukunimi opiskelija))
                            (set-or-create-cell! sheet (str "C" row) (:etunimi opiskelija))
                            (set-or-create-cell! sheet (str "D" row) (str (:suorittaja_id opiskelija)))
                            (set-or-create-cell! sheet (str "E" row) (:oid opiskelija))
                            (set-or-create-cell! sheet (str "F" row) (:hetu opiskelija))
                            (set-or-create-cell! sheet (str "G" row) (:rahoitusmuoto_nimi opiskelija))))
                        suorittajat))))
               


(defn ^:private tarkista-opiskelija-tiedot [oid hetu]
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
 
; palauttaa vektorin, jossa on käyttäjälle logia siitä mitä tehtiin
(defn ^:private luo-opiskelijat! [sheet ui-log]
  (let [rivi (atom 3) ; Käyttäjän näkökulmasta Excelin ensimmäinen tietorivi on rivi 3
        rivit (row-seq sheet)
        opiskelijat (nthrest rivit 1)
        db-opiskelijat (suorittaja-arkisto/hae-kaikki)]
    (try
      (doseq [opiskelija opiskelijat]
        (let [sukunimi (get-cell-str opiskelija 1)
              etunimi (get-cell-str opiskelija 2)
              id (get-cell-str opiskelija 3)]
          (when (and (empty? id) (not (empty? sukunimi)))
            (log/info (str "käsitellään uusi opiskelija " etunimi " " sukunimi))
            (swap! ui-log conj (str "Käsitellään uusi opiskelija " etunimi " " sukunimi))
            (let [oid (get-cell-str opiskelija 4)
                  hetu (get-cell-str opiskelija 5)]
              (tarkista-opiskelija-tiedot oid hetu)
              (let [uusi-opiskelija {:etunimi          etunimi
                                     :sukunimi         sukunimi
                                     :hetu             hetu
                                     :oid              oid}]
                (when (not (opiskelija-olemassa? uusi-opiskelija db-opiskelijat))
                  (if (and (:hetu uusi-opiskelija) (not (sade-validators/valid-hetu? (:hetu uusi-opiskelija))))
                    (swap! ui-log conj (str "Henkilötunnus on viallinen : " (:hetu uusi-opiskelija)))
                    (do
                      (swap! ui-log conj (str "Lisättiin opiskelija " etunimi " " sukunimi))
                      ; TODO: jos sama opiskelija on kaksi kertaa excelissä, siitä tulee SQL exception
                      (suorittaja-arkisto/lisaa! uusi-opiskelija))))))))
        (swap! rivi inc))
      (catch Exception e
        (swap! ui-log conj (str "Poikkeus opiskelijoiden käsittelyssä. Rivi: " @rivi ". Tarkista solujen sisältö: " e))
        (throw e)))))

(defn parse-osatunnus [osa]
  (let [start (.lastIndexOf osa "(")
        end (.lastIndexOf osa ")")]
    (when (or (= start -1)
              (= end -1))
      (throw (IllegalArgumentException. (str "Virheellinen osatunnus: " osa))))
    (.substring osa (+ 1 start) end)))

; kuten osatunnus, mutta voi olla tyhjä. Usein onkin
(defn parse-osaamisala [osaamisala]
  (if (nil? osaamisala) 
    nil
    (let [start (.lastIndexOf osaamisala "(")
          end (.lastIndexOf osaamisala ")")]
      (when (or (= start -1)
                (= end -1))
        (throw (IllegalArgumentException. (str "Virheellinen osaamisala: " osaamisala))))
      (let [idstr (.substring osaamisala (+ 1 start) end)]
        (if (= "" idstr) nil
          (Long/parseLong idstr))))))

(defn ^:private tarkista-suorittaja-id [^org.apache.poi.ss.usermodel.Cell cell]
  (try 
    (int (.getNumericCellValue cell))
    (catch IllegalStateException e
      (throw (IllegalArgumentException. "Suorittaja-id solun arvoa ei voitu tulkita.")))))

(defmacro prosessoi [sym items & body]
  `(loop [i# 0
          [~sym & items#] ~items]
     (when ~sym
       (try
         ~@body
         (catch Throwable t#
           (log/error t# "Virhe prosessoinnissa rivillä " i# ", tiedot: " ~sym)
           (throw (ex-info (str "Virhe rivillä " i#)
                           {:data ~sym
                            :exception t#}))))
       (recur (inc i#)
              items#))))
    

(defn ^:private suoritus-kentat [suoritus]
 (select-keys suoritus [:suorittaja :tutkinto :tutkinnonosa :koulutustoimija 
                                   :osaamisen_tunnustaminen :arvosanan_korotus; :jarjestelyt 
                                   ;:paikka 
                                   :arvosana :rahoitusmuoto
                                   :suoritusaika_alku :suoritusaika_loppu
                                   :kieli 
                                   :osaamisala
                                   :todistus
                                   :suorituskerta_id
                                   ; :valmistava_koulutus 
                                   ;:arvointikokouksen_pvm
                                   ]))
  
(defn ^:private hae-suoritukset [jarjestaja]
  (set (map #(suoritus-kentat %)
                             (suoritus-arkisto/hae-kaikki-suoritukset jarjestaja))))

(defn ^:private hae-suoritus 
  "Etsi suoritus annetusta joukosta annettujen avainten perusteella."
  [suoritus-set suoritus keyseq]
  (let [m 
        (-> (select-keys suoritus keyseq)
          (update :suoritusaika_alku date->LocalDate)
          (update :suoritusaika_loppu date->LocalDate))]
    (filter #(= (select-keys % keyseq) m) suoritus-set)))  
  
(defn ^:private olemassaoleva-suoritus?
  "Tarkistaa onko suoritus jo annetussa joukossa. Tarkoituksella osa kentistä on vertailun ulkopuolella."
  [suoritus-set suoritus]
  (not (empty? (hae-suoritus suoritus-set suoritus [:suorittaja :tutkinto :tutkinnonosa :koulutustoimija
                                                    :osaamisen_tunnustaminen :arvosanan_korotus ;järjestelyt ; paikka
                                                    :arvosana :rahoitusmuoto
                                                    :suoritusaika_alku :suoritusaika_loppu
                                                    :kieli :osaamisala :todistus]))))
  
(defn ^:private tulkitse-suorittajaid [^org.apache.poi.ss.usermodel.Cell id-cell
                                       ^org.apache.poi.ss.usermodel.Cell suorittaja-cell
                                       suorittajamap
                                       suorittajat-excelmap]
  (let [id (tarkista-suorittaja-id id-cell)
        id-str (.getStringCellValue suorittaja-cell)]
    (if (= id 0)
      ; excelissä voi olla useampi duplikaattivaihtoehto -> ongelma
      (let [l (get suorittajat-excelmap id-str)]
        (if (= 1 (count l))
          (:suorittaja_id (first l))
          (throw (new IllegalArgumentException (str "Opiskelijaa ei voitu tulkita yksikäsitteisesti: " id-str)))))
      ; helppo case, suorittaja löytyi id:llä
      id)))

(defn ^:private date-or-nil [^org.apache.poi.ss.usermodel.Row rowref colnum]
  (let [c (.getCell rowref colnum)]
    (when (not (nil? c))
      (.getDateCellValue c))))

(defn ^:private versionumero
  [rivit]
  (let [otsikot (nth rivit 3)]
    (reduce #(+ (.hashCode %1) (.hashCode %2)) (map #(.getStringCellValue %) (take 30 otsikot)))))

(defn ^:private onko-vanha-versio? 
  "Palauttaa true jos kyse on 9.11. tai 4.11. 2016 käytössä olleesta excelin versiosta"
  [rivit]
  (contains? #{851295559 612231933} (versionumero rivit)))

(defn ^:private luo-suoritukset-vanha-excel! 
  "Vanhan excel-version kanssa toimiva versio luo-suoritukset funktiosta. TODO: Tämä voidaan poistaa jossain kohtaa kokonaan kun vanha excel on poistunut käytöstä."
  [arvioijatiedot sheet ui-log]
  (let [rivi (atom 1) ; Y-tunnus on rivillä 1.
        solu (atom "Tutkinnon järjestäjän y-tunnus") ; Solureferenssi virheilmoituksiin, jotta käyttäjä saa selvemmin tiedon siitä mikä on vialla.
        ]
    (try
      (let [rivit (row-seq sheet)
            rivi1 (first rivit)
            jarjestaja (get-cell-str rivi1 3)               ; tutkinnon järjestäjän y-tunnus, solu D1
            suoritukset (nthrest rivit 4)
            opiskelijat (suorittaja-arkisto/hae-kaikki)
            suorittajamap (group-by :suorittaja_id opiskelijat)
            suorittajat-excelmap (group-by #(str (:sukunimi %) " " (:etunimi %) "(" (:oid %) ")") opiskelijat) ; funktion pitää matchata excelin kanssa tässä
            osamap (group-by #(select-keys % [:osatunnus :tutkintoversio]) (tutosa-arkisto/hae-kaikki))
            suoritukset-alussa (hae-suoritukset jarjestaja) ; Duplikaattirivejä verrataan näihin
            db-arvioijat (set (map #(select-keys % [:arvioija_id :etunimi :sukunimi :rooli :nayttotutkintomestari]) (arvioija-arkisto/hae-kaikki)))
            _ (reset! rivi 5)                               ; Käyttäjän näkökulmasta ensimmäinen tietorivi on rivi 5 Excelissä.
            ]
        (doseq [^org.apache.poi.ss.usermodel.Row suoritus suoritukset]
          (reset! solu "suorittajan nimi")
          (let [nimisolu (.getCell suoritus 1)
                nimi (when (not (nil? nimisolu)) (.getStringCellValue nimisolu))]
            (when (not (empty? nimi))
              (log/info ".. " nimi)
              (swap! ui-log conj (str "Käsitellään suoritus opiskelijalle " nimi))
              (let [suorittaja-id (tulkitse-suorittajaid (.getCell suoritus 2)
                                                         (.getCell suoritus 1)
                                                         suorittajamap
                                                         suorittajat-excelmap)
                    _ (reset! solu "tutkintotunnus")
                    tutkintotunnus (get-cell-str suoritus 4)
                    tutkintoversio (get-excel-tutperuste suoritus 23)
                    _ (reset! solu "osaamisala") 
                    osaamisala-id (parse-osaamisala (get-cell-str suoritus 5))
                    _ (reset! solu "tutkinnon osa")
                    osatunnus (parse-osatunnus (get-cell-str suoritus 6))
                    _ (reset! solu "tunnustamisen pvm")
                    tunnustamisen-pvm (date-or-nil suoritus 7) ; voi olla tyhjä
                    _ (reset! solu "tutkintotilaisuus, alkupvm") ; voi olla tyhjä jos osaamisen tunnustaminen
                    suoritus-alkupvm (date-or-nil suoritus 8)
                    _ (reset! solu "tutkintotilaisuus, loppupvm")
                    suoritus-loppupvm (date-or-nil suoritus 9) ; voi olla tyhjä jos osaamisen tunnustaminen
                    _ (reset! solu "paikka")
                    paikka (get-cell-str suoritus 10)
                    _ (reset! solu "järjestelyt/työtehtävät")
                    jarjestelyt (get-cell-str suoritus 11)
                    _ (reset! solu "arviointikokous pvm")
                    arviointikokous-pvm (date-or-nil suoritus 12) ; voi olla tyhjä jos osaamisen tunnustaminen
                    _ (reset! solu "arvosana")
                    arvosana (get-excel-arvosana suoritus 13)
                    _ (reset! solu "todistus")
                    todistus-valinta (get-cell-str suoritus 14)
                    koko-tutkinto (= "Koko tutkinto" todistus-valinta)
                    todistus (or koko-tutkinto (excel->boolean todistus-valinta)) ; koko tutkinnon suoritus = aina todistus
                    _ (reset! solu "suorituskieli")
                    suorituskieli (get-cell-str suoritus 15)
                    _ (reset! solu "arvosanan korotus")
                    korotus (excel->boolean (get-cell-str suoritus 16))
                  
                    _ (reset! solu "arvioija1")
                    arvioija1 (get-cell-str suoritus 18)
                    _ (reset! solu "arvioija2")
                    arvioija2 (get-cell-str suoritus 19)
                    _ (reset! solu "arvioija3")
                    arvioija3 (get-cell-str suoritus 20)
                    
                    a1 (hae-arvioija-id arvioija1 arvioijatiedot db-arvioijat)
                    a2 (hae-arvioija-id arvioija2 arvioijatiedot db-arvioijat)
                    a3 (hae-arvioija-id arvioija3 arvioijatiedot db-arvioijat)

                    vastuutoimikunta (:toimikunta (suoritus-arkisto/hae-vastuutoimikunta tutkintotunnus (parse-kieli suorituskieli)))
                    suorituskerta-map {:suorittaja suorittaja-id
                                       :rahoitusmuoto (:rahoitusmuoto_id (first (get suorittajamap suorittaja-id)))
                                       :tutkinto tutkintotunnus
                                       :tutkintoversio_id tutkintoversio
                                       :paikka paikka
                                       :toimikunta vastuutoimikunta
                                       :arviointikokouksen_pvm (date->iso-date arviointikokous-pvm)
                                       :jarjestelyt jarjestelyt
                                       :opiskelijavuosi 1 ; TODO
                                       :koulutustoimija jarjestaja
                                       :suoritusaika_alku (date->iso-date suoritus-alkupvm)
                                       :suoritusaika_loppu (date->iso-date suoritus-loppupvm)
                                       :jarjestamismuoto "oppilaitosmuotoinen" ; TODO oppilaitosmuotoinen'::character varying, 'oppisopimuskoulutus
                                       :arvioijat (filter #(not (nil? (:arvioija_id %))) (list {:arvioija_id a1} {:arvioija_id a2} {:arvioija_id a3}))
                                       }
                    suoritus-map {:suorittaja_id suorittaja-id
                                  :arvosana arvosana
                                  :todistus todistus
                                  :osaamisala osaamisala-id
                                  :kokotutkinto koko-tutkinto
                                  :tutkinnonosa (:tutkinnonosa_id (first (get osamap {:osatunnus osatunnus :tutkintoversio tutkintoversio}))) 
                                  :arvosanan_korotus korotus
                                  :osaamisen_tunnustaminen (date->iso-date tunnustamisen-pvm) 
                                  :kieli (parse-kieli suorituskieli)
                                  }
                    suoritus-full (merge suorituskerta-map
                                         {:osat [suoritus-map]})]
                (log/info "suoritus.. " suorituskerta-map)
              
                (if (olemassaoleva-suoritus? suoritukset-alussa (merge suorituskerta-map suoritus-map))
                  (do
                   (swap! ui-log conj (str "Ohitetaan suoritus, on jo tietokannassa: " nimi " " (:nimi_fi (first (get osamap osatunnus)))))
                   (log/info "ohitetaan duplikaatti suoritus"))
                  (do
                    (log/info "Lisätään suorituskerta .." suorituskerta-map)
                    (log/info "Lisätään suoritus .." suoritus-map)
                    (swap! ui-log conj (str "Lisätään suoritus: " nimi " " (:nimi_fi (first (get osamap {:osatunnus osatunnus :tutkintoversio tutkintoversio})))))
                    (suoritus-arkisto/lisaa! suoritus-full))))))
          (swap! rivi inc)))
      (catch Exception e
        (swap! ui-log conj (str "Poikkeus suoritusten käsittelyssä, rivi: " @rivi " . Tieto: " @solu " . Tarkista solujen sisältö: " e))
        (throw e)))))

(defn ^:private kirjaa-loki! [ui-log lev & args]
  {:pre [(contains? #{:info :error :fatal} lev)]}
  (let [msg (apply str args)]
    (log/info msg)
    (swap! ui-log conj [lev msg])))

; palauttaa vektorin, jossa on käyttäjälle logia siitä mitä tehtiin
(defn ^:private luo-suoritukset! [arvioijatiedot sheet ui-log]
  (let [rivi (atom 1) ; Y-tunnus on rivillä 1.
        solu (atom "Tutkinnon järjestäjän y-tunnus") ; Solureferenssi virheilmoituksiin, jotta käyttäjä saa selvemmin tiedon siitä mikä on vialla.
        import-log (atom [])
        rivicount (atom 0) ; suoritusrivejä
        suorituscount (atom 0) ; kirjattuja suoritusrivejä
        ]
    (try
      (let [rivit (row-seq sheet)
            rivi1 (first rivit)
            jarjestaja (get-cell-str rivi1 3)               ; tutkinnon järjestäjän y-tunnus, solu D1
            suoritukset (nthrest rivit 4)
            opiskelijat (suorittaja-arkisto/hae-kaikki)
            suorittajamap (group-by :suorittaja_id opiskelijat)
            suorittajat-excelmap (group-by #(str (:sukunimi %) " " (:etunimi %) "(" (:oid %) ")") opiskelijat) ; funktion pitää matchata excelin kanssa tässä
            osamap (group-by #(select-keys % [:osatunnus :tutkintoversio]) (tutosa-arkisto/hae-kaikki))
            suoritukset-set (atom (hae-suoritukset jarjestaja)) ; Duplikaattirivejä verrataan näihin
            db-arvioijat (set (map #(select-keys % [:arvioija_id :etunimi :sukunimi :rooli :nayttotutkintomestari]) (arvioija-arkisto/hae-kaikki)))
            _ (reset! rivi 5)                               ; Käyttäjän näkökulmasta ensimmäinen tietorivi on rivi 5 Excelissä.
            ]
        (when (nil? (koulutustoimija-arkisto/hae-tiedot jarjestaja))
          (throw (new IllegalArgumentException (str "Koulutuksen järjestäjää ei löydy y-tunnuksella " jarjestaja))))

        ; versiotarkistus on vasta tässä tarkoituksella, jotta saadaan vanhallekin versiolle koulutuksen järjestäjän olemassaolotarkistus 
        ; Tämä voidaan poistaa kun vanha versio on poistunut kentältä käytöstä.
        (log/info "Excel versionumero " (versionumero rivit))
        (if (onko-vanha-versio? rivit)
          (do
            (kirjaa-loki! import-log :info "Vanha excel-versio tunnistettu")           
            (luo-suoritukset-vanha-excel! arvioijatiedot sheet ui-log))
          (do                  
            (doseq [^org.apache.poi.ss.usermodel.Row suoritus suoritukset]
              (reset! solu "suorittajan nimi")
              (let [nimisolu (.getCell suoritus 1)
                    nimi (when (not (nil? nimisolu)) (.getStringCellValue nimisolu))]
                (when (not (empty? nimi))
                  (swap! rivicount inc)
                  (kirjaa-loki! import-log :info "Käsitellään suoritus opiskelijalle ")
                  (let [suorittaja-id (tulkitse-suorittajaid (.getCell suoritus 2)
                                                             (.getCell suoritus 1)
                                                             suorittajamap
                                                             suorittajat-excelmap)
                        _ (reset! solu "tutkintotunnus")
                        tutkintotunnus (get-cell-str suoritus 4)
                        tutkintoversio (get-excel-tutperuste suoritus 26)
                        _ (reset! solu "kohdistuva/suoritettava tutkinto")
                        tutkintoversio-suoritettava (or (get-excel-tutperuste suoritus 28) tutkintoversio)
                        _ (reset! solu "osaamisala") 
                        osaamisala-id (parse-osaamisala (get-cell-str suoritus 5))
                        _ (reset! solu "tutkinnon osa")
                        osatunnus (parse-osatunnus (get-cell-str suoritus 6))
                        _ (reset! solu "liittämisen pvm")
                        liittamisen-pvm (date-or-nil suoritus 8)
                        _ (reset! solu "tunnustamisen pvm")
                        tunnustamisen-pvm (date-or-nil suoritus 9) ; voi olla tyhjä
                        _ (reset! solu "tutkintotilaisuus, alkupvm") ; voi olla tyhjä jos osaamisen tunnustaminen
                        suoritus-alkupvm (date-or-nil suoritus 10)
                        _ (reset! solu "tutkintotilaisuus, loppupvm")
                        suoritus-loppupvm (date-or-nil suoritus 11) ; voi olla tyhjä jos osaamisen tunnustaminen
                        _ (reset! solu "paikka")
                        paikka (get-cell-str suoritus 12)
                        _ (reset! solu "järjestelyt/työtehtävät")
                        jarjestelyt (get-cell-str suoritus 13)
                        _ (reset! solu "arviointikokous pvm")
                        arviointikokous-pvm (date-or-nil suoritus 14) ; voi olla tyhjä jos osaamisen tunnustaminen
                        _ (reset! solu "arvosana")
                        arvosana (get-excel-arvosana suoritus 15)
                        _ (reset! solu "todistus")
                        todistus-valinta (get-cell-str suoritus 16)
                        koko-tutkinto (= "Koko tutkinto" todistus-valinta)
                        todistus (or koko-tutkinto (excel->boolean todistus-valinta)) ; koko tutkinnon suoritus = aina todistus
                        _ (reset! solu "suorituskieli")
                        suorituskieli (get-cell-str suoritus 17)
                        _ (reset! solu "arvosanan korotus")
                        korotus (excel->boolean (get-cell-str suoritus 18))
                  
                        _ (reset! solu "arvioija1")
                        arvioija1 (get-cell-str suoritus 19)
                        _ (reset! solu "arvioija2")
                        arvioija2 (get-cell-str suoritus 20)
                        _ (reset! solu "arvioija3")
                        arvioija3 (get-cell-str suoritus 21)
                    
                        a1 (hae-arvioija-id arvioija1 arvioijatiedot db-arvioijat)
                        a2 (hae-arvioija-id arvioija2 arvioijatiedot db-arvioijat)
                        a3 (hae-arvioija-id arvioija3 arvioijatiedot db-arvioijat)
                    
                        kouljarjestaja (get-cell-str suoritus 22) ; koulutuksen järjestäjän y-tunnus, solu Wn
                        koulj (koulutustoimija-arkisto/hae-tiedot kouljarjestaja)
                        kouljarj-nimi (get-cell-str suoritus 23)

                        vastuutoimikunta (:toimikunta (suoritus-arkisto/hae-vastuutoimikunta tutkintotunnus (parse-kieli suorituskieli)))
                        suorituskerta-map {:suorittaja suorittaja-id
                                           :rahoitusmuoto (:rahoitusmuoto_id (first (get suorittajamap suorittaja-id)))
                                           :tutkinto tutkintotunnus
                                           :tutkintoversio_id tutkintoversio
                                           :paikka paikka
                                           :toimikunta vastuutoimikunta
                                           :tutkintoversio_suoritettava tutkintoversio-suoritettava
                                           :arviointikokouksen_pvm (date->iso-date arviointikokous-pvm)
                                           :liitetty_pvm (date->iso-date liittamisen-pvm)
                                           :jarjestelyt jarjestelyt
                                           :opiskelijavuosi 1 ; TODO
                                           :koulutustoimija jarjestaja
                                           :kouljarjestaja (:ytunnus koulj) ; nil -> nil
                                           :suoritusaika_alku (date->iso-date suoritus-alkupvm)
                                           :suoritusaika_loppu (date->iso-date suoritus-loppupvm)
                                           :jarjestamismuoto "oppilaitosmuotoinen" ; TODO oppilaitosmuotoinen'::character varying, 'oppisopimuskoulutus
                                           :arvioijat (filter #(not (nil? (:arvioija_id %))) (list {:arvioija_id a1} {:arvioija_id a2} {:arvioija_id a3}))
                                           }
                        suoritus-map {:suorittaja_id suorittaja-id
                                      :arvosana arvosana
                                      :todistus todistus
                                      :osaamisala osaamisala-id
                                      :kokotutkinto koko-tutkinto
                                      :tutkinnonosa (:tutkinnonosa_id (first (get osamap {:osatunnus osatunnus :tutkintoversio tutkintoversio}))) 
                                      :arvosanan_korotus korotus
                                      :osaamisen_tunnustaminen (date->iso-date tunnustamisen-pvm) 
                                      :kieli (parse-kieli suorituskieli)
                                      }
                        suoritus-full (merge suorituskerta-map
                                             {:osat [suoritus-map]})]
                    (log/info "suoritus.. " suorituskerta-map)
                
                    (when (and (not (nil? kouljarjestaja))
                               (nil? koulj))
                      (kirjaa-loki! import-log :error "Tutkinnon järjestäjää ei löydy: " kouljarjestaja " , nimi " kouljarj-nimi))
                
                    ; Suorituksen lisääminen
                    (if (olemassaoleva-suoritus? @suoritukset-set (merge suorituskerta-map suoritus-map))
                      (kirjaa-loki! import-log :info "Ohitetaan suoritus, on jo tietokannassa: " nimi " " (:nimi_fi (first (get osamap osatunnus))))
                      (do
                        (log/info "Lisätään suorituskerta .." suorituskerta-map)
                        (log/info "Lisätään suoritus .." suoritus-map)
                        (kirjaa-loki! import-log :info "Lisätään suoritus: " nimi " " (:nimi_fi (first (get osamap {:osatunnus osatunnus :tutkintoversio tutkintoversio}))))
                        (swap! suoritukset-set conj (suoritus-kentat (merge (suoritus-arkisto/lisaa! suoritus-full) suoritus-map)))
                        (swap! suorituscount inc)))
                
                    ; Suorituksen liittäminen toiseen tutkintoon
                    (if (not (nil? liittamisen-pvm))
                      (if (= tutkintoversio-suoritettava tutkintoversio)
                      (kirjaa-loki! import-log :info "Suoritusta ei voi liittää samaan tutkintoon: " nimi " " (:nimi_fi (first (get osamap osatunnus))))
                      (do
                        (let [aiemmat (hae-suoritus @suoritukset-set (merge suorituskerta-map suoritus-map)
                                                                   [:suorittaja :tutkinto :tutkinnonosa :koulutustoimija
                                                                    :arvosana])
                              aiempi-suoritus (first aiemmat)]
                          (if (nil? aiempi-suoritus)
                            (kirjaa-loki! import-log :error "Ei liitetä tutkintoa, aiempaa suoritusta liittämistä varten ei löydy! " nimi " " (:nimi_fi (first (get osamap osatunnus))))
                            (if (> (count aiemmat) 1)
                              (kirjaa-loki! import-log :error "Ei liitetä tutkintoa, aiempia suorituksia löytyi useita! " nimi " " (:nimi_fi (first (get osamap osatunnus))))
                              (do
                                (kirjaa-loki! import-log :info "Liitetään suoritus: " nimi " " (:nimi_fi (first (get osamap {:osatunnus osatunnus :tutkintoversio tutkintoversio-suoritettava}))))
                                (suoritus-arkisto/liita-suoritus! {:suorituskerta_id (:suorituskerta_id aiempi-suoritus)
                                                                   :tutkintoversio_suoritettava tutkintoversio-suoritettava
                                                                   :liitetty_pvm (date->iso-date liittamisen-pvm)})))))
                      ))))))
              (swap! rivi inc)))))
      (catch Exception e
        (kirjaa-loki! import-log :fatal "Poikkeus suoritusten käsittelyssä, rivi: " @rivi " . Tieto: " @solu " . Tarkista solujen sisältö: " e)
        (throw e))
      (finally
        ; käsitellään lokiviestit
        (swap! ui-log conj "------------------------")
        (swap! ui-log conj (str "Suoritusrivejä " @rivicount " kpl. Suorituksia kirjattu " @suorituscount " kpl."))
        (swap! ui-log conj "------------------------")
        (let [virheet (map second (filter #(= :error (first %)) @import-log))
              lokiviestit (map second @import-log)]
           (doall (map #(swap! ui-log conj %) virheet))
           (swap! ui-log conj "------------------ tarkempi loki ----")
           (doall (map #(swap! ui-log conj %) lokiviestit))))
      )))

;(user/with-testikayttaja 
;     (let [wb (load-workbook "tutosat_taydennetty2.xlsx")]
;       (lue-excel! wb)))

; Palauttaa vektorin, joka sisältää käyttäjälle lokin siitä miten import onnistui
; sisältää myös virheviestin jos tulee poikkeus siksi että tietoa ei voida tulkita sisäänlukemisen yhteydessä.
(defn lue-excel! [excel-wb]
  (auditlog/lue-suoritukset-excel!)
  (let [ui-log (atom [])]
    (try
      (let [suoritukset-sheet (select-sheet "Suoritukset" excel-wb)
          _ (log/info "Käsitellään arvioijat")
          _ (swap! ui-log conj "Käsitellään arvioijat..")
          arvioijatiedot (arvioijatiedot (select-sheet "Arvioijat" excel-wb) ui-log)          
          ui-log-arvioijat (luo-arvioijat! (select-sheet "Arvioijat" excel-wb) ui-log)
          _ (log/info "Käsitellään opiskelijat")
          _ (swap! ui-log conj "Käsitellään opiskelijat..")
          ui-log-opiskelijat (luo-opiskelijat! (select-sheet "Tutkinnon suorittajat" excel-wb) ui-log)
          _ (log/info "Opiskelijat luettu")
          _ (log/info "Käsitellään suoritukset")
          _ (swap! ui-log conj (str "Opiskelijat ok. Käsitellään suoritukset.."))
          ui-log-suoritukset (luo-suoritukset! arvioijatiedot suoritukset-sheet ui-log)
          _ (log/info "Suoritukset käsitelty")
          _ (swap! ui-log conj (str "Suoritukset ok."))]
        @ui-log)
      (catch Exception e
        (log/info "Poikkeus! " e)
        (swap! ui-log conj (str "Odottamaton virhetilanne: " e))
        @ui-log))))

; TODO: 
;(user/with-testikayttaja 
;     (let [wb (luo-excel "fi")]
;       (save-workbook! "tutosat_taydennetty.xlsx" wb)))

(defn luo-excel [kieli]
  (let [export (load-workbook-from-resource "tutosat_export_base.xlsx")
        tutosat (select-sheet "Tutkinnonosat" export)
        tutkinnot (select-sheet "Tutkinnot" export)
        opiskelijat (select-sheet "Tutkinnon suorittajat" export)
        arvioijat (select-sheet "Arvioijat" export)
        osaamisalat (select-sheet "Osaamisalat" export)]
     (map-tutkintorakenne! tutosat tutkinnot kieli)
     (map-osaamisalat! osaamisalat kieli)
     ; HUOM: rivit on kommentoitu pois, koska ne ovat testatessa hyödyllinen juttu, tuotannossa tätä ei haluta!
     ;(map-opiskelijat! opiskelijat)
     ;(map-arvioijat! arvioijat)
     export))