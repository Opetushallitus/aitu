
-- Lisätään tutkinnonosaan versio ja surrogaattiavain, poistetaan vanhaan avaimeen viittaavat vierasavaimet
alter table sopimus_ja_tutkinto_ja_tutkinnonosa drop constraint sopimus_ja_tutkinto_ja_tutkinnonosa_tutkinnonosa_fkey;
alter table tutkinto_ja_tutkinnonosa drop constraint tutkinto_ja_tutkinnonosa_tutkinnonosa_fkey;
alter table tutkinnonosa drop constraint tutkinnonosa_pkey;
alter table tutkinnonosa add column versio int;
update tutkinnonosa set versio = 1;
alter table tutkinnonosa alter column versio set not null;
create sequence tutkinnonosa_id_seq;
alter table tutkinnonosa add column tutkinnonosa_id int not null default nextval('tutkinnonosa_id_seq');
alter table tutkinnonosa add constraint tutkinnonosa_pkey primary key (tutkinnonosa_id);

-- Lisätään osaamisalaan versio ja surrogaattiavain, poistetaan vanhaan avaimeen viittaavat vierasavaimet
alter table sopimus_ja_tutkinto_ja_osaamisala drop constraint sopimus_ja_tutkinto_ja_osaamisala_osaamisala_fkey;
alter table osaamisala drop constraint osaamisala_pkey;
alter table osaamisala add column versio int;
update osaamisala set versio = 1;
alter table osaamisala alter column versio set not null;
create sequence osaamisala_id_seq;
alter table osaamisala add column osaamisala_id int not null default nextval('osaamisala_id_seq');
alter table osaamisala add constraint osaamisala_pkey primary key (osaamisala_id);

-- Luodaan taulu tutkinnon versioille
create sequence tutkintoversio_id_seq;
create table tutkintoversio (
  tutkintoversio_id int primary key default nextval('tutkintoversio_id_seq'), 
  tutkintotunnus varchar(6) not null references nayttotutkinto(tutkintotunnus),
  versio int not null,
  koodistoversio int not null,
  peruste varchar(20),
  hyvaksytty boolean default false,
  voimassa_alkupvm date not null,
  voimassa_loppupvm date not null default to_date('21990101', 'YYYYMMDD'),
  siirtymaajan_loppupvm date not null default to_date('21990101', 'YYYYMMDD'),
  muutettu_kayttaja varchar(80) not null references kayttaja(oid),
  luotu_kayttaja varchar(80) not null references kayttaja(oid),
  muutettuaika timestamptz not null,
  luotuaika timestamptz not null
);
insert into tutkintoversio 
  (select nextval('tutkintoversio_id_seq'), tutkintotunnus, 1 as versio, 1 as koodistoversio, peruste, true as hyvaksytty, voimassa_alkupvm, 
          voimassa_loppupvm, siirtymaajan_loppupvm, muutettu_kayttaja, luotu_kayttaja, muutettuaika, luotuaika
    from nayttotutkinto);

create trigger tutkintoversio_update before update on tutkintoversio for each row execute procedure update_stamp() ;
create trigger tutkintoversiol_insert before insert on tutkintoversio for each row execute procedure update_created() ;
create trigger tutkintoversiom_insert before insert on tutkintoversio for each row execute procedure update_stamp() ;
create trigger tutkintoversio_mu_update before update on tutkintoversio for each row execute procedure update_modifier() ;
create trigger tutkintoversio_mu_insert before insert on tutkintoversio for each row execute procedure update_modifier() ;
create trigger tutkintoversio_cu_insert before insert on tutkintoversio for each row execute procedure update_creator() ;

-- Poistetaan tutkinnosta vanhat kentät
alter table nayttotutkinto disable trigger nayttotutkinto_update;
alter table nayttotutkinto disable trigger nayttotutkinto_mu_update;
alter table nayttotutkinto drop column peruste, drop column voimassa_alkupvm, drop column voimassa_loppupvm, drop column siirtymaajan_loppupvm;
alter table nayttotutkinto add column uusin_versio_id int;
update nayttotutkinto t set uusin_versio_id = (select tutkintoversio_id from tutkintoversio tv where tv.tutkintotunnus = t.tutkintotunnus);
alter table nayttotutkinto enable trigger nayttotutkinto_update;
alter table nayttotutkinto enable trigger nayttotutkinto_mu_update;

-- Korjataan sopimus-tutkinto-tutkinnonosa-tauluun viite uuteen tutkinnonosaan
alter table sopimus_ja_tutkinto_ja_tutkinnonosa drop constraint sopimus_ja_tutkinto_ja_tutkinnonosa_pkey;
alter table sopimus_ja_tutkinto_ja_tutkinnonosa add column tutkinnonosa_id int;
update sopimus_ja_tutkinto_ja_tutkinnonosa set tutkinnonosa_id = (select tutkinnonosa_id from tutkinnonosa where osatunnus = tutkinnonosa);
alter table sopimus_ja_tutkinto_ja_tutkinnonosa drop column tutkinnonosa;
alter table sopimus_ja_tutkinto_ja_tutkinnonosa rename column tutkinnonosa_id to tutkinnonosa;
alter table sopimus_ja_tutkinto_ja_tutkinnonosa alter column tutkinnonosa set not null;
alter table sopimus_ja_tutkinto_ja_tutkinnonosa 
  add constraint sopimus_ja_tutkinto_ja_tutkinnonosa_tutkinnonosa_fkey 
  foreign key (tutkinnonosa) references tutkinnonosa (tutkinnonosa_id);
alter table sopimus_ja_tutkinto_ja_tutkinnonosa 
  add constraint sopimus_ja_tutkinto_ja_tutkinnonosa_pkey 
  primary key (sopimus_ja_tutkinto, tutkinnonosa);

-- Korjataan sopimus-tutkinto-tauluun viite uuteen tutkintoversioon
alter table sopimus_ja_tutkinto drop constraint sopimus_ja_tutkinto_tutkintotunnus_fkey;
alter table sopimus_ja_tutkinto add column tutkintoversio int;
update sopimus_ja_tutkinto st set tutkintoversio = (select tutkintoversio_id from tutkintoversio v where v.tutkintotunnus = st.tutkintotunnus);
alter table sopimus_ja_tutkinto drop column tutkintotunnus;
alter table sopimus_ja_tutkinto alter column tutkintoversio set not null;
alter table sopimus_ja_tutkinto add constraint sopimus_ja_tutkinto_tutkintoversio_fkey
  foreign key (tutkintoversio) references tutkintoversio (tutkintoversio_id);

-- Korjataan sopimus-tutkinto-osaamisala-tauluun viite uuteen osaamisalaan
alter table sopimus_ja_tutkinto_ja_osaamisala drop constraint sopimus_ja_tutkinto_ja_osaamisala_pkey;
alter table sopimus_ja_tutkinto_ja_osaamisala add column osaamisala_id int;
update sopimus_ja_tutkinto_ja_osaamisala set osaamisala_id = (select osaamisala_id from osaamisala where osaamisalatunnus = osaamisala);
alter table sopimus_ja_tutkinto_ja_osaamisala drop column osaamisala;
alter table sopimus_ja_tutkinto_ja_osaamisala rename column osaamisala_id to osaamisala;
alter table sopimus_ja_tutkinto_ja_osaamisala alter column osaamisala set not null;
alter table sopimus_ja_tutkinto_ja_osaamisala 
  add constraint sopimus_ja_tutkinto_ja_osaamisala_osaamisala_fkey 
  foreign key (osaamisala) references osaamisala (osaamisala_id);
alter table sopimus_ja_tutkinto_ja_osaamisala 
  add constraint sopimus_ja_tutkinto_ja_osaamisala_pkey 
  primary key (sopimus_ja_tutkinto, osaamisala);

-- Korjataan tutkinto-tutkinnonosatauluun viitteet tutkintoversioon ja tutkinnonosaan
alter table tutkinto_ja_tutkinnonosa drop constraint tutkinto_ja_tutkinnonosa_pkey;
alter table tutkinto_ja_tutkinnonosa drop constraint tutkinto_ja_tutkinnonosa_tutkinto_fkey;
alter table tutkinto_ja_tutkinnonosa add column tutkintoversio int, add column tutkinnonosa_id int;
update tutkinto_ja_tutkinnonosa tt set tutkintoversio = (select tutkintoversio_id from tutkintoversio where tutkintotunnus = tt.tutkinto);
update tutkinto_ja_tutkinnonosa tt set tutkinnonosa_id = (select tutkinnonosa_id from tutkinnonosa where osatunnus = tt.tutkinnonosa);
alter table tutkinto_ja_tutkinnonosa drop column tutkinto;
alter table tutkinto_ja_tutkinnonosa drop column tutkinnonosa;
alter table tutkinto_ja_tutkinnonosa rename column tutkinnonosa_id to tutkinnonosa;
alter table tutkinto_ja_tutkinnonosa alter column tutkintoversio set not null;
alter table tutkinto_ja_tutkinnonosa alter column tutkinnonosa set not null;
alter table tutkinto_ja_tutkinnonosa add constraint tutkinto_ja_tutkinnonosa_pkey
  primary key (tutkintoversio, tutkinnonosa);
alter table tutkinto_ja_tutkinnonosa add constraint tutkinto_ja_tutkinnonosa_tutkinnonosa_fkey
  foreign key (tutkinnonosa) references tutkinnonosa(tutkinnonosa_id);
alter table tutkinto_ja_tutkinnonosa add constraint tutkinto_ja_tutkinnonosa_tutkinto_fkey
  foreign key (tutkintoversio) references tutkintoversio(tutkintoversio_id);

-- Korjataan osaamisalan tutkintoviite
alter table osaamisala add column tutkintoversio int;
update osaamisala o set tutkintoversio = (select tutkintoversio_id from tutkintoversio where tutkintotunnus = o.tutkinto);
alter table osaamisala drop constraint osaamisala_tutkinto_fkey;
alter table osaamisala drop column tutkinto;
alter table osaamisala add constraint osaamisala_tutkintoversio_fkey foreign key (tutkintoversio) references tutkintoversio (tutkintoversio_id);
