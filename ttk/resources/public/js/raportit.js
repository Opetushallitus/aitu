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

angular.module('raportit', ['ngRoute', 'resources'])

  .config(function($routeProvider) {
    $routeProvider.
      when('/raportit', {controller:'RaportitController', templateUrl:'template/raportit'});
  })

  .factory('KoulutusalaResource', ['$resource', function($resource) {
    return $resource(ophBaseUrl + '/api/koulutusala/opintoalat', {}, {
      query: {
        method: 'GET',
        isArray: true,
        id: 'koulutusalalistaus'
      }});
  }])

  .controller('RaportitController', ['$scope', '$filter', 'i18n', 'ToimikausiResource', 'KoulutusalaResource',
    function($scope, $filter, i18n, ToimikausiResource, KoulutusalaResource) {
      $scope.jasenet = {
        yhteystiedot:false,
        opintoala: []
      };
      $scope.sopimukset = {};
      $scope.tilastot = {};
      $scope.toimikunnat = {};

      $scope.select2Options = {
        allowClear: true
      };


      KoulutusalaResource.query().$promise.then(function(koulutusalat) {
        $scope.koulutusalat = koulutusalat;
      });

      ToimikausiResource.query().$promise.then(function(toimikaudet) {
        $scope.toimikaudet = toimikaudet;
        var voimassaoleva_toimikausi = _(toimikaudet).filter('voimassa').pluck('toimikausi_id').first();
        $scope.tilastot.toimikausi = voimassaoleva_toimikausi;
        $scope.sopimukset.toimikausi = voimassaoleva_toimikausi;
        $scope.jasenet.toimikausi = voimassaoleva_toimikausi;
        $scope.toimikunnat.toimikausi = voimassaoleva_toimikausi;
      });
      $scope.raportit = [
        {id: 'nayttotutkinnot', nimi: i18n.raportit.nayttotutkinnot},
        {id: 'jarjestamissopimukset', nimi: i18n.raportit.jarjestamissopimukset},
        {id: 'tilastotietoa', nimi: i18n.raportit.tilastotietoa},
        {id: 'tutkintotoimikunnat', nimi: i18n.raportit.tutkintotoimikunnat},
        {id: 'jasenet', nimi: i18n.raportit.jasenet}
      ];
      $scope.raportti = $scope.raportit[0];
    }
  ]);
