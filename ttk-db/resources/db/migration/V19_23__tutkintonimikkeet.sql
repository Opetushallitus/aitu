create table tutkintonimike (
  nimiketunnus varchar(5) primary key,
  nimi_fi text not null,
  nimi_sv text,
  muutettu_kayttaja varchar(80) NOT NULL references kayttaja(oid),
  luotu_kayttaja varchar(80) NOT NULL references kayttaja(oid),
  muutettuaika timestamptz NOT NULL,
  luotuaika timestamptz NOT NULL
);

create trigger tutkintonimike_update before update on tutkintonimike for each row execute procedure update_stamp() ;
create trigger tutkintonimikel_insert before insert on tutkintonimike for each row execute procedure update_created() ;
create trigger tutkintonimikem_insert before insert on tutkintonimike for each row execute procedure update_stamp() ;
create trigger tutkintonimike_mu_update before update on tutkintonimike for each row execute procedure update_modifier() ;
create trigger tutkintonimike_cu_insert before insert on tutkintonimike for each row execute procedure update_creator() ;
create trigger tutkintonimike_mu_insert before insert on tutkintonimike for each row execute procedure update_modifier() ;

create table tutkintonimike_ja_tutkintoversio (
  tutkintonimike varchar(5) not null,
  tutkintoversio integer not null,
  muutettu_kayttaja varchar(80) NOT NULL references kayttaja(oid),
  luotu_kayttaja varchar(80) NOT NULL references kayttaja(oid),
  muutettuaika timestamptz NOT NULL,
  luotuaika timestamptz NOT NULL,
  primary key (tutkintonimike, tutkintoversio)
);

create trigger tutkintonimike_ja_tutkintoversio_update before update on tutkintonimike_ja_tutkintoversio for each row execute procedure update_stamp() ;
create trigger tutkintonimike_ja_tutkintoversiol_insert before insert on tutkintonimike_ja_tutkintoversio for each row execute procedure update_created() ;
create trigger tutkintonimike_ja_tutkintoversiom_insert before insert on tutkintonimike_ja_tutkintoversio for each row execute procedure update_stamp() ;
create trigger tutkintonimike_ja_tutkintoversio_mu_update before update on tutkintonimike_ja_tutkintoversio for each row execute procedure update_modifier() ;
create trigger tutkintonimike_ja_tutkintoversio_cu_insert before insert on tutkintonimike_ja_tutkintoversio for each row execute procedure update_creator() ;
create trigger tutkintonimike_ja_tutkintoversio_mu_insert before insert on tutkintonimike_ja_tutkintoversio for each row execute procedure update_modifier() ;

create or replace view aituhaku.tutkintonimike_view as
  select t.tutkintotunnus, tn.nimi_fi, tn.nimi_sv
  from tutkintonimike tn
  inner join tutkintonimike_ja_tutkintoversio tnv on tn.nimiketunnus = tnv.tutkintonimike
  inner join tutkintoversio tv on tv.tutkintoversio_id = tnv.tutkintoversio
  inner join nayttotutkinto t on t.uusin_versio_id = tv.tutkintoversio_id;
  