(ns aitu.infra.suoritus-raportti)

(defn ^:private merge-first [m coll]
  (cons (merge m (first coll)) (rest coll)))

(defn ^:private suoritus-raportti [s]
  (merge-first {:suorittaja_etunimi  (:suorittaja_etunimi s)
                :suorittaja_sukunimi (:suorittaja_sukunimi s)
                :arvosana            (:arvosana s)
                :kokotutkinto        (if (:kokotutkinto s) "kyllä" "ei")
                :todistus            (if (:todistus s) "kyllä" "ei")}
               (:arvioijat s)))

(defn ^:private tutkinnonosa-raportti [t]
  (merge-first {:osatunnus       (:osatunnus t)
                :tutkinnonosa_fi (:tutkinnonosa_nimi_fi t)}
               (mapcat suoritus-raportti (:suoritukset t))))

(defn ^:private tutkinto-raportti [t]
  (merge-first {:tutkintotunnus (:tutkintotunnus t)
                :tutkinto_fi    (:tutkinto_nimi_fi t)
                :peruste        (:tutkinto_peruste t)}
               (mapcat tutkinnonosa-raportti (:tutkinnonosat t))))

(defn ^:private koulutustoimija-raportti [kt]
  (merge-first {:ytunnus            (:ytunnus kt)
                :koulutustoimija_fi (:koulutustoimija_nimi_fi kt)}
               (mapcat tutkinto-raportti (:tutkinnot kt))))

(defn ^:private toimikunta-raportti [t]
  (merge-first {:diaarinumero  (:tutkintotoimikunta_diaarinumero t)
                :toimikunta_fi (:tutkintotoimikunta_nimi_fi t)}
               (mapcat koulutustoimija-raportti (:koulutustoimijat t))))

(defn yhteenveto-raportti-excel [data]
  (mapcat toimikunta-raportti data))
