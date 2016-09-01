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

(ns aitu.infra.suoritus-excel-test
  (:require [clojure.test :refer [deftest testing is ]]
            [aitu.infra.suoritus-excel :refer :all]))

(deftest parse-osatunnus-toimii
  (is (= "1234" (parse-osatunnus "fo (1234)")))
  (is (= "12" (parse-osatunnus "kung (fu) (12)")))
  (is (thrown? IllegalArgumentException (parse-osatunnus "Kreegah bundolo")))
  (is (thrown? IllegalArgumentException (parse-osatunnus "Fo (12"))))
 

