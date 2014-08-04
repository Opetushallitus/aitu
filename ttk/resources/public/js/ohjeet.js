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

angular.module('ohjeet', [])

  .factory('ohjeetResource', ['$resource', function($resource) {
    return $resource(ophBaseUrl + '/api/ohje/:ohjetunniste', {'ohjetunniste': '@ohjetunniste'}, {
      get: {
        method: 'GET',
        params: {nocache: function() { return Date.now(); } }
      },
      update: {
        method: 'PUT',
        id : 'ohjeen-muokkaus'
      }
    });
  }])

  .directive('ohje', ['ohjeetResource', '$timeout', '$filter', function(ohjeetResource, $timeout, $filter) {
    return {
      restrict: 'E',
      scope : {
        tunniste: '@',
        tasaaVasemmalle : '@'
      },
      templateUrl: 'template/ohje',
      replace : true,
      link: function(scope) {
        scope.ohje = {};
        scope.nayta = false;
        scope.muokkaus = false;
        scope.ohjeteksti = "";

        scope.naytaOhje = function(){
          naytaOhje()
        };
        scope.piilotaOhje = function(){
          scope.nayta = false;
          scope.muokkaus = false;
        };

        scope.muokkaa = function() {
          scope.muokkaus = true;
        };

        scope.peruutaMuokkaus = function() {
          scope.muokkaus = false;
        }

        scope.tallennaOhje = function() {
          if(!scope.ohje.ohjetunniste) {
            scope.ohje.ohjetunniste = scope.tunniste;
          }
          ohjeetResource.update(scope.ohje, function() {
            naytaOhje();
          });
        }

        function naytaOhje() {
          scope.muokkaus = false;
          scope.nayta = true;
          scope.ohje = ohjeetResource.get({ohjetunniste: scope.tunniste}, parsiOhjeteksti);
        }

        function parsiOhjeteksti() {
          var lokalisoitu = $filter('lokalisoi')(null, scope.ohje, 'teksti');
          var pilkottu = lokalisoitu? lokalisoitu.split(/\n/g) : [];

          scope.ohjeteksti = _.map(pilkottu, function(value) {
            return {sisalto : value};
          });
        }
      }
    };
  }]);