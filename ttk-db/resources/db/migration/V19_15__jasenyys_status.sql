create table jasenyyden_status(nimi varchar(16) primary key);

insert into jasenyyden_status(nimi) values('esitetty'),('nimitetty'),('peruutettu');

alter table jasenyys
  drop constraint jasenyys_status_check,
  add foreign key (status) references jasenyyden_status(nimi);
