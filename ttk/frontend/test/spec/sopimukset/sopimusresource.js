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

describe('Service: sopimukset: sopimusResource:', function () {

  // lataa palvelun moduuli
  beforeEach(module('sopimukset'));

  // aseta Angularin http-pyyntöjen kaappaus testeissä
  var kiinniotettuPyynto;
  beforeEach(module(function($httpProvider) {
    kiinniotettuPyynto = null;
    $httpProvider.interceptors.push(function() {
      return {
       'request': function(config) {
          kiinniotettuPyynto = config;
          return config;
        }
      };
    });
  }));

  var $httpBackend, $rootScope;
  var captureDependencies = ['$httpBackend', '$rootScope', function(service1, service2) {
    $httpBackend = service1;
    $rootScope = service2;
  }];

  beforeEach(function initDependencies() {
    inject(captureDependencies);
  });

  // alusta palvelu
  var sopimusResource;
  beforeEach(inject(['sopimusResource', function(service) {
    sopimusResource = service;
  }]));

  afterEach(function() {
    $httpBackend.verifyNoOutstandingExpectation();
    $httpBackend.verifyNoOutstandingRequest();
  });

  function suoritaPyynto() {
    $rootScope.$apply();
    $httpBackend.flush();
  }

  var OK = 200;

  describe('save:', function() {

    it('pitäisi tehdä pyyntö oikeaan osoitteeseen', function () {
      $httpBackend.expect('POST', /\/api\/jarjestamissopimus/).respond(OK);
      sopimusResource.save({});
      suoritaPyynto();
    });

    it('pitäisi lähettää sopimuksen sisältö', function () {
      $httpBackend.expect('POST', undefined, /"avain":"arvo"/).respond(OK);
      sopimusResource.save({avain: 'arvo'});
      suoritaPyynto();
    });

    it('pitäisi asettaa sopijatoimikunta', function () {
      $httpBackend.expect('POST', undefined, /"sopijatoimikunta":"TK"/).respond(OK);
      sopimusResource.save({toimikunta: 'TK'});
      suoritaPyynto();
    });

    it('pitäisi asettaa pyyntöön tunniste', function () {
      $httpBackend.expect('POST').respond(OK);
      sopimusResource.save({});
      suoritaPyynto();
      expect(kiinniotettuPyynto.id).toBeDefined();
    });

    it('pitäisi asettaa pyyntöön lokalisaatio', function () {
      $httpBackend.expect('POST').respond(OK);
      sopimusResource.save({});
      suoritaPyynto();
      expect(kiinniotettuPyynto.i18n).toBeDefined();
    });
  });
});
