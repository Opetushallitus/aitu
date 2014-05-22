-- http://www.postgresql.org/docs/9.1/static/sql-comment.html
-- käytetään kommenttimekanismia dokumentointiin. Autogeneroitu dokumentaatio, ks. OPH-90

COMMENT ON TABLE alue IS 'Maantieteellinen alue. Vanhat läänit, mutta ei enää olennainen tieto jatkossa.';
COMMENT ON TABLE edustus  IS 'Edustettavan tahon tyyppi toimikunnan jäsenyydessä.';
COMMENT ON TABLE henkilo  IS 'Toimikunnan jäsen';
COMMENT ON TABLE jarjestamissopimus  IS 'Sopimus tutkinnon järjestämisestä toimikunnan ja oppilaitoksen välillä.';
COMMENT ON TABLE jarjestamissuunnitelma  IS 'Sopimuksen ja hakemuksen liite, jossa kuvataan miten toiminta järjestetään.';
COMMENT ON TABLE jarjesto  IS 'Ammattijärjestö. Sisältää myös keskusjärjestöt.';
COMMENT ON TABLE jasenyys IS 'Henkilön jäsenyydet eri toimikunnissa.';
COMMENT ON TABLE kayttaja  IS 'Käyttäjä, jolla on käyttöoikeus ja käyttäjätunnus järjestelmään.';
COMMENT ON TABLE kayttajarooli  IS 'Käyttäjärooli, joka määrittää oikeustason.';
COMMENT ON TABLE koulutusala      IS 'Tilastokeskuksen luokittelun mukainen koulutusala';
COMMENT ON TABLE nayttotutkinto  IS 'Ne tutkinnot, joihin voidaan tehdä järjestämissopimuksia.';
COMMENT ON TABLE ohje  IS 'Käyttöohjeet, jotka näkyvät käyttöliittymässä';
COMMENT ON TABLE opintoala  IS 'Tilastokeskuksen luokittelun mukainen opintoala.';
COMMENT ON TABLE oppilaitos  IS 'Tilastokeskuksen koodittamat oppilaitokset.';
COMMENT ON TABLE osaamisala  IS 'Ryhmittelee tutkintoja osaamisaloihin';
COMMENT ON TABLE peruste  IS 'Tutkinnon perusteet.';
COMMENT ON TABLE rooli  IS 'Henkilön rooli/tehtävä toimikunnan jäsenenä.';
COMMENT ON TABLE schema_version  IS 'Flyway-työkalun käyttämä aputaulu tietokantamuutosten kirjanpitoon.';
COMMENT ON TABLE sopimuksen_liite  IS 'Sopimuksen vapaamuotoinen liite, binääridataa.';
COMMENT ON TABLE tiedote  IS 'Etusivulla näkyvä tiedoteteksti. ';
COMMENT ON TABLE toimikausi  IS 'Toimikunnan toimikausi. Normaalisti toimikausi vaihtuu samalla kaikille toimikunnille.';
COMMENT ON TABLE toimipaikka  IS 'Koulutustoimijan toimipiste, jossa tutkintoja järjestetään. Voi olla myös yritys.';
COMMENT ON TABLE tutkintotaso  IS 'Ammattitutkinnon taso.';
COMMENT ON TABLE tutkintotyyppi  IS 'OPH:n tyyppiluokitus tutkinnoille.';
COMMENT ON TABLE tutkintoversio IS 'Tutkinnon tietojen muutoshistoria.';
 