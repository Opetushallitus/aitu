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

  .factory('tiedoteResource', ['$resource', 'i18n', function($resource, i18n) {
    return $resource(ttkBaseUrl + '/api/tiedote', {}, {
      get: {
        method: 'GET',
        params: { nocache: function() { return Date.now(); }},
        id: 'tiedotteen-teksti'
      },
      delete: {
        method: 'DELETE',
        id: 'tiedotteen-poisto',
        i18n : 'etusivu'
      },
      save: {
        method: 'POST',
        id: 'tiedotteen-tallennus',
        i18n : 'etusivu'
      }
    });
  }])

  .factory('hakuResource', ['$resource', 'i18n', function($resource, i18n) {
    return $resource(ttkBaseUrl + '/api/haku/:tunnus', {'tunnus': '@tunnus'}, {
      get: {
        method: 'GET',
        i18n: 'etusivu'
      }
    });
  }])

  .controller('etusivuController', ['$scope',  '$rootScope', '$location', 'tiedoteResource', 'hakuResource', function($scope, $rootScope, $location, tiedoteResource, hakuResource) {
    $scope.tiedote = tiedoteResource.get();
    $scope.hakuehto = '';

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

    function palaaNormaalitilaan(tiedote) {
      $scope.tiedote = tiedote ? tiedote : {};
      $scope.muokkausTila = false;
      $rootScope.$broadcast('wizardPageChange');
    }

    $scope.tiedoteOlemassa = function() {
      return !_.isUndefined($scope.tiedote.tiedoteid);
    };

    $scope.muokkaa = function() {
      $scope.muokkausTila = true;
      $rootScope.$broadcast('wizardPageChange');
    };

    $scope.julkaise = function() {
      tiedoteResource.save($scope.tiedote, palaaNormaalitilaan);
    };

    $scope.poista = function() {
      tiedoteResource.delete(null, palaaNormaalitilaan);
    };

    $scope.peruuta = function() {
      palaaNormaalitilaan($scope.tiedote);
    };

  }]);