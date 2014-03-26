alter table sopimus_ja_tutkinto_ja_tutkinnonosa
  add column toimipaikka varchar(7) references toimipaikka(toimipaikkakoodi);

alter table sopimus_ja_tutkinto_ja_osaamisala
  add column toimipaikka varchar(7) references toimipaikka(toimipaikkakoodi);
