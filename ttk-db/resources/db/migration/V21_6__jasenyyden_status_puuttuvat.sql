-- lisätään puuttuvat triggerit ja vakiosarakkeet
alter table jasenyyden_status add column muutettu_kayttaja varchar(80) NOT NULL default 'JARJESTELMA' references kayttaja(oid);
alter table jasenyyden_status add column luotu_kayttaja varchar(80) NOT NULL default 'JARJESTELMA' references kayttaja(oid);
alter table jasenyyden_status add column muutettuaika timestamptz NOT NULL default current_timestamp;
alter table jasenyyden_status add column luotuaika timestamptz NOT NULL default current_timestamp;
create trigger jasenyyden_status_update before update on jasenyyden_status for each row execute procedure update_stamp() ;
create trigger jasenyyden_statusl_insert before insert on jasenyyden_status for each row execute procedure update_created() ;
create trigger jasenyyden_statusm_insert before insert on jasenyyden_status for each row execute procedure update_stamp() ;
create trigger jasenyyden_status_mu_update before update on jasenyyden_status for each row execute procedure update_modifier() ;
create trigger jasenyyden_status_mu_insert before insert on jasenyyden_status for each row execute procedure update_modifier() ;
create trigger jasenyyden_status_cu_insert before insert on jasenyyden_status for each row execute procedure update_creator() ;

-- V21_5 jälkeen nullit on poistettu, joten on hyvä että niitä ei enää voi jatkossa tulla
alter table jasenyys
  alter column status set not NULL;