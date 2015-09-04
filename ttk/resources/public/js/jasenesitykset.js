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

angular.module('jasenesitykset', ['ngRoute', 'rest.jasenesitykset'])
  .config(['$routeProvider', function($routeProvider) {
    $routeProvider.when('/jasenesitykset', {controller: 'JasenesityksetController', templateUrl: 'template/jasenesitykset'});
  }])

  .controller('JasenesityksetController', ['$location', '$q', '$scope', 'Jasenesitykset', function($location, $q, $scope, Jasenesitykset) {
    $scope.haku = {};

    $scope.luoJasenesitys = function() {
      $location.url('/henkilot/uusi');
    };

    $scope.$watch('haku', function(haku) {
      Jasenesitykset.hae(haku.ehdokas, haku.jarjesto, haku.toimikunta, haku.asiantuntijaksi, haku.tila).then(function(esitykset) {
        $scope.esitykset = esitykset;
      });
    }, true);
  }])
;