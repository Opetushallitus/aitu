alter table kayttaja alter column uid set not null;
create unique index kayttaja_unique_uid on kayttaja(lower(uid));