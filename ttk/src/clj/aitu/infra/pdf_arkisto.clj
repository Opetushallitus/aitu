(ns aitu.infra.pdf-arkisto
  (:import (org.apache.pdfbox.pdmodel PDPage
                                      PDDocument)
           org.apache.pdfbox.pdmodel.edit.PDPageContentStream
           (org.apache.pdfbox.pdmodel.font PDTrueTypeFont
                                           PDType1Font)
           org.apache.pdfbox.util.LayerUtility
           java.awt.geom.AffineTransform
           java.io.ByteArrayOutputStream)
  (:require [clojure.java.io :as io]))

(def sivukoko (PDPage/PAGE_SIZE_A4))
(def ylamarginaali (- (.getUpperRightY sivukoko) 28))
(def ensimmainen-rivi (- ylamarginaali 12))
(def vasen-marginaali 57.0)
(def oikea-marginaali 50.0)
(def sisennys 128.0)
(def footer-tila 70.0)

(defn muodosta-header
  "Header on aina vakiomuotoinen ja esiintyy vain dokumentin ensimmäisellä sivulla"
  [otsikko]
  [{:x (+ vasen-marginaali 262)
    :y ensimmainen-rivi
    :teksti (:teksti otsikko)}
   {:x 0
    :y (- (* 3 12))
    :teksti (:paivays otsikko)}
   {:x 135
    :y 0
    :teksti (:dnro otsikko)}
   ;; lopuksi headerin marginaali
   {:x 0
    :y (- (* 3 12))}])

(defn laske-koordinaatti
  [key elementti]
  (reduce + (map key elementti)))

(defn lisaa-elementti
  "lisää elementin taulukkoon"
  [coll uusi-elementti]
  (if (not-empty coll)
    (concat coll
      (let [sivunumero (:sivu (last coll))
            viimeinen-sivu (filter #(= sivunumero (:sivu %)) coll)
            viimeisen-sivun-alareuna (laske-koordinaatti :y viimeinen-sivu)
            viimeinen-x-positio (laske-koordinaatti :x viimeinen-sivu)
            elementin-korkeus (- ylamarginaali (laske-koordinaatti :y uusi-elementti))
            mahtuu-sivulle (< footer-tila (- viimeisen-sivun-alareuna elementin-korkeus))
            uusi-elementti (vec (map #(assoc % :sivu (if mahtuu-sivulle
                                                       sivunumero
                                                       (+ sivunumero 1))) uusi-elementti))]
        (if mahtuu-sivulle
          (-> (assoc-in uusi-elementti [0 :x] (- (:x (first uusi-elementti)) viimeinen-x-positio))
            (assoc-in [0 :y] (- (:y (first uusi-elementti)) ylamarginaali)))
          uusi-elementti)))
    (map #(assoc % :sivu 1) uusi-elementti)))

(defn tekstin-pituus
  [fontti fonttikoko teksti]
  (* (/ (.getStringWidth fontti teksti) 1000) fonttikoko))

(defn yhdista-sanat
  "yhdistää peräkkäisiä sanoja pidemmiksi merkkijonoiksi niin että annetulla fontilla merkkijono mahtuu vapaaseen tilaan."
  [sanat fontti fonttikoko vapaa-tila]
  (loop [yhdistetty (first sanat)
         loput (next sanat)]
    (cond
      (and loput
           (> vapaa-tila (+ (tekstin-pituus fontti fonttikoko yhdistetty) (tekstin-pituus fontti fonttikoko (first loput)))))
        (recur (str yhdistetty (first loput)) (next loput))
      loput
        (cons yhdistetty (yhdista-sanat loput fontti fonttikoko vapaa-tila))
      :else
        (list yhdistetty))))

(defn jaa-tekstirivi
  [teksti fontti fonttikoko vapaa-tila]
  (let [sanat (clojure.string/split teksti #"(?<=\s)")]
    (yhdista-sanat sanat fontti fonttikoko vapaa-tila)))

(defn rivita-kappale
  [ensimmainen-siirtyma fontti bold-fontti fonttikoko vapaa-tila teksti]
  (let [[otsikko sisalto] (if (.contains teksti "\t")
                            (clojure.string/split teksti #"\t")
                            [nil teksti])
        bold? (= \* (first sisalto))
        underline? (= \_ (first sisalto))
        fonttikoko (if bold?
                     (- fonttikoko 2)
                     fonttikoko)
        fontti (if bold?
                 bold-fontti
                 fontti)
        sisalto (-> sisalto
                  (clojure.string/replace #"^[*_]" "")
                  (clojure.string/replace #"[*_]$" ""))]
    (apply vector
           {:x (- ensimmainen-siirtyma)
            :y (- fonttikoko)
            :bold true
            :underline false
            :teksti otsikko}
           (-> (vec (for [rivi (jaa-tekstirivi sisalto fontti fonttikoko vapaa-tila)]
                      {:x 0
                       :y (- fonttikoko)
                       :teksti rivi
                       :bold bold?
                       :underline underline?}))
             (update-in [0 :x] + ensimmainen-siirtyma)
             (update-in [0 :y] + fonttikoko)))))

(defn rivita-teksti
  [sisalto ensimmainen-siirtyma fontti bold-fontti fonttikoko vapaa-tila]
  (update-in
    (apply (comp vec concat)
           (map (partial rivita-kappale ensimmainen-siirtyma fontti bold-fontti fonttikoko vapaa-tila)
                (clojure.string/split-lines sisalto)))
    [0 :x] + ensimmainen-siirtyma))

(defn muodosta-otsikko
  [otsikko]
  (map-indexed (fn [i rivi]
                 {:x (if (zero? i)
                       vasen-marginaali
                       0)
                  :y (if (zero? i)
                       ensimmainen-rivi
                       -12)
                  :teksti rivi
                  :bold true})
               (clojure.string/split-lines otsikko)))

(defn muodosta-tekstit
  [sisalto fontti bold-fontti]
  (flatten
    [(muodosta-otsikko (-> sisalto :sisalto :otsikko))
     (rivita-teksti (-> sisalto :sisalto :asiasisalto) sisennys fontti bold-fontti
                    12 (- (.getWidth sivukoko) vasen-marginaali sisennys oikea-marginaali))]))

(defn lisaa-logo
  [dokumentti sivu]
  (with-open [pdf (io/input-stream (io/file (io/resource "pdf-sisalto/oph-logo.pdf")))
              logo-dokumentti (PDDocument/load pdf)]
    (let [layer (LayerUtility. dokumentti)
          logo (.importPageAsForm layer logo-dokumentti 0)
          korkeus (.getHeight (.getBBox logo))
          skaalaus (/ 50 korkeus)
          skaalattu-korkeus (* skaalaus korkeus)
          ylareuna (.getUpperRightY sivukoko)
          aft (AffineTransform. skaalaus 0.0 0.0 skaalaus vasen-marginaali (- ylareuna (+ 28 skaalattu-korkeus))) ]
      (.appendFormAsLayer layer sivu logo aft "OPH-LOGO"))))

(defn muodosta-osat
  [fontti bold-fontti osat]
  (reduce lisaa-elementti []
          [(muodosta-header (:otsikko osat))
           (muodosta-tekstit (:teksti osat) fontti bold-fontti)]))

(defn muodosta-footer
  "Footer tulee jokaiselle sivulle. Oletuksena että suhteellinen lähtöpositio on oikea"
  [fontti bold-fontti osat]
  (rivita-teksti (-> osat :footer :teksti) 0 fontti bold-fontti 8 (- (.getWidth sivukoko) vasen-marginaali oikea-marginaali)))

(defn kirjoita-rivit
  [pdstream rivit koko fontti bold-fontti]
  (doseq [rivi rivit]
    (.moveTextPositionByAmount pdstream (:x rivi) (:y rivi))
    (if (:bold rivi)
      (.setFont pdstream bold-fontti (- koko 2))
      (.setFont pdstream fontti koko))
    (when (:teksti rivi) (.drawString pdstream (:teksti rivi)))))

(defn kirjoita-sisalto
  "Kirjoittaa valmiiksi sivutetun ja rivitetyn sisällön PDPage sivuihin ja palauttaa sivut"
  [dokumentti fontti bold-fontti sivutettu-sisalto footer]
  (for [[sivunumero sivun-rivit] (sort-by key (group-by :sivu sivutettu-sisalto))]
    (let [pdfsivu (PDPage. sivukoko)]
      (with-open [pdstream (PDPageContentStream. dokumentti pdfsivu)]
        (when (= 1 sivunumero) (lisaa-logo dokumentti pdfsivu))
        (doto pdstream
          (.beginText)
          (kirjoita-rivit sivun-rivit 12 fontti bold-fontti)
          (.moveTextPositionByAmount (- vasen-marginaali (laske-koordinaatti :x sivun-rivit)) (- footer-tila (laske-koordinaatti :y sivun-rivit))) ; siirrytään footerin alkuun
          (kirjoita-rivit footer 8 fontti bold-fontti)
          (.endText)
          (.drawLine vasen-marginaali footer-tila (- (.getWidth sivukoko) oikea-marginaali) footer-tila)))
      pdfsivu)))

(defn muodosta-pdf
  [osat]
  (with-open [dokumentti (PDDocument.)
              fonttitiedosto (io/input-stream (io/file (io/resource "pdf-sisalto/ebgaramond/EBGaramond-Regular.ttf")))]
    (let [output (ByteArrayOutputStream.)
          fontti (PDTrueTypeFont/loadTTF dokumentti fonttitiedosto)
          bold-fontti PDType1Font/HELVETICA_BOLD
          sivutettu-sisalto (muodosta-osat fontti bold-fontti osat)
          footer (muodosta-footer fontti bold-fontti osat)
          sivut (kirjoita-sisalto dokumentti fontti bold-fontti sivutettu-sisalto footer)]
      (doseq [sivu sivut]
        (.addPage dokumentti sivu))
      (.save dokumentti output)
      output)))
