(ns aitu.infra.pdf-arkisto
  (:import (org.apache.pdfbox.pdmodel PDPage
                                      PDDocument)
           org.apache.pdfbox.pdmodel.edit.PDPageContentStream
           org.apache.pdfbox.pdmodel.font.PDTrueTypeFont
           org.apache.pdfbox.util.LayerUtility
           java.awt.geom.AffineTransform
           java.io.ByteArrayOutputStream)
  (:require [clojure.java.io :as io]))

(defn lisaa-otsikkotekstit
  [sisalto otsikko ylareuna]
  (.beginText sisalto)
  (.moveTextPositionByAmount sisalto (+ 57 262) (- ylareuna (+ 12 28)))
  (when (:teksti otsikko) (.drawString sisalto (:teksti otsikko)))
  (.moveTextPositionByAmount sisalto 0 (- (* 2 1.5 12)))
  (when (:paivays otsikko) (.drawString sisalto (:paivays otsikko)))
  (.moveTextPositionByAmount sisalto 135 0)
  (when (:dnro otsikko) (.drawString sisalto (:dnro otsikko)))
  (.endText sisalto))

(defn lisaa-tekstit
  [sisalto teksti ylareuna]
  (.beginText sisalto)
  (.moveTextPositionByAmount sisalto 57 (- ylareuna (+ 12 100)))
  (when (:vastaanottaja teksti) (.drawString sisalto (:vastaanottaja teksti)))
  (.endText sisalto))

(defn lisaa-logo
  [dokumentti sivu ylareuna]
  (with-open [pdf (io/input-stream (io/file (io/resource "pdf-sisalto/oph-logo.pdf")))
              logo-dokumentti (PDDocument/load pdf)]
    (let [layer (LayerUtility. dokumentti)
          logo (.importPageAsForm layer logo-dokumentti 0)
          koko (.getBBox logo)
          korkeus (.getHeight koko)
          skaalaus (/ 50 korkeus)
          skaalattu-korkeus (* skaalaus korkeus)
          aft (AffineTransform. skaalaus 0.0 0.0 skaalaus 57.0 (- ylareuna (+ 28 skaalattu-korkeus))) ]
      (.appendFormAsLayer layer sivu logo aft "OPH-LOGO"))))

(defn muodosta-osat
  [dokumentti osat]
  (let [sivukoko (PDPage/PAGE_SIZE_A4)
        ylareuna (.getUpperRightY sivukoko)
        sivu (PDPage. sivukoko)]
    (.addPage dokumentti sivu)
    (with-open [sisalto (PDPageContentStream. dokumentti sivu)
                fonttitiedosto (io/input-stream (io/file (io/resource "pdf-sisalto/ebgaramond/EBGaramond-Regular.ttf")))]
      (let [fontti (PDTrueTypeFont/loadTTF dokumentti fonttitiedosto)]
        (.setFont sisalto fontti 12)
        (lisaa-logo dokumentti sivu ylareuna)
        (when (:otsikko osat) (lisaa-otsikkotekstit sisalto (:otsikko osat) ylareuna))
        (when (:teksti osat) (lisaa-tekstit sisalto (:teksti osat) ylareuna))))))

(defn muodosta-pdf
  [osat]
  (with-open [dokumentti (PDDocument.)]
    (let [output (ByteArrayOutputStream.)]
      (muodosta-osat dokumentti osat)
      (.save dokumentti output)
      output)))
