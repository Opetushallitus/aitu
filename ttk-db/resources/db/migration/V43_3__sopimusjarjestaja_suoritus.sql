-- OPH-1904
alter table suorituskerta
  add column kouljarjestaja varchar(10) references koulutustoimija(ytunnus) null,
  alter column liitetty_pvm type date;
  
comment on column suorituskerta.koulutustoimija is 'Tutkinnon järjestäjä, joka on tosiasiallisesti järjestänyt tutkintosuorituksen arviointitilaisuuden.';
comment on column suorituskerta.kouljarjestaja is 'Koulutuksen järjestäjä, joka on sopimusosapuoli OPH:n ja toimikuntien suuntaan järjestäjänä.';

update suorituskerta set kouljarjestaja=koulutustoimija;

alter table suorituskerta
  alter column kouljarjestaja set not null;