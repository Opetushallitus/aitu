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

angular.module('direktiivit.kokemusvuodet', [])
  .directive('kokemusvuodet', ['i18n', function(i18n) {
    return {
      restrict: 'E',
      templateUrl: 'template/direktiivit/kokemusvuodet',
      scope: {
        arvo: '=',
        muokattavissa: '='
      },
      link: function(scope) {
        scope.i18n = i18n;

        scope.vaihtoehdot = [
          {arvo: null, avain: 'kokemus_ei_tiedossa'},
          {arvo: 0, avain: 'kokemus_ei_lainkaan'},
          {arvo: 2, avain: 'kokemus_1_3_vuotta'},
          {arvo: 5, avain: 'kokemus_4_6_vuotta'},
          {arvo: 7, avain: 'kokemus_7_tai_enemman'}
        ];

        scope.haeTeksti = function(vuodet) {
          var vaihtoehto = _.find(scope.vaihtoehdot, {arvo: vuodet});
          if (vaihtoehto !== undefined) {
            return i18n.jasenesitykset[vaihtoehto.avain];
          }
          return vuodet;
        };
      }
    }
  }])
;