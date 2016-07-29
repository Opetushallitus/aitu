-- kieli huomioitu, voimassaolo huomioitu
create table sopimuspaivitys as 
select distinct j.jarjestamissopimusid, j.sopimusnumero, j.toimikunta, j.sopijatoimikunta, wt.nimi_fi as vanha_nimi_fi, t.tkunta, t.diaarinumero, t.nimi_fi as uusi_nimi_fi, t.nimi_sv as uusi_nimi_sv, t.kielisyys, t.tilikoodi, st.kieli from jarjestamissopimus j
inner join sopimus_ja_tutkinto st on st.jarjestamissopimusid = j.jarjestamissopimusid
inner join tutkintoversio tv on tv.tutkintoversio_id = st.tutkintoversio
inner join toimikunta_ja_tutkinto tt on tt.tutkintotunnus = tv.tutkintotunnus
inner join tutkintotoimikunta t on t.tkunta = tt.toimikunta
inner join tutkintotoimikunta wt on wt.tkunta = j.sopijatoimikunta
  where j.voimassa = true
  and j.poistettu = false
  and t.toimikausi_id = 3
  and st.poistettu = false
  and j.jarjestamissopimusid > 0
  and ((t.kielisyys = st.kieli) or (t.kielisyys='2k') or (st.kieli is null))
  and tv.voimassa_alkupvm < to_date('2016-07-31','YYYY-MM-DD')
  and tv.voimassa_loppupvm > to_date('2016-07-31', 'YYYY-MM-DD')
and not exists (select 42 from sopimus_ja_tutkinto st2 
  inner join tutkintoversio tv2 on tv2.tutkintoversio_id = st2.tutkintoversio
  inner join toimikunta_ja_tutkinto tt2 on tt2.tutkintotunnus = tv2.tutkintotunnus
  inner join tutkintotoimikunta t2 on t2.tkunta = tt2.toimikunta
  where st2.jarjestamissopimusid = j.jarjestamissopimusid
  and t2.toimikausi_id = 3
  and tt2.toimikunta != tt.toimikunta
  and t2.kielisyys = t.kielisyys
  and tv2.tutkintotunnus != tv.tutkintotunnus
  and ((t2.kielisyys = st2.kieli) or (t2.kielisyys='2k'))
  and st2.kieli = st.kieli
  and st2.poistettu = false
  and tv2.voimassa_alkupvm < to_date('2016-07-31','YYYY-MM-DD')
  and tv2.voimassa_loppupvm > to_date('2016-07-31', 'YYYY-MM-DD')
  )
order by j.jarjestamissopimusid ;

update jarjestamissopimus j
set toimikunta = s.tkunta
from sopimuspaivitys s 
where j.jarjestamissopimusid = s.jarjestamissopimusid;