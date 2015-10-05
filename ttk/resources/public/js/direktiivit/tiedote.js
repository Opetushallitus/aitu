// Copyright (c) 2015 The Finnish National Board of Education - Opetushallitus
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

angular.module('direktiivit.tiedote', [])
  .directive('tiedote', [function() {
    return {
      restrict: 'E',
      templateUrl: 'template/direktiivit/tiedote',
      replace: true,
      scope: {},
      controller: ['$rootScope', '$scope', 'tiedoteResource', function($rootScope, $scope, tiedoteResource) {
        $scope.tiedote = tiedoteResource.get();

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
      }]
    }
  }])
;