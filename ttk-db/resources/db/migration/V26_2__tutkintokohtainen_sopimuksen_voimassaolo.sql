alter table sopimus_ja_tutkinto
add column alkupvm date,
add column loppupvm date;

update sopimus_ja_tutkinto set alkupvm = js.alkupvm, loppupvm = js.loppupvm
from jarjestamissopimus js where js.jarjestamissopimusid = sopimus_ja_tutkinto.jarjestamissopimusid;

create or replace view aituhaku.tutkinnon_jarjestajat_view as
  select distinct
    t.tutkintotunnus, ol.nimi, ol.oppilaitoskoodi, ol.www_osoite, kt.nimi_fi ktnimi_fi, kt.nimi_sv ktnimi_sv, st.kieli
  from tutkintoversio t
  inner join sopimus_ja_tutkinto st on st.tutkintoversio = t.tutkintoversio_id
  inner join jarjestamissopimus js on js.jarjestamissopimusid = st.jarjestamissopimusid
  inner join oppilaitos ol on ol.oppilaitoskoodi = js.tutkintotilaisuuksista_vastaava_oppilaitos
  inner join koulutustoimija kt on kt.ytunnus = js.koulutustoimija
  where js.voimassa and not js.poistettu and not st.poistettu
  and st.alkupvm <= now() and (st.loppupvm is null or st.loppupvm >= now());