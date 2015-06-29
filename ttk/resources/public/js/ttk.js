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

angular.module('ttk', [
  'ngResource',
  'ngRoute',

  'angular-loading-bar',
  'ngUpload',
  'ui.select2',

  'aitulocale',
  'apimetodiPalaute',
  'arviointipaatokset',
  'directives',
  'direktiivit.dialogi',
  'etusivu',
  'filters',
  'henkilot',
  'kayttooikeudet',
  'koulutustoimijat',
  'ohjeet',
  'oppilaitokset',
  'organisaatiomuutokset',
  'raportit',
  'rest.suorittaja',
  'sopimukset',
  'toimikunnat',
  'tutkinnot',
  'uiKomponentit',
  'yhteiset.palvelut.virheLogitus'
])

  .config(['$routeProvider', '$httpProvider', 'asetukset', function($routeProvider, $httpProvider, asetukset) {
    $routeProvider.
      when('/', {controller: 'etusivuController', templateUrl:'template/etusivu'}).
      otherwise({templateUrl:'template/blank'});

    $httpProvider.interceptors.push(
      function(apiCallInterceptor, $q){
        return {
          request : function(pyynto){
            pyynto.timeout = asetukset.requestTimeout;
            apiCallInterceptor.apiPyynto(pyynto);
            return pyynto;
          },
          response : function(vastaus){
            apiCallInterceptor.apiVastaus(vastaus, false);
            return vastaus;
          },
          responseError : function(vastaus){
            apiCallInterceptor.apiVastaus(vastaus, true);
            return $q.reject(vastaus);
          }
        };
      }
    );

    $httpProvider.interceptors.push(
      ['kieli', function(kieli){
        return {
          request : function(pyynto){
            pyynto.headers["Accept-Language"] = kieli;
            return pyynto;
          }
        };
      }]
    );

    $httpProvider.interceptors.push(
      function($q){
        return {
          request : function(pyynto){
            pyynto.headers["Angular-Ajax-Request"] = true;
            return pyynto;
          },
          responseError : function(vastaus){
            if(vastaus.status == 403) {
              window.location.reload();
            }
            return $q.reject(vastaus);
          }
        };
      }
    );
  }])

  .factory('impersonaatioResource', ['$resource', function($resource) {
    return $resource(null, null, {
      impersonoi: {
        method: 'POST',
        url: ophBaseUrl + '/api/kayttaja/impersonoi',
        id:"impersonoi"
      },
      lopeta: {
        method: 'POST',
        url: ophBaseUrl + '/api/kayttaja/lopeta-impersonointi',
        id:"impersonoi-lopetus"
      }
    });
  }])

  .controller('AituController', ['$scope', '$window', 'i18n', 'impersonaatioResource', function($scope, $window, i18n, impersonaatioResource) {
    $scope.impersonoitava = {};
    $scope.varmistaLogout = function() {
      if(confirm(i18n['haluatko-kirjautua-ulos'])) {
        $window.location = aituLogoutUrl;
      }
    };
    $scope.valitse = function() {
      $scope.valitseHenkilo = true;
    };
    $scope.piilota = function() {
      $scope.valitseHenkilo = false;
    };
    $scope.impersonoi = function() {
      impersonaatioResource.impersonoi({oid: $scope.impersonoitava.oid}, function() {
        $window.location = ophBaseUrl;
      });
    };
    $scope.lopetaImpersonointi = function() {
      impersonaatioResource.lopeta(null, function() {
        $window.location = ophBaseUrl;
      });
    };
  }])

  .constant('asetukset', {
    requestTimeout : 120000 //2min timeout kaikille pyynnöille
  })

  .config(['cfpLoadingBarProvider', function(cfpLoadingBarProvider) {
    cfpLoadingBarProvider.latencyThreshold = 100;
    cfpLoadingBarProvider.includeSpinner = false;
  }])

  .factory('$exceptionHandler', ['virheLogitus', function(virheLogitus) {
    return virheLogitus;
  }]);
