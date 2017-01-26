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

(ns aitu.infra.tutkinnonosa-arkisto
  (:require [korma.core :as sql]))

(defn hae-kaikki-uusimmat []
  (sql/select :tutkinnonosa
    (sql/where
      (not (sql/sqlfn exists 
        (sql/subselect :tutkinnonosa 
           (sql/where (and (> :tutkinnonosa.versio :versio)
                           (= :tutkinnonosa.osatunnus :osatunnus)))))))))

(defn hae-kaikki []
  (sql/select :tutkinnonosa))

; TODO: tämä hakee kaikkien versioiden tutkinnon osat tunnuksella. Se ei ole todennäköisesti se mitä kutsuja haluaisi, vaan uusimman version osat.
; Pitää käydä läpi paikat joissa tätä kutsutaan, semantiikkaa ei voi muuttaa sokkona.
(defn hae
  [tutkintotunnus]
  (->
    (sql/select* :tutkinnonosa)
    (sql/join :tutkintoversio (= :tutkintoversio.tutkintoversio_id :tutkinnonosa.tutkintoversio))
    (sql/join :nayttotutkinto (= :nayttotutkinto.tutkintotunnus :tutkintoversio.tutkintotunnus))
    (sql/fields :osatunnus :nimi_fi :nimi_sv :tutkinnonosa_id
                [:nayttotutkinto.nimi_fi :nayttotutkinto_nimi_fi]
                [:nayttotutkinto.nimi_sv :nayttotutkinto_nimi_sv])
    (cond-> tutkintotunnus (sql/where {:tutkintoversio.tutkintotunnus tutkintotunnus}))
    (sql/order :nayttotutkinto_nimi_fi :ASC)
    (sql/order :nimi_fi :ASC)
    sql/exec))

(defn hae-versiolla
  [tutkintoversio-id]
  (->
    (sql/select* :tutkinnonosa)
    (sql/join :tutkintoversio (= :tutkintoversio.tutkintoversio_id :tutkinnonosa.tutkintoversio))
    (sql/join :nayttotutkinto (= :nayttotutkinto.tutkintotunnus :tutkintoversio.tutkintotunnus))
    (sql/fields :osatunnus :nimi_fi :nimi_sv :tutkinnonosa_id
                [:nayttotutkinto.nimi_fi :nayttotutkinto_nimi_fi]
                [:nayttotutkinto.nimi_sv :nayttotutkinto_nimi_sv])
    (sql/where {:tutkintoversio.tutkintoversio_id tutkintoversio-id})
    (sql/order :nimi_fi :ASC)
    sql/exec))
  
