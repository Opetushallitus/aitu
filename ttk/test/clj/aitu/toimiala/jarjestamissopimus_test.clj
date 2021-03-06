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

(ns aitu.toimiala.jarjestamissopimus-test
  (:require [clojure.test :refer [deftest is are testing]]
            [aitu.toimiala.jarjestamissopimus :refer :all]
            [clj-time.core :as time]))

(deftest jarjestamissopimus-validointi
   (is (jarjestamissopimus? {:sopimusnumero "12345"
                             :koulutustoimija "9999999-9"
                             :tutkintotilaisuuksista_vastaava_oppilaitos "2013/01/001"
                             :toimikunta "TTK1"
                             :alkupvm (time/now)}))
   (is (not (jarjestamissopimus? {; sopimusnumero puuttuu
                                  :koulutustoimija "9999999-9"
                                  :oppilaitos "2013/01/001"
                                  :toimikunta "TTK1"
                                  :alkupvm (time/now)}))))
