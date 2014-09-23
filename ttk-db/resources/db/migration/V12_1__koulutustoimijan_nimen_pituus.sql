-- OPH-789 kanta sallii nimiä joiden pituus on nolla.
ALTER TABLE koulutustoimija ADD CONSTRAINT nimi_ei_tyhja CHECK (length(nimi_fi) > 0);

