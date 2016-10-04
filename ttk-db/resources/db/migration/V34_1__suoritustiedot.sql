--alter table arvioija
--  drop column nimi,
--  add column etunimi varchar(40) not null,
--  add column sukunimi varchar(40) not null;
  
alter table suoritus
  drop column osaamisen_tunnustaminen,
  add column osaamisen_tunnustaminen date default null;
  
comment on column suoritus.osaamisen_tunnustaminen is 'Aiemmin suoritetun osaamisen tunnustaminen, yleensä arviointikokouksen pvm.';
