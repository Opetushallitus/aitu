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

describe('Filter voimassaoloAika:', function () {

  var alkuPvm = '2009-02-01';
  var loppuPvm = '2012-06-01'

  beforeEach(module('filters'));
  beforeEach(module('mock.i18n'));

  it('pitäisi palauttaa "alkupvm - loppupvm" jos molemmat pvm:t annettu', inject(function($filter){

    var tulos = $filter('voimassaoloAika')(alkuPvm, loppuPvm);

    expect(tulos).toEqual('01.02.2009 - 01.06.2012')

  }));

  it('pitäisi palauttaa "alkupvm alkaen" jos alkupäivämäärä annettu, mutta loppupvm ei', inject(function($filter){

    var tulos = $filter('voimassaoloAika')(alkuPvm, null);

    expect(tulos).toEqual('01.02.2009 alkaen')

  }));

  it('pitäisi palauttaa "loppupvm asti" jos loppupäivämäärä annettu, mutta alkupvm ei', inject(function($filter){

    var tulos = $filter('voimassaoloAika')(null, loppuPvm);

    expect(tulos).toEqual('01.06.2012 asti')

  }));

  it('pitäisi palauttaa "-" jos päimämääriä ei annettu', inject(function($filter){

    var tulos = $filter('voimassaoloAika')(null, null);

    expect(tulos).toEqual('-')

  }));
});