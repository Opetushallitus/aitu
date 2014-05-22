-- http://www.postgresql.org/docs/9.1/static/sql-comment.html
-- käytetään kommenttimekanismia dokumentointiin. Autogeneroitu dokumentaatio, ks. OPH-90
 
COMMENT ON COLUMN henkilo.kayttaja_oid IS 'Sitoo henkilön yksilöityyn käyttäjätunnukseen. Kaikilla henkilöillä ei ole käyttäjätunnuksia.';
COMMENT ON COLUMN henkilo.nayttomestari IS 'Onko henkilö näyttömestaritutkinnon suorittanut? K/E';

COMMENT ON COLUMN jarjestamissopimus.alkupvm IS 'Sopimuksen voimaantuloaika. Voimassa alkaen tästä päivästä.';
COMMENT ON COLUMN jarjestamissopimus.loppupvm IS 'Sopimuksen viimeinen voiamssaolopäivä.';
COMMENT ON COLUMN jarjestamissopimus.poistettu IS 'Jos sopimus on poistettu, se on edelleen tietokannassa, mutta sitä ei enää näytetä käyttöliittymässä ja rajapinnoissa.';
COMMENT ON COLUMN jarjestamissopimus.sopijatoimikunta IS 'Toimikunta jonka kanssa sopimus on alun perin tehty.';
COMMENT ON COLUMN jarjestamissopimus.sopimusnumero IS 'Sopimuksen virallinen diaarinumero.';
COMMENT ON COLUMN jarjestamissopimus.toimikunta IS 'Toimikunta joka vastaa sopimuksesta. Voi olla eri toimikunta kuin sopijatoimikunta.';
COMMENT ON COLUMN jarjestamissopimus.tutkintotilaisuuksista_vastaava_oppilaitos IS 'Sopimuksen allekirjoittaja ja vastuuosapuoli ei ole välttämättä tutkintotilaisuuksia järjestävä taho.';


COMMENT ON COLUMN jarjesto.keskusjarjestoid IS 'Viite keskusjärjestöön jos järjestö on osa jotain keskusjärjestöä.';

COMMENT ON COLUMN jasenyys.alkupvm  IS 'Ensimmäinen päivä jolloin henkilö on tullut toimikunnan jäseneksi.';
COMMENT ON COLUMN jasenyys.loppupvm  IS 'Viimeinen päivä jolloin henkilö on ollut toimikunnan jäsen.';

COMMENT ON COLUMN kayttaja.oid  IS 'Käyttäjän yksilöivä tunnus käyttäjähallintapalvelussa.';
COMMENT ON COLUMN kayttaja.uid  IS 'Käyttäjän yksilöivä käyttäjätunnus käyttäjähallintapalvelussa.';
COMMENT ON COLUMN kayttaja.voimassa  IS 'Voimassaolo päätellään käyttäjähallintapalvelusta sieltä tuoduille käyttäjätunnuksille.';

COMMENT ON COLUMN ohje.ohjetunniste  IS 'Sitoo ohjetekstin käyttöliittymässä tiettyyn kohtaan, jossa se näytetään.';

COMMENT ON COLUMN peruste.alkupvm  IS 'Tutkinnon perusteen ensimmäinen voimassaolopäivä.';
COMMENT ON COLUMN peruste.siirtymaajan_loppupvm  IS 'Siirtymäajasta on erillinen sääntö. Vanhan perusteen mukaisesti voi suorittaa tutkinnon loppuun siirtymäajan aikana.';
COMMENT ON COLUMN peruste.diaarinumero  IS 'Virallinen diaarinumero.';


COMMENT ON COLUMN sopimus_ja_tutkinto.nayttomestari  IS 'Onko vastuuhenkilö näyttötutkintomestari?';
COMMENT ON COLUMN sopimus_ja_tutkinto.nayttomestari_vara  IS 'Onko vastuuhenkilön varahenkilö näyttötutkintomestari?';
COMMENT ON COLUMN sopimus_ja_tutkinto.lisatiedot  IS 'Vapaamuotoisia lisätietoja ja muistiinpanoja OPH:n käyttöön.';

COMMENT ON COLUMN tutkintotoimikunta.diaarinumero  IS 'Asettamispäätöksen virallinen diaarinumero.';
COMMENT ON COLUMN tutkintotoimikunta.kielisyys  IS 'Mitä eri kieliä toimikunta käsittelee tutkintojensa osalta';
COMMENT ON COLUMN tutkintotoimikunta.tilikoodi  IS 'Toimikunnan yksilöivä koodi, joka liittyy hallintoon ja kirjanpitoon.';
COMMENT ON COLUMN tutkintotoimikunta.tkunta  IS 'Toimikunnan yksilöivä surrogaattiavain. Perua AMTU-järjestelmästä, jossa osa luonnollisista avaimista puuttui.';
COMMENT ON COLUMN tutkintotoimikunta.toimikausi_alku  IS 'Poikkeustapauksissa voi olla eri päivämäärä kuin kolmivuotiskauden alkupäivä. Virallinen voimaantulopäivä, jolloin toimikunta on toimivaltainen.';
COMMENT ON COLUMN tutkintotoimikunta.toimikausi_loppu  IS 'Poikkeustapauksissa voi olla eri päivämäärä kuin kolmivuotiskauden loppupäivä. Virallinen loppupäivä, jolloin toimikunta on toimivaltainen.';
COMMENT ON COLUMN tutkintotoimikunta.toimikausi_id  IS 'Virallisen kolmivuotiskauden viiteavain.';
