-- luodaan uusi toimikausi tietokantaan. Tällä kertaa kaksivuotinen kausi kolmen vuoden sijaan.

set session aitu.kayttaja='JARJESTELMA';

insert into toimikausi (alkupvm, loppupvm, voimassa)
  values (to_date('01082016','DDMMYYYY'), to_date('31072018', 'DDMMYYYY'), false);