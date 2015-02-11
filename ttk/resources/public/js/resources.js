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

angular.module('resources', ['ngRoute'])

  .factory('EnumResource', ['$resource', function($resource) {
    return $resource(ophBaseUrl + '/api/enum/:enum', {'enum': '@enum'}, {
      get: {
        method: 'GET',
        isArray: true,
        id: 'enumeraatio'
      }
    });
  }])

  .factory('toimikuntaResource', ['$resource', '$routeParams', function($resource, $routeParams) {
    return $resource(ophBaseUrl + '/api/ttk/:diaarinumero', {'diaarinumero': '@diaarinumero', 'tkunta' : '@tkunta'}, {
      get: {
        method: 'GET',
        params: {
          nocache: function() {return Date.now();},
          diaarinumero : function() {return $routeParams.id;}
        },
        id: 'toimikunnan-tiedot'
      },
      update: {
        method: 'PUT',
        id:"toimikunnan-tietojen-muokkaus",
        i18n : 'toimikunta'
      },
      save: {
        url: ophBaseUrl + '/api/ttk/',
        method: 'POST',
        id: 'toimikunnan-tallennus',
        i18n : 'toimikunta'
      },
      tutkinnot: {
        method: 'POST',
        id: 'toimikunnan-tutkintojen-muokkaus',
        i18n : 'toimikunta|tutkintojen-muokkaus',
        url : ophBaseUrl + '/api/ttk/:tkunta/tutkinnot'
      }
    });
  }])

  .factory('toimikuntaHakuResource', ['$resource', function($resource) {
    return $resource(ophBaseUrl + '/api/ttk/', {}, {
      query: {
        method: 'GET',
        isArray: true,
        id : 'toimikuntalistaus'
      }
    });
  }])

  .factory('ToimikuntaJasenetResource', ['$resource', function($resource) {
    return $resource(ophBaseUrl + '/api/ttk/:diaarinumero/jasenet', {'diaarinumero': '@diaarinumero'}, {
      update: {
        method: 'PUT',
        id:"muokkaa-toimikunnan-jasenia",
        i18n: 'toimikunta-jasenet'
      }
    });
  }])

  .factory('henkiloVelhoResource', ['$resource', function($resource) {
    return $resource(ophBaseUrl + '/api/ttk/:diaarinumero/jasenet', {'diaarinumero': '@diaarinumero'}, {
      saveJasen : {
        method: 'POST',
        id:"uusi-toimikunnan-jasen",
        i18n : "toimikunta-jasenet"
      },
      saveHenkilo : {
        url: ophBaseUrl + '/api/henkilo/',
        method: 'POST',
        id:"uusi-henkilo",
        i18n : 'henkilo'
      },
      get : {
        url: ophBaseUrl + '/api/henkilo/nimi/:etunimi/:sukunimi',
        method: 'GET',
        isArray: true,
        id:"hae-henkilo-toimikunnan-jaseneksi"
      }
    });
  }])

  .factory('ToimikausiResource', ['$resource', function($resource) {
    return $resource(ophBaseUrl + '/api/toimikausi', {}, {
      query: {
        method: 'GET',
        isArray: true,
        id: 'toimikausilistaus'
      }});
  }]);