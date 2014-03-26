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

angular.module('testResource', ['ngResource']).factory('resource', function($resource){
  return $resource('/api/henkilot/:henkiloid', {'henkiloid': '@henkiloid'}, {
    get: {
      method: 'GET'
    },
    save : {
      method: 'POST'
    },
    update: {
      method: 'PUT'
    }
  });
});

var EnumResource;
function initEnumResource($provide) {
  EnumResource = {
    get: jasmine.createSpy('get')
  };
  $provide.value('EnumResource', EnumResource);
}

describe('crudController', function () {
  var httpBackend, config, scope, $rootScope;

  beforeEach(function(){
    config = {
      basePath : '/henkilot' ,
      modelIdProperty : 'henkiloid',
      modelProperty : 'henkilo',
      enumProperty: 'edustus'
    };
    module('crud');
    module('testResource');
    module('mock.i18n');
    module(initEnumResource);
    module(function($provide){
      $provide.value('$routeParams', {id : 1});
      $provide.value('config', config);
      //Mockataan varmistaPoistuminen, koska beforeUnload alertti hängää testit.
      $provide.value('varmistaPoistuminen', {
        kysyVarmistusPoistuttaessa : function() {},
        tallenna : function(resource, okCallback) {
          var promise = resource.$promise ?  resource.$promise : resource;
          promise.then(
            function(response){
              okCallback(response);
            });
        }
      });
    });
  });

  function captureDependencies() {
    inject(['$rootScope', function(service) {$rootScope = service;}]);
    inject(['$httpBackend', function(service) {httpBackend = service;}]);
  }

  function createCrudController() {
    captureDependencies();
    inject(['$controller', function($controller) {
      scope = $rootScope.$new();
      $controller('crudController', { $scope: scope });
    }]);
  }

  function tarkistaEtteiOdottamattomiaPyyntoja() {
    httpBackend.verifyNoOutstandingExpectation();
    httpBackend.verifyNoOutstandingRequest();
  }

  describe('muokkaustilassa', function() {
    beforeEach(function(){
      module(function($provide){
        $provide.value('$routeParams', {id : 1});
      });
      captureDependencies();
    });

    afterEach(tarkistaEtteiOdottamattomiaPyyntoja);

    it('hakee entiteetin annetulla funktiolla', function() {
      config.haeTiedot = jasmine.createSpy('haeTiedot')
        .andReturn({etunimi: 'Etu', sukunimi: 'Suku'});
      createCrudController();
      expect(config.haeTiedot).toHaveBeenCalled();
      expect(scope.henkilo.etunimi).toEqual('Etu');
      expect(scope.henkilo.sukunimi).toEqual('Suku');
    });

    it("hakee muokattavan entiteetin tiedot jos id asetettu", function() {
      httpBackend.expectGET('/api/henkilot/1').respond({etunimi : "Ahto", sukunimi : "Simakuutio"});
      createCrudController();
      $rootScope.$digest();
      httpBackend.flush();
      expect(scope.henkilo.etunimi).toEqual("Ahto");
      expect(scope.henkilo.sukunimi).toEqual("Simakuutio");
    });

    it("kutsuu tiedot tallettavaa apimetodia", function() {
      httpBackend.expectGET('/api/henkilot/1').respond({});
      httpBackend.expect('PUT', '/api/henkilot', {etunimi: 'AhtoMuokattu', sukunimi : "SimakuutioMuokattu"} ).respond({});
      createCrudController();
      scope.henkilo.etunimi = "AhtoMuokattu";
      scope.henkilo.sukunimi = "SimakuutioMuokattu";
      scope.tallenna();
      $rootScope.$digest();
      httpBackend.flush();
    });
  });

  describe('uuden entiteetin luomistilassa', function() {
    beforeEach(function(){
      module(function($provide){
        $provide.value('$routeParams', {});
      });
    });

    afterEach(tarkistaEtteiOdottamattomiaPyyntoja);

    it('hakee entiteetin alkuarvon annetulla funktiolla', function() {
      config.haeTiedot = jasmine.createSpy('haeTiedot')
        .andReturn({etunimi: 'Etu'});
      createCrudController();
      expect(config.haeTiedot).toHaveBeenCalled();
      expect(scope.henkilo.etunimi).toEqual('Etu');
    });

    it("ei hae entiteetin tietoja apilta, jos id:tä ei ole asettu", function() {
      createCrudController();
      $rootScope.$digest();
      expect(scope.henkilo).toEqual({});
    });

    it("kutsuu tiedot tallettavaa apimetodia", function() {
      captureDependencies();
      httpBackend.expect('POST', '/api/henkilot', {etunimi: 'Sihto', sukunimi : "Amakuutio"} ).respond({});
      createCrudController();
      scope.henkilo.etunimi = "Sihto";
      scope.henkilo.sukunimi = "Amakuutio";
      scope.tallenna();
      $rootScope.$digest();
      httpBackend.flush();
    });
  });

  describe('tallennuksen paluuosoite:', function() {
    beforeEach(function() {
      module('mock.services.varmistaPoistuminen', 'mock.crud.crudLocation', 'mock.angular.location');
    });

    function tallennusOnnistuuPaluuarvonaan(arvo) {
      inject(function(varmistaPoistuminen) {
        var okCallback = varmistaPoistuminen.tallenna.mostRecentCall.args[1];
        okCallback(arvo);
      });
    }

    describe('oletusosoite:', function() {
      beforeEach(function() {
        config.basePath = '/henkilot';
        config.modelIdProperty = 'henkiloid';

        createCrudController();
        scope.tallenna();
      });

      it('siirtyy onnistuneen tallennuksen jälkeen tiedot-sivulle', function() {
        tallennusOnnistuuPaluuarvonaan({henkiloid: 'xyz'});
        inject(function(crudLocation) {
          expect(crudLocation.naytaTiedot).toHaveBeenCalledWith('xyz', '/henkilot');
        });
      });
    });

    describe('kutsuja muodostaa osoitteen:', function() {
      beforeEach(function() {
        config.muodostaPaluuosoite = jasmine.createSpy('muodostaPaluuosoite');
        createCrudController();
        scope.tallenna();
      });

      it('muodostaa onnistuneen tallennuksen jälkeen paluuosoitteen', function() {
        tallennusOnnistuuPaluuarvonaan({avain: 'arvo'});
        expect(config.muodostaPaluuosoite).toHaveBeenCalledWith({avain: 'arvo'});
      });

      it('siirtyy onnistuneen tallennuksen jälkeen paluuosoitteeseen', function() {
        config.muodostaPaluuosoite.andReturn('paluuosoite');
        tallennusOnnistuuPaluuarvonaan({});
        inject(function($location) {
          expect($location.path).toHaveBeenCalledWith('paluuosoite');
        });
      });
    });
  });
});