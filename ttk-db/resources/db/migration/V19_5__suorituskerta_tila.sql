alter table suorituskerta add column tila varchar(12) not null default 'luonnos' check (tila in ('luonnos', 'ehdotettu', 'hyvaksytty'));
