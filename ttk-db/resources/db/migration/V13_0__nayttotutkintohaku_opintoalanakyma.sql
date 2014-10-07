-- OPH-898, laajennetaan opintoalanäkymää siten että se sisältää myös tiedon koulutusaloista
create or replace view aituhaku.opintoala_view as
  select
    o.selite_fi as opintoala_nimi_fi, o.selite_sv as opintoala_nimi_sv,
    o.opintoala_tkkoodi, o.voimassa_alkupvm, o.voimassa_loppupvm,
    k.selite_fi as koulutusala_nimi_fi, k.selite_sv as koulutusala_nimi_sv,
    k.koulutusala_tkkoodi
  from opintoala o
    inner join koulutusala k on k.koulutusala_tkkoodi = o.koulutusala_tkkoodi
  where exists(select 1 from nayttotutkinto where opintoala=o.opintoala_tkkoodi);
