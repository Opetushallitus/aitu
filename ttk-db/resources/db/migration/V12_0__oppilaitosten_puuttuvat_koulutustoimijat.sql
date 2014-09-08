-- OPH-824

update oppilaitos set koulutustoimija = '9999999-9' where oppilaitoskoodi = '30049';
update oppilaitos set koulutustoimija = '0150622-3' where oppilaitoskoodi = '01776';
update oppilaitos set koulutustoimija = '0150605-3' where oppilaitoskoodi = '01180';
update oppilaitos set koulutustoimija = '8888888-8' where oppilaitoskoodi = '30021';
update oppilaitos set koulutustoimija = '8888888-8' where oppilaitoskoodi = '30028';

-- mystinen "testioppilaitos"
update oppilaitos set koulutustoimija = '8888888-8' where oppilaitoskoodi = '99999';

ALTER TABLE oppilaitos  ALTER COLUMN koulutustoimija SET NOT NULL;