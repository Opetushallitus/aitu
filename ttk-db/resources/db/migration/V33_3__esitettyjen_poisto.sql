-- Poistetaan sellaiset henkilöt, joita on esitetty toimikuntiin (toimikaudelle 1.8.2016 - 31.7.2018) ja jotka
-- eivät ole tulleet valituksi mihinkään toimikuntaan. 
select * from henkilo h  
inner join jasenyys j on j.henkiloid = h.henkiloid where status = 'esitetty' -- 112
and not exists (select * from jasenyys jj where jj.henkiloid = h.henkiloid and jj.status = 'nimitetty') -- 67
and h.kayttaja_oid is null; --67 kpl 


