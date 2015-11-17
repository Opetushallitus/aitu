set session aitu.kayttaja='JARJESTELMA';

insert into tutkintotoimikunta (tkunta, diaarinumero, nimi_fi, nimi_sv, tilikoodi, toimiala, sahkoposti, kielisyys, toimikausi_id, toimikausi_alku, toimikausi_loppu)
values ('Gulo gulo', '6510/6502','Aavasaksalainen testitoimikunta', 'Aakkosissa Aavasaksa asettuu alkuun', 8086, 'Toimitustoimitus', 'trolololoo@solita.fi', 'fi',
  3,   to_date('2016-08-01', 'YYYY-MM-DD'),   to_date('2018-07-31', 'YYYY-MM-DD'));
  
insert into tutkintotoimikunta (tkunta, diaarinumero, nimi_fi, nimi_sv, tilikoodi, toimiala, sahkoposti, kielisyys, toimikausi_id, toimikausi_alku, toimikausi_loppu)
values ('Lynx lynx', '80186/8086','Lattaraudan taivutuksen testitoimikunta', 'Ruotsalaisen lattaraudan testitoimikunta', 8088, 'Rauta-ala', 'rauta-aika@solita.fi', 'sv',
  3,   to_date('2016-08-01', 'YYYY-MM-DD'),   to_date('2018-07-31', 'YYYY-MM-DD'));