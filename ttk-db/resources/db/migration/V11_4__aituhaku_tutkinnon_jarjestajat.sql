create or replace view aituhaku.tutkinnon_jarjestajat_view as
  select distinct
    t.tutkintotunnus, ol.nimi, ol.oppilaitoskoodi
  from nayttotutkinto t
  inner join sopimus_ja_tutkinto st on st.tutkintoversio = t.uusin_versio_id
  inner join jarjestamissopimus js on js.jarjestamissopimusid = st.jarjestamissopimusid
  inner join oppilaitos ol on ol.oppilaitoskoodi = js.tutkintotilaisuuksista_vastaava_oppilaitos;
