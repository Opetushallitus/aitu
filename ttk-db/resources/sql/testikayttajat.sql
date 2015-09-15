
insert into kayttaja(uid, oid, etunimi, sukunimi, rooli)
values ('T-800', 'OID.T-800', 'Arska', 'Raaka', 'KAYTTAJA');

insert into kayttaja(uid, oid, etunimi, sukunimi, rooli)
values ('T-1001', 'OID.T-1001', 'Ivan', 'Ivanovits', 'YLLAPITAJA');

insert into jarjesto(jarjestoid, nimi_fi, keskusjarjestotieto)
values (-1, 'Testikeskusjärjestö', true);

insert into jarjesto(jarjestoid, nimi_fi, keskusjarjestoid)
values (-2, 'Testijäsenjärjestö', -1);

insert into kayttaja(uid, oid, etunimi, sukunimi, rooli, jarjesto)
values ('T-9999', 'OID.T-9999', 'Jäsen', 'Järjestö', 'JARJESTO', -1);

insert into kayttaja(uid, oid, etunimi, sukunimi, rooli)
values ('osoitepalvelu', 'OID.osoitepalvelu', 'osoitepalvelu', 'api', 'OSOITEPALVELU');

insert into kayttaja(oid, etunimi, sukunimi, rooli, voimassa, uid)
values('OID.OPH-KATSELIJA', 'Oph', 'Katselija', 'OPH-KATSELIJA', true, 'oph-katselija');

insert into kayttaja(uid, oid, etunimi, sukunimi, rooli)
values ('readaituaipal', 'OID.readaituaipal', 'readaituaipal', 'api', 'AIPAL');