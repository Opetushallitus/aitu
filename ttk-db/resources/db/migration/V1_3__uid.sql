-- Lisää käyttäjätauluun sarakkeen UID:lle (OPH-341).
alter table kayttaja
    add uid character varying(80);
