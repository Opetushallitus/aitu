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

angular.module('etusivu', [])

  .factory('hakuResource', ['$resource', 'i18n', function($resource, i18n) {
    return $resource(ophBaseUrl + '/api/haku/:tunnus', {'tunnus': '@tunnus'}, {
      get: {
        method: 'GET',
        i18n: 'etusivu'
      }
    });
  }])

  .controller('etusivuController', ['$scope',  '$rootScope', '$location', 'hakuResource', 'organisaatiomuutosResource', function($scope, $rootScope, $location, hakuResource, organisaatiomuutosResource) {
    $scope.hakuehto = '';
    $scope.muutostenMaara = organisaatiomuutosResource.maara();

    function siirryHakutulokseen(hakutulos) {
      if (hakutulos && hakutulos.url && hakutulos.tunnus) {
        $location.path(hakutulos.url.replace('*', encodeURIComponent(hakutulos.tunnus)));
      }
      else {
        $scope.hakupalaute = true;
      }
    }

    $scope.tyhjennaPalaute = function() {
      $scope.hakupalaute = false;
    };

    $scope.hae = function(hakuehto) {
      if (!_.isEmpty(hakuehto)) {
        hakuResource.get({'tunnus': hakuehto}, siirryHakutulokseen);
        // broadcast, jottei eri hakujen virheilmoitukset kasaannu
        $rootScope.$broadcast('wizardPageChange');
      }
    };

    $scope.muutoksiaOlemassa = function() {
      return !_.isUndefined($scope.muutostenMaara.maara) && $scope.muutostenMaara.maara > 0;
    }
  }]);