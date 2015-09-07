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

angular.module('henkilot', ['ngRoute', 'services', 'crud', 'resources', 'toimikunnat'])

  .config(function($routeProvider) {

    function crudConfig() {
      return {
        basePath : '/henkilot',
        modelProperty : 'jasen',
        subModelProperty : 'henkilo',
        modelIdProperty : 'henkiloid'
      };
    }

    var crudDeps = {resource : "henkiloResource", config : crudConfig };

    $routeProvider.
      when('/henkilot', {controller:'listaaHenkilotController', templateUrl:'template/henkilot'}).
      when('/henkilot/:id/tiedot', {controller:'henkilonTiedotController', templateUrl:'template/henkilo'}).
      when('/henkilot/:id/muokkaa', {controller:'crudController', resolve : crudDeps, templateUrl:'template/henkilo'}).
      when('/henkilot/uusi', {controller:'HenkiloVelhoController', templateUrl:'template/jasen'});
  })

  .factory('henkiloResource', ['$resource', 'i18n', function($resource, i18n) {
    return $resource(ophBaseUrl + '/api/henkilo/:henkiloid', {'henkiloid': '@henkiloid'}, {
      get: {
        method: 'GET',
        params: { nocache: function() { return Date.now(); }},
        id:"henkilon-tiedot"
      },
      query: {
        method: 'GET',
        params: { nocache: function() { return Date.now(); }},
        isArray: true,
        id:"henkilolistaus"
      },
      update: {
        method: 'PUT',
        id:"henkilon-tietojen-muokkaus",
        i18n : 'henkilo'
      }
    });
  }])
  .factory('henkiloUtil', ['$location', function($location) {
    return {
      siirryHenkiloWizardiin: function() { $location.path('/henkilot/uusi'); }
    };
  }])

  .controller('listaaHenkilotController', ['$scope', '$filter', 'henkiloResource', 'henkiloUtil',
    function($scope, $filter, henkiloResource, henkiloUtil) {
      $scope.luoUusi = function(){ henkiloUtil.siirryHenkiloWizardiin(); };
      $scope.henkilot = [];
      $scope.search = {nimi: "",
        toimikunta: "",
        toimikausi: "nykyinen"};

      $scope.$watch('search.nimi', suodataHenkilot);
      $scope.$watch('search.toimikunta', suodataHenkilot);
      $scope.$watchCollection('kaikkiHenkilot', suodataHenkilot);
      $scope.$watch('search.toimikausi', haeHenkilot);

      $scope.haeHenkilot = haeHenkilot;

      function suodataHenkilot() {
        var filteredNimella = $filter('kokonimi')($scope.kaikkiHenkilot, $scope.search.nimi);
        var filteredToimikunnalla = $filter('henkilonToimikunta')(filteredNimella, $scope.search.toimikunta);
        $scope.henkilot = $filter('orderBy')(filteredToimikunnalla, 'sukunimi');
      }

      function haeHenkilot() {
        $scope.kaikkiHenkilot = henkiloResource.query({toimikausi: $scope.search.toimikausi});
        return false;
      }
    }
  ])

  .controller('henkilonTiedotController', ['$scope', '$routeParams', 'crudLocation', 'henkiloResource', '$filter',
    function($scope, $routeParams, crudLocation, resource, $filter) {
      $scope.jasen = { henkilo: {} };
      $scope.muokkaa = muokkaa;

      haeTiedot();

      function muokkaa() {
        crudLocation.siirryMuokkaukseen($routeParams.id, '/henkilot');
      }

      function haeTiedot() {
        $scope.jasen.henkilo = $routeParams.id ? resource.get({henkiloid : $routeParams.id}) : {};
      }

      function eritteleVoimassaolonMukaan(henkilo) {
        $scope.nykyisetJasenet = $filter('voimassaOlevat')(henkilo.jasenyys, true);
        $scope.entisetJasenet = $filter('voimassaOlevat')(henkilo.jasenyys, false);
      }

      $scope.$watchCollection('jasen.henkilo', eritteleVoimassaolonMukaan);
    }
  ])

  .controller('HenkiloVelhoController', ['$scope', '$rootScope', '$routeParams', 'ToimikuntaUtil', 'varmistaPoistuminen', 'henkiloVelhoResource', 'edellinenLokaatio', 'toimikuntaResource',
    function($scope, $rootScope, $routeParams, ToimikuntaUtil, varmistaPoistuminen, henkiloVelhoResource, edellinenLokaatio, toimikuntaResource) {
      var nykyinenAskel = 0;

      function siirrySeuraavaan() {
        if (nykyinenAskel < 2) {
          $rootScope.$broadcast('wizardPageChange');
          nykyinenAskel++;
        }
      }
      $scope.kokemusvuodet = _.range(1, 21);
      $scope.jasen = { henkilo: {} };
      $scope.search = { henkilo: {} };
      $scope.voimassaAlkaen = new Date(new Date().setHours(0,0,0,0));
      $scope.jasen.toimikunta = $routeParams.id ? toimikuntaResource.get({"diaarinumero": $routeParams.id}) : {};
      $scope.lisaaJasen = function(jasen) {
        varmistaPoistuminen.tallenna(
          henkiloVelhoResource.saveJasen(_.assign({diaarinumero: jasen.toimikunta.diaarinumero }, jasen)),
          function() { ToimikuntaUtil.siirryJasentenMuokkaukseen(jasen.toimikunta.diaarinumero); });
      };
      $scope.lisaaHenkiloJaSiirrySeuraavaan = function(henkilo) {
        // Siivotaan hakuvalitsimen malleihin jättämät ylimääräiset kentät pois
        var dto = _.cloneDeep(henkilo);
        delete dto.henkilo;
        if (dto.kayttaja !== undefined) {
          dto.kayttaja_oid = dto.kayttaja.oid;
          delete dto.kayttaja;
        }
        if (dto.jarjesto !== undefined) {
          dto.jarjesto = dto.jarjesto.jarjesto;
        }

        varmistaPoistuminen.tallenna(
          henkiloVelhoResource.saveHenkilo(dto),
          function(response) {
            $scope.jasen.henkilo = response;
            siirrySeuraavaan();
          });
      };
      $scope.peruuta = function() {
        varmistaPoistuminen.kysyVarmistusPoistuttaessa();
        edellinenLokaatio();
      };
      $scope.haeHenkilotJaSiirrySeuraavaan = function() {
        var henkiloValittu = ($scope.search.henkilo.osat !== undefined);
        if(henkiloValittu) {
          $scope.henkilot = henkiloVelhoResource.get(
            {etunimi: $scope.search.henkilo.osat.etunimi, sukunimi: $scope.search.henkilo.osat.sukunimi},
            siirrySeuraavaan);
        } else {
          siirrySeuraavaan();
        }

      };
      $scope.vaihdaTila = function(henkilo) {
        $scope.muokkausTila = !henkilo;
        if (!henkilo) {
          $scope.jasen.henkilo = {};
        }
      };

      $scope.$watchCollection('henkilot', valitseHenkilo);

      $scope.nykyinenAskel = function(askelNumero) {
        return nykyinenAskel === askelNumero;
      };
      $scope.siirrySeuraavaan = siirrySeuraavaan;

      function valitseHenkilo() {
        if ($scope.henkilot && $scope.henkilot.length > 0) {
          $scope.jasen.henkilo = $scope.henkilot[0];
          $scope.muokkausTila = false;
        }
        else {
          $scope.muokkausTila = true;
          $scope.jasen.henkilo = $scope.search;
        }
      }
    }
  ]);

