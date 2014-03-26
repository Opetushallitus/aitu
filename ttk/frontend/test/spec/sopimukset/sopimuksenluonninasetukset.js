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

describe('Factory: sopimukset: sopimuksenLuonninAsetukset:', function () {

  // lataa palvelun moduuli
  beforeEach(module('sopimukset'));

  var $routeParams, toimikuntaResource;
  var captureDependencies = ['$routeParams', 'toimikuntaResource', function(service1, service2) {
    $routeParams = service1;
    toimikuntaResource = service2;
  }];

  function initDependencies() {
    module('mock.$routeParams', 'mock.toimikunnat.toimikuntaResource');
    inject(captureDependencies);
  }

  // alusta tehdas
  var sopimuksenLuonninAsetukset;
  var initFactory = ['sopimuksenLuonninAsetukset', function(factory) {
    sopimuksenLuonninAsetukset = factory;
  }];

  // luodaan ympäristö simuloimaan toimikunnan hakua
  // tämä on karvalakkimalli, yleisemmin tällaisen testin
  // tulisi käyttää oikeaa promisea.
  var toimikuntaPromise, toimikuntaResult;
  function alustaToimikuntahaunTestaus() {
    toimikuntaResult = {};
    toimikuntaPromise = {
      then: jasmine.createSpy('then').andReturn(toimikuntaResult)
    };
    toimikuntaResource.get = jasmine.createSpy('get').andReturn({
      $promise: toimikuntaPromise
    });
  }

  function palautaToimikunta(toimikunta) {
    var callback = toimikuntaPromise.then.mostRecentCall.args[0];
    _.extend(toimikuntaResult, callback(toimikunta));
  }

  function asetaHakuehdoksi(toimikunta) {
    $routeParams.toimikunta = toimikunta;
  }

  beforeEach(function() {
    initDependencies();
    alustaToimikuntahaunTestaus();
    inject(initFactory);
  });

  it('pitäisi asettaa luontitila', function() {
    expect(sopimuksenLuonninAsetukset.luontiTila).toBe(true);
  });

  it('pitäisi asettaa tietojen haku', function() {
    expect(sopimuksenLuonninAsetukset.haeTiedot).toBeDefined();
  });

  it('pitäisi asettaa paluuosoitteen muodostus', function() {
    expect(sopimuksenLuonninAsetukset.muodostaPaluuosoite).toBeDefined();
  });

  it('pitäisi tehdä muut asetukset kontrolleria varten', function() {
    expect(sopimuksenLuonninAsetukset.modelProperty).toBe('sopimus');
  });

  describe('sopimuksen alkuarvo:', function() {
    var sopimus;
    beforeEach(function() {
      asetaHakuehdoksi('TK');
      sopimus = sopimuksenLuonninAsetukset.haeTiedot();
    });

    it('pitäisi hakea toimikunta hakuehdolla', function() {
      expect(toimikuntaResource.get).toHaveBeenCalledWith({diaarinumero: 'TK'});
    });

    describe('toimikunta on haettu:', function() {
      var toimikunta = {
        tkunta: 'tkunta',
        nimi_fi: 'nimi',
        nimi_sv: 'nimi (sv)'
      };
      beforeEach(function() {
        palautaToimikunta(toimikunta);
      });

      it('pitäisi asettaa sopimuksen toimikunta', function() {
        expect(sopimus.toimikunta).toEqual(toimikunta);
      });
    });
  });

  describe('sopimuksen alkuarvon haun valmistuminen:', function() {
    var toimikunta = {
      tkunta: 'tkunta',
      nimi_fi: 'nimi',
      nimi_sv: 'nimi (sv)'
    };

    it('pitäisi muokata aluksi palautettua sopimus-objektia', function() {
      asetaHakuehdoksi('TK');
      var sopimus = sopimuksenLuonninAsetukset.haeTiedot();
      palautaToimikunta(toimikunta);
      expect(sopimus.toimikunta).toEqual(toimikunta);
    });
  });
});
