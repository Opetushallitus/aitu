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

angular.module('rest.suoritus', [])
  .factory('Suoritus', ['$http', function($http) {
    return {
      haeKaikki: function() {
        return $http.get(ophBaseUrl + '/api/suoritus').then(function(response) {
          return response.data;
        });
      },
      lisaa: function(form) {
        return $http.post(ophBaseUrl + '/api/suoritus', form).then(function(response) {
          return response.data;
        });
      },
      lahetaHyvaksyttavaksi: function(suoritukset) {
        return $http.post(ophBaseUrl + '/api/suoritus/laheta', {suoritukset: suoritukset}).then(function(response) {
          return response.data;
        });
      },
      hyvaksy: function(suoritukset) {
        return $http.post(ophBaseUrl + '/api/suoritus/hyvaksy', {suoritukset: suoritukset}).then(function(response) {
          return response.data;
        });
      },
      poista: function(suoritusId) {
        return $http.delete(ophBaseUrl + '/api/suoritus/' + suoritusId).then(function(response) {
          return response.data;
        });
      }
    };
  }])
;