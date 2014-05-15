alter table tutkinnonosa add column nimi_sv text;
alter table tutkinnonosa rename column nimi to nimi_fi;
alter table osaamisala add column nimi_sv text;
alter table osaamisala rename column nimi to nimi_fi;
alter table tutkinnonosa add column voimassa_alkupvm date;
alter table tutkinnonosa add column voimassa_loppupvm date;
alter table tutkinnonosa add column koodistoversio int;
alter table osaamisala add column koodistoversio int;