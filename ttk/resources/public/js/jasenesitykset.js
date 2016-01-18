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

angular.module('jasenesitykset', ['ngRoute', 'rest.jasenesitykset'])
  .config(['$routeProvider', function($routeProvider) {
    $routeProvider.when('/jasenesitykset', {controller: 'JasenesityksetController', templateUrl: 'template/jasenesitykset'});
    $routeProvider.when('/jasenesitykset/uusi', {controller:'HenkiloVelhoController', templateUrl:'template/jasen'});
    $routeProvider.when('/jasenesitykset/yhteenveto', {controller:'JasenesityksetYhteenvetoController', templateUrl: 'template/jasenesitykset-yhteenveto'});
  }])

  .controller('JasenesityksetController', ['$filter', '$location', '$q', '$scope', 'Jasenesitykset', 'Kayttaja', 'i18n', 'kayttooikeudet', function($filter, $location, $q, $scope, Jasenesitykset, Kayttaja, i18n, kayttooikeudet) {
    $scope.haku = {
      asiantuntijaksi: null
    };

    // Hakuvalitsin-workaroundit
    $scope.$watch('hakuvalitsin_jarjesto', function(jarjesto) {
      $scope.haku.jarjesto = jarjesto && jarjesto.jarjesto;
    });
    $scope.$watch('hakuvalitsin_toimikunta', function(toimikunta) {
      $scope.haku.toimikunta = toimikunta && toimikunta.tkunta;
    });

    $scope.luoJasenesitys = function() {
      $location.url('/jasenesitykset/uusi');
    };

    var kirjautunutKayttaja;
    kayttooikeudet.hae().then(function(oikeudet) {
      kirjautunutKayttaja = oikeudet;
    });

    $scope.poistaJasenesitys = function(esitys) {
      if (confirm(i18n.jasenesitykset.poistetaanko)) {
        Jasenesitykset.poista(esitys.jasenyys_id).then(function() {
          _.remove($scope.esitykset, function(e) { return e.jasenyys_id == esitys.jasenyys_id; });
        });
      }
    };

    $scope.poistoSallittu = function(esitys) {
      return esitys.status === 'esitetty' && (kirjautunutKayttaja.roolitunnus === 'YLLAPITAJA' || kirjautunutKayttaja.oid === esitys.luotu_kayttaja);
    };

    $scope.yhteenveto = function() {
      $location.url('/jasenesitykset/yhteenveto');
    };

    Kayttaja.haeJarjesto().then(function(jarjesto) {
      if (jarjesto.nimi_fi !== undefined) {
        $scope.jarjesto = jarjesto;
      }
    });

    $scope.$watch('haku', function(haku) {
      Jasenesitykset.hae(haku.ehdokas, haku.jarjesto, haku.toimikunta, haku.asiantuntijaksi, haku.tila).then(function(esitykset) {
        $scope.esitykset = $filter('orderByLokalisoitu')(esitykset, 'tutkintotoimikunta_nimi');
      });
    }, true);
  }])

  .controller('JasenesityksetYhteenvetoController', ['$filter', '$scope', 'Jasenesitykset', 'ToimikausiResource', function($filter, $scope, Jasenesitykset, ToimikausiResource) {
    $scope.search = {};
    $scope.summat = {};
    $scope.sarakekentat = ['esitetty_yhteensa', 'esitetty_miehia', 'esitetty_naisia', 'esitetty_fi', 'esitetty_sv', 'nimitetty_yhteensa', 'nimitetty_miehia', 'nimitetty_naisia', 'nimitetty_fi', 'nimitetty_sv'];

    ToimikausiResource.query({}, function(toimikaudet) {
      $scope.toimikaudet = toimikaudet;
      $scope.search.toimikausi = toimikaudet[0].toimikausi_id;

      $scope.$watch('search', function(search) {
        paivita();
      }, true);
    });

    $scope.$watch('filter', function() {
      paivitaSummat();
    });

    var paivitaSummat = function() {
      var toimikunnat = $filter('filter')($scope.toimikunnat, $scope.filter);

      _.forEach($scope.sarakekentat, function(kentta) {
        $scope.summat[kentta] = _.reduce(_.pluck(toimikunnat, kentta), function(total, n) { return total + n; }, 0);
      });
    };

    var paivita = function() {
      Jasenesitykset.haeYhteenveto($scope.search).then(function(toimikunnat) {
        _.forEach(toimikunnat, function(toimikunta) {
          toimikunta.esitetty_yhteensa = toimikunta.esitetty_miehia + toimikunta.esitetty_naisia;
          toimikunta.nimitetty_yhteensa = toimikunta.nimitetty_miehia + toimikunta.nimitetty_naisia;
        });

        $scope.toimikunnat = toimikunnat;
        paivitaSummat();
      });
    };
  }])
;