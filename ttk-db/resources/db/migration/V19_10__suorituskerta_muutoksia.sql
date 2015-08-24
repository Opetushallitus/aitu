alter table suorituskerta
  add column jarjestamismuoto varchar(25) not null default 'ei_valmistavaa_koulutusta' check (jarjestamismuoto in ('oppilaitosmuotoinen', 'oppisopimuskoulutus', 'ei_valmistavaa_koulutusta')),
  add column opiskelijavuosi integer not null default 1;
