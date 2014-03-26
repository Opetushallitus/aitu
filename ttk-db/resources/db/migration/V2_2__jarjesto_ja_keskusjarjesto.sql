set session aitu.kayttaja='JARJESTELMA';

alter table jarjesto
  add column keskusjarjestotieto boolean not null default(false),
  add column keskusjarjestoid integer references jarjesto(jarjestoid),
  add constraint keskusjarjestosta_ei_viitetta check (keskusjarjestotieto = false OR keskusjarjestoid is null);

insert into jarjesto
  (jarjestoid, nimi_fi, osoite, postinumero, postitoimipaikka, puhelin, sahkoposti, keskusjarjestotieto)
select
  keskusjarjestoid * 1000, nimi, osoite, postinumero, postitoimipaikka, puhelin, sahkoposti, true
from keskusjarjesto;

update jarjesto
  set keskusjarjestoid = keskusjarjesto * 1000
  where keskusjarjesto is not null;
