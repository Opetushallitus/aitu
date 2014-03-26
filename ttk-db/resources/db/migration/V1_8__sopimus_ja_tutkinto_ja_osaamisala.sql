create table sopimus_ja_tutkinto_ja_osaamisala(
  sopimus_ja_tutkinto integer not null references sopimus_ja_tutkinto(sopimus_ja_tutkinto_id),
  osaamisala varchar(5) not null references osaamisala(osaamisalatunnus),
  primary key(sopimus_ja_tutkinto, osaamisala)
);
