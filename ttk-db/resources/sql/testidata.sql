set session aitu.kayttaja='JARJESTELMA';

insert into koulutusala(koulutusala_tkkoodi, selite_fi,selite_sv, voimassa_alkupvm)
  select '6', 'Kulttuuriala', 'Kultur', to_date('1997-01-01', 'YYYY-MM-DD')
  where not exists (select 1 from koulutusala where koulutusala_tkkoodi = '6');

insert into opintoala (opintoala_tkkoodi, koulutusala_tkkoodi, selite_fi, selite_sv, voimassa_alkupvm)
 select '202', '6', 'Viestintä ja informaatiotieteet', 'Mediekultur och informationsvetenskaper', to_date('1997-01-01', 'YYYY-MM-DD')
 where not exists (select 1 from opintoala where opintoala_tkkoodi = '202');

insert into opintoala (opintoala_tkkoodi, koulutusala_tkkoodi, selite_fi, selite_sv, voimassa_alkupvm)
 select '201', '6', 'Käsi- ja taideteollisuus', 'Hantverk och konstindustri', to_date('1997-01-01', 'YYYY-MM-DD')
 where not exists (select 1 from opintoala where opintoala_tkkoodi = '201');

insert into nayttotutkinto(tutkintotunnus, opintoala, nimi_fi, nimi_sv, tutkintotaso, tyyppi)
 select '324601', '202', 'Audiovisuaalisen viestinnän perustutkinto', 'Audiovisuaalisen viestinnän perustutkinto (sv)', 'ammattitutkinto', '02'
 where not exists (select 1 from nayttotutkinto where tutkintotunnus = '324601');

insert into nayttotutkinto(tutkintotunnus, opintoala, nimi_fi, nimi_sv, tutkintotaso, tyyppi)
 select '327128', '201', 'Käsityömestarin erikoisammattitutkinto', 'Käsityömestarin erikoisammattitutkinto (sv)', 'erikoisammattitutkinto', '02'
 where not exists (select 1 from nayttotutkinto where tutkintotunnus = '327128');
 
insert into tutkintoversio (tutkintoversio_id, tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm)
  values (-10000, '324601', 1, 1, '38/011/2014', true, to_date('2005-01-01', 'YYYY-MM-DD')),
         (-20000, '327128', 1, 1, '34/011/2010', true, to_date('2005-01-01', 'YYYY-MM-DD'));

update nayttotutkinto set uusin_versio_id = -10000 where tutkintotunnus = '324601';
update nayttotutkinto set uusin_versio_id = -20000 where tutkintotunnus = '327128';

insert into toimikunta_ja_tutkinto(toimikunta, tutkintotunnus)
  values ('Gulo gulo', '324601');

insert into jarjestamissopimus (jarjestamissopimusid, sopimusnumero, toimikunta, sopijatoimikunta, voimassa)
  values (-324601, '123456', 'Gulo gulo', 'Gulo gulo', true);

insert into sopimus_ja_tutkinto(jarjestamissopimusid, tutkintoversio)
  values (-324601, -10000);





