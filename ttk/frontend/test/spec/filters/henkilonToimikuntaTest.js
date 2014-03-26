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

describe('Filter henkilonToimikunta:', function () {

  var testiData = [
    {nimi : 'Aku Ankka', jasenyydet : [{nimi_fi : 'Suomalainen1', nimi_sv : 'Svenska1'}, {nimi_fi : 'Suomalainen2', nimi_sv : 'Svenska2'}]},
    {nimi : 'Roope Ankka', jasenyydet : [{nimi_fi : 'Suomalainen3', nimi_sv : 'Svenska3'}, {nimi_fi : 'Suomalainen4', nimi_sv : 'Svenska4'}]}
  ];

  beforeEach(module('filters'));

  it('Pitäisi suodattaa oikein suomenkielisellä hakutermillä', inject(function($filter){
    var tulos = $filter('henkilonToimikunta')(testiData, 'Suomalainen1');
    expect(tulos).toEqual([testiData[0]]);
    tulos = $filter('henkilonToimikunta')(testiData, 'Suomalainen4');
    expect(tulos).toEqual([testiData[1]]);
    tulos = $filter('henkilonToimikunta')(testiData, 'Suomalainen');
    expect(tulos).toEqual(testiData);
  }));

  it('Pitäisi suodattaa oikein ruotsinkielisellä hakutermillä', inject(function($filter){
    var tulos = $filter('henkilonToimikunta')(testiData, 'Svenska1');
    expect(tulos).toEqual([testiData[0]]);
    tulos = $filter('henkilonToimikunta')(testiData, 'Svenska3');
    expect(tulos).toEqual([testiData[1]]);
    tulos = $filter('henkilonToimikunta')(testiData, 'Svenska');
    expect(tulos).toEqual(testiData);
  }));

});