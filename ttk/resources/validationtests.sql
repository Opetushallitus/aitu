-- Kyselyt pitää kirjoittaa seuraavalla rakenteella:
-- 1. selectin hakemat columnit pitää luetella, * ei käy
-- 2. Kyselyä edeltävän rivin kommentti on kyselyn näkyvä otsikko
--
-- Lisäksi on jotain pieniä rajoituksia, esim. Oraclen with-syntaksi ei käy yms.
 
-- OPH-1357 oppilaitos ei ole voimassa, mutta sopimus on voimassa
select * from jarjestamissopimus j
  inner join oppilaitos o on j.tutkintotilaisuuksista_vastaava_oppilaitos = o.oppilaitoskoodi
  where o.voimassa = 'f' and j.voimassa ='t'
  order by sopimusnumero, tutkintotilaisuuksista_vastaava_oppilaitos;

-- OPH-236 Jäsenyyden alku ja loppupäivien tarkastaminen suhteessa toimikunnan toimikauteen
select j.jasenyys_id, h.etunimi, h.sukunimi
  from jasenyys j
  inner join henkilo h on h.henkiloid = j.henkiloid
  where exists (
    select 1 from tutkintotoimikunta t
    where t.tkunta = j.toimikunta
      and (t.toimikausi_alku > j.alkupvm or t.toimikausi_loppu < j.loppupvm));

-- OPH-764 Siirtymäaika on loppunut ennen tutkinnon voimassaolon päättymistä
select * from tutkintoversio where siirtymaajan_loppupvm < voimassa_loppupvm;

-- OPH-836 tutkintoja joita ei ole kohdistettu toimikunnalle
select nt.tutkintotunnus,nt.nimi_fi,nt.luotuaika, tv.voimassa_loppupvm, tv.hyvaksytty
  from nayttotutkinto nt 
  inner join tutkintoversio tv on tv.tutkintotunnus = nt.tutkintotunnus 
  where not exists (select 1 from toimikunta_ja_tutkinto tt where tt.tutkintotunnus = nt.tutkintotunnus)
  and tv.voimassa_loppupvm > CURRENT_DATE 
  and tv.hyvaksytty = 't';
 
-- OPH-1622 käyttäjän etunimi ja henkilön etunimi pitäisi olla samat loogisesti jos henkilö ja käyttäjä on kytketty toisiinsa.
select k.oid, k.uid, h.henkiloid, h.etunimi, h.sukunimi, k.etunimi, k.sukunimi from henkilo h
 inner join kayttaja k on h.kayttaja_oid = k.oid
 where h.etunimi != k.etunimi or h.sukunimi != k.sukunimi;
 
-- Henkilöitä, jotka eivät ole jäseniä toimikunnissa lainkaan. Näitä ei pitäisi olla tietokannassa.
--select * from henkilo h
-- where not exists (select * from jasenyys j where j.henkiloid = h.henkiloid) order by henkiloid;