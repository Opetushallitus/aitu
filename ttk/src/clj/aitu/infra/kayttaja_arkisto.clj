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

(ns aitu.infra.kayttaja-arkisto
  (:require [korma.core :as sql]
            [korma.db :as db]
            [clojure.tools.logging :as log]
            [aitu.integraatio.sql.korma :as taulut :refer [ilike]]
            [aitu.toimiala.kayttajaroolit :refer [kayttajaroolit]]
            [oph.common.util.util :refer [sisaltaako-kentat?]]
            [oph.korma.korma-auth :refer [*current-user-uid*
                                          *current-user-oid*
                                          integraatiokayttaja
                                          konversiokayttaja
                                          jarjestelmakayttaja]]
            [oph.korma.common :refer [select-unique-or-nil select-unique]]))

(defn kayttaja-liitetty-henkiloon?
  [henkiloid oid]
  (and
    (not (nil? oid))
    (= oid (:kayttaja_oid (select-unique-or-nil taulut/henkilo
                            (sql/where {:henkiloid henkiloid})
                            (sql/fields :kayttaja_oid))))))

(defn kayttaja-liitetty-johonkin-henkiloon?
  [oid]
  (when oid
    (boolean (seq (sql/select taulut/henkilo
                    (sql/where {:kayttaja_oid oid}))))))

(defn hae-kayttajaan-liitetty-henkilo
  [oid]
  (select-unique-or-nil taulut/henkilo (sql/where {:kayttaja_oid oid})))

(defn hae
  "Hakee käyttäjätunnuksen perusteella. Palauttaa nil jos ei löydy."
  [oid]
  (select-unique-or-nil taulut/kayttaja (sql/where {:oid oid})))

(defn ^:integration-api merkitse-kaikki-vanhentuneiksi! []
  (sql/update taulut/kayttaja
    (sql/set-fields {:voimassa false})
    (sql/where {:oid [not-in [jarjestelmakayttaja konversiokayttaja integraatiokayttaja]]})))

(defn olemassa? [k]
  (boolean (hae (:oid k))))

(defn ^:integration-api paivita!
  "Päivittää käyttäjätaulun uusilla käyttäjillä kt."
  [kt]
  {:pre [(= *current-user-uid* integraatiokayttaja)]}
  (db/transaction
    ;; Merkitään nykyiset käyttäjät ei-voimassaoleviksi
    (log/debug "Merkitään olemassaolevat käyttäjät ei-voimassaoleviksi")
    (sql/update taulut/kayttaja
      (sql/set-fields {:voimassa false})
      (sql/where {:luotu_kayttaja [= @*current-user-oid*]}))
    (doseq [k kt]
      (log/debug "Päivitetään käyttäjä" (pr-str k))
      (if (olemassa? k)
        ;; Päivitetään olemassaoleva käyttäjä merkiten voimassaolevaksi
        (do
          (log/debug "Käyttäjä on jo olemassa, päivitetään tiedot")
          (sql/update taulut/kayttaja
          (sql/set-fields (assoc k :voimassa true))
          (sql/where {:oid [= (:oid k)]})))
        ;; Lisätään uusi käyttäjä
        (do
          (log/debug "Luodaan uusi käyttäjä")
          (sql/insert taulut/kayttaja (sql/values k)))))))

(defn hae-impersonoitava-termilla
  "Hakee impersonoitavia käyttäjiä termillä"
  [termi]
  (for [kayttaja (sql/select taulut/kayttaja
                   (sql/fields :oid :uid :etunimi :sukunimi)
                   (sql/where (and (not= :rooli (:yllapitaja kayttajaroolit))
                                   (not= :rooli (:osoitepalvelu kayttajaroolit)))))
        :when (sisaltaako-kentat? kayttaja [:etunimi :sukunimi] termi)]
    {:nimi (str (:etunimi kayttaja) " " (:sukunimi kayttaja) " (" (:uid kayttaja) ")")
     :oid (:oid kayttaja)}))

(defn hae-toimikuntakayttajat-termilla
  "Hakee toimikuntakäyttäjiä termillä uid:stä"
  [termi]
  (sql/select taulut/kayttaja
    (sql/fields :oid :uid :etunimi :sukunimi)
    (sql/where {:rooli "KAYTTAJA"
                :uid [ilike (str \% termi \%)]})
    (sql/order :sukunimi)
    (sql/order :etunimi)
    (sql/order :uid)))

(defn hae-jarjesto
  [jarjestoid]
  (select-unique-or-nil :jarjesto
    (sql/fields :jarjestoid :nimi_fi :nimi_sv)
    (sql/where {:jarjestoid jarjestoid})))
