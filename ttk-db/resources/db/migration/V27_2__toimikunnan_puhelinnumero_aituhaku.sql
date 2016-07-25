create or replace view aituhaku.toimikuntaview as
  select
    t.tkunta, t.nimi_fi, t.nimi_sv, t.sahkoposti, t.kielisyys,
    t.toimikausi_alku, t.toimikausi_loppu,
    h.puhelin, h.osoite, h.postinumero, h.postitoimipaikka
  from tutkintotoimikunta t
  left join jasenyys j on j.toimikunta = t.tkunta and j.rooli = 'sihteeri' and j.alkupvm <= current_date and j.loppupvm > current_date
  left join henkilo h on j.henkiloid = h.henkiloid;