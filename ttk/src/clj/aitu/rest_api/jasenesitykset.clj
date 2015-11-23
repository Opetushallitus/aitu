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
            [aitu.util :refer [muodosta-csv] :as aitu-util]
            [oph.common.util.http-util :refer [csv-download-response json-response]]))

(def ^:private kenttien-jarjestys [:henkiloid :etunimi :sukunimi :asiantuntijaksi :vapaateksti_kokemus
                                   ; Vain jos "Kaikki henkilötiedot" on ruksittu
                                   :syntymavuosi :sukupuoli :sahkoposti :sahkoposti_julkinen :puhelin :puhelin_julkinen :osoite :postinumero :postitoimipaikka :osoite_julkinen :nayttomestari :kokemusvuodet :lisatiedot

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

(def ^:private sarakkeiden-otsikot
  (merge
    aitu-util/sarakkeiden-otsikot
    {:henkiloid "Henkilöid"
     :asiantuntijaksi "Käytettävissä asiantuntijaksi"
     :vapaateksti_kokemus "Kokemus-tieto"
     :syntymavuosi "Syntymävuosi"
     :sukupuoli "Sukupuoli"
     :sahkoposti_julkinen "Sähköposti julkinen"
     :puhelin_julkinen "Puhelinnumero julkinen"
     :osoite_julkinen "Osoite julkinen"
     :nayttomestari "Näyttömestari"
     :kokemusvuodet "Kokemusvuodet"
     :lisatiedot "Lisätiedot-tieto"
     :esittaja_henkilo_henkiloid "Esittäjän henkilöid"
     :esittaja_henkilo_etunimi "Esittäjän etunimi"
     :esittaja_henkilo_sukunimi "Esittäjän sukunimi"
     :esittaja_keskusjarjesto_nimi_fi "Esittäjän keskusjärjestö suomeksi"
     :esittaja_keskusjarjesto_nimi_sv "Esittäjän keskusjärjestö ruotsiksi"
     :esittaja "Esittäjän järjestöid"
     :esittaja_jarjesto_nimi_fi "Esittäjän järjestö suomeksi"
     :esittaja_jarjesto_nimi_sv "Esittäjän järjestö ruotsiksi"
     :tutkintotoimikunta_diaarinumero "Toimikunnan diaarinumero"
     :toimikunta "Tutkintotoimikunta"
     :tutkintotoimikunta_nimi_fi "Toimikunnan nimi suomeksi"
     :tutkintotoimikunta_nimi_sv "Toimikunnan nimi ruotsiksi"
     :luotuaika "Esityspvm"
     :nimityspaiva "Nimityspvm"
     :muutettuaika "Muutettu"
     :status "Tila"}))

(c/defroutes reitit-csv
  (cu/defapi :jasenesitykset nil :get "/csv" [& ehdot]
    (let [jarjesto (:jarjesto ko/*current-user-authmap*)]
      (csv-download-response (muodosta-csv (arkisto/hae jarjesto ehdot true)
                                           kenttien-jarjestys
                                           sarakkeiden-otsikot)
                             "jasenesitykset.csv"))))

(c/defroutes reitit
  (cu/defapi :jasenesitykset nil :get "/" [& ehdot]
    (let [jarjesto (:jarjesto ko/*current-user-authmap*)]
      (json-response (arkisto/hae jarjesto ehdot false))))
  (cu/defapi :jasenesitys-poisto jasenyysid :delete "/jasenyys/:jasenyysid", [jasenyysid]
    (json-response (arkisto/poista! (Integer/parseInt jasenyysid))))
  (cu/defapi :jasenesitykset nil :get "/yhteenveto" [toimikausi vain_jasenesityksia_sisaltavat]
    (let [jarjesto (:jarjesto ko/*current-user-authmap*)]
      (json-response (arkisto/hae-yhteenveto jarjesto (Integer/parseInt toimikausi) (Boolean/valueOf vain_jasenesityksia_sisaltavat))))))
