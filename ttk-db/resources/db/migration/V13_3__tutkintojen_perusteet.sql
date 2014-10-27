-- kts. https://issues.solita.fi/browse/OPH-962
-- SQL-lauseet generoitu Clojurella, kts. ttk-db.tut-peruste

set session aitu.kayttaja='INTEGRAATIO';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '73/011/1996' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '324104' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='324104' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '324104') where tutkintotunnus = '324104';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '19/011/2005' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '327503' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='327503' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '327503') where tutkintotunnus = '327503';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '36/011/2011' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '324115' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='324115' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '324115') where tutkintotunnus = '324115';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '22/011/2005' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '337106' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='337106' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '337106') where tutkintotunnus = '337106';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '34/011/2008' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '327107' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='327107' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '327107') where tutkintotunnus = '327107';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '2/011/2007' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '327118' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='327118' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '327118') where tutkintotunnus = '327118';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '44/011/2009' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '374121' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='374121' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '374121') where tutkintotunnus = '374121';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '40/011/2004' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '387101' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='387101' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '387101') where tutkintotunnus = '387101';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '21/011/2008' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '354310' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='354310' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '354310') where tutkintotunnus = '354310';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '4/011/2002' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '355410' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='355410' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '355410') where tutkintotunnus = '355410';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '61/011/2000' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '354211' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='354211' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '354211') where tutkintotunnus = '354211';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '46/011/2005' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '354101' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='354101' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '354101') where tutkintotunnus = '354101';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '15/011/2010' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '039996' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='039996' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '039996') where tutkintotunnus = '039996';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '26/011/2006' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '355201' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='355201' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '355201') where tutkintotunnus = '355201';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '25/011/97' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '367301' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='367301' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '367301') where tutkintotunnus = '367301';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '13/011/2002' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '354112' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='354112' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '354112') where tutkintotunnus = '354112';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '60/011/2010' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '377102' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='377102' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '377102') where tutkintotunnus = '377102';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '8/011/2006' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '355212' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='355212' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '355212') where tutkintotunnus = '355212';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '25/011/2009' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '321101' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='321101' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '321101') where tutkintotunnus = '321101';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '9/011/2008' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '357401' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='357401' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '357401') where tutkintotunnus = '357401';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '11/011/2008' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '355102' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='355102' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '355102') where tutkintotunnus = '355102';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '54/011/2002' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '357302' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='357302' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '357302') where tutkintotunnus = '357302';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '28/011/2005' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '367103' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='367103' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '367103') where tutkintotunnus = '367103';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '47/011/2005' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '357203' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='357203' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '357203') where tutkintotunnus = '357203';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '8/011/2009' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '334103' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='334103' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '334103') where tutkintotunnus = '334103';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '10/011/2012' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '334114' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='334114' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '334114') where tutkintotunnus = '334114';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '6/011/2006' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '357104' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='357104' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '357104') where tutkintotunnus = '357104';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '43/011/2010' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '358204' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='358204' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '358204') where tutkintotunnus = '358204';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '67/011/1996' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '324105' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='324105' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '324105') where tutkintotunnus = '324105';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '6/011/2013' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '324116' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='324116' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '324116') where tutkintotunnus = '324116';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '4/011/2008' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '337107' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='337107' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '337107') where tutkintotunnus = '337107';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '14/011/2011' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '384110' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='384110' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '384110') where tutkintotunnus = '384110';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '33/011/2007' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '327119' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='327119' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '327119') where tutkintotunnus = '327119';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '30/011/2011' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '374111' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='374111' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '374111') where tutkintotunnus = '374111';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '49/011/2009' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '374122' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='374122' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '374122') where tutkintotunnus = '374122';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '47/011/2006' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '387201' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='387201' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '387201') where tutkintotunnus = '387201';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '34/011/2012' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '364101' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='364101' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '364101') where tutkintotunnus = '364101';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '44/011/2005' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '387102' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='387102' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '387102') where tutkintotunnus = '387102';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '18/011/2008' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '354311' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='354311' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '354311') where tutkintotunnus = '354311';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '38/011/2004' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '355411' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='355411' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '355411') where tutkintotunnus = '355411';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '29/011/2012' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '354201' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='354201' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '354201') where tutkintotunnus = '354201';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '34/011/2009' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '331101' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='331101' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '331101') where tutkintotunnus = '331101';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '28/011/2012' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '355301' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='355301' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '355301') where tutkintotunnus = '355301';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '27/011/2012' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '354212' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='354212' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '354212') where tutkintotunnus = '354212';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, ' 17/011/2013' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '354102' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='354102' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '354102') where tutkintotunnus = '354102';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '31/011/2006' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '357501' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='357501' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '357501') where tutkintotunnus = '357501';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '17/011/2004' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '367302' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='367302' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '367302') where tutkintotunnus = '367302';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '55/011/2010' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '354113' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='354113' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '354113') where tutkintotunnus = '354113';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '46/011/2012' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '377103' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='377103' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '377103') where tutkintotunnus = '377103';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '51/011/2001' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '357402' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='357402' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '357402') where tutkintotunnus = '357402';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '41/011/2012' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '355103' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='355103' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '355103') where tutkintotunnus = '355103';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '51/011/2000' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '367203' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='367203' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '367203') where tutkintotunnus = '367203';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '14/011/2005' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '358502' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='358502' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '358502') where tutkintotunnus = '358502';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '12/011/2009' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '357303' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='357303' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '357303') where tutkintotunnus = '357303';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '21/011/2005' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '367104' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='367104' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '367104') where tutkintotunnus = '367104';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '19/011/2013' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '357204' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='357204' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '357204') where tutkintotunnus = '357204';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '52/011/2009' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '334104' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='334104' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '334104') where tutkintotunnus = '334104';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '17/011/2005' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '357105' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='357105' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '357105') where tutkintotunnus = '357105';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '2/011/2011' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '358205' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='358205' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '358205') where tutkintotunnus = '358205';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '27/011/2006' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '324117' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='324117' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '324117') where tutkintotunnus = '324117';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '19/011/2007' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '324128' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='324128' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '324128') where tutkintotunnus = '324128';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '39/011/2001' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '337108' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='337108' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '337108') where tutkintotunnus = '337108';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '29/011/2006' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '384111' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='384111' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '384111') where tutkintotunnus = '384111';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '32/011/2014' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '327109' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='327109' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '327109') where tutkintotunnus = '327109';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '10/011/2013' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '364201' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='364201' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '364201') where tutkintotunnus = '364201';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '23/011/2011' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '374123' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='374123' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '374123') where tutkintotunnus = '374123';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '20/011/2009' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '352101' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='352101' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '352101') where tutkintotunnus = '352101';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '50/011/2006' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '387202' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='387202' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '387202') where tutkintotunnus = '387202';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '16/011/2010' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '341101' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='341101' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '341101') where tutkintotunnus = '341101';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '7/011/2009' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '354301' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='354301' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '354301') where tutkintotunnus = '354301';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '31/011/2012' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '364102' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='364102' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '364102') where tutkintotunnus = '364102';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '23/011/2012' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '387103' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='387103' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '387103') where tutkintotunnus = '387103';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '48/011/2006' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '354312' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='354312' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '354312') where tutkintotunnus = '354312';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '22/011/2011' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '355412' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='355412' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '355412') where tutkintotunnus = '355412';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '30/011/2008' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '354202' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='354202' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '354202') where tutkintotunnus = '354202';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '13/011/2010' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '321301' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='321301' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '321301') where tutkintotunnus = '321301';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '24/011/2003' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '357601' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='357601' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '357601') where tutkintotunnus = '357601';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '46/011/2003' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '354103' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='354103' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '354103') where tutkintotunnus = '354103';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '7/011/2008' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '039998' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='039998' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '039998') where tutkintotunnus = '039998';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '31/011/2005' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '357502' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='357502' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '357502') where tutkintotunnus = '357502';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '16/011/2005' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '354114' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='354114' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '354114') where tutkintotunnus = '354114';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '43/011/2009' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '377104' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='377104' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '377104') where tutkintotunnus = '377104';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '41/011/2005' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '324601' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='324601' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '324601') where tutkintotunnus = '324601';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '5/011/2011' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '357403' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='357403' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '357403') where tutkintotunnus = '357403';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '36/011/2013' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '355104' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='355104' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '355104') where tutkintotunnus = '355104';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '18/011/2005' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '324502' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='324502' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '324502') where tutkintotunnus = '324502';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '45/011/2004' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '358503' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='358503' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '358503') where tutkintotunnus = '358503';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '38/011/2001' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '357304' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='357304' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '357304') where tutkintotunnus = '357304';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '24/011/2002' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '357205' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='357205' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '357205') where tutkintotunnus = '357205';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '11/011/2012' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '334105' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='334105' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '334105') where tutkintotunnus = '334105';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '9/011/2009' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '334116' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='334116' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '334116') where tutkintotunnus = '334116';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '10/011/2007' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '357106' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='357106' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '357106') where tutkintotunnus = '357106';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '28/011/2013' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '358206' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='358206' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '358206') where tutkintotunnus = '358206';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '35/011/2008' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '324107' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='324107' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '324107') where tutkintotunnus = '324107';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '38/011/2013' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '324129' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='324129' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '324129') where tutkintotunnus = '324129';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '19/011/2010' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '371110' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='371110' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '371110') where tutkintotunnus = '371110';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '29/011/2007' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '337109' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='337109' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '337109') where tutkintotunnus = '337109';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '13/011/2011' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '384101' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='384101' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '384101') where tutkintotunnus = '384101';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '12/011/2011' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '384112' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='384112' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '384112') where tutkintotunnus = '384112';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '39/011/2010' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '351101' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='351101' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '351101') where tutkintotunnus = '351101';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '40/011/2009' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '387401' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='387401' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '387401') where tutkintotunnus = '387401';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '35/011/2009' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '352201' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='352201' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '352201') where tutkintotunnus = '352201';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '7/011/2013' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '364301' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='364301' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '364301') where tutkintotunnus = '364301';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '29/011/2011' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '374113' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='374113' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '374113') where tutkintotunnus = '374113';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '15/011/2005' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '364202' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='364202' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '364202') where tutkintotunnus = '364202';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '63/011/2010' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '374124' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='374124' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '374124') where tutkintotunnus = '374124';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '3/011/2013' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '354401' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='354401' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '354401') where tutkintotunnus = '354401';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '14/011/2013' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '387203' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='387203' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '387203') where tutkintotunnus = '387203';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '48/011/2009' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '355501' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='355501' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '355501') where tutkintotunnus = '355501';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '52/011/2002' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '354302' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='354302' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '354302') where tutkintotunnus = '354302';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '25/011/2006' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '364103' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='364103' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '364103') where tutkintotunnus = '364103';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '36/011/2005' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '387104' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='387104' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '387104') where tutkintotunnus = '387104';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '21/011/2014' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '354313' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='354313' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '354313') where tutkintotunnus = '354313';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '5/011/2005' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '355413' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='355413' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '355413') where tutkintotunnus = '355413';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '18/011/2013' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '354203' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='354203' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '354203') where tutkintotunnus = '354203';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '13/011/2007' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '357602' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='357602' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '357602') where tutkintotunnus = '357602';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '49/011/2012' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '354104' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='354104' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '354104') where tutkintotunnus = '354104';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '9/011/2010' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '039999' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='039999' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '039999') where tutkintotunnus = '039999';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '97/011/2014' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '357503' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='357503' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '357503') where tutkintotunnus = '357503';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '8/011/2013' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '367304' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='367304' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '367304') where tutkintotunnus = '367304';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '56/011/2010' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '354115' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='354115' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '354115') where tutkintotunnus = '354115';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '16/011/2013' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '377105' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='377105' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '377105') where tutkintotunnus = '377105';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '53/011/2010' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '357404' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='357404' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '357404') where tutkintotunnus = '357404';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '33/011/2013' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '355105' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='355105' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '355105') where tutkintotunnus = '355105';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '46/011/2004' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '358504' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='358504' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '358504') where tutkintotunnus = '358504';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '40/011/2001' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '357305' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='357305' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '357305') where tutkintotunnus = '357305';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '29/011/2002' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '358405' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='358405' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '358405') where tutkintotunnus = '358405';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '20/011/2011' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '357206' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='357206' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '357206') where tutkintotunnus = '357206';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '19/011/2012' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '334106' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='334106' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '334106') where tutkintotunnus = '334106';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '48/011/2010' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '334117' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='334117' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '334117') where tutkintotunnus = '334117';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '11/011/2006' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '357107' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='357107' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '357107') where tutkintotunnus = '357107';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '1/011/2007' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '324119' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='324119' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '324119') where tutkintotunnus = '324119';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '42/011/2006' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '384201' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='384201' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '384201') where tutkintotunnus = '384201';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '29/011/2009' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '361101' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='361101' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '361101') where tutkintotunnus = '361101';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '21/011/2003' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '387501' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='387501' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '387501') where tutkintotunnus = '387501';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '33/011/2014' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '384113' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='384113' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '384113') where tutkintotunnus = '384113';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '36/011/2008' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '354710' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='354710' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '354710') where tutkintotunnus = '354710';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '5/011/2010' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '352301' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='352301' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '352301') where tutkintotunnus = '352301';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '19/011/2008' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '364401' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='364401' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '364401') where tutkintotunnus = '364401';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '46/011/2006' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '364302' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='364302' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '364302') where tutkintotunnus = '364302';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '32/011/2006' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '354501' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='354501' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '354501') where tutkintotunnus = '354501';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '31/011/2011' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '387303' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='387303' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '387303') where tutkintotunnus = '387303';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '15/011/2013' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '374114' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='374114' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '374114') where tutkintotunnus = '374114';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '19/011/2006' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '364203' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='364203' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '364203') where tutkintotunnus = '364203';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '50/011/2001' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '354402' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='354402' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '354402') where tutkintotunnus = '354402';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '29/011/2010' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '321501' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='321501' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '321501') where tutkintotunnus = '321501';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '11/011/2003' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '357801' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='357801' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '357801') where tutkintotunnus = '357801';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '26/011/2002' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '355502' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='355502' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '355502') where tutkintotunnus = '355502';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '5/011/2009' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '358901' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='358901' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '358901') where tutkintotunnus = '358901';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '37/011/2005' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '387105' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='387105' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '387105') where tutkintotunnus = '387105';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '33/011/2008' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '357702' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='357702' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '357702') where tutkintotunnus = '357702';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '37/011/2012' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '354314' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='354314' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '354314') where tutkintotunnus = '354314';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '30/011/2013' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '357603' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='357603' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '357603') where tutkintotunnus = '357603';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '20/011/2007' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '354105' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='354105' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '354105') where tutkintotunnus = '354105';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '30/011/2010' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '321204' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='321204' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '321204') where tutkintotunnus = '321204';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '32/011/2011' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '377106' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='377106' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '377106') where tutkintotunnus = '377106';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '37/011/2013' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '357405' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='357405' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '357405') where tutkintotunnus = '357405';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '26/011/2013' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '355106' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='355106' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '355106') where tutkintotunnus = '355106';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '46/011/2004' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '358505' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='358505' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '358505') where tutkintotunnus = '358505';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '20/011/2005' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '357306' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='357306' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '357306') where tutkintotunnus = '357306';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '7/011/2014' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '357207' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='357207' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '357207') where tutkintotunnus = '357207';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '3/011/2011' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '334118' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='334118' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '334118') where tutkintotunnus = '334118';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '5/011/2006' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '357108' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='357108' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '357108') where tutkintotunnus = '357108';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '31/011/2014' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '324109' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='324109' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '324109') where tutkintotunnus = '324109';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '17/011/2010' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '371101' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='371101' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '371101') where tutkintotunnus = '371101';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '12/011/2012' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '384301' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='384301' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '384301') where tutkintotunnus = '384301';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '2/011/2010' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '361201' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='361201' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '361201') where tutkintotunnus = '361201';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '49/011/2006' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '384202' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='384202' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '384202') where tutkintotunnus = '384202';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '30/011/2009' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '351301' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='351301' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '351301') where tutkintotunnus = '351301';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '6/011/2009' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '352401' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='352401' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '352401') where tutkintotunnus = '352401';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '12/011/2013' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '384103' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='384103' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '384103') where tutkintotunnus = '384103';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '11/011/2013' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '384114' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='384114' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '384114') where tutkintotunnus = '384114';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '54/011/2009' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '364402' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='364402' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '364402') where tutkintotunnus = '364402';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '31/011/2002' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '354601' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='354601' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '354601') where tutkintotunnus = '354601';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '30/011/2005' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '354502' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='354502' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '354502') where tutkintotunnus = '354502';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '42/011/2012' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '387304' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='387304' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '387304') where tutkintotunnus = '387304';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '2/011/2013' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '374115' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='374115' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '374115') where tutkintotunnus = '374115';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '11/011/2007' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '364204' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='364204' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '364204') where tutkintotunnus = '364204';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '98/011/2014' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '354403' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='354403' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '354403') where tutkintotunnus = '354403';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '14/011/2003' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '357802' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='357802' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '357802') where tutkintotunnus = '357802';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '46/011/2009' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '355503' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='355503' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '355503') where tutkintotunnus = '355503';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '35/011/2007' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '358902' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='358902' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '358902') where tutkintotunnus = '358902';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '24/011/2007' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '364105' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='364105' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '364105') where tutkintotunnus = '364105';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '34/011/2014' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '387106' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='387106' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '387106') where tutkintotunnus = '387106';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '21/011/2011' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '354205' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='354205' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '354205') where tutkintotunnus = '354205';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '3/011/2005' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '354106' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='354106' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '354106') where tutkintotunnus = '354106';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '2/011/2006' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '377107' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='377107' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '377107') where tutkintotunnus = '377107';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '59/011/2010' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '355107' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='355107' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '355107') where tutkintotunnus = '355107';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '47/011/2004' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '358506' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='358506' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '358506') where tutkintotunnus = '358506';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '22/011/2014' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '357307' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='357307' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '357307') where tutkintotunnus = '357307';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '65/011/2002' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '334108' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='334108' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '334108') where tutkintotunnus = '334108';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '55/011/2009' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '334119' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='334119' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '334119') where tutkintotunnus = '334119';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '57/011/2010' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '357109' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='357109' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '357109') where tutkintotunnus = '357109';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '3/011/2010' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '381112' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='381112' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '381112') where tutkintotunnus = '381112';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '38/011/2005' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '384401' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='384401' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '384401') where tutkintotunnus = '384401';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '36/011/2009' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '361301' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='361301' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '361301') where tutkintotunnus = '361301';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '8/011/2012' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '384203' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='384203' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '384203') where tutkintotunnus = '384203';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '35/011/2010' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '351203' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='351203' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '351203') where tutkintotunnus = '351203';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '121/011/1995' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '367901' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='367901' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '367901') where tutkintotunnus = '367901';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '26/011/2005' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '364403' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='364403' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '364403') where tutkintotunnus = '364403';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '34/011/2005' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '354602' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='354602' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '354602') where tutkintotunnus = '354602';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '3/011/2008' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '364304' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='364304' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '364304') where tutkintotunnus = '364304';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '96/011/2014' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '354503' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='354503' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '354503') where tutkintotunnus = '354503';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '32/011/2010' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '321602' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='321602' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '321602') where tutkintotunnus = '321602';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '13/011/2012' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '364205' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='364205' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '364205') where tutkintotunnus = '364205';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '28/011/2007' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '354404' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='354404' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '354404') where tutkintotunnus = '354404';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '25/011/2003' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '357803' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='357803' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '357803') where tutkintotunnus = '357803';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '45/011/2009' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '355504' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='355504' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '355504') where tutkintotunnus = '355504';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '14/011/2014' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '364106' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='364106' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '364106') where tutkintotunnus = '364106';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '25/011/2002' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '354206' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='354206' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '354206') where tutkintotunnus = '354206';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '27/011/2007' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '354107' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='354107' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '354107') where tutkintotunnus = '354107';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '28/011/2010' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '377108' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='377108' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '377108') where tutkintotunnus = '377108';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '25/011/2004' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '355108' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='355108' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '355108') where tutkintotunnus = '355108';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '5/011/2002' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '358408' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='358408' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '358408') where tutkintotunnus = '358408';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '30/011/2006' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '334109' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='334109' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '334109') where tutkintotunnus = '334109';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '44/011/2006' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '384501' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='384501' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '384501') where tutkintotunnus = '384501';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '25/011/2010' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '381113' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='381113' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '381113') where tutkintotunnus = '381113';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '22/011/2010' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '361401' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='361401' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '361401') where tutkintotunnus = '361401';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '16/011/2008' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '384402' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='384402' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '384402') where tutkintotunnus = '384402';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '9/011/2003' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '354801' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='354801' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '354801') where tutkintotunnus = '354801';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '2/011/2014' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '384204' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='384204' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '384204') where tutkintotunnus = '384204';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '24/011/2009' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '361104' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='361104' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '361104') where tutkintotunnus = '361104';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '2/011/2008' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '355901' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='355901' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '355901') where tutkintotunnus = '355901';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '28/011/2008' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '354702' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='354702' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '354702') where tutkintotunnus = '354702';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '7/011/2010' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '351204' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='351204' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '351204') where tutkintotunnus = '351204';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '53/011/2009' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '367902' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='367902' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '367902') where tutkintotunnus = '367902';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '29/011/2013' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '354603' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='354603' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '354603') where tutkintotunnus = '354603';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '43/011/2012' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '364305' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='364305' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '364305') where tutkintotunnus = '364305';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '61/011/2010' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '374117' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='374117' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '374117') where tutkintotunnus = '374117';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '54/011/2010' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '354405' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='354405' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '354405') where tutkintotunnus = '354405';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '47/011/2009' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '355505' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='355505' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '355505') where tutkintotunnus = '355505';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '54/011/2012' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '358904' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='358904' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '358904') where tutkintotunnus = '358904';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '32/011/2012' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '364107' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='364107' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '364107') where tutkintotunnus = '364107';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '25/011/2008' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '357705' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='357705' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '357705') where tutkintotunnus = '357705';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '26/011/2012' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '354207' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='354207' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '354207') where tutkintotunnus = '354207';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '9/011/2006' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '354108' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='354108' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '354108') where tutkintotunnus = '354108';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '54/011/2004' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '355208' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='355208' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '355208') where tutkintotunnus = '355208';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '18/011/2012' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '377109' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='377109' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '377109') where tutkintotunnus = '377109';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '23/011/2010' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '355109' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='355109' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '355109') where tutkintotunnus = '355109';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '41/011/2006' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '358508' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='358508' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '358508') where tutkintotunnus = '358508';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '49/011/2004' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '358409' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='358409' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '358409') where tutkintotunnus = '358409';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '26/011/2010' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '381410' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='381410' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '381410') where tutkintotunnus = '381410';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '17/011/2009' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '381201' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='381201' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '381201') where tutkintotunnus = '381201';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '15/011/2008' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '384403' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='384403' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '384403') where tutkintotunnus = '384403';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '22/011/2009' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '351502' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='351502' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '351502') where tutkintotunnus = '351502';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '12/011/2003' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '354802' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='354802' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '354802') where tutkintotunnus = '354802';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '31/011/2010' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '352503' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='352503' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '352503') where tutkintotunnus = '352503';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '15/011/2006' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '384205' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='384205' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '384205') where tutkintotunnus = '384205';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '57/011/2009' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '321901' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='321901' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '321901') where tutkintotunnus = '321901';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '12/011/2007' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '355902' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='355902' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '355902') where tutkintotunnus = '355902';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '39/011/2004' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '384106' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='384106' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '384106') where tutkintotunnus = '384106';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '16/011/2011' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '367903' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='367903' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '367903') where tutkintotunnus = '367903';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '13/011/2007' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '354604' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='354604' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '354604') where tutkintotunnus = '354604';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '27/011/2010' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '351106' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='351106' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '351106') where tutkintotunnus = '351106';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '7/011/2012' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '374118' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='374118' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '374118') where tutkintotunnus = '374118';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '17/011/2012' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '354406' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='354406' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '354406') where tutkintotunnus = '354406';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '55/011/2002' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '354307' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='354307' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '354307') where tutkintotunnus = '354307';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '30/011/2012' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '364108' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='364108' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '364108') where tutkintotunnus = '364108';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '28/011/2002' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '355407' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='355407' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '355407') where tutkintotunnus = '355407';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '4/011/2006' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '354109' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='354109' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '354109') where tutkintotunnus = '354109';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '25/011/2014' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '355209' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='355209' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '355209') where tutkintotunnus = '355209';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '33/011/2010' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '351701' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='351701' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '351701') where tutkintotunnus = '351701';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '40/011/2006' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '364901' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='364901' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '364901') where tutkintotunnus = '364901';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '34/011/2013' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '354803' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='354803' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '354803') where tutkintotunnus = '354803';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '24/011/2010' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '321902' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='321902' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '321902') where tutkintotunnus = '321902';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '21/011/2007' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '355903' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='355903' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '355903') where tutkintotunnus = '355903';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '15/011/2007' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '367904' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='367904' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '367904') where tutkintotunnus = '367904';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '31/011/2001' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '354605' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='354605' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '354605') where tutkintotunnus = '354605';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '28/011/2009' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '351107' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='351107' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '351107') where tutkintotunnus = '351107';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '27/011/2008' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '364307' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='364307' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '364307') where tutkintotunnus = '364307';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '9/011/2004' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '374119' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='374119' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '374119') where tutkintotunnus = '374119';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '22/011/2013' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '354407' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='354407' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '354407') where tutkintotunnus = '354407';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '26/011/2004' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '364109' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='364109' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '364109') where tutkintotunnus = '364109';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '53/011/2001' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '357707' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='357707' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '357707') where tutkintotunnus = '357707';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '20/011/2008' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '354209' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='354209' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '354209') where tutkintotunnus = '354209';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '21/011/2010' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '381203' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='381203' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '381203') where tutkintotunnus = '381203';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '40/011/2012' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '364902' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='364902' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '364902') where tutkintotunnus = '364902';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '19/011/2009' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '351603' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='351603' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '351603') where tutkintotunnus = '351603';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '16/011/2009' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '384405' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='384405' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '384405') where tutkintotunnus = '384405';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '10/011/2003' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '354804' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='354804' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '354804') where tutkintotunnus = '354804';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '26/011/2011' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '384108' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='384108' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '384108') where tutkintotunnus = '384108';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '24/011/2008' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '354705' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='354705' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '354705') where tutkintotunnus = '354705';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '51/011/2002' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '367905' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='367905' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '367905') where tutkintotunnus = '367905';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '68/011/2002' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '364308' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='364308' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '364308') where tutkintotunnus = '364308';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '16/011/2012' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '354408' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='354408' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '354408') where tutkintotunnus = '354408';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '19/011/2004' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '354309' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='354309' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '354309') where tutkintotunnus = '354309';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '28/011/2011' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '357708' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='357708' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '357708') where tutkintotunnus = '357708';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '36/011/2010' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '381402' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='381402' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '381402') where tutkintotunnus = '381402';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '27/011/2009' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '381303' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='381303' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '381303') where tutkintotunnus = '381303';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '18/011/2009' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '381204' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='381204' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '381204') where tutkintotunnus = '381204';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '33/011/2009' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '351703' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='351703' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '351703') where tutkintotunnus = '351703';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '21/011/2009' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '381106' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='381106' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '381106') where tutkintotunnus = '381106';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '38/011/2010' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '351307' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='351307' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '351307') where tutkintotunnus = '351307';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '45/011/2012' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '355905' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='355905' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '355905') where tutkintotunnus = '355905';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '25/011/2011' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '384109' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='384109' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '384109') where tutkintotunnus = '384109';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '39/011/2009' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '354409' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='354409' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '354409') where tutkintotunnus = '354409';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '100/011/2014' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '357709' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='357709' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '357709') where tutkintotunnus = '357709';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '26/011/2009' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '381304' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='381304' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '381304') where tutkintotunnus = '381304';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '1/011/2010' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '352902' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='352902' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '352902') where tutkintotunnus = '352902';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '37/011/2009' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '351704' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='351704' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '351704') where tutkintotunnus = '351704';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '25/011/2012' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '364904' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='364904' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '364904') where tutkintotunnus = '364904';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '4/011/2010' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '351605' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='351605' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '351605') where tutkintotunnus = '351605';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '23/011/2009' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '351407' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='351407' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '351407') where tutkintotunnus = '351407';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '15/011/2014' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '355906' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='355906' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '355906') where tutkintotunnus = '355906';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '8/011/2010' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '352903' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='352903' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '352903') where tutkintotunnus = '352903';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '50/011/2004' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '364905' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='364905' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '364905') where tutkintotunnus = '364905';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '12/011/2010' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '371109' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='371109' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '371109') where tutkintotunnus = '371109';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '53/011/2001' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '354708' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='354708' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '354708') where tutkintotunnus = '354708';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '31/011/2009' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '361902' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='361902' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '361902') where tutkintotunnus = '361902';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '18/011/2010' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '381504' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='381504' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '381504') where tutkintotunnus = '381504';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '27/011/2013' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '364906' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='364906' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '364906') where tutkintotunnus = '364906';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '27/011/2011' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '354709' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='354709' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '354709') where tutkintotunnus = '354709';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '37/011/2010' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '351805' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='351805' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '351805') where tutkintotunnus = '351805';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '32/011/2009' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '381408' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='381408' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '381408') where tutkintotunnus = '381408';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '41/011/2013' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '337110' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='337110' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '337110') where tutkintotunnus = '337110';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '8/011/2014' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '317101' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='317101' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '317101') where tutkintotunnus = '317101';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '32/011/2007' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '324120' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='324120' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '324120') where tutkintotunnus = '324120';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '14/011/2008' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '337111' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='337111' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '337111') where tutkintotunnus = '337111';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '51/011/2012' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '327101' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='327101' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '327101') where tutkintotunnus = '327101';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '45/011/2006' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '324110' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='324110' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '324110') where tutkintotunnus = '324110';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '15/011/2011' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '337101' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='337101' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '337101') where tutkintotunnus = '337101';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '46/011/2010' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '337112' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='337112' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '337112') where tutkintotunnus = '337112';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '26/011/2007' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '357110' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='357110' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '357110') where tutkintotunnus = '357110';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '12/011/2008' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '358101' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='358101' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '358101') where tutkintotunnus = '358101';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '17/011/2011' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '347101' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='347101' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '347101') where tutkintotunnus = '347101';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '51/011/2006' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '327301' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='327301' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '327301') where tutkintotunnus = '327301';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '40/011/2013' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '337102' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='337102' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '337102') where tutkintotunnus = '337102';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '35/011/2011' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '327114' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='327114' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '327114') where tutkintotunnus = '327114';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '13/011/2013' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '357111' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='357111' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '357111') where tutkintotunnus = '357111';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '50/011/2012' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '324101' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='324101' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '324101') where tutkintotunnus = '324101';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '13/011/2008' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '358102' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='358102' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '358102') where tutkintotunnus = '358102';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '42/011/2005' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '327302' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='327302' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '327302') where tutkintotunnus = '327302';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '52/011/97' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '327104' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='327104' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '327104') where tutkintotunnus = '327104';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '52/011/2010' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '327115' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='327115' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '327115') where tutkintotunnus = '327115';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '33/011/2011' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '377110' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='377110' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '377110') where tutkintotunnus = '377110';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '4/011/2011' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '355110' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='355110' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '355110') where tutkintotunnus = '355110';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '37/011/2004' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '358410' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='358410' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '358410') where tutkintotunnus = '358410';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '8/011/2011' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '334111' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='334111' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '334111') where tutkintotunnus = '334111';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '44/011/2004' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '357101' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='357101' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '357101') where tutkintotunnus = '357101';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '48/011/2004' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '358103' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='358103' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '358103') where tutkintotunnus = '358103';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '24/011/2014' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '337104' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='337104' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '337104') where tutkintotunnus = '337104';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '54/011/1997' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '327105' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='327105' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '327105') where tutkintotunnus = '327105';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '28/011/2006' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '327116' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='327116' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '327116') where tutkintotunnus = '327116';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '26/011/2008' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '327127' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='327127' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '327127') where tutkintotunnus = '327127';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '23/011/2003' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '354110' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='354110' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '354110') where tutkintotunnus = '354110';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '55/011/2012' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '355210' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='355210' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '355210') where tutkintotunnus = '355210';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '38/011/2006' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '367101' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='367101' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '367101') where tutkintotunnus = '367101';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '99/011/2014' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '358411' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='358411' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '358411') where tutkintotunnus = '358411';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '19/011/2011' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '357201' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='357201' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '357201') where tutkintotunnus = '357201';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '66/011/2002' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '334101' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='334101' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '334101') where tutkintotunnus = '334101';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '53/011/2012' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '334112' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='334112' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '334112') where tutkintotunnus = '334112';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '23/011/2004' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '357102' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='357102' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '357102') where tutkintotunnus = '357102';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '13/011/2009' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '324201' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='324201' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '324201') where tutkintotunnus = '324201';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '34/011/2010' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '327128' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='327128' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '327128') where tutkintotunnus = '327128';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '20/011/2010' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '038411' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='038411' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '038411') where tutkintotunnus = '038411';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '21/011/2013' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '354111' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='354111' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '354111') where tutkintotunnus = '354111';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '47/011/2012' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '377101' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='377101' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '377101') where tutkintotunnus = '377101';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '18/011/2011' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '355211' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='355211' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '355211') where tutkintotunnus = '355211';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '10/011/2008' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '355101' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='355101' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '355101') where tutkintotunnus = '355101';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '35/011/2014' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '367201' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='367201' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '367201') where tutkintotunnus = '367201';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '58/011/2010' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '344101' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='344101' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '344101') where tutkintotunnus = '344101';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '53/011/2002' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '357301' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='357301' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '357301') where tutkintotunnus = '357301';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '8/011/2004' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '367102' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='367102' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '367102') where tutkintotunnus = '367102';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '4/011/2005' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '358412' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='358412' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '358412') where tutkintotunnus = '358412';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '12/011/2006' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '324301' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='324301' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '324301') where tutkintotunnus = '324301';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '39/011/2012' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '334102' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='334102' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '334102') where tutkintotunnus = '334102';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '3/011/2014' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '334113' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='334113' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '334113') where tutkintotunnus = '334113';

insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto)   select tutkintotunnus, 2 as versio, koodistoversio, '61/011/2001' as peruste, hyvaksytty, (select DATE 'yesterday'), voimassa_loppupvm, siirtymaajan_loppupvm, jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio     where tutkintotunnus = '357103' and versio = 1;
update tutkintoversio set voimassa_loppupvm = (select DATE 'yesterday') where tutkintotunnus='357103' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 2 and tutkintotunnus = '357103') where tutkintotunnus = '357103';
