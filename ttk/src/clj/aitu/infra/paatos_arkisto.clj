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

(defn lokalisoi [m kentta kieli]
  (let [suomeksi ((keyword (str (name kentta) "_fi")) m)
        ruotsiksi ((keyword (str (name kentta) "_sv")) m)]
    (case kieli
      :fi (or suomeksi ruotsiksi)
      :sv (or ruotsiksi suomeksi))))

(defn luo-paatos [kieli header pohja data]
  (let [pohja-file (case [pohja kieli]
                     [:asettamispaatos :fi] "pdf-sisalto/mustache/asettamispaatos_fi.mustache"
                     [:asettamispaatos :sv] "pdf-sisalto/mustache/asettamispaatos_sv.mustache"
                     [:taydennyspaatos :fi] "pdf-sisalto/mustache/taydennyspaatos_fi.mustache"
                     [:taydennyspaatos :sv] "pdf-sisalto/mustache/taydennyspaatos_sv.mustache"
                     [:muutospaatos :fi] "pdf-sisalto/mustache/muutospaatos_fi.mustache"
                     [:muutospaatos :sv] "pdf-sisalto/mustache/muutospaatos_sv.mustache")
        pohja-string (slurp (io/resource pohja-file))
        footer-string (slurp (io/resource "pdf-sisalto/mustache/footer.mustache"))]
    (pdf-arkisto/muodosta-pdf {:otsikko header
                               :teksti (stencil/render-string pohja-string data)
                               :footer (stencil/render-string footer-string data)})))

(defn ^:private muotoile-jasen [kieli jasen]
  {:nimi (str (:etunimi jasen) " " (:sukunimi jasen))
   :jarjesto (lokalisoi jasen :jarjesto_nimi kieli)
   :edustus (:edustus jasen)})

(defn ^:private muotoile-kieli [kieli toimikunnan-kieli]
  (case [toimikunnan-kieli kieli]
    ["fi" :fi] "suomi"
    ["fi" :sv] "finska"
    ["sv" :fi] "ruotsi"
    ["sv" :sv] "svenska"
    ["2k" :fi] "suomi, ruotsi"
    ["2k" :sv] "finska, svenska"))

(defn ^:private muotoile-pvm [pvm]
  (.toString pvm "dd.MM.yyyy"))

(defn luo-asettamispaatos [kieli diaarinumero data]
  (let [toimikunta (-> (select-and-rename-keys (ttk-arkisto/hae diaarinumero)
                                              [:nimi_fi :nimi_sv [:toimikausi_alku :alkupvm] [:toimikausi_loppu :loppupvm]
                                               :tilikoodi :kielisyys [:toimiala :toimialue] :nayttotutkinto :jasenyys])
                     (update-in [:alkupvm] muotoile-pvm)
                     (update-in [:loppupvm] muotoile-pvm))
        edustus->jasenet (->> (:jasenyys toimikunta)
                           (sort-by (juxt :sukunimi :etunimi))
                           (map (partial muotoile-jasen kieli))
                           (group-by :edustus))
        jasenet (filter (comp seq :jasen)
                        [{:edustus (case kieli :fi "Työnantajien edustajat" :sv "Työnantajien edustajat (sv)")
                          :jasen (edustus->jasenet "tyonantaja")}
                         {:edustus (case kieli :fi "Työntekijöiden edustajat" :sv "Työntekijöiden edustajat (sv)")
                          :jasen (edustus->jasenet "tyontekija")}
                         {:edustus (case kieli :fi "Opettajien edustajat" :sv "Opettajien edustajat (sv)")
                          :jasen (edustus->jasenet "opettaja")}
                         {:edustus (case kieli :fi "Muut edustajat" :sv "Muut edustajat (sv)")
                          :jasen (mapcat edustus->jasenet ["asiantuntija" "itsenainen" "muu"])}])
        toimiala (for [tutkinto (:nayttotutkinto toimikunta)]
                   (lokalisoi tutkinto :nimi kieli))]
    (luo-paatos kieli
                {:teksti "PÄÄTÖS"
                 :paivays (:paivays data)
                 :diaarinumero diaarinumero}
                :asettamispaatos
                (assoc data :toimikunta (assoc toimikunta :jasen jasenet
                                                          :kieli (muotoile-kieli kieli (:kielisyys toimikunta))
                                                          :toimiala toimiala)))))

(defn luo-taydennyspaatos [kieli diaarinumero data]
  (let [toimikunta (-> (select-and-rename-keys (ttk-arkisto/hae diaarinumero)
                                              [:nimi_fi :nimi_sv [:toimikausi_alku :alkupvm] [:toimikausi_loppu :loppupvm]])
                     (update-in [:alkupvm] muotoile-pvm)
                     (update-in [:loppupvm] muotoile-pvm))
        jasenet (map ttk-arkisto/hae-jasen-ja-henkilo (:jasenet data))
        edustus->jasenet (->> jasenet
                           (sort-by (juxt :sukunimi :etunimi))
                           (map (partial muotoile-jasen kieli))
                           (group-by :edustus))
        jasenet (filter (comp seq :jasen)
                        [{:edustus (case kieli :fi "Työnantajien edustaja" :sv "Työnantajien edustaja (sv)")
                          :jasen (edustus->jasenet "tyonantaja")}
                         {:edustus (case kieli :fi "Työntekijöiden edustaja" :sv "Työntekijöiden edustaja (sv)")
                          :jasen (edustus->jasenet "tyontekija")}
                         {:edustus (case kieli :fi "Opettajien edustaja" :sv "Opettajien edustaja (sv)")
                          :jasen (edustus->jasenet "opettaja")}
                         {:edustus (case kieli :fi "Muu edustaja" :sv "Muu edustaja (sv)")
                          :jasen (mapcat edustus->jasenet ["asiantuntija" "itsenainen" "muu"])}])]
    (luo-paatos kieli
                {:teksti "PÄÄTÖS"
                 :paivays (:paivays data)
                 :diaarinumero diaarinumero}
                :taydennyspaatos
                (assoc data :toimikunta (assoc toimikunta :jasen jasenet)))))

(defn luo-muutospaatos [kieli diaarinumero data]
  (let [toimikunta (-> (select-and-rename-keys (ttk-arkisto/hae diaarinumero)
                                              [:nimi_fi :nimi_sv [:toimikausi_alku :alkupvm] [:toimikausi_loppu :loppupvm]])
                     (update-in [:alkupvm] muotoile-pvm)
                     (update-in [:loppupvm] muotoile-pvm))
        jasenet (map ttk-arkisto/hae-jasen-ja-henkilo (:jasenet data))
        edustus->jasenet (->> jasenet
                           (sort-by (juxt :sukunimi :etunimi))
                           (map (partial muotoile-jasen kieli))
                           (group-by :edustus))
        jasenet (filter (comp seq :jasen)
                        [{:edustus (case kieli :fi "Työnantajien edustaja" :sv "Työnantajien edustaja (sv)")
                          :jasen (edustus->jasenet "tyonantaja")}
                         {:edustus (case kieli :fi "Työntekijöiden edustaja" :sv "Työntekijöiden edustaja (sv)")
                          :jasen (edustus->jasenet "tyontekija")}
                         {:edustus (case kieli :fi "Opettajien edustaja" :sv "Opettajien edustaja (sv)")
                          :jasen (edustus->jasenet "opettaja")}
                         {:edustus (case kieli :fi "Muu edustaja" :sv "Muu edustaja (sv)")
                          :jasen (mapcat edustus->jasenet ["asiantuntija" "itsenainen" "muu"])}])]
    (luo-paatos kieli
                {:teksti "PÄÄTÖS"
                 :paivays (:paivays data)
                 :diaarinumero diaarinumero}
                :muutospaatos
                (assoc data :toimikunta (assoc toimikunta :jasen jasenet)))))
