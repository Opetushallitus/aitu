-- 1. Siirretään tutkinnot uuteen kuvitteelliseen opintoalaan
-- 2. poistetaan vanhan luokituksen mukaiset opintoalat ja koulutusalat
-- 3. .. ajetaan integraatio..
-- 4. tämän jälkeen kaikki muut tutkinnot ovat päivittyneet, paitsi tässä erikseen listatut muutamat, joille ei löydy
--    uudesta luokituksesta ainakaan vielä paikkaa.
-- 5. integraation jälkeen keksityn opintoalan voisi poistaa tietokannasta.
insert into opintoala(opintoala_tkkoodi, koulutusala_tkkoodi, selite_fi, voimassa_alkupvm, voimassa_loppupvm)
values ('007', '0', 'Keksitty opintoala ISCED-päivitystä varten', to_date('2017-01-01','YYYY-MM-DD'), to_date('2017-01-01','YYYY-MM-DD'));

update nayttotutkinto set opintoala = '007' where tutkintotunnus not in
('321603','377111','371113','354116','351108','354204','200003','010001','324503');

delete from opintoala where opintoala_tkkoodi not in ('505','703','502','599','801','706','204', '007');
delete from koulutusala k where not exists (select null from opintoala o where o.koulutusala_tkkoodi = k.koulutusala_tkkoodi);
