CREATE OR REPLACE VIEW aituhaku.tutkinnon_toimikunnat_view AS 
 SELECT tt.tutkintotunnus, ttk.nimi_fi, ttk.nimi_sv, ttk.tkunta, nt.nimi_fi as tutkinto_nimi_fi, nt.nimi_sv as tutkinto_nimi_sv, nt.tutkintotaso, nt.opintoala
   FROM toimikunta_ja_tutkinto tt
   inner JOIN tutkintotoimikunta ttk ON ttk.tkunta::text = tt.toimikunta::text
   inner JOIN toimikausi toimik ON toimik.toimikausi_id = ttk.toimikausi_id
   inner join nayttotutkinto nt on nt.tutkintotunnus = tt.tutkintotunnus
  WHERE toimik.voimassa = true;
  
CREATE OR REPLACE VIEW aituhaku.toimikuntaview AS 
 SELECT t.tkunta, t.nimi_fi, t.nimi_sv, t.sahkoposti, t.kielisyys, 
    t.toimikausi_alku, t.toimikausi_loppu, h.puhelin, h.osoite, h.postinumero, 
    h.postitoimipaikka
   FROM tutkintotoimikunta t
   inner JOIN toimikausi toimik ON toimik.toimikausi_id = t.toimikausi_id
   LEFT JOIN jasenyys j ON j.toimikunta::text = t.tkunta::text AND j.rooli::text = 'sihteeri'::text AND j.alkupvm <= 'now'::text::date AND j.loppupvm > 'now'::text::date
   LEFT JOIN henkilo h ON j.henkiloid = h.henkiloid
   WHERE toimik.voimassa = true;

