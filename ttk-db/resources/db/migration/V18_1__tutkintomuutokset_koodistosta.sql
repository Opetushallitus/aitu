drop table koulutusala_tmp;
drop table opintoala_tmp;
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

update tutkintoversio set voimassa_loppupvm='2011-02-28' where tutkintotunnus = '367202';
update tutkintoversio set voimassa_loppupvm='2014-12-24' where tutkintotunnus = '344102';
update tutkintoversio set voimassa_loppupvm='2005-03-31' where tutkintotunnus = '358402';
update nayttotutkinto set nimi_sv='Omsorgsarbete för utvecklingsstörda, yrkesexamen' where tutkintotunnus = '374122';

update tutkintoversio set voimassa_loppupvm='2014-12-24' where tutkintotunnus = '355202';
update tutkintoversio set voimassa_loppupvm='2014-12-24' where tutkintotunnus = '324501';
update tutkintoversio set voimassa_loppupvm='2005-03-31' where tutkintotunnus = '358403';
update nayttotutkinto set nimi_sv='Yrkesexamen inom finansbranschen', nimi_fi='Finanssialan ammattitutkinto' where tutkintotunnus = '334115';

update tutkintoversio set voimassa_loppupvm='2014-12-24' where tutkintotunnus = '324106';

update tutkintoversio set voimassa_loppupvm='2014-12-24' where tutkintotunnus = '387301';
update tutkintoversio set voimassa_loppupvm='2009-12-31' where tutkintotunnus = '374112';
update tutkintoversio set voimassa_loppupvm='2014-12-24' where tutkintotunnus = '355401';
update tutkintoversio set voimassa_loppupvm='2014-12-24' where tutkintotunnus = '355203';
update tutkintoversio set voimassa_loppupvm='2014-12-24' where tutkintotunnus = '367303';
update tutkintoversio set voimassa_loppupvm='2014-12-24' where tutkintotunnus = '358404';
update tutkintoversio set voimassa_loppupvm='2014-12-24' where tutkintotunnus = '324118';
update tutkintoversio set voimassa_loppupvm='2014-12-24' where tutkintotunnus = '387302';
update tutkintoversio set voimassa_loppupvm='2014-12-24' where tutkintotunnus = '357701';
update tutkintoversio set voimassa_loppupvm='2014-12-24' where tutkintotunnus = '355204';
update nayttotutkinto set nimi_sv='Puunkorjuun erikoisammattitutkinto', nimi_fi='Puunkorjuun erikoisammattitutkinto' where tutkintotunnus = '367304';

update tutkintoversio set voimassa_loppupvm='2014-12-24' where tutkintotunnus = '384102';
update tutkintoversio set voimassa_loppupvm='2011-02-28' where tutkintotunnus = '364104';
update tutkintoversio set voimassa_loppupvm='2014-12-24' where tutkintotunnus = '355403';
update tutkintoversio set voimassa_loppupvm='2014-12-24' where tutkintotunnus = '355205';
update tutkintoversio set voimassa_loppupvm='2014-12-24' where tutkintotunnus = '367305';
update tutkintoversio set voimassa_loppupvm='2005-03-31' where tutkintotunnus = '358406';
update tutkintoversio set voimassa_loppupvm='2014-12-24' where tutkintotunnus = '334107';
update nayttotutkinto set nimi_fi='Kiinteistönvälitysalan ammattitutkinto' where tutkintotunnus = '334118';

update tutkintoversio set voimassa_loppupvm='2014-12-24' where tutkintotunnus = '364303';
update tutkintoversio set voimassa_loppupvm='2014-12-24' where tutkintotunnus = '357703';
update tutkintoversio set voimassa_loppupvm='2005-03-31' where tutkintotunnus = '355404';
update tutkintoversio set voimassa_loppupvm='2014-12-24' where tutkintotunnus = '355206';
update tutkintoversio set voimassa_loppupvm='2014-12-24' where tutkintotunnus = '367306';
update tutkintoversio set voimassa_loppupvm='2005-03-31' where tutkintotunnus = '358407';
update tutkintoversio set voimassa_loppupvm='2014-12-24' where tutkintotunnus = '384104';
update tutkintoversio set voimassa_loppupvm='2014-12-24' where tutkintotunnus = '354701';
update nayttotutkinto set nimi_sv='Luonnontuotealan erikoisammattitutkinto' where tutkintotunnus = '367901';

update tutkintoversio set voimassa_loppupvm='2014-12-24' where tutkintotunnus = '374116';
update tutkintoversio set voimassa_loppupvm='2009-12-31' where tutkintotunnus = '354305';
update tutkintoversio set voimassa_loppupvm='2014-12-24' where tutkintotunnus = '357704';
update tutkintoversio set voimassa_loppupvm='2014-12-24' where tutkintotunnus = '355405';
update tutkintoversio set voimassa_loppupvm='2014-12-24' where tutkintotunnus = '355207';
update tutkintoversio set voimassa_loppupvm='2014-12-24' where tutkintotunnus = '358507';
update tutkintoversio set voimassa_loppupvm='2014-12-24' where tutkintotunnus = '384105';
update tutkintoversio set voimassa_loppupvm='2014-12-24' where tutkintotunnus = '354306';
update tutkintoversio set voimassa_loppupvm='2014-12-24' where tutkintotunnus = '355406';
update tutkintoversio set voimassa_loppupvm='2003-12-31' where tutkintotunnus = '354703';
update tutkintoversio set voimassa_loppupvm='2014-12-24' where tutkintotunnus = '364306';
update tutkintoversio set voimassa_loppupvm='2014-12-24' where tutkintotunnus = '357706';
update tutkintoversio set voimassa_loppupvm='2014-12-24' where tutkintotunnus = '354208';
update tutkintoversio set voimassa_loppupvm='2014-12-24' where tutkintotunnus = '384404';
update tutkintoversio set voimassa_loppupvm='2014-12-24' where tutkintotunnus = '384107';
update tutkintoversio set voimassa_loppupvm='2014-12-24' where tutkintotunnus = '354704';
update nayttotutkinto set nimi_fi='Sähköteollisuuden ammattitutkinto' where tutkintotunnus = '354407';

update tutkintoversio set voimassa_loppupvm='2014-12-24' where tutkintotunnus = '354308';
update tutkintoversio set voimassa_loppupvm='2005-03-31' where tutkintotunnus = '355408';
update tutkintoversio set voimassa_loppupvm='2005-03-31' where tutkintotunnus = '355409';
update tutkintoversio set voimassa_loppupvm='2014-12-24' where tutkintotunnus = '364903';
update tutkintoversio set voimassa_loppupvm='2014-12-24' where tutkintotunnus = '354706';
update tutkintoversio set voimassa_loppupvm='2014-12-24' where tutkintotunnus = '364309';
update tutkintoversio set voimassa_loppupvm='2014-12-24' where tutkintotunnus = '354707';
update nayttotutkinto set nimi_sv='Grundexamen i teknisk planering', nimi_fi='Teknisen suunnittelun perustutkinto' where tutkintotunnus = '352903';

update tutkintoversio set voimassa_loppupvm='2005-03-31' where tutkintotunnus = '327120';
update tutkintoversio set voimassa_loppupvm='2005-03-31' where tutkintotunnus = '327121';
update tutkintoversio set voimassa_loppupvm='2014-12-24' where tutkintotunnus = '327112';
update tutkintoversio set voimassa_loppupvm='2014-12-24' where tutkintotunnus = '327123';
update tutkintoversio set voimassa_loppupvm='2005-03-31' where tutkintotunnus = '324121';
update tutkintoversio set voimassa_loppupvm='2014-12-24' where tutkintotunnus = '327113';
update tutkintoversio set voimassa_loppupvm='2005-03-31' where tutkintotunnus = '324122';
update tutkintoversio set voimassa_loppupvm='2014-12-24' where tutkintotunnus = '337103';
update tutkintoversio set voimassa_loppupvm='2014-12-24' where tutkintotunnus = '358201';
update tutkintoversio set voimassa_loppupvm='2014-12-24' where tutkintotunnus = '327501';
update tutkintoversio set voimassa_loppupvm='2014-12-24' where tutkintotunnus = '324113';
update tutkintoversio set voimassa_loppupvm='2014-12-24' where tutkintotunnus = '324124';
update nayttotutkinto set nimi_fi='Tekstiilialan erikoisammattitutkinto, tekstiili- ja vaatetustekniikka' where tutkintotunnus = '358411';

update tutkintoversio set voimassa_loppupvm='2014-12-24' where tutkintotunnus = '358202';
update tutkintoversio set voimassa_loppupvm='2014-12-24' where tutkintotunnus = '327502';
update tutkintoversio set voimassa_loppupvm='2003-12-31' where tutkintotunnus = '324114';
update tutkintoversio set voimassa_loppupvm='2014-12-24' where tutkintotunnus = '327106';
update tutkintoversio set voimassa_loppupvm='2014-12-24' where tutkintotunnus = '327117';
update tutkintoversio set voimassa_loppupvm='2009-12-31' where tutkintotunnus = '374120';
update tutkintoversio set voimassa_loppupvm='2014-12-24' where tutkintotunnus = '354210';
update tutkintoversio set voimassa_loppupvm='2014-12-24' where tutkintotunnus = '358401';
update tutkintoversio set voimassa_loppupvm='2014-12-24' where tutkintotunnus = '358203';
update tutkintoversio set voimassa_loppupvm='2014-12-24' where tutkintotunnus = '327601';
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('100320', 'Palvelun toteuttaminen', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('102751', 'Pahkatyö', '', '2013-12-17', '2199-01-01', 1, 1);
update tutkinnonosa set nimi_sv='Framställning av audiovisuell produktion', nimi_fi='Audiovisuaalisen tuotannon toteuttaminen' where osatunnus = '100001';
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('100364', 'Myynnin tukipalvelut', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('101464', 'Ilmanvaihtokoneen huolto', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('100375', 'Tuoteneuvonta', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('101596', 'Hyvinvoinnin, elämänhallinnan ja toimintakyvyn havainnointi ja arviointi', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('100386', 'Käytön tuki tieto- ja kirjastopalvelussa', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('104654', 'Hankintasopimusten laatiminen', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('103257', 'CNC-tekniikka', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('100431', 'Kestävällä tavalla toimiminen', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('100321', 'Asiakaspalvelu ja myyntityö', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('102752', 'Puutyö', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('100365', 'Talouspalvelut', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('101465', 'LVI-korjausrakentaminen', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('100376', 'Toiminnan kannattavuuden suunnittelu', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('101597', 'Yksilöllinen tuki ja hoito', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('100387', 'Verkkopalvelut tieto- ja kirjastopalvelussa', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('104655', 'Kustannussuunnittelu', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('104666', 'Hevostaidot', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('103258', '5-akselinen työstö', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('100322', 'Kulttuurin soveltaminen käsityöhön', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('102753', 'Luu- ja sarvityö', '', '2013-12-17', '2199-01-01', 1, 1);
update tutkinnonosa set nimi_fi='Tukea tarvitsevien lasten ja perheiden kohtaaminen ja ohjaus' where osatunnus = '100344';
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('100366', 'Kirjanpito', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('101466', 'LVI-suunnittelu', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('100377', 'Tilinpäätöskirjaukset ja yrityksen verotus', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('101598', 'Tasa-arvoinen vuorovaikutus ja kommunikointi', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('104656', 'Työturvallisuus ja ympäristönsuojelu', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('104667', 'Ratsastustaito', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('103259', 'CAM-työstöratojen valmistus', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('102743', 'Saamenkäsityökisällin kaikille yhteinen ammattitaito', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('100323', 'Ohjaustoiminta', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('102754', 'Jalometallityö', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('100367', 'Toimistopalvelut', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('101467', 'Taloteknisten komponenttien sähköistys', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('100378', 'Kansainvälisen kaupan laskutus ja reskontran hoito', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('101599', 'Arkielämän taitojen edistäminen ja oppimisen tukeminen', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('104657', 'Sopimustekniikan hallinta', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('104668', 'Ratsastuksen opettaminen ja valmentaminen', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('101600', 'Yhteistyö perheen, moniammatillisen työryhmän ja verkostojen kanssa', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('102744', 'Saamenpuku ja asusteet', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('100324', 'Palvelun tuotteistaminen', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('102755', 'Juurityö', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('100368', 'Palkanlaskenta', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('100379', 'Työvälineohjelmien käyttö', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('104658', 'Neuvotteluissa toimiminen', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('104669', 'Esimiehenä toimiminen', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('101601', 'Sosiokulttuurinen työ kehitysvamma-alalla', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('102745', 'Nahka- ja turkistyö', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('100325', 'Sisustustyö', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('102756', 'Työvälineet', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('100369', 'Tieto- ja kirjastopalvelu', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('101602', 'Yrittäjyys', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('100315', 'Asiakaslähtöinen valmistaminen', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('102746', 'Saamenpuvun asusteet', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('100326', 'Stailaus', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('102757', 'Kuljetusvälineet', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('100359', 'Asiakaspalvelu', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('101459', 'Kanava- ja laiteasennus', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('100316', 'Kulttuurilähtöinen valmistaminen', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('102747', 'Valinnainen nahka- tai turkistuote', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('100327', 'Taidekäsityö', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('102758', 'Koneneulonta', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('100317', 'Tuotteen suunnittelu', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('102748', 'Tiuhta- ja punontatyö', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('100328', 'Tilaustyön valmistaminen', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('102759', 'Nahan ompelu koneella', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('100318', 'Tuotteen valmistaminen', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('102749', 'Erityistekniikat', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('100329', 'Tuotekehitys', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('100319', 'Toteuttamisen suunnittelu', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('103071', 'Kemiallinen pesu', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('104193', 'Ihonhoidon asiantuntijana toimiminen', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('104194', 'Kauneudenhoitoalan asiantuntijana toimiminen', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('104195', 'Aromaterapian ja eteeristen öljyjen käyttäminen ihonhoidossa', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('103260', 'Mallinnus', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('104196', 'Lymfahoitomenetelmän käyttäminen ihonhoidossa', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('103261', 'CAD-suunnittelu', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('104197', 'Jalkojen hyvinvoinnin tukeminen ja ennaltaehkäiseva jalkahoito kauneudenhoitoalalla', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('100380', 'Yhdistyksen ja asunto-osakeyhtiön asiakirjojen hoitaminen', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('104670', 'Yritystoiminta', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('103262', 'Muotinvalmistustekniikka', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('104198', 'Kauneudenhoitoalan palvelujen tuottaminen ja kehittäminen', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('100370', 'Tiedottaminen sekä kirjastonkäytön ja tiedonhaun opastus', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('101470', 'IV-koneen huolto', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('100381', 'Verkkopalvelujen tuottaminen ja ylläpito', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('103252', 'Työelämän yleistaidot', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('103263', 'Erityismallitekniikka', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('100360', 'Kaupan palvelu ja myynti', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('101460', 'Kanavaosien valmistus', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('100371', 'Aineiston hankinta ja kokoelman ylläpito', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('100382', 'Rahoituspalvelut', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('104650', 'Työmaan johtaminen', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('103253', 'Valumallialan perustaidot', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('103264', 'Nopeat mallinvalmistustekniikat (pikamallit)', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('100361', 'Asiakkuuksien hoito ja myyntityö', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('101461', 'Ilmanvaihtojärjestelmien korjaus ja huolto', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('100372', 'Aineiston tallentaminen, kuvailu ja esittely', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('101472', 'Sisäilmastomittaukset', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('100383', 'Asiakasryhmien kirjastopalvelut', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('104651', 'Työmaan laadunvarmistus', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('103254', 'Puumallit ja keernalaatikot', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('103265', 'Mittaus ja laadunvalvonta', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('102760', 'Korukivityö', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('100362', 'Visuaalinen myyntityö', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('101462', 'Huoltopalvelujen tuottaminen ja palvelutilanteen hallinta', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('100373', 'Markkinointiviestinnän toimenpiteiden suunnittelu ja toteutus', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('100384', 'Erikoiskirjastopalvelut', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('104652', 'Työmaan aikataulusuunnittelu ja -valvonta', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('103255', 'Muovimallit ja keernalaatikot', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('103266', 'Yrittäjyys', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('102750', 'Kudontatyö', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('100330', 'Tuotteen valmistaminen käsityönä', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('102761', 'Yrittäjyys', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('100363', 'Kansainvälisen kaupan tukipalvelut', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('101463', 'Ilmanvaihtojärjestelmien tasapainotus', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('100374', 'Sähköinen kaupankäynti', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('101595', 'Ammatillinen toiminta kehitysvamma-alalla', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('100385', 'Luettelointi ja sisällön kuvailu', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('104653', 'Tehtäväsuunnittelu', '', '2013-12-17', '2199-01-01', 1, 1);
insert into tutkinnonosa_tmp (osatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('103256', 'Valumallien suunnittelu', '', '2013-12-17', '2199-01-01', 1, 1);
update osaamisala set nimi_sv='Kompetensområdet för numeriskt styrd bearbetning inom snickeribranschen' where osaamisalatunnus = '2101';
update osaamisala set nimi_sv='Kompetensområdet för kompositmaterial inom snickeribranschen' where osaamisalatunnus = '2112';
update osaamisala set nimi_sv='Kompetensområdet för filmning och belysning' where osaamisalatunnus = '2002';
update osaamisala set nimi_sv='Kompetensområdet för restaurering av träkonstruktioner' where osaamisalatunnus = '2123';
update osaamisala set nimi_sv='Kompetensområdet för hundmassage' where osaamisalatunnus = '2013';
update osaamisala set nimi_sv='Kompetensområdet för fastighetsförvaltning av bostadsaktiebolag' where osaamisalatunnus = '2024';
update osaamisala set nimi_sv='Kompetensområdet för konstruktionsplanering' where osaamisalatunnus = '2145';
update osaamisala set nimi_sv='Kompetensområdet för lokalvård' where osaamisalatunnus = '2035';
update osaamisala set nimi_sv='Kompetensområdet för maskering' where osaamisalatunnus = '2156';
update osaamisala set nimi_sv='Kompetensområdet för reparation av skor (skomakarmästare)' where osaamisalatunnus = '3014';
update osaamisala set nimi_sv='Kompetensområdet för tillverkning av slangar och profilgods' where osaamisalatunnus = '2046';
update osaamisala set nimi_sv='Kompetensområdet för skötsel av produktionsdjur' where osaamisalatunnus = '2167';
update osaamisala set nimi_sv='Kompetensområdet för målning av nybyggnader' where osaamisalatunnus = '3025';
update osaamisala set nimi_sv='Kompetensområdet för emballage- och bruksglas' where osaamisalatunnus = '2057';
update osaamisala set nimi_sv='Kompetensområdet för metallbåtar 2' where osaamisalatunnus = '2178';
update osaamisala set nimi_sv='Kompetensområdet för inrednings- och möbeltextilier' where osaamisalatunnus = '3036';
update osaamisala set nimi_sv='Kompetensområdet för hantering och utnyttjande av material' where osaamisalatunnus = '2189';
update osaamisala set nimi_sv='Kompetensområdet för skeppsmäkleri och fartygsklarering' where osaamisalatunnus = '3047';
update osaamisala set nimi_sv='Kompetensområdet för asfalteringsarbeten' where osaamisalatunnus = '2079';
update osaamisala set nimi_sv='Kompetensområdet för avlopps- och rörhantering samt industriell rengöring' where osaamisalatunnus = '3058';
update osaamisala set nimi_sv='Kompetensområdet för CAD/CAM-teknik inom snickeribranschen' where osaamisalatunnus = '2102';
update osaamisala set nimi_sv='Kompetensområdet för tillverkning av betongprodukter' where osaamisalatunnus = '2113';
update osaamisala set nimi_sv='Kompetensområdet för mediearbete' where osaamisalatunnus = '2003';
update osaamisala set nimi_sv='Kompetensområdet för restaurering av fönsterkonstruktioner' where osaamisalatunnus = '2124';
update osaamisala set nimi_sv='Kompetensområdet för träning av djur' where osaamisalatunnus = '2014';
update osaamisala set nimi_sv='Kompetensområdet för fastighetsförvaltning av hyreshus' where osaamisalatunnus = '2025';
update osaamisala set nimi_sv='Kompetensområdet för elplanering' where osaamisalatunnus = '2146';
update osaamisala set nimi_sv='Kompetensområdet för stenbrytning' where osaamisalatunnus = '2036';
update osaamisala set nimi_sv='Kompetensområdet för dockteater' where osaamisalatunnus = '2157';
update osaamisala set nimi_sv='Kompetensområdet för industriell tillverkning av skor (skomästare)' where osaamisalatunnus = '3015';
update osaamisala set nimi_sv='Kompetensområdet för tillverkning av remmar och mattor' where osaamisalatunnus = '2047';
update osaamisala set nimi_sv='Kompetensområdet för ledning av avbytararbete' where osaamisalatunnus = '2168';
update osaamisala set nimi_sv='Kompetensområdet för målning av renoverade byggnader' where osaamisalatunnus = '3026';
update osaamisala set nimi_sv='Kompetensområdet för fiberglas och glasull' where osaamisalatunnus = '2058';
update osaamisala set nimi_sv='Kompetensområdet för tapetsering' where osaamisalatunnus = '2179';
update osaamisala set nimi_sv='Kompetensområdet för byggande av inredning' where osaamisalatunnus = '3037';
update osaamisala set nimi_sv='Kompetensområdet för konservering som privatföretagare' where osaamisalatunnus = '2069';
update osaamisala set nimi_sv='Kompetensområdet för permanenta formar' where osaamisalatunnus = '3048';
update osaamisala set nimi_sv='Kompetensområdet för miljöfostran' where osaamisalatunnus = '3059';
update osaamisala set nimi_sv='Kompetensområdet för flygplansmekanik' where osaamisalatunnus = '1530';
insert into osaamisala_tmp (osaamisalatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('1541', 'Puutuotteiden pintakäsittelyjen osaamisala', 'Kompetensområde för ytbehandling av trävaror', '2014-01-01', '2199-01-01', 1, 1);
update osaamisala set nimi_sv='Kompetensområdet för underhåll av verktyg och maskiner inom snickeribranschen' where osaamisalatunnus = '2103';
update osaamisala set nimi_sv='Kompetensområdet för tillverkning av keramiska produkter' where osaamisalatunnus = '2114';
update osaamisala set nimi_sv='Kompetensområdet för radioarbete' where osaamisalatunnus = '2004';
update osaamisala set nimi_sv='Kompetensområdet för restaurering av ytbehandlingar' where osaamisalatunnus = '2125';
update osaamisala set nimi_sv='Kompetensområdet för trimning av djur' where osaamisalatunnus = '2015';
update osaamisala set nimi_sv='Kompetensområdet för teknisk service i fastigheter' where osaamisalatunnus = '2026';
update osaamisala set nimi_sv='Kompetensområdet för VVS-planering' where osaamisalatunnus = '2147';
update osaamisala set nimi_sv='Kompetensområdet för förädling' where osaamisalatunnus = '2037';
update osaamisala set nimi_sv='Kompetensområdet för tillverkning av handgjorda och konstindustriella textilier' where osaamisalatunnus = '2158';
update osaamisala set nimi_sv='Kompetensområdet för hantverksmässig tillverkning av skor (skomästare)' where osaamisalatunnus = '3016';
update osaamisala set nimi_sv='Kompetensområdet för tillverkning av transportband' where osaamisalatunnus = '2048';
update osaamisala set nimi_sv='Kompetensområdet för lantbruksavbytarservice' where osaamisalatunnus = '2169';
update osaamisala set nimi_sv='Kompetensområdet för reparation och målning av fasader' where osaamisalatunnus = '3027';
update osaamisala set nimi_sv='Kompetensområdet för keramik' where osaamisalatunnus = '2059';
update osaamisala set nimi_sv='Kompetensområdet för behandling av inomhusytor' where osaamisalatunnus = '3038';
update osaamisala set nimi_sv='Kompetensområdet för engångsformar' where osaamisalatunnus = '3049';
update osaamisala set nimi_sv='Kompetensområdet för transportservice' where osaamisalatunnus = '1531';
update osaamisala set nimi_sv='Kompetensområdet för skötsel av maskinlinje inom snickeribranschen' where osaamisalatunnus = '2104';
update osaamisala set nimi_sv='Kompetensområdet för tillverkning av stenbaserad byggisolering' where osaamisalatunnus = '2115';
update osaamisala set nimi_sv='Kompetensområdet för produktion och projektstyrning' where osaamisalatunnus = '2005';
update osaamisala set nimi_sv='Kompetensområdet för programproducent' where osaamisalatunnus = '2126';
update osaamisala set nimi_sv='Kompetensområdet för travträning' where osaamisalatunnus = '2016';
update osaamisala set nimi_sv='Kompetensområdet för reparation av skor (skomakargesäll)' where osaamisalatunnus = '2027';
update osaamisala set nimi_sv='Kompetensområdet för planering av infrastruktur' where osaamisalatunnus = '2148';
update osaamisala set nimi_sv='Kompetensområdet för montering' where osaamisalatunnus = '2038';
update osaamisala set nimi_sv='Kompetensområdet för tillverkning av industriella textilier' where osaamisalatunnus = '2159';
update osaamisala set nimi_sv='Kompetensområdet för montering utomlands' where osaamisalatunnus = '3017';
update osaamisala set nimi_sv='Kompetensområdet för tillverkning av formgods' where osaamisalatunnus = '2049';
update osaamisala set nimi_sv='Kompetensområdet för strategisk ledning av marknadsföringskommunikation' where osaamisalatunnus = '3028';
update osaamisala set nimi_sv='Kompetensområdet för möbler' where osaamisalatunnus = '3039';
update osaamisala set nimi_sv='Kompetensområdet för mentalhälsoarbete och missbrukarvård' where osaamisalatunnus = '1510';
update osaamisala set nimi_sv='Kompetensområdet för lagerservice' where osaamisalatunnus = '1532';
update osaamisala set nimi_sv='Kompetensområdet för formpressning' where osaamisalatunnus = '2105';
update osaamisala set nimi_sv='Kompetensområdet för tillverkning av stenbaserade byggskivor' where osaamisalatunnus = '2116';
update osaamisala set nimi_sv='Kompetensområdet för nätverkskommunikation' where osaamisalatunnus = '2006';
update osaamisala set nimi_sv='Kompetensområdet för inspelningsproducent' where osaamisalatunnus = '2127';
update osaamisala set nimi_sv='Kompetensområdet för utbildning av ung-, dressyr- eller hopphästar' where osaamisalatunnus = '2017';
update osaamisala set nimi_sv='Kompetensområdet för industriell tillverkning av skor (skogesäll)' where osaamisalatunnus = '2028';
update osaamisala set nimi_sv='Kompetensområdet för bokförare' where osaamisalatunnus = '2149';
update osaamisala set nimi_sv='Kompetensområdet för hopsättningsmontering' where osaamisalatunnus = '2039';
update osaamisala set nimi_sv='Kompetensområdet för underhåll' where osaamisalatunnus = '3018';
update osaamisala set nimi_sv='Kompetensområdet för strategisk planering av marknadsföringskommunikation' where osaamisalatunnus = '3029';
update osaamisala set nimi_sv='Kompetensområdet för sjukvård och omsorg' where osaamisalatunnus = '1511';
update osaamisala set nimi_sv='Kompetensområdet för automationsteknik och underhåll' where osaamisalatunnus = '1522';
update osaamisala set nimi_sv='Kompetensområdet för industriell hopmontering och färdigställning av snickeriprodukter' where osaamisalatunnus = '2106';
update osaamisala set nimi_sv='Kompetensområdet för tillverkning av basmaterial' where osaamisalatunnus = '2117';
update osaamisala set nimi_sv='Kompetensområdet för ljudarbete' where osaamisalatunnus = '2007';
update osaamisala set nimi_sv='Kompetensområdet för ljudtekniker', nimi_fi='Ääniteknikko' where osaamisalatunnus = '2128';
update osaamisala set nimi_sv='Kompetensområdet för massage av häst' where osaamisalatunnus = '2018';
update osaamisala set nimi_sv='Kompetensområdet för byggande av knäppinstrument' where osaamisalatunnus = '2139';
update osaamisala set nimi_sv='Kompetensområdet för hantverksmässig tillverkning av skor (skogesäll)' where osaamisalatunnus = '2029';
update osaamisala set nimi_sv='Kompetensområdet för numeriskt styrt arbete' where osaamisalatunnus = '3019';
update osaamisala set nimi_sv='Kompetensområdet för fastighetsskötsel' where osaamisalatunnus = '1501';
update osaamisala set nimi_sv='Kompetensområdet för munhälsovård' where osaamisalatunnus = '1512';
update osaamisala set nimi_sv='Kompetensområdet för gjutningsteknik' where osaamisalatunnus = '1523';
insert into osaamisala_tmp (osaamisalatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('0104', 'Perustason ensihoidon osaamisala (kokeilu)', 'Kompetensområdet för förstavård på basnivå (försök)', '2014-12-16', '2199-01-01', 1, 1);
update osaamisala set nimi_sv='Kompetensområdet för montering av snickeriprodukter' where osaamisalatunnus = '2107';
update osaamisala set nimi_sv='Kompetensområdet för tillverkning av pressad byggsten' where osaamisalatunnus = '2118';
update osaamisala set nimi_sv='Kompetensområdet för djurskötsel på kliniker' where osaamisalatunnus = '2008';
update osaamisala set nimi_sv='Kompetensområdet för låtskrivare' where osaamisalatunnus = '2129';
update osaamisala set nimi_sv='Kompetensområdet för ventilationsplåtslagare' where osaamisalatunnus = '2019';
update osaamisala set nimi_sv='Kompetensområdet för handikappomsorg' where osaamisalatunnus = '1513';
update osaamisala set nimi_sv='Kompetensområdet för tillverkningsteknik' where osaamisalatunnus = '1524';
update osaamisala set nimi_sv='Kompetensområdet för skivindustrin' where osaamisalatunnus = '1535';
update osaamisala set nimi_sv='Kompetensområdet för teknisk planering och marknadsföring av möbler inom snickeribranschen' where osaamisalatunnus = '2108';
update osaamisala set nimi_sv='Kompetensområdet för skötsel av försöksdjur' where osaamisalatunnus = '2009';
update osaamisala set nimi_sv='Kompetensområdet för äldreomsorg' where osaamisalatunnus = '1514';
update osaamisala set nimi_sv='Kompetensområdet för bilplåtslagning' where osaamisalatunnus = '1525';
update osaamisala set nimi_sv='Kompetensområdet för industriell dörr- och fönstertillverkning' where osaamisalatunnus = '2109';
update osaamisala set nimi_sv='Kompetensområdet för jordbyggnad' where osaamisalatunnus = '1504';
update osaamisala set nimi_sv='Kompetensområdet för kundbetjäning och informationshantering' where osaamisalatunnus = '1515';
update osaamisala set nimi_sv='Kompetensområdet för billackering' where osaamisalatunnus = '1526';
update osaamisala set nimi_sv='Kompetensområdet för schaktningsmaskintransportKompetensområde för schaktningsmaskintransport' where osaamisalatunnus = '1505';
update osaamisala set nimi_sv='Kompetensområdet för bilförsäljning' where osaamisalatunnus = '1527';
update osaamisala set nimi_sv='Kompetensområdet för husbyggnad' where osaamisalatunnus = '1506';
update osaamisala set nimi_sv='Kompetensområdet för textilteknik' where osaamisalatunnus = '1517';
update osaamisala set nimi_sv='Kompetensområdet för bilteknik' where osaamisalatunnus = '1528';
update osaamisala set nimi_sv='Kompetensområdet för reservdelsförsäljning' where osaamisalatunnus = '1529';
update osaamisala set nimi_sv='Kompetensområdet för akutvård' where osaamisalatunnus = '1508';
update osaamisala set nimi_sv='Kompetensområdet för vård och fostran av barn och unga' where osaamisalatunnus = '1509';
update osaamisala set nimi_sv='Kompetensområdet för mätningar och kalibreringar i processer' where osaamisalatunnus = '2090';
update osaamisala set nimi_sv='Kompetensområdet för tillverkning och reparering av hästutrustning' where osaamisalatunnus = '2190';
update osaamisala set nimi_sv='Kompetensområdet för transporter inom infrastrukturområdet' where osaamisalatunnus = '2080';
update osaamisala set nimi_sv='Kompetensområdet för kalibreringar av kontrollanordningar för tunga fordon' where osaamisalatunnus = '2091';
update osaamisala set nimi_sv='Kompetensområdet för industriell tapetsering' where osaamisalatunnus = '2180';
update osaamisala set nimi_sv='Kompetensområdet för konservering inom museibranschen' where osaamisalatunnus = '2070';
update osaamisala set nimi_sv='Kompetensområdet för miljöarbeten' where osaamisalatunnus = '2081';
update osaamisala set nimi_sv='Kompetensområdet för kvalitets-, miljö- och säkerhetsledning' where osaamisalatunnus = '3060';
update osaamisala set nimi_sv='Kompetensområdet för utsläppsmätningar' where osaamisalatunnus = '2092';
update osaamisala set nimi_sv='Kompetensområdet för klövvård' where osaamisalatunnus = '2170';
update osaamisala set nimi_sv='Kompetensområdet för plast och motsvarande beläggnings- och beklädnadsmaterial' where osaamisalatunnus = '2060';
update osaamisala set nimi_sv='Kompetensområdet för vingårdsföretagare' where osaamisalatunnus = '2181';
update osaamisala set nimi_sv='Kompetensområdet för byggnadsmålning' where osaamisalatunnus = '2071';
update osaamisala set nimi_sv='Kompetensområdet för pressfotografering' where osaamisalatunnus = '3050';
update osaamisala set nimi_sv='Kompetensområdet för företagare inom landsbygdsturism' where osaamisalatunnus = '2082';
update osaamisala set nimi_sv='Kompetensområdet för rörmontör', nimi_fi='Putkiasentajan osaamisala' where osaamisalatunnus = '2093';
update osaamisala set nimi_sv='Kompetensområdet för allmänna informations- och bibliotekstjänster' where osaamisalatunnus = '2160';
update osaamisala set nimi_sv='Kompetensområdet för gummering' where osaamisalatunnus = '2050';
update osaamisala set nimi_sv='Kompetensområdet för assistent inom utrikeshandeln' where osaamisalatunnus = '2171';
update osaamisala set nimi_sv='Kompetensområdet för parkett och andra träytor' where osaamisalatunnus = '2061';
update osaamisala set nimi_sv='Kompetensområdet för vingårdsarbetare' where osaamisalatunnus = '2182';
update osaamisala set nimi_sv='Kompetensområdet för montering av fartygsinredning' where osaamisalatunnus = '3040';
update osaamisala set nimi_sv='Kompetensområdet för spacklingsarbeten' where osaamisalatunnus = '2072';
update osaamisala set nimi_sv='Kompetensområdet för naturfotografering' where osaamisalatunnus = '3051';
update osaamisala set nimi_sv='Kompetensområdet för arbetstagare vid företag inom landsbygdsturism' where osaamisalatunnus = '2083';
update osaamisala set nimi_sv='Kompetensområdet för VS-servicemontör' where osaamisalatunnus = '2094';
update osaamisala set nimi_sv='Kompetensområdet för löneberäknare' where osaamisalatunnus = '2150';
update osaamisala set nimi_sv='Kompetensområdet för montering utomlands' where osaamisalatunnus = '2040';
update osaamisala set nimi_sv='Kompetensområdet för informations- och bibliotekstjänster i lärmiljöer' where osaamisalatunnus = '2161';
update osaamisala set nimi_sv='Kompetensområdet för framställning av blandningar' where osaamisalatunnus = '2051';
update osaamisala set nimi_sv='Kompetensområdet för speditör' where osaamisalatunnus = '2172';
update osaamisala set nimi_sv='Kompetensområdet för snickarmästare' where osaamisalatunnus = '3030';
update osaamisala set nimi_sv='Kompetensområdet för keramiska plattor' where osaamisalatunnus = '2062';
update osaamisala set nimi_sv='Kompetensområdet för lantbruksföretagare' where osaamisalatunnus = '2183';
update osaamisala set nimi_sv='Kompetensområdet för byggande av knäppinstrument' where osaamisalatunnus = '3041';
update osaamisala set nimi_sv='Kompetensområdet för målning av fasader' where osaamisalatunnus = '2073';
update osaamisala set nimi_sv='Kompetensområdet för reklamfotografering' where osaamisalatunnus = '3052';
update osaamisala set nimi_sv='Kompetensområdet för marknadsföringsassistent' where osaamisalatunnus = '2084';
update osaamisala set nimi_sv='Kompetensområdet för träsnideri' where osaamisalatunnus = '2095';
update osaamisala set nimi_sv='Kompetensområdet för byggande av stråkinstrument' where osaamisalatunnus = '2140';
insert into osaamisala_tmp (osaamisalatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('2030', 'Kemian perusteollisuus', 'Kompetensområdet för kemisk basindustri', '2014-01-01', '2199-01-01', 1, 1);
update osaamisala set nimi_sv='Kompetensområdet för ekonomiassistent' where osaamisalatunnus = '2151';
update osaamisala set nimi_sv='Kompetensområdet för underhåll' where osaamisalatunnus = '2041';
update osaamisala set nimi_sv='Kompetensområdet för informations- och bibliotekstjänster för vetenskap och specialområden' where osaamisalatunnus = '2162';
update osaamisala set nimi_sv='Kompetensområdet för manuellt arbete' where osaamisalatunnus = '3020';
update osaamisala set nimi_sv='Kompetensområdet för installation och underhåll av kylanläggningar i butiker' where osaamisalatunnus = '2052';
update osaamisala set nimi_sv='Kompetensområdet för permanenta formar' where osaamisalatunnus = '2173';
update osaamisala set nimi_sv='Kompetensområdet för industrisnickarmästare' where osaamisalatunnus = '3031';
update osaamisala set nimi_sv='Kompetensområdet för plywoodindustri' where osaamisalatunnus = '2063';
update osaamisala set nimi_sv='Kompetensområdet för lantbruksarbetare' where osaamisalatunnus = '2184';
update osaamisala set nimi_sv='Kompetensområdet för byggande av stråkinstrument' where osaamisalatunnus = '3042';
update osaamisala set nimi_sv='Kompetensområdet för användning av anläggningsmaskiner' where osaamisalatunnus = '2074';
insert into osaamisala_tmp (osaamisalatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('2195', 'Hiilidioksidikylmälaitteiden asentamisen ja huoltamisen osaamisala', 'Kompetensområdet för installation och underhåll av kylanläggningar som innehåller koldioxid', '2014-01-01', '2199-01-01', 1, 1);
update osaamisala set nimi_sv='Kompetensområdet för porträttfotografering' where osaamisalatunnus = '3053';
update osaamisala set nimi_sv='Kompetensområdet för visuell marknadsförare' where osaamisalatunnus = '2085';
update osaamisala set nimi_sv='Kompetensområdet för intarsia' where osaamisalatunnus = '2096';
update osaamisala set nimi_sv='Kompetensområdet för beklädnad' where osaamisalatunnus = '2130';
update osaamisala set nimi_sv='Kompetensområdet för ventilationsmontör' where osaamisalatunnus = '2020';
update osaamisala set nimi_sv='Kompetensområdet för dragspelsbranschen' where osaamisalatunnus = '2141';
insert into osaamisala_tmp (osaamisalatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('2031', 'Kemian tuoteteollisuus', 'Kompetensområdet för kemisk produktindustri', '2014-01-01', '2199-01-01', 1, 1);
update osaamisala set nimi_sv='Kompetensområdet för produktionsteknik' where osaamisalatunnus = '2152';
update osaamisala set nimi_sv='Kompetensområdet för försäljning av fordon och/eller maskiner' where osaamisalatunnus = '3010';
update osaamisala set nimi_sv='Kompetensområdet för hushållsmaskiner' where osaamisalatunnus = '2042';
update osaamisala set nimi_sv='Kompetensområdet för tulltjänster' where osaamisalatunnus = '2163';
update osaamisala set nimi_sv='Kompetensområdet för plywoodindustri' where osaamisalatunnus = '3021';
update osaamisala set nimi_sv='Kompetensområdet för installation och underhåll av kylutrustning i luftkonditioneringssystem och stora värmepumpar' where osaamisalatunnus = '2053';
update osaamisala set nimi_sv='Kompetensområdet för engångsformar' where osaamisalatunnus = '2174';
update osaamisala set nimi_sv='Kompetensområdet för beklädnad' where osaamisalatunnus = '3032';
update osaamisala set nimi_sv='Kompetensområdet för spånskiveindustri' where osaamisalatunnus = '2064';
update osaamisala set nimi_sv='Kompetensområdet för planering och inledande av företagsverksamhet' where osaamisalatunnus = '2185';
update osaamisala set nimi_sv='Kompetensområdet för dragspelsbranschen' where osaamisalatunnus = '3043';
update osaamisala set nimi_sv='Kompetensområdet för nätanläggning' where osaamisalatunnus = '2075';
update osaamisala set nimi_sv='Kompetensområdet för tapetserarmästarens arbetsuppgifter' where osaamisalatunnus = '3054';
update osaamisala set nimi_sv='Kompetensområdet för produktion av programtjänster' where osaamisalatunnus = '2086';
update osaamisala set nimi_sv='Kompetensområdet för svarvning' where osaamisalatunnus = '2097';
update osaamisala set nimi_sv='Kompetensområdet för skötsel av djurparksdjur', nimi_fi='Eläintarhaeläinten hoitamisen osaamisala' where osaamisalatunnus = '2010';
update osaamisala set nimi_sv='Kompetensområdet för framställning av föremål' where osaamisalatunnus = '2131';
update osaamisala set nimi_sv='Kompetensområdet för annat område inom instrumentbyggande' where osaamisalatunnus = '2142';
insert into osaamisala_tmp (osaamisalatunnus, nimi_fi, nimi_sv, voimassa_alkupvm, voimassa_loppupvm, versio, koodistoversio) values ('2032', 'Lääke- ja bioteollisuus', 'Kompetensområdet för läkemedels- och bioindustri', '2014-01-01', '2199-01-01', 1, 1);
update osaamisala set nimi_sv='Kompetensområdet för föreställningsteknik' where osaamisalatunnus = '2153';
update osaamisala set nimi_sv='Kompetensområdet för försäljning av reservdelar och tillbehör' where osaamisalatunnus = '3011';
update osaamisala set nimi_sv='Kompetensområdet för storhushållsmaskiner' where osaamisalatunnus = '2043';
update osaamisala set nimi_sv='Kompetensområdet för tullkontrollverksamhet' where osaamisalatunnus = '2164';
update osaamisala set nimi_sv='Kompetensområdet för spånskiveindustri' where osaamisalatunnus = '3022';
update osaamisala set nimi_sv='Kompetensområdet för installation och underhåll av kylanläggningar i fordon' where osaamisalatunnus = '2054';
update osaamisala set nimi_sv='Kompetensområdet för båtar av armerad plast' where osaamisalatunnus = '2175';
update osaamisala set nimi_sv='Kompetensområdet för framställning av föremål' where osaamisalatunnus = '3033';
update osaamisala set nimi_sv='Kompetensområdet för utveckling av företagsverksamhet' where osaamisalatunnus = '2186';
update osaamisala set nimi_sv='Kompetensområdet för annat område inom instrumentbyggande' where osaamisalatunnus = '3044';
update osaamisala set nimi_sv='Kompetensområdet för grundläggning' where osaamisalatunnus = '2076';
update osaamisala set nimi_sv='Kompetensområdet för modelltapetserarmästarens arbetsuppgifter' where osaamisalatunnus = '3055';
update osaamisala set nimi_sv='Kompetensområdet för produktion av evenemang' where osaamisalatunnus = '2087';
update osaamisala set nimi_sv='Kompetensområdet för inramning av konst' where osaamisalatunnus = '2098';
update osaamisala set nimi_fi='Ulkoasun toteuttajan osaamisala' where osaamisalatunnus = '1593';
update osaamisala set nimi_sv='Kompetensområdet för tillverkning av trappor' where osaamisalatunnus = '2110';
update osaamisala set nimi_sv='Kompetensområdet för redigering' where osaamisalatunnus = '2000';
update osaamisala set nimi_sv='Kompetensområdet för arbetsmaskinsteknik' where osaamisalatunnus = '2121';
update osaamisala set nimi_sv='Kompetensområdet för djuraffärer' where osaamisalatunnus = '2011';
update osaamisala set nimi_sv='Kompetensområdet för smed' where osaamisalatunnus = '2132';
update osaamisala set nimi_sv='Kompetensområdet för maskinplanering' where osaamisalatunnus = '2143';
update osaamisala set nimi_sv='Kompetensområdet för VVS-underhåll' where osaamisalatunnus = '2033';
update osaamisala set nimi_sv='Kompetensområdet för scenografiframställning' where osaamisalatunnus = '2154';
update osaamisala set nimi_sv='Kompetensområdet för personbilsteknik' where osaamisalatunnus = '3012';
update osaamisala set nimi_sv='Kompetensområdet för tillverkning av däck' where osaamisalatunnus = '2044';
update osaamisala set nimi_sv='Kompetensområdet för produktionsdjursföretagande' where osaamisalatunnus = '2165';
update osaamisala set nimi_sv='Kompetensområdet för produktionsplanering inom persontrafik' where osaamisalatunnus = '3023';
update osaamisala set nimi_sv='Kompetensområdet för installation och underhåll av industriella kylanläggningar' where osaamisalatunnus = '2055';
update osaamisala set nimi_sv='Kompetensområdet för träbåtar' where osaamisalatunnus = '2176';
update osaamisala set nimi_sv='Kompetensområdet för smedsmästare' where osaamisalatunnus = '3034';
update osaamisala set nimi_sv='Kompetensområdet för transport av avfall och farliga ämnen' where osaamisalatunnus = '2187';
update osaamisala set nimi_sv='Kompetensområdet för internationell marknadsföring' where osaamisalatunnus = '3045';
update osaamisala set nimi_sv='Kompetensområdet för berganläggning' where osaamisalatunnus = '2077';
update osaamisala set nimi_sv='Kompetensområdet för vattenförsörjning' where osaamisalatunnus = '3056';
update osaamisala set nimi_sv='Kompetensområdet för verkstadsmätningar' where osaamisalatunnus = '2088';
update osaamisala set nimi_sv='Kompetensområdet för reparation och restaurering av snickeriprodukter' where osaamisalatunnus = '2099';
update osaamisala set nimi_sv='Kompetensområdet för tillverkning av en snickeriprodukt' where osaamisalatunnus = '2100';
update osaamisala set nimi_sv='Kompetensområdet för tillverkning av parkett' where osaamisalatunnus = '2111';
update osaamisala set nimi_sv='Kompetensområdet för publikationsgrafik' where osaamisalatunnus = '2001';
update osaamisala set nimi_sv='Kompetensområdet för restaurering av stenkonstruktioner' where osaamisalatunnus = '2122';
update osaamisala set nimi_sv='Kompetensområdet för djurpensionat' where osaamisalatunnus = '2012';
update osaamisala set nimi_sv='Kompetensområdet för knivsmed' where osaamisalatunnus = '2133';
update osaamisala set nimi_sv='Kompetensområdet för arkitektplanering' where osaamisalatunnus = '2144';
update osaamisala set voimassa_loppupvm='2014-10-02', nimi_sv='Kompetensområdet för fastighetsskötsel' where osaamisalatunnus = '2034';
update osaamisala set nimi_sv='Kompetensområdet för dräktframställning' where osaamisalatunnus = '2155';
update osaamisala set nimi_sv='Kompetensområdet för lastbils- och bussteknik' where osaamisalatunnus = '3013';
update osaamisala set nimi_sv='Kompetensområdet för tillverkning av gummiskodon' where osaamisalatunnus = '2045';
update osaamisala set nimi_sv='Kompetensområdet för förmansarbete på en produktionsdjursgård' where osaamisalatunnus = '2166';
update osaamisala set nimi_sv='Kompetensområdet för produktionsplanering inom godstrafik' where osaamisalatunnus = '3024';
update osaamisala set nimi_sv='Kompetensområdet för planglas', nimi_fi='Tasolasi' where osaamisalatunnus = '2056';
update osaamisala set nimi_sv='Kompetensområdet för metallbåtar 1' where osaamisalatunnus = '2177';
update osaamisala set nimi_sv='Kompetensområdet för knivsmedsmästare' where osaamisalatunnus = '3035';
update osaamisala set nimi_sv='Kompetensområdet för miljötjänster för fastigheter och industrin' where osaamisalatunnus = '2188';
update osaamisala set nimi_sv='Kompetensområdet för internationell inköpsverksamhet' where osaamisalatunnus = '3046';
update osaamisala set nimi_sv='Kompetensområdet för underhåll av trafikområden' where osaamisalatunnus = '2078';
update osaamisala set nimi_sv='Kompetensområdet för avfallshantering' where osaamisalatunnus = '3057';
update osaamisala set nimi_sv='Kompetensområdet för kalibrering av verkstadsmätinstrument' where osaamisalatunnus = '2089';
insert into tutkinto_ja_tutkinnonosa_tmp (tutkintotunnus, osatunnus, jarjestysnumero) values ('327118','104752',3), ('327118','104750',1), ('327118','104751',2);
insert into tutkinto_ja_tutkinnonosa_tmp (tutkintotunnus, osatunnus, jarjestysnumero) values ('331100','100366',8), ('331100','100367',9), ('331100','100382',24), ('331100','100385',27), ('331100','100381',23), ('331100','100380',22), ('331100','100383',25), ('331100','100384',26), ('331100','100376',18), ('331100','100377',19), ('331100','100375',17), ('331100','100379',21), ('331100','100360',2), ('331100','100361',3), ('331100','101040',31), ('331100','100378',20), ('331100','101039',30), ('331100','101041',32), ('331100','100362',4), ('331100','100363',5), ('331100','100387',29), ('331100','100386',28), ('331100','100372',14), ('331100','100359',1), ('331100','100371',13), ('331100','100373',15), ('331100','100365',7), ('331100','100368',10), ('331100','100369',11), ('331100','100364',6), ('331100','100370',12), ('331100','100374',16);
insert into tutkinto_ja_tutkinnonosa_tmp (tutkintotunnus, osatunnus, jarjestysnumero) values ('367103','104670',5), ('367103','104669',4), ('367103','104666',1), ('367103','104668',3), ('367103','104667',2);
insert into tutkinto_ja_tutkinnonosa_tmp (tutkintotunnus, osatunnus, jarjestysnumero) values ('358204','104657',8), ('358204','104651',2), ('358204','104650',1), ('358204','104652',3), ('358204','104653',4), ('358204','104655',6), ('358204','104658',9), ('358204','104656',7), ('358204','104654',5);
insert into tutkinto_ja_tutkinnonosa_tmp (tutkintotunnus, osatunnus, jarjestysnumero) values ('387300','104194',2), ('387300','104193',1), ('387300','104195',3), ('387300','104196',4), ('387300','104198',6), ('387300','104197',5);
insert into tutkinto_ja_tutkinnonosa_tmp (tutkintotunnus, osatunnus, jarjestysnumero) values ('374122','101595',1), ('374122','101600',6), ('374122','101601',7), ('374122','101597',3), ('374122','101596',2), ('374122','101602',8), ('374122','101599',5), ('374122','101598',4);
insert into tutkinto_ja_tutkinnonosa_tmp (tutkintotunnus, osatunnus, jarjestysnumero) values ('354201','101466',8), ('354201','101467',9), ('354201','101461',3), ('354201','101460',2), ('354201','101462',4), ('354201','101463',5), ('354201','101459',1), ('354201','101464',6), ('354201','101465',7);
insert into tutkinto_ja_tutkinnonosa_tmp (tutkintotunnus, osatunnus, jarjestysnumero) values ('354212','101467',8), ('354212','101462',2), ('354212','101470',3), ('354212','101472',5), ('354212','101463',4), ('354212','101465',6), ('354212','101466',7), ('354212','100208',1);
insert into tutkinto_ja_tutkinnonosa_tmp (tutkintotunnus, osatunnus, jarjestysnumero) values ('324117','102755',13), ('324117','102753',11), ('324117','102751',9), ('324117','102757',15), ('324117','102749',7), ('324117','102748',6), ('324117','102758',16), ('324117','102754',12), ('324117','102759',17), ('324117','102744',2), ('324117','102745',3), ('324117','102750',8), ('324117','102747',5), ('324117','102743',1), ('324117','102746',4), ('324117','102761',19), ('324117','102760',18), ('324117','102756',14), ('324117','102752',10);
insert into tutkinto_ja_tutkinnonosa_tmp (tutkintotunnus, osatunnus, jarjestysnumero) values ('324129','102853',8), ('324129','102849',4), ('324129','102846',1), ('324129','102852',7), ('324129','102850',5), ('324129','102847',2), ('324129','102848',3);
insert into tutkinto_ja_tutkinnonosa_tmp (tutkintotunnus, osatunnus, jarjestysnumero) values ('384113','103071',2);
insert into tutkinto_ja_tutkinnonosa_tmp (tutkintotunnus, osatunnus, jarjestysnumero) values ('357603','104555',9), ('357603','104557',11), ('357603','104558',12), ('357603','104553',7), ('357603','104554',8), ('357603','104556',10);
insert into tutkinto_ja_tutkinnonosa_tmp (tutkintotunnus, osatunnus, jarjestysnumero) values ('358505','104580',3), ('358505','104581',4), ('358505','104583',6), ('358505','104582',5), ('358505','104578',1), ('358505','104579',2);
insert into tutkinto_ja_tutkinnonosa_tmp (tutkintotunnus, osatunnus, jarjestysnumero) values ('354403','101779',12), ('354403','101775',8), ('354403','101777',10), ('354403','101776',9), ('354403','101778',11), ('354403','101774',7);
insert into tutkinto_ja_tutkinnonosa_tmp (tutkintotunnus, osatunnus, jarjestysnumero) values ('354603','102369',21), ('354603','102360',12);
insert into tutkinto_ja_tutkinnonosa_tmp (tutkintotunnus, osatunnus, jarjestysnumero) values ('355902','103400',15), ('355902','103404',19), ('355902','103410',25), ('355902','103391',6), ('355902','103405',20), ('355902','103401',16), ('355902','103407',22), ('355902','103390',5), ('355902','103406',21), ('355902','103402',17), ('355902','103388',3), ('355902','103386',1), ('355902','103387',2), ('355902','103392',7), ('355902','103396',11), ('355902','103408',23), ('355902','103393',8), ('355902','103395',10), ('355902','103397',12), ('355902','103399',14), ('355902','103409',24), ('355902','103398',13), ('355902','103394',9), ('355902','103389',4);
insert into tutkinto_ja_tutkinnonosa_tmp (tutkintotunnus, osatunnus, jarjestysnumero) values ('354109','103259',8), ('354109','103253',2), ('354109','103254',3), ('354109','103252',1), ('354109','103255',4), ('354109','103256',5), ('354109','103265',14), ('354109','103261',10), ('354109','103262',11), ('354109','103264',13), ('354109','103260',9), ('354109','103266',15), ('354109','103257',6), ('354109','103258',7), ('354109','103263',12);
insert into tutkinto_ja_tutkinnonosa_tmp (tutkintotunnus, osatunnus, jarjestysnumero) values ('355904','102853',8), ('355904','102849',4), ('355904','102846',1), ('355904','102851',6), ('355904','102852',7), ('355904','102850',5), ('355904','102847',2), ('355904','102848',3);
insert into tutkinto_ja_tutkinnonosa_tmp (tutkintotunnus, osatunnus, jarjestysnumero) values ('355906','103171',2), ('355906','103170',1), ('355906','103172',3), ('355906','103173',4);
insert into tutkinto_ja_tutkinnonosa_tmp (tutkintotunnus, osatunnus, jarjestysnumero) values ('361902','100431',1);
insert into tutkinto_ja_tutkinnonosa_tmp (tutkintotunnus, osatunnus, jarjestysnumero) values ('327101','104012',6), ('327101','104009',3), ('327101','104010',4), ('327101','104011',5), ('327101','104008',2), ('327101','104007',1);
insert into tutkinto_ja_tutkinnonosa_tmp (tutkintotunnus, osatunnus, jarjestysnumero) values ('324100','100323',9), ('324100','100327',13), ('324100','100322',8), ('324100','100326',12), ('324100','100318',4), ('324100','101039',17), ('324100','100319',5), ('324100','100328',14), ('324100','100329',15), ('324100','100316',2), ('324100','100315',1), ('324100','100324',10), ('324100','100320',6), ('324100','100321',7), ('324100','100325',11), ('324100','100330',16), ('324100','100317',3), ('324100','101041',19), ('324100','101040',18);
insert into tutkinto_ja_tutkinnonosa_tmp (tutkintotunnus, osatunnus, jarjestysnumero) values ('324111','102164',2), ('324111','102165',3), ('324111','102170',8), ('324111','102166',4), ('324111','102167',5), ('324111','102163',1), ('324111','102172',10), ('324111','102171',9), ('324111','102173',11), ('324111','102169',7), ('324111','102168',6), ('324111','102174',12);
insert into tutkinto_ja_tutkinnonosa_tmp (tutkintotunnus, osatunnus, jarjestysnumero) values ('337102','104200',2), ('337102','104199',1), ('337102','105042',3), ('337102','104202',4);
insert into tutkinto_ja_tutkinnonosa_tmp (tutkintotunnus, osatunnus, jarjestysnumero) values ('334113','103110',4), ('334113','103108',2), ('334113','103109',3), ('334113','103107',1);
update osaamisala set tutkintoversio = (select tutkintoversio_id from tutkintoversio where tutkintotunnus = '371101') where osaamisalatunnus = '1598';
update osaamisala set tutkintoversio = (select tutkintoversio_id from tutkintoversio where tutkintotunnus = '371101') where osaamisalatunnus = '1510';
update osaamisala set tutkintoversio = (select tutkintoversio_id from tutkintoversio where tutkintotunnus = '371101') where osaamisalatunnus = '1511';
update osaamisala set tutkintoversio = (select tutkintoversio_id from tutkintoversio where tutkintotunnus = '371101') where osaamisalatunnus = '1512';
update osaamisala set tutkintoversio = (select tutkintoversio_id from tutkintoversio where tutkintotunnus = '371101') where osaamisalatunnus = '1655';
update osaamisala set tutkintoversio = (select tutkintoversio_id from tutkintoversio where tutkintotunnus = '371101') where osaamisalatunnus = '0104';
update osaamisala set tutkintoversio = (select tutkintoversio_id from tutkintoversio where tutkintotunnus = '371101') where osaamisalatunnus = '1513';
update osaamisala set tutkintoversio = (select tutkintoversio_id from tutkintoversio where tutkintotunnus = '371101') where osaamisalatunnus = '1514';
update osaamisala set tutkintoversio = (select tutkintoversio_id from tutkintoversio where tutkintotunnus = '371101') where osaamisalatunnus = '1515';
update osaamisala set tutkintoversio = (select tutkintoversio_id from tutkintoversio where tutkintotunnus = '371101') where osaamisalatunnus = '1508';
update osaamisala set tutkintoversio = (select tutkintoversio_id from tutkintoversio where tutkintotunnus = '371101') where osaamisalatunnus = '1509';
update osaamisala set tutkintoversio = (select tutkintoversio_id from tutkintoversio where tutkintotunnus = '354601') where osaamisalatunnus = '2030';
update osaamisala set tutkintoversio = (select tutkintoversio_id from tutkintoversio where tutkintotunnus = '354601') where osaamisalatunnus = '2031';
update osaamisala set tutkintoversio = (select tutkintoversio_id from tutkintoversio where tutkintotunnus = '354601') where osaamisalatunnus = '2032';
update osaamisala set tutkintoversio = (select tutkintoversio_id from tutkintoversio where tutkintotunnus = '354205') where osaamisalatunnus = '2052';
update osaamisala set tutkintoversio = (select tutkintoversio_id from tutkintoversio where tutkintotunnus = '354205') where osaamisalatunnus = '2195';
update osaamisala set tutkintoversio = (select tutkintoversio_id from tutkintoversio where tutkintotunnus = '354205') where osaamisalatunnus = '2053';
update osaamisala set tutkintoversio = (select tutkintoversio_id from tutkintoversio where tutkintotunnus = '354205') where osaamisalatunnus = '2054';
update osaamisala set tutkintoversio = (select tutkintoversio_id from tutkintoversio where tutkintotunnus = '354205') where osaamisalatunnus = '2055';
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

drop table koulutusala_tmp;
drop table opintoala_tmp;
drop table nayttotutkinto_tmp;
drop table tutkintoversio_tmp;
drop table tutkinnonosa_tmp;
drop table tutkinto_ja_tutkinnonosa_tmp;
drop table osaamisala_tmp;