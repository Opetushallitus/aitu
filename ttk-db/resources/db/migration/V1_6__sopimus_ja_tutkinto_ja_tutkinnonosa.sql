create table sopimus_ja_tutkinto_ja_tutkinnonosa(
  sopimus_ja_tutkinto integer not null references sopimus_ja_tutkinto(sopimus_ja_tutkinto_id),
  tutkinnonosa varchar(6) references tutkinnonosa(osatunnus),
  primary key(sopimus_ja_tutkinto, tutkinnonosa)
);
