alter table arvioija
  add column etunimi varchar(40),
  add column sukunimi varchar(40);
  
update arvioija set etunimi = '-';
update arvioija set sukunimi = nimi;
  
alter table arvioija
  drop column nimi,
  alter column etunimi set not null,
  alter column sukunimi set not null; 
  
alter table suoritus
  drop column osaamisen_tunnustaminen,
  add column osaamisen_tunnustaminen date default null;
  
comment on column suoritus.osaamisen_tunnustaminen is 'Aiemmin suoritetun osaamisen tunnustaminen, yleensä arviointikokouksen pvm.';
