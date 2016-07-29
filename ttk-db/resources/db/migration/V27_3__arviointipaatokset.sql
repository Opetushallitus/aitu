-- OPH-1806

alter table suorituskerta 
  add column paikka varchar(500),
  add column jarjestelyt varchar(500);
  
  
comment on column suorituskerta.paikka is 'Vapaa kuvaus paikasta, jossa suoritus on arvioitu.';
comment on column suorituskerta.jarjestelyt is 'Vapaa kuvaus järjestelyistä, jotka liittyvät näyttötilaisuuteen.';


create table arvioija (
  nimi varchar(200) not null primary key,
  rooli varchar(100),
  nayttotutkintomestari boolean not null default false,
  muutettu_kayttaja varchar(80) NOT NULL references kayttaja(oid),
  luotu_kayttaja varchar(80) NOT NULL references kayttaja(oid),
  muutettuaika timestamptz NOT NULL,
  luotuaika timestamptz NOT NULL  
);

create trigger arvioija_update before update on arvioija for each row execute procedure update_stamp() ;
create trigger arvioijal_insert before insert on arvioija for each row execute procedure update_created() ;
create trigger arvioijam_insert before insert on arvioija for each row execute procedure update_stamp() ;
create trigger arvioija_mu_update before update on arvioija for each row execute procedure update_modifier() ;
create trigger arvioija_cu_insert before insert on arvioija for each row execute procedure update_creator() ;
create trigger arvioija_mu_insert before insert on arvioija for each row execute procedure update_modifier() ;

create table suorituskerta_arvioija (
  suorituskerta_id int not null references suorituskerta(suorituskerta_id),
  arvioija varchar(200) not null references arvioija(nimi),
  muutettu_kayttaja varchar(80) NOT NULL references kayttaja(oid),
  luotu_kayttaja varchar(80) NOT NULL references kayttaja(oid),
  muutettuaika timestamptz NOT NULL,
  luotuaika timestamptz NOT NULL,
  primary key (suorituskerta_id, arvioija)
);
  
create trigger suorituskerta_arvioija_update before update on suorituskerta_arvioija for each row execute procedure update_stamp() ;
create trigger suorituskerta_arvioijal_insert before insert on suorituskerta_arvioija for each row execute procedure update_created() ;
create trigger suorituskerta_arvioijam_insert before insert on suorituskerta_arvioija for each row execute procedure update_stamp() ;
create trigger suorituskerta_arvioija_mu_update before update on suorituskerta_arvioija for each row execute procedure update_modifier() ;
create trigger suorituskerta_arvioija_cu_insert before insert on suorituskerta_arvioija for each row execute procedure update_creator() ;
create trigger suorituskerta_arvioija_mu_insert before insert on suorituskerta_arvioija for each row execute procedure update_modifier() ;

comment on table arvioija is 'Sisältää listan arvioijista. Vain nimilista, ei tarvetta yksilöidä henkilöitä.';

alter table suoritus
  add column osaamisala int references osaamisala(osaamisala_id);
  
comment on column suoritus.osaamisala is 'Osaamisalaa ei voida aina päätellä tutkinnon osan perusteella.';
 
alter table suorittaja
  add column rahoitusmuoto int not null references rahoitusmuoto(rahoitusmuoto_id);
  




