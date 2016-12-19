set session aitu.kayttaja='JARJESTELMA';

-- Vaihdetaan hetun loppuosa. Tarkistusnumero ei ole oikein.
-- Ensimmäiset 65536 riviä eivät ole ongelma, mutta sen jälkeen on mahdollista että tulee kaksi samaa hetua jos käy huono tuuri.
update suorittaja set hetu = left(hetu, 7) || lpad(to_hex(suorittaja_id),4,'X') where suorittaja_id > 0;

-- Ylikirjoitetaan henkilöiden nimet. Samat nimet voivat toistua, mutta niin myös reaalimaailmassa.
-- over() -> ei ole stabiili, ts. järjestys ei ole nyt eri ajokerroilla aina sama. 
with 
  etunimi (e) as (values ('Pekka'), ('Jorma'), ('Pirkko'),('Jalmari')
  ,('Iisakki'),('Virve'),('Irmeli'),('Kyllikki'),('Jere'),('Aapeli'),('George'),('Birgit')
  ,('Jesse'),('Orvokki'),('Kaunokki'),('Otso'), ('Vieno'), ('Isabel'), ('Joona')
  ),
  kirjain (k) as (values ('A.'),('E.'),('H.'),('I.'), ('T.'),('O.'),('J.'),('V.'),('K.'),('L.')),
  sukunimi (s) as (values ('Jormakka'), ('Pekkala'),('Gyllendahl'),('Hakkarainen'),('Pikkarainen')
  ,('Mähönen'),('Perälä'),('Kukkamaa'),('Kekkonen')
  ,('Juurivuo'),('Möttönen'),('Korhonen'),('JokuTosiPitkäNimiJotaEiOlekaanOlemassa-Yhdistelmä'),('Silvennoinen'),('Jansson')),
  nimet as (select 
  row_number() over() as id, e,k,s from etunimi cross join 
  kirjain cross join
  sukunimi)
update suorittaja set etunimi = e || ' ' || k,
  sukunimi = s
from nimet 
  where nimet.id = suorittaja.suorittaja_id % 2850   -- 2850 on with-lauseen generoima rivimäärä
  and suorittaja.suorittaja_id > 0;