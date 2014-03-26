alter table tutkinnonosa drop column osaamisala;
alter table tutkinnonosa drop column tutkinto;
create table tutkinto_ja_tutkinnonosa(
  tutkinto varchar(6) references nayttotutkinto(tutkintotunnus), 
  tutkinnonosa varchar(6) references tutkinnonosa(osatunnus),
  jarjestysnumero int not null,
  primary key(tutkinto, tutkinnonosa)
);
