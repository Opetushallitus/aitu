alter table sopimus_ja_tutkinto
  add column nayttomestari boolean,
  add column lisatiedot text,
  add column vastuuhenkilo_vara varchar(100),
  add column puhelin_vara varchar(100),
  add column sahkoposti_vara varchar(100),
  add column nayttomestari_vara boolean,
  add column lisatiedot_vara text;
