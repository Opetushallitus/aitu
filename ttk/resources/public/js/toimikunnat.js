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

angular.module('toimikunnat', ['ngRoute', 'services', 'resources', 'crud'])

  .config(function($routeProvider) {
    function controllerConfig() {
      return {
        basePath : '/toimikunta',
        modelProperty : 'toimikunta',
        modelIdProperty : 'diaarinumero',
        tutkinnotProperty : 'nayttotutkinto'
      };
    }

    var resolve = {resource : 'toimikuntaResource', config : controllerConfig };

    $routeProvider.
      when('/search-toimikunta', {controller:'ToimikunnatController', templateUrl:'template/toimikunnat'}).
      when('/toimikunta/:id*\/jasenet', {controller:'ToimikuntaJasenetController', templateUrl:'template/jasenet'}).
      when('/toimikunta/:id*\/jasenet/uusi', {controller:'HenkiloVelhoController', templateUrl:'template/jasen'}).
      when('/toimikunta/:id*\/muokkaa', {controller:'crudController', resolve : resolve, templateUrl:'template/toimikunta'}).
      when('/toimikunta/:id*\/tiedot', {controller:'ToimikuntaController', templateUrl:'template/toimikunta'}).
      when('/toimikunta/:id*\/tutkinnot', {controller:'TutkintojenLisaysController', resolve : resolve,  templateUrl: 'template/tutkintojen-lisays'});
  })

  .factory('ToimikuntaUtil', ['$location', function($location) {
    return {
      siirryJasentenMuokkaukseen: function(diaarinumero) { $location.path('/toimikunta/' + diaarinumero + '/jasenet'); },
      siirryJasenenLisaykseen: function(diaarinumero) { $location.path('/toimikunta/' + diaarinumero + '/jasenet/uusi'); },
      siirrySopimuksenLuontiin: function(diaarinumero) { $location.url('/sopimus/uusi?toimikunta=' + diaarinumero); },
      palaaToimikuntaan: function(diaarinumero) { $location.path('/toimikunta/' + diaarinumero + '/tiedot'); },
      muokkaaToimialaa: function(diaarinumero) { $location.path('/toimikunta/' + diaarinumero + '/tutkinnot'); }
    };
  }])

  .controller('ToimikunnatController', ['$scope', 'toimikuntaHakuResource', '$filter',
    function($scope, toimikuntaHakuResource, $filter) {
      $scope.kaikkiToimikunnat = [];
      $scope.toimikunnat = [];
      $scope.toimikuntaHakuehto = {
        nimi: "",
        toimikausi: "nykyinen",
        kielisyys: []
      };
      $scope.tutkintoHakuehto = {
        tunnus: ""
      };
      $scope.$watch('toimikuntaHakuehto.nimi', suodataToimikunnat);
      $scope.$watchCollection('toimikuntaHakuehto.kielisyys', suodataToimikunnat);
      $scope.$watchCollection('kaikkiToimikunnat', suodataToimikunnat);
      $scope.$watch('toimikuntaHakuehto.toimikausi', haeToimikunnat);
      $scope.$watch('tutkintoHakuehto.tunnus', haeToimikunnat);

      haeToimikunnat();

      function suodataToimikunnat() {
        var filteredNimella = $filter('suomiJaRuotsi')($scope.kaikkiToimikunnat, 'nimi', $scope.toimikuntaHakuehto.nimi);
        var filteredKielisyydella = $filter('kielisyys')(filteredNimella, 'kielisyys', $scope.toimikuntaHakuehto.kielisyys);
        $scope.toimikunnat = $filter('orderByLokalisoitu')(filteredKielisyydella, 'nimi');
      }

      function haeToimikunnat() {
        $scope.kaikkiToimikunnat = toimikuntaHakuResource.query({toimikausi: $scope.toimikuntaHakuehto.toimikausi, tunnus: $scope.tutkintoHakuehto.tunnus});
      }

      $scope.kielisyysParametri = function() {
        return _.pluck($scope.toimikuntaHakuehto.kielisyys, "nimi").join(",");
      }
    }
  ])

  .controller('ToimikuntaController', ['$scope', '$routeParams', 'toimikuntaResource', 'ToimikuntaUtil', '$filter', 'crudLocation',
    function($scope, $routeParams, toimikuntaResource, ToimikuntaUtil, $filter, crudLocation) {
      $scope.toimikunta = toimikuntaResource.get({"diaarinumero": $routeParams.id});
      $scope.nykyisetJasenet = [];
      $scope.entisetJasenet = [];

      $scope.salliMuokkaus = function() {
        return !$scope.muokkausTila && $scope.toimikunta.voimassa !== false;
      };

      $scope.muokkaa = function() {
        crudLocation.siirryMuokkaukseen($routeParams.id, '/toimikunta');
      };

      $scope.muokkaaJasenia = function() {
        ToimikuntaUtil.siirryJasentenMuokkaukseen($routeParams.id);
      };

      $scope.muokkaaToimialaa = function() {
        ToimikuntaUtil.muokkaaToimialaa($routeParams.id);
      };

      $scope.siirrySopimuksenLuontiin = function() {
        ToimikuntaUtil.siirrySopimuksenLuontiin($routeParams.id);
      };

      function eritteleVoimassaolonMukaan(toimikunta) {
        $scope.nykyisetJasenet = $filter('voimassaOlevat')(toimikunta.jasenyys, true);
        $scope.entisetJasenet = $filter('voimassaOlevat')(toimikunta.jasenyys, false);
        $scope.nykyisetSopimukset = $filter('voimassaOlevat')(toimikunta.jarjestamissopimus, toimikunta.voimassa);
        $scope.entisetSopimukset = $filter('voimassaOlevat')(toimikunta.jarjestamissopimus, !toimikunta.voimassa);
      }

      $scope.$watchCollection('toimikunta', eritteleVoimassaolonMukaan);

      $scope.naytaVanhatSopimukset = false;
      $scope.naytaVanhatJasenyydet = false;
    }
  ])

  .controller('ToimikuntaJasenetController', ['$scope', '$rootScope', '$routeParams', 'toimikuntaResource',
    'ToimikuntaJasenetResource', 'ToimikuntaUtil', '$filter', 'varmistaPoistuminen', 'i18n',
    function($scope, $rootScope, $routeParams, toimikuntaResource, ToimikuntaJasenetResource, ToimikuntaUtil, $filter, varmistaPoistuminen, i18n) {
      $scope.toimikunta = toimikuntaResource.get({"diaarinumero": $routeParams.id});

      $scope.poistaJasen = function(jasen) {
        jasen.poistettu = true;
      };
      $scope.peruutaPoisto = function(jasen) {
        jasen.poistettu = false;
      };
      $scope.siirryJasenenLisaykseen = function() {
        ToimikuntaUtil.siirryJasenenLisaykseen($routeParams.id);
      };
      $scope.tallenna = function(toimikunnanHenkilot) {
        if(varmistaJasentenPoisto(toimikunnanHenkilot)) {
          varmistaPoistuminen.tallenna(
            ToimikuntaJasenetResource.update({diaarinumero: $routeParams.id, jasenet: toimikunnanHenkilot}),
            function() { ToimikuntaUtil.palaaToimikuntaan($routeParams.id); });
        }
      };
      $scope.peruuta = function() {
        varmistaPoistuminen.kysyVarmistusPoistuttaessa();
        ToimikuntaUtil.palaaToimikuntaan($routeParams.id);
      };

      function suodataJasenet(toimikunta) {
        $scope.nykyisetJasenet = $filter('voimassaOlevat')(toimikunta.jasenyys, true);
      }

      function varmistaJasentenPoisto(jasenet) {

        var poistetut = _.filter(jasenet, {poistettu : true});
        var poistetutNimet = _.map(poistetut, function(jasen){
          return jasen.etunimi + ' ' + jasen.sukunimi;
        });

        if(!_.isEmpty(poistetut)) {
          return confirm(i18n.toimikunta['varmista-jasenten-poisto'] + '\n\n' + poistetutNimet.join(', '));
        } else {
          return true;
        }
      }

      $scope.$watchCollection('toimikunta', suodataJasenet);
    }
  ]);
