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

(ns aitu.test-api.ttk
  "Testien tarvitsemat REST-rajapinnat jos niitä on. Näitä ei tarvita normaalista käyttöliittymästä koskaan"
  (:require [compojure.core :as c]
            [aitu.infra.ttk-arkisto :as ttk-arkisto]
            [korma.db :as db]))

(c/defroutes reitit
  (c/DELETE "/" []
    (db/transaction
      (ttk-arkisto/tyhjenna!)
      {:status 200}))
  (c/DELETE "/:tkunta" [tkunta]
    (db/transaction
      (ttk-arkisto/poista! tkunta)
      {:status 200}))
  (c/DELETE "/:tkunta/jasen/:jasenid" [tkunta jasenid]
    (db/transaction
      (ttk-arkisto/poista-jasen! tkunta (java.lang.Integer/parseInt jasenid))
      {:status 200}))
  (c/DELETE "/:tkunta/jasen" [tkunta]
    (db/transaction
      (ttk-arkisto/poista-jasenet! tkunta)
      {:status 200}))
  (c/POST "/:tkunta/tutkinto" [tkunta tutkintotunnus]
    (ttk-arkisto/lisaa-tutkinto! {:toimikunta tkunta
                              :tutkintotunnus tutkintotunnus})
    {:status 200})

  (c/DELETE "/:tkunta/tutkinto/:tutkintotunnus" [tkunta tutkintotunnus]
    (ttk-arkisto/poista-tutkinto! tkunta tutkintotunnus)
    {:status 200}))
