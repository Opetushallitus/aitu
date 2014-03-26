set session aitu.kayttaja='JARJESTELMA';
alter table oppilaitos 
  add column sahkoposti varchar(100),
  add column puhelin varchar(100),
  add column osoite varchar(100),
  add column postinumero varchar(5),
  add column postitoimipaikka varchar(40); 

create table toimipaikka (
  toimipaikkakoodi varchar(7) primary key,
  oppilaitos varchar(6) not null references oppilaitos(oppilaitoskoodi),
  nimi varchar(200) not null,
  kieli varchar(2) references kieli(nimi),
  sahkoposti varchar(100),
  puhelin varchar(100),
  osoite varchar(100),
  postinumero varchar(5),
  postitoimipaikka varchar(40)
);

insert into kieli (nimi, kuvaus) values ('en', 'englanti');