alter table suorituskerta
  alter column suoritusaika_alku drop not null,
  alter column suoritusaika_loppu drop not null;