alter table suorituskerta 
  alter column jarjestelyt type varchar(1000);

comment on column suorituskerta.jarjestelyt is 'Vapaa kuvaus työtehtävistä ja järjestelyistä, jotka liittyvät näyttötilaisuuteen.';
