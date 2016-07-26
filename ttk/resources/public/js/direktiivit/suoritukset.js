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

'use strict';

angular.module('direktiivit.suoritukset', ['rest.suoritus'])
  .directive('suoritukset', [function() {
    return {
      restrict: 'E',
      templateUrl: 'template/direktiivit/suoritukset',
      scope: {},
      controller: ['$scope', 'Koulutustoimija', 'Rahoitusmuoto', 'Suoritus', 'TutkintoResource', 'Varmistus', 'i18n', function($scope, Koulutustoimija, Rahoitusmuoto, Suoritus, TutkintoResource, Varmistus, i18n) {
        $scope.hakuForm = {};
        $scope.i18n = i18n;

        $scope.$watch('haeSuorituksia', function(haeSuorituksia) {
          // Tyhjennä hakuehdot kun haku suljetaan
          if (haeSuorituksia === false) {
            $scope.hakuForm = _.pick($scope.hakuForm, 'tila');
          }
        });

        Koulutustoimija.haeKaikkiNimet().then(function(koulutustoimijat) {
          $scope.koulutustoimijat = koulutustoimijat;
        });

        Rahoitusmuoto.haeKaikki().then(function(rahoitusmuodot) {
          $scope.rahoitusmuodot = rahoitusmuodot;
        });

        TutkintoResource.query(function(tutkinnot) {
          $scope.tutkinnot = tutkinnot;
        });

        $scope.$watch('hakuForm', function(hakuForm) {
          Suoritus.haeKaikki(hakuForm).then(function(suoritukset) {
            $scope.suoritukset = suoritukset;
          });
        }, true);

        $scope.tila = '';
        $scope.form = {};

        $scope.$watch('tila', function() {
          // Tyhjennä valitut suoritukset filtterin vaihtuessa, että näkymättömiä ei ole valittuna
          $scope.form = {};
        });

        $scope.poistaSuoritus = function(poistettavaSuoritus) {
          Varmistus.varmista(i18n.arviointipaatokset.poistetaanko_suoritus, i18n.arviointipaatokset.poista_suoritus_teksti, i18n.arviointipaatokset.poista_suoritus).then(function() {
            Suoritus.poista(poistettavaSuoritus.suorituskerta_id).then(function() {
              _.remove($scope.suoritukset, function(suoritus) {
                return suoritus.suorituskerta_id == poistettavaSuoritus.suorituskerta_id;
              })
            });
          });
        };
        
        $scope.muokkaaSuoritusta = function(muokattavaSuoritus) {
        	return alert('Kesken, OPH-1502');        	
        };

        $scope.valitutSuoritukset = function() {
          return _.chain($scope.form).pairs().filter(function(x) { return x[1]; }).map(function(x) { return parseInt(x[0]); }).value();
        };

        var paivitaSuoritustenTila = function(suoritukset, tila) {
          _.forEach(suoritukset, function(valittuSuoritus) {
            var suoritus = _.find($scope.suoritukset, {suorituskerta_id: valittuSuoritus});
            if (suoritus !== undefined) {
              suoritus.tila = tila;
            }
          });
        };

        $scope.lahetaHyvaksyttavaksi = function() {
          Varmistus.varmista(i18n.arviointipaatokset.esitetaanko_suoritus, i18n.arviointipaatokset.esita_suoritus_teksti, i18n.arviointipaatokset.esita_suoritus).then(function() {
            var valitutSuoritukset = $scope.valitutSuoritukset();
            Suoritus.lahetaHyvaksyttavaksi(valitutSuoritukset).then(function() {
              paivitaSuoritustenTila(valitutSuoritukset, 'ehdotettu');
            });
          });
        };

        $scope.hyvaksy = function() {
          Varmistus.varmista(i18n.arviointipaatokset.hyvaksytaanko_suoritus, i18n.arviointipaatokset.hyvaksy_suoritus_teksti, i18n.arviointipaatokset.hyvaksy_suoritus).then(function() {
            var valitutSuoritukset = $scope.valitutSuoritukset();
            Suoritus.hyvaksy(valitutSuoritukset).then(function() {
              paivitaSuoritustenTila(valitutSuoritukset, 'hyvaksytty');
            });
          });
        };

        Suoritus.haeKaikki().then(function(suoritukset) {
          $scope.suoritukset = suoritukset;
        })
      }]
    }
  }])
;