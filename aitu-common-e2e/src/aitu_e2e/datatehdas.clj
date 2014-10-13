;; Copyright (c) 2013 The Finnish National Board of Education - Opetushallitus
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

(ns aitu-e2e.datatehdas
  (:require
    [aitu-e2e.data-util :refer [menneisyydessa tulevaisuudessa]]))

(defn tunnusgeneraattori
  ([f]
    (let [a (atom 0)]
      (fn []
        (let [id (swap! a inc)]
          (f id)))))
  ([] (tunnusgeneraattori identity)))

(def uusi-sopimusnumero! (tunnusgeneraattori str))
(def uusi-ytunnus! (tunnusgeneraattori str))
(def uusi-oppilaitostunnus! (tunnusgeneraattori str))
(def uusi-toimikuntatunnus! (tunnusgeneraattori #(str "TTK" %)))

(defn setup-koulutustoimija
  ([y-tunnus nimi]
    {:ytunnus y-tunnus
     :nimi_fi nimi})
  ([y-tunnus]
    (setup-koulutustoimija y-tunnus "Ruikonperän koulutuskuntayhtymä"))
  ([]
    (setup-koulutustoimija (uusi-ytunnus!))))

(defn setup-oppilaitos
  ([koodi nimi koulutustoimija]
    {:oppilaitoskoodi koodi
     :nimi nimi
     :koulutustoimija koulutustoimija})
  ([koodi koulutustoimija]
    (setup-oppilaitos koodi "Ruikonperän multakurkkuopisto" koulutustoimija))
  ([koulutustoimija] (setup-oppilaitos (uusi-oppilaitostunnus!) koulutustoimija)))

(defn oppilaitos-nimella [nimi koulutustoimija]
  (setup-oppilaitos (uusi-oppilaitostunnus!) nimi koulutustoimija))

(defn setup-opintoala
  [koodi nimi]
  {:koodi koodi
   :selite_fi nimi
   :koulutusala "KA1"})

(defn setup-tutkinto-map
  ([tunnus versio]
  {
   :koulutusalat {:koodi "KA1"}
   :opintoalat (setup-opintoala "OPI" "Ilmausala")
   :tutkinnot {:nimi_fi "Ilmastointialan tutkinto"
               :tutkintotunnus tunnus
               :versio versio
               :tutkintoversio_id versio
               :koodistoversio versio
               :opintoala "OPI"}
   })
  ([] (setup-tutkinto-map "TU1" 1)))

(defn tutkinto-opintoalan-nimella
  [opintoala-nimi]
  (assoc (setup-tutkinto-map) :opintoalat
    (setup-opintoala "OPI" opintoala-nimi)))

(defn setup-voimassaoleva-jarjestamissopimus
  ([sopimusnumero koulutustoimija oppilaitos toimikunta tutkintoversio]
    (let [jarjestamissopimusid (Integer/parseInt sopimusnumero)
          oppilaitostunnus (get oppilaitos :oppilaitoskoodi oppilaitos)
          toimikuntatunnus (get toimikunta :tkunta toimikunta)
          y-tunnus (get koulutustoimija :ytunnus koulutustoimija)
          tutkintoversio-id (get tutkintoversio :tutkintoversio_id tutkintoversio)]
      {:jarjestamissopimukset
       {:toimikunta toimikuntatunnus
        :sopijatoimikunta toimikuntatunnus
        :koulutustoimija y-tunnus
        :tutkintotilaisuuksista_vastaava_oppilaitos oppilaitostunnus
        :jarjestamissopimusid jarjestamissopimusid
        :sopimusnumero sopimusnumero
        :alkupvm menneisyydessa
        :loppupvm tulevaisuudessa
        :voimassa true}
       :sopimus_ja_tutkinto
       {:jarjestamissopimusid jarjestamissopimusid
        :sopimus_ja_tutkinto [{:tutkintoversio_id tutkintoversio-id}]}}))
  ([y-tunnus oppilaitostunnus toimikuntatunnus tutkintoversio]
    (setup-voimassaoleva-jarjestamissopimus (uusi-sopimusnumero!) y-tunnus oppilaitostunnus toimikuntatunnus tutkintoversio)))

(defn setup-lakannut-jarjestamissopimus [sopimusnumero y-tunnus oppilaitostunnus toimikuntatunnus tutkintoversio]
  (-> (setup-voimassaoleva-jarjestamissopimus sopimusnumero y-tunnus oppilaitostunnus toimikuntatunnus tutkintoversio)
    (assoc-in [:jarjestamissopimukset :loppupvm] menneisyydessa)
    (assoc-in [:jarjestamissopimukset :voimassa] false)))


(defn setup-toimikunta
  ([toimikuntatunnus]
  {:toimikunnat [{:tkunta toimikuntatunnus}]})
  ([] (setup-toimikunta "ILMA")))

(defn toimikunta-nimella [nimi]
  {:nimi_fi nimi
   :nimi_sv nimi
   :tkunta (uusi-toimikuntatunnus!)
   :toimikausi 2})

(defn toimikunta-diaarinumerolla [diaarinumero tkunta]
  (assoc
    (toimikunta-nimella "Hevoshieronnan toimikunta")
    :diaarinumero diaarinumero
    :tkunta tkunta))

(defn toimikunta-nimella-vastuussa-tutkinnosta [nimi tutkintotunnus]
  (let [toimikunta (toimikunta-nimella nimi)]
    {:toimikunnat toimikunta
     :toimikunta_ja_tutkinto {:toimikunta (:tkunta toimikunta)
                              :tutkintotunnus tutkintotunnus}}))

(defn vanha-toimikunta-nimella-vastuussa-tutkinnosta [nimi tutkintotunnus]
  (let [t (toimikunta-nimella-vastuussa-tutkinnosta nimi tutkintotunnus)
        toimikunta (:toimikunnat t)]
    (assoc t :toimikunnat
      (merge toimikunta
        {:toimikausi 1
        :toimikausi_alku "2010-08-01"}))))

(defn jarjesto-nimella
  [jarjesto-nimi]
  {:jarjestoid ((tunnusgeneraattori))
   :nimi_fi jarjesto-nimi
   :nimi_sv jarjesto-nimi})

(defn henkilo-nimella
  [etunimi sukunimi]
  {:henkiloid ((tunnusgeneraattori))
   :etunimi etunimi
   :sukunimi sukunimi})

(defn henkilo-jarjestolla
  [etunimi sukunimi jarjestoid]
  (let [henkilo (henkilo-nimella etunimi sukunimi)]
    (assoc henkilo :jarjesto jarjestoid)))

(defn vectorize [m]
  (into {} (for [[k v] m]
             {k (cond
                  (vector? v) v
                  (seq? v) (vec v)
                  :else (vector v))})))

(defn merge-datamaps
  [& ms]
    (vectorize (apply merge-with concat (map vectorize ms))))

(defn luo-tutkintoja-opintoalaan [lkm opintoala]
  (take lkm
        (for [a (seq "abcdefghijklmnopqrstuwvxyz")
              i (range 1 10)]
          {:nimi_fi (str "Haettava tutkinto " a i)
           :tutkintotunnus (str "TU" a i)
           :opintoala opintoala})))

(defn tutkinnot-oletus-testidata []
  (let [toimikunnat (assoc-in (setup-toimikunta) [:toimikunnat 0 :nimi_fi] "Tutkinnon toimikunnan nimi")
        tutkinnot (luo-tutkintoja-opintoalaan 25 "OA1")
        ensimmainen_koulutustoimija (setup-koulutustoimija)
        toinen_koulutustoimija (setup-koulutustoimija)
        ensimmainen_oppilaitos (setup-oppilaitos (:ytunnus ensimmainen_koulutustoimija))
        toinen_oppilaitos (setup-oppilaitos (uusi-oppilaitostunnus!) "Toinen oppilaitos" (:ytunnus toinen_koulutustoimija))
        toimikunta (:tkunta (get-in toimikunnat [:toimikunnat 0]))
        tutkinto (get-in (vec tutkinnot) [0 :tutkintotunnus])
        ensimmainen_sopimus (setup-voimassaoleva-jarjestamissopimus "12345" (:ytunnus ensimmainen_koulutustoimija) (:oppilaitoskoodi ensimmainen_oppilaitos) toimikunta -1)
        toinen_sopimus (setup-voimassaoleva-jarjestamissopimus "23456" (:ytunnus toinen_koulutustoimija) (:oppilaitoskoodi toinen_oppilaitos) toimikunta -1)
        muu-testidata {:koulutusalat [{:koodi "KA1"
                                       :selite_fi "Koulutusalan nimi"}]
                       :opintoalat [{:koodi "OA1"
                                     :koulutusala "KA1"
                                     :selite_fi "Opintoalan nimi"}
                                    {:koodi "OA2"
                                     :koulutusala "KA1"
                                     :selite_fi "Toisen opintoalan nimi"}]
                       :tutkinnot tutkinnot
                       :toimikunta_ja_tutkinto [{:toimikunta toimikunta
                                                 :tutkintotunnus tutkinto}]}]
    (merge
      muu-testidata
      toimikunnat
      {:koulutustoimijat [ensimmainen_koulutustoimija toinen_koulutustoimija]}
      {:oppilaitokset [ensimmainen_oppilaitos toinen_oppilaitos]}
      (merge-datamaps ensimmainen_sopimus toinen_sopimus))))
