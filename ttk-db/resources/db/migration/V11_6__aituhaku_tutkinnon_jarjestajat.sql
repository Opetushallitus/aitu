create or replace view aituhaku.tutkinnon_jarjestajat_view as
  select distinct
    t.tutkintotunnus, ol.nimi, ol.oppilaitoskoodi, ol.www_osoite, kt.nimi_fi ktnimi_fi, kt.nimi_sv ktnimi_sv
  from nayttotutkinto t
  inner join sopimus_ja_tutkinto st on st.tutkintoversio = t.uusin_versio_id
  inner join jarjestamissopimus js on js.jarjestamissopimusid = st.jarjestamissopimusid
  inner join oppilaitos ol on ol.oppilaitoskoodi = js.tutkintotilaisuuksista_vastaava_oppilaitos
  inner join koulutustoimija kt on kt.ytunnus = js.koulutustoimija
  ;
