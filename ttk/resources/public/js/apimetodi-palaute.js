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

angular.module('apimetodiPalaute', ['services'])

  .directive('apiMetodiPalaute', ['$rootScope', function($rootScope) {
    return {
      restrict: 'E',
      scope : {},
      replace: true,
      templateUrl: 'template/apimetodi-palaute',
      controller: 'apimetodiPalauteController',
      link: function(scope, element) {
        var content = $('#content');
        var palautteet = element.find('.api-method-feedback');
        scope.$watchCollection('palautteet', function(p) {
          if(p && p.length > 0) {
            window.scrollTo(0, 0);
            unFloatPalautteet();
          }
        });
        element.waypoint({
          handler: function(direction) {
            if(direction === 'down') {
              floatPalautteet();
            } else if (direction === 'up') {
              unFloatPalautteet();
            }
          },
          offset : -10
        });

        function floatPalautteet() {
          content.css('margin-top', palautteet.height());
          palautteet.width(content.width());
          palautteet.css('position', 'fixed');
          $(window).on('resize.feedback', function(){
            palautteet.width(content.width());
          });
        }

        function unFloatPalautteet() {
          content.css('margin-top', 0);
          palautteet.css('width', 'auto');
          palautteet.css('position', 'static');
          $(window).off('resize.feedback');
        }
      }
    };
  }])

  .controller('apimetodiPalauteController', ['$scope', 'apiCallInterceptor', 'i18n', function($scope, apiCallInterceptor, i18n) {
    $scope.palautteet = [];

    function luoPalautteetVastauksista() {
      var palautteet = luoPalautteet(apiCallInterceptor.vastaukset.lista);
      apiCallInterceptor.vastaukset.lista = [];
      if(palautteet && palautteet.length > 0) {
        $scope.palautteet = yhdistaPalautteet($scope.palautteet, palautteet);
      }
    }

    function paivitaPalautteet() {
      // Varmistetaan, että kaikille saapuneille HTTP-vastauksille on luotu
      // palautteet. Jos palautteet luotaisiin vasta paivitaPalautteet-kutsun
      // jälkeen, uudet palautteet myöhästyisivät yhdeltä päivityskierrokselta
      // ja näkyisivät täten yhden "sivulatauksen" liian pitkään.
      luoPalautteetVastauksista();

      _($scope.palautteet).each(
        function(palaute){
          if(palaute.redirectFlag) {
            palaute.redirectFlag = false;
          } else {
            palaute.naytetty = true;
          }
        }
      );

      $scope.palautteet = poistaNaytetyt($scope.palautteet);
    }

    $scope.$on('$locationChangeStart', paivitaPalautteet);
    $scope.$on('wizardPageChange', paivitaPalautteet);

    $scope.$watch(function(){
      return apiCallInterceptor.vastaukset.paivitetty;
    }, function(){
      luoPalautteetVastauksista();
    });

    function yhdistaPalautteet(palautteet, uudetPalautteet) {
      return _.unique( _.union(uudetPalautteet, palautteet), 'viesti');
    }

    function poistaNaytetyt(palautteet) {
      return _(palautteet).filter(function(palaute){return !palaute.naytetty;}).value();
    }

    function luoPalautteet(vastaukset) {
      return _(vastaukset).filter(naytaPalauteVastaukselle).map(function(vastaus) {
        return vastaus.status === 200 ? luoOkPalaute(vastaus) : luoVirhePalaute(vastaus);
      }).value();
    }

    function naytaPalauteVastaukselle(vastaus) {
      return (vastaus.config.method === 'POST' ||
              vastaus.config.method === 'PUT' ||
              vastaus.config.method === 'DELETE') &&
              vastaus.config.i18n  ||
              vastaus.status !== 200;
    }

    function luoOkPalaute(vastaus) {
      var viesti = lokalisoituViesti(vastaus.config.i18n, vastaus.config.method, true);
      return {
        virhe: false,
        viesti : viesti,
        redirectFlag : true,
        virheet : [],
        naytetty : false
      };
    }

    function luoVirhePalaute(vastaus) {
      var viesti = vastaus.config.i18n ? lokalisoituViesti(vastaus.config.i18n, vastaus.config.method, false) : defaultVirheviesti(vastaus);
      return {
        virhe: true,
        viesti: viesti,
        redirectFlag: false,
        virheet: vastaus.data ? luoEritellytVirheet(vastaus.data.errors, vastaus.config.i18n) : [],
        naytetty: false
      };
    }

    function luoEritellytVirheet(virheet, i18nPolku) {
      if(virheet) {
        return _.map(virheet, function(value, key){
          return {
            ominaisuus: i18nPolku ? (i18n[i18nPolku][key] ? i18n[i18nPolku][key] : i18n.yleiset[key]) : key,
            viesti: value.join(', ')
          };
        });
      } else {
        return [];
      }
    }

    function defaultVirheviesti(vastaus) {
      return i18n.palautteet['api-metodi-kutsu-ei-onnistunut'] + ' ' + vastaus.config.url + ' (' + vastaus.status + ')';
    }

    function lokalisoituViesti(i18nPolku, metodi, ok) {
      function kaannosAvainMetodille(metodi, ok) {
        var avain = '';

        switch(metodi) {
          case 'PUT':
            avain = 'muokkaus-';
            break;
          case 'POST':
            avain = 'uuden-luonti-';
            break;
          case 'DELETE':
            avain = 'poistaminen-';
            break;
        }
        return kaannosAvain(avain, ok);
      }

      function kaannosAvain(avain, ok) {
        return avain + ( ok ? 'onnistui' : 'ei-onnistunut' );
      }

      if(i18nPolku.indexOf('|') == -1) {
        //Näytetään metodin mukainen virheilmoitus
        return i18n[i18nPolku][kaannosAvainMetodille(metodi, ok)];
      } else {
        //Näytetään kustomoitu virheilmoitus
        var parts = i18nPolku.split('|');
        return i18n[parts[0]][kaannosAvain(parts[1] + '-', ok)];
      }
    }
  }]);
