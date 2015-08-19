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

angular.module('suoritus', [])

  .config(['$routeProvider', function($routeProvider) {
    $routeProvider.when('/lisaa-suoritus', {controller: 'SuoritusController', templateUrl: 'template/suoritus'});
  }])

  .controller('SuoritusController', ['$location', '$scope', 'Rahoitusmuoto', 'Suorittaja', 'Suoritus', 'Tutkinnonosa', 'TutkintoResource', function($location, $scope, Rahoitusmuoto, Suorittaja, Suoritus, Tutkinnonosa, TutkintoResource) {
    $scope.form = {
      osat: []
    };
    $scope.osat = [];
    $scope.$watchCollection('osat', function(osat) {
      $scope.form.osat = _.pluck(osat, 'tutkinnonosa_id');
    });

    Rahoitusmuoto.haeKaikki().then(function(rahoitusmuodot) {
      $scope.rahoitusmuodot = rahoitusmuodot;
    });

    Suorittaja.haeKaikki().then(function(suorittajat) {
      $scope.suorittajat = suorittajat;
    });

    Tutkinnonosa.haeKaikki().then(function(tutkinnonosat) {
      $scope.tutkinnonosat = tutkinnonosat;
    });

    TutkintoResource.query(function(tutkinnot) {
      $scope.tutkinnot = tutkinnot;
    });

    $scope.lisaaOsa = function(osa) {
      if (!_.find($scope.osat, {tutkinnonosa_id: osa.tutkinnonosa_id})) {
        $scope.osat.push(osa);
      }
    };

    $scope.poistaOsa = function(osa) {
      _.remove($scope.osat, {tutkinnonosa_id: osa.tutkinnonosa_id});
    };

    $scope.lisaaSuoritus = function() {
      Suoritus.lisaa($scope.form).then(function() {
        $location.url('/arviointipaatokset');
      });
    };

    $scope.peruuta = function() {
      $location.url('/arviointipaatokset');
    };
  }])
;