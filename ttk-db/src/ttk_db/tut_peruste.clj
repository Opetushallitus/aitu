(ns ttk-db.tut-peruste)

; pohjana Excel-taulukko  http://www.oph.fi/download/158907_Peruslista_10_7_14_www.xlsx
; per rivi käytetty kaavaa =CONCATENATE("""";I309;""" {:peruste-diaari """;L309;"""}")
(def tut-perusteet
 {
"354301" {:peruste-diaari "7/011/2009" :alkupvm "2009-04-01"}
"039996" {:peruste-diaari "15/011/2010" :alkupvm "2010-08-01"}
"355201" {:peruste-diaari "26/011/2006" :alkupvm "2006-06-01"}
"364308" {:peruste-diaari "68/011/2002" :alkupvm "2003-02-01"}
"324101" {:peruste-diaari "50/011/2012" :alkupvm "2013-01-01"}
"327101" {:peruste-diaari "51/011/2012" :alkupvm "2013-01-01"}
"334117" {:peruste-diaari "48/011/2010" :alkupvm "2010-09-01"}
"384201" {:peruste-diaari "42/011/2006" :alkupvm "2006-12-01"}
"324601" {:peruste-diaari "41/011/2005" :alkupvm "2006-01-01"}
"327302" {:peruste-diaari "42/011/2005" :alkupvm "2006-01-01"}
"321602" {:peruste-diaari "32/011/2010" :alkupvm "2010-08-01"}
"351301" {:peruste-diaari "30/011/2009" :alkupvm "2009-08-01"}
"337108" {:peruste-diaari "39/011/2001" :alkupvm "2001-07-01"}
"357305" {:peruste-diaari "40/011/2001" :alkupvm "2001-07-01"}
"354302" {:peruste-diaari "52/011/2002" :alkupvm "2002-10-15"}
"357301" {:peruste-diaari "53/011/2002" :alkupvm "2002-10-15"}
"357302" {:peruste-diaari "54/011/2002" :alkupvm "2002-10-15"}
"354307" {:peruste-diaari "55/011/2002" :alkupvm "2002-10-15"}
"354401" {:peruste-diaari "3/011/2013" :alkupvm "2013-06-01"}
"357401" {:peruste-diaari "9/011/2008" :alkupvm "2008-05-01"}
"357304" {:peruste-diaari "38/011/2001" :alkupvm "2001-07-01"}
"334101" {:peruste-diaari "66/011/2002" :alkupvm "2003-01-01"}
"387101" {:peruste-diaari "40/011/2004" :alkupvm "2005-01-01"}
"364304" {:peruste-diaari "3/011/2008" :alkupvm "2008-03-01"}
"387102" {:peruste-diaari "44/011/2005" :alkupvm "2005-12-15"}
"355505" {:peruste-diaari "47/011/2009" :alkupvm "2010-01-01"}
"354402" {:peruste-diaari "50/011/2001" :alkupvm "2001-12-01"}
"357402" {:peruste-diaari "51/011/2001" :alkupvm "2001-12-01"}
"352101" {:peruste-diaari "20/011/2009" :alkupvm "2009-08-01"}
"355104" {:peruste-diaari "36/011/2013" :alkupvm "2014-01-01"}
"358103" {:peruste-diaari "48/011/2004" :alkupvm "2005-01-01"}
"355105" {:peruste-diaari "33/011/2013" :alkupvm "2014-01-01"}
"364902" {:peruste-diaari "40/011/2012" :alkupvm "2012-12-01"}
"364904" {:peruste-diaari "25/011/2012" :alkupvm "2012-08-01"}
"358508" {:peruste-diaari "41/011/2006" :alkupvm "2006-12-01"}
"367201" {:peruste-diaari "33/011/2006" :alkupvm "2006-08-01"}
"364201" {:peruste-diaari "10/011/2013" :alkupvm "2013-08-01"}
"367904" {:peruste-diaari "15/011/2007" :alkupvm "2007-08-01"}
"364901" {:peruste-diaari "40/011/2006" :alkupvm "2006-12-01"}
"371109" {:peruste-diaari "12/011/2010" :alkupvm "2010-08-01"}
"354312" {:peruste-diaari "48/011/2006" :alkupvm "2007-01-01"}
"361104" {:peruste-diaari "24/011/2009" :alkupvm "2009-08-01"}
"364101" {:peruste-diaari "34/011/2012" :alkupvm "2012-10-01"}
"374111" {:peruste-diaari "30/011/2011" :alkupvm "2012-01-01"}
"377101" {:peruste-diaari "47/011/2012" :alkupvm "2013-01-01"}
"354111" {:peruste-diaari "21/011/2013" :alkupvm "2013-08-01"}
"357101" {:peruste-diaari "44/011/2004" :alkupvm "2005-01-01"}
"354101" {:peruste-diaari "46/011/2005" :alkupvm "2006-01-01"}
"384301" {:peruste-diaari "12/011/2012" :alkupvm "2012-06-01"}
"387304" {:peruste-diaari "42/011/2012" :alkupvm "2012-12-01"}
"381303" {:peruste-diaari "27/011/2009" :alkupvm "2009-08-01"}
"381112" {:peruste-diaari "3/011/2010" :alkupvm "2010-08-01"}
"384101" {:peruste-diaari "13/011/2011" :alkupvm "2011-09-01"}
"354201" {:peruste-diaari "29/011/2012" :alkupvm "2012-08-01"}
"357201" {:peruste-diaari "19/011/2011" :alkupvm "2011-09-01"}
"354212" {:peruste-diaari "27/011/2012" :alkupvm "2012-08-01"}
"334103" {:peruste-diaari "8/011/2009" :alkupvm "2009-04-01"}
"355411" {:peruste-diaari "38/011/2004" :alkupvm "2005-01-01"}
"358410" {:peruste-diaari "37/011/2004" :alkupvm "2005-01-01"}
"374117" {:peruste-diaari "61/011/2010" :alkupvm "2011-01-01"}
"337101" {:peruste-diaari "15/011/2011" :alkupvm "2011-09-01"}
"355501" {:peruste-diaari "48/011/2009" :alkupvm "2010-01-01"}
"351107" {:peruste-diaari "28/011/2009" :alkupvm "2009-08-01"}
"354113" {:peruste-diaari "55/011/2010" :alkupvm "2011-01-01"}
"364401" {:peruste-diaari "19/011/2008" :alkupvm "2008-09-01"}
"364402" {:peruste-diaari "54/011/2009" :alkupvm "2010-01-01"}
"364403" {:peruste-diaari "26/011/2005" :alkupvm "2005-11-01"}
"361401" {:peruste-diaari "22/011/2010" :alkupvm "2010-08-01"}
"354202" {:peruste-diaari "30/011/2008" :alkupvm "2008-10-01"}
"357203" {:peruste-diaari "47/011/2005" :alkupvm "2006-01-01"}
"387303" {:peruste-diaari "31/011/2011" :alkupvm "2012-01-01"}
"381304" {:peruste-diaari "26/011/2009" :alkupvm "2009-08-01"}
"337102" {:peruste-diaari "40/011/2013" :alkupvm "2014-05-01"}
"374122" {:peruste-diaari "49/011/2009" :alkupvm "2009-10-01"}
"377108" {:peruste-diaari "28/011/2010" :alkupvm "2010-04-01"}
"351106" {:peruste-diaari "27/011/2010" :alkupvm "2010-08-01"}
"354601" {:peruste-diaari "31/011/2002" :alkupvm "2002-08-01"}
"357601" {:peruste-diaari "24/011/2003" :alkupvm "2003-06-01"}
"364103" {:peruste-diaari "25/011/2006" :alkupvm "2006-07-01"}
"324104" {:peruste-diaari "73/011/1996" :alkupvm "1996-09-01"}
"327104" {:peruste-diaari "52/011/97" :alkupvm "1998-01-01"}
"354203" {:peruste-diaari "18/011/2013" :alkupvm "2013-08-01"}
"357204" {:peruste-diaari "19/011/2013" :alkupvm "2013-08-01"}
"334118" {:peruste-diaari "3/011/2011" :alkupvm "2011-03-01"}
"351204" {:peruste-diaari "7/011/2010" :alkupvm "2010-08-01"}
"374123" {:peruste-diaari "23/011/2011" :alkupvm "2011-09-01"}
"377103" {:peruste-diaari "46/011/2012" :alkupvm "2013-01-01"}
"358502" {:peruste-diaari "14/011/2005" :alkupvm "2005-08-01"}
"355502" {:peruste-diaari "26/011/2002" :alkupvm "2002-08-01"}
"354409" {:peruste-diaari "39/011/2009" :alkupvm "2009-09-01"}
"354102" {:peruste-diaari " 17/011/2013" :alkupvm "2013-09-01"}
"324105" {:peruste-diaari "67/011/1996" :alkupvm "1996-08-01"}
"327105" {:peruste-diaari "54/011/1997" :alkupvm "1998-01-01"}
"367905" {:peruste-diaari "51/011/2002" :alkupvm "2002-11-15"}
"358101" {:peruste-diaari "12/011/2008" :alkupvm "2008-04-01"}
"355101" {:peruste-diaari "10/011/2008" :alkupvm "2008-04-01"}
"351101" {:peruste-diaari "39/011/2010" :alkupvm "2010-08-01"}
"357102" {:peruste-diaari "23/011/2004" :alkupvm "2004-08-01"}
"354103" {:peruste-diaari "46/011/2003" :alkupvm "2004-01-01"}
"357103" {:peruste-diaari "61/011/2001" :alkupvm "2002-02-01"}
"354104" {:peruste-diaari "49/011/2012" :alkupvm "2013-02-01"}
"358503" {:peruste-diaari "45/011/2004" :alkupvm "2004-12-15"}
"354801" {:peruste-diaari "9/011/2003" :alkupvm "2003-02-01"}
"354403" {:peruste-diaari "16/011/2004" :alkupvm "2004-08-01"}
"038411" {:peruste-diaari "20/011/2010" :alkupvm "2010-08-01"}
"381113" {:peruste-diaari "25/011/2010" :alkupvm "2010-08-01"}
"384114" {:peruste-diaari "11/011/2013" :alkupvm "2013-08-01"}
"374124" {:peruste-diaari "63/011/2010" :alkupvm "2011-01-01"}
"377106" {:peruste-diaari "32/011/2011" :alkupvm "2012-01-01"}
"324107" {:peruste-diaari "35/011/2008" :alkupvm "2008-12-15"}
"327107" {:peruste-diaari "34/011/2008" :alkupvm "2008-12-15"}
"354602" {:peruste-diaari "34/011/2005" :alkupvm "2005-12-01"}
"354115" {:peruste-diaari "56/011/2010" :alkupvm "2011-01-01"}
"357109" {:peruste-diaari "57/011/2010" :alkupvm "2011-01-01"}
"321301" {:peruste-diaari "13/011/2010" :alkupvm "2010-08-01"}
"354205" {:peruste-diaari "21/011/2011" :alkupvm "2011-09-01"}
"357207" {:peruste-diaari "7/011/2014" :alkupvm "2014-05-01"}
"321101" {:peruste-diaari "25/011/2009" :alkupvm "2009-08-01"}
"327128" {:peruste-diaari "34/011/2010" :alkupvm "2010-05-01"}
"324128" {:peruste-diaari "19/011/2007" :alkupvm "2007-07-01"}
"351603" {:peruste-diaari "19/011/2009" :alkupvm "2009-08-01"}
"384103" {:peruste-diaari "12/011/2013" :alkupvm "2013-08-01"}
"357111" {:peruste-diaari "13/011/2013" :alkupvm "2013-07-01"}
"354114" {:peruste-diaari "16/011/2005" :alkupvm "2005-08-01"}
"357405" {:peruste-diaari "37/011/2013" :alkupvm "2014-01-01"}
"381204" {:peruste-diaari "18/011/2009" :alkupvm "2009-08-01"}
"354605" {:peruste-diaari "31/011/2001" :alkupvm "2001-07-01"}
"374118" {:peruste-diaari "7/011/2012" :alkupvm "2012-04-01"}
"384401" {:peruste-diaari "38/011/2005" :alkupvm "2006-01-01"}
"387401" {:peruste-diaari "40/011/2009" :alkupvm "2009-09-01"}
"357803" {:peruste-diaari "25/011/2003" :alkupvm "2003-07-01"}
"354802" {:peruste-diaari "12/011/2003" :alkupvm "2003-02-01"}
"355410" {:peruste-diaari "4/011/2002" :alkupvm "2002-03-01"}
"358408" {:peruste-diaari "5/011/2002" :alkupvm "2002-03-01"}
"355108" {:peruste-diaari "25/011/2004" :alkupvm "2004-09-01"}
"358102" {:peruste-diaari "13/011/2008" :alkupvm "2008-04-01"}
"355102" {:peruste-diaari "11/011/2008" :alkupvm "2008-04-01"}
"381410" {:peruste-diaari "26/011/2010" :alkupvm "2010-08-01"}
"384405" {:peruste-diaari "16/011/2009" :alkupvm "2009-06-01"}
"351307" {:peruste-diaari "38/011/2010" :alkupvm "2010-08-01"}
"354702" {:peruste-diaari "28/011/2008" :alkupvm "2008-10-01"}
"357702" {:peruste-diaari "33/011/2008" :alkupvm "2009-01-01"}
"354105" {:peruste-diaari "20/011/2007" :alkupvm "2007-09-01"}
"357104" {:peruste-diaari "6/011/2006" :alkupvm "2006-04-01"}
"355103" {:peruste-diaari "41/011/2012" :alkupvm "2013-01-01"}
"355110" {:peruste-diaari "4/011/2011" :alkupvm "2011-04-01"}
"355106" {:peruste-diaari "26/011/2013" :alkupvm "2014-01-01"}
"357303" {:peruste-diaari "12/011/2009" :alkupvm "2009-04-01"}
"317101" {:peruste-diaari "8/011/2014" :alkupvm "2014-04-01"}
"331101" {:peruste-diaari "34/011/2009" :alkupvm "2009-08-01"}
"384204" {:peruste-diaari "2/011/2014" :alkupvm "2014-04-01"}
"381203" {:peruste-diaari "21/011/2010" :alkupvm "2010-08-01"}
"387201" {:peruste-diaari "47/011/2006" :alkupvm "2007-01-01"}
"384202" {:peruste-diaari "49/011/2006" :alkupvm "2007-01-01"}
"384402" {:peruste-diaari "16/011/2008" :alkupvm "2008-04-01"}
"381408" {:peruste-diaari "32/011/2009" :alkupvm "2009-08-01"}
"357105" {:peruste-diaari "17/011/2005" :alkupvm "2005-08-01"}
"354106" {:peruste-diaari "3/011/2005" :alkupvm "2005-05-01"}
"324110" {:peruste-diaari "45/011/2006" :alkupvm "2006-12-15"}
"364906" {:peruste-diaari "27/011/2013" :alkupvm "2013-11-01"}
"367901" {:peruste-diaari "121/011/1995" :alkupvm "1995-08-01"}
"361902" {:peruste-diaari "31/011/2009" :alkupvm "2009-08-01"}
"367301" {:peruste-diaari "25/011/97" :alkupvm "1997-08-01"}
"354404" {:peruste-diaari "28/011/2007" :alkupvm "2008-01-01"}
"371110" {:peruste-diaari "19/011/2010" :alkupvm "2010-08-01"}
"039998" {:peruste-diaari "7/011/2008" :alkupvm "2008-08-01"}
"357802" {:peruste-diaari "14/011/2003" :alkupvm "2003-02-01"}
"354803" {:peruste-diaari "34/011/2013" :alkupvm "2013-11-01"}
"355301" {:peruste-diaari "28/011/2012" :alkupvm "2012-08-01"}
"352301" {:peruste-diaari "5/011/2010" :alkupvm "2010-08-01"}
"355210" {:peruste-diaari "55/011/2012" :alkupvm "2013-03-01"}
"358206" {:peruste-diaari "28/011/2013" :alkupvm "2013-11-01"}
"367903" {:peruste-diaari "16/011/2011" :alkupvm "2011-09-01"}
"367102" {:peruste-diaari "8/011/2004" :alkupvm "2004-04-01"}
"364905" {:peruste-diaari "50/011/2004" :alkupvm "2005-02-01"}
"361101" {:peruste-diaari "29/011/2009" :alkupvm "2009-08-01"}
"354311" {:peruste-diaari "18/011/2008" :alkupvm "2008-09-01"}
"355107" {:peruste-diaari "59/011/2010" :alkupvm "2011-02-01"}
"387103" {:peruste-diaari "23/011/2012" :alkupvm "2012-08-01"}
"334104" {:peruste-diaari "52/011/2009" :alkupvm "2010-01-01"}
"337110" {:peruste-diaari "41/011/2013" :alkupvm "2014-04-01"}
"381106" {:peruste-diaari "21/011/2009" :alkupvm "2009-08-01"}
"384111" {:peruste-diaari "29/011/2006" :alkupvm "2006-08-01"}
"384112" {:peruste-diaari "12/011/2011" :alkupvm "2011-09-01"}
"384110" {:peruste-diaari "14/011/2011" :alkupvm "2011-09-01"}
"364109" {:peruste-diaari "26/011/2004" :alkupvm "2004-10-01"}
"355109" {:peruste-diaari "23/011/2010" :alkupvm "2010-04-01"}
"381402" {:peruste-diaari "36/011/2010" :alkupvm "2010-08-01"}
"354110" {:peruste-diaari "23/011/2003" :alkupvm "2003-06-01"}
"364301" {:peruste-diaari "7/011/2013" :alkupvm "2013-07-01"}
"364305" {:peruste-diaari "43/011/2012" :alkupvm "2012-12-01"}
"367302" {:peruste-diaari "17/011/2004" :alkupvm "2004-06-01"}
"361301" {:peruste-diaari "36/011/2009" :alkupvm "2009-08-01"}
"364302" {:peruste-diaari "46/011/2006" :alkupvm "2007-01-01"}
"355905" {:peruste-diaari "45/011/2012" :alkupvm "2012-12-01"}
"352902" {:peruste-diaari "1/011/2010" :alkupvm "2010-08-01"}
"354603" {:peruste-diaari "29/011/2013" :alkupvm "2014-01-01"}
"357603" {:peruste-diaari "30/011/2013" :alkupvm "2014-01-01"}
"321204" {:peruste-diaari "30/011/2010" :alkupvm "2010-08-01"}
"334105" {:peruste-diaari "11/011/2012" :alkupvm "2012-04-01"}
"358409" {:peruste-diaari "49/011/2004" :alkupvm "2005-01-01"}
"357205" {:peruste-diaari "24/011/2002" :alkupvm "2002-08-01"}
"354206" {:peruste-diaari "25/011/2002" :alkupvm "2002-08-01"}
"381201" {:peruste-diaari "17/011/2009" :alkupvm "2009-08-01"}
"377109" {:peruste-diaari "18/011/2012" :alkupvm "2012-05-01"}
"374121" {:peruste-diaari "44/011/2009" :alkupvm "2009-10-01"}
"387203" {:peruste-diaari "14/011/2013" :alkupvm "2013-08-01"}
"374119" {:peruste-diaari "9/011/2004" :alkupvm "2004-04-01"}
"358504" {:peruste-diaari "46/011/2004" :alkupvm "2004-12-15"}
"355503" {:peruste-diaari "46/011/2009" :alkupvm "2010-01-01"}
"355504" {:peruste-diaari "45/011/2009" :alkupvm "2010-01-01"}
"352503" {:peruste-diaari "31/011/2010" :alkupvm "2010-08-01"}
"354604" {:peruste-diaari "13/011/2007" :alkupvm "2007-08-01"}
"357602" {:peruste-diaari "13/011/2007" :alkupvm "2007-08-01"}
"374114" {:peruste-diaari "15/011/2013" :alkupvm "2013-09-01"}
"354309" {:peruste-diaari "19/011/2004" :alkupvm "2004-07-01"}
"351805" {:peruste-diaari "37/011/2010" :alkupvm "2010-08-01"}
"357801" {:peruste-diaari "11/011/2003" :alkupvm "2003-02-01"}
"364105" {:peruste-diaari "24/011/2007" :alkupvm "2007-11-01"}
"351605" {:peruste-diaari "4/011/2010" :alkupvm "2010-08-01"}
"377104" {:peruste-diaari "43/011/2009" :alkupvm "2009-10-01"}
"377102" {:peruste-diaari "60/011/2010" :alkupvm "2011-01-01"}
"367203" {:peruste-diaari "51/011/2000" :alkupvm "2000-08-14"}
"364202" {:peruste-diaari "15/011/2005" :alkupvm "2005-07-01"}
"354207" {:peruste-diaari "26/011/2012" :alkupvm "2012-08-01"}
"357206" {:peruste-diaari "20/011/2011" :alkupvm "2011-09-01"}
"351701" {:peruste-diaari "33/011/2010" :alkupvm "2010-08-01"}
"367304" {:peruste-diaari "8/011/2013" :alkupvm "2013-07-01"}
"354703" {:peruste-diaari "36/011/2008" :alkupvm "2009-01-15"}
"357709" {:peruste-diaari "41/0112004" :alkupvm "2005-01-01"}
"361201" {:peruste-diaari "2/011/2010" :alkupvm "2010-08-01"}
"364307" {:peruste-diaari "27/011/2008" :alkupvm "2008-10-01"}
"374115" {:peruste-diaari "2/011/2013" :alkupvm "2013-05-01"}
"334115" {:peruste-diaari "10/011/2010" :alkupvm "2010-02-01"}
"352201" {:peruste-diaari "35/011/2009" :alkupvm "2009-08-01"}
"358204" {:peruste-diaari "43/011/2010" :alkupvm "2010-08-01"}
"357110" {:peruste-diaari "26/011/2007" :alkupvm "2008-01-01"}
"354107" {:peruste-diaari "27/011/2007" :alkupvm "2008-01-01"}
"355208" {:peruste-diaari "54/011/2004" :alkupvm "2005-01-01"}
"355211" {:peruste-diaari "18/011/2011" :alkupvm "2011-09-01"}
"354310" {:peruste-diaari "21/011/2008" :alkupvm "2008-06-01"}
"364108" {:peruste-diaari "30/011/2012" :alkupvm "2012-08-01"}
"367103" {:peruste-diaari "28/011/2005" :alkupvm "2005-11-01"}
"355903" {:peruste-diaari "21/011/2007" :alkupvm "2007-09-01"}
"384109" {:peruste-diaari "25/011/2011" :alkupvm "2012-01-01"}
"354314" {:peruste-diaari "37/011/2012" :alkupvm "1.10.2012"}
"324115" {:peruste-diaari "36/011/2011" :alkupvm "2012-01-01"}
"327114" {:peruste-diaari "35/011/2011" :alkupvm "2012-01-01"}
"367902" {:peruste-diaari "53/011/2009" :alkupvm "2010-01-01"}
"324116" {:peruste-diaari "6/011/2013" :alkupvm "2013-05-01"}
"327115" {:peruste-diaari "52/011/2010" :alkupvm "2011-01-01"}
"358505" {:peruste-diaari "46/011/2004" :alkupvm "2004-12-15"}
"337104" {:peruste-diaari "36/011/2004" :alkupvm "2005-01-01"}
"324201" {:peruste-diaari "13/011/2009" :alkupvm "2009-06-01"}
"324117" {:peruste-diaari "27/011/2006" :alkupvm "2006-08-15"}
"327116" {:peruste-diaari "28/011/2006" :alkupvm "2006-08-15"}
"354705" {:peruste-diaari "24/011/2008" :alkupvm "2008-10-01"}
"357705" {:peruste-diaari "25/011/2008" :alkupvm "2008-10-01"}
"364106" {:peruste-diaari "14/011/2014" :alkupvm "2014-09-01"}
"324119" {:peruste-diaari "1/011/2007" :alkupvm "2007-03-01"}
"327118" {:peruste-diaari "2/011/2007" :alkupvm "2007-03-01"}
"334106" {:peruste-diaari "19/011/2012" :alkupvm "2012-08-01"}
"387105" {:peruste-diaari "37/011/2005" :alkupvm "2005-11-01"}
"387104" {:peruste-diaari "36/011/2005" :alkupvm "2005-11-01"}
"321902" {:peruste-diaari "24/011/2010" :alkupvm "2010-08-01"}
"324129" {:peruste-diaari "38/011/2013" :alkupvm "2014-01-01"}
"327127" {:peruste-diaari "26/011/2008" :alkupvm "2008-09-15"}
"358506" {:peruste-diaari "47/011/2004" :alkupvm "2004-12-15"}
"324120" {:peruste-diaari "32/011/2007" :alkupvm "2008-01-01"}
"327119" {:peruste-diaari "33/011/2007" :alkupvm "2008-01-01"}
"371101" {:peruste-diaari "17/011/2010" :alkupvm "2010-08-01"}
"384203" {:peruste-diaari "8/011/2012" :alkupvm "2012-04-01"}
"352903" {:peruste-diaari "8/011/2010" :alkupvm "2010-08-01"}
"355901" {:peruste-diaari "2/011/2008" :alkupvm "2008-03-01"}
"384108" {:peruste-diaari "26/011/2011" :alkupvm "2012-01-01"}
"351407" {:peruste-diaari "23/011/2009" :alkupvm "2009-08-01"}
"354405" {:peruste-diaari "54/011/2010" :alkupvm "2011-01-01"}
"354407" {:peruste-diaari "22/011/2013" :alkupvm "2013-09-01"}
"357403" {:peruste-diaari "5/011/2011" :alkupvm "2011-03-01"}
"354406" {:peruste-diaari "17/011/2012" :alkupvm "2012-06-01"}
"357404" {:peruste-diaari "53/011/2010" :alkupvm "2011-01-01"}
"367104" {:peruste-diaari "21/011/2005" :alkupvm "2005-10-01"}
"355209" {:peruste-diaari "30/011/2007" :alkupvm "2008-01-01"}
"358205" {:peruste-diaari "2/011/2011" :alkupvm "2011-04-01"}
"351203" {:peruste-diaari "35/011/2010" :alkupvm "2010-08-01"}
"334114" {:peruste-diaari "10/011/2012" :alkupvm "2012-04-01"}
"337109" {:peruste-diaari "29/011/2007" :alkupvm "2008-01-01"}
"321501" {:peruste-diaari "29/011/2010" :alkupvm "2010-08-01"}
"367101" {:peruste-diaari "38/011/2006" :alkupvm "2006-10-15"}
"384106" {:peruste-diaari "39/011/2004" :alkupvm "2005-01-01"}
"324502" {:peruste-diaari "18/011/2005" :alkupvm "2005-07-01"}
"327503" {:peruste-diaari "19/011/2005" :alkupvm "2005-07-01"}
"358901" {:peruste-diaari "5/011/2009" :alkupvm "2009-05-01"}
"354209" {:peruste-diaari "20/011/2008" :alkupvm "2008-08-01"}
"352401" {:peruste-diaari "6/011/2009" :alkupvm "2009-08-01"}
"355412" {:peruste-diaari "22/011/2011" :alkupvm "2011-09-01"}
"358411" {:peruste-diaari "2/011/2005" :alkupvm "2005-04-01"}
"384113" {:peruste-diaari "58/011/2000" :alkupvm "2000-09-01"}
"354804" {:peruste-diaari "10/011/2003" :alkupvm "2003-02-01"}
"354211" {:peruste-diaari "61/011/2000" :alkupvm "2000-11-01"}
"334113" {:peruste-diaari "3/011/2014" :alkupvm "2014-08-01"}
"351502" {:peruste-diaari "22/011/2009" :alkupvm "2009-08-01"}
"344101" {:peruste-diaari "58/011/2010" :alkupvm "2011-01-01"}
"347101" {:peruste-diaari "17/011/2011" :alkupvm "2011-09-01"}
"341101" {:peruste-diaari "16/011/2010" :alkupvm "2010-08-01"}
"354501" {:peruste-diaari "32/011/2006" :alkupvm "2006-08-01"}
"357501" {:peruste-diaari "31/011/2006" :alkupvm "2006-08-01"}
"354502" {:peruste-diaari "30/011/2005" :alkupvm "2005-12-01"}
"357502" {:peruste-diaari "31/011/2005" :alkupvm "2005-12-01"}
"334119" {:peruste-diaari "55/011/2009" :alkupvm "2010-01-01"}
"364102" {:peruste-diaari "31/011/2012" :alkupvm "2012-08-01"}
"358902" {:peruste-diaari "35/011/2007" :alkupvm "2008-01-01"}
"358405" {:peruste-diaari "29/011/2002" :alkupvm "2002-06-01"}
"355407" {:peruste-diaari "28/011/2002" :alkupvm "2002-06-01"}
"381504" {:peruste-diaari "18/011/2010" :alkupvm "2010-08-01"}
"387501" {:peruste-diaari "21/011/2003" :alkupvm "2003-06-01"}
"355906" {:peruste-diaari "15/011/2014" :alkupvm "2014-08-01"}
"377110" {:peruste-diaari "33/011/2011" :alkupvm "2012-01-01"}
"357106" {:peruste-diaari "10/011/2007" :alkupvm "2007-08-01"}
"354112" {:peruste-diaari "13/011/2002" :alkupvm "2002-04-01"}
"334102" {:peruste-diaari "39/011/2012" :alkupvm "2013-01-01"}
"337106" {:peruste-diaari "22/011/2005" :alkupvm "2005-10-01"}
"355413" {:peruste-diaari "5/011/2005" :alkupvm "2005-04-01"}
"358412" {:peruste-diaari "4/011/2005" :alkupvm "2005-04-01"}
"357107" {:peruste-diaari "11/011/2006" :alkupvm "2006-05-01"}
"354108" {:peruste-diaari "9/011/2006" :alkupvm "2006-05-01"}
"384205" {:peruste-diaari "15/011/2006" :alkupvm "2007-01-01"}
"387202" {:peruste-diaari "50/011/2006" :alkupvm "2007-01-01"}
"324301" {:peruste-diaari "12/011/2006" :alkupvm "2007-01-01"}
"327301" {:peruste-diaari "51/011/2006" :alkupvm "2007-01-01"}
"357108" {:peruste-diaari "5/011/2006" :alkupvm "2006-04-01"}
"354109" {:peruste-diaari "4/011/2006" :alkupvm "2006-04-01"}
"039999" {:peruste-diaari "9/011/2010" :alkupvm "2010-08-01"}
"377105" {:peruste-diaari "16/011/2013" :alkupvm "2013-06-01"}
"334108" {:peruste-diaari "65/011/2002" :alkupvm "2003-01-01"}
"334109" {:peruste-diaari "30/011/2006" :alkupvm "2006-08-01"}
"357306" {:peruste-diaari "20/011/2005" :alkupvm "2005-07-01"}
"384501" {:peruste-diaari "44/011/2006" :alkupvm "2007-01-01"}
"351703" {:peruste-diaari "33/011/2009" :alkupvm "2009-08-01"}
"354708" {:peruste-diaari "53/011/2001" :alkupvm "2001-12-01"}
"357707" {:peruste-diaari "53/011/2001" :alkupvm "2001-12-01"}
"357708" {:peruste-diaari "28/011/2011" :alkupvm "2012-01-01"}
"354709" {:peruste-diaari "27/011/2011" :alkupvm "2012-01-01"}
"351704" {:peruste-diaari "37/011/2009" :alkupvm "2009-08-01"}
"355212" {:peruste-diaari "8/011/2006" :alkupvm "2006-04-01"}
"334116" {:peruste-diaari "9/011/2009" :alkupvm "2009-04-01"}
"337111" {:peruste-diaari "14/011/2008" :alkupvm "2008-04-01"}
"364205" {:peruste-diaari "13/011/2012" :alkupvm "2012-05-01"}
"364203" {:peruste-diaari "19/011/2006" :alkupvm "2006-06-01"}
"321901" {:peruste-diaari "57/011/2009" :alkupvm "2010-08-01"}
"364107" {:peruste-diaari "32/011/2012" :alkupvm "2012-08-01"}
"364204" {:peruste-diaari "11/011/2007" :alkupvm "2007-08-01"}
"334111" {:peruste-diaari "8/011/2011" :alkupvm "2011-05-01"}
"354408" {:peruste-diaari "16/011/2012" :alkupvm "2012-06-01"}
"374113" {:peruste-diaari "29/011/2011" :alkupvm "2012-01-01"}
"377107" {:peruste-diaari "2/011/2006" :alkupvm "2006-03-01"}
"384403" {:peruste-diaari "15/011/2008" :alkupvm "2008-04-01"}
"358904" {:peruste-diaari "54/011/2012" :alkupvm "2013-01-01"}
"355902" {:peruste-diaari "12/011/2007" :alkupvm "2007-08-01"}
"334112" {:peruste-diaari "53/011/2012" :alkupvm "2013-03-01"}
"337107" {:peruste-diaari "4/011/2008" :alkupvm "2008-03-01"}
"337112" {:peruste-diaari "46/011/2010" :alkupvm "2010-09-01"}
})

(defn generoi-sql [perusteet]
  (println "set session aitu.kayttaja='INTEGRAATIO';")
  (println)
  (doseq [[tutkintotunnus {:keys [peruste-diaari alkupvm]}] perusteet]
    (println (str "insert into tutkintoversio (tutkintotunnus, versio, koodistoversio, peruste, hyvaksytty, voimassa_alkupvm, jarjestyskoodistoversio, osajarjestyskoodisto) "
              "  select tutkintotunnus, 2 as versio, koodistoversio, '" peruste-diaari "' as peruste, hyvaksytty, DATE '" alkupvm "', jarjestyskoodistoversio, osajarjestyskoodisto from tutkintoversio "
              "    where tutkintotunnus = '" tutkintotunnus "' and versio = 1 and peruste != '" peruste-diaari "';"))
      (println (str "update tutkintoversio set voimassa_loppupvm = (DATE '" alkupvm "' - INTERVAL '1 day')::date, "
                    "siirtymaajan_loppupvm = (DATE '" alkupvm "' - INTERVAL '1 day' + (case tutkintotaso when 'perustutkinto' then INTERVAL '10 years' else INTERVAL '2 years' end))::date "
                    "from nayttotutkinto where nayttotutkinto.tutkintotunnus = tutkintoversio.tutkintotunnus "
                    " and tutkintoversio.tutkintotunnus='" tutkintotunnus "' and versio=1 and peruste != '" peruste-diaari "';"))
      (println (str "update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio tv where tutkintotunnus = '" tutkintotunnus "' and not exists (select 1 from tutkintoversio where tutkintotunnus = '" tutkintotunnus "' and versio > tv.versio)) where tutkintotunnus = '" tutkintotunnus "';"))
      (println))
  (println (str "update sopimus_ja_tutkinto set tutkintoversio = tv2.tutkintoversio_id "
                    "from sopimus_ja_tutkinto st "
                    "join tutkintoversio tv on st.tutkintoversio = tv.tutkintoversio_id "
                    "join tutkintoversio tv2 on tv.tutkintotunnus = tv2.tutkintotunnus and tv2.versio = 2 "
                    "join jarjestamissopimus on st.jarjestamissopimusid = jarjestamissopimus.jarjestamissopimusid "
                    "where st.sopimus_ja_tutkinto_id = sopimus_ja_tutkinto.sopimus_ja_tutkinto_id "
                    "and jarjestamissopimus.alkupvm >= '2014-01-01'::date;"))
  (println))

(defn generoi-sql-peruutus []
  (println "set session aitu.kayttaja='INTEGRAATIO';")
  (println)
  (doseq [tutv tut-perusteet ]
    (let [tutkinto (first tutv)
          diaarinumero (:peruste-diaari (second tutv))]
      (println (str "update tutkintoversio set voimassa_loppupvm = (select voimassa_loppupvm from tutkintoversio t2 where tutkintotunnus='" tutkinto "' and versio=2) where tutkintotunnus='" tutkinto "' and versio=1;"))
      (println (str "update nayttotutkinto set uusin_versio_id = (select tutkintoversio_id from tutkintoversio where versio = 1 and tutkintotunnus = '" tutkinto "') where tutkintotunnus = '" tutkinto "';"))
      (println)
      )))
