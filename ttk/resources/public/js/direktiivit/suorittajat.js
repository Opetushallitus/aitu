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

angular.module('direktiivit.suorittajat', ['rest.suorittaja'])
  .directive('suorittajat', [function() {
    return {
      restrict: 'E',
      templateUrl: 'template/direktiivit/suorittajat',
      scope: {}, 
      controller: ['$modal', '$scope', 'Suorittaja', 'Varmistus', 'i18n', 
                   function($modal, $scope,  Suorittaja, Varmistus, i18n) {
        $scope.muokkaaSuorittajaa = function(suorittaja) {
          var modalInstance = $modal.open({
            templateUrl: 'template/modal/suorittaja',
            controller: 'LisaaSuorittajaModalController',
            resolve: {
              suorittaja: function() {
                return suorittaja;
              }
            }
          });
          modalInstance.result.then(function(suorittajaForm) {
            if (suorittaja === undefined) {
              // uusi
              Suorittaja.lisaa(suorittajaForm).then(function(suorittaja) {
            	  if (suorittaja != undefined) {  $scope.suorittajat.unshift(suorittaja); }
              });
            } else {
              // muokkaus
              Suorittaja.tallenna(suorittajaForm).then(function(muokattuSuorittaja) {
                _.assign(suorittaja, muokattuSuorittaja);
              });
            }
          });
        };

        Suorittaja.haeKaikki().then(function(suorittajat) {
          $scope.suorittajat = suorittajat;
        });

        $scope.poistaSuorittaja = function(suorittaja) {
          Varmistus.varmista(i18n.arviointipaatokset.poistetaanko_suorittaja, i18n.arviointipaatokset.poista_suorittaja_teksti, i18n.arviointipaatokset.poista_suorittaja).then(function() {
            Suorittaja.poista(suorittaja).then(function() {
              _.remove($scope.suorittajat, {suorittaja_id: suorittaja.suorittaja_id});
            });
          });
        };
      }]
    }
  }])

  .controller('LisaaSuorittajaModalController', ['$modalInstance', '$scope', 'suorittaja',
                                                 function($modalInstance, $scope, suorittaja) {
    $scope.suorittajaForm = {};
    if (suorittaja) {
      $scope.suorittajaForm = _.cloneDeep(suorittaja);
    }

    $scope.tallennaSuorittaja = function() {
      $modalInstance.close($scope.suorittajaForm);
    };
    
    $scope.peruuta = function() {
        $modalInstance.dismiss('cancel');
    };
  }])
;