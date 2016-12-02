(ns aitu.infra.suoritus-raportti)

(defn ^:private suoritus-raportti [s]
  (cons {:suorittaja_etunimi (:suorittaja_etunimi s)
         :suorittaja_sukunimi (:suorittaja_sukunimi s)
         :arvosana (:arvosana s)
         :kokotutkinto (:kokotutkinto s)
         :todistus (:todistus s)}
        (:arvioijat s)))

(defn ^:private tutkinnonosa-raportti [t]
  (cons {:osatunnus (:osatunnus t)
         :tutkinnonosa_fi (:tutkinnonosa_nimi_fi t)}
        (mapcat suoritus-raportti (:suoritukset t))))

(defn ^:private tutkinto-raportti [t]
  (cons {:tutkintotunnus (:tutkintotunnus t)
         :tutkinto_fi (:tutkinto_nimi_fi t)
         :peruste (:tutkinto_peruste t)}
        (mapcat tutkinnonosa-raportti (:tutkinnonosat t))))

(defn ^:private koulutustoimija-raportti [kt]
  (cons {:ytunnus (:ytunnus kt)
         :koulutustoimija_fi (:koulutustoimija_nimi_fi kt)}
        (mapcat tutkinto-raportti (:tutkinnot kt))))

(defn ^:private toimikunta-raportti [t]
  (cons {:diaarinumero (:tutkintotoimikunta_diaarinumero t)
         :toimikunta_fi (:tutkintotoimikunta_nimi_fi t)}
        (mapcat koulutustoimija-raportti (:koulutustoimijat t))))

(defn yhteenveto-raportti-excel [data]
  (mapcat toimikunta-raportti data))
