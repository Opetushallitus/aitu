;; Copyright (c) 2015 The Finnish National Board of Education - Opetushallitus
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

(ns aitu.rest-api.jasenesitykset
  (:require [compojure.core :as c]
            [aitu.compojure-util :as cu]
            [aitu.infra.jasenesitykset-arkisto :as arkisto]
            [aitu.toimiala.kayttajaoikeudet :as ko]
            [aitu.util :refer [muodosta-csv]]
            [oph.common.util.http-util :refer [csv-download-response json-response]]))

(def ^:private kenttien-jarjestys [:etunimi :sukunimi
                                   :esittaja_henkilo_henkiloid :esittaja_henkilo_etunimi :esittaja_henkilo_sukunimi
                                   :esittaja_keskusjarjesto_nimi_fi :esittaja_keskusjarjesto_nimi_sv
                                   :esittaja :esittaja_jarjesto_nimi_fi :esittaja_jarjesto_nimi_sv
                                   :tutkintotoimikunta_diaarinumero :toimikunta :tutkintotoimikunta_nimi_fi :tutkintotoimikunta_nimi_sv
                                   :luotuaika
                                   :nimityspaiva
                                   :rooli
                                   :edustus
                                   :muutettuaika
                                   :status])

(c/defroutes reitit-csv
  (cu/defapi :jasenesitykset nil :get "/csv" [& ehdot]
    (let [jarjesto (:jarjesto ko/*current-user-authmap*)]
      (csv-download-response (muodosta-csv (arkisto/hae jarjesto ehdot)
                                           kenttien-jarjestys)
                             "jasenesitykset.csv"))))

(c/defroutes reitit
  (cu/defapi :jasenesitykset nil :get "/" [& ehdot]
    (let [jarjesto (:jarjesto ko/*current-user-authmap*)]
      (json-response (arkisto/hae jarjesto ehdot))))
  (cu/defapi :jasenesitykset nil :get "/yhteenveto" [toimikausi vain_jasenesityksia_sisaltavat]
    (let [jarjesto (:jarjesto ko/*current-user-authmap*)]
      (json-response (arkisto/hae-yhteenveto jarjesto (Integer/parseInt toimikausi) (Boolean/valueOf vain_jasenesityksia_sisaltavat))))))
