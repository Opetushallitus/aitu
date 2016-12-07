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

angular.module('rest.suorittaja', [])
  .factory('Suorittaja', ['$http', function($http) {
    return {
      hae: function(suorittajaid) {
          return $http.get(ophBaseUrl + '/api/suorittaja/' + suorittajaid).then(function(response) {
              return response.data;
            });
      },
      haeKaikki: function() {
        return $http.get(ophBaseUrl + '/api/suorittaja').then(function(response) {
          return response.data;
        });
      },
      lisaa: function(form) {
        return $http.post(ophBaseUrl + '/api/suorittaja', form).then(
        		function successCallback(response) {
        			return response.data;
        		}, function errorCallback(response) {
        			// {"data":{"errors":["hetu","Viallinen henkilötunnus"]},
        			alert("Virhe: " + response["data"]["errors"][1]); // TODO: rumaa. OPH-1877
        			});        		
      },
      poista: function(suorittaja) {
        return $http.delete(ophBaseUrl + '/api/suorittaja/' + suorittaja.suorittaja_id).then(function(response) {
          return response.data;
        });
      },
      tallenna: function(suorittaja) {
        return $http.put(ophBaseUrl + '/api/suorittaja/' + suorittaja.suorittaja_id, suorittaja).then(
        		function successCallback(response) {
        			return response.data;
        		}, function errorCallback(response) {
        			// {"data":{"errors":["hetu","Viallinen henkilötunnus"]},
        			alert("Virhe: " + response["data"]["errors"][1]); // TODO: rumaa. OPH-1877
        			});
      }
    };
  }])
;