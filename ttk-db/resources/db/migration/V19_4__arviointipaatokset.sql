create table rahoitusmuoto (
  rahoitusmuoto_id serial primary key,
  rahoitusmuoto varchar(80) not null,
  muutettu_kayttaja varchar(80) NOT NULL references kayttaja(oid),
  luotu_kayttaja varchar(80) NOT NULL references kayttaja(oid),
  muutettuaika timestamptz NOT NULL,
  luotuaika timestamptz NOT NULL
);

create trigger rahoitusmuoto_update before update on rahoitusmuoto for each row execute procedure update_stamp() ;
create trigger rahoitusmuotol_insert before insert on rahoitusmuoto for each row execute procedure update_created() ;
create trigger rahoitusmuotom_insert before insert on rahoitusmuoto for each row execute procedure update_stamp() ;
create trigger rahoitusmuoto_mu_update before update on rahoitusmuoto for each row execute procedure update_modifier() ;
create trigger rahoitusmuoto_cu_insert before insert on rahoitusmuoto for each row execute procedure update_creator() ;
create trigger rahoitusmuoto_mu_insert before insert on rahoitusmuoto for each row execute procedure update_modifier() ;

insert into rahoitusmuoto(rahoitusmuoto) values ('valtionosuus'), ('oppisopimus'), ('työvoimapoliittinen'), ('henkilöstökoulutus'), ('ei_rahoitusmuotoa');

create table arvosana (
  nimi varchar(20) primary key,
  kuvaus varchar(200),
  muutettu_kayttaja varchar(80) NOT NULL references kayttaja(oid),
  luotu_kayttaja varchar(80) NOT NULL references kayttaja(oid),
  muutettuaika timestamptz NOT NULL,
  luotuaika timestamptz NOT NULL
);

create trigger arvosana_update before update on arvosana for each row execute procedure update_stamp() ;
create trigger arvosanal_insert before insert on arvosana for each row execute procedure update_created() ;
create trigger arvosanam_insert before insert on arvosana for each row execute procedure update_stamp() ;
create trigger arvosana_mu_update before update on arvosana for each row execute procedure update_modifier() ;
create trigger arvosana_cu_insert before insert on arvosana for each row execute procedure update_creator() ;
create trigger arvosana_mu_insert before insert on arvosana for each row execute procedure update_modifier() ;

create table suorituskerta (
  suorituskerta_id serial primary key,
  tutkinto varchar(6) not null references nayttotutkinto(tutkintotunnus),
  rahoitusmuoto int not null references rahoitusmuoto(rahoitusmuoto_id),
  suorittaja int not null references suorittaja(suorittaja_id),
  koulutustoimija varchar(10) not null references koulutustoimija(ytunnus),
  muutettu_kayttaja varchar(80) NOT NULL references kayttaja(oid),
  luotu_kayttaja varchar(80) NOT NULL references kayttaja(oid),
  muutettuaika timestamptz NOT NULL,
  luotuaika timestamptz NOT NULL
);

create trigger suorituskerta_update before update on suorituskerta for each row execute procedure update_stamp() ;
create trigger suorituskertal_insert before insert on suorituskerta for each row execute procedure update_created() ;
create trigger suorituskertam_insert before insert on suorituskerta for each row execute procedure update_stamp() ;
create trigger suorituskerta_mu_update before update on suorituskerta for each row execute procedure update_modifier() ;
create trigger suorituskerta_cu_insert before insert on suorituskerta for each row execute procedure update_creator() ;
create trigger suorituskerta_mu_insert before insert on suorituskerta for each row execute procedure update_modifier() ;

create table kokotutkinto (
  kokotutkinto_id serial primary key,
  tutkinto varchar(6) not null references nayttotutkinto(tutkintotunnus),
  suorittaja int not null references suorittaja(suorittaja_id),
  koulutustoimija varchar(10) not null references koulutustoimija(ytunnus),
  muutettu_kayttaja varchar(80) NOT NULL references kayttaja(oid),
  luotu_kayttaja varchar(80) NOT NULL references kayttaja(oid),
  muutettuaika timestamptz NOT NULL,
  luotuaika timestamptz NOT NULL
);

create trigger kokotutkinto_update before update on kokotutkinto for each row execute procedure update_stamp() ;
create trigger kokotutkintol_insert before insert on kokotutkinto for each row execute procedure update_created() ;
create trigger kokotutkintom_insert before insert on kokotutkinto for each row execute procedure update_stamp() ;
create trigger kokotutkinto_mu_update before update on kokotutkinto for each row execute procedure update_modifier() ;
create trigger kokotutkinto_cu_insert before insert on kokotutkinto for each row execute procedure update_creator() ;
create trigger kokotutkinto_mu_insert before insert on kokotutkinto for each row execute procedure update_modifier() ;

create table suoritus (
  suoritus_id serial primary key,
  kokotutkinto int references kokotutkinto(kokotutkinto_id),
  arvosana varchar(20) references arvosana(nimi),
  suorituskerta int not null references suorituskerta(suorituskerta_id),
  tutkinnonosa int not null references tutkinnonosa(tutkinnonosa_id),
  muutettu_kayttaja varchar(80) NOT NULL references kayttaja(oid),
  luotu_kayttaja varchar(80) NOT NULL references kayttaja(oid),
  muutettuaika timestamptz NOT NULL,
  luotuaika timestamptz NOT NULL
);

create trigger suoritus_update before update on suoritus for each row execute procedure update_stamp() ;
create trigger suoritusl_insert before insert on suoritus for each row execute procedure update_created() ;
create trigger suoritusm_insert before insert on suoritus for each row execute procedure update_stamp() ;
create trigger suoritus_mu_update before update on suoritus for each row execute procedure update_modifier() ;
create trigger suoritus_cu_insert before insert on suoritus for each row execute procedure update_creator() ;
create trigger suoritus_mu_insert before insert on suoritus for each row execute procedure update_modifier() ;
