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

describe('Filter tutkintorakenneHakuFilter:', function () {

  var tutkintorakenne = [{
    opintoala : [
      {nayttotutkinto : [{nimi_fi: '1', nimi_sv: '1 (sv)'}, {nimi_fi: '2', nimi_sv: '2 (sv)'}]},
      {nayttotutkinto : [{nimi_fi: '3', nimi_sv: '3 (sv)'}, {nimi_fi: '4', nimi_sv: '4 (sv)'}]}
    ]
  }];

  beforeEach(module('filters'));


  function odotettuTulos(nimi) {
    return [{opintoala: [{nayttotutkinto: [{nimi_fi: nimi, nimi_sv: nimi + ' (sv)'}]}]}];
  }

  it("palauttaa alkuperäisen rakenteen tyhjällä hakuehdolla", inject(function($filter){
    var tulos = $filter('tutkintorakenneHakuFilter')(tutkintorakenne, '');
    expect(tulos).toEqual(tutkintorakenne);
  }));

  it("palauttaa alkuperäisen rakenteen undefined hakuehdolla", inject(function($filter){
    var tulos = $filter('tutkintorakenneHakuFilter')(tutkintorakenne, undefined);
    expect(tulos).toEqual(tutkintorakenne);
  }));

  it("Löytää oikeat tutkinnot suomenkielisellä hakuehdolla", inject(function($filter){
    var tulos =  $filter('tutkintorakenneHakuFilter')(tutkintorakenne, '1');
    expect(tulos).toEqual(odotettuTulos('1'));

    tulos =  $filter('tutkintorakenneHakuFilter')(tutkintorakenne, '2');
    expect(tulos).toEqual(odotettuTulos('2'));
  }));

  it("Löytää oikeat tutkinnot ruotsinkielisellä hakuehdolla", inject(function($filter){
    var tulos =  $filter('tutkintorakenneHakuFilter')(tutkintorakenne, '1 (sv)');
    expect(tulos).toEqual(odotettuTulos('1'));

    tulos =  $filter('tutkintorakenneHakuFilter')(tutkintorakenne, '2 (sv)');
    expect(tulos).toEqual(odotettuTulos('2'));
  }));
});