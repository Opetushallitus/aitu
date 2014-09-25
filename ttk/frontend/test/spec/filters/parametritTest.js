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
    {arvo : 'arvo1'},
    {arvo : 'arvo2'}
  ];

  beforeEach(module('filters'));

  it('Pitäisi palauttaa tyhjä merkkijono tyhjällä listalla', inject(function($filter){
    var tulos = $filter('parametrit')([], 'arvo', 'kentta');
    expect(tulos).toEqual("");
  }));

  it('Pitäisi palauttaa validit query-parametrit', inject(function($filter){
    var tulos = $filter('parametrit')(testiData, 'arvo', 'kentta');
    expect(tulos).toEqual("kentta=arvo1&kentta=arvo2");
  }));
});