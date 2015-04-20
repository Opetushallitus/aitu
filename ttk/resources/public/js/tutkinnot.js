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

angular.module('tutkinnot', ['ngRoute', 'resources'])

  .config(function($routeProvider) {
    $routeProvider.
      when('/search-tutkinto', {controller:'TutkinnotController', templateUrl:'template/tutkinnot'}).
      when('/tutkinto/:tutkintotunnus', {controller:'TutkintoController', templateUrl:'template/tutkinto'});
  })

  .factory('TutkintoResource', ['$resource', function($resource) {
    return $resource(ophBaseUrl + '/api/tutkinto/:tutkintotunnus', {'tutkintotunnus': '@tutkintotunnus'}, {
      get: {
        method: 'GET',
        params: {nocache: function() { return Date.now(); } },
        id: 'tutkinnon-tiedot'
      },
      query: {
        method: 'GET',
        isArray: true,
        id: 'tutkintolistaus'
      }});
  }])

  .factory('TutkintorakenneResource', ['$resource', function($resource) {
    return $resource(ophBaseUrl + '/api/tutkintorakenne', {}, {
      query: {
        method: 'GET',
        params: {nocache: function() { return Date.now(); } },
        isArray: true,
        id: 'tutkintorakenneHaku'
      }
    });
  }])

  .controller('TutkinnotController', ['$scope', 'TutkintoResource', 'KoulutusalaResource', '$filter',
    function($scope, TutkintoResource, KoulutusalaResource, $filter) {
      $scope.kaikkiTutkinnot = TutkintoResource.query();
      $scope.koulutusalat = KoulutusalaResource.query();
      $scope.tutkinnot = [];
      $scope.tutkintoHakuehto = {'nimi': '',
                                 'voimassaolo': 'voimassaolevat',
                                 'opintoala': '',
                                 'tutkintotaso': ''};

      $scope.$watch('tutkintoHakuehto.nimi', suodataTutkinnot);
      $scope.$watch('tutkintoHakuehto.voimassaolo', suodataTutkinnot);
      $scope.$watch('tutkintoHakuehto.opintoala', suodataTutkinnot);
      $scope.$watch('tutkintoHakuehto.tutkintotaso', suodataTutkinnot);
      $scope.$watchCollection('kaikkiTutkinnot', suodataTutkinnot);

      $scope.haeTutkinnot = haeTutkinnot;

      function haeTutkinnot() {
        $scope.kaikkiTutkinnot = TutkintoResource.query();
      }

      function suodataTutkinnot() {
        var filtered = $filter('suomiJaRuotsi')($scope.kaikkiTutkinnot, 'nimi', $scope.tutkintoHakuehto.nimi);
        if($scope.tutkintoHakuehto.voimassaolo == 'voimassaolevat') {
          filtered = $filter('voimassaOlevat')(filtered, true);
        }
        if($scope.tutkintoHakuehto.opintoala) {
          filtered = _.filter(filtered, {opintoala: $scope.tutkintoHakuehto.opintoala});
        }
        if($scope.tutkintoHakuehto.tutkintotaso) {
          filtered = _.filter(filtered, {tutkintotaso: $scope.tutkintoHakuehto.tutkintotaso});
        }
        $scope.tutkinnot = $filter('orderByLokalisoitu')(filtered, 'nimi');
      }
    }]
  )

  .controller('TutkintoController', ['$scope', '$routeParams', 'TutkintoResource', '$filter',
    function($scope, $routeParams, TutkintoResource, $filter) {
      $scope.tutkinto = TutkintoResource.get({"tutkintotunnus": $routeParams.tutkintotunnus});
      $scope.toimikunnat = {
        nykyiset: [],
        vanhat: []
      };
      $scope.sopimukset = {
        nykyiset: [],
        vanhat: []
      };

      $scope.tutkinto.$promise.then(function(tutkinto) {
        $scope.toimikunnat.nykyiset = $filter('voimassaOlevat')(tutkinto.tutkintotoimikunta, true);
        $scope.toimikunnat.vanhat = $filter('voimassaOlevat')(tutkinto.tutkintotoimikunta, false);

        var jarjestamissopimukset = _.map(tutkinto.sopimus_ja_tutkinto, _.property('jarjestamissopimus'));
        if (tutkinto.voimassa) {
          $scope.sopimukset.nykyiset = _.filter(jarjestamissopimukset, {voimassa: true});
          $scope.sopimukset.vanhat = _.filter(jarjestamissopimukset, {voimassa: false});
        } else {
          $scope.sopimukset.nykyiset = jarjestamissopimukset;
          $scope.sopimukset.vanhat = [];
        }
      });
      $scope.naytaVanhatTutkinnot = false;
    }]
  )

  .controller('TutkintojenLisaysController', ['$scope',
                                              'TutkintorakenneResource',
                                              'resource',
                                              'config',
                                              '$filter',
                                              'varmistaPoistuminen',
                                              '$location',
                                              '$routeParams',
                                              'i18n',
                                              'debounce',
    function($scope, tutkintorakenneResource, resource, config, $filter, varmistaPoistuminen, $location, $routeParams, i18n, debounce){
      $scope.suodatettuTutkintorakenne = [];
      $scope.suodatus = false;
      var tutkinnotAlussa = [];
      var entity = resource.get({}, function(r) {
        tutkinnotAlussa = _.map(r[config.tutkinnotProperty], function(tutkinto){return _.pick(tutkinto, ['tutkintotunnus', 'nimi_fi', 'nimi_sv']);});
        $scope.valitutTutkinnot = r[config.tutkinnotProperty];
      });

      varmistaPoistuminen.kysyVarmistusPoistuttaessa();

      haeTutkintorakenne();

      var hakuEhtoMuuttunut = function(value, oldValue) {
        $scope.suodatus = value && value.length > 0;
        if(value !== oldValue) {
          suodataTutkintorakenne();
          $scope.$apply();
        }
      };

      $scope.$watch('tutkintoHakuehto', debounce(hakuEhtoMuuttunut, 500));

      $scope.lisaa = function() {
        var tutkinto = _.find($scope.valitutTutkinnot, {tutkintotunnus : this.tutkinto.tutkintotunnus });
        if(tutkinto === undefined) {
          $scope.valitutTutkinnot.push(this.tutkinto);
        }
      };

      $scope.poista = function() {
        var poistettava = this.tutkinto;
        _.remove($scope.valitutTutkinnot, function(tutkinto) {return tutkinto.tutkintotunnus == poistettava.tutkintotunnus;});
      };

      $scope.tallenna = function() {
        if(varmistaTallennus()) {
          varmistaPoistuminen.tallenna(resource.tutkinnot(entity),
            function() {
              $location.path(config.basePath + '/' + $routeParams.id + '/tiedot');
            });
        }
      };

      $scope.valittu = function() {
        return _.find($scope.valitutTutkinnot, {tutkintotunnus : this.tutkinto.tutkintotunnus }) !== undefined;
      };

      $scope.peruuta = function() {
        $location.path(config.basePath + '/' + $routeParams.id + '/tiedot');
      };

      $scope.haeTutkintorakenne = haeTutkintorakenne;

      function haeTutkintorakenne() {
        $scope.tutkintoRakenne = tutkintorakenneResource.query({}, suodataTutkintorakenne);
      }

      function suodataTutkintorakenne() {
        if($scope.tutkintoRakenne.$resolved) {
          $scope.suodatettuTutkintorakenne = $filter('tutkintorakenneHakuFilter')($scope.tutkintoRakenne, $scope.tutkintoHakuehto);
        }
      }

      function varmistaTallennus() {
        var poistettavat = _.reject(tutkinnotAlussa, function(tutkinto){return _.find($scope.valitutTutkinnot, {tutkintotunnus : tutkinto.tutkintotunnus}); });
        if(poistettavat.length > 0) {
          var viesti = i18n.tutkinnot['tutkintojen-poistamisen-varmistus'];
          var tutkinnot = _.reduce(poistettavat, function(viesti, tutkinto){ return viesti + $filter('lokalisoi')(null, tutkinto, 'nimi') + '\n';}, '');
          return confirm(viesti + '\n\n' + tutkinnot);
        } else {
          return true;
        }
      }
  }]);


