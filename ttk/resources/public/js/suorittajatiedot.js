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

angular.module('suorittajatiedot', [])

  .config(['$routeProvider', function($routeProvider) {
    $routeProvider.when('/suorittaja/:suorittajaid', {controller: 'SuorittajaController', templateUrl: 'template/suorittajatiedot'});
  }])

  .controller('SuorittajaController', ['$routeParams', '$location', '$modal', '$scope', 'Arvioija', 'Osaamisala', 'Koulutustoimija', 'Toimikunta', 'Rahoitusmuoto',
                                     'Suorittaja', 'Suoritus', 'Tutkinnonosa', 'TutkintoResource', 'Varmistus', 'i18n',
    function($routeParams, $location, $modal, $scope, Arvioija, Osaamisala, Koulutustoimija, Toimikunta, Rahoitusmuoto, Suorittaja, Suoritus, Tutkinnonosa, TutkintoResource, Varmistus, i18n) {
      if ($routeParams.suorittajaid) {
        Suorittaja.hae($routeParams.suorittajaid).then(function(suorittaja) {
          $scope.suorittaja = suorittaja;
        });
        var hakuForm = {suorittajaid: $routeParams.suorittajaid};
        Suoritus.haeKaikki(hakuForm).then(function(suoritukset) {
          $scope.suoritukset = suoritukset;
          $scope.suorituksetJarjestetty = suoritukset;
          });
      }

      $scope.muokkaaSuoritusta = function(muokattavaSuoritus) {
        $location.url('/muokkaa-suoritus/' + muokattavaSuoritus.suorituskerta_id);
      };
  }]);