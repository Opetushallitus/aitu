(ns aitu.infra.pdf-arkisto
  (:import (org.apache.pdfbox.pdmodel PDPage
                                      PDDocument)
           org.apache.pdfbox.pdmodel.edit.PDPageContentStream
           (org.apache.pdfbox.pdmodel.font PDTrueTypeFont
                                           PDType1Font)
           org.apache.pdfbox.util.LayerUtility
           java.awt.geom.AffineTransform
           (java.io ByteArrayOutputStream
                    ByteArrayInputStream))
  (:require [clojure.java.io :as io]
            [aitu.util :refer [update-in-if-exists]]))

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
    :teksti (:diaarinumero otsikko)}
   ;; lopuksi headerin marginaali
   {:x (- 0 262 135)
    :y (- (* 3 12))}])

(defn laske-koordinaatti
  [key elementti]
  (reduce + (map key elementti)))

(defn lisaa-elementti
  "lisää elementin taulukkoon"
  [v uusi-elementti]
  (if (seq v)
    (conj v
          (let [sivunumero (:sivu (peek v))
                viimeinen-sivu (filter #(= sivunumero (:sivu %)) v)
                viimeisen-sivun-alareuna (laske-koordinaatti :y viimeinen-sivu)
                viimeinen-x-positio (laske-koordinaatti :x viimeinen-sivu)
                sivunumero (or sivunumero 1)
                elementin-korkeus (:y uusi-elementti)
                mahtuu-sivulle (< footer-tila (+ viimeisen-sivun-alareuna elementin-korkeus))
                uusi-elementti (assoc uusi-elementti :sivu (if mahtuu-sivulle
                                                             sivunumero
                                                             (inc sivunumero)))]
            (if mahtuu-sivulle
              uusi-elementti
              (-> uusi-elementti
                (update-in [:x] + vasen-marginaali)
                (update-in [:y] + ylamarginaali)))))
    [(assoc uusi-elementti :sivu 1)]))

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

(defn pura-muotoilut
  [teksti]
  {:bold (= \* (first teksti))
   :underline (= \_ (first teksti))
   :teksti (some-> teksti
             (clojure.string/replace #"^[*_]" "")
             (clojure.string/replace #"[*_]$" ""))})

(defn rivita-kappale
  [fontti bold-fontti fonttikoko teksti]
  (let [osat (map pura-muotoilut (clojure.string/split teksti #"\t"))]
    (-> (apply (comp vec concat)
               (for [[tab osa] (map-indexed vector osat)
                     :let [vapaa-tila (- (.getWidth sivukoko)
                                         vasen-marginaali
                                         (* tab sisennys)
                                         oikea-marginaali)
                           fontti (if (:bold osa)
                                    bold-fontti
                                    fontti)]]
                 (-> (vec (for [rivi (jaa-tekstirivi (:teksti osa) fontti fonttikoko vapaa-tila)]
                            (merge osa
                                  {:x 0
                                   :y (- fonttikoko)
                                   :teksti rivi})))
                   (update-in [0 :x] + (* tab sisennys))
                   (update-in [0 :y] + fonttikoko)
                   (conj {:x (- (* tab sisennys))
                          :y 0}))))
      (update-in-if-exists [0 :y] - fonttikoko))))

(defn rivita-teksti
  [sisalto fontti bold-fontti fonttikoko]
  (apply concat
         (map (partial rivita-kappale fontti bold-fontti fonttikoko)
              (clojure.string/split-lines sisalto))))

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
         (concat
           (muodosta-header (:otsikko osat))
           (rivita-teksti (:teksti osat) fontti bold-fontti 12))))

(defn muodosta-footer
  "Footer tulee jokaiselle sivulle. Oletuksena että suhteellinen lähtöpositio on oikea"
  [fontti bold-fontti osat]
  (rivita-teksti (:footer osat) fontti bold-fontti 8))

(defn kirjoita-rivit
  [pdstream rivit koko fontti bold-fontti]
  (doseq [rivi rivit]
    (.moveTextPositionByAmount pdstream (:x rivi) (:y rivi))
    (if (:bold rivi)
      (.setFont pdstream bold-fontti (- koko 1))
      (.setFont pdstream fontti koko))
    (when (:teksti rivi) (.drawString pdstream (:teksti rivi)))))

(defn alleviivaa-rivit [stream rivit fonttikoko fontti]
  (reduce (fn [tila rivi]
            (let [tila (merge-with + tila (select-keys rivi [:x :y]))]
              (when (:underline rivi)
                (let [{:keys [x y]} tila
                      pituus (tekstin-pituus fontti fonttikoko (:teksti rivi))]
                  (.drawLine stream x (- y 2) (+ x pituus) (- y 2))))
              tila))
          {:x 0 :y 0}
          rivit))

(defn kirjoita-sivunumero
  [stream sivu viimeinen-sivu fonttikoko fontti]
  (let [teksti (str sivu " (" viimeinen-sivu ")")
        pituus (tekstin-pituus fontti fonttikoko teksti)]
    (doto stream
      (.setTextMatrix 1 0 0 1 (- (.getUpperRightX sivukoko) oikea-marginaali pituus) ensimmainen-rivi)
      (.setFont fontti fonttikoko)
      (.drawString teksti))))

(defn kirjoita-sisalto
  "Kirjoittaa valmiiksi sivutetun ja rivitetyn sisällön PDPage sivuihin ja palauttaa sivut"
  [dokumentti fontti bold-fontti sivutettu-sisalto footer]
  (let [sivut (sort-by key (group-by :sivu sivutettu-sisalto))
        viimeinen-sivu (reduce max 0 (keys sivut))]
    (for [[sivunumero sivun-rivit] sivut]
      (let [pdfsivu (PDPage. sivukoko)]
        (with-open [pdstream (PDPageContentStream. dokumentti pdfsivu)]
          (when (= 1 sivunumero) (lisaa-logo dokumentti pdfsivu))
          (doto pdstream
            (.beginText)
            (kirjoita-rivit sivun-rivit 12 fontti bold-fontti)
            (.setTextMatrix 1 0 0 1 vasen-marginaali footer-tila) ; siirrytään footerin alkuun
            (kirjoita-rivit footer 8 fontti bold-fontti)
            (kirjoita-sivunumero sivunumero viimeinen-sivu 12 fontti)
            (.endText)
            (alleviivaa-rivit sivun-rivit 12 fontti)
            (.drawLine vasen-marginaali footer-tila (- (.getWidth sivukoko) oikea-marginaali) footer-tila)))
        pdfsivu))))

(defn muodosta-pdf
  [osat]
  (with-open [dokumentti (PDDocument.)
              fonttitiedosto (io/input-stream (io/file (io/resource "pdf-sisalto/ebgaramond/EBGaramond-Regular.ttf")))]
    (let [output (ByteArrayOutputStream.)
          fontti (PDTrueTypeFont/loadTTF dokumentti fonttitiedosto)
          bold-fontti PDType1Font/TIMES_BOLD
          sivutettu-sisalto (muodosta-osat fontti bold-fontti osat)
          footer (muodosta-footer fontti bold-fontti osat)
          sivut (kirjoita-sisalto dokumentti fontti bold-fontti sivutettu-sisalto footer)]
      (doseq [sivu sivut]
        (.addPage dokumentti sivu))
      (.save dokumentti output)
      (ByteArrayInputStream. (.toByteArray output)))))
