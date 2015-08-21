alter table suoritus
  drop column kokotutkinto,
  add column arvosanan_korotus boolean not null default false,
  add column osaamisen_tunnustaminen boolean not null default false,
  add column kieli varchar(2) not null default 'fi' references kieli(nimi),
  add column todistus boolean not null default false;
