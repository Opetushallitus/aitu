alter table suorituskerta
  add column tutkintoversio_suoritettava integer references tutkintoversio(tutkintoversio_id) null,
  add column liitetty_pvm timestamp with time zone null;
  
update suorituskerta set tutkintoversio_suoritettava = tutkintoversio_id;
  
comment on column suorituskerta.tutkintoversio_suoritettava is 'Viite suoritettavaan tutkintoon, johon tutkinnon osan suoritus kohdistuu. Tämä on normaalisti sama tutkinto, josta tutkinnon osat löytyvät, mutta valinnaisen osasuorituksen liittämisessä voi olla eri tutkinto.';
comment on column suorituskerta.liitetty_pvm is 'Päivämäärä tutkinnon osan liittämiselle.';

