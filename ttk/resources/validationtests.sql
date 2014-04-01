-- Kyselyt pitää kirjoittaa seuraavalla rakenteella:
-- 1. selectin hakemat columnit pitää luetella, * ei käy
-- 2. Kyselyä edeltävän rivin kommentti on kyselyn näkyvä otsikko
--
-- Lisäksi on jotain pieniä rajoituksia, esim. Oraclen with-syntaksi ei käy yms.

-- OPH-236 Jäsenyyden alku ja loppupäivien tarkastaminen suhteessa toimikunnan toimikauteen
select j.jasenyys_id, h.etunimi, h.sukunimi
  from jasenyys j
  inner join henkilo h on h.henkiloid = j.henkiloid
  where exists (
    select 1 from tutkintotoimikunta t
    where t.tkunta = j.toimikunta
      and (t.toimikausi_alku > j.alkupvm or t.toimikausi_loppu < j.loppupvm));
