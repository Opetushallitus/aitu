alter table rahoitusmuoto
  add column nimi varchar(24),
  add column kuvaus varchar(200);

comment on column rahoitusmuoto.nimi is 'Aitun enum-lokalisointisysteemin käyttämä avain.';
comment on column rahoitusmuoto.rahoitusmuoto_id is 'Pääavain, tämä tullaan ehkä poistamaan ja korvaamaan nimi-columnilla. Tätä ei olisi alun perin pitänyt tehdä.';
comment on column rahoitusmuoto.rahoitusmuoto is 'Column, jota excel-suoritustiedot käyttää, mutta joka on tarkoitus myöhemmin ehkä poistaa. Tätä ei olisi alun perin pitänyt tehdä.';

update rahoitusmuoto set nimi = 'valtionosuus' where rahoitusmuoto_id = 1;
update rahoitusmuoto set nimi = 'oppisopimus' where rahoitusmuoto_id = 2;
update rahoitusmuoto set nimi = 'tyovoimapoliittinen' where rahoitusmuoto_id = 3;
update rahoitusmuoto set nimi = 'henkilostokoulutus' where rahoitusmuoto_id = 4;
update rahoitusmuoto set nimi = 'eirahoitusmuotoa' where rahoitusmuoto_id = 5;

create unique index rahoitusnimi on rahoitusmuoto (nimi);

insert into rahoitusmuoto(rahoitusmuoto_id, nimi, rahoitusmuoto, kuvaus) 
  values(6,'opsmapa','opsmapa', 'Oppisopimus maksullisena palveluna.');

