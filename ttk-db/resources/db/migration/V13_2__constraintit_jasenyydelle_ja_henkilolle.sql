update jasenyys set edustus='itsenainen' where edustus is null;
update henkilo set aidinkieli='fi' where aidinkieli is null;
update henkilo set sukupuoli='mies' where sukupuoli is null;

alter table jasenyys alter column rooli set not null;
alter table jasenyys alter column edustus set not null;
alter table henkilo alter column aidinkieli set not null;
alter table henkilo alter column sukupuoli set not null;
alter table tutkintotoimikunta alter column nimi_sv set not null;
