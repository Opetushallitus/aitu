alter table suoritus
  add column lisatiedot varchar(500);
  
alter table suorituskerta
  drop column suoritusaika,
  add column suoritusaika_alku date not null,
  add column suoritusaika_loppu date not null,
  add column arviointikokouksen_pvm date;

comment on column suorituskerta.suoritusaika_alku is 'Arvointitilaisuuden alkupäivä.';
comment on column suorituskerta.suoritusaika_loppu is 'Arviointitilaisuuden loppupäivä.';
comment on column suorituskerta.arviointikokouksen_pvm is 'Arviointikokouksen päivämäärä';
