CREATE SEQUENCE sopimuksen_liite_id_seq;

create table sopimuksen_liite (
  sopimuksen_liite_id integer NOT NULL primary key DEFAULT nextval('sopimuksen_liite_id_seq'),
  sopimus_ja_tutkinto integer not null references sopimus_ja_tutkinto(sopimus_ja_tutkinto_id),
  sopimuksen_liite_filename varchar(200) not null ,
  sopimuksen_liite_content_type varchar(100) not null ,
  poistettu boolean not null default(false),
  sopimuksen_liite bytea,
  muutettu_kayttaja varchar(80) NOT NULL references kayttaja(oid),
  luotu_kayttaja varchar(80) NOT NULL references kayttaja(oid),
  muutettuaika timestamptz NOT NULL,
  luotuaika timestamptz NOT NULL
);

create trigger sopimuksen_liite_update before update on sopimuksen_liite for each row execute procedure update_stamp() ;
create trigger sopimuksen_liitel_insert before insert on sopimuksen_liite for each row execute procedure update_created() ;
create trigger sopimuksen_liitem_insert before insert on sopimuksen_liite for each row execute procedure update_stamp() ;
create trigger sopimuksen_liite_mu_update before update on sopimuksen_liite for each row execute procedure update_modifier() ;
create trigger sopimuksen_liite_mu_insert before insert on sopimuksen_liite for each row execute procedure update_modifier() ;
create trigger sopimuksen_liite_cu_insert before insert on sopimuksen_liite for each row execute procedure update_creator() ;
