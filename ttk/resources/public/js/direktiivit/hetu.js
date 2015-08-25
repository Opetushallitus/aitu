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

angular.module('direktiivit.hetu', [])
  .directive('hetu', [function() {
    return {
      restrict: 'A',
      require: 'ngModel',
      link: function(scope, element, attrs, controller) {
        var pattern = /^[0-9]{6}[-+A][0-9]{3}[0-9ABCDEFHJKLMNPRSTUVWXY]$/;
        var tarkistusmerkit = '0123456789ABCDEFHJKLMNPRSTUVWXY';

        controller.$validators.hetu = function(modelValue, viewValue) {
          if (controller.$isEmpty(modelValue)) {
            return false;
          }

          if (!pattern.test(modelValue)) {
            return false;
          }

          // Tarkista tarkistusmerkki
          var lukuarvo = parseInt(modelValue.substr(0, 6) + modelValue.substr(7, 3));
          var tarkistusmerkki = tarkistusmerkit[lukuarvo % 31];

          return modelValue[10] === tarkistusmerkki;
        }
      }
    }
  }])
;