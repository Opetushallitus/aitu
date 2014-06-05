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

angular.module('jarjestajat', ['ngRoute'])

  .config(function($routeProvider) {
    $routeProvider.
      when('/search-jarjestaja', {controller:'JarjestajatController', templateUrl:'template/jarjestajat'}).
      when('/jarjestaja/:id/tiedot', {controller:'JarjestajaTiedotController', templateUrl:'template/jarjestaja'});
  })

  .factory('JarjestajaResource', ['$resource', function($resource) {
    return $resource(ttkBaseUrl + '/api/oppilaitos/:oppilaitoskoodi', {'oppilaitoskoodi': '@oppilaitoskoodi'}, {
      query: {
        method: 'GET',
        isArray: true,
        id : 'jarjestajalistaus'
      },
      get: {
        method: 'GET',
        id : 'jarjestaja'
      }
    });
  }])

  .factory('JarjestajaHakuResource', ['$resource', function($resource) {
    return $resource(ttkBaseUrl + '/api/oppilaitos/haku/ala', {}, {
      query: {
        method: 'GET',
        isArray: true,
        id : 'jarjestajalistaus'
      }
    });
  }])

  .controller('JarjestajatController', ['$scope', 'JarjestajaResource', 'JarjestajaHakuResource', '$filter', 'i18n',
    function($scope, JarjestajaResource, JarjestajaHakuResource, $filter, i18n) {
      $scope.i18n = i18n;
      $scope.kaikkiJarjestajat = [];
      $scope.jarjestajat = [];
      $scope.search = {nimi: "", termi: "", properties: ""};
      $scope.tutkintoHakuehto = {nimi: "", termi: "", properties: ""};

      $scope.$watch('search.nimi', suodataJarjestajat);
      $scope.$watch('search.termi', haeJarjestajat);

      $scope.$watch('tutkintoHakuehto.nimi', suodataJarjestajat);
      $scope.$watch('tutkintoHakuehto.termi', haeJarjestajat);

      $scope.$watchCollection('kaikkiJarjestajat', suodataJarjestajat);

      haeJarjestajat();

      function suodataJarjestajat() {
        var filteredNimella = $filter('filter')($scope.kaikkiJarjestajat, {nimi: $scope.search.nimi});
        $scope.jarjestajat = $filter('orderBy')(filteredNimella, 'nimi');
      }

      function haeJarjestajat() {
        $scope.search.properties = {tutkinto: $scope.tutkintoHakuehto.termi};
        $scope.kaikkiJarjestajat = JarjestajaHakuResource.query({termi: $scope.search.termi, tutkinto: $scope.tutkintoHakuehto.termi});
      }
    }
  ])

  .controller('JarjestajaTiedotController', ['$scope', '$routeParams', 'JarjestajaResource', '$filter',
    function($scope, $routeParams, JarjestajaResource, $filter) {
      $scope.jarjestaja = JarjestajaResource.get({oppilaitoskoodi : $routeParams.id});
      $scope.sopimukset = {
        nykyiset: [],
        vanhat: []
      };

      $scope.jarjestaja.$promise.then(function(jarjestaja) {
        $scope.sopimukset.nykyiset = $filter('voimassaOlevat')(jarjestaja.jarjestamissopimus, true);
        $scope.sopimukset.vanhat = $filter('voimassaOlevat')(jarjestaja.jarjestamissopimus, false);
      });

    }
  ]);
