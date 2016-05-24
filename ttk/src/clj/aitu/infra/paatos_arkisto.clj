(ns aitu.infra.paatos-arkisto
  (:require [stencil.core :as stencil]
            [aitu.infra.ttk-arkisto :as ttk-arkisto]
            [aitu.infra.pdf-arkisto :as pdf-arkisto]
            [clojure.java.io :as io]
            [aitu.asetukset :refer [asetukset]]
            [oph.common.util.util :refer [select-and-rename-keys]])
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

(defn ^:private muotoile-nimet [toimikunta]
  (if (= (:nimi_fi toimikunta) (:nimi_sv toimikunta))
    (dissoc toimikunta :nimi_sv)
    toimikunta))

(defn asettamispaatos->pdf [kieli header data]
  (luo-paatos kieli header :asettamispaatos data))

(defn jasen-nimike-filter [jasenet kieli]
  (filter (comp seq :jasen)
          [{:edustus (case kieli :fi "Työnantajien edustajat" :sv "Representanter för arbetsgivare")
            :jasen (jasenet "tyonantaja")}
           {:edustus (case kieli :fi "Työntekijöiden edustajat" :sv "Representanter för arbetstagare")
            :jasen (jasenet "tyontekija")}
           {:edustus (case kieli :fi "Opetusalan edustajat" :sv "Representanter för lärare")
            :jasen (jasenet "opettaja")}
           {:edustus (case kieli :fi "Itsenäisten ammatinharjoittajien edustajat" :sv "Representanter för självständiga yrkesutövare")
            :jasen (jasenet "itsenainen")}
           {:edustus (case kieli :fi "Muut toimikuntaan kuuluvat" :sv "Övriga")
            :jasen (mapcat jasenet ["asiantuntija" "muu"])}]))

(defn luo-asettamispaatos-data [kieli diaarinumero data]
  (let [toimikunta (-> (select-and-rename-keys (ttk-arkisto/hae diaarinumero)
                                              [:nimi_fi :nimi_sv [:toimikausi_alku :alkupvm] [:toimikausi_loppu :loppupvm]
                                               :tilikoodi :kielisyys [:toimiala :toimialue] :nayttotutkinto :jasenyys])
                     (update-in [:alkupvm] muotoile-pvm)
                     (update-in [:loppupvm] muotoile-pvm)
                     muotoile-nimet)
        nimitetyt-jasenet (filter #(= "nimitetty" (:status %)) (:jasenyys toimikunta))
        edustus->jasenet (->> nimitetyt-jasenet
                           (sort-by (juxt :sukunimi :etunimi))
                           (map (partial muotoile-jasen kieli))
                           (group-by :edustus))

        jasenet (jasen-nimike-filter edustus->jasenet kieli)

        toimiala (for [tutkinto (:nayttotutkinto toimikunta)]
                   (lokalisoi tutkinto :nimi kieli))
        jakelu (concat (for [{:keys [sukunimi etunimi]} (sort-by (juxt :sukunimi :etunimi) nimitetyt-jasenet)]
                         (str etunimi \space sukunimi))
                       (:jakelu data))
        tiedoksi (concat (sort (for [jasen (:jasenyys toimikunta)]
                                 (lokalisoi jasen :jarjesto_nimi kieli)))
                         (:tiedoksi data))]

    ; paluuarvona tietorakenne PDF-generointia varten
    {:kieli kieli
     :header {:teksti (case kieli :fi "PÄÄTÖS" :sv "BESLUT")
              :paivays (:paivays data)
              :diaarinumero diaarinumero}
     :data (assoc data :toimikunta (assoc toimikunta :jasen jasenet
                                                     :kieli (muotoile-kieli kieli (:kielisyys toimikunta))
                                                     :toimiala toimiala)
                       :jakelu jakelu
                       :tiedoksi tiedoksi
                       :paatosteksti (str (apply str (repeat (:tyhjiariveja data) "\n")) (:paatosteksti data)))}))

(defn luo-asettamispaatos [kieli diaarinumero data]
  (let [pdf-data (luo-asettamispaatos-data kieli diaarinumero data)]
    (asettamispaatos->pdf (:kieli pdf-data) (:header pdf-data) (:data pdf-data))))


(defn asettamispaatos->pdf [kieli header data]
  (luo-paatos kieli header :asettamispaatos data))

(defn luo-taydennyspaatos [kieli diaarinumero data]
  (let [toimikunta (-> (select-and-rename-keys (ttk-arkisto/hae diaarinumero)
                                              [:nimi_fi :nimi_sv [:toimikausi_alku :alkupvm] [:toimikausi_loppu :loppupvm]])
                     (update-in [:alkupvm] muotoile-pvm)
                     (update-in [:loppupvm] muotoile-pvm)
                     muotoile-nimet)
        jasenet [(ttk-arkisto/hae-jasen-ja-henkilo (Integer/parseInt (:jasen data)))]
        edustus->jasenet (->> jasenet
                           (sort-by (juxt :sukunimi :etunimi))
                           (map (partial muotoile-jasen kieli))
                           (group-by :edustus))
        jasenet (jasen-nimike-filter edustus->jasenet kieli)]

    (luo-paatos kieli
                {:teksti (case kieli :fi "PÄÄTÖS" :sv "BESLUT")
                 :paivays (:paivays data)
                 :diaarinumero diaarinumero}
                :taydennyspaatos
                (assoc data :toimikunta (assoc toimikunta :jasen jasenet)))))

(defn luo-muutospaatos [kieli diaarinumero data]
  (let [toimikunta (-> (select-and-rename-keys (ttk-arkisto/hae diaarinumero)
                                              [:nimi_fi :nimi_sv [:toimikausi_alku :alkupvm] [:toimikausi_loppu :loppupvm]])
                     (update-in [:alkupvm] muotoile-pvm)
                     (update-in [:loppupvm] muotoile-pvm)
                     muotoile-nimet)
        jasenet [(ttk-arkisto/hae-jasen-ja-henkilo (Integer/parseInt (:jasen data)))]
        edustus->jasenet (->> jasenet
                           (sort-by (juxt :sukunimi :etunimi))
                           (map (partial muotoile-jasen kieli))
                           (group-by :edustus))
        jasenet (jasen-nimike-filter edustus->jasenet kieli)
        korvattu (ttk-arkisto/hae-jasen-ja-henkilo (Integer/parseInt (:korvattu data)))]
    (luo-paatos kieli
                {:teksti (case kieli :fi "PÄÄTÖS" :sv "BESLUT")
                 :paivays (:paivays data)
                 :diaarinumero diaarinumero}
                :muutospaatos
                (assoc data :toimikunta (assoc toimikunta :jasen jasenet)
                            :korvattu (str (:etunimi korvattu) " " (:sukunimi korvattu))))))
