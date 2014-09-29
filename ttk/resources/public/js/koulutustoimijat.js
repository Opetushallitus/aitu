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

angular.module('koulutustoimijat', ['ngRoute'])

  .config(function($routeProvider) {
    $routeProvider.
      when('/search-koulutustoimija', {controller:'KoulutustoimijatController', templateUrl:'template/koulutustoimijat'}).
      when('/koulutustoimija/:id/tiedot', {controller:'KoulutustoimijaTiedotController', templateUrl:'template/koulutustoimija'});
  })

  .factory('KoulutustoimijaResource', ['$resource', function($resource) {
    return $resource(ophBaseUrl + '/api/koulutustoimija/:ytunnus', {'ytunnus': '@ytunnus'}, {
      query: {
        method: 'GET',
        isArray: true,
        id : 'koulutustoimijalistaus'
      },
      get: {
        method: 'GET',
        id : 'koulutustoimija'
      }
    });
  }])

  .factory('KoulutustoimijaHakuResource', ['$resource', function($resource) {
    return $resource(ophBaseUrl + '/api/koulutustoimija/haku/ala', {}, {
      query: {
        method: 'GET',
        isArray: true,
        id : 'koulutustoimijalistaus'
      }
    });
  }])

  .controller('KoulutustoimijatController', ['$scope', 'KoulutustoimijaHakuResource', '$filter', 'i18n', 
    function($scope, KoulutustoimijaHakuResource, $filter, i18n) {
      $scope.i18n = i18n;
      $scope.kaikkiKoulutustoimijat = [];
      $scope.koulutustoimijat = [];
      $scope.search = {nimi: "", ala: {tunnus: ""}, sopimuksia: "kylla"};
      $scope.$watch('search.nimi', suodataKoulutustoimijat);
      $scope.$watch('search.sopimuksia', suodataKoulutustoimijat);
      $scope.$watch('search.ala', haeKoulutustoimijat);
      $scope.$watchCollection('kaikkiKoulutustoimijat', suodataKoulutustoimijat);

      haeKoulutustoimijat();

      function suodataKoulutustoimijat() {
        var filteredNimella = $filter('suomiJaRuotsi')($scope.kaikkiKoulutustoimijat, 'nimi', $scope.search.nimi);
        var filteredSopimuksilla = $filter('sopimukset')(filteredNimella, $scope.search.sopimuksia);
        $scope.koulutustoimijat = $filter('orderByLokalisoitu')(filteredSopimuksilla, 'nimi');
      }

      function haeKoulutustoimijat() {
        $scope.kaikkiKoulutustoimijat = KoulutustoimijaHakuResource.query({tunnus: $scope.search.ala && $scope.search.ala.tunnus});
      }
    }
  ])

  .controller('KoulutustoimijaTiedotController', ['$scope', '$routeParams', 'KoulutustoimijaResource', '$filter',
    function($scope, $routeParams, KoulutustoimijaResource, $filter) {
      $scope.koulutustoimija = KoulutustoimijaResource.get({ytunnus : $routeParams.id});
      $scope.sopimukset = {
        nykyiset: [],
        vanhat: []
      };

      $scope.koulutustoimija.$promise.then(function(koulutustoimija) {
        $scope.sopimukset.nykyiset = $filter('voimassaOlevat')(koulutustoimija.jarjestamissopimus, true);
        $scope.sopimukset.vanhat = $filter('voimassaOlevat')(koulutustoimija.jarjestamissopimus, false);
      });

      $scope.naytaVanhatSopimukset = false;
    }
  ]);
