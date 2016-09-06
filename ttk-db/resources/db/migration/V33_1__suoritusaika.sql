alter table suorituskerta
  add column suoritusaika date not null;

comment on column suoritus.kieli is 'Kieli, jolla tutkinnon osa on suoritettu.';
comment on column suoritus.osaamisen_tunnustaminen is 'Onko kyseessä aiemmin suoritetun osaamisen tunnustaminen';
comment on column suoritus.todistus is 'Annetaanko tutkinnon osan suorittamisesta erillinen todistus? Pyydettäessä annetaan.';
comment on column suoritus.arvosanan_korotus is 'Onko kyseessä aiemmin suoritetun tutkinnonosan suorittaminen uudelleen arvosanan korottamiseksi?';


comment on column suorituskerta.suoritusaika is 'Suorituksen ajankohta.';
comment on column suorituskerta.hyvaksymisaika is 'Aika (kokouspvm), jolloin tutkintotoimikunta on hyväksynyt suoritukset.';
comment on column suorituskerta.ehdotusaika is 'Aika, jolloin suoritukset on lähetetty tutkintotoimikunnalle käsiteltäväksi.';
