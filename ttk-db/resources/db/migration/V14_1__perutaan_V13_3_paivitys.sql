
set session aitu.kayttaja='INTEGRAATIO';

update sopimus_ja_tutkinto set tutkintoversio = t1.tutkintoversio_id
from tutkintoversio t1 join tutkintoversio t2 on t1.tutkintotunnus = t2.tutkintotunnus and t1.versio = 1 and t2.versio = 2
where sopimus_ja_tutkinto.tutkintoversio = t2.tutkintoversio_id;
 
update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='324104' and versio=2) where tutkintotunnus='324104' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '324104') where tutkintotunnus = '324104';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='327503' and versio=2) where tutkintotunnus='327503' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '327503') where tutkintotunnus = '327503';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='324115' and versio=2) where tutkintotunnus='324115' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '324115') where tutkintotunnus = '324115';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='337106' and versio=2) where tutkintotunnus='337106' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '337106') where tutkintotunnus = '337106';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='327107' and versio=2) where tutkintotunnus='327107' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '327107') where tutkintotunnus = '327107';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='327118' and versio=2) where tutkintotunnus='327118' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '327118') where tutkintotunnus = '327118';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='374121' and versio=2) where tutkintotunnus='374121' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '374121') where tutkintotunnus = '374121';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='387101' and versio=2) where tutkintotunnus='387101' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '387101') where tutkintotunnus = '387101';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='354310' and versio=2) where tutkintotunnus='354310' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '354310') where tutkintotunnus = '354310';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='355410' and versio=2) where tutkintotunnus='355410' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '355410') where tutkintotunnus = '355410';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='354211' and versio=2) where tutkintotunnus='354211' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '354211') where tutkintotunnus = '354211';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='354101' and versio=2) where tutkintotunnus='354101' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '354101') where tutkintotunnus = '354101';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='039996' and versio=2) where tutkintotunnus='039996' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '039996') where tutkintotunnus = '039996';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='355201' and versio=2) where tutkintotunnus='355201' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '355201') where tutkintotunnus = '355201';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='367301' and versio=2) where tutkintotunnus='367301' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '367301') where tutkintotunnus = '367301';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='354112' and versio=2) where tutkintotunnus='354112' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '354112') where tutkintotunnus = '354112';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='377102' and versio=2) where tutkintotunnus='377102' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '377102') where tutkintotunnus = '377102';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='355212' and versio=2) where tutkintotunnus='355212' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '355212') where tutkintotunnus = '355212';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='321101' and versio=2) where tutkintotunnus='321101' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '321101') where tutkintotunnus = '321101';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='357401' and versio=2) where tutkintotunnus='357401' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '357401') where tutkintotunnus = '357401';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='355102' and versio=2) where tutkintotunnus='355102' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '355102') where tutkintotunnus = '355102';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='357302' and versio=2) where tutkintotunnus='357302' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '357302') where tutkintotunnus = '357302';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='367103' and versio=2) where tutkintotunnus='367103' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '367103') where tutkintotunnus = '367103';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='357203' and versio=2) where tutkintotunnus='357203' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '357203') where tutkintotunnus = '357203';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='334103' and versio=2) where tutkintotunnus='334103' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '334103') where tutkintotunnus = '334103';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='334114' and versio=2) where tutkintotunnus='334114' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '334114') where tutkintotunnus = '334114';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='357104' and versio=2) where tutkintotunnus='357104' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '357104') where tutkintotunnus = '357104';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='358204' and versio=2) where tutkintotunnus='358204' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '358204') where tutkintotunnus = '358204';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='324105' and versio=2) where tutkintotunnus='324105' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '324105') where tutkintotunnus = '324105';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='324116' and versio=2) where tutkintotunnus='324116' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '324116') where tutkintotunnus = '324116';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='337107' and versio=2) where tutkintotunnus='337107' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '337107') where tutkintotunnus = '337107';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='384110' and versio=2) where tutkintotunnus='384110' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '384110') where tutkintotunnus = '384110';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='327119' and versio=2) where tutkintotunnus='327119' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '327119') where tutkintotunnus = '327119';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='374111' and versio=2) where tutkintotunnus='374111' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '374111') where tutkintotunnus = '374111';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='374122' and versio=2) where tutkintotunnus='374122' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '374122') where tutkintotunnus = '374122';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='387201' and versio=2) where tutkintotunnus='387201' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '387201') where tutkintotunnus = '387201';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='364101' and versio=2) where tutkintotunnus='364101' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '364101') where tutkintotunnus = '364101';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='387102' and versio=2) where tutkintotunnus='387102' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '387102') where tutkintotunnus = '387102';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='354311' and versio=2) where tutkintotunnus='354311' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '354311') where tutkintotunnus = '354311';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='355411' and versio=2) where tutkintotunnus='355411' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '355411') where tutkintotunnus = '355411';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='354201' and versio=2) where tutkintotunnus='354201' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '354201') where tutkintotunnus = '354201';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='331101' and versio=2) where tutkintotunnus='331101' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '331101') where tutkintotunnus = '331101';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='355301' and versio=2) where tutkintotunnus='355301' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '355301') where tutkintotunnus = '355301';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='354212' and versio=2) where tutkintotunnus='354212' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '354212') where tutkintotunnus = '354212';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='354102' and versio=2) where tutkintotunnus='354102' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '354102') where tutkintotunnus = '354102';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='357501' and versio=2) where tutkintotunnus='357501' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '357501') where tutkintotunnus = '357501';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='367302' and versio=2) where tutkintotunnus='367302' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '367302') where tutkintotunnus = '367302';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='354113' and versio=2) where tutkintotunnus='354113' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '354113') where tutkintotunnus = '354113';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='377103' and versio=2) where tutkintotunnus='377103' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '377103') where tutkintotunnus = '377103';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='357402' and versio=2) where tutkintotunnus='357402' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '357402') where tutkintotunnus = '357402';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='355103' and versio=2) where tutkintotunnus='355103' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '355103') where tutkintotunnus = '355103';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='367203' and versio=2) where tutkintotunnus='367203' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '367203') where tutkintotunnus = '367203';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='358502' and versio=2) where tutkintotunnus='358502' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '358502') where tutkintotunnus = '358502';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='357303' and versio=2) where tutkintotunnus='357303' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '357303') where tutkintotunnus = '357303';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='367104' and versio=2) where tutkintotunnus='367104' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '367104') where tutkintotunnus = '367104';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='357204' and versio=2) where tutkintotunnus='357204' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '357204') where tutkintotunnus = '357204';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='334104' and versio=2) where tutkintotunnus='334104' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '334104') where tutkintotunnus = '334104';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='357105' and versio=2) where tutkintotunnus='357105' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '357105') where tutkintotunnus = '357105';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='358205' and versio=2) where tutkintotunnus='358205' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '358205') where tutkintotunnus = '358205';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='324117' and versio=2) where tutkintotunnus='324117' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '324117') where tutkintotunnus = '324117';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='324128' and versio=2) where tutkintotunnus='324128' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '324128') where tutkintotunnus = '324128';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='337108' and versio=2) where tutkintotunnus='337108' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '337108') where tutkintotunnus = '337108';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='384111' and versio=2) where tutkintotunnus='384111' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '384111') where tutkintotunnus = '384111';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='327109' and versio=2) where tutkintotunnus='327109' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '327109') where tutkintotunnus = '327109';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='364201' and versio=2) where tutkintotunnus='364201' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '364201') where tutkintotunnus = '364201';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='374123' and versio=2) where tutkintotunnus='374123' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '374123') where tutkintotunnus = '374123';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='352101' and versio=2) where tutkintotunnus='352101' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '352101') where tutkintotunnus = '352101';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='387202' and versio=2) where tutkintotunnus='387202' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '387202') where tutkintotunnus = '387202';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='341101' and versio=2) where tutkintotunnus='341101' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '341101') where tutkintotunnus = '341101';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='354301' and versio=2) where tutkintotunnus='354301' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '354301') where tutkintotunnus = '354301';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='364102' and versio=2) where tutkintotunnus='364102' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '364102') where tutkintotunnus = '364102';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='387103' and versio=2) where tutkintotunnus='387103' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '387103') where tutkintotunnus = '387103';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='354312' and versio=2) where tutkintotunnus='354312' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '354312') where tutkintotunnus = '354312';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='355412' and versio=2) where tutkintotunnus='355412' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '355412') where tutkintotunnus = '355412';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='354202' and versio=2) where tutkintotunnus='354202' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '354202') where tutkintotunnus = '354202';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='321301' and versio=2) where tutkintotunnus='321301' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '321301') where tutkintotunnus = '321301';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='357601' and versio=2) where tutkintotunnus='357601' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '357601') where tutkintotunnus = '357601';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='354103' and versio=2) where tutkintotunnus='354103' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '354103') where tutkintotunnus = '354103';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='039998' and versio=2) where tutkintotunnus='039998' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '039998') where tutkintotunnus = '039998';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='357502' and versio=2) where tutkintotunnus='357502' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '357502') where tutkintotunnus = '357502';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='354114' and versio=2) where tutkintotunnus='354114' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '354114') where tutkintotunnus = '354114';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='377104' and versio=2) where tutkintotunnus='377104' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '377104') where tutkintotunnus = '377104';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='324601' and versio=2) where tutkintotunnus='324601' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '324601') where tutkintotunnus = '324601';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='357403' and versio=2) where tutkintotunnus='357403' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '357403') where tutkintotunnus = '357403';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='355104' and versio=2) where tutkintotunnus='355104' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '355104') where tutkintotunnus = '355104';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='324502' and versio=2) where tutkintotunnus='324502' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '324502') where tutkintotunnus = '324502';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='358503' and versio=2) where tutkintotunnus='358503' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '358503') where tutkintotunnus = '358503';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='357304' and versio=2) where tutkintotunnus='357304' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '357304') where tutkintotunnus = '357304';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='357205' and versio=2) where tutkintotunnus='357205' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '357205') where tutkintotunnus = '357205';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='334105' and versio=2) where tutkintotunnus='334105' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '334105') where tutkintotunnus = '334105';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='334116' and versio=2) where tutkintotunnus='334116' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '334116') where tutkintotunnus = '334116';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='357106' and versio=2) where tutkintotunnus='357106' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '357106') where tutkintotunnus = '357106';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='358206' and versio=2) where tutkintotunnus='358206' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '358206') where tutkintotunnus = '358206';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='324107' and versio=2) where tutkintotunnus='324107' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '324107') where tutkintotunnus = '324107';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='324129' and versio=2) where tutkintotunnus='324129' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '324129') where tutkintotunnus = '324129';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='371110' and versio=2) where tutkintotunnus='371110' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '371110') where tutkintotunnus = '371110';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='337109' and versio=2) where tutkintotunnus='337109' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '337109') where tutkintotunnus = '337109';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='384101' and versio=2) where tutkintotunnus='384101' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '384101') where tutkintotunnus = '384101';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='384112' and versio=2) where tutkintotunnus='384112' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '384112') where tutkintotunnus = '384112';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='351101' and versio=2) where tutkintotunnus='351101' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '351101') where tutkintotunnus = '351101';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='387401' and versio=2) where tutkintotunnus='387401' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '387401') where tutkintotunnus = '387401';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='352201' and versio=2) where tutkintotunnus='352201' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '352201') where tutkintotunnus = '352201';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='364301' and versio=2) where tutkintotunnus='364301' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '364301') where tutkintotunnus = '364301';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='374113' and versio=2) where tutkintotunnus='374113' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '374113') where tutkintotunnus = '374113';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='364202' and versio=2) where tutkintotunnus='364202' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '364202') where tutkintotunnus = '364202';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='374124' and versio=2) where tutkintotunnus='374124' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '374124') where tutkintotunnus = '374124';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='354401' and versio=2) where tutkintotunnus='354401' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '354401') where tutkintotunnus = '354401';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='387203' and versio=2) where tutkintotunnus='387203' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '387203') where tutkintotunnus = '387203';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='355501' and versio=2) where tutkintotunnus='355501' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '355501') where tutkintotunnus = '355501';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='354302' and versio=2) where tutkintotunnus='354302' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '354302') where tutkintotunnus = '354302';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='364103' and versio=2) where tutkintotunnus='364103' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '364103') where tutkintotunnus = '364103';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='387104' and versio=2) where tutkintotunnus='387104' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '387104') where tutkintotunnus = '387104';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='354313' and versio=2) where tutkintotunnus='354313' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '354313') where tutkintotunnus = '354313';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='355413' and versio=2) where tutkintotunnus='355413' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '355413') where tutkintotunnus = '355413';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='354203' and versio=2) where tutkintotunnus='354203' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '354203') where tutkintotunnus = '354203';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='357602' and versio=2) where tutkintotunnus='357602' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '357602') where tutkintotunnus = '357602';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='354104' and versio=2) where tutkintotunnus='354104' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '354104') where tutkintotunnus = '354104';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='039999' and versio=2) where tutkintotunnus='039999' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '039999') where tutkintotunnus = '039999';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='357503' and versio=2) where tutkintotunnus='357503' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '357503') where tutkintotunnus = '357503';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='367304' and versio=2) where tutkintotunnus='367304' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '367304') where tutkintotunnus = '367304';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='354115' and versio=2) where tutkintotunnus='354115' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '354115') where tutkintotunnus = '354115';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='377105' and versio=2) where tutkintotunnus='377105' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '377105') where tutkintotunnus = '377105';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='357404' and versio=2) where tutkintotunnus='357404' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '357404') where tutkintotunnus = '357404';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='355105' and versio=2) where tutkintotunnus='355105' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '355105') where tutkintotunnus = '355105';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='358504' and versio=2) where tutkintotunnus='358504' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '358504') where tutkintotunnus = '358504';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='357305' and versio=2) where tutkintotunnus='357305' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '357305') where tutkintotunnus = '357305';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='358405' and versio=2) where tutkintotunnus='358405' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '358405') where tutkintotunnus = '358405';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='357206' and versio=2) where tutkintotunnus='357206' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '357206') where tutkintotunnus = '357206';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='334106' and versio=2) where tutkintotunnus='334106' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '334106') where tutkintotunnus = '334106';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='334117' and versio=2) where tutkintotunnus='334117' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '334117') where tutkintotunnus = '334117';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='357107' and versio=2) where tutkintotunnus='357107' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '357107') where tutkintotunnus = '357107';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='324119' and versio=2) where tutkintotunnus='324119' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '324119') where tutkintotunnus = '324119';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='384201' and versio=2) where tutkintotunnus='384201' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '384201') where tutkintotunnus = '384201';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='361101' and versio=2) where tutkintotunnus='361101' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '361101') where tutkintotunnus = '361101';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='387501' and versio=2) where tutkintotunnus='387501' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '387501') where tutkintotunnus = '387501';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='384113' and versio=2) where tutkintotunnus='384113' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '384113') where tutkintotunnus = '384113';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='354710' and versio=2) where tutkintotunnus='354710' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '354710') where tutkintotunnus = '354710';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='352301' and versio=2) where tutkintotunnus='352301' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '352301') where tutkintotunnus = '352301';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='364401' and versio=2) where tutkintotunnus='364401' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '364401') where tutkintotunnus = '364401';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='364302' and versio=2) where tutkintotunnus='364302' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '364302') where tutkintotunnus = '364302';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='354501' and versio=2) where tutkintotunnus='354501' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '354501') where tutkintotunnus = '354501';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='387303' and versio=2) where tutkintotunnus='387303' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '387303') where tutkintotunnus = '387303';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='374114' and versio=2) where tutkintotunnus='374114' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '374114') where tutkintotunnus = '374114';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='364203' and versio=2) where tutkintotunnus='364203' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '364203') where tutkintotunnus = '364203';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='354402' and versio=2) where tutkintotunnus='354402' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '354402') where tutkintotunnus = '354402';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='321501' and versio=2) where tutkintotunnus='321501' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '321501') where tutkintotunnus = '321501';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='357801' and versio=2) where tutkintotunnus='357801' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '357801') where tutkintotunnus = '357801';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='355502' and versio=2) where tutkintotunnus='355502' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '355502') where tutkintotunnus = '355502';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='358901' and versio=2) where tutkintotunnus='358901' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '358901') where tutkintotunnus = '358901';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='387105' and versio=2) where tutkintotunnus='387105' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '387105') where tutkintotunnus = '387105';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='357702' and versio=2) where tutkintotunnus='357702' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '357702') where tutkintotunnus = '357702';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='354314' and versio=2) where tutkintotunnus='354314' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '354314') where tutkintotunnus = '354314';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='357603' and versio=2) where tutkintotunnus='357603' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '357603') where tutkintotunnus = '357603';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='354105' and versio=2) where tutkintotunnus='354105' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '354105') where tutkintotunnus = '354105';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='321204' and versio=2) where tutkintotunnus='321204' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '321204') where tutkintotunnus = '321204';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='377106' and versio=2) where tutkintotunnus='377106' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '377106') where tutkintotunnus = '377106';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='357405' and versio=2) where tutkintotunnus='357405' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '357405') where tutkintotunnus = '357405';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='355106' and versio=2) where tutkintotunnus='355106' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '355106') where tutkintotunnus = '355106';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='358505' and versio=2) where tutkintotunnus='358505' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '358505') where tutkintotunnus = '358505';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='357306' and versio=2) where tutkintotunnus='357306' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '357306') where tutkintotunnus = '357306';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='357207' and versio=2) where tutkintotunnus='357207' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '357207') where tutkintotunnus = '357207';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='334118' and versio=2) where tutkintotunnus='334118' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '334118') where tutkintotunnus = '334118';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='357108' and versio=2) where tutkintotunnus='357108' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '357108') where tutkintotunnus = '357108';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='324109' and versio=2) where tutkintotunnus='324109' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '324109') where tutkintotunnus = '324109';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='371101' and versio=2) where tutkintotunnus='371101' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '371101') where tutkintotunnus = '371101';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='384301' and versio=2) where tutkintotunnus='384301' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '384301') where tutkintotunnus = '384301';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='361201' and versio=2) where tutkintotunnus='361201' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '361201') where tutkintotunnus = '361201';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='384202' and versio=2) where tutkintotunnus='384202' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '384202') where tutkintotunnus = '384202';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='351301' and versio=2) where tutkintotunnus='351301' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '351301') where tutkintotunnus = '351301';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='352401' and versio=2) where tutkintotunnus='352401' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '352401') where tutkintotunnus = '352401';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='384103' and versio=2) where tutkintotunnus='384103' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '384103') where tutkintotunnus = '384103';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='384114' and versio=2) where tutkintotunnus='384114' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '384114') where tutkintotunnus = '384114';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='364402' and versio=2) where tutkintotunnus='364402' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '364402') where tutkintotunnus = '364402';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='354601' and versio=2) where tutkintotunnus='354601' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '354601') where tutkintotunnus = '354601';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='354502' and versio=2) where tutkintotunnus='354502' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '354502') where tutkintotunnus = '354502';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='387304' and versio=2) where tutkintotunnus='387304' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '387304') where tutkintotunnus = '387304';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='374115' and versio=2) where tutkintotunnus='374115' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '374115') where tutkintotunnus = '374115';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='364204' and versio=2) where tutkintotunnus='364204' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '364204') where tutkintotunnus = '364204';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='354403' and versio=2) where tutkintotunnus='354403' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '354403') where tutkintotunnus = '354403';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='357802' and versio=2) where tutkintotunnus='357802' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '357802') where tutkintotunnus = '357802';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='355503' and versio=2) where tutkintotunnus='355503' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '355503') where tutkintotunnus = '355503';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='358902' and versio=2) where tutkintotunnus='358902' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '358902') where tutkintotunnus = '358902';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='364105' and versio=2) where tutkintotunnus='364105' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '364105') where tutkintotunnus = '364105';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='387106' and versio=2) where tutkintotunnus='387106' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '387106') where tutkintotunnus = '387106';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='354205' and versio=2) where tutkintotunnus='354205' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '354205') where tutkintotunnus = '354205';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='354106' and versio=2) where tutkintotunnus='354106' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '354106') where tutkintotunnus = '354106';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='377107' and versio=2) where tutkintotunnus='377107' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '377107') where tutkintotunnus = '377107';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='355107' and versio=2) where tutkintotunnus='355107' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '355107') where tutkintotunnus = '355107';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='358506' and versio=2) where tutkintotunnus='358506' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '358506') where tutkintotunnus = '358506';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='357307' and versio=2) where tutkintotunnus='357307' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '357307') where tutkintotunnus = '357307';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='334108' and versio=2) where tutkintotunnus='334108' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '334108') where tutkintotunnus = '334108';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='334119' and versio=2) where tutkintotunnus='334119' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '334119') where tutkintotunnus = '334119';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='357109' and versio=2) where tutkintotunnus='357109' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '357109') where tutkintotunnus = '357109';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='381112' and versio=2) where tutkintotunnus='381112' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '381112') where tutkintotunnus = '381112';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='384401' and versio=2) where tutkintotunnus='384401' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '384401') where tutkintotunnus = '384401';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='361301' and versio=2) where tutkintotunnus='361301' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '361301') where tutkintotunnus = '361301';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='384203' and versio=2) where tutkintotunnus='384203' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '384203') where tutkintotunnus = '384203';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='351203' and versio=2) where tutkintotunnus='351203' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '351203') where tutkintotunnus = '351203';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='367901' and versio=2) where tutkintotunnus='367901' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '367901') where tutkintotunnus = '367901';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='364403' and versio=2) where tutkintotunnus='364403' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '364403') where tutkintotunnus = '364403';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='354602' and versio=2) where tutkintotunnus='354602' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '354602') where tutkintotunnus = '354602';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='364304' and versio=2) where tutkintotunnus='364304' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '364304') where tutkintotunnus = '364304';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='354503' and versio=2) where tutkintotunnus='354503' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '354503') where tutkintotunnus = '354503';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='321602' and versio=2) where tutkintotunnus='321602' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '321602') where tutkintotunnus = '321602';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='364205' and versio=2) where tutkintotunnus='364205' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '364205') where tutkintotunnus = '364205';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='354404' and versio=2) where tutkintotunnus='354404' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '354404') where tutkintotunnus = '354404';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='357803' and versio=2) where tutkintotunnus='357803' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '357803') where tutkintotunnus = '357803';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='355504' and versio=2) where tutkintotunnus='355504' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '355504') where tutkintotunnus = '355504';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='364106' and versio=2) where tutkintotunnus='364106' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '364106') where tutkintotunnus = '364106';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='354206' and versio=2) where tutkintotunnus='354206' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '354206') where tutkintotunnus = '354206';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='354107' and versio=2) where tutkintotunnus='354107' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '354107') where tutkintotunnus = '354107';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='377108' and versio=2) where tutkintotunnus='377108' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '377108') where tutkintotunnus = '377108';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='355108' and versio=2) where tutkintotunnus='355108' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '355108') where tutkintotunnus = '355108';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='358408' and versio=2) where tutkintotunnus='358408' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '358408') where tutkintotunnus = '358408';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='334109' and versio=2) where tutkintotunnus='334109' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '334109') where tutkintotunnus = '334109';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='384501' and versio=2) where tutkintotunnus='384501' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '384501') where tutkintotunnus = '384501';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='381113' and versio=2) where tutkintotunnus='381113' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '381113') where tutkintotunnus = '381113';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='361401' and versio=2) where tutkintotunnus='361401' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '361401') where tutkintotunnus = '361401';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='384402' and versio=2) where tutkintotunnus='384402' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '384402') where tutkintotunnus = '384402';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='354801' and versio=2) where tutkintotunnus='354801' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '354801') where tutkintotunnus = '354801';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='384204' and versio=2) where tutkintotunnus='384204' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '384204') where tutkintotunnus = '384204';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='361104' and versio=2) where tutkintotunnus='361104' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '361104') where tutkintotunnus = '361104';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='355901' and versio=2) where tutkintotunnus='355901' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '355901') where tutkintotunnus = '355901';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='354702' and versio=2) where tutkintotunnus='354702' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '354702') where tutkintotunnus = '354702';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='351204' and versio=2) where tutkintotunnus='351204' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '351204') where tutkintotunnus = '351204';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='367902' and versio=2) where tutkintotunnus='367902' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '367902') where tutkintotunnus = '367902';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='354603' and versio=2) where tutkintotunnus='354603' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '354603') where tutkintotunnus = '354603';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='364305' and versio=2) where tutkintotunnus='364305' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '364305') where tutkintotunnus = '364305';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='374117' and versio=2) where tutkintotunnus='374117' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '374117') where tutkintotunnus = '374117';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='354405' and versio=2) where tutkintotunnus='354405' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '354405') where tutkintotunnus = '354405';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='355505' and versio=2) where tutkintotunnus='355505' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '355505') where tutkintotunnus = '355505';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='358904' and versio=2) where tutkintotunnus='358904' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '358904') where tutkintotunnus = '358904';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='364107' and versio=2) where tutkintotunnus='364107' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '364107') where tutkintotunnus = '364107';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='357705' and versio=2) where tutkintotunnus='357705' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '357705') where tutkintotunnus = '357705';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='354207' and versio=2) where tutkintotunnus='354207' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '354207') where tutkintotunnus = '354207';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='354108' and versio=2) where tutkintotunnus='354108' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '354108') where tutkintotunnus = '354108';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='355208' and versio=2) where tutkintotunnus='355208' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '355208') where tutkintotunnus = '355208';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='377109' and versio=2) where tutkintotunnus='377109' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '377109') where tutkintotunnus = '377109';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='355109' and versio=2) where tutkintotunnus='355109' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '355109') where tutkintotunnus = '355109';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='358508' and versio=2) where tutkintotunnus='358508' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '358508') where tutkintotunnus = '358508';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='358409' and versio=2) where tutkintotunnus='358409' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '358409') where tutkintotunnus = '358409';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='381410' and versio=2) where tutkintotunnus='381410' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '381410') where tutkintotunnus = '381410';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='381201' and versio=2) where tutkintotunnus='381201' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '381201') where tutkintotunnus = '381201';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='384403' and versio=2) where tutkintotunnus='384403' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '384403') where tutkintotunnus = '384403';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='351502' and versio=2) where tutkintotunnus='351502' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '351502') where tutkintotunnus = '351502';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='354802' and versio=2) where tutkintotunnus='354802' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '354802') where tutkintotunnus = '354802';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='352503' and versio=2) where tutkintotunnus='352503' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '352503') where tutkintotunnus = '352503';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='384205' and versio=2) where tutkintotunnus='384205' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '384205') where tutkintotunnus = '384205';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='321901' and versio=2) where tutkintotunnus='321901' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '321901') where tutkintotunnus = '321901';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='355902' and versio=2) where tutkintotunnus='355902' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '355902') where tutkintotunnus = '355902';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='384106' and versio=2) where tutkintotunnus='384106' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '384106') where tutkintotunnus = '384106';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='367903' and versio=2) where tutkintotunnus='367903' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '367903') where tutkintotunnus = '367903';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='354604' and versio=2) where tutkintotunnus='354604' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '354604') where tutkintotunnus = '354604';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='351106' and versio=2) where tutkintotunnus='351106' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '351106') where tutkintotunnus = '351106';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='374118' and versio=2) where tutkintotunnus='374118' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '374118') where tutkintotunnus = '374118';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='354406' and versio=2) where tutkintotunnus='354406' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '354406') where tutkintotunnus = '354406';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='354307' and versio=2) where tutkintotunnus='354307' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '354307') where tutkintotunnus = '354307';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='364108' and versio=2) where tutkintotunnus='364108' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '364108') where tutkintotunnus = '364108';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='355407' and versio=2) where tutkintotunnus='355407' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '355407') where tutkintotunnus = '355407';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='354109' and versio=2) where tutkintotunnus='354109' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '354109') where tutkintotunnus = '354109';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='355209' and versio=2) where tutkintotunnus='355209' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '355209') where tutkintotunnus = '355209';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='351701' and versio=2) where tutkintotunnus='351701' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '351701') where tutkintotunnus = '351701';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='364901' and versio=2) where tutkintotunnus='364901' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '364901') where tutkintotunnus = '364901';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='354803' and versio=2) where tutkintotunnus='354803' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '354803') where tutkintotunnus = '354803';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='321902' and versio=2) where tutkintotunnus='321902' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '321902') where tutkintotunnus = '321902';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='355903' and versio=2) where tutkintotunnus='355903' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '355903') where tutkintotunnus = '355903';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='367904' and versio=2) where tutkintotunnus='367904' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '367904') where tutkintotunnus = '367904';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='354605' and versio=2) where tutkintotunnus='354605' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '354605') where tutkintotunnus = '354605';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='351107' and versio=2) where tutkintotunnus='351107' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '351107') where tutkintotunnus = '351107';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='364307' and versio=2) where tutkintotunnus='364307' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '364307') where tutkintotunnus = '364307';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='374119' and versio=2) where tutkintotunnus='374119' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '374119') where tutkintotunnus = '374119';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='354407' and versio=2) where tutkintotunnus='354407' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '354407') where tutkintotunnus = '354407';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='364109' and versio=2) where tutkintotunnus='364109' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '364109') where tutkintotunnus = '364109';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='357707' and versio=2) where tutkintotunnus='357707' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '357707') where tutkintotunnus = '357707';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='354209' and versio=2) where tutkintotunnus='354209' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '354209') where tutkintotunnus = '354209';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='381203' and versio=2) where tutkintotunnus='381203' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '381203') where tutkintotunnus = '381203';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='364902' and versio=2) where tutkintotunnus='364902' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '364902') where tutkintotunnus = '364902';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='351603' and versio=2) where tutkintotunnus='351603' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '351603') where tutkintotunnus = '351603';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='384405' and versio=2) where tutkintotunnus='384405' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '384405') where tutkintotunnus = '384405';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='354804' and versio=2) where tutkintotunnus='354804' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '354804') where tutkintotunnus = '354804';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='384108' and versio=2) where tutkintotunnus='384108' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '384108') where tutkintotunnus = '384108';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='354705' and versio=2) where tutkintotunnus='354705' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '354705') where tutkintotunnus = '354705';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='367905' and versio=2) where tutkintotunnus='367905' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '367905') where tutkintotunnus = '367905';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='364308' and versio=2) where tutkintotunnus='364308' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '364308') where tutkintotunnus = '364308';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='354408' and versio=2) where tutkintotunnus='354408' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '354408') where tutkintotunnus = '354408';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='354309' and versio=2) where tutkintotunnus='354309' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '354309') where tutkintotunnus = '354309';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='357708' and versio=2) where tutkintotunnus='357708' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '357708') where tutkintotunnus = '357708';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='381402' and versio=2) where tutkintotunnus='381402' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '381402') where tutkintotunnus = '381402';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='381303' and versio=2) where tutkintotunnus='381303' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '381303') where tutkintotunnus = '381303';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='381204' and versio=2) where tutkintotunnus='381204' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '381204') where tutkintotunnus = '381204';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='351703' and versio=2) where tutkintotunnus='351703' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '351703') where tutkintotunnus = '351703';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='381106' and versio=2) where tutkintotunnus='381106' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '381106') where tutkintotunnus = '381106';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='351307' and versio=2) where tutkintotunnus='351307' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '351307') where tutkintotunnus = '351307';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='355905' and versio=2) where tutkintotunnus='355905' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '355905') where tutkintotunnus = '355905';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='384109' and versio=2) where tutkintotunnus='384109' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '384109') where tutkintotunnus = '384109';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='354409' and versio=2) where tutkintotunnus='354409' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '354409') where tutkintotunnus = '354409';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='357709' and versio=2) where tutkintotunnus='357709' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '357709') where tutkintotunnus = '357709';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='381304' and versio=2) where tutkintotunnus='381304' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '381304') where tutkintotunnus = '381304';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='352902' and versio=2) where tutkintotunnus='352902' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '352902') where tutkintotunnus = '352902';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='351704' and versio=2) where tutkintotunnus='351704' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '351704') where tutkintotunnus = '351704';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='364904' and versio=2) where tutkintotunnus='364904' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '364904') where tutkintotunnus = '364904';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='351605' and versio=2) where tutkintotunnus='351605' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '351605') where tutkintotunnus = '351605';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='351407' and versio=2) where tutkintotunnus='351407' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '351407') where tutkintotunnus = '351407';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='355906' and versio=2) where tutkintotunnus='355906' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '355906') where tutkintotunnus = '355906';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='352903' and versio=2) where tutkintotunnus='352903' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '352903') where tutkintotunnus = '352903';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='364905' and versio=2) where tutkintotunnus='364905' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '364905') where tutkintotunnus = '364905';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='371109' and versio=2) where tutkintotunnus='371109' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '371109') where tutkintotunnus = '371109';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='354708' and versio=2) where tutkintotunnus='354708' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '354708') where tutkintotunnus = '354708';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='361902' and versio=2) where tutkintotunnus='361902' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '361902') where tutkintotunnus = '361902';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='381504' and versio=2) where tutkintotunnus='381504' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '381504') where tutkintotunnus = '381504';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='364906' and versio=2) where tutkintotunnus='364906' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '364906') where tutkintotunnus = '364906';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='354709' and versio=2) where tutkintotunnus='354709' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '354709') where tutkintotunnus = '354709';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='351805' and versio=2) where tutkintotunnus='351805' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '351805') where tutkintotunnus = '351805';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='381408' and versio=2) where tutkintotunnus='381408' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '381408') where tutkintotunnus = '381408';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='337110' and versio=2) where tutkintotunnus='337110' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '337110') where tutkintotunnus = '337110';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='317101' and versio=2) where tutkintotunnus='317101' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '317101') where tutkintotunnus = '317101';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='324120' and versio=2) where tutkintotunnus='324120' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '324120') where tutkintotunnus = '324120';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='337111' and versio=2) where tutkintotunnus='337111' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '337111') where tutkintotunnus = '337111';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='327101' and versio=2) where tutkintotunnus='327101' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '327101') where tutkintotunnus = '327101';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='324110' and versio=2) where tutkintotunnus='324110' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '324110') where tutkintotunnus = '324110';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='337101' and versio=2) where tutkintotunnus='337101' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '337101') where tutkintotunnus = '337101';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='337112' and versio=2) where tutkintotunnus='337112' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '337112') where tutkintotunnus = '337112';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='357110' and versio=2) where tutkintotunnus='357110' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '357110') where tutkintotunnus = '357110';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='358101' and versio=2) where tutkintotunnus='358101' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '358101') where tutkintotunnus = '358101';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='347101' and versio=2) where tutkintotunnus='347101' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '347101') where tutkintotunnus = '347101';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='327301' and versio=2) where tutkintotunnus='327301' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '327301') where tutkintotunnus = '327301';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='337102' and versio=2) where tutkintotunnus='337102' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '337102') where tutkintotunnus = '337102';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='327114' and versio=2) where tutkintotunnus='327114' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '327114') where tutkintotunnus = '327114';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='357111' and versio=2) where tutkintotunnus='357111' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '357111') where tutkintotunnus = '357111';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='324101' and versio=2) where tutkintotunnus='324101' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '324101') where tutkintotunnus = '324101';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='358102' and versio=2) where tutkintotunnus='358102' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '358102') where tutkintotunnus = '358102';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='327302' and versio=2) where tutkintotunnus='327302' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '327302') where tutkintotunnus = '327302';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='327104' and versio=2) where tutkintotunnus='327104' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '327104') where tutkintotunnus = '327104';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='327115' and versio=2) where tutkintotunnus='327115' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '327115') where tutkintotunnus = '327115';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='377110' and versio=2) where tutkintotunnus='377110' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '377110') where tutkintotunnus = '377110';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='355110' and versio=2) where tutkintotunnus='355110' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '355110') where tutkintotunnus = '355110';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='358410' and versio=2) where tutkintotunnus='358410' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '358410') where tutkintotunnus = '358410';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='334111' and versio=2) where tutkintotunnus='334111' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '334111') where tutkintotunnus = '334111';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='357101' and versio=2) where tutkintotunnus='357101' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '357101') where tutkintotunnus = '357101';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='358103' and versio=2) where tutkintotunnus='358103' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '358103') where tutkintotunnus = '358103';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='337104' and versio=2) where tutkintotunnus='337104' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '337104') where tutkintotunnus = '337104';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='327105' and versio=2) where tutkintotunnus='327105' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '327105') where tutkintotunnus = '327105';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='327116' and versio=2) where tutkintotunnus='327116' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '327116') where tutkintotunnus = '327116';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='327127' and versio=2) where tutkintotunnus='327127' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '327127') where tutkintotunnus = '327127';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='354110' and versio=2) where tutkintotunnus='354110' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '354110') where tutkintotunnus = '354110';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='355210' and versio=2) where tutkintotunnus='355210' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '355210') where tutkintotunnus = '355210';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='367101' and versio=2) where tutkintotunnus='367101' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '367101') where tutkintotunnus = '367101';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='358411' and versio=2) where tutkintotunnus='358411' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '358411') where tutkintotunnus = '358411';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='357201' and versio=2) where tutkintotunnus='357201' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '357201') where tutkintotunnus = '357201';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='334101' and versio=2) where tutkintotunnus='334101' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '334101') where tutkintotunnus = '334101';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='334112' and versio=2) where tutkintotunnus='334112' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '334112') where tutkintotunnus = '334112';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='357102' and versio=2) where tutkintotunnus='357102' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '357102') where tutkintotunnus = '357102';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='324201' and versio=2) where tutkintotunnus='324201' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '324201') where tutkintotunnus = '324201';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='327128' and versio=2) where tutkintotunnus='327128' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '327128') where tutkintotunnus = '327128';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='038411' and versio=2) where tutkintotunnus='038411' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '038411') where tutkintotunnus = '038411';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='354111' and versio=2) where tutkintotunnus='354111' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '354111') where tutkintotunnus = '354111';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='377101' and versio=2) where tutkintotunnus='377101' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '377101') where tutkintotunnus = '377101';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='355211' and versio=2) where tutkintotunnus='355211' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '355211') where tutkintotunnus = '355211';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='355101' and versio=2) where tutkintotunnus='355101' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '355101') where tutkintotunnus = '355101';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='367201' and versio=2) where tutkintotunnus='367201' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '367201') where tutkintotunnus = '367201';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='344101' and versio=2) where tutkintotunnus='344101' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '344101') where tutkintotunnus = '344101';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='357301' and versio=2) where tutkintotunnus='357301' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '357301') where tutkintotunnus = '357301';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='367102' and versio=2) where tutkintotunnus='367102' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '367102') where tutkintotunnus = '367102';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='358412' and versio=2) where tutkintotunnus='358412' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '358412') where tutkintotunnus = '358412';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='324301' and versio=2) where tutkintotunnus='324301' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '324301') where tutkintotunnus = '324301';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='334102' and versio=2) where tutkintotunnus='334102' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '334102') where tutkintotunnus = '334102';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='334113' and versio=2) where tutkintotunnus='334113' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '334113') where tutkintotunnus = '334113';

update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='357103' and versio=2) where tutkintotunnus='357103' and versio=1;
update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '357103') where tutkintotunnus = '357103';
 
delete from tutkintoversio where versio = 2;
