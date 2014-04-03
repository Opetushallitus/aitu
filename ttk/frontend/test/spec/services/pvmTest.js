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

describe('Services: pvm', function () {

  var pvmService;

  beforeEach(module('services'));

  beforeEach(inject(function(pvm){
    pvmService = pvm;
  }));

  it('Muuttaa p.k.vvvv formaattia olevan stringin date:ksi ', function() {
    var tulos = pvmService.parsiPvm('28.1.2014');
    expect(tulos).toEqual(new Date(2014, 0, 28));
  });

  it('Muuttaa pp.kk.vvvv formaattia olevan stringin date:ksi', function() {
    var tulos = pvmService.parsiPvm('27.05.2014');
    expect(tulos).toEqual(new Date(2014, 4, 27));
  });

  it('Palauttaa null jos päivämäärästring ei sisällä kolmea pisteellä erotettua osaa', function() {
    var tulos = pvmService.parsiPvm('03.2013');
    expect(tulos).toEqual(null);
  });

  it('Palauttaa null jos päivämäärää ei ole olemassa', function() {
    var tulos = pvmService.parsiPvm('30.02.2013');
    expect(tulos).toEqual(null);
  });

});