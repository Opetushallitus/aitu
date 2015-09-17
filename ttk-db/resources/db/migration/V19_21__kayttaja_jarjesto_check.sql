alter table kayttaja add constraint rooli_jarjesto_vaatii_jarjeston check (rooli <> 'JARJESTO' OR jarjesto IS NOT NULL);
