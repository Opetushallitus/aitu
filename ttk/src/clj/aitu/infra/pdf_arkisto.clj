(ns aitu.infra.pdf-arkisto
  (:import (org.apache.pdfbox.pdmodel PDPage
                                      PDDocument)
           org.apache.pdfbox.pdmodel.edit.PDPageContentStream
           org.apache.pdfbox.pdmodel.font.PDTrueTypeFont
           org.apache.pdfbox.util.LayerUtility
           java.awt.geom.AffineTransform
           java.io.ByteArrayOutputStream)
  (:require [clojure.java.io :as io]))

(def sivukoko (PDPage/PAGE_SIZE_A4))
(def ylamarginaali (- (.getUpperRightY sivukoko) 28))
(def ensimmainen-rivi (- ylamarginaali 12))
(def vasen-marginaali 57.0)
(def footer-tila 70.0)

(defn muodosta-header
  "Header on aina vakiomuotoinen ja esiintyy vain dokumentin ensimmäisellä sivulla"
  [otsikko]
  [{:sivu 1
    :x (+ 57 262)
    :y ensimmainen-rivi
    :teksti (:teksti otsikko)}
   {:sivu 1
    :x 0
    :y (- (* 3 12))
    :teksti (:paivays otsikko)}
   {:sivu 1
    :x 135
    :y 0
    :teksti (:dnro otsikko)}
   ;; lopuksi headerin marginaali
   {:sivu 1
    :x 0
    :y (- (* 3 12))}])

(defn laske-koordinaatti
  [key elementti]
  (reduce + (map key elementti)))

(defn lisaa-elementti
  "lisää elementin taulukkoon"
  [coll uusi-elementti]
  (if (not-empty coll)
    (concat coll
      (let [last (last coll)
            viimeinen-sivu (filter #(= (:sivu last) (:sivu %)) coll)
            viimeisen-sivun-alareuna (laske-koordinaatti :y viimeinen-sivu)
            viimeinen-x-positio (laske-koordinaatti :x viimeinen-sivu)
            elementin-korkeus (- ylamarginaali (laske-koordinaatti :y uusi-elementti))
            mahtuu-sivulle (< footer-tila (- viimeisen-sivun-alareuna elementin-korkeus))
            uusi-elementti (vec (map #(assoc % :sivu (if mahtuu-sivulle
                                                       (:sivu last)
                                                       (+ (:sivu last) 1))) uusi-elementti))]
        (if mahtuu-sivulle
          (-> (assoc-in uusi-elementti [0 :x] (- (:x (first uusi-elementti)) viimeinen-x-positio))
            (assoc-in [0 :y] (- (:y (first uusi-elementti)) ylamarginaali)))
          uusi-elementti)))
    uusi-elementti))

(defn rivita-teksti
  [sisalto ensimmainen-siityma]
  (assoc-in
    (vec (for [rivi (clojure.string/split-lines sisalto)]
           {:x 0
            :y -12
            :teksti rivi}))
    [0 :x] ensimmainen-siityma))

(defn muodosta-tekstit
  [sisalto fontti]
  (flatten
    [{:x 57
      :y ensimmainen-rivi
      :teksti (-> sisalto :sisalto :otsikko)}
     (rivita-teksti (-> sisalto :sisalto :asiasisalto) 128)]))

(defn lisaa-logo
  [dokumentti sivu]
  (with-open [pdf (io/input-stream (io/file (io/resource "pdf-sisalto/oph-logo.pdf")))
              logo-dokumentti (PDDocument/load pdf)]
    (let [layer (LayerUtility. dokumentti)
          logo (.importPageAsForm layer logo-dokumentti 0)
          koko (.getBBox logo)
          korkeus (.getHeight koko)
          skaalaus (/ 50 korkeus)
          skaalattu-korkeus (* skaalaus korkeus)
          ylareuna (.getUpperRightY sivukoko)
          aft (AffineTransform. skaalaus 0.0 0.0 skaalaus 57.0 (- ylareuna (+ 28 skaalattu-korkeus))) ]
      (.appendFormAsLayer layer sivu logo aft "OPH-LOGO"))))

(defn muodosta-osat
  [fontti osat]
  (-> (muodosta-header (:otsikko osat))
    (lisaa-elementti (muodosta-tekstit (:teksti osat) fontti))))

(defn kirjoita-sisalto
  "Kirjoittaa valmiiksi sivutetun ja rivitetyn sisällön PDPage sivuihin ja palauttaa sivut"
  [dokumentti fontti sivutettu-sisalto]
  (for [[sivunumero sivun-rivit] (group-by :sivu sivutettu-sisalto)]
    (let [pdfsivu (PDPage. sivukoko)]
      (with-open [pdstream (PDPageContentStream. dokumentti pdfsivu)]
        (.setFont pdstream fontti 12)
        (when (= 1 sivunumero) (lisaa-logo dokumentti pdfsivu))
        (.beginText pdstream)
        (doseq [rivi sivun-rivit]
          (.moveTextPositionByAmount pdstream (:x rivi) (:y rivi))
          (when (:teksti rivi) (.drawString pdstream (:teksti rivi))))
        (.endText pdstream))
      pdfsivu)))

(defn muodosta-pdf
  [osat]
  (with-open [dokumentti (PDDocument.)
              fonttitiedosto (io/input-stream (io/file (io/resource "pdf-sisalto/ebgaramond/EBGaramond-Regular.ttf")))]
    (let [output (ByteArrayOutputStream.)
          fontti (PDTrueTypeFont/loadTTF dokumentti fonttitiedosto)
          sivutettu-sisalto (muodosta-osat fontti osat)
          sivut (kirjoita-sisalto dokumentti fontti sivutettu-sisalto)]
      (doseq [sivu sivut]
        (.addPage dokumentti sivu))
      (.save dokumentti output)
      output)))
