create view toimikuntien_tutkinnot as 
   select tt.toimikunta, tt.tutkintotunnus, t.nimi_fi, t.nimi_sv, t.kielisyys, t.toimikausi_alku, t.toimikausi_loppu 
   from toimikunta_ja_tutkinto tt
   inner join tutkintotoimikunta t on tt.toimikunta = t.tkunta
   where t.toimikausi_loppu >= now();

comment on view toimikuntien_tutkinnot is 'Toimikuntien vastuulla olevat tutkinnot tällä hetkellä.';

alter table suorituskerta
  add column toimikunta character varying(9) references tutkintotoimikunta(tkunta),
  add column tutkintoversio_id int references tutkintoversio(tutkintoversio_id);
  
comment on column suorituskerta.toimikunta is 'Tutkintotoimikunta, jonka vastuulle suoritus kuuluu. Yleensä pääteltävissä tutkinnosta, mutta ei aina.';

CREATE TABLE tutkintosuoritus (
  tutkintosuoritus_id serial NOT NULL,
  tutkintoversio_id int NOT NULL references tutkintoversio(tutkintoversio_id),
  suorittaja_id int NOT NULL references suorittaja(suorittaja_id),
  suoritus_id int references suoritus(suoritus_id),
  muutettu_kayttaja character varying(80) NOT NULL references kayttaja(oid),
  luotu_kayttaja character varying(80) NOT NULL references kayttaja(oid),
  muutettuaika timestamp with time zone NOT NULL,
  luotuaika timestamp with time zone NOT NULL,
  tila character varying(12) NOT NULL DEFAULT 'luonnos'::character varying,
  ehdotusaika timestamp with time zone, -- Aika, jolloin suoritukset on lähetetty tutkintotoimikunnalle käsiteltäväksi.
  hyvaksymisaika timestamp with time zone, -- Aika (kokouspvm), jolloin tutkintotoimikunta on hyväksynyt suoritukset.
  CONSTRAINT tutkintosuoritus_pkey PRIMARY KEY (tutkintosuoritus_id),
  CONSTRAINT tutkintosuoritus_tila_check CHECK (tila::text = ANY (ARRAY['luonnos'::character varying::text, 'ehdotettu'::character varying::text, 'hyvaksytty'::character varying::text]))
);

comment on table tutkintosuoritus is 'Koko tutkinnon suoritukset.';
comment on column tutkintosuoritus.suoritus_id is 'Viimeinen tutkinnon osa, jonka perusteella koko tutkinnon suoritus on todettu valmiiksi.';

CREATE TRIGGER tutkintosuoritus_cu_insert BEFORE INSERT
  ON tutkintosuoritus
  FOR EACH ROW
  EXECUTE PROCEDURE update_creator();
 
CREATE TRIGGER tutkintosuoritus_mu_insert
  BEFORE INSERT
  ON tutkintosuoritus
  FOR EACH ROW
  EXECUTE PROCEDURE update_modifier();
 
CREATE TRIGGER tutkintosuoritus_mu_update
  BEFORE UPDATE
  ON tutkintosuoritus
  FOR EACH ROW
  EXECUTE PROCEDURE update_modifier();
 
CREATE TRIGGER tutkintosuoritus_update
  BEFORE UPDATE
  ON tutkintosuoritus
  FOR EACH ROW
  EXECUTE PROCEDURE update_stamp();
 
CREATE TRIGGER tutkintosuoritusl_insert
  BEFORE INSERT
  ON tutkintosuoritus
  FOR EACH ROW
  EXECUTE PROCEDURE update_created();
 
CREATE TRIGGER tutkintosuoritusm_insert
  BEFORE INSERT
  ON tutkintosuoritus
  FOR EACH ROW
  EXECUTE PROCEDURE update_stamp();

