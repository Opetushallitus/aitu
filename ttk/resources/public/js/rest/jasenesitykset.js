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

angular.module('rest.jasenesitykset', [])
  .factory('Jasenesitykset', ['$http', function($http) {
    return {
      hae: function(ehdokas, jarjesto, toimikunta, asiantuntijaksi, tila) {
        return $http.get(ophBaseUrl + '/api/jasenesitykset', {params: {ehdokas: ehdokas, jarjesto: jarjesto, toimikunta: toimikunta, asiantuntijaksi: asiantuntijaksi, tila: tila}}).then(function(response) {
          return response.data;
        });
      },
      haeYhteenveto: function(search) {
        return $http.get(ophBaseUrl + '/api/jasenesitykset/yhteenveto', {params: search}).then(function(response) {
          return response.data;
        });
      },
      poista: function(jasenyysId) {
        return $http.delete(ophBaseUrl + '/api/jasenesitykset/jasenyys/' + jasenyysId).then(function(response) {
          return response.data;
        });
      }
    };
  }])
;