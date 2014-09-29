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

angular.module('oppilaitokset', ['ngRoute'])

  .config(function($routeProvider) {
    $routeProvider.
      when('/search-oppilaitos', {controller:'OppilaitoksetController', templateUrl:'template/oppilaitokset'}).
      when('/oppilaitos/:id/tiedot', {controller:'OppilaitosTiedotController', templateUrl:'template/oppilaitos'});
  })

  .factory('OppilaitosResource', ['$resource', function($resource) {
    return $resource(ophBaseUrl + '/api/oppilaitos/:oppilaitoskoodi', {'oppilaitoskoodi': '@oppilaitoskoodi'}, {
      query: {
        method: 'GET',
        isArray: true,
        id : 'oppilaitoslistaus'
      },
      get: {
        method: 'GET',
        id : 'oppilaitos'
      }
    });
  }])

  .factory('OppilaitosHakuResource', ['$resource', function($resource) {
    return $resource(ophBaseUrl + '/api/oppilaitos/haku/ala', {}, {
      query: {
        method: 'GET',
        isArray: true,
        id : 'oppilaitoslistaus'
      }
    });
  }])

  .controller('OppilaitoksetController', ['$scope', 'OppilaitosHakuResource', '$filter', 'i18n',
    function($scope, OppilaitosHakuResource, $filter, i18n) {
      $scope.i18n = i18n;
      $scope.kaikkiOppilaitokset = [];
      $scope.oppilaitokset = [];
      $scope.search = {nimi: "", ala: {tunnus: ""}, sopimuksia: "kylla"};
      $scope.$watch('search.nimi', suodataOppilaitokset);
      $scope.$watch('search.sopimuksia', suodataOppilaitokset);
      $scope.$watch('search.ala', haeOppilaitokset);
      $scope.$watchCollection('kaikkiOppilaitokset', suodataOppilaitokset);

      haeOppilaitokset();

      function suodataOppilaitokset() {
        var filteredNimella = $filter('filter')($scope.kaikkiOppilaitokset, {nimi: $scope.search.nimi});
        var filteredSopimuksilla = $filter('sopimukset')(filteredNimella, $scope.search.sopimuksia);
        $scope.oppilaitokset = $filter('orderBy')(filteredSopimuksilla, 'nimi');
      }

      function haeOppilaitokset() {
        $scope.kaikkiOppilaitokset = OppilaitosHakuResource.query({tunnus: $scope.search.ala && $scope.search.ala.tunnus});
      }
    }
  ])

  .controller('OppilaitosTiedotController', ['$scope', '$routeParams', 'OppilaitosResource', '$filter',
    function($scope, $routeParams, OppilaitosResource, $filter) {
      $scope.oppilaitos = OppilaitosResource.get({oppilaitoskoodi : $routeParams.id});
      $scope.sopimukset = {
        nykyiset: [],
        vanhat: []
      };

      $scope.oppilaitos.$promise.then(function(oppilaitos) {
        $scope.sopimukset.nykyiset = $filter('voimassaOlevat')(oppilaitos.jarjestamissopimus, true);
        $scope.sopimukset.vanhat = $filter('voimassaOlevat')(oppilaitos.jarjestamissopimus, false);
      });
      $scope.naytaVanhatSopimukset = false;
    }
  ]);
