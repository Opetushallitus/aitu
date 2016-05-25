create or replace view aituhaku.tutkinnot_view as
  select
    t.tutkintotunnus, t.nimi_fi, t.nimi_sv, t.tutkintotaso,
    o.selite_fi as opintoala_nimi_fi, o.selite_sv as opintoala_nimi_sv,
    ka.selite_fi as koulutusala_nimi_fi, ka.selite_sv as koulutusala_nimi_sv,
    tv.voimassa_alkupvm, tv.voimassa_loppupvm, tv.siirtymaajan_loppupvm,
    o.opintoala_tkkoodi as opintoala_tkkoodi, tv.peruste, tv.eperustetunnus,
    tv.tutkintoversio_id = t.uusin_versio_id as uusin_versio
  from nayttotutkinto t
  inner join tutkintoversio tv on tv.tutkintotunnus = t.tutkintotunnus
  inner join opintoala o on o.opintoala_tkkoodi = t.opintoala
  inner join koulutusala ka on ka.koulutusala_tkkoodi = o.koulutusala_tkkoodi
  where exists (select 1
                from jarjestamissopimus js
                inner join sopimus_ja_tutkinto st on js.jarjestamissopimusid = st.jarjestamissopimusid
                inner join tutkintoversio tv2 on st.tutkintoversio = tv2.tutkintoversio_id
                where js.voimassa and not js.poistettu and tv2.tutkintotunnus = t.tutkintotunnus);