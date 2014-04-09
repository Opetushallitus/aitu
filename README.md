[Näyttötutkintojärjestelmän](http://www.oph.fi/nayttotutkinnot) tutkintotoimikuntarekisteri. Rekisterisovelluksesta käytetään nimeä Aitu ja sen lähdekoodi löytyy tästä repositorysta.

# Repositoryn sisältö ja rakenne

* **ttk** sisältää varsinaisen sovelluksen 
* **ttk-db** sovelluksen tietokannan hallintaan liittyvä apuohjelma
* **dev-scripts** kehitystyössä käytettäviä skriptejä
* **vagrant** virtuaalikoneiden konfiguraatio
* **env** virtuaalikoneisiin liittyvät alustusskriptit ja asennuspaketit

# Virtuaalikoneiden käyttö

Sovellusta voi ajaa paikallisesti [Vagrant](http://www.vagrantup.com/) ohjelman avulla. Virtuaalikoneiden ajamisesta huolehtii [https://www.virtualbox.org/](Oracle Virtualbox). Molemmat ovat ilmaisia ohjelmia. Virtuaalikoneissa ajetaan [CentOS](http://www.centos.org/) Linux-käyttöjärjestelmää ja palvelinohjelmistoina erilaisia avoimen lähdekoodin ilmaisia sovelluksia, kuten [PostgreSQL](http://www.postgresql.org/).

## Koneiden pystytys

Ensin tietysti pitää ottaa lähdekoodi itselleen esimerkiksi näin: 

```
git clone https://github.com/Opetushallitus/aitu
cd aitu
```

Varsinainen virtuaalikoneiden pystyttäminen vaatii vain kaksi komentoa
```
cd vagrant
vagrant up db
vagrant up aitu
```

## Vagrantfile ja verkkoyhteydet

Vagrantfile määrittelee privaatti-IP osoitteet koneille ja joitakin port-forwardeja isäntäkoneen ja virtuaalikoneen välisiä yhteyksiä varten. Jos nämä ovat jo käytössä voi tulla ongelmia. Asian joutuu ratkaisemaan paikallisesti.

## Sovelluksen asennus virtuaalikoneeseen

Sovelluksella on kaksi asennuspakettia. Tietokannan päivityksestä vastaava apuohjelma ja varsinainen sovellus, joka on toteutettu upotetulla http-palvelimella. Asennuspaketit eivät ole käytännön syistä tällä hetkellä julkisesti jaossa valmiiksi käännettyinä binaaritiedostoina. Mikäli kiinnostusta ja tarvetta niille on, niitä voidaan laittaa jakoon.

Asennuspakettien luonnin ja sovelluksen koko asennuksen virtuaalikoneeseen voi tehdä yhdellä komennolla:
```
cd dev-scripts
./deploy.sh
```


# Kehitystyö

Koodi on enimmäkseen [Clojurea](http://clojure.org/). Tarvitset Java-virtuaalikoneen ja [leiningen](http://leiningen.org/) työkalun.

Tiedostossa ttk/LUEMINUT.txt on ohjeita kehitystyöhön enemmän.



