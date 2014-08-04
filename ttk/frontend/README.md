Frontend asennus:

Asenna node ja npm:
http://nodejs.org/download/

Asenna grunt:

```
npm install -g grunt-cli
```

Asenna bower:

```
npm install -g bower
```

Hakemistossa /frontend tee seuraava:

```
npm install
```

Uuden bower kirjaston käyttöönotto: aja

```
bower install <kirjaston-nimi> --save
```

ja lisää kirjaston js-filen kopiointi Gruntfile.js-tiedostoon initConfig.concat.dist.src -listaan. Aja lopuksi

```
grunt bower
```

Uuden nodejs kirjaston asennus kehityskäyttöön:

```
npm install <kirjaston-nimi> --save-dev
npm shrinkwrap --dev
```

Testien ajaminen:

```
Grunt test
```

Testien ajaminen jatkuvasti:

```
Grunt autotest
```








