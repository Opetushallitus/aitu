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

describe('Filter suomiJaRuotsi:', function () {

  var testiData = [
    {nimi_fi : 'Suomalainen1', nimi_sv : 'Svenska1'},
    {nimi_fi : 'Suomalainen2', nimi_sv : 'Svenska2'}
  ];

  beforeEach(module('filters'));

  it('Pitäisi suodattaa oikein suomenkielisellä hakutermillä', inject(function($filter){
    var tulos = $filter('suomiJaRuotsi')(testiData, 'nimi', 'Suomalainen1');
    expect(tulos).toEqual([testiData[0]]);
    tulos = $filter('suomiJaRuotsi')(testiData, 'nimi', 'Suomalainen2');
    expect(tulos).toEqual([testiData[1]]);
    tulos = $filter('suomiJaRuotsi')(testiData, 'nimi', 'Suomalainen');
    expect(tulos).toEqual(testiData);
  }));

  it('Pitäisi suodattaa oikein ruotsinkielisellä hakutermillä', inject(function($filter){
    var tulos = $filter('suomiJaRuotsi')(testiData, 'nimi', 'Svenska1');
    expect(tulos).toEqual([testiData[0]]);
    tulos = $filter('suomiJaRuotsi')(testiData, 'nimi', 'Svenska2');
    expect(tulos).toEqual([testiData[1]]);
    tulos = $filter('suomiJaRuotsi')(testiData, 'nimi', 'Svenska');
    expect(tulos).toEqual(testiData);
  }));
});