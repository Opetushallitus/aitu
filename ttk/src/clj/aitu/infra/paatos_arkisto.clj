(ns aitu.infra.paatos-arkisto
  (:require [stencil.core :as stencil]
            [aitu.infra.ttk-arkisto :as ttk-arkisto]
            [aitu.infra.pdf-arkisto :as pdf-arkisto]
            [clojure.java.io :as io]
            [aitu.asetukset :refer [asetukset]]
            [aitu.util :refer [select-and-rename-keys]])
  (:import java.io.File))

(defn tallenna-pdf [filename pdf]
  (with-open [out (io/output-stream (io/as-file filename))]
    (io/copy pdf out)))

(defn luo-paatos [header pohja data]
  (let [pohja-file (case pohja
                     :asettamispaatos "pdf-sisalto/mustache/asettamispaatos.mustache"
                     :taydennyspaatos "pdf-sisalto/mustache/taydennyspaatos.mustache")
        pohja-string (slurp (io/resource pohja-file))
        footer-string (slurp (io/resource "pdf-sisalto/mustache/footer.mustache"))]
    (pdf-arkisto/muodosta-pdf {:otsikko header
                               :teksti (stencil/render-string pohja-string data)
                               :footer (stencil/render-string footer-string data)})))

(defn ^:private muotoile-jasen [jasen]
  {:nimi (str (:etunimi jasen) " " (:sukunimi jasen))
   :jarjesto (:jarjesto_nimi_fi jasen)
   :edustus (:edustus jasen)})

(def ^:private muotoile-kieli
  {"fi" "suomi"
   "sv" "ruotsi"
   "2k" "suomi, ruotsi"})

(defn luo-asettamispaatos [diaarinumero data]
  (let [toimikunta (select-and-rename-keys (ttk-arkisto/hae diaarinumero)
                                           [:nimi_fi :nimi_sv [:toimikausi_alku :alkupvm] [:toimikausi_loppu :loppupvm]
                                            :tilikoodi :kielisyys [:toimiala :toimialue] :nayttotutkinto :jasenyys])
        edustus->jasenet (->> (:jasenyys toimikunta)
                           (sort-by (juxt :sukunimi :etunimi))
                           (map muotoile-jasen)
                           (group-by :edustus))
        jasenet (filter #(seq (:jasen %))
                        [{:edustus "Työnantajien edustajat"
                          :jasen (edustus->jasenet "tyonantaja")}
                        {:edustus "Työntekijöiden edustajat"
                         :jasen (edustus->jasenet "tyontekija")}
                        {:edustus "Opettajien edustajat"
                         :jasen (edustus->jasenet "opettaja")}
                        {:edustus "Muut edustajat"
                         :jasen (mapcat edustus->jasenet ["asiantuntija" "itsenainen" "muu"])}])
        toimiala (for [tutkinto (:nayttotutkinto toimikunta)]
                   (select-keys tutkinto [:nimi_fi]))]
    (luo-paatos {:teksti "PÄÄTÖS"
                 :paivays (:paivays data)
                 :diaarinumero diaarinumero}
                :asettamispaatos
                (assoc data :toimikunta (assoc toimikunta :jasen jasenet
                                                          :kieli (muotoile-kieli (:kielisyys toimikunta))
                                                          :toimiala toimiala)))))

(defn luo-taydennyspaatos [diaarinumero data]
  (let [toimikunta (select-and-rename-keys (ttk-arkisto/hae diaarinumero)
                                           [:nimi_fi :nimi_sv [:toimikausi_alku :alkupvm] [:toimikausi_loppu :loppupvm]])
        jasenet (map ttk-arkisto/hae-jasen-ja-henkilo (:jasenet data))
        edustus->jasenet (->> jasenet
                           (sort-by (juxt :sukunimi :etunimi))
                           (map muotoile-jasen)
                           (group-by :edustus))
        jasenet (filter #(seq (:jasen %))
                        [{:edustus "Työnantajien edustajat"
                          :jasen (edustus->jasenet "tyonantaja")}
                        {:edustus "Työntekijöiden edustajat"
                         :jasen (edustus->jasenet "tyontekija")}
                        {:edustus "Opettajien edustajat"
                         :jasen (edustus->jasenet "opettaja")}
                        {:edustus "Muut edustajat"
                         :jasen (mapcat edustus->jasenet ["asiantuntija" "itsenainen" "muu"])}])]
    (luo-paatos {:teksti "PÄÄTÖS"
                 :paivays (:paivays data)
                 :diaarinumero diaarinumero}
                :taydennyspaatos
                (assoc data :toimikunta (assoc toimikunta :jasen jasenet)))))
