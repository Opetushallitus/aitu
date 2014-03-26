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

describe('Filter: filters: kokonimi:', function () {

  // load the filter's module
  beforeEach(module('filters'));

  // initialize a new instance of the filter before each test
  var kokonimi;
  beforeEach(inject(function($filter) {
    kokonimi = $filter('kokonimi');
  }));

  describe('ei-tyhjä nimilista:', function() {
    var henkilo1, henkilo2, nimilista;
    beforeEach(function() {
      henkilo1 = {
        etunimi: 'Etu1',
        sukunimi: 'Suku1'
      };
      henkilo2 = {
        etunimi: 'Etu2',
        sukunimi: 'Suku2'
      };
      nimilista = [henkilo1, henkilo2];
    });

    it('pitäisi palauttaa tyhjä lista jos hakusanaa ei löydy', function () {
      expect(kokonimi(nimilista, 'sana')).toEqual([]);
    });

    describe('löytyy etunimestä:', function() {
      it('pitäisi löytää ensimmäinen henkilö', function () {
        expect(kokonimi(nimilista, 'Etu1')).toEqual([henkilo1]);
      });

      it('pitäisi löytää toinen henkilö', function () {
        expect(kokonimi(nimilista, 'Etu2')).toEqual([henkilo2]);
      });

      it('pitäisi löytää molemmat', function () {
        expect(kokonimi(nimilista, 'Etu')).toEqual([henkilo1, henkilo2]);
      });

      it('löytää etunimen osa', function () {
        expect(kokonimi(nimilista, 'tu1')).toEqual([henkilo1]);
      });

      it('löytää huolimatta isoista ja pienistä kirjaimista', function () {
        expect(kokonimi(nimilista, 'etu1')).toEqual([henkilo1]);
      });
    });

    describe('löytyy sukunimestä:', function() {
      it('pitäisi löytää ensimmäinen henkilö', function () {
        expect(kokonimi(nimilista, 'Suku1')).toEqual([henkilo1]);
      });

      it('pitäisi löytää toinen henkilö', function () {
        expect(kokonimi(nimilista, 'Suku2')).toEqual([henkilo2]);
      });

      it('pitäisi löytää molemmat', function () {
        expect(kokonimi(nimilista, 'Suku')).toEqual([henkilo1, henkilo2]);
      });

      it('löytää sukunimen osa', function () {
        expect(kokonimi(nimilista, 'uku1')).toEqual([henkilo1]);
      });

      it('löytää huolimatta isoista ja pienistä kirjaimista', function () {
        expect(kokonimi(nimilista, 'suku1')).toEqual([henkilo1]);
      });
    });

    describe('löytyy molemmista:', function() {
      it('pitäisi löytää ensimmäinen henkilö', function () {
        expect(kokonimi(nimilista, 'u1')).toEqual([henkilo1]);
      });
    });

    describe('löytyy puoliksi molemmista:', function() {
      it('pitäisi palauttaa tyhjä lista', function () {
        expect(kokonimi(nimilista, 'u1Su')).toEqual([]);
      });
    });
  });

  describe('tyhjä nimilista:', function() {
    var nimilista;
    beforeEach(function() {
      nimilista = [];
    });

    it('pitäisi palauttaa tyhjä lista', function () {
      expect(kokonimi(nimilista, 'sana')).toEqual([]);
    });
  });

  describe('ei hakusanaa:', function() {
    var nimilista;
    beforeEach(function() {
      nimilista = [{
        etunimi: 'E1',
        sukunimi: 'S1'
      }];
    });

    it('pitäisi palauttaa koko nimilista', function () {
      expect(kokonimi(nimilista)).toEqual(nimilista);
    });
  });
});
