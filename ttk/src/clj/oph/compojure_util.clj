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

(ns oph.compojure-util
  (:require [clojure.tools.logging :as log]
            compojure.core
            [korma.db :as db]
            [oph.common.util.util :refer [retrying]]
            [compojure.api.sweet :as c]))

(def http-compojure {
  :get 'compojure.core/GET
  :post 'compojure.core/POST
  :delete 'compojure.core/DELETE
  :put 'compojure.core/PUT
  :update 'compojure.core/UPDATE})

(defmacro autorisoi
  "Tarkastaa käyttöoikeudet"
  [auth-map toiminto konteksti & body]
  (let [auth-fn (get auth-map toiminto)]
    (assert (not (nil? auth-fn)) (str "Toimintoa ei ole määritelty:  " toiminto))
    (log/info (str "auth-check " auth-fn " context param " (or konteksti "N/A")))
    `(do
       (if (~auth-fn ~@(when konteksti [konteksti]))
         (do
           ~@body)
         (do 
           (log/error "Käyttöoikeudet eivät riitä. Toiminto estetty.")
           (throw (RuntimeException. "Käyttöoikeudet eivät riitä.")))))))


(defmacro autorisoitu-transaktio
  "Tarkastaa käyttöoikeudet ja hallitsee tietokanta-transaktion"
  [auth-map toiminto konteksti & body]
  `(retrying Exception 3
     (korma.db/transaction
       (autorisoi ~auth-map ~toiminto ~konteksti ~@body))))

(defmacro defapi
  "Esittelee rajapinta-funktion sisältäen käyttöoikeuksien tarkastamisen ja tietokanta-transaktion hallinnan."
  [auth-map toiminto konteksti-arg http-method path args & body]
  (let [compojure-macro (get http-compojure http-method)
        _ (log/info (str "declared " toiminto " on API: " http-method " at " path args))]
    `(~compojure-macro ~path ~args
       (autorisoitu-transaktio ~auth-map ~toiminto ~konteksti-arg
         ~@body))))
