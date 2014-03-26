
set session aitu.kayttaja='JARJESTELMA';
CREATE SEQUENCE henkilo_id_seq;

CREATE SEQUENCE toimikausi_id_seq;

CREATE SEQUENCE jasenyys_id_seq;

CREATE SEQUENCE jarjestamissuunnitelma_id_seq;

CREATE SEQUENCE sopimus_ja_tutkinto_id_seq;

CREATE SEQUENCE jarjestamissopimus_id_seq;


create table kayttajarooli (
    roolitunnus varchar(16) NOT NULL primary key,
    kuvaus varchar(200),
    muutettuaika timestamp NOT NULL,
    luotuaika timestamp NOT NULL
);


create table kayttaja(
    oid varchar(80) NOT NULL primary key,
    etunimi varchar(100) not null,
    sukunimi varchar(100) not null,
    rooli varchar(16) NOT NULL references kayttajarooli(roolitunnus),
    muutettu_kayttaja varchar(80) NOT NULL references kayttaja(oid),
    luotu_kayttaja varchar(80) NOT NULL references kayttaja(oid),
    muutettuaika timestamptz NOT NULL,
    luotuaika timestamptz NOT NULL,
    voimassa boolean not null default(true)
);


create table kieli(
    nimi varchar(2) NOT NULL primary key,
    kuvaus varchar(200),
    muutettu_kayttaja varchar(80) NOT NULL references kayttaja(oid),
    luotu_kayttaja varchar(80) NOT NULL references kayttaja(oid),
    muutettuaika timestamp NOT NULL,
    luotuaika timestamp NOT NULL
);


create table sukupuoli (
    nimi varchar(20) not null primary key,
    kuvaus varchar(200),
    muutettu_kayttaja varchar(80) NOT NULL references kayttaja(oid),
    luotu_kayttaja varchar(80) NOT NULL references kayttaja(oid),
    muutettuaika timestamptz NOT NULL,
    luotuaika timestamptz NOT NULL
);


create table edustus (
    nimi varchar(20) not null primary key,
    kuvaus varchar(200),
    muutettu_kayttaja varchar(80) NOT NULL references kayttaja(oid),
    luotu_kayttaja varchar(80) NOT NULL references kayttaja(oid),
    muutettuaika timestamptz NOT NULL,
    luotuaika timestamptz NOT NULL
);


create table tutkintotaso(
    nimi varchar(25) not null primary key,
    kuvaus varchar(200),
    muutettu_kayttaja varchar(80) NOT NULL references kayttaja(oid),
    luotu_kayttaja varchar(80) NOT NULL references kayttaja(oid),
    muutettuaika timestamptz NOT NULL,
    luotuaika timestamptz NOT NULL
);


create table keskusjarjesto (
    keskusjarjestoid integer NOT NULL PRIMARY KEY,
    nimi varchar(200) not null,
    osoite varchar(200),
    postinumero varchar(5),
    postitoimipaikka varchar(40),
    puhelin varchar(100),
    sahkoposti varchar(100),
    muutettu_kayttaja varchar(80) NOT NULL references kayttaja(oid),
    luotu_kayttaja varchar(80) NOT NULL references kayttaja(oid),
    muutettuaika timestamptz NOT NULL,
    luotuaika timestamptz NOT NULL
);


create table jarjesto (
    jarjestoid integer NOT NULL primary key,
    keskusjarjesto integer references keskusjarjesto(keskusjarjestoid),
    nimi_fi varchar(200) not null,
    nimi_sv varchar(200),
    osoite varchar(200),
    postinumero varchar(5),
    postitoimipaikka varchar(40),
    puhelin varchar(100),
    sahkoposti varchar(100),
    muutettu_kayttaja varchar(80) NOT NULL references kayttaja(oid),
    luotu_kayttaja varchar(80) NOT NULL references kayttaja(oid),
    muutettuaika timestamptz NOT NULL,
    luotuaika timestamptz NOT NULL
);


create table henkilo(
    henkiloid integer NOT NULL primary key DEFAULT nextval('henkilo_id_seq'),
    jarjesto integer references jarjesto(jarjestoid),
    kayttaja_oid varchar(80) references kayttaja(oid),
    etunimi varchar(100) not null,
    sukunimi varchar(100) not null,
    organisaatio varchar(100),
    aidinkieli varchar(2) references kieli(nimi),
    sukupuoli varchar(20) references sukupuoli(nimi),
    sahkoposti varchar(100),
    puhelin varchar(100),
    osoite varchar(100),
    postinumero varchar(5),
    postitoimipaikka varchar(40),
    lisatiedot text,
    nayttomestari boolean,
    muutettu_kayttaja varchar(80) NOT NULL references kayttaja(oid),
    luotu_kayttaja varchar(80) NOT NULL references kayttaja(oid),
    muutettuaika timestamptz NOT NULL,
    luotuaika timestamptz NOT NULL
);


-- kerrallaan vain yksi toimikausi menossa -> alkupvm uniikki
-- id on silti surrogaattiavain tehokkuus ja mukavuussyistä
create table toimikausi (
    toimikausi_id integer NOT NULL primary key DEFAULT nextval('toimikausi_id_seq'),
    alkupvm date NOT NULL,
    loppupvm date NOT NULL,
    voimassa boolean NOT NULL,
    muutettu_kayttaja varchar(80) NOT NULL references kayttaja(oid),
    luotu_kayttaja varchar(80) NOT NULL references kayttaja(oid),
    muutettuaika timestamptz NOT NULL,
    luotuaika timestamptz NOT NULL,
    CONSTRAINT "ei_paallekkaisia" UNIQUE ("alkupvm")
);


create table koulutusala (
    koulutusala_tkkoodi varchar(3) not null primary key,
    selite_fi text not null,
    selite_sv text,
    selite_en text,
    voimassa_alkupvm date NOT NULL,
    voimassa_loppupvm date NOT NULL DEFAULT to_date('21990101', 'YYYYMMDD'),
    muutettu_kayttaja varchar(80) NOT NULL references kayttaja(oid),
    luotu_kayttaja varchar(80) NOT NULL references kayttaja(oid),
    muutettuaika timestamptz NOT NULL,
    luotuaika timestamptz NOT NULL
);


create table opintoala (
    opintoala_tkkoodi varchar(3) not null primary key,
    koulutusala_tkkoodi varchar(3) not null references koulutusala(koulutusala_tkkoodi),
    selite_fi text not null,
    selite_sv text,
    selite_en text,
    voimassa_alkupvm date NOT NULL,
    voimassa_loppupvm date NOT NULL DEFAULT to_date('21990101', 'YYYYMMDD'),
    muutettu_kayttaja varchar(80) NOT NULL references kayttaja(oid),
    luotu_kayttaja varchar(80) NOT NULL references kayttaja(oid),
    muutettuaika timestamptz NOT NULL,
    luotuaika timestamptz NOT NULL
);


create table osaamisala (
    osaamisalatunnus varchar(5) not null primary key,
    selite_fi text not null,
    selite_sv text,
    selite_en text,
    voimassa_alkupvm date NOT NULL,
    voimassa_loppupvm date NOT NULL DEFAULT to_date('21990101', 'YYYYMMDD'),
    muutettu_kayttaja varchar(80) NOT NULL references kayttaja(oid),
    luotu_kayttaja varchar(80) NOT NULL references kayttaja(oid),
    muutettuaika timestamptz NOT NULL,
    luotuaika timestamptz NOT NULL
);


create table tutkintotyyppi (
    tyyppi varchar(2) not null primary key,
    selite_fi text not null,
    selite_sv text not null,
    muutettu_kayttaja varchar(80) NOT NULL references kayttaja(oid),
    luotu_kayttaja varchar(80) NOT NULL references kayttaja(oid),
    muutettuaika timestamp NOT NULL,
    luotuaika timestamp NOT NULL
);


create table peruste (
    diaarinumero varchar(20) not null primary key,
    alkupvm date not null,
    siirtymaajan_loppupvm date NOT NULL DEFAULT to_date('21990101', 'YYYYMMDD'),
    muutettu_kayttaja varchar(80) NOT NULL references kayttaja(oid),
    luotu_kayttaja varchar(80) NOT NULL references kayttaja(oid),
    muutettuaika timestamp NOT NULL,
    luotuaika timestamp NOT NULL
);


create table nayttotutkinto (
    tutkintotunnus varchar(6) NOT NULL primary key,
    opintoala varchar(3) not null references opintoala(opintoala_tkkoodi),
-- HUOM: Esimerkiksi Merenkulkualan perustutkinnolla on kolme eri osaamisalaa?
--    osaamisala varchar(5) not null references osaamisala(osaamisalatunnus),
    nimi_fi text not null,
    nimi_sv text,
    voimassa_alkupvm date NOT NULL,
    voimassa_loppupvm date NOT NULL DEFAULT to_date('21990101', 'YYYYMMDD'),
    siirtymaajan_loppupvm date NOT NULL DEFAULT to_date('21990101', 'YYYYMMDD'),
    tyyppi varchar(2) references tutkintotyyppi(tyyppi),
    peruste varchar(20) references peruste(diaarinumero),
    tutkintotaso varchar(25) references tutkintotaso(nimi),
    muutettu_kayttaja varchar(80) NOT NULL references kayttaja(oid),
    luotu_kayttaja varchar(80) NOT NULL references kayttaja(oid),
    muutettuaika timestamptz NOT NULL,
    luotuaika timestamptz NOT NULL
);


create table tutkinnonosa (
   osatunnus varchar(6) not null primary key,
   tutkinto varchar(6) not null references nayttotutkinto(tutkintotunnus),
   osaamisala varchar(5) not null references osaamisala(osaamisalatunnus),
   nimi text not null,
   muutettu_kayttaja varchar(80) NOT NULL references kayttaja(oid),
   luotu_kayttaja varchar(80) NOT NULL references kayttaja(oid),
   muutettuaika timestamptz NOT NULL,
   luotuaika timestamptz NOT NULL
);


create table tutkintotoimikunta(
    tkunta varchar(9) not null primary key,
    diaarinumero varchar(20),
    nimi_fi text not null,
    nimi_sv text,
    tilikoodi varchar(4),
    toimiala text,
    sahkoposti varchar(100),
    kielisyys varchar(2) not null references kieli(nimi),
    toimikausi_id integer NOT NULL references toimikausi(toimikausi_id),
    toimikausi_alku date NOT NULL,
    toimikausi_loppu date NOT NULL,
    muutettu_kayttaja varchar(80) NOT NULL references kayttaja(oid),
    luotu_kayttaja varchar(80) NOT NULL references kayttaja(oid),
    muutettuaika timestamptz NOT NULL,
    luotuaika timestamptz NOT NULL
);


CREATE UNIQUE INDEX toimikunta_tilikoodi
    ON tutkintotoimikunta (tilikoodi) ;


CREATE UNIQUE  INDEX toimikunta_diaarinumero
    ON tutkintotoimikunta (diaarinumero);


create table toimikunta_ja_tutkinto(
    toimikunta varchar(9) not null references tutkintotoimikunta(tkunta),
    tutkintotunnus varchar(6) NOT NULL references nayttotutkinto(tutkintotunnus),
    muutettu_kayttaja varchar(80) NOT NULL references kayttaja(oid),
    luotu_kayttaja varchar(80) NOT NULL references kayttaja(oid),
    muutettuaika timestamptz NOT NULL,
    luotuaika timestamptz NOT NULL,
    primary key (toimikunta, tutkintotunnus)
);


create table rooli(
    nimi varchar(20) not null primary key,
    kuvaus varchar(200),
    muutettu_kayttaja varchar(80) NOT NULL references kayttaja(oid),
    luotu_kayttaja varchar(80) NOT NULL references kayttaja(oid),
    muutettuaika timestamptz NOT NULL,
    luotuaika timestamptz NOT NULL
);


create table jasenyys(
    jasenyys_id integer NOT NULL primary key DEFAULT nextval('jasenyys_id_seq'),
    henkiloid integer NOT NULL references henkilo(henkiloid),
    toimikunta varchar(9) NOT NULL references tutkintotoimikunta(tkunta),
    alkupvm date NOT NULL,
    loppupvm date NOT NULL,
    rooli varchar(20) references rooli(nimi),
    edustus varchar(20) references edustus(nimi),
    muutettu_kayttaja varchar(80) NOT NULL references kayttaja(oid),
    luotu_kayttaja varchar(80) NOT NULL references kayttaja(oid),
    muutettuaika timestamptz NOT NULL,
    luotuaika timestamptz NOT NULL,
    CONSTRAINT ei_paallekkaisia_alkupvm UNIQUE (henkiloid, toimikunta, alkupvm),
    CONSTRAINT ei_paallekkaisia_loppupvm UNIQUE (henkiloid, toimikunta, loppupvm),
    CONSTRAINT alkupvm_sama_tai_ennen_loppupvm CHECK (alkupvm <= loppupvm)
 );


create table alue(
    nimi varchar(20) not null primary key,
    kuvaus varchar(200),
    muutettu_kayttaja varchar(80) NOT NULL references kayttaja(oid),
    luotu_kayttaja varchar(80) NOT NULL references kayttaja(oid),
    muutettuaika timestamptz NOT NULL,
    luotuaika timestamptz NOT NULL
);


create table oppilaitos (
    oppilaitoskoodi varchar(5) not null primary key,
    nimi varchar(200) not null,
    alue varchar(20) references alue(nimi),
    kieli varchar(2) references kieli(nimi),
    muutettu_kayttaja varchar(80) NOT NULL references kayttaja(oid),
    luotu_kayttaja varchar(80) NOT NULL references kayttaja(oid),
    muutettuaika timestamptz NOT NULL,
    luotuaika timestamptz NOT NULL
);


create table jarjestamissopimus (
    jarjestamissopimusid integer NOT NULL primary key DEFAULT nextval('jarjestamissopimus_id_seq'),
    sopimusnumero varchar(25) NOT NULL,
    oppilaitos varchar(5) not null references oppilaitos(oppilaitoskoodi),
    toimikunta varchar(9) not null references tutkintotoimikunta(tkunta),
    sopijatoimikunta varchar(9) not null references tutkintotoimikunta(tkunta),
    tutkintotilaisuuksista_vastaava_oppilaitos varchar(5) references oppilaitos(oppilaitoskoodi),
    vastuuhenkilo varchar(100),
    puhelin varchar(100),
    sahkoposti varchar(100),
    alkupvm date,
    loppupvm date,
    muutettu_kayttaja varchar(80) NOT NULL references kayttaja(oid),
    luotu_kayttaja varchar(80) NOT NULL references kayttaja(oid),
    muutettuaika timestamptz NOT NULL,
    luotuaika timestamptz NOT NULL
);


CREATE UNIQUE INDEX jarjestamissopimus_sopimusnumero
    ON jarjestamissopimus (sopimusnumero) ;


create table sopimus_ja_tutkinto (
    sopimus_ja_tutkinto_id integer NOT NULL primary key DEFAULT nextval('sopimus_ja_tutkinto_id_seq'),
    jarjestamissopimusid integer not null references jarjestamissopimus(jarjestamissopimusid),
    tutkintotunnus varchar(6) not null references nayttotutkinto(tutkintotunnus),
    vastuuhenkilo varchar(100),
    puhelin varchar(100),
    sahkoposti varchar(100),
    poistettu boolean not null default(false),
    kieli varchar(2) references kieli(nimi),
    muutettu_kayttaja varchar(80) NOT NULL references kayttaja(oid),
    luotu_kayttaja varchar(80) NOT NULL references kayttaja(oid),
    muutettuaika timestamptz NOT NULL,
    luotuaika timestamptz NOT NULL
);


create table jarjestamissuunnitelma (
  jarjestamissuunnitelma_id integer NOT NULL primary key DEFAULT nextval('jarjestamissuunnitelma_id_seq'),
  sopimus_ja_tutkinto integer not null references sopimus_ja_tutkinto(sopimus_ja_tutkinto_id),
  jarjestamissuunnitelma_filename varchar(200) not null ,
  jarjestamissuunnitelma_content_type varchar(100) not null ,
  poistettu boolean not null default(false),
  jarjestamissuunnitelma bytea,
  muutettu_kayttaja varchar(80) NOT NULL references kayttaja(oid),
  luotu_kayttaja varchar(80) NOT NULL references kayttaja(oid),
  muutettuaika timestamptz NOT NULL,
  luotuaika timestamptz NOT NULL
);


insert into kayttajarooli(roolitunnus, kuvaus, muutettuaika, luotuaika)
  values ('YLLAPITAJA', 'Ylläpitäjäroolilla on kaikki oikeudet', current_timestamp, current_timestamp);


insert into kayttajarooli(roolitunnus, kuvaus, muutettuaika, luotuaika)
  values ('KAYTTAJA', 'Käyttäjäroolin oikeudet riippuvat kontekstisensitiivisistä roolioikeuksista.', current_timestamp, current_timestamp);


insert into kayttaja(oid, etunimi, sukunimi, voimassa, rooli, muutettuaika, luotuaika, luotu_kayttaja, muutettu_kayttaja)
values ('JARJESTELMA', 'Järjestelmä', '', true, 'YLLAPITAJA', current_timestamp, current_timestamp, 'JARJESTELMA', 'JARJESTELMA');


insert into kayttaja(oid, etunimi, sukunimi, voimassa, rooli, muutettuaika, luotuaika, luotu_kayttaja, muutettu_kayttaja)
values ('KONVERSIO', 'AMTU', 'Oracle', false, 'YLLAPITAJA', current_timestamp, current_timestamp, 'JARJESTELMA', 'JARJESTELMA');


insert into kayttaja(oid, etunimi, sukunimi, voimassa, rooli, muutettuaika, luotuaika, luotu_kayttaja, muutettu_kayttaja)
values ('INTEGRAATIO', 'Integraatio', '', true, 'YLLAPITAJA', current_timestamp, current_timestamp, 'JARJESTELMA', 'JARJESTELMA');

set aitu.kayttaja to default;
CREATE OR REPLACE function update_stamp() returns trigger as $$ begin new.muutettuaika := now(); return new; end; $$ language plpgsql;
CREATE OR REPLACE function update_created() returns trigger as $$ begin new.luotuaika := now(); return new; end; $$ language plpgsql;
create trigger jarjestamissuunnitelma_update before update on jarjestamissuunnitelma for each row execute procedure update_stamp() ;
create trigger jarjestamissuunnitelmal_insert before insert on jarjestamissuunnitelma for each row execute procedure update_created() ;
create trigger jarjestamissuunnitelmam_insert before insert on jarjestamissuunnitelma for each row execute procedure update_stamp() ;
create trigger sopimus_ja_tutkinto_update before update on sopimus_ja_tutkinto for each row execute procedure update_stamp() ;
create trigger sopimus_ja_tutkintol_insert before insert on sopimus_ja_tutkinto for each row execute procedure update_created() ;
create trigger sopimus_ja_tutkintom_insert before insert on sopimus_ja_tutkinto for each row execute procedure update_stamp() ;
create trigger tutkintotaso_update before update on tutkintotaso for each row execute procedure update_stamp() ;
create trigger tutkintotasol_insert before insert on tutkintotaso for each row execute procedure update_created() ;
create trigger tutkintotasom_insert before insert on tutkintotaso for each row execute procedure update_stamp() ;
create trigger jarjestamissopimus_update before update on jarjestamissopimus for each row execute procedure update_stamp() ;
create trigger jarjestamissopimusl_insert before insert on jarjestamissopimus for each row execute procedure update_created() ;
create trigger jarjestamissopimusm_insert before insert on jarjestamissopimus for each row execute procedure update_stamp() ;
create trigger oppilaitos_update before update on oppilaitos for each row execute procedure update_stamp() ;
create trigger oppilaitosl_insert before insert on oppilaitos for each row execute procedure update_created() ;
create trigger oppilaitosm_insert before insert on oppilaitos for each row execute procedure update_stamp() ;
create trigger alue_update before update on alue for each row execute procedure update_stamp() ;
create trigger aluel_insert before insert on alue for each row execute procedure update_created() ;
create trigger aluem_insert before insert on alue for each row execute procedure update_stamp() ;
create trigger jasenyys_update before update on jasenyys for each row execute procedure update_stamp() ;
create trigger jasenyysl_insert before insert on jasenyys for each row execute procedure update_created() ;
create trigger jasenyysm_insert before insert on jasenyys for each row execute procedure update_stamp() ;
create trigger toimikunta_ja_tutkinto_update before update on toimikunta_ja_tutkinto for each row execute procedure update_stamp() ;
create trigger toimikunta_ja_tutkintol_insert before insert on toimikunta_ja_tutkinto for each row execute procedure update_created() ;
create trigger toimikunta_ja_tutkintom_insert before insert on toimikunta_ja_tutkinto for each row execute procedure update_stamp() ;
create trigger rooli_update before update on rooli for each row execute procedure update_stamp() ;
create trigger roolil_insert before insert on rooli for each row execute procedure update_created() ;
create trigger roolim_insert before insert on rooli for each row execute procedure update_stamp() ;
create trigger tutkinnonosa_update before update on tutkinnonosa for each row execute procedure update_stamp() ;
create trigger tutkinnonosal_insert before insert on tutkinnonosa for each row execute procedure update_created() ;
create trigger tutkinnonosam_insert before insert on tutkinnonosa for each row execute procedure update_stamp() ;
create trigger nayttotutkinto_update before update on nayttotutkinto for each row execute procedure update_stamp() ;
create trigger nayttotutkintol_insert before insert on nayttotutkinto for each row execute procedure update_created() ;
create trigger nayttotutkintom_insert before insert on nayttotutkinto for each row execute procedure update_stamp() ;
create trigger osaamisala_update before update on osaamisala for each row execute procedure update_stamp() ;
create trigger osaamisalal_insert before insert on osaamisala for each row execute procedure update_created() ;
create trigger osaamisalam_insert before insert on osaamisala for each row execute procedure update_stamp() ;
create trigger opintoala_update before update on opintoala for each row execute procedure update_stamp() ;
create trigger opintoalal_insert before insert on opintoala for each row execute procedure update_created() ;
create trigger opintoalam_insert before insert on opintoala for each row execute procedure update_stamp() ;
create trigger koulutusala_update before update on koulutusala for each row execute procedure update_stamp() ;
create trigger koulutusalal_insert before insert on koulutusala for each row execute procedure update_created() ;
create trigger koulutusalam_insert before insert on koulutusala for each row execute procedure update_stamp() ;
create trigger keskusjarjesto_update before update on keskusjarjesto for each row execute procedure update_stamp() ;
create trigger keskusjarjestol_insert before insert on keskusjarjesto for each row execute procedure update_created() ;
create trigger keskusjarjestom_insert before insert on keskusjarjesto for each row execute procedure update_stamp() ;
create trigger jarjesto_update before update on jarjesto for each row execute procedure update_stamp() ;
create trigger jarjestol_insert before insert on jarjesto for each row execute procedure update_created() ;
create trigger jarjestom_insert before insert on jarjesto for each row execute procedure update_stamp() ;
create trigger henkilo_update before update on henkilo for each row execute procedure update_stamp() ;
create trigger henkilol_insert before insert on henkilo for each row execute procedure update_created() ;
create trigger henkilom_insert before insert on henkilo for each row execute procedure update_stamp() ;
create trigger edustus_update before update on edustus for each row execute procedure update_stamp() ;
create trigger edustusl_insert before insert on edustus for each row execute procedure update_created() ;
create trigger edustusm_insert before insert on edustus for each row execute procedure update_stamp() ;
create trigger tutkintotoimikunta_update before update on tutkintotoimikunta for each row execute procedure update_stamp() ;
create trigger tutkintotoimikuntal_insert before insert on tutkintotoimikunta for each row execute procedure update_created() ;
create trigger tutkintotoimikuntam_insert before insert on tutkintotoimikunta for each row execute procedure update_stamp() ;
create trigger toimikausi_update before update on toimikausi for each row execute procedure update_stamp() ;
create trigger toimikausil_insert before insert on toimikausi for each row execute procedure update_created() ;
create trigger toimikausim_insert before insert on toimikausi for each row execute procedure update_stamp() ;
create trigger kieli_update before update on kieli for each row execute procedure update_stamp() ;
create trigger kielil_insert before insert on kieli for each row execute procedure update_created() ;
create trigger kielim_insert before insert on kieli for each row execute procedure update_stamp() ;
create trigger peruste_update before update on peruste for each row execute procedure update_stamp() ;
create trigger perustel_insert before insert on peruste for each row execute procedure update_created() ;
create trigger perustem_insert before insert on peruste for each row execute procedure update_stamp() ;
create trigger tutkintotyyppi_update before update on tutkintotyyppi for each row execute procedure update_stamp() ;
create trigger tutkintotyyppil_insert before insert on tutkintotyyppi for each row execute procedure update_created() ;
create trigger tutkintotyyppim_insert before insert on tutkintotyyppi for each row execute procedure update_stamp() ;
create trigger sukupuoli_update before update on sukupuoli for each row execute procedure update_stamp() ;
create trigger sukupuolil_insert before insert on sukupuoli for each row execute procedure update_created() ;
create trigger sukupuolim_insert before insert on sukupuoli for each row execute procedure update_stamp() ;
create trigger kayttaja_update before update on kayttaja for each row execute procedure update_stamp() ;
create trigger kayttajal_insert before insert on kayttaja for each row execute procedure update_created() ;
create trigger kayttajam_insert before insert on kayttaja for each row execute procedure update_stamp() ;
create trigger kayttajarooli_update before update on kayttajarooli for each row execute procedure update_stamp() ;
create trigger kayttajaroolil_insert before insert on kayttajarooli for each row execute procedure update_created() ;
create trigger kayttajaroolim_insert before insert on kayttajarooli for each row execute procedure update_stamp() ;
CREATE OR REPLACE function update_creator() returns trigger as $$ begin new.luotu_kayttaja := current_setting('aitu.kayttaja'); return new; end; $$ language plpgsql;
CREATE OR REPLACE function update_modifier() returns trigger as $$ begin new.muutettu_kayttaja := current_setting('aitu.kayttaja'); return new; end; $$ language plpgsql;
create trigger jarjestamissuunnitelma_mu_update before update on jarjestamissuunnitelma for each row execute procedure update_modifier() ;
create trigger jarjestamissuunnitelma_cu_insert before insert on jarjestamissuunnitelma for each row execute procedure update_creator() ;
create trigger jarjestamissuunnitelma_mu_insert before insert on jarjestamissuunnitelma for each row execute procedure update_modifier() ;
create trigger sopimus_ja_tutkinto_mu_update before update on sopimus_ja_tutkinto for each row execute procedure update_modifier() ;
create trigger sopimus_ja_tutkinto_cu_insert before insert on sopimus_ja_tutkinto for each row execute procedure update_creator() ;
create trigger sopimus_ja_tutkinto_mu_insert before insert on sopimus_ja_tutkinto for each row execute procedure update_modifier() ;
create trigger tutkintotaso_mu_update before update on tutkintotaso for each row execute procedure update_modifier() ;
create trigger tutkintotaso_cu_insert before insert on tutkintotaso for each row execute procedure update_creator() ;
create trigger tutkintotaso_mu_insert before insert on tutkintotaso for each row execute procedure update_modifier() ;
create trigger jarjestamissopimus_mu_update before update on jarjestamissopimus for each row execute procedure update_modifier() ;
create trigger jarjestamissopimus_cu_insert before insert on jarjestamissopimus for each row execute procedure update_creator() ;
create trigger jarjestamissopimus_mu_insert before insert on jarjestamissopimus for each row execute procedure update_modifier() ;
create trigger oppilaitos_mu_update before update on oppilaitos for each row execute procedure update_modifier() ;
create trigger oppilaitos_cu_insert before insert on oppilaitos for each row execute procedure update_creator() ;
create trigger oppilaitos_mu_insert before insert on oppilaitos for each row execute procedure update_modifier() ;
create trigger alue_mu_update before update on alue for each row execute procedure update_modifier() ;
create trigger alue_cu_insert before insert on alue for each row execute procedure update_creator() ;
create trigger alue_mu_insert before insert on alue for each row execute procedure update_modifier() ;
create trigger jasenyys_mu_update before update on jasenyys for each row execute procedure update_modifier() ;
create trigger jasenyys_cu_insert before insert on jasenyys for each row execute procedure update_creator() ;
create trigger jasenyys_mu_insert before insert on jasenyys for each row execute procedure update_modifier() ;
create trigger toimikunta_ja_tutkinto_mu_update before update on toimikunta_ja_tutkinto for each row execute procedure update_modifier() ;
create trigger toimikunta_ja_tutkinto_cu_insert before insert on toimikunta_ja_tutkinto for each row execute procedure update_creator() ;
create trigger toimikunta_ja_tutkinto_mu_insert before insert on toimikunta_ja_tutkinto for each row execute procedure update_modifier() ;
create trigger rooli_mu_update before update on rooli for each row execute procedure update_modifier() ;
create trigger rooli_cu_insert before insert on rooli for each row execute procedure update_creator() ;
create trigger rooli_mu_insert before insert on rooli for each row execute procedure update_modifier() ;
create trigger tutkinnonosa_mu_update before update on tutkinnonosa for each row execute procedure update_modifier() ;
create trigger tutkinnonosa_cu_insert before insert on tutkinnonosa for each row execute procedure update_creator() ;
create trigger tutkinnonosa_mu_insert before insert on tutkinnonosa for each row execute procedure update_modifier() ;
create trigger nayttotutkinto_mu_update before update on nayttotutkinto for each row execute procedure update_modifier() ;
create trigger nayttotutkinto_cu_insert before insert on nayttotutkinto for each row execute procedure update_creator() ;
create trigger nayttotutkinto_mu_insert before insert on nayttotutkinto for each row execute procedure update_modifier() ;
create trigger osaamisala_mu_update before update on osaamisala for each row execute procedure update_modifier() ;
create trigger osaamisala_cu_insert before insert on osaamisala for each row execute procedure update_creator() ;
create trigger osaamisala_mu_insert before insert on osaamisala for each row execute procedure update_modifier() ;
create trigger opintoala_mu_update before update on opintoala for each row execute procedure update_modifier() ;
create trigger opintoala_cu_insert before insert on opintoala for each row execute procedure update_creator() ;
create trigger opintoala_mu_insert before insert on opintoala for each row execute procedure update_modifier() ;
create trigger koulutusala_mu_update before update on koulutusala for each row execute procedure update_modifier() ;
create trigger koulutusala_cu_insert before insert on koulutusala for each row execute procedure update_creator() ;
create trigger koulutusala_mu_insert before insert on koulutusala for each row execute procedure update_modifier() ;
create trigger keskusjarjesto_mu_update before update on keskusjarjesto for each row execute procedure update_modifier() ;
create trigger keskusjarjesto_cu_insert before insert on keskusjarjesto for each row execute procedure update_creator() ;
create trigger keskusjarjesto_mu_insert before insert on keskusjarjesto for each row execute procedure update_modifier() ;
create trigger jarjesto_mu_update before update on jarjesto for each row execute procedure update_modifier() ;
create trigger jarjesto_cu_insert before insert on jarjesto for each row execute procedure update_creator() ;
create trigger jarjesto_mu_insert before insert on jarjesto for each row execute procedure update_modifier() ;
create trigger henkilo_mu_update before update on henkilo for each row execute procedure update_modifier() ;
create trigger henkilo_cu_insert before insert on henkilo for each row execute procedure update_creator() ;
create trigger henkilo_mu_insert before insert on henkilo for each row execute procedure update_modifier() ;
create trigger edustus_mu_update before update on edustus for each row execute procedure update_modifier() ;
create trigger edustus_cu_insert before insert on edustus for each row execute procedure update_creator() ;
create trigger edustus_mu_insert before insert on edustus for each row execute procedure update_modifier() ;
create trigger tutkintotoimikunta_mu_update before update on tutkintotoimikunta for each row execute procedure update_modifier() ;
create trigger tutkintotoimikunta_cu_insert before insert on tutkintotoimikunta for each row execute procedure update_creator() ;
create trigger tutkintotoimikunta_mu_insert before insert on tutkintotoimikunta for each row execute procedure update_modifier() ;
create trigger toimikausi_mu_update before update on toimikausi for each row execute procedure update_modifier() ;
create trigger toimikausi_cu_insert before insert on toimikausi for each row execute procedure update_creator() ;
create trigger toimikausi_mu_insert before insert on toimikausi for each row execute procedure update_modifier() ;
create trigger kieli_mu_update before update on kieli for each row execute procedure update_modifier() ;
create trigger kieli_cu_insert before insert on kieli for each row execute procedure update_creator() ;
create trigger kieli_mu_insert before insert on kieli for each row execute procedure update_modifier() ;
create trigger peruste_mu_update before update on peruste for each row execute procedure update_modifier() ;
create trigger peruste_cu_insert before insert on peruste for each row execute procedure update_creator() ;
create trigger peruste_mu_insert before insert on peruste for each row execute procedure update_modifier() ;
create trigger tutkintotyyppi_mu_update before update on tutkintotyyppi for each row execute procedure update_modifier() ;
create trigger tutkintotyyppi_cu_insert before insert on tutkintotyyppi for each row execute procedure update_creator() ;
create trigger tutkintotyyppi_mu_insert before insert on tutkintotyyppi for each row execute procedure update_modifier() ;
create trigger sukupuoli_mu_update before update on sukupuoli for each row execute procedure update_modifier() ;
create trigger sukupuoli_cu_insert before insert on sukupuoli for each row execute procedure update_creator() ;
create trigger sukupuoli_mu_insert before insert on sukupuoli for each row execute procedure update_modifier() ;
create trigger kayttaja_mu_update before update on kayttaja for each row execute procedure update_modifier() ;
create trigger kayttaja_cu_insert before insert on kayttaja for each row execute procedure update_creator() ;
create trigger kayttaja_mu_insert before insert on kayttaja for each row execute procedure update_modifier() ;
set session aitu.kayttaja='JARJESTELMA';

insert into toimikausi (alkupvm, loppupvm, voimassa)
values (to_date('01082010','DDMMYYYY'), to_date('31072013', 'DDMMYYYY'), false);


insert into toimikausi (alkupvm, loppupvm, voimassa)
values (to_date('01082013','DDMMYYYY'), to_date('31072016', 'DDMMYYYY'), true);


insert into rooli (nimi, kuvaus)
values
    ('asiantuntija', 'pysyvä asiantuntija'),
    ('jasen', 'jäsen'),
    ('puheenjohtaja', 'puheenjohtaja'),
    ('sihteeri', 'sihteeri'),
    ('ulkopuolinensihteeri', 'ulkopuolinen sihteeri'),
    ('varapuheenjohtaja', 'varapuheenjohtaja');


insert into edustus (nimi, kuvaus)
values
    ('muu', 'muu'),
    ('opettaja', 'opettaja'),
    ('tyonantaja', 'työnantaja'),
    ('tyontekija', 'työntekijä'),
    ('itsenainen', 'ammatin harjoittaja'),
    ('asiantuntija', 'asiantuntija');


insert into alue (nimi, kuvaus)
values
    ('etelasuomi', 'Etelä-Suomi'),
    ('lounaissuomi', 'Lounais-Suomi'),
    ('itasuomi', 'Itä-Suomi'),
    ('lansisuomi', 'Länsi-Suomi'),
    ('pohjoissuomi', 'Pohjois-Suomi'),
    ('lappi', 'Lappi'),
    ('ahvenanmaa', 'Ahvenanmaa');


insert into kieli(nimi, kuvaus) values
    ('fi', 'suomi'),
    ('sv', 'ruotsi'),
    ('se', 'saame'),
    ('2k', 'kaksikielinen');


insert into sukupuoli(nimi, kuvaus) values
    ('mies', 'mies'),
    ('nainen', 'nainen');


insert into tutkintotaso(nimi, kuvaus) values
    ('erikoisammattitutkinto', 'erikoisammattitutkinto'),
    ('ammattitutkinto', 'ammattitutkinto'),
    ('perustutkinto', 'perustutkinto');

;
set aitu.kayttaja to default;