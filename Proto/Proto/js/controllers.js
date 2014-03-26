// Copyright (c) 2013 The Finnish National Board of Education - Opetushallitus
//
// This program is free software:  Licensed under the EUPL, Version 1.1 or - as
// soon as they will be approved by the European Commission - subsequent versions
// of the EUPL (the "Licence");
//
// You may not use this work except in compliance with the Licence.
// You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// European Union Public Licence for more details.

'use strict';



function IndexController($scope) {
  
}

function SearchController($scope) {
  $scope.toimikunnat  =  [
    {"nimi": "Toimikunta 1",
      "ruotsinkielinenNimi": "  ",
      "kielisyys": "suomi",
      "toimikausi": "31.10.2013 - 30.10.2016",
      "diaarinumero": "12345-5678",
      "tilikoodi": "13",
      "paatospaiva": "30.10.2016",
      "puheenjohtaja": "Maija Meikäläinen",
      "henkilonumero": "1",
      "sahkopostiosoitteet": "toimikunta1@mail.com",
      "laatija": "Matti Meikäläinen",
      "luontipaiva": "13.08.2013",
      "viimeisinMuuttaja": "Matti Meikäläinen",
       "viimeisinMuutospaiva": "4.9.2013"},
      {"nimi": "Toimikunta 2",
      "ruotsinkielinen nimi": "  ",
      "kielisyys": "suomi",
      "toimikausi": "31.10.2013 - 30.10.2016",
      "diaarinumero": "12345-5678",
      "tilikoodi": "13",
      "paatospaiva": "30.10.2016",
      "puheenjohtaja": "Matti Meikäläinen",
      "henkilonumero": "0",
      "sahkopostiosoitteet": "toimikunta2@mail.com",
      "laatija": "Veikko Viranomainen",
      "luontipaiva": "13.08.2013",
      "viimeisinMuuttaja": "Matti Meikäläinen",
       "viimeisinMuutospaiva": "4.9.2013"
      },
      {"nimi": "Toimikunta 3",
      "ruotsinkielinen nimi": "  ",
      "kielisyys": "suomi",
      "toimikausi": "31.10.2013 - 30.10.2016",
      "diaarinumero": "12345-5678",
      "tilikoodi": "13",
      "paatospaiva": "30.10.2016",
      "puheenjohtaja": "Matti Meikäläinen",
      "henkilonumero": "0",
      "sahkopostiosoitteet": "toimikunta2@mail.com",
      "laatija": "Veikko Viranomainen",
      "luontipaiva": "13.08.2013",
      "viimeisinMuuttaja": "Matti Meikäläinen",
       "viimeisinMuutospaiva": "4.9.2013"
      },
      {"nimi": "Toimikunta 4",
      "ruotsinkielinen nimi": "  ",
      "kielisyys": "suomi",
      "toimikausi": "31.10.2013 - 30.10.2016",
      "diaarinumero": "12345-5678",
      "tilikoodi": "13",
      "paatospaiva": "30.10.2016",
      "puheenjohtaja": "Matti Meikäläinen",
      "henkilonumero": "0",
      "sahkopostiosoitteet": "toimikunta2@mail.com",
      "laatija": "Veikko Viranomainen",
      "luontipaiva": "13.08.2013",
      "viimeisinMuuttaja": "Matti Meikäläinen",
       "viimeisinMuutospaiva": "4.9.2013"
      },
      {"nimi": "Testitoimikunta 5",
      "ruotsinkielinen nimi": "  ",
      "kielisyys": "suomi",
      "toimikausi": "31.10.2013 - 30.10.2016",
      "diaarinumero": "12345-5678",
      "tilikoodi": "13",
      "paatospaiva": "30.10.2016",
      "puheenjohtaja": "Matti Meikäläinen",
      "henkilonumero": "0",
      "sahkopostiosoitteet": "toimikunta2@mail.com",
      "laatija": "Veikko Viranomainen",
      "luontipaiva": "13.08.2013",
      "viimeisinMuuttaja": "Matti Meikäläinen",
       "viimeisinMuutospaiva": "4.9.2013"
      },
      {"nimi": "Toimikunta 6",
      "ruotsinkielinen nimi": "  ",
      "kielisyys": "suomi",
      "toimikausi": "31.10.2013 - 30.10.2016",
      "diaarinumero": "12345-5678",
      "tilikoodi": "13",
      "paatospaiva": "30.10.2016",
      "puheenjohtaja": "Matti Meikäläinen",
      "henkilonumero": "0",
      "sahkopostiosoitteet": "toimikunta2@mail.com",
      "laatija": "Veikko Viranomainen",
      "luontipaiva": "13.08.2013",
      "viimeisinMuuttaja": "Matti Meikäläinen",
       "viimeisinMuutospaiva": "4.9.2013"
      },
      {"nimi": "Tosi tehokas toimikunta 3",
      "ruotsinkielinen nimi": "  ",
      "kielisyys": "suomi",
      "toimikausi": "31.10.2013 - 30.10.2016",
      "diaarinumero": "12345-5678",
      "tilikoodi": "13",
      "paatospaiva": "30.10.2016",
      "puheenjohtaja": "Matti Meikäläinen",
      "henkilonumero": "0",
      "sahkopostiosoitteet": "toimikunta2@mail.com",
      "laatija": "Veikko Viranomainen",
      "luontipaiva": "13.08.2013",
      "viimeisinMuuttaja": "Matti Meikäläinen",
       "viimeisinMuutospaiva": "4.9.2013"
      }
    ];
}

function personController($scope, $routeParams) {
  $scope.henkilonumero = $routeParams.henkilonumero;
  $scope.henkilot = [
    { "nimi": "Matti Meikäläinen",
      "henkilonumero": "0",
      "nmt": "ntm1",
      "sahkoposti": "joku@jossakin.com",
      "puhelin": "050-1234567",
      "sukupuoli": "mies",
      "kieli": "suomi",
      "nayttomestarintutkinto": "suoritettu",
      "edustettavaTaho": "opettaja",
      "keskusjarjesto": "Akava",
      "jarjesto": "OPE",
      "organisaatio": "organisaatio 1",
      "lisatiedot": "hieno mies"
    },
    { "nimi": "Maija Meikäläinen",
      "henkilonumero": "1",
      "nmt": "ntm2",
      "sahkoposti": "jokutoinen@jossakin.com",
      "puhelin": "050-1234568",
      "sukupuoli": "nainen",
      "kieli": "suomi",
      "nayttomestarintutkinto": "ei suoritettu",
      "edustettavaTaho": "tyonantaja",
      "keskusjarjesto": "EK",
      "jarjesto": "ON",
      "organisaatio": "organisaatio 2",
      "lisatiedot": "hienompi nainen"
    }
    ];
    $scope.henkilo = $scope.henkilot[$routeParams.henkilonumero];
}

function toimikuntaController($scope, $routeParams) {
  $scope.toimikuntatunnus = $routeParams.toimikuntatunnus;
  
  $scope.toimikunnat  =  [
    {"nimi": "Toimikunta 1",
      "ruotsinkielinenNimi": "  ",
      "kielisyys": "suomi",
      "toimikausi": "31.10.2013 - 30.10.2016",
      "diaarinumero": "12345-5678",
      "tilikoodi": "13",
      "paatospaiva": "30.10.2016",
      "puheenjohtaja": "Maija Meikäläinen",
      "henkilonumero": "1",
      "sahkopostiosoitteet": "toimikunta1@mail.com",
      "laatija": "Matti Meikäläinen",
      "luontipaiva": "13.08.2013",
      "viimeisinMuuttaja": "Matti Meikäläinen",
       "viimeisinMuutospaiva": "4.9.2013"},
      {"nimi": "Toimikunta 2",
      "ruotsinkielinen nimi": "  ",
      "kielisyys": "suomi",
      "toimikausi": "31.10.2013 - 30.10.2016",
      "diaarinumero": "12345-5678",
      "tilikoodi": "13",
      "paatospaiva": "30.10.2016",
      "puheenjohtaja": "Matti Meikäläinen",
      "henkilonumero": "0",
      "sahkopostiosoitteet": "toimikunta2@mail.com",
      "laatija": "Veikko Viranomainen",
      "luontipaiva": "13.08.2013",
      "viimeisinMuuttaja": "Matti Meikäläinen",
       "viimeisinMuutospaiva": "4.9.2013"
      }
    ];
  $scope.tutkintotasot = [
    {"tutkintotaso": "Perustutkinto",
     "yhteyshenkilo": "Matti Meikaläinen",
     "henkilonumero": "0",
     "nmt":"ntm1",
     "sahkoposti": "joku@jossakin.com",
     "puhelin": "050-1234567",
     "liitettytmk": "31.10.1984",
     "poistettutmk": "12.08.2010",
     "liitettytrk": "10.02.1990",
     "poistettutrk": "12.08.2010",
     "siirtymaaika": "06.12.2014"},
    {"tutkintotaso": "Perustutkinto",
     "yhteyshenkilo": "Matti Meikaläinen",
     "henkilonumero": "0",
     "nmt":"ntm1",
     "sahkoposti": "joku@jossakin.com",
     "puhelin": "050-1234567",
     "liitettytmk": "31.10.1984",
     "poistettutmk": "12.08.2010",
     "liitettytrk": "10.02.1990",
     "poistettutrk": "12.08.2010",
     "siirtymaaika": "06.12.2014"},
     {"tutkintotaso": "Perustutkinto",
     "yhteyshenkilo": "Matti Meikaläinen",
     "henkilonumero": "0",
     "nmt":"ntm1",
     "sahkoposti": "joku@jossakin.com",
     "puhelin": "050-1234567",
     "liitettytmk": "31.10.1984",
     "poistettutmk": "12.08.2010",
     "liitettytrk": "10.02.1990",
     "poistettutrk": "12.08.2010",
     "siirtymaaika": "06.12.2014"}
  ];

     $scope.opintoalat = [
    { "nimi": "Metsänhoito",
      "koodi": "OO-1922"
    },
    { "nimi": "Metsäntalous",
      "koodi": "OO-1924"
    }
  ];

    $scope.nayttotutkinnot = [
     { "nimi": "Metsuri",
       "koodi": "NT-1214",
       "taso": "ammattitutkinto"
     },
     { "nimi": "Metsänhoitaja",
       "koodi": "NT-1221",
       "taso": "ammattitutkinto"
     }
    ];

     $scope.osaamisalat = [
    { "nimi": "Metsäteollisuus",
      "koodi": "OA-1922"
    },
    { "nimi": "Metsäntalous",
      "koodi": "OA-1924"
    }
  ];
  $scope.tutkintonimikkeet = [
    { "nimi": "Metsänhoidon ammattitutkinto",
      "koodi": "TN-1922"
    },
    { "nimi": "Metsätalouden ammattitutkinto",
      "koodi": "TN-1924"
    }
  ];
  $scope.toimikunta = $scope.toimikunnat[$scope.toimikuntatunnus];
}

function sopimusController($scope, $routeParams) {
  $scope.sopimustunnus = $routeParams.sopimustunnus;
  
}