CREATE OR REPLACE FUNCTION syntymapvm(suorittaja)
  RETURNS DATE AS
$$
SELECT
  case when substring($1.hetu, 7, 1) = '-'
    then to_date(substring($1.hetu, 1, 4) || '19' || substring($1.hetu, 5,2), 'DDMMYYYY') 
    else to_date(substring($1.hetu, 1, 4) || '20' || substring($1.hetu, 5,2), 'DDMMYYYY')
  end;
$$ LANGUAGE SQL STABLE;

CREATE OR REPLACE FUNCTION sukupuoli(suorittaja)
  RETURNS VARCHAR AS
$$
SELECT
  case when substring($1.hetu, 10, 1) in ('1', '3','5','7','9')
   then 'M' else 'N'
   end;
$$ LANGUAGE SQL STABLE;