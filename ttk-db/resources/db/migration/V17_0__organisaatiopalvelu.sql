create table organisaatiopalvelu_log (
  id serial primary key,
  paivitetty timestamptz not null,
  muutettu_kayttaja varchar(80) NOT NULL references kayttaja(oid),
  luotu_kayttaja varchar(80) NOT NULL references kayttaja(oid),
  muutettuaika timestamptz NOT NULL,
  luotuaika timestamptz NOT NULL
);

create trigger organisaatiopalvelu_log_update before update on organisaatiopalvelu_log for each row execute procedure update_stamp() ;
create trigger organisaatiopalvelu_logl_insert before insert on organisaatiopalvelu_log for each row execute procedure update_created() ;
create trigger organisaatiopalvelu_logm_insert before insert on organisaatiopalvelu_log for each row execute procedure update_stamp() ;
create trigger organisaatiopalvelu_log_mu_update before update on organisaatiopalvelu_log for each row execute procedure update_modifier() ;
create trigger organisaatiopalvelu_log_mu_insert before insert on organisaatiopalvelu_log for each row execute procedure update_modifier() ;
create trigger organisaatiopalvelu_log_cu_insert before insert on organisaatiopalvelu_log for each row execute procedure update_creator() ;

alter table koulutustoimija add column voimassa boolean not null default true;
alter table oppilaitos add column voimassa boolean not null default true;
alter table toimipaikka add column voimassa boolean not null default true;
