create or replace view aituhaku.toimikuntien_jasenet_view as
  select
    j.toimikunta, h.etunimi, h.sukunimi, j.rooli
  from henkilo h join jasenyys j on h.henkiloid = j.henkiloid;
