aitu-common-e2e
Julkaisuohje:

Ohje clojars:n käyttöönottoon
- tee tunnus clojars.org-palveluun ja pyydä liittyä ryhmään solita
- asenna GPG
  - (GPG:n käytön tarkempi ohje: https://github.com/technomancy/leiningen/blob/stable/doc/GPG.md)
- luo avainpari
- ota avaimesi id talteen (kuten leiningenin ohjeessa)
- julkaise luomasi avain
- lisää seuraava ~/.lein/profiles.clj-tiedostoon (user-profiiliin)
    {:user {...
            :signing {:gpg-key "<avaimesi id>"}}}
- tulosta julkinen avain
- vie tulostettu avain clojars-tiliisi

Ohjeet aitu-common-e2e-kirjaston muutoksiin
- tee muutokset aitu-common-e2e-kirjastoon
- päivitä versionumero project.clj:ssä (<http://semver.org>)
  - x+1.y.z: ei taaksepäin yhteensopiva version x.y.z kanssa
  - x.y+1.z: taaksepäin yhteensopiva version x.y.z kanssa
  - x.y.z+1: taaksepäin yhteensopiva korjaus versioon x.y.z
- aja lein install
- tee checkouts hakemisto projekteihin, joissa käytät e2e-kirjastoa
- tee checkouts-hakemistoon symbolinen linkki aitu-common-e2e-projektin hakemistoon
- päivitä e2e-kirjaston versionumero kirjastoa käyttävässä projektissa
- aja e2e testit
- palaa aitu-common-e2e-hakemistoon
- tee commit
- aja lein deploy clojars
- kasvata e2e-kirjaston minor-versionumeroa (x.Y.z) ja lisää perään sana '-SNAPSHOT'
- tee commit
- julkaise muutokset versionhallintaan
- julkaise aitu-common-e2e-kirjastoa käyttävien projektien muutokset versionhallintaan 

