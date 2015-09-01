alter table henkilo
  add column syntymavuosi int,
  add column kokemusvuodet int;

alter table jasenyys
  add column status varchar(16) not null default 'nimitetty' check (status in ('esitetty', 'nimitetty', 'peruutettu')),
  add column asiantuntijaksi bool not null default false,
  add column esittaja int references jarjesto(jarjestoid),
  add column vapaateksti_kokemus text;
