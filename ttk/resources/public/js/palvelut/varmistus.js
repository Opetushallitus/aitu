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

angular.module('palvelut.varmistus', [])
  .factory('Varmistus', ['$modal', function($modal) {
    return {
      varmista: function(otsikko, teksti, nappi) {
        return $modal.open({
          template: '<div class="modal-header"><h1 ng-bind="otsikko"></h1></div><div class="modal-body"><p ng-bind="teksti"></p></div><div class="modal-footer"><button ng-click="ok()" ng-bind="nappi"></button><button ng-click="cancel()" ng-bind="i18n.yleiset.peruuta"></button></div>',
          controller: 'VarmistaModalController',
          resolve: {
            tekstit: function() {
              return {otsikko: otsikko, teksti: teksti, nappi: nappi};
            }
          }
        }).result;
      }
    }
  }])

  .controller('VarmistaModalController', ['$modalInstance', '$scope', 'i18n', 'tekstit', function($modalInstance, $scope, i18n, tekstit) {
    $scope.i18n = i18n;
    $scope.ok = $modalInstance.close;
    $scope.cancel = $modalInstance.dismiss;

    _.assign($scope, tekstit);
  }])
;