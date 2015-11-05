alter table jarjesto add column oid varchar(80);
alter table jarjesto add column www_osoite varchar(200);

create temporary table jarjestomuutos (uusi int, vanha int);
insert into jarjestomuutos (uusi, vanha) values
	(1, 12022000),
	(12761, 12024000),
	(12104, 13662),
	(13085, 13663),
	(102, 12042000),
	(30, 13661),
	(96, 12361),
	(178, 315),
	(13644, 12302),
	(42, 12001000),
	(13024, 13581),
	(160, 13591),
	(12057, 13501),
	(12681, 12253),
	(455, 12041000),
	(6, 12023000);

update jarjesto set keskusjarjestoid = m.uusi from jarjestomuutos m where keskusjarjestoid = m.vanha;
update henkilo set jarjesto = m.uusi from jarjestomuutos m where jarjesto = m.vanha;
update jasenyys set esittaja = m.uusi from jarjestomuutos m where esittaja = m.vanha;

delete from jarjesto where jarjestoid in (select vanha from jarjestomuutos);
drop table jarjestomuutos;

update jarjesto set oid = '1.2.246.562.10.83712673618' where jarjestoid = 12128;
update jarjesto set oid = '1.2.246.562.10.76409151693' where jarjestoid = 146;
update jarjesto set oid = '1.2.246.562.10.77037285505' where jarjestoid = 1;
update jarjesto set oid = '1.2.246.562.10.23537593786' where jarjestoid = 13682;
update jarjesto set oid = '1.2.246.562.10.38230845821' where jarjestoid = 12054;
update jarjesto set oid = '1.2.246.562.10.70171116788' where jarjestoid = 194;
update jarjesto set oid = '1.2.246.562.10.64890093254' where jarjestoid = 13782;
update jarjesto set oid = '1.2.246.562.10.50376787280' where jarjestoid = 193;
update jarjesto set oid = '1.2.246.562.10.55757490549' where jarjestoid = 13798;
update jarjesto set oid = '1.2.246.562.10.98301593353' where jarjestoid = 12761;
update jarjesto set oid = '1.2.246.562.10.55249886732' where jarjestoid = 150;
update jarjesto set oid = '1.2.246.562.10.52313316187' where jarjestoid = 13083;
update jarjesto set oid = '1.2.246.562.10.38339452281' where jarjestoid = 166;
update jarjesto set oid = '1.2.246.562.10.55182524321' where jarjestoid = 80;
update jarjesto set oid = '1.2.246.562.10.80081534978' where jarjestoid = 12986;
update jarjesto set oid = '1.2.246.562.10.44827401911' where jarjestoid = 13584;
update jarjesto set oid = '1.2.246.562.10.71244066238' where jarjestoid = 13045;
update jarjesto set oid = '1.2.246.562.10.66104677575' where jarjestoid = 13609;
update jarjesto set oid = '1.2.246.562.10.68379383379' where jarjestoid = 13762;
update jarjesto set oid = '1.2.246.562.10.21574422604' where jarjestoid = 12120;
update jarjesto set oid = '1.2.246.562.10.88314099576' where jarjestoid = 13046;
update jarjesto set oid = '1.2.246.562.10.96489565980' where jarjestoid = 12682;
update jarjesto set oid = '1.2.246.562.10.42823289230' where jarjestoid = 13608;
update jarjesto set oid = '1.2.246.562.10.52258382170' where jarjestoid = 12961;
update jarjesto set oid = '1.2.246.562.10.62358456594' where jarjestoid = 12821;
update jarjesto set oid = '1.2.246.562.10.65893293607' where jarjestoid = 12921;
update jarjesto set oid = '1.2.246.562.10.11532650031' where jarjestoid = 314;
update jarjesto set oid = '1.2.246.562.10.64432736250' where jarjestoid = 13599;
update jarjesto set oid = '1.2.246.562.10.75260646545' where jarjestoid = 13600;
update jarjesto set oid = '1.2.246.562.10.96329530866' where jarjestoid = 2;
update jarjesto set oid = '1.2.246.562.10.44408193921' where jarjestoid = 142;
update jarjesto set oid = '1.2.246.562.10.88185158518' where jarjestoid = 12842;
update jarjesto set oid = '1.2.246.562.10.34564082903' where jarjestoid = 13796;
update jarjesto set oid = '1.2.246.562.10.88628324337' where jarjestoid = 170;
update jarjesto set oid = '1.2.246.562.10.53024950765' where jarjestoid = 13784;
update jarjesto set oid = '1.2.246.562.10.27164493753' where jarjestoid = 13025;
update jarjesto set oid = '1.2.246.562.10.23692790806' where jarjestoid = 12104;
update jarjesto set oid = '1.2.246.562.10.11659538258' where jarjestoid = 56;
update jarjesto set oid = '1.2.246.562.10.42132459991' where jarjestoid = 79;
update jarjesto set oid = '1.2.246.562.10.34714953735' where jarjestoid = 187;
update jarjesto set oid = '1.2.246.562.10.72065979702' where jarjestoid = 158;
update jarjesto set oid = '1.2.246.562.10.43398611411' where jarjestoid = 12183;
update jarjesto set oid = '1.2.246.562.10.72628149702' where jarjestoid = 12053;
update jarjesto set oid = '1.2.246.562.10.54525158065' where jarjestoid = 124;
update jarjesto set oid = '1.2.246.562.10.43598042758' where jarjestoid = 13583;
update jarjesto set oid = '1.2.246.562.10.72643846251' where jarjestoid = 12122;
update jarjesto set oid = '1.2.246.562.10.29219127167' where jarjestoid = 12113;
update jarjesto set oid = '1.2.246.562.10.23105835236' where jarjestoid = 13085;
update jarjesto set oid = '1.2.246.562.10.58359660439' where jarjestoid = 44;
update jarjesto set oid = '1.2.246.562.10.72306185577' where jarjestoid = 13787;
update jarjesto set oid = '1.2.246.562.10.94830243529' where jarjestoid = 186;
update jarjesto set oid = '1.2.246.562.10.52547716042' where jarjestoid = 102;
update jarjesto set oid = '1.2.246.562.10.52440875149' where jarjestoid = 13783;
update jarjesto set oid = '1.2.246.562.10.22983607308' where jarjestoid = 117;
update jarjesto set oid = '1.2.246.562.10.43691789224' where jarjestoid = 12126;
update jarjesto set oid = '1.2.246.562.10.37407795143' where jarjestoid = 12043;
update jarjesto set oid = '1.2.246.562.10.73781485327' where jarjestoid = 13122;
update jarjesto set oid = '1.2.246.562.10.68167875057' where jarjestoid = 13281;
update jarjesto set oid = '1.2.246.562.10.66421540106' where jarjestoid = 13795;
update jarjesto set oid = '1.2.246.562.10.11053362262' where jarjestoid = 9;
update jarjesto set oid = '1.2.246.562.10.86362218853' where jarjestoid = 53;
update jarjesto set oid = '1.2.246.562.10.35663293556' where jarjestoid = 30;
update jarjesto set oid = '1.2.246.562.10.48233345937' where jarjestoid = 96;
update jarjesto set oid = '1.2.246.562.10.99732588486' where jarjestoid = 12109;
update jarjesto set oid = '1.2.246.562.10.29050308269' where jarjestoid = 13802;
update jarjesto set oid = '1.2.246.562.10.61161503912' where jarjestoid = 12941;
update jarjesto set oid = '1.2.246.562.10.50062812559' where jarjestoid = 13081;
update jarjesto set oid = '1.2.246.562.10.73434743755' where jarjestoid = 94;
update jarjesto set oid = '1.2.246.562.10.58888133933' where jarjestoid = 3;
update jarjesto set oid = '1.2.246.562.10.78913146549' where jarjestoid = 119;
update jarjesto set oid = '1.2.246.562.10.14702746459' where jarjestoid = 13204;
update jarjesto set oid = '1.2.246.562.10.82590884348' where jarjestoid = 12022;
update jarjesto set oid = '1.2.246.562.10.93649315678' where jarjestoid = 12661;
update jarjesto set oid = '1.2.246.562.10.21294539460' where jarjestoid = 31;
update jarjesto set oid = '1.2.246.562.10.49923707097' where jarjestoid = 13792;
update jarjesto set oid = '1.2.246.562.10.26055510092' where jarjestoid = 12982;
update jarjesto set oid = '1.2.246.562.10.33381876743' where jarjestoid = 45;
update jarjesto set oid = '1.2.246.562.10.99353189217' where jarjestoid = 60;
update jarjesto set oid = '1.2.246.562.10.87652412696' where jarjestoid = 13610;
update jarjesto set oid = '1.2.246.562.10.51381076087' where jarjestoid = 27;
update jarjesto set oid = '1.2.246.562.10.30112301779' where jarjestoid = 12881;
update jarjesto set oid = '1.2.246.562.10.56017845208' where jarjestoid = 13644;
update jarjesto set oid = '1.2.246.562.10.20668872959' where jarjestoid = 13041;
update jarjesto set oid = '1.2.246.562.10.36102564716' where jarjestoid = 13241;
update jarjesto set oid = '1.2.246.562.10.71790730003' where jarjestoid = 12063;
update jarjesto set oid = '1.2.246.562.10.47074435777' where jarjestoid = 13785;
update jarjesto set oid = '1.2.246.562.10.42480376395' where jarjestoid = 12721;
update jarjesto set oid = '1.2.246.562.10.33496136810' where jarjestoid = 12119;
update jarjesto set oid = '1.2.246.562.10.77427971912' where jarjestoid = 12116;
update jarjesto set oid = '1.2.246.562.10.17931968695' where jarjestoid = 12161;
update jarjesto set oid = '1.2.246.562.10.50651746320' where jarjestoid = 13622;
update jarjesto set oid = '1.2.246.562.10.36128786639' where jarjestoid = 178;
update jarjesto set oid = '1.2.246.562.10.71299456817' where jarjestoid = 13521;
update jarjesto set oid = '1.2.246.562.10.26858787761' where jarjestoid = 12987;
update jarjesto set oid = '1.2.246.562.10.28064356404' where jarjestoid = 12058;
update jarjesto set oid = '1.2.246.562.10.57774954989' where jarjestoid = 12118;
update jarjesto set oid = '1.2.246.562.10.51722358107' where jarjestoid = 130;
update jarjesto set oid = '1.2.246.562.10.37965376161' where jarjestoid = 42;
update jarjesto set oid = '1.2.246.562.10.85184226057' where jarjestoid = 13024;
update jarjesto set oid = '1.2.246.562.10.16492899210' where jarjestoid = 13588;
update jarjesto set oid = '1.2.246.562.10.97524648338' where jarjestoid = 13561;
update jarjesto set oid = '1.2.246.562.10.26422074025' where jarjestoid = 69;
update jarjesto set oid = '1.2.246.562.10.50101276097' where jarjestoid = 12045;
update jarjesto set oid = '1.2.246.562.10.23232624865' where jarjestoid = 23;
update jarjesto set oid = '1.2.246.562.10.94417981480' where jarjestoid = 15;
update jarjesto set oid = '1.2.246.562.10.15287102597' where jarjestoid = 12055;
update jarjesto set oid = '1.2.246.562.10.11089469459' where jarjestoid = 12228;
update jarjesto set oid = '1.2.246.562.10.32476826322' where jarjestoid = 13597;
update jarjesto set oid = '1.2.246.562.10.26265202926' where jarjestoid = 12062;
update jarjesto set oid = '1.2.246.562.10.56578954211' where jarjestoid = 415;
update jarjesto set oid = '1.2.246.562.10.20102531956' where jarjestoid = 13063;
update jarjesto set oid = '1.2.246.562.10.30634972186' where jarjestoid = 12462;
update jarjesto set oid = '1.2.246.562.10.84843630442' where jarjestoid = 318;
update jarjesto set oid = '1.2.246.562.10.39415002494' where jarjestoid = 132;
update jarjesto set oid = '1.2.246.562.10.41907106567' where jarjestoid = 13001;
update jarjesto set oid = '1.2.246.562.10.32757853083' where jarjestoid = 13102;
update jarjesto set oid = '1.2.246.562.10.22386455515' where jarjestoid = 12123;
update jarjesto set oid = '1.2.246.562.10.42182895391' where jarjestoid = 234;
update jarjesto set oid = '1.2.246.562.10.47075649552' where jarjestoid = 12841;
update jarjesto set oid = '1.2.246.562.10.77875094954' where jarjestoid = 435;
update jarjesto set oid = '1.2.246.562.10.56416538930' where jarjestoid = 12167;
update jarjesto set oid = '1.2.246.562.10.18830550471' where jarjestoid = 129;
update jarjesto set oid = '1.2.246.562.10.33383409159' where jarjestoid = 59;
update jarjesto set oid = '1.2.246.562.10.90832009080' where jarjestoid = 13201;
update jarjesto set oid = '1.2.246.562.10.54139225534' where jarjestoid = 12106;
update jarjesto set oid = '1.2.246.562.10.22503538422' where jarjestoid = 160;
update jarjesto set oid = '1.2.246.562.10.35601974037' where jarjestoid = 177;
update jarjesto set oid = '1.2.246.562.10.17153677818' where jarjestoid = 12184;
update jarjesto set oid = '1.2.246.562.10.36223901549' where jarjestoid = 12057;
update jarjesto set oid = '1.2.246.562.10.43522322494' where jarjestoid = 12166;
update jarjesto set oid = '1.2.246.562.10.14326350294' where jarjestoid = 16;
update jarjesto set oid = '1.2.246.562.10.14516715323' where jarjestoid = 13022;
update jarjesto set oid = '1.2.246.562.10.18309499882' where jarjestoid = 13606;
update jarjesto set oid = '1.2.246.562.10.64295669474' where jarjestoid = 13142;
update jarjesto set oid = '1.2.246.562.10.67918585710' where jarjestoid = 12644;
update jarjesto set oid = '1.2.246.562.10.73134028902' where jarjestoid = 13062;
update jarjesto set oid = '1.2.246.562.10.10879842445' where jarjestoid = 12282;
update jarjesto set oid = '1.2.246.562.10.47096080344' where jarjestoid = 12681;
update jarjesto set oid = '1.2.246.562.10.73114343343' where jarjestoid = 144;
update jarjesto set oid = '1.2.246.562.10.72056599473' where jarjestoid = 13621;
update jarjesto set oid = '1.2.246.562.10.78458652570' where jarjestoid = 255;
update jarjesto set oid = '1.2.246.562.10.97199918185' where jarjestoid = 375;
update jarjesto set oid = '1.2.246.562.10.66701641409' where jarjestoid = 13788;
update jarjesto set oid = '1.2.246.562.10.63139428244' where jarjestoid = 184;
update jarjesto set oid = '1.2.246.562.10.80157919646' where jarjestoid = 183;
update jarjesto set oid = '1.2.246.562.10.88168498911' where jarjestoid = 13794;
update jarjesto set oid = '1.2.246.562.10.98924267279' where jarjestoid = 12985;
update jarjesto set oid = '1.2.246.562.10.85749564763' where jarjestoid = 13601;
update jarjesto set oid = '1.2.246.562.10.72221872501' where jarjestoid = 13401;
update jarjesto set oid = '1.2.246.562.10.25488944639' where jarjestoid = 101;
update jarjesto set oid = '1.2.246.562.10.27969391462' where jarjestoid = 12222;
update jarjesto set oid = '1.2.246.562.10.86227695992' where jarjestoid = 12111;
update jarjesto set oid = '1.2.246.562.10.97699008562' where jarjestoid = 13603;
update jarjesto set oid = '1.2.246.562.10.21275022456' where jarjestoid = 256;
update jarjesto set oid = '1.2.246.562.10.41475601821' where jarjestoid = 213;
update jarjesto set oid = '1.2.246.562.10.66861450942' where jarjestoid = 12163;
update jarjesto set oid = '1.2.246.562.10.13009398505' where jarjestoid = 46;
update jarjesto set oid = '1.2.246.562.10.22275186923' where jarjestoid = 275;
update jarjesto set oid = '1.2.246.562.10.89314880233' where jarjestoid = 13582;
update jarjesto set oid = '1.2.246.562.10.28194798254' where jarjestoid = 455;
update jarjesto set oid = '1.2.246.562.10.91279745589' where jarjestoid = 12181;
update jarjesto set oid = '1.2.246.562.10.65328756245' where jarjestoid = 123;
update jarjesto set oid = '1.2.246.562.10.61029456643' where jarjestoid = 13541;
update jarjesto set oid = '1.2.246.562.10.32917458950' where jarjestoid = 4;
update jarjesto set oid = '1.2.246.562.10.53351144976' where jarjestoid = 12110;
update jarjesto set oid = '1.2.246.562.10.89753260201' where jarjestoid = 12382;
update jarjesto set oid = '1.2.246.562.10.40256206337' where jarjestoid = 12541;
update jarjesto set oid = '1.2.246.562.10.20817456291' where jarjestoid = 155;
update jarjesto set oid = '1.2.246.562.10.50252235381' where jarjestoid = 12461;
update jarjesto set oid = '1.2.246.562.10.64191728270' where jarjestoid = 6;
update jarjesto set oid = '1.2.246.562.10.41637436197' where jarjestoid = 13542;
update jarjesto set oid = '1.2.246.562.10.86660025615' where jarjestoid = 13221;
update jarjesto set oid = '1.2.246.562.10.74662228676' where jarjestoid = 13604;
update jarjesto set oid = '1.2.246.562.10.76954956828' where jarjestoid = 12261;
update jarjesto set oid = '1.2.246.562.10.22646723066' where jarjestoid = 400;
update jarjesto set oid = '1.2.246.562.10.26993879039' where jarjestoid = 13103;
update jarjesto set oid = '1.2.246.562.10.94450649496' where jarjestoid = 13023;
update jarjesto set oid = '1.2.246.562.10.17345177902' where jarjestoid = 13586;
update jarjesto set oid = '1.2.246.562.10.20847291171' where jarjestoid = 13381;
update jarjesto set oid = '1.2.246.562.10.49617070819' where jarjestoid = 54;
update jarjesto set oid = '1.2.246.562.10.92169156949' where jarjestoid = 12243;
update jarjesto set oid = '1.2.246.562.10.54271620850' where jarjestoid = 317;
update jarjesto set oid = '1.2.246.562.10.99397066029' where jarjestoid = 13027;

insert into organisaatiopalvelu_log default values;
