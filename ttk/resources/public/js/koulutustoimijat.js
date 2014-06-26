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
      when('/search-koulutustoimija', {controller:'KoulutustoimijatController', templateUrl:'template/koulutustoimijat'});
  })

  .factory('KoulutustoimijaResource', ['$resource', function($resource) {
    return $resource(ttkBaseUrl + '/api/koulutustoimija/:ytunnus', {'ytunnus': '@ytunnus'}, {
      query: {
        method: 'GET',
        isArray: true,
        id : 'koulutustoimijalistaus'
      }
    });
  }])

  .factory('KoulutustoimijaHakuResource', ['$resource', function($resource) {
    return $resource(ttkBaseUrl + '/api/koulutustoimija/haku/ala', {}, {
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
      $scope.search = {nimi: "", termi: ""};
      $scope.$watch('search.nimi', suodataKoulutustoimijat);
      $scope.$watch('search.termi', haeKoulutustoimijat);
      $scope.$watchCollection('kaikkiKoulutustoimijat', suodataKoulutustoimijat);

      haeKoulutustoimijat();

      function suodataKoulutustoimijat() {
        var filteredNimella = $filter('suomiJaRuotsi')($scope.kaikkiKoulutustoimijat, 'nimi', $scope.search.nimi);
        $scope.koulutustoimijat = $filter('orderByLokalisoitu')(filteredNimella, 'nimi');
      }

      function haeKoulutustoimijat() {
        $scope.kaikkiKoulutustoimijat = KoulutustoimijaHakuResource.query({termi: $scope.search.termi});
      }
    }
  ]);
