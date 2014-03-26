-- Kantaversiossa 2.2 käyttämättömäksi jääneet kentät pois
alter table jarjesto
  drop column keskusjarjesto;

-- Kantaversiossa 2.2 keskusjarjesto taulun tiedot siirrettin jarjesto tauluun
drop table keskusjarjesto;

-- Sekvenssi järjestötaululle
CREATE SEQUENCE jarjesto_id_seq;
select setval('jarjesto_id_seq', (select max(jarjestoid) + 1 from jarjesto), false);
alter table jarjesto
  alter column jarjestoid set default nextval('jarjesto_id_seq');