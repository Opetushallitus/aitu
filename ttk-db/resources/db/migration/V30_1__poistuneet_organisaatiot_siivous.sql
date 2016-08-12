

create table poistettavat_koulutustoimijat as 
select o.organisaatiomuutos_id, o.koulutustoimija, o.paivamaara, o.tehty, k.nimi_fi, k.nimi_sv, k.lakkautuspaiva from organisaatiomuutos o
 inner join koulutustoimija k on o.koulutustoimija = k.ytunnus
  where o.tyyppi = 'poistunut'
  and k.voimassa = 'f'
 and not exists (select * from organisaatiomuutos oo where oo.koulutustoimija = o.koulutustoimija and oo.paivamaara > o.paivamaara)
 and not exists (select * from jarjestamissopimus j where j.koulutustoimija = o.koulutustoimija)
 order by o.koulutustoimija, o.paivamaara desc;
 
 delete from organisaatiomuutos where oppilaitos in (select oppilaitoskoodi from oppilaitos o
inner join poistettavat_koulutustoimijat pk on pk.koulutustoimija = o.koulutustoimija);

delete from organisaatiomuutos where toimipaikka in (select toimipaikkakoodi from toimipaikka t
  inner join oppilaitos o on o.oppilaitoskoodi = t.oppilaitos
  inner join poistettavat_koulutustoimijat pk on pk.koulutustoimija = o.koulutustoimija);

delete from toimipaikka where oppilaitos in (select oppilaitoskoodi from oppilaitos o
inner join poistettavat_koulutustoimijat pk on pk.koulutustoimija = o.koulutustoimija)
and toimipaikkakoodi != '0246101';


select * from sopimus_ja_tutkinto st
 inner join jarjestamissopimus j on j.jarjestamissopimusid = st.jarjestamissopimusid
 inner join sopimus_ja_tutkinto_ja_tutkinnonosa stt on stt.sopimus_ja_tutkinto = st.sopimus_ja_tutkinto_id
 where stt.toimipaikka in  (select toimipaikkakoodi from toimipaikka t
  inner join oppilaitos o on o.oppilaitoskoodi = t.oppilaitos
  inner join poistettavat_koulutustoimijat pk on pk.koulutustoimija = o.koulutustoimija)
 and j.voimassa = true; 
 
delete from sopimus_ja_tutkinto st
  where st.sopimus_ja_tutkinto_id in 
  (select sopimus_ja_tutkinto from sopimus_ja_tutkinto_ja_tutkinnonosa stt
   inner join toimipaikka t on t.toimipaikkakoodi = stt.toimipaikka
   inner join oppilaitos o on o.oppilaitoskoodi = t.oppilaitos
   inner join poistettavat_koulutustoimijat pk on pk.koulutustoimija = o.koulutustoimija)
   and not exists (select * from jarjestamissopimus j where j.jarjestamissopimusid = st.jarjestamissopimusid
   and j.voimassa = true);
 
select * from jarjestamissopimus j
 where j.oppilaitos in (select oppilaitoskoodi from oppilaitos o
 inner join  poistettavat_koulutustoimijat pk on pk.koulutustoimija = o.koulutustoimija);

delete from oppilaitos  where koulutustoimija in (select koulutustoimija from poistettavat_koulutustoimijat)
and oppilaitoskoodi not in('01482', '02461');

delete from organisaatiomuutos where koulutustoimija in  (select koulutustoimija from poistettavat_koulutustoimijat);

delete from koulutustoimija where ytunnus in (select koulutustoimija from poistettavat_koulutustoimijat)
and ytunnus not in ('0964971-1', '0243437-1');


drop table poistettavat_koulutustoimijat;
