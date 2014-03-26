create or replace view aituhaku.tutkinnot_view as
  select
    t.tutkintotunnus, t.nimi_fi, t.nimi_sv, t.tutkintotaso,
    o.selite_fi as opintoala_nimi_fi, o.selite_sv as opintoala_nimi_sv,
    ka.selite_fi as koulutusala_nimi_fi, ka.selite_sv as koulutusala_nimi_sv,
    tv.voimassa_alkupvm, tv.voimassa_loppupvm, tv.siirtymaajan_loppupvm,
    o.opintoala_tkkoodi as opintoala_tkkoodi
  from nayttotutkinto t
  inner join tutkintoversio tv on tv.tutkintoversio_id = t.uusin_versio_id
  inner join opintoala o on o.opintoala_tkkoodi = t.opintoala
  inner join koulutusala ka on ka.koulutusala_tkkoodi = o.koulutusala_tkkoodi;

create or replace view aituhaku.opintoala_view as
  select
    o.selite_fi as opintoala_nimi_fi, o.selite_sv as opintoala_nimi_sv,
    o.opintoala_tkkoodi, o.voimassa_alkupvm, o.voimassa_loppupvm
  from opintoala o
  where exists(select 1 from nayttotutkinto where opintoala=o.opintoala_tkkoodi);