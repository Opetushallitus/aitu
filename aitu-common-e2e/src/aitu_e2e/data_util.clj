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

(ns aitu-e2e.data-util
  (:require [aitu-e2e.util :refer [aitu-url]]
            [aitu-e2e.http :as h]
            [cheshire.core :as json]
            [clj-time.core :as time]
            [clj-time.format :as time-format]
            [cheshire.generate :as json-gen]))

(json-gen/add-encoder org.joda.time.DateTime
  (fn [c json-generator]
    (.writeString json-generator (str c))))
(json-gen/add-encoder org.joda.time.LocalDate
  (fn [c json-generator]
    (.writeString json-generator (str c))))

(defn paivamaara-iso-muodossa [paivamaara]
  (time-format/unparse-local-date (time-format/formatters :year-month-day)
                                  paivamaara))
(defn paivamaara-kayttoliittyman-muodossa [paivamaara]
  (time-format/unparse-local-date (time-format/formatter "dd.MM.yyyy")
                                  paivamaara))

(def kuukausi-sitten (paivamaara-iso-muodossa (time/minus (time/today) (time/months 1))))
(def tanaan (paivamaara-iso-muodossa (time/today)))

(def menneisyydessa-pvm (time/minus (time/today) (time/days 1)))
(def tulevaisuudessa-pvm (time/plus (time/today) (time/days 1)))
(def menneisyydessa (paivamaara-iso-muodossa menneisyydessa-pvm))
(def tulevaisuudessa (paivamaara-iso-muodossa tulevaisuudessa-pvm))
(def menneisyydessa-kayttoliittyman-muodossa (paivamaara-kayttoliittyman-muodossa menneisyydessa-pvm))
(def tulevaisuudessa-kayttoliittyman-muodossa (paivamaara-kayttoliittyman-muodossa tulevaisuudessa-pvm))

(def henkilo-tiedot {:post-fn #(str "/api/test/henkilo/" (:henkiloid %))
                     :delete-fn #(str "/api/test/henkilo/" (:henkiloid %))
                     :default (for [i (iterate inc 1)]
                                {:henkiloid (- i)
                                 :etunimi (str "etu" i)
                                 :sukunimi (str "suku" i)})})

(def toimikunta-tiedot {:post-fn (constantly "/api/ttk")
                        :delete-fn #(str "/api/test/ttk/" (:tkunta %))
                        :default (for [i (iterate inc 1)]
                                   {:tkunta (str "TTK" i)
                                    :nimi_fi (str "Toimikunta " i)
                                    :nimi_sv (str "Toimikunta " i)
                                    :diaarinumero (str "13/04/" i)
                                    :tilikoodi (str i)
                                    :toimiala (str "Toimiala " i)
                                    :toimikausi 2
                                    :kielisyys "fi"
                                    :sahkoposti (str "toimikunta" i "@mail.fi")
                                    :toimikausi_alku "2013-08-01"
                                    :toimikausi_loppu "2016-07-31"})})

(def jasen-tiedot {:post-fn #(str "/api/ttk/" (:diaarinumero %) "/jasenet")
                   :delete-fn #(str "/api/test/ttk/" (:toimikunta %) "/jasen/" (:henkiloid (:henkilo %)))
                   :default (for [i (iterate inc 1)]
                              {:henkilo {:henkiloid -1}
                               :diaarinumero (str "13/04/" i)
                               :alkupvm kuukausi-sitten
                               :loppupvm tulevaisuudessa
                               :rooli "jasen"
                               :edustus "opettaja"
                               :toimikunta "T850"})})

(def opintoala-tiedot {:post-fn (constantly "/api/test/opintoala")
                       :delete-fn #(str "/api/test/opintoala/" (:koodi %))
                       :default (for [i (iterate inc 1)]
                                  {:koodi (str (- i))
                                   :selite_fi (str "Opintoala " i)
                                   :selite_sv (str "Opintoala (sv)" i)})})

(def koulutusala-tiedot {:post-fn (constantly "/api/test/koulutusala")
                         :delete-fn #(str "/api/test/koulutusala/" (:koodi %))
                         :default (for [i (iterate inc 1)]
                                    {:koodi (str (- i))
                                     :selite_fi (str "Koulutusala " i)
                                     :selite_sv (str "Koulutusala (sv)" i)})})

(def peruste-tiedot {:post-fn (constantly "/api/test/peruste")
                     :delete-fn #(str "/api/test/peruste/" (:diaarinumero %))
                     :default (for [i (iterate inc 1)]
                                {:diaarinumero (str i "/04/13")
                                 :alkupvm kuukausi-sitten})})

(def tutkinto-tiedot {:post-fn (constantly "/api/test/tutkinto")
                      :delete-fn #(str "/api/test/tutkinto/" (:tutkintotunnus %))
                      :default (for [i (iterate inc 1)]
                                 {:tutkintotunnus (str (- i))
                                  :tutkintoversio_id (- i)
                                  :versio 1
                                  :koodistoversio 1
                                  :nimi_fi (str "Tutkinto " i)
                                  :nimi_sv (str "Tutkinto (sv)" i)
                                  :opintoala "24"
                                  :tyyppi "02"
                                  :tutkintotaso "perustutkinto"
                                  :voimassa_alkupvm menneisyydessa
                                  :voimassa_loppupvm tulevaisuudessa})})

(def tutkintoversio-tiedot {:post-fn (constantly "/api/test/tutkintoversio")
                            :delete-fn #(str "/api/test/tutkintoversio/" (:tutkintoversio_id %))
                            :default (for [i (iterate inc 1)]
                                       {:voimassa_alkupvm menneisyydessa
                                        :voimassa_loppupvm tulevaisuudessa})})

(def tutkintotyyppi-tiedot {:post-fn (constantly "/api/test/tutkintotyyppi")
                            :delete-fn #(str "/api/test/tutkintotyyppi/" (:tyyppi %))
                            :default (for [i (iterate inc 1)]
                                       {:tyyppi (str (- i))
                                        :selite_fi (str "Tutkintotyyppi " i)
                                        :selite_sv (str "Tutkintotyyppi (sv) " i)})})

(def toimikunta-ja-tutkinto-tiedot {:post-fn #(str "/api/test/ttk/" (:toimikunta %) "/tutkinto")
                                    :delete-fn #(str "/api/test/ttk/" (:toimikunta %) "/tutkinto/" (:tutkintotunnus %))
                                    :default (repeat {})})

(def koulutustoimija-tiedot {:post-fn (constantly "/api/test/koulutustoimija/")
                             :delete-fn #(str "/api/test/koulutustoimija/" (:ytunnus %))
                             :default (for [i (iterate inc 1)]
                                        {:ytunnus (str (- i))
                                         :nimi_fi (str "Koulutustoimija " i)
                                         :nimi_sv (str "Koulutustoimija " i)})})

(def oppilaitos-tiedot {:post-fn (constantly "/api/test/oppilaitos/")
                        :delete-fn #(str "/api/test/oppilaitos/" (:oppilaitoskoodi %))
                        :default (for [i (iterate inc 1)]
                                   {:oppilaitoskoodi (str (- i))
                                    :nimi (str "Oppilaitos " i)
                                    :alue "etelasuomi"
                                    :kieli "1"
                                    :koulutustoimija (str (- i))})})

(def toimipaikka-tiedot {:post-fn #(str "/api/test/oppilaitos/toimipaikka/" (:oppilaitos %))
                         :delete-fn #(str "/api/test/oppilaitos/toimipaikka/" (:toimipaikkakoodi %))
                         :default (for [i (iterate inc 1)]
                                    {:toimipaikkakoodi (str (- i))})})

(def jarjestamissopimus-tiedot {:post-fn #(str "/api/jarjestamissopimus/" (:toimikunta %))
                                :delete-fn #(str "/api/test/jarjestamissopimus/" (:jarjestamissopimusid %))
                                :default (for [i (iterate inc 1)]
                                           {:alkupvm menneisyydessa
                                            :loppupvm tulevaisuudessa})})

(def jarjestamissuunnitelma-tiedot {:post-fn #(str "/api/test/jarjestamissopimus/" (:jarjestamissopimusid %) "/suunnitelma")
                                :delete-fn #(str "/api/test/jarjestamissopimus/" (:jarjestamissopimusid %) "/suunnitelma")
                                :default (for [i (iterate inc 1)]
                                           {:jarjestamissuunnitelma_filename "testfile.pdf"
                                            :jarjestamissuunnitelma_content_type "Application/pdf"})})

(def sopimus-ja-tutkinto-tiedot {:post-fn #(str "/api/jarjestamissopimus/" (:jarjestamissopimusid %) "/tutkinnot")
                                 :delete-fn #(str "/api/test/jarjestamissopimus/" (:jarjestamissopimusid %) "/tutkinnot")
                                 :default (repeat {})})

(def tutkinnon-osa-tiedot {:post-fn (constantly "/api/test/tutkinto/tutkinnonosa")
                           :delete-fn #(str "/api/test/tutkinto/tutkinnonosa/" (:osatunnus %))
                           :default (for [i (iterate inc 1)]
                                      {:osatunnus (str (- i))
                                       :versio 1
                                       :nimi (str "Tutkinnon osa " i)})})

(def osaamisala-tiedot {:post-fn (constantly "/api/test/tutkinto/osaamisala")
                       :delete-fn #(str "/api/test/tutkinto/osaamisala/" (:osaamisalatunnus %))
                       :default (for [i (iterate inc 1)]
                                      {:osaamisalatunnus (str (- i))
                                       :versio 1
                                       :nimi (str "Osaamisala " i)})})

(def tiedote-tiedot {:post-fn (constantly "/api/tiedote")
                     :delete-fn (constantly "/api/tiedote")
                     :default (repeat {:teksti_fi "Tiedote suomi"
                                       :teksti_sv "Tiedote ruotsi"})})

(def jarjesto-tiedot {:post-fn (constantly "/api/test/jarjesto")
                      :delete-fn #(str "/api/test/jarjesto/" (:jarjestoid %))
                      :default (repeat {:nimi_fi "Jarjesto suomi"
                                        :nimi_sv "Jarjesto ruotsi"
                                        :keskusjarjestotieto false})})

(def entity-tiedot {:henkilot henkilo-tiedot
                    :toimikunnat toimikunta-tiedot
                    :jasenet jasen-tiedot
                    :koulutusalat koulutusala-tiedot
                    :opintoalat opintoala-tiedot
                    :tutkinnonosat tutkinnon-osa-tiedot
                    :tutkinnot tutkinto-tiedot
                    :tutkintoversiot tutkintoversio-tiedot
                    :osaamisalat osaamisala-tiedot
                    :toimikunta_ja_tutkinto toimikunta-ja-tutkinto-tiedot
                    :koulutustoimijat koulutustoimija-tiedot
                    :oppilaitokset oppilaitos-tiedot
                    :toimipaikat toimipaikka-tiedot
                    :jarjestamissopimukset jarjestamissopimus-tiedot
                    :jarjestamissuunnitelmat jarjestamissuunnitelma-tiedot
                    :tutkintotyypit tutkintotyyppi-tiedot
                    :perusteet peruste-tiedot
                    :sopimus_ja_tutkinto sopimus-ja-tutkinto-tiedot
                    :tiedote tiedote-tiedot
                    :jarjestot jarjesto-tiedot})

(defn on-testikayttaja
  []
  (h/post (aitu-url "/api/test/e2e/e2e-user")))

(defn poista-testikayttaja
  []
  (h/delete (aitu-url "/api/test/e2e/e2e-user")))

(defn ^:private luo-tai-paivita-entityt
  [entityt url-fn metodi]
  (doseq [entity entityt]
    (metodi (aitu-url (url-fn entity))
      {:headers {"Content-Type" "application/json"
                 "Accept-Language" "fi"
                 "Cookie" "XSRF-TOKEN=e2e-xsrf-token"
                 "X-XSRF-TOKEN" "e2e-xsrf-token"}
       :body (json/generate-string entity)})))

(defn on-olemassa
  [entityt url-fn]
  (luo-tai-paivita-entityt entityt url-fn h/post))

(defn paivita-olemassa-olevat
  [entityt url-fn]
  (luo-tai-paivita-entityt entityt url-fn h/put))

(defn aseta-jarjestamissopimus-paattyneeksi
  [sopimus]
  (paivita-olemassa-olevat
    [(assoc sopimus :loppupvm menneisyydessa)]
    (constantly (str "/api/jarjestamissopimus/" (:jarjestamissopimusid sopimus)))))

(defn aseta-toimikunta-paattyneeksi
  [toimikunta]
  (paivita-olemassa-olevat
    [(assoc toimikunta :toimikausi_loppu menneisyydessa)]
    (constantly (str "/api/ttk/" (:diaarinumero toimikunta)))))

(defn poista
  [entityt url-fn]
  (doseq [entity entityt]
    (h/delete (aitu-url (url-fn entity)))))

(def ^:private taulut
  [:jarjestot :henkilot :toimikunnat :jasenet :koulutusalat :opintoalat :tutkintotyypit
   :perusteet :tutkinnot :tutkintoversiot :tutkinnonosat :osaamisalat :toimikunta_ja_tutkinto
   :koulutustoimijat :oppilaitokset :toimipaikat :jarjestamissopimukset :sopimus_ja_tutkinto :jarjestamissuunnitelmat
   :tiedote])

(defn ^:private taydenna-data
  [data]
  (into {}
        (for [[taulu entityt] data
              :let [default (get-in entity-tiedot [taulu :default])]]
          {taulu (map merge default entityt)})))

(defn poista-data! []
  (h/delete (aitu-url "/api/test/e2e/data")))

(defn with-data*
  [data body-fn cleanup]
  (let [taydennetty-data (taydenna-data data)]
    (doseq [taulu taulut
            :let [data (taydennetty-data taulu)
                  post-fn (get-in entity-tiedot [taulu :post-fn])]
            :when data]
      (on-olemassa data post-fn))
    (try
      (body-fn)
      (finally
        (if cleanup
          (poista-data!)
          (doseq [taulu (reverse taulut)
                  :let [data (taydennetty-data taulu)
                        delete-fn (get-in entity-tiedot [taulu :delete-fn])]
                  :when data]
            (poista data delete-fn)))))))

(defmacro with-data
  [data & body]
  `(with-data* ~data (fn [] ~@body) false))

(defmacro with-cleaned-data
  [data & body]
  `(with-data* ~data (fn [] ~@body) true))
