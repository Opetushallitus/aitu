angular.module('kayttooikeudet', ['ngResource'])

  .factory('kayttooikeudet', ['$resource', 'apiCallInterceptor', function($resource, apiCallInterceptor) {
    var resource = $resource(ophBaseUrl + '/api/kayttaja', null, {
      get: {
        method: 'GET',
        params: { nocache: function() { return Date.now(); }},
        id:"henkilon-tiedot"
      }
    });

    var oikeudet;

    function paivitaOikeudet() {
      oikeudet = resource.get().$promise;
    }

    paivitaOikeudet();

    apiCallInterceptor.asetaVastausCallback('sopimuksen-luonti', paivitaOikeudet);

    return {
      hae : function() {
        return oikeudet;
      },
      paivita : paivitaOikeudet
    }
  }])

  .run(['kayttooikeudet', function(kayttooikeudet) {}])
;
