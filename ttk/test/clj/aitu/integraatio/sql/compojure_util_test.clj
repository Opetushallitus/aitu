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

(ns aitu.integraatio.sql.compojure-util-test
  "Testataan auth-makron toteutusta. käyttöoikeuksien tarkistusfunktioille on erikseen yksikkötestit"
  (:require [aitu.compojure-util :as cu]
            [compojure.core :as c]
            [ring.mock.request :as rmock]
            [aitu.toimiala.kayttajaoikeudet :as ko]
            [korma.db :as db]
            [aitu.integraatio.sql.test-util :refer :all]
            [aitu.toimiala.kayttajaroolit :refer [kayttajaroolit]])
  (:use clojure.test))

(def sample-admin-api
  '(compojure.core/POST "/toimikunta" []
    (aitu.compojure-util/autorisoi :toimikunta_luonti nil
      {:status 200})))

(def sample-user-api
  '(compojure.core/GET ["/toimikunta/:diaarinumero" :diaarinumero #"[0-9/]+"] [diaarinumero]
    (aitu.compojure-util/autorisoi :toimikunta_katselu diaarinumero
      {:status 200})))

(defn with-admin-rights [f]
 (binding [ko/*current-user-authmap* {:roolitunnus (:yllapitaja kayttajaroolit)}]
   (f)))

(deftest yllapitaja-saa-lisata-toimikunnan []
  (let [crout (eval sample-admin-api)
        response (with-admin-rights #(crout (rmock/request :post "/toimikunta")))]
    (is (= (:status response) 200))))

(deftest kayttaja-ei-saa-lisata-toimikuntaa []
  (let [crout (eval sample-admin-api)]
    (is (thrown? Throwable
                 (with-user-rights #(crout (rmock/request :post "/toimikunta")))))))

(deftest kayttaja-saa-katsoa-oman-toimikuntansa-tiedot []
  (let [crout (eval sample-user-api)
        response (with-user-rights #(crout (rmock/request :get "/toimikunta/123")))]
    (is (= (:status response) 200))))

(deftest yllapitaja-saa-katsoa-toimikunnan-tiedot []
  (let [crout (eval sample-user-api)
        response (with-admin-rights #(crout (rmock/request :get "/toimikunta/123")))]
    (is (= (:status response) 200))))

(deftest kayttaja-saa-katsoa-toisen-toimikunnan-tietoja []
  (let [crout (eval sample-user-api)
        response (with-user-rights #(crout (rmock/request :get "/toimikunta/451")))]
    (is (= (:status response) 200))))
