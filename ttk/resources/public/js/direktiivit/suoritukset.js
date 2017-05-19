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
      controller: ['$location', '$scope', '$timeout', 'Toimikunta', 'Koulutustoimija', 'Suoritus', 'TutkintoResource', 'Tutkinnonosa', 'Osaamisala', 'Varmistus', 'i18n',
         function($location, $scope, $timeout, Toimikunta, Koulutustoimija, Suoritus, TutkintoResource, Tutkinnonosa, Osaamisala, Varmistus, i18n) {

        var haeSuorituksetTimeoutPromise = null;

        $scope.hakuForm = {
            tila: null,
            tuloksiasivulla: "10"
        };
        $scope.filterTila = function(tila) {
          return function(item) {
            return tila === null || tila === item.tila;
          };
        };
        $scope.i18n = i18n;

        $scope.$watch('haeSuorituksia', function(haeSuorituksia) {
          // Tyhjennä hakuehdot kun haku suljetaan
          if (haeSuorituksia === false) {
            $scope.hakuForm = _.pick($scope.hakuForm, 'tila', 'tuloksiasivulla');
          }
        });

        Koulutustoimija.haeKaikkiNimet().then(function(koulutustoimijat) {
          $scope.koulutustoimijat = koulutustoimijat;
        });

        TutkintoResource.query(function(tutkinnot) {
          $scope.tutkinnot = tutkinnot;
        });

        Toimikunta.haeKaikki().then(function(tutkintotoimikunnat) {
          var ei_valittu_ttk = {
              diaarinumero: "",
              nimi_fi: i18n.arviointipaatokset.ei_valittu,
              nimi_sv: i18n.arviointipaatokset.ei_valittu,
              tkunta: "Ei valittu"
          };
          $scope.tutkintotoimikunnat = [ei_valittu_ttk].concat(tutkintotoimikunnat);
        });


        $scope.$watch('hakuForm.tutkinto', function(tutkinto) {
            if (tutkinto != undefined) {
              Tutkinnonosa.hae(tutkinto).then(function(tutkinnonosat) {
                $scope.tutkinnonosat = tutkinnonosat;
//                $scope.hakuForm.tutkinnonosa = _.find($scope.tutkinnonosat, {'tutkinnonosa_id' : $scope.tutkinnonosa.tutkinnonosa_id});
              });
              Osaamisala.hae(tutkinto).then(function(osaamisalat) {
                $scope.osaamisalat = osaamisalat.osaamisala;
              });
            }
          });

        $scope.tila = '';
        $scope.form = {};

        $scope.$watch('tila', function() {
          // Tyhjennä valitut suoritukset filtterin vaihtuessa, että näkymättömiä ei ole valittuna
          $scope.form = {};
        });

        $scope.poistaSuoritus = function(poistettavaSuoritus) {
          Varmistus.varmista(i18n.arviointipaatokset.poistetaanko_suoritus, i18n.arviointipaatokset.poista_suoritus_teksti, i18n.arviointipaatokset.poista_suoritus)
            .then(function() {
              Suoritus.poista(poistettavaSuoritus.suorituskerta_id).then(function() {
                _.remove($scope.suoritukset, function(suoritus) {
                  return suoritus.suorituskerta_id == poistettavaSuoritus.suorituskerta_id;
                })
              });
            });
        };

        $scope.muokkaaSuoritusta = function(muokattavaSuoritus) {
          $location.url('/muokkaa-suoritus/' + muokattavaSuoritus.suorituskerta_id);
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
          // tyhjentää valintalomakkeen checkboxit kun tila muuttuu
          _.forEach(_.keys($scope.form), function(k) { $scope.form[k]=false;});

          Suoritus.haeKaikki($scope.hakuForm).then(function(suoritukset) {
              $scope.suoritukset = suoritukset;
              $scope.suorituksetjarjestetty = suoritukset;
              $scope.suoritussivu = 1;
          });
        };

        $scope.palautaLuonnokseksi = function() {
          Varmistus.varmista(i18n.arviointipaatokset.palautetaanko_suoritus_luonnokseksi, i18n.arviointipaatokset.palauta_suoritus_luonnokseksi_teksti, i18n.arviointipaatokset.palauta_suoritus_luonnokseksi)
            .then(function() {
              var valitutSuoritukset = $scope.valitutSuoritukset();
              Suoritus.palauta(valitutSuoritukset).then(function() {
                paivitaSuoritustenTila(valitutSuoritukset, 'luonnos');
              });
            });
        };

        $scope.palautaHyvaksyttavaksi = function() {
          Varmistus.varmista(i18n.arviointipaatokset.palautetaanko_suoritus_hyvaksyttavaksi, i18n.arviointipaatokset.palauta_suoritus_hyvaksyttavaksi_teksti, i18n.arviointipaatokset.palauta_suoritus_hyvaksyttavaksi)
            .then(function() {
              var valitutSuoritukset = $scope.valitutSuoritukset();
              Suoritus.lahetaHyvaksyttavaksi(valitutSuoritukset).then(function() {
                paivitaSuoritustenTila(valitutSuoritukset, 'ehdotettu');
              });
            });
        };


        $scope.lahetaHyvaksyttavaksi = function() {
          Varmistus.varmista(i18n.arviointipaatokset.esitetaanko_suoritus, i18n.arviointipaatokset.esita_suoritus_teksti, i18n.arviointipaatokset.esita_suoritus)
            .then(function() {
              var valitutSuoritukset = $scope.valitutSuoritukset();
              Suoritus.lahetaHyvaksyttavaksi(valitutSuoritukset).then(function() {
                paivitaSuoritustenTila(valitutSuoritukset, 'ehdotettu');
              });
            });
        };

        $scope.hyvaksy = function() {
          Varmistus.varmista(i18n.arviointipaatokset.hyvaksytaanko_suoritus, i18n.arviointipaatokset.hyvaksy_suoritus_teksti, i18n.arviointipaatokset.hyvaksy_suoritus)
            .then(function() {
              var valitutSuoritukset = $scope.valitutSuoritukset();
              Suoritus.hyvaksy(valitutSuoritukset, $scope.form.hyvaksymispvm).then(function() {
                paivitaSuoritustenTila(valitutSuoritukset, 'hyvaksytty');
              });
            });
        };

        $scope.haeSuoritukset = function() {

          if (haeSuorituksetTimeoutPromise) {
            $timeout.cancel(haeSuorituksetTimeoutPromise);  // cancel the previous pending request
          }
          haeSuorituksetTimeoutPromise = $timeout(Suoritus.haeKaikki, 500, true, $scope.hakuForm);  // 500ms = 0,5s
          haeSuorituksetTimeoutPromise.then(function(suoritukset) {
            $scope.suoritukset = suoritukset;
            $scope.suorituksetjarjestetty = suoritukset;
            $scope.suoritussivu = 1;
          })
        };

        $scope.$watch('hakuForm', function(hakuForm, oldhakuform) {
          if (! (angular.equals(hakuForm,oldhakuform))) { // ei haeta ensimmäisellä sivulatauksella dataa
            $scope.haeSuoritukset();
          }
          }, true);

      }]
    }
  }])
;