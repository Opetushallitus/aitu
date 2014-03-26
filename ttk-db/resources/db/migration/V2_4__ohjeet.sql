create table ohje(
  ohjetunniste varchar(80) NOT NULL primary key,
  teksti_fi text,
  teksti_sv text,
  muutettu_kayttaja varchar(80) NOT NULL references kayttaja(oid),
  luotu_kayttaja varchar(80) NOT NULL references kayttaja(oid),
  muutettuaika timestamptz NOT NULL,
  luotuaika timestamptz NOT NULL
);

create trigger ohje_update before update on ohje for each row execute procedure update_stamp();
create trigger ohjel_insert before insert on ohje for each row execute procedure update_created();
create trigger ohjem_insert before insert on ohje for each row execute procedure update_stamp();
create trigger ohje_mu_update before update on ohje for each row execute procedure update_modifier();
create trigger ohje_mu_insert before insert on ohje for each row execute procedure update_modifier();
create trigger ohje_cu_insert before insert on ohje for each row execute procedure update_creator();