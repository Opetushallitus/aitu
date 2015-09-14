create or replace view aituhaku.toimikuntien_jasenet_view as
  select
    j.toimikunta, h.etunimi, h.sukunimi, j.rooli
  from henkilo h join jasenyys j on h.henkiloid = j.henkiloid
  where j.status = 'nimitetty' and j.alkupvm <= current_date and current_date <= j.loppupvm;
