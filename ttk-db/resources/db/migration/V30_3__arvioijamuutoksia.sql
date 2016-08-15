alter table suorituskerta_arvioija drop column arvioija;

alter table arvioija add column arvioija_id serial not null;

ALTER TABLE arvioija DROP CONSTRAINT arvioija_pkey,
  ADD CONSTRAINT arvioija_pkey PRIMARY KEY (arvioija_id);

alter table suorituskerta_arvioija 
  add column arvioija_id integer not null references arvioija(arvioija_id);