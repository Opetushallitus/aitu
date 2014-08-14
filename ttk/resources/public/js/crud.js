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

angular.module('crud', ['services','resources'])

  .factory('crudLocation', ['$location','$anchorScroll','$window','$timeout', function($location, $anchorScroll, $window,$timeout) {
    return {
      siirryMuokkaukseen : function(id, basePath, hash){
        $location.path(basePath + '/' + id + '/muokkaa');
        if(hash!=""){
            $location.hash(hash);
            $timeout(function() {
              $anchorScroll();
            }, 500);
          }
      },
      naytaTiedot : function(id, basePath){$location.path(basePath + '/' + id + '/tiedot');},
      naytaLista : function(basePath){$location.path(basePath  + '/');},
      luoUusi : function(basePath){$location.path(basePath  + '/' + 'uusi');}
    };
  }])

  .controller('crudController',
    ['$scope',
     '$routeParams',
     '$location',
     '$window',
     'edellinenLokaatio',
     'varmistaPoistuminen',
     'crudLocation',
     'resource',
     'config',
     'i18n',
    function($scope, $routeParams, $location, $window, edellinenLokaatio, varmistaPoistuminen, crudLocation, resource, config, i18n){
      var basePath = config.basePath;
      var idProp = config.modelIdProperty;
      var modelProperty = config.modelProperty;
      var subModelProperty = config.subModelProperty;
      var muodostaPaluuosoite = config.muodostaPaluuosoite;
      $scope.i18n = i18n;
      $scope.muokkausTila = true;
      $scope.luontiTila = config.luontiTila;
      $scope[modelProperty] = {};
      var scopeObject = $scope;
      if (subModelProperty) {
        $scope[modelProperty][subModelProperty] = {};
        scopeObject = $scope[modelProperty];
        modelProperty = subModelProperty;
      }
      $scope.peruuta = peruuta;
      $scope.tallenna = tallenna;

      varmistaPoistuminen.kysyVarmistusPoistuttaessa();

      haeTiedot();
      function haeTiedot() {
        if (config.haeTiedot) {
          scopeObject[modelProperty] = config.haeTiedot();
        } else if ($routeParams.id) {
          var params = {};
          params[idProp] = $routeParams.id;
          scopeObject[modelProperty] = resource.get(params);
        }
      }

      function peruuta() {
        edellinenLokaatio();
      }

      function tyhjennaHakuehto() {
        $location.search('');
      }

      function tallenna() {
        var saveFn =  $routeParams.id ? resource.update :  resource.save;
        varmistaPoistuminen.tallenna(saveFn(scopeObject[modelProperty]),
          function(response) {
            tyhjennaHakuehto();
            if (muodostaPaluuosoite) {
              $location.path(muodostaPaluuosoite(response));
            } else {
              crudLocation.naytaTiedot(response[idProp], basePath);
            }
          });
      }
    }]);
