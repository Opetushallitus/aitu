alter table suorituskerta drop column jarjestamismuoto;

alter table suorituskerta
  add column jarjestamismuoto varchar(25)  not null  check (jarjestamismuoto in ('oppilaitosmuotoinen', 'oppisopimuskoulutus')),
  add column valmistava_koulutus boolean  not null default true ;

comment on column suorituskerta.valmistava_koulutus is 'Opiskelija voi tulla suoraan tutkinnon näyttötilaisuuteen ilman valmistavaa koulutusta.';

alter table arvioija 
  add constraint edustus_check
    check (rooli in ('itsenainen', 'opettaja','tyonantaja','tyontekija'));
  