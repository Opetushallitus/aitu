alter table tutkinnonosa add column kuvaus text;
update tutkinnonosa set kuvaus = nimi;