-- luodaan uusi toimikausi tietokantaan. Tällä kertaa kaksivuotinen kausi kolmen vuoden sijaan.

insert into toimikausi (alkupvm, loppupvm, voimassa)
  values (to_date('01082016','DDMMYYYY'), to_date('31072018', 'DDMMYYYY'), false);