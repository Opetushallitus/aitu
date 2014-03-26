create view toimikuntaview as
  select
    t.tkunta, t.nimi_fi, t.nimi_sv, t.sahkoposti, t.kielisyys, 
    t.toimikausi_alku, t.toimikausi_loppu
  from tutkintotoimikunta t;
