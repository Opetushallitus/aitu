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

(ns aitu.test-api.jarjestamissopimus
  "Testien tarvitsemat REST-rajapinnat jos niitä on. Näitä ei tarvita normaalista käyttöliittymästä koskaan"
  (:require [compojure.core :as c]
            [aitu.infra.jarjestamissopimus-arkisto :as arkisto]
            [korma.db :as db]
            [oph.common.util.http-util :refer [parse-iso-date]]))

(c/defroutes reitit
  (c/DELETE "/:jarjestamissopimusid" [jarjestamissopimusid]
    (db/transaction
      (arkisto/poista! (Integer/parseInt jarjestamissopimusid))
      {:status 200}))
  (c/DELETE "/:jarjestamissopimusid/tutkinnot" [jarjestamissopimusid]
    (db/transaction
      (arkisto/poista-kaikki-tutkinnot-sopimukselta! (Integer/parseInt jarjestamissopimusid))
      {:status 200}))
  (c/DELETE "/:jarjestamissopimusid/suunnitelma" [jarjestamissopimusid]
    (db/transaction
      (arkisto/poista-sopimuksen-suunnitelmat! (Integer/parseInt jarjestamissopimusid))
      {:status 200}))
  (c/POST "/:jarjestamissopimusid/suunnitelma" [jarjestamissopimusid tutkintoversio jarjestamissuunnitelma_content_type jarjestamissuunnitelma_filename]
    (db/transaction
      (arkisto/lisaa-suunnitelma-kantaan! (Integer/parseInt jarjestamissopimusid) tutkintoversio jarjestamissuunnitelma_content_type jarjestamissuunnitelma_filename)
      {:status 200})))
