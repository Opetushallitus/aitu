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

describe('Filter: filters: voimassaOlevat:', function () {

  // load the filter's module
  beforeEach(module('filters'));

  // initialize a new instance of the filter before each test
  var voimassaOlevat;
  beforeEach(inject(function($filter) {
    voimassaOlevat = $filter('voimassaOlevat');
  }));

  describe('ei-tyhjä lista:', function() {
    var lista,
      alkio1 = {voimassa: true},
      alkio2 = {voimassa: false};
    beforeEach(function() {
      lista = [alkio1, alkio2];
    });

    it('pitäisi palauttaa voimassaolevat', function () {
      expect(voimassaOlevat(lista, true)).toEqual([alkio1]);
    });

    it('pitäisi palauttaa ei-voimassaolevat', function () {
      expect(voimassaOlevat(lista, false)).toEqual([alkio2]);
    });
  });

  describe('tyhjä lista:', function() {
    var lista;
    beforeEach(function() {
      lista = [];
    });

    it('pitäisi palauttaa tyhjä lista', function () {
      expect(voimassaOlevat(lista)).toEqual([]);
    });
  });
});
