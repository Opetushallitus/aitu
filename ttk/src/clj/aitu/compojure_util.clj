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

(ns aitu.compojure-util
 (:require compojure.api.core
           [oph.compojure-util :as oph-cjure]
           [aitu.toimiala.kayttajaoikeudet :as ko]))

(defmacro autorisoitu-transaktio
  "Tarkastaa käyttöoikeudet ja hallitsee tietokanta-transaktion"
  [toiminto konteksti & body]
  (let [auth-map ko/toiminnot]
      `(oph-cjure/autorisoitu-transaktio ~auth-map ~toiminto ~konteksti ~@body)))

(defmacro autorisoi
  "Tarkastaa käyttöoikeudet"
  [toiminto konteksti & body]
  (let [auth-map ko/toiminnot]
      `(oph-cjure/autorisoi ~auth-map ~toiminto ~konteksti ~@body)))

(defmacro api [method path params & body]
  (let [[kw-args body] (split-with (comp keyword? first) (partition-all 2 body))
        kw-args (apply hash-map (apply concat kw-args))
        auth-info (str "Käyttöoikeus " (:kayttooikeus kw-args) " , konteksti: " (or (:konteksti kw-args) "N/A"))
        swagger-args (update (dissoc kw-args :kayttooikeus :konteksti)
                       :description #(str % (str " \n\n " auth-info)))]
    (assert (:kayttooikeus kw-args) "Käyttöoikeutta ei ole määritelty")
    `(~method ~path ~params ~@(apply concat swagger-args)
       (autorisoitu-transaktio ~(:kayttooikeus kw-args) ~(:konteksti kw-args) ~@(apply concat body)))))

(defmacro GET* [& args] `(api compojure.api.core/GET ~@args))
(defmacro POST* [& args] `(api compojure.api.core/POST ~@args))
(defmacro PUT* [& args] `(api compojure.api.core/PUT ~@args))
(defmacro DELETE* [& args] `(api compojure.api.core/DELETE ~@args))
