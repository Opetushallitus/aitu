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

describe('Filters: orderByDate', function() {

  var orderByDate;

  var entity1 = {pvm : '21.03.2013'};
  var entity2 = {pvm : '20.03.2013'};

  beforeEach(function() {
    module('filters');
    module('services');
  });

  beforeEach(inject(function($filter){
    orderByDate = $filter('orderByDate');
  }))

  it('Järjestää päivämäärät oikein', function(){
    expect(orderByDate([entity1, entity2], 'pvm')).toEqual([entity2, entity1]);
  });

  it('Järjestää päivämäärät oikein käänteiseen järjestykseen', function(){
    expect(orderByDate([entity1, entity2], 'pvm', true)).toEqual([entity1, entity2]);
  })
});