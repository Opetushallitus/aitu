alter table osaamisala add column kuvaus text;
update osaamisala set kuvaus = nimi;