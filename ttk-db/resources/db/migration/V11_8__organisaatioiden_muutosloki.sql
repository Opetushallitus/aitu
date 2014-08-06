create sequence organisaatiomuutos_id_seq;
create table organisaatiomuutos (
  organisaatiomuutos_id int primary key default nextval('organisaatiomuutos_id_seq'),
  koulutustoimija varchar(10) references koulutustoimija(ytunnus),
  oppilaitos varchar(5) references oppilaitos(oppilaitoskoodi),
  toimipaikka varchar(7) references toimipaikka(toimipaikkakoodi),
  tyyppi varchar(20),
  paivamaara date,
  check (koulutustoimija is not null or oppilaitos is not null or toimipaikka is not null));
