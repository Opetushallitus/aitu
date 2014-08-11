create sequence organisaatiomuutos_id_seq;
create table organisaatiomuutos (
  organisaatiomuutos_id int primary key default nextval('organisaatiomuutos_id_seq'),
  koulutustoimija varchar(10) references koulutustoimija(ytunnus),
  oppilaitos varchar(5) references oppilaitos(oppilaitoskoodi),
  toimipaikka varchar(7) references toimipaikka(toimipaikkakoodi),
  tyyppi varchar(20),
  paivamaara date,
  tehty date,
  luotuaika timestamptz not null,
  muutettuaika timestamptz not null,
  luotu_kayttaja varchar(80) references kayttaja(oid) not null,
  muutettu_kayttaja varchar(80) references kayttaja (oid) not null,
  check (koulutustoimija is not null or oppilaitos is not null or toimipaikka is not null));

create table organisaatiomuutostyyppi (
  nimi varchar(20) primary key,
  kuvaus varchar(200),
  luotuaika timestamptz not null,
  muutettuaika timestamptz not null,
  luotu_kayttaja varchar(80) references kayttaja(oid) not null,
  muutettu_kayttaja varchar(80) references kayttaja (oid) not null);

create trigger organisaatiomuutos_update before update on organisaatiomuutos for each row execute procedure update_stamp();
create trigger organisaatiomuutosl_insert before insert on organisaatiomuutos for each row execute procedure update_created();
create trigger organisaatiomuutosm_insert before insert on organisaatiomuutos for each row execute procedure update_stamp();
create trigger organisaatiomuutos_mu_update before update on organisaatiomuutos for each row execute procedure update_modifier();
create trigger organisaatiomuutos_cu_insert before insert on organisaatiomuutos for each row execute procedure update_creator();
create trigger organisaatiomuutos_mu_insert before insert on organisaatiomuutos for each row execute procedure update_modifier();
create trigger organisaatiomuutostyyppi_update before update on organisaatiomuutostyyppi for each row execute procedure update_stamp();
create trigger organisaatiomuutostyyppil_insert before insert on organisaatiomuutostyyppi for each row execute procedure update_created();
create trigger organisaatiomuutostyyppim_insert before insert on organisaatiomuutostyyppi for each row execute procedure update_stamp();
create trigger organisaatiomuutostyyppi_mu_update before update on organisaatiomuutostyyppi for each row execute procedure update_modifier();
create trigger organisaatiomuutostyyppi_cu_insert before insert on organisaatiomuutostyyppi for each row execute procedure update_creator();
create trigger organisaatiomuutostyyppi_mu_insert before insert on organisaatiomuutostyyppi for each row execute procedure update_modifier();

insert into organisaatiomuutostyyppi (nimi) values ('uusi');