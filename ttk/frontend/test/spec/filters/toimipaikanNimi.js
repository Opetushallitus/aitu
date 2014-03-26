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

'use strict'

describe('Filters: toimipaikanNimi', function(){

  var toimipaikannimiFilter;

  var toimipaikat = [{nimi : 'Toimipaikan nimi', toimipaikkakoodi: 'a12345'}];

  beforeEach(module('filters'));

  beforeEach(inject(function($filter){
    toimipaikannimiFilter = $filter('toimipaikanNimi');
  }));

  it('Muuttaa toimipaikkakoodin toimipaikan nimeksi jos toimipaikkakoodi löytyy toimipaikat -taulukosta', function() {
    var toimipaikkakoodi = _.first(toimipaikat).toimipaikkakoodi;
    expect(toimipaikannimiFilter(toimipaikkakoodi, toimipaikat)).toEqual(_.first(toimipaikat).nimi);
  })

  it('Palauttaa toimipaikkakoodin sellaisenaan, jos sille ei löydy vastinetta toimipaikat -taulukosta', function() {
    var toimipaikkakoodi = 'b12345';
    expect(toimipaikannimiFilter(toimipaikkakoodi, toimipaikat)).toEqual(toimipaikkakoodi);
  })

});