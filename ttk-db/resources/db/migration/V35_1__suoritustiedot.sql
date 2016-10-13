alter table suorituskerta 
  alter column jarjestelyt type varchar(1000);

comment on column suorituskerta.jarjestelyt is 'Vapaa kuvaus työtehtävistä ja järjestelyistä, jotka liittyvät näyttötilaisuuteen.';

alter table suorittaja
  drop constraint suorittaja_oid_key,
  drop constraint suorittaja_hetu_key,
  add CONSTRAINT oidhetu_constraint UNIQUE (hetu, oid);
