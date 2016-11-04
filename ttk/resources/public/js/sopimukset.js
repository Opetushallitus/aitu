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

'use strict';

(function(){

  function controllerConfig() {
    return {
      basePath : '/sopimus',
      modelProperty : 'sopimus',
      modelIdProperty : 'jarjestamissopimusid',
      tutkinnotProperty : 'sopimus_ja_tutkinto'
    };
  }

  angular.module('sopimukset', ['ngRoute', 'ngResource', 'crud', 'services', 'tutkinnot', 'toimikunnat'])

    .factory('sopimuksenLuonninAsetukset', ['toimikuntaResource', '$routeParams', function (toimikuntaResource, $routeParams) {
      function haeTiedot() {
        var sopimus = {};
        var toimikunta = toimikuntaResource.get({'diaarinumero': $routeParams.toimikunta});
        toimikunta.$promise.then(function(toimikunta) {
          return _.extend(sopimus, {
            toimikunta: toimikunta
          });
        });
        return sopimus;
      }

      return _.extend({
        haeTiedot: haeTiedot,
        luontiTila: true,
        muodostaPaluuosoite: function(sopimus) {
          return '/sopimus/' + sopimus.jarjestamissopimusid + '/tutkinnot';
        }
      }, controllerConfig());
    }])

    .config(function($routeProvider) {
      var resolve = {resource : 'sopimusResource', config : controllerConfig, peruste: function() { return true; }};

      var luonninRiippuvuudet = {resource: 'sopimusResource', config: 'sopimuksenLuonninAsetukset'};

      $routeProvider.
        when('/sopimus/:id/tiedot', {controller:'sopimuksenTiedotController', templateUrl: 'template/sopimus'}).
        when('/sopimus/:id/muokkaa', {controller:'crudController', resolve: resolve, templateUrl: 'template/sopimus'}).
        when('/sopimus/uusi', {controller:'crudController', resolve: luonninRiippuvuudet, templateUrl: 'template/sopimus'}).
        when('/sopimus/:id/tutkinnot', {controller:'TutkintojenLisaysController', resolve : resolve,  templateUrl: 'template/tutkintojen-lisays'});
    })

    .factory('sopimusResource', ['$resource', '$routeParams', function($resource, $routeParams) {
      return $resource(ophBaseUrl + '/api/jarjestamissopimus/:jarjestamissopimusid', {'jarjestamissopimusid': '@jarjestamissopimusid', 'toimikunta': '@toimikunta.tkunta'}, {
        get: {
          method: 'GET',
          params: {
            nocache: function() {return Date.now();},
            jarjestamissopimusid : function() {return $routeParams.id;}
          },
          id: 'sopimuksen-tiedot'
        },
        update: {
          method: 'PUT',
          id: 'sopimuksen-tietojen-muokkaus',
          i18n : 'jarjestamissopimus'
        },
        save: {
          method: 'POST',
          transformRequest: function(sopimus) {
            return angular.toJson(_.extend({sopijatoimikunta: sopimus.toimikunta}, sopimus));
          },
          id: 'sopimuksen-luonti',
          i18n: 'jarjestamissopimus',
          url: ophBaseUrl + '/api/jarjestamissopimus/:toimikunta'
        },
        delete : {
          method: 'DELETE',
          id: 'sopimuksen-poisto',
          i18n: 'jarjestamissopimus'
        },
        tutkinnot: {
          method: 'POST',
          id: 'sopimuksen-tutkintojen-muokkaus',
          i18n : 'jarjestamissopimus|tutkintojen-muokkaus',
          url : ophBaseUrl + '/api/jarjestamissopimus/:jarjestamissopimusid/tutkinnot'
        }
      });
    }])

    .factory('jarjestamissuunnitelmaResource', ['$resource', '$routeParams', function($resource) {
      return $resource(ophBaseUrl + '/api/jarjestamissopimus/:sopimus_id/:liitetyyppi/:id', {}, {
        delete: {
          method: 'DELETE'
        }
      });
    }])

    .controller('sopimuksenTiedotController', ['$scope', '$routeParams', 'sopimusResource', 'crudLocation', '$location', '$anchorScroll', 'i18n',
      function($scope, $routeParams, sopimusResource, crudLocation, $location, $anchorScroll, i18n){
        $scope.sopimus = sopimusResource.get();

        $scope.salliMuokkaus = function() {
          return !$scope.muokkausTila;
        };

        $scope.muokkaa = function(hash) {
          crudLocation.siirryMuokkaukseen($routeParams.id, '/sopimus', hash);
        };

        $scope.poista = function() {
          if(confirm(i18n.jarjestamissopimus['varmista-sopimuksen-poisto'])) {
            sopimusResource.delete({jarjestamissopimusid : $routeParams.id }, function(){
              siirryToimikunnanSivulle($scope.sopimus.toimikunta.diaarinumero);
            });
          }
        };

        $scope.muokkaaTutkintoja = function() {
          $location.path('/sopimus/' + $routeParams.id + '/tutkinnot');
        };
        $scope.siirryToimikunnanSivulle = siirryToimikunnanSivulle;

        $scope.sopimusJaTutkintoOtsikko = function(sopimusJaTutkinto) {
          var otsikko = '';
          var osat = [];

          var tutkinnonOsia = _.isEmpty(sopimusJaTutkinto.sopimus_ja_tutkinto_ja_tutkinnonosa) ? 0 : sopimusJaTutkinto.sopimus_ja_tutkinto_ja_tutkinnonosa.length;
          var osaamisAloja = _.isEmpty(sopimusJaTutkinto.sopimus_ja_tutkinto_ja_osaamisala) ? 0 : sopimusJaTutkinto.sopimus_ja_tutkinto_ja_osaamisala.length;

          if(tutkinnonOsia === 0 && osaamisAloja === 0) {
            otsikko = i18n.jarjestamissopimus['koko-tutkinto'];
          } else {
            if (tutkinnonOsia > 0) {
              osat.push(i18n.jarjestamissopimus['tutkinnon-osia'] + ': ' + tutkinnonOsia + '/' + sopimusJaTutkinto.tutkintoversio.tutkinnonosa.length);
            }
            if (osaamisAloja > 0) {
              osat.push(i18n.jarjestamissopimus['osaamisaloja'] + ': ' + osaamisAloja + '/' + sopimusJaTutkinto.tutkintoversio.osaamisala.length);
            }
            otsikko = osat.join(', ');
          }
          return ' (' + otsikko + ')';
        };

        function siirryToimikunnanSivulle(toimikunta) {
          $location.path('/toimikunta/' + toimikunta + '/tiedot');
        }
      }])

    .controller('jarjestamissuunnitelmaController', ['$scope', '$routeParams', 'jarjestamissuunnitelmaResource', 'i18n', function($scope, $routeParams, resource, i18n){
      $scope.uploadValmis = function(r, liitetyyppi) {
        if(r.result === "failed") {
          alert(i18n.yleiset['virus-havaittu']);
        } else if(r.result === "error") {
          alert(i18n.yleiset['virusskannaus-yhteysvirhe']);
        } else {
          $scope.sopimusJaTutkinto[liitetyyppi].push(r);
        }
      };

      $scope.poistaSuunnitelma = function() {
        var suunnitelma = { sopimus_id: $routeParams.id, id: this.suunnitelma.jarjestamissuunnitelma_id, liitetyyppi: 'suunnitelma' };
        if( confirm(i18n.jarjestamissopimus['varmista-suunnitelman-poisto']) ) {
          resource.delete(suunnitelma, function(r) {
            _.remove($scope.sopimusJaTutkinto.jarjestamissuunnitelmat, function(s){return s.jarjestamissuunnitelma_id === suunnitelma.id;});
          });
        }
      };
      $scope.poistaLiite = function() {
        var liite = { sopimus_id: $routeParams.id, id: this.liite.sopimuksen_liite_id, liitetyyppi: 'liite' };
        if( confirm(i18n.jarjestamissopimus['varmista-liitteen-poisto']) ) {
          resource.delete(liite, function(r) {
            _.remove($scope.sopimusJaTutkinto.liitteet, function(s){ return s.sopimuksen_liite_id === liite.id; });
          });
        }
      };
    }])

    .directive('sopimuksenTutkinnonosat', function() {
      return {
        restrict: 'E',
        replace: true,
        templateUrl: 'template/sopimuksen-tutkinnonosat',
        scope : {
          toimipaikat : '=',
          valitutTutkinnonosat : '=',
          kaikkiTutkinnonosat : '=',
          valitutOsaamisalat : '=',
          kaikkiOsaamisalat : '=',
          muokkaus : '='
        },
        controller : 'sopimuksenTutkinnonosatController'
      };
    })
    .controller('sopimuksenTutkinnonosatController', ['$scope', function($scope){
      $scope.valitutTutkinnonosat = $scope.valitutTutkinnonosat ? $scope.valitutTutkinnonosat : [];
      $scope.valitutOsaamisalat = $scope.valitutOsaamisalat ? $scope.valitutOsaamisalat : [];
      $scope.kaikkiTutkinnonosat = $scope.kaikkiTutkinnonosat ? $scope.kaikkiTutkinnonosat : [];
      $scope.kaikkiOsaamisalat = $scope.kaikkiOsaamisalat ? $scope.kaikkiOsaamisalat : [];

      $scope.$watchCollection('valitutTutkinnonosat', function(value){
        paivitaValitseKaikki(value, $scope.valitutOsaamisalat);
      });

      $scope.$watchCollection('valitutOsaamisalat', function(value){
        paivitaValitseKaikki(value, $scope.valitutTutkinnonosat);
      });

      $scope.valitseKaikkiChange = function() {
        if($scope.valitseKaikki === 'true') {
          //Valitaan koko tutkinto, ei erillisiä tutkinnonosia tai osaamisaloja
          $scope.valitutTutkinnonosat = [];
          $scope.valitutOsaamisalat = [];
        } else {
          //Esivalittuja kaikki tutkinnon osat ja osaamisalat. Käyttäjä voi poistaa haluamansa.
          $scope.valitutTutkinnonosat = _.map($scope.kaikkiTutkinnonosat.slice(), liitaToimipaikka);
          $scope.valitutOsaamisalat = _.map($scope.kaikkiOsaamisalat.slice(), liitaToimipaikka);
        }
      };

      $scope.tutkinnonosaValittu = function(tutkinnonosa) {
        return onkoValittu(tutkinnonosa, 'osatunnus', 'valitutTutkinnonosat');
      };

      $scope.osaamisalaValittu = function(osaamisala) {
        return onkoValittu(osaamisala, 'osaamisalatunnus', 'valitutOsaamisalat');
      };

      $scope.valitseTutkinnonosa = function(tutkinnonosa) {
        valitse(tutkinnonosa, 'osatunnus', 'valitutTutkinnonosat');
      };

      $scope.valitseOsaamisala = function(osaamisala) {
        valitse(osaamisala, 'osaamisalatunnus', 'valitutOsaamisalat');
      };

      $scope.naytaTutkinnonosienValinta = function () {
        return $scope.muokkaus && ($scope.kaikkiTutkinnonosat.length > 0 || $scope.kaikkiOsaamisalat.length > 0);
      };

      $scope.naytaTutkinnonosat = function () {
        if($scope.muokkaus && $scope.valitseKaikki === 'false') {
          return $scope.kaikkiTutkinnonosat.length > 0;
        } else {
          return $scope.valitutTutkinnonosat.length > 0;
        }
      };

      $scope.naytaOsaamisalat = function () {
        if($scope.muokkaus && $scope.valitseKaikki === 'false') {
          return $scope.kaikkiOsaamisalat.length > 0;
        } else {
          return $scope.valitutOsaamisalat.length > 0;
        }
      };

      $scope.valitseToimipaikka = function(osa, valitutOsat, tunnisteProperty) {
        var valittuOsa = _.find(valitutOsat, function(t){return t[tunnisteProperty] === osa[tunnisteProperty];});

        if(valittuOsa) {
          valittuOsa.toimipaikka = osa.toimipaikka;
        }
      };

      function paivitaValitseKaikki() {
        $scope.valitseKaikki = _.every(arguments, _.isEmpty).toString();
      }

      function onkoValittu(tutkinnonosaTaiOsaamisala, tunnusProperty, valitutProperty) {
        return _.find($scope[valitutProperty], function(t){return t[tunnusProperty] === tutkinnonosaTaiOsaamisala[tunnusProperty]}) !== undefined;
      }

      function valitse(tutkinnonosaTaiOsaamisala, tunnusProperty, valitutProperty) {
        if(onkoValittu(tutkinnonosaTaiOsaamisala, tunnusProperty, valitutProperty)) {
          _.remove($scope[valitutProperty], function(t) {return t[tunnusProperty] === tutkinnonosaTaiOsaamisala[tunnusProperty];});
        } else {
          $scope[valitutProperty].push(liitaToimipaikka(tutkinnonosaTaiOsaamisala));
        }
      }

      function liitaToimipaikka(osa) {
        var o = _.clone(osa);
        if(!osa.toimipaikka && $scope.toimipaikat && $scope.toimipaikat.length > 0) {
          _.assign(o, {toimipaikka : $scope.toimipaikat[0].toimipaikkakoodi});
        }
        return o;
      }
    }])

    .directive('sopimuksenTutkinnonVastuuhenkilo', function() {
      return {
        restrict: 'E',
        replace: true,
        templateUrl: 'template/sopimuksen-tutkinnon-vastuuhenkilo',
        scope : {
          muokkaus : '=',
          otsikko : '@',
          nimi : '=',
          sahkoposti : '=',
          puhelin : '=',
          nayttotutkintomestari : '=',
          lisatiedot : '='
        }
      };
    })

    .filter('jarjestaValitutJarjestysnumerolla', ['$filter', function($filter) {
      return function(entityt, tutkinnonosatJarjestysnumerolla, reverse){
        return $filter('orderBy')(entityt, function(entity){
          var tutkinnonosaJaJarjestysnumero = _.find(tutkinnonosatJarjestysnumerolla, {osatunnus: entity.osatunnus});
          return tutkinnonosaJaJarjestysnumero? parseInt(tutkinnonosaJaJarjestysnumero.jarjestysnumero) : 0;
        }, reverse);
      };
    }])

    .filter('naytaJarjestysnumeroValitulleTutkinnonosalle', function(){
      return function(tutkinnonosa, tutkinnonosatJarjestysnumerolla) {
        var tutkinnonosaJaJarjestysnumero = _.find(tutkinnonosatJarjestysnumerolla, {osatunnus: tutkinnonosa.osatunnus});
        return tutkinnonosaJaJarjestysnumero? tutkinnonosaJaJarjestysnumero.jarjestysnumero : '';
      };
    });
})();
