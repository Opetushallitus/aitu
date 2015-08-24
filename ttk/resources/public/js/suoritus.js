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

  .controller('SuoritusController', ['$location', '$modal', '$scope', 'Koulutustoimija', 'Rahoitusmuoto', 'Suorittaja', 'Suoritus', 'Tutkinnonosa', 'TutkintoResource', function($location, $modal, $scope, Koulutustoimija, Rahoitusmuoto, Suorittaja, Suoritus, Tutkinnonosa, TutkintoResource) {
    $scope.vuodet = _.range(1, 21);
    $scope.form = {
      osat: []
    };
    $scope.osat = [];
    $scope.$watchCollection('osat', function(osat) {
      $scope.form.osat = _.map(osat, function(osa) {
        var result = _.pick(osa, ['arvosana', 'korotus', 'kieli', 'todistus', 'tunnustaminen']);
        result.tutkinnonosa_id = osa.tutkinnonosa.tutkinnonosa_id;
        return result;
      });
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

    Koulutustoimija.haeKaikkiNimet().then(function(koulutustoimijat) {
      $scope.koulutustoimijat = koulutustoimijat;
    });

    $scope.lisaaTutkinnonosa = function() {
      var modalInstance = $modal.open({
        templateUrl: 'template/modal/suoritus-tutkinnonosa',
        controller: 'SuoritusTutkinnonosaModalController',
        resolve: {
          tutkinnot: function() { return $scope.tutkinnot; },
          tutkinto: function() { return $scope.form.tutkinto; }
        }
      });

      modalInstance.result.then(function(uusiOsa) {
        if (uusiOsa.tunnustaminen) {
          uusiOsa.arvosana = null;
          uusiOsa.korotus = false;
        }
        if (!_.find($scope.osat, function(osa) {
            return osa.tutkinnonosa.tutkinnonosa_id === uusiOsa.tutkinnonosa.tutkinnonosa_id;
          })) {
          $scope.osat.push(uusiOsa);
        }
      });
    };

    $scope.poistaOsa = function(poistettavaOsa) {
      _.remove($scope.osat, function(osa) {
        return osa.tutkinnonosa.tutkinnonosa_id === poistettavaOsa.tutkinnonosa.tutkinnonosa_id;
      });
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

  .controller('SuoritusTutkinnonosaModalController', ['$modalInstance', '$scope', 'Tutkinnonosa', 'tutkinnot', 'tutkinto', function($modalInstance, $scope, Tutkinnonosa, tutkinnot, tutkinto) {
    $scope.form = {
      arvosana: 'hyvaksytty',
      korotus: false,
      kieli: 'fi',
      todistus: false,
      tunnustaminen: false
    };
    $scope.tutkinto = tutkinto;

    $scope.tutkinnot = tutkinnot;

    $scope.$watch('tutkinto', function(tutkinto) {
      if (tutkinto !== undefined) {
        Tutkinnonosa.hae(tutkinto).then(function(tutkinnonosat) {
          $scope.tutkinnonosat = tutkinnonosat;
        });
      }
    });

    $scope.ok = function() {
      $modalInstance.close($scope.form);
    };

    $scope.sulje = function() {
      $modalInstance.dismiss();
    };
  }])
;