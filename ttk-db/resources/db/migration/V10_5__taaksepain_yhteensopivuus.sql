alter table tutkinnonosa add column nimi text;
update tutkinnonosa set nimi = nimi_fi;

alter table osaamisala add column nimi text;
update osaamisala set nimi = nimi_fi;