# Tutkintotoimikuntarekisteri

## Koodissa käytetyistä nimistä

Toteutettavan ohjelmiston nimi vaihtui useaan otteeseen projektin
alkuvaiheessa. Tästä syystä koodissa on käytetty nimiä "TTK", "TTKR", "Amtu" ja
"Aitu". Kaikki nämä nimet viittaavat kuitenkin samaan Aitu-projektiin. Kaikkien
nimien vaihtaminen Aituksi olisi melko työläs operaatio suhteessa
saavutettuun hyötyyn, joten ainakaan toistaiseksi ei käytetä resursseja
nimien yhtenäistämiseen.

## Projektin tuominen Eclipseen

Clojure-kehitykseen Eclipsellä käytetään Counterclockwise-nimistä laajennusta.
Projektin kotisivulta löydät Software Update site -linkit:
http://code.google.com/p/counterclockwise/

  1. Kopioi joko stable channel tai beta channel -linkin kohdeosoite

  2. Avaa Eclipsen valikosta Help -> Install New Software... ja liitä kopioimasi
     URL "Work with"-kenttään

  3. Asenna paketti "Clojure Programming"

Projektista ei ole versionhallinnassa Eclipsen projektitiedostoja, vaan
ainoastaan Leiningenin `project.clj`. Voit tuoda projektin Eclipseen
seuraavasti.

  1. Valitse valikosta File -> New -> Project... (nimenomaan pelkkä Project,
     eikä esim. Java Project tai Clojure project) ja syötä uuden projektin
     sijainniksi Git-reposi ttk-alihakemisto (sama hakemisto, jossa tämä
     README.md on)

  2. Klikkaa projektia Package Explorer -näkymässä oikealla napilla ja valitse
     Configure -> Convert to Leiningen project

## Kehitysympäristön käynnistäminen ja työnkulku

Projektin kehittämiseen suositeltu työnkulku mukailee Stuart Sierran
työnkulkua[1]. Tavoitteena on, että REPL:iä ei tarvitse sulkea kuin äärimmäisen
harvoin.

  0. Jos käytät Counterclockwisea, lisää ccw.server[2] tiedostoon
     `~/.lein/profiles.clj`, esim.

        {:user {:dependencies [[ccw/ccw.server "0.1.0"]]
                :repl-options {:init (require 'ccw.debug.serverrepl)}}}

     Tämä mahdollistaa tekstintäydennyksen ja muiden edistyneempien
     ominaisuuksien käytön Counterclockwisen ulkopuoliseen REPLiin
     yhdistettäessä.

  1. Käynnistä nREPL Leiningenillä: `$ lein repl :start :port 9999`.

  2. Yhdistä editorisi nREPL:iin. Counterclockwisessa Window -> Connect to REPL
     -> nrepl://127.0.0.1:9999

  3. Käynnistä palvelin (uudelleen) ajamalla REPLissä `(uudelleenkaynnista!)`.
     Tämä lataa uudelleen kaikki nimiavaruudet, joita vastaavat tiedostot ovat
     muuttuneet.

     Jos jonkin nimiavaruuden lataaminen epäonnistuu esim. syntaksivirheen
     vuoksi, latausprosessi keskeytyy, ja jäät tilaan, jossa mm.
     `user`-nimiavaruus puuttuu. Pääset takaisin toimivaan tilaan seuraavalla
     tavalla:

     1. Korjaa virhe ja lataa sen sisältänyt nimiavaruus uudelleen
        (Counterclockwisessa Clojure -> Load file in REPL).

     2. Lataa `user`-nimiavaruus (tiedostossa `dev/user.clj`) samalla tavalla.

     3. Kutsu `uudelleenkaynnista!`-funktiota uudelleen.

REPL:in oletusnimiavaruus `dev.user` on määritelty tiedostossa `dev/user.clj`.
Voit määritellä siihen esim. kehityskäytössä tarvittavia funktiota ja aliaksia
muille nimiavaruuksille.

## Yleiskuva ja käytetyt kirjastot

Sovelluksen HTTP-palvelin on määritelty nimiavaruudessa `ttk.palvelin`.
Palvelinkirjastona käytetään Ring-yhteensopivaa[3] HTTP-kit-kirjastoa.[4]

[1] http://thinkrelevance.com/blog/2013/06/04/clojure-workflow-reloaded
[2] https://github.com/laurentpetit/ccw.server
[3] https://github.com/ring-clojure/ring
[4] http://http-kit.org/server.html

## Testien ajaminen

Testit ajetaan komennolla `lein test`. Oletuksena ajetaan vain yksikkötestit.

### Integraatiotestit

Integraatiotestit ajetaan komennolla `lein test :integraatio`. Niiden käyttämä
tietokantapalvelin valitaan jollain seuraavista tavoista (viimeisin määritelty
voittaa):

1. Oletusasetukset: `localhost:2345`. Nämä asetukset toimivat, jos luot
   tietokantapalvelimen Vagrantilla allaolevien ohjeiden mukaisesti.

2. `ttk.properties`-tiedoston avaimet `db.host` ja `db.port`.

3. Ympäristömuuttujat `AMTU_DB_HOST` ja `AMTU_DB_PORT`.

Integraatiotestien käyttämä tietokantayhteys muodostetaan fixturessa
`ttk.integraatio.sql.sql-henkilo-arkisto-test/tietokanta-fixture`. Fixturea
käytetään automaattisesti, jos ajat kaikki testit projektissa tai
nimiavaruudessa. Jos haluat ajaa yksittäisen testin, kutsu fixture-funktiota
antaen sille argumentiksi haluamasi testifunktio:

    ;; Testitiedostossa
    (deftest foobar-test
      ...)

    ;; REPLissä
    (tietokanta-fixture foobar-test)

Jos luot uusia testinimiavaruuksia, muista ottaa fixture käyttöön niissä:

    (use-fixtures :once tietokanta-fixture)

### e2e-testit

e2e-testit löytyvät projektista amtu-e2e. Voit ajaa testit komennolla
`lein test` hakemistossa `amtu-e2e`. Testit olettavat, että palvelin on
käynnissä ja löytyy osoitteesta `http://localhost:8080`. Voit määritellä jonkin
muun palvelin-URL:n asettamalla ympäristömuuttujan `AITU_URL` ennen testien
ajamista.

# Tietokantapalvelin

1. Asenna Virtualbox ja Vagrant koneeseen

2. Aja komento `dev-scripts/init-db.sh`. Se luo uuden Vagrant-virtuaalikoneen ja konfiguroi sen tietokantapalvelimeksi, sekä lataa viimeisimmät konversiotulokset tietokantaan dumpista. Tietokantakone on tavoitettavissa osoitteessa `192.168.50.51`. Lisäksi sen PostgreSQL-portti 5432 forwardoidaan isäntäkoneen porttiin 2345.

Interface Clojuresta tietokantaan on yksinkertaisimmillaan testattavissa REPL:ssa seuraavasti:
(require '[clojure.java.jdbc :as sql])
(sql/with-connection "postgresql://ttk_adm:ttk-adm@127.0.0.1:2345/ttk"  (sql/with-query-results res ["select 'lol'"] (println res)))

Jos tämä ei palauta tulosta, palomuurissa tai jossain vastaavassa on ongelma.
