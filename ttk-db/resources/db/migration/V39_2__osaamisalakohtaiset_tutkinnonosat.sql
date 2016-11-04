create table osaamisala_ja_tutkinnonosa (
  osaamisala int not null references osaamisala(osaamisala_id),
  tutkinnonosa int not null references tutkinnonosa(tutkinnonosa_id),
  jarjestysnumero int not null,
  muutettu_kayttaja varchar(80) NOT NULL references kayttaja(oid),
  luotu_kayttaja varchar(80) NOT NULL references kayttaja(oid),
  muutettuaika timestamptz NOT NULL,
  luotuaika timestamptz NOT NULL,
  primary key (osaamisala, tutkinnonosa)
);

create trigger osaamisala_ja_tutkinnonosa_update before update on osaamisala_ja_tutkinnonosa for each row execute procedure update_stamp() ;
create trigger osaamisala_ja_tutkinnonosal_insert before insert on osaamisala_ja_tutkinnonosa for each row execute procedure update_created() ;
create trigger osaamisala_ja_tutkinnonosam_insert before insert on osaamisala_ja_tutkinnonosa for each row execute procedure update_stamp() ;
create trigger osaamisala_ja_tutkinnonosa_mu_update before update on osaamisala_ja_tutkinnonosa for each row execute procedure update_modifier() ;
create trigger osaamisala_ja_tutkinnonosa_cu_insert before insert on osaamisala_ja_tutkinnonosa for each row execute procedure update_creator() ;
create trigger osaamisala_ja_tutkinnonosa_mu_insert before insert on osaamisala_ja_tutkinnonosa for each row execute procedure update_modifier() ;

create temporary table osaamisala_tmp as
select (row_number() over (order by osaamisala_id)) as osaamisala_id, osaamisalatunnus, nimi_fi, nimi_sv, tutkintoversio
from osaamisala oa1 where oa1.osaamisala_id = (select max(osaamisala_id) from osaamisala oa2 where oa1.tutkintoversio = oa2.tutkintoversio and oa1.osaamisalatunnus = oa2.osaamisalatunnus);

update suorituskerta set tutkintoversio_id = uusin_versio_id from nayttotutkinto where nayttotutkinto.tutkintotunnus = suorituskerta.tutkinto and suorituskerta.tutkintoversio_id is null;

create temporary table suoritus_osaamisala_tmp as
  select suoritus_id, osaamisalatunnus, sk.tutkintoversio_id from suoritus s
  inner join osaamisala o on o.osaamisala_id = s.osaamisala
  inner join suorituskerta sk on sk.suorituskerta_id = s.suorituskerta;

update suoritus set osaamisala = null;

create temporary table suoritus_tutkinnonosa_tmp as
  select suoritus_id, osatunnus, sk.tutkintoversio_id from suoritus s
  inner join tutkinnonosa t on t.tutkinnonosa_id = s.tutkinnonosa
  inner join suorituskerta sk on sk.suorituskerta_id = s.suorituskerta;

alter table suoritus
  alter column tutkinnonosa drop not null;
  
update suoritus set tutkinnonosa = null;

create temporary table sopimus_ja_tutkinto_ja_osaamisala_tmp as
select distinct osaamisalatunnus, tutkintoversio, sopimus_ja_tutkinto, toimipaikka
from sopimus_ja_tutkinto_ja_osaamisala sto
join osaamisala oa on sto.osaamisala = oa.osaamisala_id;

alter table osaamisala
drop column versio,
drop column koodistoversio,
drop column nimi,
drop column kuvaus,
drop column voimassa_alkupvm,
drop column voimassa_loppupvm,
alter column osaamisalatunnus type varchar(10);

delete from sopimus_ja_tutkinto_ja_osaamisala;
delete from osaamisala;

insert into osaamisala(osaamisala_id, osaamisalatunnus, nimi_fi, nimi_sv, tutkintoversio)
select osaamisala_id, osaamisalatunnus, nimi_fi, nimi_sv, tutkintoversio
from osaamisala_tmp;

update suoritus set osaamisala = osaamisala_id from osaamisala 
inner join suoritus_osaamisala_tmp on suoritus_osaamisala_tmp.osaamisalatunnus = osaamisala.osaamisalatunnus and
                                      suoritus_osaamisala_tmp.tutkintoversio_id = osaamisala.tutkintoversio
where suoritus.suoritus_id = suoritus_osaamisala_tmp.suoritus_id;

drop table suoritus_osaamisala_tmp;

insert into sopimus_ja_tutkinto_ja_osaamisala (sopimus_ja_tutkinto, osaamisala, toimipaikka)
select sopimus_ja_tutkinto, osaamisala_id as osaamisala, toimipaikka
from sopimus_ja_tutkinto_ja_osaamisala_tmp sto
join osaamisala oa on sto.osaamisalatunnus = oa.osaamisalatunnus and sto.tutkintoversio = oa.tutkintoversio;

select setval('osaamisala_id_seq',(select max(osaamisala_id) from osaamisala));

drop table sopimus_ja_tutkinto_ja_osaamisala_tmp;
drop table osaamisala_tmp;

create temporary table tutkinnonosa_tmp as
select (row_number() over (order by tutkinnonosa_id, tutkintoversio)) as tutkinnonosa_id, osatunnus, nimi_fi, nimi_sv, tutkintoversio, jarjestysnumero
from tutkinnonosa t
join tutkinto_ja_tutkinnonosa tt on t.tutkinnonosa_id = tt.tutkinnonosa
where not (osatunnus = '102582' and jarjestysnumero = 8); -- tapaus jossa sama osa on kahdella järjestysnumerolla

create temporary table sopimus_ja_tutkinto_ja_tutkinnonosa_tmp as
select distinct osatunnus, tutkintoversio, sopimus_ja_tutkinto, toimipaikka
from sopimus_ja_tutkinto_ja_tutkinnonosa stt
join sopimus_ja_tutkinto st on stt.sopimus_ja_tutkinto = st.sopimus_ja_tutkinto_id
join tutkinnonosa t on stt.tutkinnonosa = t.tutkinnonosa_id;

alter table tutkinnonosa
drop column versio,
drop column koodistoversio,
drop column nimi,
drop column kuvaus,
drop column voimassa_alkupvm,
drop column voimassa_loppupvm,
add column tutkintoversio int references tutkintoversio(tutkintoversio_id),
add column jarjestysnumero int;

delete from sopimus_ja_tutkinto_ja_tutkinnonosa;
drop table tutkinto_ja_tutkinnonosa;
delete from tutkinnonosa;

insert into tutkinnonosa(tutkinnonosa_id, osatunnus, nimi_fi, nimi_sv, tutkintoversio, jarjestysnumero)
select tutkinnonosa_id, osatunnus, nimi_fi, nimi_sv, tutkintoversio, min(jarjestysnumero)
from tutkinnonosa_tmp
group by tutkinnonosa_id, osatunnus, nimi_fi, nimi_sv, tutkintoversio;

insert into sopimus_ja_tutkinto_ja_tutkinnonosa (sopimus_ja_tutkinto, tutkinnonosa, toimipaikka)
select sopimus_ja_tutkinto, tutkinnonosa_id as tutkinnonosa, toimipaikka
from sopimus_ja_tutkinto_ja_tutkinnonosa_tmp stt
join tutkinnonosa t on stt.osatunnus = t.osatunnus and stt.tutkintoversio = t.tutkintoversio;

select setval('tutkinnonosa_id_seq',(select max(tutkinnonosa_id) from tutkinnonosa));

drop table tutkinnonosa_tmp;
drop table sopimus_ja_tutkinto_ja_tutkinnonosa_tmp;


update suoritus set tutkinnonosa = tutkinnonosa_id from tutkinnonosa
 inner join suoritus_tutkinnonosa_tmp on suoritus_tutkinnonosa_tmp.osatunnus = tutkinnonosa.osatunnus and
                                          suoritus_tutkinnonosa_tmp.tutkintoversio_id = tutkinnonosa.tutkintoversio;

drop table suoritus_tutkinnonosa_tmp;

alter table suoritus
  alter column tutkinnonosa set not null;



insert into eperusteet_log default values;
