insert into koulutustoimija (ytunnus, nimi_fi, nimi_sv, sahkoposti, puhelin, osoite, postinumero, postitoimipaikka, www_osoite, oid) values ('0174666-4', 'Jyväskylän kaupunki', '', '', '014  266 0081', 'PL 341', '40101', 'JYVÄSKYLÄ', 'http://www.jkl.fi', '1.2.246.562.10.81269623245');
insert into koulutustoimija (ytunnus, nimi_fi, nimi_sv, sahkoposti, puhelin, osoite, postinumero, postitoimipaikka, www_osoite, oid) values ('0150605-3', 'Riihimäen keskusvankila', 'Riihimäen keskusvankila', 'harri.jokela@om.fi', '010 3681200', 'PL 100', '11311', 'RIIHIMÄKI', '', '1.2.246.562.10.97215993698');
insert into koulutustoimija (ytunnus, nimi_fi, nimi_sv, sahkoposti, puhelin, osoite, postinumero, postitoimipaikka, www_osoite, oid) values ('0150622-3', 'Rikosseuraamusalan koulutuskeskus', 'Rikosseuraamusalan koulutuskeskus', 'info.rise@om.fi', '010 3688500', 'PL 41', '01301', 'VANTAA', '', '1.2.246.562.10.42922264819');
insert into koulutustoimija (ytunnus, nimi_fi, nimi_sv, sahkoposti, puhelin, osoite, postinumero, postitoimipaikka, www_osoite, oid) values ('9999999-9', 'Pohjoiskalotin koulutussäätiö', 'Pohjoiskalotin koulutussäätiö', 'info@utbnord.se', '+46 927 751 00', 'Box 42 SE-957 21 Övertorneå', '', '', '', '1.2.246.562.10.2013120211542064151791');
insert into koulutustoimija (ytunnus, nimi_fi, nimi_sv, sahkoposti, puhelin, osoite, postinumero, postitoimipaikka, www_osoite, oid) values ('8888888-8', 'Tuntematon', 'Tuntematon', 'kirjaamo@oph.fi', '123456', 'Ei tiedossa', '00000', 'Ei tiedossa', '', '1.2.246.562.10.2013120314194853405606');
update oppilaitos set koulutustoimija = '1027740-9' where oppilaitoskoodi = '02531';
update oppilaitos set koulutustoimija = '1027740-9' where oppilaitoskoodi = '01589';
update oppilaitos set koulutustoimija = '0203929-1' where oppilaitoskoodi = '01583';
update oppilaitos set koulutustoimija = '0174666-4' where oppilaitoskoodi = '02181';

update oppilaitos set koulutustoimija = '9999999-9' where oppilaitoskoodi = '30049';
update oppilaitos set koulutustoimija = '0150622-3' where oppilaitoskoodi = '01776';
update oppilaitos set koulutustoimija = '0150605-3' where oppilaitoskoodi = '01180';
update oppilaitos set koulutustoimija = '8888888-8' where oppilaitoskoodi = '30021';
update oppilaitos set koulutustoimija = '8888888-8' where oppilaitoskoodi = '30028';


alter table jarjestamissopimus add column koulutustoimija varchar(10) references koulutustoimija(ytunnus);

update jarjestamissopimus set koulutustoimija = (select koulutustoimija from oppilaitos where oppilaitoskoodi = jarjestamissopimus.oppilaitos);
update jarjestamissopimus set tutkintotilaisuuksista_vastaava_oppilaitos = oppilaitos where tutkintotilaisuuksista_vastaava_oppilaitos is null;

alter table jarjestamissopimus alter column oppilaitos drop not null;