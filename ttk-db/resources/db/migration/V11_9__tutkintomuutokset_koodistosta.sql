create table koulutusala_tmp (
  koulutusala_tkkoodi varchar(3) primary key,
  selite_fi text,
  selite_sv text,
  voimassa_alkupvm date,
  voimassa_loppupvm date);

create table opintoala_tmp (
  opintoala_tkkoodi varchar(3) primary key, 
  koulutusala_tkkoodi varchar(3),
  selite_fi text,
  selite_sv text,
  voimassa_alkupvm date,
  voimassa_loppupvm date);

create table nayttotutkinto_tmp (
  tutkintotunnus varchar(6) primary key,
  opintoala varchar(3),
  nimi_fi text,
  nimi_sv text,
  tyyppi varchar(2),
  tutkintotaso varchar(25));

create table tutkintoversio_tmp (
  tutkintotunnus varchar(6) primary key,
  versio int,
  koodistoversio int,
  hyvaksytty boolean,
  voimassa_alkupvm timestamptz,
  voimassa_loppupvm timestamptz);

create table osaamisala_tmp (
  osaamisalatunnus varchar(5) primary key,
  nimi_fi text,
  nimi_sv text,
  voimassa_alkupvm timestamptz,
  voimassa_loppupvm timestamptz,
  versio int,
  koodistoversio int);

create table tutkinnonosa_tmp (
  osatunnus varchar(6) primary key,
  tutkintotunnus varchar(6),
  nimi_fi text,
  nimi_sv text,
  voimassa_alkupvm timestamptz,
  voimassa_loppupvm timestamptz,
  versio int,
  koodistoversio int);

create table tutkinto_ja_tutkinnonosa_tmp (
  tutkintotunnus varchar(6),
  osatunnus varchar(6),
  jarjestysnumero int,
  primary key (tutkintotunnus, osatunnus, jarjestysnumero));

insert into koulutusala_tmp (koulutusala_tkkoodi, selite_fi, selite_sv, voimassa_alkupvm, voimassa_loppupvm) values ('0','Yleissivistävä koulutus','Allmänutbildning','1995-01-01','2199-01-01');
insert into koulutusala_tmp (koulutusala_tkkoodi, selite_fi, selite_sv, voimassa_alkupvm, voimassa_loppupvm) values ('9','Muu koulutus','Övrig utbildning','1995-01-01','2199-01-01');

insert into opintoala_tmp (opintoala_tkkoodi, koulutusala_tkkoodi, selite_fi, selite_sv, voimassa_alkupvm, voimassa_loppupvm) values ('709','7','Eläinlääketiede','Veterinärmedicin','1995-01-01','2199-01-01');
insert into opintoala_tmp (opintoala_tkkoodi, koulutusala_tkkoodi, selite_fi, selite_sv, voimassa_alkupvm, voimassa_loppupvm) values ('099','0','Muu yleissivistävä koulutus','Övrig allmänutbildning','1995-01-01','2199-01-01');
insert into opintoala_tmp (opintoala_tkkoodi, koulutusala_tkkoodi, selite_fi, selite_sv, voimassa_alkupvm, voimassa_loppupvm) values ('001','0','Esiopetus','Förskoleundervisning','1995-01-01','2199-01-01');
insert into opintoala_tmp (opintoala_tkkoodi, koulutusala_tkkoodi, selite_fi, selite_sv, voimassa_alkupvm, voimassa_loppupvm) values ('002','0','Perusopetus','Grundläggande utbildning','1995-01-01','2199-01-01');
insert into opintoala_tmp (opintoala_tkkoodi, koulutusala_tkkoodi, selite_fi, selite_sv, voimassa_alkupvm, voimassa_loppupvm) values ('299','2','Muu kulttuurialan koulutus','Övrig utbildning inom kultur','1995-01-01','2199-01-01');
insert into opintoala_tmp (opintoala_tkkoodi, koulutusala_tkkoodi, selite_fi, selite_sv, voimassa_alkupvm, voimassa_loppupvm) values ('003','0','Lukiokoulutus','Gymnasieutbildning','1995-01-01','2199-01-01');
insert into opintoala_tmp (opintoala_tkkoodi, koulutusala_tkkoodi, selite_fi, selite_sv, voimassa_alkupvm, voimassa_loppupvm) values ('399','3','Muu yhteiskuntatieteiden, liiketalouden ja hallinnon alan koulutus','Övrig utbildning inom det samh.vetensk., företagsekon. och administ. området','1995-01-01','2199-01-01');
insert into opintoala_tmp (opintoala_tkkoodi, koulutusala_tkkoodi, selite_fi, selite_sv, voimassa_alkupvm, voimassa_loppupvm) values ('510','5','Tuotantotalous','Produktionsekonomi','1995-01-01','2199-01-01');
insert into opintoala_tmp (opintoala_tkkoodi, koulutusala_tkkoodi, selite_fi, selite_sv, voimassa_alkupvm, voimassa_loppupvm) values ('103','1','Historia ja arkeologia','Historia och arkeologi','1995-01-01','2199-01-01');
insert into opintoala_tmp (opintoala_tkkoodi, koulutusala_tkkoodi, selite_fi, selite_sv, voimassa_alkupvm, voimassa_loppupvm) values ('499','4','Muu luonnontieteiden alan koulutus','Övrig utbildning inom det naturvetenskapliga området','1995-01-01','2199-01-01');
insert into opintoala_tmp (opintoala_tkkoodi, koulutusala_tkkoodi, selite_fi, selite_sv, voimassa_alkupvm, voimassa_loppupvm) values ('401','4','Matematiikka','Matematik','1995-01-01','2199-01-01');
insert into opintoala_tmp (opintoala_tkkoodi, koulutusala_tkkoodi, selite_fi, selite_sv, voimassa_alkupvm, voimassa_loppupvm) values ('302','3','Kansantalous','Nationalekonomi','1995-01-01','2199-01-01');
insert into opintoala_tmp (opintoala_tkkoodi, koulutusala_tkkoodi, selite_fi, selite_sv, voimassa_alkupvm, voimassa_loppupvm) values ('203','2','Kirjallisuus','Litteratur','1995-01-01','2199-01-01');
insert into opintoala_tmp (opintoala_tkkoodi, koulutusala_tkkoodi, selite_fi, selite_sv, voimassa_alkupvm, voimassa_loppupvm) values ('104','1','Filosofia','Filosofi','1995-01-01','2199-01-01');
insert into opintoala_tmp (opintoala_tkkoodi, koulutusala_tkkoodi, selite_fi, selite_sv, voimassa_alkupvm, voimassa_loppupvm) values ('303','3','Hallinto','Administration','1995-01-01','2199-01-01');
insert into opintoala_tmp (opintoala_tkkoodi, koulutusala_tkkoodi, selite_fi, selite_sv, voimassa_alkupvm, voimassa_loppupvm) values ('105','1','Kasvatustieteet ja psykologia','Pedagogiska vetenskaper och psykologi','1995-01-01','2199-01-01');
insert into opintoala_tmp (opintoala_tkkoodi, koulutusala_tkkoodi, selite_fi, selite_sv, voimassa_alkupvm, voimassa_loppupvm) values ('403','4','Geo-, avaruus- ja tähtitieteet','Geovetenskap, rymdvetenskap och astronomi','1995-01-01','2199-01-01');
insert into opintoala_tmp (opintoala_tkkoodi, koulutusala_tkkoodi, selite_fi, selite_sv, voimassa_alkupvm, voimassa_loppupvm) values ('304','3','Tilastotiede','Statistik','1995-01-01','2199-01-01');
insert into opintoala_tmp (opintoala_tkkoodi, koulutusala_tkkoodi, selite_fi, selite_sv, voimassa_alkupvm, voimassa_loppupvm) values ('799','7','Muu sosiaali-, terveys- ja liikunta-alan koulutus','Övrig utbildning inom social-, hälso- och idrottsområdet','1995-01-01','2199-01-01');
insert into opintoala_tmp (opintoala_tkkoodi, koulutusala_tkkoodi, selite_fi, selite_sv, voimassa_alkupvm, voimassa_loppupvm) values ('998','9','Muu OPM:n hallinnonalalla järjestettävä koulutus','Övrig utbildning inom UVM:s förvaltningsområde','1995-01-01','2199-01-01');
insert into opintoala_tmp (opintoala_tkkoodi, koulutusala_tkkoodi, selite_fi, selite_sv, voimassa_alkupvm, voimassa_loppupvm) values ('404','4','Fysiikka','Fysik','1995-01-01','2199-01-01');
insert into opintoala_tmp (opintoala_tkkoodi, koulutusala_tkkoodi, selite_fi, selite_sv, voimassa_alkupvm, voimassa_loppupvm) values ('899','8','Muu matkailu-, ravitsemis- ja talousalan koulutus','Övrig utbildning inom turism-, kosthålls- och ekonomibranschen','1995-01-01','2199-01-01');
insert into opintoala_tmp (opintoala_tkkoodi, koulutusala_tkkoodi, selite_fi, selite_sv, voimassa_alkupvm, voimassa_loppupvm) values ('305','3','Sosiaalitieteet','Socialvetenskap','1995-01-01','2199-01-01');
insert into opintoala_tmp (opintoala_tkkoodi, koulutusala_tkkoodi, selite_fi, selite_sv, voimassa_alkupvm, voimassa_loppupvm) values ('206','2','Kuvataide','Bildkonst','1995-01-01','2199-01-01');
insert into opintoala_tmp (opintoala_tkkoodi, koulutusala_tkkoodi, selite_fi, selite_sv, voimassa_alkupvm, voimassa_loppupvm) values ('107','1','Teologia','Teologi','1995-01-01','2199-01-01');
insert into opintoala_tmp (opintoala_tkkoodi, koulutusala_tkkoodi, selite_fi, selite_sv, voimassa_alkupvm, voimassa_loppupvm) values ('405','4','Kemia','Kemi','1995-01-01','2199-01-01');
insert into opintoala_tmp (opintoala_tkkoodi, koulutusala_tkkoodi, selite_fi, selite_sv, voimassa_alkupvm, voimassa_loppupvm) values ('306','3','Politiikkatieteet','Politikvetenskap','1995-01-01','2199-01-01');
insert into opintoala_tmp (opintoala_tkkoodi, koulutusala_tkkoodi, selite_fi, selite_sv, voimassa_alkupvm, voimassa_loppupvm) values ('207','2','Kulttuurin- ja taiteiden tutkimus','Kultur- och konstforskning','1995-01-01','2199-01-01');
insert into opintoala_tmp (opintoala_tkkoodi, koulutusala_tkkoodi, selite_fi, selite_sv, voimassa_alkupvm, voimassa_loppupvm) values ('406','4','Biologia','Biologi','1995-01-01','2199-01-01');
insert into opintoala_tmp (opintoala_tkkoodi, koulutusala_tkkoodi, selite_fi, selite_sv, voimassa_alkupvm, voimassa_loppupvm) values ('307','3','Oikeustiede','Juridik','1995-01-01','2199-01-01');
insert into opintoala_tmp (opintoala_tkkoodi, koulutusala_tkkoodi, selite_fi, selite_sv, voimassa_alkupvm, voimassa_loppupvm) values ('901','9','Sotilas- ja rajavartioala','Militär- och gränsbevakningsbranschen','1995-01-01','2199-01-01');
insert into opintoala_tmp (opintoala_tkkoodi, koulutusala_tkkoodi, selite_fi, selite_sv, voimassa_alkupvm, voimassa_loppupvm) values ('407','4','Maantiede','Geografi','1995-01-01','2199-01-01');
insert into opintoala_tmp (opintoala_tkkoodi, koulutusala_tkkoodi, selite_fi, selite_sv, voimassa_alkupvm, voimassa_loppupvm) values ('902','9','Palo- ja pelastusala','Brand- och räddningsbranschen','1995-01-01','2199-01-01');
insert into opintoala_tmp (opintoala_tkkoodi, koulutusala_tkkoodi, selite_fi, selite_sv, voimassa_alkupvm, voimassa_loppupvm) values ('969','9','Muu OPM:n hallinnonalan ulkopuolella järjestettävä koulutus','Övrig utbildning utanför UVM:s förvaltningsområde','1995-01-01','2199-01-01');
insert into opintoala_tmp (opintoala_tkkoodi, koulutusala_tkkoodi, selite_fi, selite_sv, voimassa_alkupvm, voimassa_loppupvm) values ('903','9','Poliisiala','Polisbranschen','1995-01-01','2199-01-01');
insert into opintoala_tmp (opintoala_tkkoodi, koulutusala_tkkoodi, selite_fi, selite_sv, voimassa_alkupvm, voimassa_loppupvm) values ('904','9','Vankeinhoito','Fångvård','1995-01-01','2199-01-01');
insert into opintoala_tmp (opintoala_tkkoodi, koulutusala_tkkoodi, selite_fi, selite_sv, voimassa_alkupvm, voimassa_loppupvm) values ('708','7','Lääketiede','Medicin','1995-01-01','2199-01-01');

insert into nayttotutkinto_tmp (tutkintotunnus, nimi_fi, nimi_sv, tyyppi, tutkintotaso, opintoala) values ('387599', 'Muu tai tuntematon suojelualan erikoisammattitutkinto', 'Annan eller okänd specialyrkesexamen inom skyddsbranschen', '03', 'erikoisammattitutkinto', '969');
insert into tutkintoversio_tmp (tutkintotunnus, versio, koodistoversio, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm) values ('387599', 1, 0, true, '1997-01-01', '2199-01-01');
insert into nayttotutkinto_tmp (tutkintotunnus, nimi_fi, nimi_sv, tyyppi, tutkintotaso, opintoala) values ('384599', 'Muu tai tuntematon suojelualan ammattitutkinto', 'Annan eller okänd yrkesexamen inom skyddsbranschen', '03', 'ammattitutkinto', '969');
insert into tutkintoversio_tmp (tutkintotunnus, versio, koodistoversio, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm) values ('384599', 1, 0, true, '1997-01-01', '2199-01-01');
insert into nayttotutkinto_tmp (tutkintotunnus, nimi_fi, nimi_sv, tyyppi, tutkintotaso, opintoala) values ('331954', 'Vakuutusalan perustutkinto', 'Försäkringsbranschen, grundexamen', '02', 'perustutkinto', '303');
insert into tutkintoversio_tmp (tutkintotunnus, versio, koodistoversio, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm) values ('331954', 1, 0, true, '1997-01-01', '2199-01-01');
insert into nayttotutkinto_tmp (tutkintotunnus, nimi_fi, nimi_sv, tyyppi, tutkintotaso, opintoala) values ('387999', 'Muu tai tuntematon palvelualojen erikoisammattitutkinto', 'Annan eller okänd specialyrkesexamen inom servicebranscher', '03', 'erikoisammattitutkinto', '969');
insert into tutkintoversio_tmp (tutkintotunnus, versio, koodistoversio, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm) values ('387999', 1, 0, true, '1997-01-01', '2199-01-01');
insert into nayttotutkinto_tmp (tutkintotunnus, nimi_fi, nimi_sv, tyyppi, tutkintotaso, opintoala) values ('331955', 'Sosiaaliturvan perustutkinto', 'Socialskydd, grundexamen', '02', 'perustutkinto', '303');
insert into tutkintoversio_tmp (tutkintotunnus, versio, koodistoversio, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm) values ('331955', 1, 0, true, '1997-01-01', '2199-01-01');
insert into nayttotutkinto_tmp (tutkintotunnus, nimi_fi, nimi_sv, tyyppi, tutkintotaso, opintoala) values ('381521', 'Vankeinhoidon perustutkinto', 'Fångvårdens grundexamen', '02', 'perustutkinto', '904');
insert into tutkintoversio_tmp (tutkintotunnus, versio, koodistoversio, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm) values ('381521', 1, 0, true, '1997-01-01', '2199-01-01');
insert into nayttotutkinto_tmp (tutkintotunnus, nimi_fi, nimi_sv, tyyppi, tutkintotaso, opintoala) values ('384999', 'Muu tai tuntematon palvelualojen ammattitutkinto', 'Annan eller okänd yrkesexamen inom servicebranscher', '03', 'ammattitutkinto', '969');
insert into tutkintoversio_tmp (tutkintotunnus, versio, koodistoversio, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm) values ('384999', 1, 0, true, '1997-01-01', '2199-01-01');
insert into nayttotutkinto_tmp (tutkintotunnus, nimi_fi, nimi_sv, tyyppi, tutkintotaso, opintoala) values ('381514', 'Poliisin perustutkinto', 'Grundexamen för polis', '02', 'perustutkinto', '903');
insert into tutkintoversio_tmp (tutkintotunnus, versio, koodistoversio, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm) values ('381514', 1, 0, true, '1997-01-01', '2199-01-01');
insert into nayttotutkinto_tmp (tutkintotunnus, nimi_fi, nimi_sv, tyyppi, tutkintotaso, opintoala) values ('327399', 'Muu tai tuntematon kuvataiteen erikoisammattitutkinto', 'Annan eller okänd specialyrkesexamen i bildkonst', '03', 'erikoisammattitutkinto', '206');
insert into tutkintoversio_tmp (tutkintotunnus, versio, koodistoversio, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm) values ('327399', 1, 0, true, '1997-01-01', '2199-01-01');
insert into nayttotutkinto_tmp (tutkintotunnus, nimi_fi, nimi_sv, tyyppi, tutkintotaso, opintoala) values ('324399', 'Muu tai tuntematon kuvataiteen ammattitutkinto', 'Annan eller okänd yrkesexamen i bildkonst', '03', 'ammattitutkinto', '206');
insert into tutkintoversio_tmp (tutkintotunnus, versio, koodistoversio, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm) values ('324399', 1, 0, true, '1997-01-01', '2199-01-01');
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('100320', 'Palvelun toteuttaminen', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('100364', 'Myynnin tukipalvelut', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('100375', 'Tuoteneuvonta', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('100386', 'Käytön tuki tieto- ja kirjastopalvelussa', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('100321', 'Asiakaspalvelu ja myyntityö', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('100365', 'Talouspalvelut', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('100376', 'Toiminnan kannattavuuden suunnittelu', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('100387', 'Verkkopalvelut tieto- ja kirjastopalvelussa', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('104578', 'Painotuotteen laadun kehittäminen', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('100322', 'Kulttuurin soveltaminen käsityöhön', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('100366', 'Kirjanpito', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('100377', 'Tilinpäätöskirjaukset ja yrityksen verotus', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('104579', 'Työpaikkakouluttajana toimiminen', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('100323', 'Ohjaustoiminta', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('100367', 'Toimistopalvelut', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('100378', 'Kansainvälisen kaupan laskutus ja reskontran hoito', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('100324', 'Palvelun tuotteistaminen', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('100368', 'Palkanlaskenta', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('100379', 'Työvälineohjelmien käyttö', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('100325', 'Sisustustyö', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('100369', 'Tieto- ja kirjastopalvelu', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('100315', 'Asiakaslähtöinen valmistaminen', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('100326', 'Stailaus', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('100359', 'Asiakaspalvelu', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('100316', 'Kulttuurilähtöinen valmistaminen', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('100327', 'Taidekäsityö', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('100317', 'Tuotteen suunnittelu', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('100328', 'Tilaustyön valmistaminen', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('100318', 'Tuotteen valmistaminen', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('100329', 'Tuotekehitys', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('100319', 'Toteuttamisen suunnittelu', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('104193', 'Ihonhoidon asiantuntijana toimiminen', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('104194', 'Kauneudenhoitoalan asiantuntijana toimiminen', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('104195', 'Aromaterapian ja eteeristen öljyjen käyttäminen ihonhoidossa', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('104580', 'Suunnittelun ja kehittämisen hallinta', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('104196', 'Lymfahoitomenetelmän käyttäminen ihonhoidossa', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('104581', 'Painaminen ja painokoneen hallinta', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('104197', 'Jalkojen hyvinvoinnin tukeminen ja ennaltaehkäiseva jalkahoito kauneudenhoitoalalla', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('100380', 'Yhdistyksen ja asunto-osakeyhtiön asiakirjojen hoitaminen', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('104582', 'Yrittäjyys', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('104198', 'Kauneudenhoitoalan palvelujen tuottaminen ja kehittäminen', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('100370', 'Tiedottaminen sekä kirjastonkäytön ja tiedonhaun opastus', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('100381', 'Verkkopalvelujen tuottaminen ja ylläpito', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('104583', 'Esimiestoiminta', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('100360', 'Kaupan palvelu ja myynti', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('100371', 'Aineiston hankinta ja kokoelman ylläpito', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('100382', 'Rahoituspalvelut', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('100361', 'Asiakkuuksien hoito ja myyntityö', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('100372', 'Aineiston tallentaminen, kuvailu ja esittely', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('100383', 'Asiakasryhmien kirjastopalvelut', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('100362', 'Visuaalinen myyntityö', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('100373', 'Markkinointiviestinnän toimenpiteiden suunnittelu ja toteutus', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('100384', 'Erikoiskirjastopalvelut', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('100330', 'Tuotteen valmistaminen käsityönä', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('100363', 'Kansainvälisen kaupan tukipalvelut', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('100374', 'Sähköinen kaupankäynti', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('100385', 'Luettelointi ja sisällön kuvailu', '', '2013-12-17', '2199-01-01', 1, 1);
update osaamisala set nimi_sv='Kompetensområde för grönsektorn' where osaamisalatunnus = '1584';
update osaamisala set nimi_sv='Kompetensområde för säkerhetsbranschen' where osaamisalatunnus = '1595';
update osaamisala set nimi_sv='Kompetensområde för lokalvård' where osaamisalatunnus = '1650';
update osaamisala set nimi_sv='Kompetensområde för ytbehandling inom byggbranchen' where osaamisalatunnus = '1540';
update osaamisala set nimi_sv='Kompetensområde för köttbranschen' where osaamisalatunnus = '1551';
update osaamisala set nimi_sv='Kompetensområde för blomster- och trädgårdshandel' where osaamisalatunnus = '1585';
update osaamisala set nimi_sv='Kompetensområde för fotografering' where osaamisalatunnus = '1640';
update osaamisala set nimi_sv='Kompetensområde för flygplansmekanik' where osaamisalatunnus = '1530';
update osaamisala set nimi_sv='Kompetensområde för flygteknik' where osaamisalatunnus = '1651';
insert into osaamisala_tmp (osaamisalatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('1541', 'Puutuotteiden pintakäsittelyjen osaamisala', 'Kompetensområde för ytbehandling av trävaror', '2014-01-01', '2199-01-01', 1, 1);
update osaamisala set nimi_sv='Kompetensområde för mejeribranschen' where osaamisalatunnus = '1552';
update osaamisala set nimi_sv='Kompetensområde för informations- och bibliotekstjänst' where osaamisalatunnus = '1563';
update osaamisala set nimi_sv='Kompetensområde för fiskeri' where osaamisalatunnus = '1586';
update osaamisala set nimi_sv='Kompetensområde för stenbranschen' where osaamisalatunnus = '1630';
update osaamisala set nimi_sv='Kompetensområde för musikteknologi' where osaamisalatunnus = '1641';
update osaamisala set nimi_sv='Kompetensområde för transportservice' where osaamisalatunnus = '1531';
update osaamisala set nimi_sv='Kompetensområde för pappersindustrin' where osaamisalatunnus = '1652';
update osaamisala set nimi_sv='Kompetensområde för ytbehandling av metallprodukter' where osaamisalatunnus = '1542';
update osaamisala set nimi_sv='Kompetensområde för däcks- och maskinreparationer' where osaamisalatunnus = '1553';
update osaamisala set nimi_sv='Kompetensområde för skogsbruk' where osaamisalatunnus = '1587';
update osaamisala set nimi_sv='Kompetensområde för rehabilitering' where osaamisalatunnus = '1598';
update osaamisala set nimi_sv='Kompetensområde för lantbruksteknologi' where osaamisalatunnus = '1620';
update osaamisala set nimi_sv='Kompetensområde för mentalhälsoarbete och missbrukarvård' where osaamisalatunnus = '1510';
update osaamisala set nimi_sv='Kompetensområde för gruvbranschen' where osaamisalatunnus = '1631';
update osaamisala set nimi_sv='Kompetensområde för pianostämmning' where osaamisalatunnus = '1642';
update osaamisala set nimi_sv='Kompetensområde för lagerservice' where osaamisalatunnus = '1532';
update osaamisala set nimi_sv='Kompetensområde för hemarbetsservice' where osaamisalatunnus = '1653';
update osaamisala set nimi_sv='Kompetensområde för kemisk teknik' where osaamisalatunnus = '1543';
update osaamisala set nimi_sv='Kompetensområde för eldrift' where osaamisalatunnus = '1554';
update osaamisala set nimi_sv='Kompetensområde för textilvård' where osaamisalatunnus = '1577';
update osaamisala set nimi_sv='Kompetensområde för körning av skogsmaskiner' where osaamisalatunnus = '1588';
update osaamisala set nimi_sv='Kompetensområde för tandteknik' where osaamisalatunnus = '1599';
update osaamisala set nimi_sv='kompetensområde för barn- och familjearbete' where osaamisalatunnus = '1610';
update osaamisala set nimi_sv='Kompetensområde för djurskötsel' where osaamisalatunnus = '1621';
update osaamisala set nimi_sv='Kompetensområde för sjukvård och omsorg' where osaamisalatunnus = '1511';
update osaamisala set nimi_sv='Kompetensområde för produktion av skogsenergi' where osaamisalatunnus = '1632';
update osaamisala set nimi_sv='Kompetensområde för automationsteknik och underhåll' where osaamisalatunnus = '1522';
update osaamisala set nimi_sv='Kompetensområde för mikrostöd' where osaamisalatunnus = '1643';
update osaamisala set nimi_sv='Kompetensområde för rengöringsservice' where osaamisalatunnus = '1654';
update osaamisala set nimi_sv='Kompetensområde för bioteknik' where osaamisalatunnus = '1544';
update osaamisala set nimi_sv='Kompetensområde för maskinbefäl' where osaamisalatunnus = '1555';
update osaamisala set nimi_sv='Kompetensområde för frisör' where osaamisalatunnus = '1578';
update osaamisala set nimi_sv='Kompetensområde för läkemedelsbranschen' where osaamisalatunnus = '1600';
update osaamisala set nimi_sv='Kompetensområde för idrottsinstruktion' where osaamisalatunnus = '1611';
update osaamisala set nimi_sv='Kompetensområde för fastighetsskötsel' where osaamisalatunnus = '1501';
update osaamisala set nimi_sv='Kompetensområde för reparation av motordrivna småmaskiner' where osaamisalatunnus = '1622';
update osaamisala set nimi_sv='Kompetensområde för munhälsovård' where osaamisalatunnus = '1512';
update osaamisala set nimi_sv='Kompetensområde för el- och automationsteknik' where osaamisalatunnus = '1633';
update osaamisala set nimi_sv='Kompetensområde för gjutningsteknik' where osaamisalatunnus = '1523';
update osaamisala set nimi_sv='Kompetensområde för programmering' where osaamisalatunnus = '1644';
update osaamisala set nimi_sv='Kompetensområde för fotvård' where osaamisalatunnus = '1655';
update osaamisala set nimi_sv='Kompetensområde för laboratoriebranschen' where osaamisalatunnus = '1545';
update osaamisala set nimi_sv='Kompetensområde för däcksbefäl' where osaamisalatunnus = '1556';
update osaamisala set nimi_sv='Kompetensområde för kosmetolog' where osaamisalatunnus = '1579';
update osaamisala set nimi_sv='Kompetensområde för ungdoms- och fritidsinstruktion' where osaamisalatunnus = '1612';
update osaamisala set nimi_sv='Utbildning för montering av skogsmaskiner' where osaamisalatunnus = '1623';
update osaamisala set nimi_sv='Kompetensområde för handikappomsorg' where osaamisalatunnus = '1513';
update osaamisala set nimi_sv='Kompetensområde för skönhetsvård och produktrådgiving' where osaamisalatunnus = '1634';
update osaamisala set nimi_sv='Kompetensområde för tillverkningsteknik' where osaamisalatunnus = '1524';
update osaamisala set nimi_sv='Kompetensområde för rörmontering' where osaamisalatunnus = '1645';
update osaamisala set nimi_sv='Kompetensområde för skivindustrin' where osaamisalatunnus = '1535';
update osaamisala set nimi_sv='Kompetensområde för urbranschen' where osaamisalatunnus = '1557';
update osaamisala set nimi_sv='Kompetensområde för turismservice' where osaamisalatunnus = '1568';
update osaamisala set nimi_sv='Kompetensområde för flygledning' where osaamisalatunnus = '1613';
update osaamisala set nimi_sv='Kompetensområde för produktplanering och -tillverkning' where osaamisalatunnus = '1624';
update osaamisala set nimi_sv='Kompetensområde för äldreomsorg' where osaamisalatunnus = '1514';
update osaamisala set nimi_sv='Kompetensområde för datateknik och datakommunikationsteknik' where osaamisalatunnus = '1635';
update osaamisala set nimi_sv='Kompetensområde för bilplåtslagare' where osaamisalatunnus = '1525';
update osaamisala set nimi_sv='Kompetensområde för ventilationsmontering' where osaamisalatunnus = '1646';
update osaamisala set nimi_sv='Kompetensområde för industrisnickare' where osaamisalatunnus = '1536';
update osaamisala set nimi_sv='Kompetensområde för mikromekanik' where osaamisalatunnus = '1558';
update osaamisala set nimi_sv='Kompetensområde för försäljning av turismservice och informationstjänst' where osaamisalatunnus = '1569';
update osaamisala set nimi_sv='Kompetensområde för miljöplanering och -byggande' where osaamisalatunnus = '1603';
update osaamisala set nimi_sv='Kompetensområde för jordbyggnad' where osaamisalatunnus = '1504';
update osaamisala set nimi_sv='Kompetensområde för kundbetjäning och försäljning' where osaamisalatunnus = '1625';
update osaamisala set nimi_sv='Kompetensområde för kundbetjäning och informationshantering' where osaamisalatunnus = '1515';
update osaamisala set nimi_sv='Kompetensområde för kundservice' where osaamisalatunnus = '1636';
update osaamisala set nimi_sv='Kompetensområde för billackering' where osaamisalatunnus = '1526';
update osaamisala set nimi_sv='Kompetensområde för kylmontering' where osaamisalatunnus = '1647';
update osaamisala set nimi_sv='Kompetensområde för sågindustrin' where osaamisalatunnus = '1537';
update osaamisala set nimi_sv='Kompetensområde för gummiteknik' where osaamisalatunnus = '1559';
update osaamisala set nimi_sv='Kompetensområde för audiovisuell kommunikation' where osaamisalatunnus = '1604';
update osaamisala set nimi_sv='Kompetensområde för apoteksbranschen' where osaamisalatunnus = '1615';
update osaamisala set nimi_sv='Kompetensområde för schaktningsmaskintransportKompetensområde för schaktningsmaskintransport' where osaamisalatunnus = '1505';
update osaamisala set nimi_sv='Kompetensområde för ekonomi- och kontorsservice' where osaamisalatunnus = '1626';
update osaamisala set nimi_sv='Kompetensområde för kock' where osaamisalatunnus = '1637';
update osaamisala set nimi_sv='Kompetensområde för bilförsäljning' where osaamisalatunnus = '1527';
update osaamisala set nimi_sv='Kompetensområde för isolering och montering av byggnadsplåt' where osaamisalatunnus = '1648';
update osaamisala set nimi_sv='Kompetensområde för båtbyggnad' where osaamisalatunnus = '1538';
update osaamisala set nimi_sv='Kompetensområde för livsmedelsteknologi' where osaamisalatunnus = '1549';
update osaamisala set nimi_sv='Kompetensområde för husbyggnad' where osaamisalatunnus = '1506';
update osaamisala set nimi_sv='Kompetensområde för beklädnad' where osaamisalatunnus = '1627';
update osaamisala set nimi_sv='Kompetensområde för textilteknik' where osaamisalatunnus = '1517';
update osaamisala set nimi_sv='Kompetensområde för grafisk planering' where osaamisalatunnus = '1638';
update osaamisala set nimi_sv='Kompetensområde för bilteknik' where osaamisalatunnus = '1528';
update osaamisala set nimi_sv='Kompetensområde för tapetsering' where osaamisalatunnus = '1539';
update osaamisala set nimi_sv='Kompetensområde för dans' where osaamisalatunnus = '1606';
update osaamisala set nimi_sv='Kompetensområde för flygplatsservice' where osaamisalatunnus = '1617';
update osaamisala set nimi_sv='Kompetensområde för lantmäteriteknik' where osaamisalatunnus = '1507';
update osaamisala set nimi_sv='Kompetensområde för bild- och mediekonst' where osaamisalatunnus = '1639';
update osaamisala set nimi_sv='Kompetensområde för reservdelsförsäljning' where osaamisalatunnus = '1529';
update osaamisala set nimi_sv='Kompetensområde för musik' where osaamisalatunnus = '1607';
update osaamisala set nimi_sv='Kompetensområde för inredning' where osaamisalatunnus = '1618';
update osaamisala set nimi_sv='Kompetensområde för akutvård' where osaamisalatunnus = '1508';
update osaamisala set nimi_sv='Kompetensområde för skobranschen' where osaamisalatunnus = '1629';
update osaamisala set nimi_sv='Kompetensområde för vård och fostran av barn och unga' where osaamisalatunnus = '1509';
update osaamisala set nimi_sv='Kompetensområde för teckenspråkhandledning' where osaamisalatunnus = '1609';
update osaamisala set nimi_sv='Kompetensområde för miljö' where osaamisalatunnus = '1590';
insert into osaamisala_tmp (osaamisalatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('2030', 'Kemian perusteollisuus', '', '2014-01-01', '2199-01-01', 1, 1);
update osaamisala set nimi_sv='Kompetensområde för lantbruk' where osaamisalatunnus = '1580';
update osaamisala set nimi_sv='Kompetensområde för natur' where osaamisalatunnus = '1591';
insert into osaamisala_tmp (osaamisalatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('2031', 'Kemian tuoteteollisuus', '', '2014-01-01', '2199-01-01', 1, 1);
update osaamisala set nimi_sv='Kompetensområde för hästhushållning' where osaamisalatunnus = '1581';
update osaamisala set nimi_sv='Kompetensområde för renskötsel' where osaamisalatunnus = '1592';
insert into osaamisala_tmp (osaamisalatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('2032', 'Lääke- ja bioteollisuus', '', '2014-01-01', '2199-01-01', 1, 1);
update osaamisala set nimi_sv='Kompetensområde för pälsdjursuppfödning' where osaamisalatunnus = '1582';
update osaamisala set nimi_sv='Kompetensområde för layout' where osaamisalatunnus = '1593';
update osaamisala set nimi_sv='Kompetensområde för plastteknik' where osaamisalatunnus = '1560';
update osaamisala set nimi_sv='Kompetensområde för trädgårdsproduktion' where osaamisalatunnus = '1583';
update osaamisala set nimi_sv='Kompetensområde för tryckeriteknik' where osaamisalatunnus = '1594';
update osaamisala set nimi_sv='Kompetensområde för bageribranscen' where osaamisalatunnus = '1550';
update osaamisala set nimi_sv='Kompetensområde för teknisk planering' where osaamisalatunnus = '1561';
update osaamisala set nimi_sv='Kompetensområde för hotellservice' where osaamisalatunnus = '1572';
insert into tutkinto_ja_tutkinnonosa_tmp (tutkintotunnus, osatunnus, jarjestysnumero) values ('331100','100366',8), ('331100','100367',9), ('331100','100382',24), ('331100','100385',27), ('331100','100381',23), ('331100','100380',22), ('331100','100383',25), ('331100','100384',26), ('331100','100376',18), ('331100','100377',19), ('331100','100375',17), ('331100','100379',21), ('331100','100360',2), ('331100','100361',3), ('331100','101040',31), ('331100','100378',20), ('331100','101039',30), ('331100','101041',32), ('331100','100362',4), ('331100','100363',5), ('331100','100387',29), ('331100','100386',28), ('331100','100372',14), ('331100','100359',1), ('331100','100371',13), ('331100','100373',15), ('331100','100365',7), ('331100','100368',10), ('331100','100369',11), ('331100','100364',6), ('331100','100370',12), ('331100','100374',16);
insert into tutkinto_ja_tutkinnonosa_tmp (tutkintotunnus, osatunnus, jarjestysnumero) values ('387300','104194',2), ('387300','104193',1), ('387300','104195',3), ('387300','104196',4), ('387300','104198',6), ('387300','104197',5);
insert into tutkinto_ja_tutkinnonosa_tmp (tutkintotunnus, osatunnus, jarjestysnumero) values ('358504','104580',3), ('358504','104583',6), ('358504','104581',4), ('358504','104582',5), ('358504','104579',2), ('358504','104578',1);
insert into tutkinto_ja_tutkinnonosa_tmp (tutkintotunnus, osatunnus, jarjestysnumero) values ('355902','103400',15), ('355902','103404',19), ('355902','103410',25), ('355902','103391',6), ('355902','103405',20), ('355902','103401',16), ('355902','103407',22), ('355902','103390',5), ('355902','103406',21), ('355902','103402',17), ('355902','103388',3), ('355902','103386',1), ('355902','103387',2), ('355902','103392',7), ('355902','103396',11), ('355902','103408',23), ('355902','103393',8), ('355902','103395',10), ('355902','103397',12), ('355902','103399',14), ('355902','103409',24), ('355902','103398',13), ('355902','103394',9), ('355902','103389',4);
insert into tutkinto_ja_tutkinnonosa_tmp (tutkintotunnus, osatunnus, jarjestysnumero) values ('324100','100323',9), ('324100','100327',13), ('324100','100322',8), ('324100','100326',12), ('324100','100318',4), ('324100','101039',17), ('324100','100319',5), ('324100','100328',14), ('324100','100329',15), ('324100','100316',2), ('324100','100315',1), ('324100','100324',10), ('324100','100320',6), ('324100','100321',7), ('324100','100325',11), ('324100','100330',16), ('324100','100317',3), ('324100','101041',19), ('324100','101040',18);
insert into tutkinto_ja_tutkinnonosa_tmp (tutkintotunnus, osatunnus, jarjestysnumero) values ('337102','104200',2), ('337102','104199',1), ('337102','105042',3), ('337102','104202',4);
insert into tutkinto_ja_tutkinnonosa_tmp (tutkintotunnus, osatunnus, jarjestysnumero) values ('355211','102595',57);
insert into tutkinto_ja_tutkinnonosa_tmp (tutkintotunnus, osatunnus, jarjestysnumero) values ('334113','103110',4), ('334113','103108',2), ('334113','103107',1), ('334113','103109',3);
update osaamisala set tutkintoversio = (select tutkintoversio_id from tutkintoversio where tutkintotunnus = '354601') where osaamisalatunnus = '2030';
update osaamisala set tutkintoversio = (select tutkintoversio_id from tutkintoversio where tutkintotunnus = '354601') where osaamisalatunnus = '2031';
update osaamisala set tutkintoversio = (select tutkintoversio_id from tutkintoversio where tutkintotunnus = '354601') where osaamisalatunnus = '2032';
update osaamisala set tutkintoversio = (select tutkintoversio_id from tutkintoversio where tutkintotunnus = '354605') where osaamisalatunnus = '2057';
update osaamisala set tutkintoversio = (select tutkintoversio_id from tutkintoversio where tutkintotunnus = '354605') where osaamisalatunnus = '2058';
update osaamisala set tutkintoversio = (select tutkintoversio_id from tutkintoversio where tutkintotunnus = '354605') where osaamisalatunnus = '2059';
update osaamisala set tutkintoversio = (select tutkintoversio_id from tutkintoversio where tutkintotunnus = '354605') where osaamisalatunnus = '2056';
update osaamisala set tutkintoversio = (select tutkintoversio_id from tutkintoversio where tutkintotunnus = '351805') where osaamisalatunnus = '1540';
update osaamisala set tutkintoversio = (select tutkintoversio_id from tutkintoversio where tutkintotunnus = '351805') where osaamisalatunnus = '1541';
update osaamisala set tutkintoversio = (select tutkintoversio_id from tutkintoversio where tutkintotunnus = '351805') where osaamisalatunnus = '1542';

insert into koulutusala (koulutusala_tkkoodi, selite_fi, selite_sv, voimassa_alkupvm, voimassa_loppupvm)
select koulutusala_tkkoodi, selite_fi, selite_sv, voimassa_alkupvm, voimassa_loppupvm from koulutusala_tmp
where exists (select 1 from koulutusala where koulutusala_tkkoodi is not null);

insert into opintoala (opintoala_tkkoodi, koulutusala_tkkoodi, selite_fi, selite_sv, voimassa_alkupvm, voimassa_loppupvm)
select t.opintoala_tkkoodi, t.koulutusala_tkkoodi, t.selite_fi, t.selite_sv, t.voimassa_alkupvm, t.voimassa_loppupvm from opintoala_tmp t
join koulutusala k on t.koulutusala_tkkoodi = k.koulutusala_tkkoodi;

insert into nayttotutkinto (tutkintotunnus, opintoala, nimi_fi, nimi_sv, tyyppi, tutkintotaso)
select tutkintotunnus, opintoala, nimi_fi, nimi_sv, tyyppi, tutkintotaso from nayttotutkinto_tmp t
join opintoala o on t.opintoala = o.opintoala_tkkoodi;

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm)
select v.tutkintotunnus, v.versio, v.koodistoversio, v.hyvaksytty, v.voimassa_alkupvm, v.voimassa_loppupvm
from tutkintoversio_tmp v
join nayttotutkinto t on v.tutkintotunnus = t.tutkintotunnus;

update nayttotutkinto
set uusin_versio_id = 
  (select tutkintoversio_id from tutkintoversio v where v.tutkintotunnus = nayttotutkinto.tutkintotunnus)
where uusin_versio_id is null;

insert into tutkinnonosa (osatunnus, nimi_fi, nimi_sv, versio, koodistoversio, voimassa_alkupvm, voimassa_loppupvm)
select o.osatunnus, o.nimi_fi, o.nimi_sv, o.versio, o.koodistoversio, o.voimassa_alkupvm, o.voimassa_loppupvm
from tutkinnonosa_tmp o;

insert into tutkinto_ja_tutkinnonosa (tutkintoversio, tutkinnonosa, jarjestysnumero)
select v.tutkintoversio_id tutkintoversio, o.tutkinnonosa_id tutkinnonosa, jarjestysnumero
from tutkinto_ja_tutkinnonosa_tmp tt
join tutkintoversio v on v.tutkintotunnus = tt.tutkintotunnus
join tutkinnonosa o on o.osatunnus = tt.osatunnus
where not exists (select 1 from tutkinto_ja_tutkinnonosa t where t.tutkintoversio = v.tutkintoversio_id and t.tutkinnonosa = o.tutkinnonosa_id);

update tutkinto_ja_tutkinnonosa t
set jarjestysnumero = tt.jarjestysnumero
from tutkinto_ja_tutkinnonosa_tmp tt
join tutkintoversio v on v.tutkintotunnus = tt.tutkintotunnus
join tutkinnonosa o on o.osatunnus = tt.osatunnus
where t.tutkintoversio = v.tutkintoversio_id and t.tutkinnonosa = o.tutkinnonosa_id;

insert into osaamisala (osaamisalatunnus, versio, koodistoversio, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm)
select osaamisalatunnus, versio, koodistoversio, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm
from osaamisala_tmp;

drop table nayttotutkinto_tmp;
drop table tutkintoversio_tmp;
drop table tutkinnonosa_tmp;
drop table tutkinto_ja_tutkinnonosa_tmp;
drop table osaamisala_tmp;
