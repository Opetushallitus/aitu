create or replace view tutkinnon_jarjestajat_view as
  select distinct
    t.tutkintotunnus, ol.nimi, ol.oppilaitoskoodi
  from nayttotutkinto t
  inner join sopimus_ja_tutkinto st on st.tutkintoversio = t.uusin_versio_id
  inner join jarjestamissopimus js on js.jarjestamissopimusid = st.jarjestamissopimusid
  inner join oppilaitos ol on ol.oppilaitoskoodi = js.oppilaitos;

create or replace view tutkinnon_toimikunnat_view as
  select
    tt.tutkintotunnus, ttk.nimi_fi, ttk.nimi_sv, ttk.tkunta
  from toimikunta_ja_tutkinto tt
  inner join tutkintotoimikunta ttk on ttk.tkunta = tt.toimikunta
  inner join toimikausi toimik on toimik.toimikausi_id = ttk.toimikausi_id
  where toimik.voimassa = true;
