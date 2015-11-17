ALTER TABLE kayttaja ALTER COLUMN uid SET NOT NULL;
CREATE UNIQUE INDEX kayttaja_unique_uid_voimassa ON kayttaja(lower(uid)) WHERE voimassa;
