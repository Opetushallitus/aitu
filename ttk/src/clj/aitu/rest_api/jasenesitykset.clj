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
  (:require [compojure.api.core :refer [DELETE GET defroutes]]
            [clj-time.coerce :as time-coerce]
            [aitu.infra.jasenesitykset-arkisto :as arkisto]
            [aitu.toimiala.henkilo :as henkilo]
            [aitu.toimiala.kayttajaoikeudet :as ko]
            [aitu.util :refer [muodosta-csv convert-values] :as aitu-util]
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
    {:asiantuntijaksi "Käytettävissä asiantuntijaksi"
     :vapaateksti_kokemus "Kokemus-tieto"
     :syntymavuosi "Syntymävuosi"
     :sukupuoli "Sukupuoli"
     :sahkoposti_julkinen "Sähköposti julkinen"
     :puhelin_julkinen "Puhelinnumero julkinen"
     :osoite_julkinen "Osoite julkinen"
     :nayttomestari "Näyttömestari"
     :kokemusvuodet "Kokemusvuodet"
     :lisatiedot "Lisätiedot-tieto"
     :esittaja_henkilo_etunimi "Esittäjän etunimi"
     :esittaja_henkilo_sukunimi "Esittäjän sukunimi"
     :esittaja_keskusjarjesto_nimi_fi "Esittäjän keskusjärjestö"
     :esittaja_jarjesto_nimi_fi "Esittäjän järjestö"
     :tutkintotoimikunta_diaarinumero "Toimikunnan diaarinumero"
     :tutkintotoimikunta_nimi_fi "Toimikunnan nimi"
     :luotuaika "Esityspvm"
     :nimityspaiva "Nimityspvm"
     :muutettuaika "Muutettu"
     :status "Tila"}))

(def ^:private csv-poistettavat-kentat [:henkiloid :esittaja_henkilo_henkiloid :esittaja_keskusjarjesto_nimi_sv :esittaja :esittaja_jarjesto_nimi_sv :toimikunta :tutkintotoimikunta_nimi_sv :luotu_kayttaja :jasenyys_id])

(defroutes reitit-csv
  (GET "/csv" [& ehdot]
    :kayttooikeus :jasenesitykset
    (let [jarjesto (:jarjesto ko/*current-user-authmap*)]
      (-> (arkisto/hae jarjesto ehdot true)
          (->>
            (map #(apply dissoc % csv-poistettavat-kentat))
            (map #(update % :luotuaika time-coerce/to-local-date))
            (map #(update % :muutettuaika time-coerce/to-local-date)))
          henkilo/piilota-salaiset-henkiloilta
          convert-values
          (muodosta-csv kenttien-jarjestys sarakkeiden-otsikot)
          (csv-download-response "jasenesitykset.csv")))))

(defroutes reitit
  (GET "/" [& ehdot]
    :kayttooikeus :jasenesitykset
    (let [jarjesto (:jarjesto ko/*current-user-authmap*)]
      (json-response (arkisto/hae jarjesto ehdot false))))
  (DELETE "/jasenyys/:jasenyysid" []
    :path-params [jasenyysid]
    :kayttooikeus [:jasenesitys-poisto jasenyysid]
    (json-response (arkisto/poista! (Integer/parseInt jasenyysid))))
  (GET "/yhteenveto" [toimikausi vain_jasenesityksia_sisaltavat]
    :kayttooikeus :jasenesitykset
    (let [jarjesto (:jarjesto ko/*current-user-authmap*)]
      (json-response (arkisto/hae-yhteenveto jarjesto (Integer/parseInt toimikausi) (Boolean/valueOf vain_jasenesityksia_sisaltavat))))))
