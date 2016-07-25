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

(ns aitu.toimiala.henkilo
  (:require [aitu.toimiala.skeema :refer [SisaltaaHenkilonTiedot]]
            [schema.core :as s]
            [aitu.toimiala.voimassaolo.saanto.toimikunta :as toimikunta-saanto]
            [aitu.toimiala.voimassaolo.saanto.jasenyys :as jasenyys-saanto]
            [aitu.infra.kayttaja-arkisto :as kayttaja-arkisto])
  (:import org.joda.time.DateTime))

(def ei-julkiset-kentat [:lisatiedot :syntymavuosi])

(defn poista-salaiset-henkilolta
  "Poistaa yhden henkilön ei julkisiksi määritellyt kentät"
  [henkilo]
  (let [yhteystiedot (cond-> henkilo
                       (not (:sahkoposti_julkinen henkilo)) (dissoc :sahkoposti)
                       (not (:puhelin_julkinen henkilo)) (dissoc :puhelin)
                       (not (:osoite_julkinen henkilo)) (dissoc :osoite :postinumero :postitoimipaikka))]
    (apply dissoc yhteystiedot ei-julkiset-kentat)))

(defn piilota-salaiset-henkiloilta
  "Tyhjentää salaiseksi määritellyt kentät henkilöiltä"
  [henkilot]
  (map poista-salaiset-henkilolta henkilot))

(defn piilota-salaiset
  "Piilottaa vastauksesta henkilöiden tiedoista salaiseksi määritellyt kentät"
  [vastaus & [henkilo-avain]]
  (if henkilo-avain
    (update-in vastaus [henkilo-avain] piilota-salaiset-henkiloilta)
    (piilota-salaiset-henkiloilta vastaus)))

(defn taydenna-jasenyys
  "Täydentää jäsenyyden ja sen alta löytyvän toimikunnan voimassaolon"
  [jasenyys]
  (-> jasenyys
    (update-in [:ttk] toimikunta-saanto/taydenna-toimikunnan-voimassaolo)
    (#(jasenyys-saanto/taydenna-jasenyyden-voimassaolo % (get-in % [:ttk :voimassa])))))

(defn liita-kayttaja
  "Liittää henkilön tietoihin henkilöön liitetyn käyttäjän tiedot"
  [henkilo]
  (let [kayttaja (kayttaja-arkisto/hae (:kayttaja_oid henkilo))]
    (-> henkilo
      (dissoc :kayttaja_oid)
      (assoc :kayttaja kayttaja))))

(s/defn taydenna-henkilo :- SisaltaaHenkilonTiedot
  "Täydentää henkilon tiedot, kuten jäsenyyksien ja toimikuntien voimassaolo"
  [henkilo :- SisaltaaHenkilonTiedot]
  (some-> henkilo
         (update-in [:jasenyys] #(map taydenna-jasenyys %))
         liita-kayttaja))