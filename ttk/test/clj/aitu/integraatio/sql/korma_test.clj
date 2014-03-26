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

(ns aitu.integraatio.sql.korma-test
    (:use [aitu.integraatio.sql.korma])
    (:require [clojure.test :refer [deftest testing is are]]))

(deftest korma-asetukset-test
  (testing "korma-asetukset"
    (testing "käyttää asetuksissa annettuja tietokannan koordinaatteja"
       (let [k (korma-asetukset {:host "palvelin"
                                 :port "12345"
                                 :name "kanta"
                                 :user "user"
                                 :password "pass"
                                 :minimum-pool-size "3"
                                 :maximum-pool-size "15"})]
         (are [avain arvo] (= arvo (get k avain))
           :host "palvelin"
           :port 12345
           :db "kanta"
           :user "user"
           :password "pass"
           :minimum-pool-size 3
           :maximum-pool-size 15)))))
