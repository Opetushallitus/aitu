
-- Merkitään vanhat opintoalat ja koulutusalat päättyneiksi
update opintoala set voimassa_loppupvm = to_date('01-07-2017', 'DD-MM-YYYY');
update koulutusala set voimassa_loppupvm = to_date('01-07-2017', 'DD-MM-YYYY');

