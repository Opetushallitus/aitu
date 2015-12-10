create temporary table muutos as select t.tutkinnonosa_id as vanha, (select max(tutkinnonosa_id) from tutkinnonosa t2 where t2.osatunnus = t.osatunnus) as uusi from tutkinnonosa t;
create temporary table uudet_st as select distinct sopimus_ja_tutkinto, toimipaikka, m.uusi as tutkinnonosa
                                   from sopimus_ja_tutkinto_ja_tutkinnonosa st
                                   join muutos m on st.tutkinnonosa = m.vanha;
delete from sopimus_ja_tutkinto_ja_tutkinnonosa;
insert into sopimus_ja_tutkinto_ja_tutkinnonosa (sopimus_ja_tutkinto, tutkinnonosa, toimipaikka) select sopimus_ja_tutkinto, tutkinnonosa, toimipaikka from uudet_st;
delete from tutkinto_ja_tutkinnonosa where tutkinnonosa not in (select uusi from muutos);
delete from tutkinnonosa where tutkinnonosa_id not in (select uusi from muutos);
drop table muutos;
drop table uudet_st;