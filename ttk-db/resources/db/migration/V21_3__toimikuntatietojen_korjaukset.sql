-- kts. Jira: OPH-1677
-- Korjauksia perustietoihin, joita ei voi muokata järjestelmän käyttöliittymässä (tarkoituksellisesti)

update tutkintotoimikunta set nimi_fi = 'Elintarvikealan tutkintotoimikunta' where diaarinumero = '5/042/2015';
update tutkintotoimikunta set nimi_fi = 'Eläintenhoidon tutkintotoimikunta' where diaarinumero = '6/042/2015';

update tutkintotoimikunta set nimi_sv = 'Examenskommissionen inom naturbruk' where diaarinumero = '10/042/2015';

update tutkintotoimikunta set nimi_sv = 'Examenskommissionen för kyltekniska branschen' where diaarinumero = '32/042/2015';
update tutkintotoimikunta set nimi_sv = 'Examenskommissionen för lantmäteribranschen och teknisk  planering' where diaarinumero = '43/042/2015';

update tutkintotoimikunta set nimi_fi = 'Sosiaali- ja terveysalan tutkintotoimikunta' where diaarinumero = '70/042/2015';
update tutkintotoimikunta set nimi_sv = 'Yrittäjyysalan tutkintotoimikunta' where diaarinumero = '93/042/2015';

