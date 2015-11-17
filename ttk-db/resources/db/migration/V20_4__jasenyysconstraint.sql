alter table jasenyys drop constraint ei_paallekkaisia_alkupvm;
alter table jasenyys drop constraint ei_paallekkaisia_loppupvm;
alter table jasenyys add CONSTRAINT ei_paallekkaisia_alkupvm UNIQUE (henkiloid, toimikunta, alkupvm, esittaja);
alter table jasenyys add CONSTRAINT ei_paallekkaisia_loppupvm UNIQUE (henkiloid, toimikunta, loppupvm, esittaja);
 