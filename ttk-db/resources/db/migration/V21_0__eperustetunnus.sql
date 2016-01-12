alter table tutkintoversio add column eperustetunnus integer;
alter table tutkintoversio alter column peruste type text;

-- Ajetaan täydellinen eperusteet-päivitys
alter table eperusteet_log alter column paivitetty drop not null;
insert into eperusteet_log default values;