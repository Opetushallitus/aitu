set session aitu.kayttaja='JARJESTELMA';
create table tutkintotyyppi_tmp (
  tyyppi varchar(2),
  selite_fi text,
  selite_sv text
);

insert into tutkintotyyppi_tmp (tyyppi, selite_fi, selite_sv) values
('01','Yleissivistävä koulutus','Allmänbildande utbildning'),
('02','Ammatilliset perustutkinnot','Yrkesinriktade grundexamina'),
('03','Tutkintoon johtava ammatillinen lisäkoulutus','Yrkesinriktad tilläggsutbildning som leder till examen'),
('04','Ei tutkintoon johtava ammatillinen lisäkoulutus', 'Yrkesinriktad tilläggsutbildning som inte leder till examen'),
('06','Ammattikorkeakoulutus','Yrkeshögskoleutbildning'),
('07','Ammattikorkeakoulujen erikoistumisopinnot','Yrkeshögskolornas specialiseringsstudier'),
('08','Vapaan sivistystyön koulutus','Utbildning inom fritt bildningsarbete'),
('09','Muu ammatillinen koulutus','Övrig yrkesinriktad utbildning'),
('10','Muu koulutus','Övrig utbildning'),
('11','Kokeilu','Försök'),
('12','Ylempi ammattikorkeakoulututkinto','Högre yrkeshögskoleexaman'),
('13','Yliopistotutkinto','Universitetsexamen');

insert into tutkintotyyppi (tyyppi, selite_fi, selite_sv)
select tyyppi, selite_fi, selite_sv
from tutkintotyyppi_tmp
where not exists (select 1 from tutkintotyyppi where tutkintotyyppi.tyyppi = tutkintotyyppi_tmp.tyyppi);

drop table tutkintotyyppi_tmp;


alter table nayttotutkinto alter column tutkintotaso set not null;
alter table nayttotutkinto alter column tyyppi set not null;
