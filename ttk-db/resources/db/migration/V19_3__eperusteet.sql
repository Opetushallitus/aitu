create table eperusteet_log (
  id serial NOT NULL primary key,
  paivitetty timestamptz NOT NULL,
  muutettu_kayttaja varchar(80) NOT NULL references kayttaja(oid),
  luotu_kayttaja varchar(80) NOT NULL references kayttaja(oid),
  muutettuaika timestamptz NOT NULL,
  luotuaika timestamptz NOT NULL
);

create trigger eperusteet_log_update before update on eperusteet_log for each row execute procedure update_stamp() ;
create trigger eperusteet_logl_insert before insert on eperusteet_log for each row execute procedure update_created() ;
create trigger eperusteet_logm_insert before insert on eperusteet_log for each row execute procedure update_stamp() ;
create trigger eperusteet_log_mu_update before update on eperusteet_log for each row execute procedure update_modifier() ;
create trigger eperusteet_log_cu_insert before insert on eperusteet_log for each row execute procedure update_creator() ;
create trigger eperusteet_log_mu_insert before insert on eperusteet_log for each row execute procedure update_modifier() ;
