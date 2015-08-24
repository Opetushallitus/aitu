alter table suoritus
  drop constraint suoritus_suorituskerta_fkey,
  add constraint suoritus_suorituskerta_fkey foreign key (suorituskerta) references suorituskerta(suorituskerta_id) on delete cascade;
