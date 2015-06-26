create table opiskelija(
    opiskelija_id SERIAL PRIMARY KEY,
    etunimi VARCHAR(100) NOT NULL,
    sukunimi VARCHAR(100) NOT NULL,
    hetu CHAR(11) UNIQUE,
    oid VARCHAR(80) UNIQUE,

    muutettu_kayttaja VARCHAR(80) NOT NULL REFERENCES kayttaja(oid),
    luotu_kayttaja VARCHAR(80) NOT NULL REFERENCES kayttaja(oid),
    muutettuaika TIMESTAMPTZ NOT NULL,
    luotuaika TIMESTAMPTZ NOT NULL,

    CHECK (hetu IS NOT NULL OR oid IS NOT NULL)
);

create trigger opiskelija_update before update on opiskelija for each row execute procedure update_stamp();
create trigger opiskelijal_insert before insert on opiskelija for each row execute procedure update_created();
create trigger opiskelijam_insert before insert on opiskelija for each row execute procedure update_stamp();
create trigger opiskelija_mu_update before update on opiskelija for each row execute procedure update_modifier() ;
create trigger opiskelija_cu_insert before insert on opiskelija for each row execute procedure update_creator() ;
create trigger opiskelija_mu_insert before insert on opiskelija for each row execute procedure update_modifier() ;
