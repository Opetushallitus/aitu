create table tiedote(
  tiedoteid integer primary key default 1,
  teksti_fi text,
  teksti_sv text,
  muutettu_kayttaja varchar(80) NOT NULL references kayttaja(oid),
  luotu_kayttaja varchar(80) NOT NULL references kayttaja(oid),
  muutettuaika timestamptz NOT NULL,
  luotuaika timestamptz NOT NULL,
  CONSTRAINT vain_yksi_tiedote CHECK( tiedoteid = 1 )
);

create trigger tiedote_update before update on tiedote for each row execute procedure update_stamp() ;
create trigger tiedotel_insert before insert on tiedote for each row execute procedure update_created() ;
create trigger tiedotem_insert before insert on tiedote for each row execute procedure update_stamp() ;
create trigger tiedote_mu_update before update on tiedote for each row execute procedure update_modifier() ;
create trigger tiedote_mu_insert before insert on tiedote for each row execute procedure update_modifier() ;
create trigger tiedote_cu_insert before insert on tiedote for each row execute procedure update_creator() ;
