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

describe('Filters: merkitseValinnatOsiin', function(){

  var merkitseValinnatOsiinFilter;
  var kaikkiOsat;

  beforeEach(module('filters'));

  beforeEach(inject(function($filter) {
    merkitseValinnatOsiinFilter = $filter('merkitseValinnatOsiin');
    kaikkiOsat = [{nimi:'osa1'}, {nimi:'osa2'}];
  }))

  it('Ei merkitse osaa valitutksi kaikkiOsat -taulukosta, jos osaa ei löydy valitutOsat -taulukosta', function(){
    var valitutOsat = [];
    var suodatettu = merkitseValinnatOsiinFilter(kaikkiOsat, valitutOsat, 'nimi');

    expect(_.filter(suodatettu, {valittu : true})).toEqual([]);
  });

  it('Merkitsee osan valituksi kaikkiOsat -taulukosta, jos osa löytyy valitutOsat -taulukosta ja kopio toimipaikka propertyn', function(){
    var valitutOsat = [{nimi:'osa1', toimipaikka : 'toimipaikka1'}];
    var suodatettu = merkitseValinnatOsiinFilter(kaikkiOsat, valitutOsat, 'nimi');
    var merkitty = _.filter(suodatettu, {valittu : true});

    expect(merkitty.length).toEqual(1);
    expect(_.first(merkitty).nimi).toEqual('osa1');
    expect(_.first(merkitty).toimipaikka).toEqual('toimipaikka1');
  });

  it('Poistaa valinnat jos osaa ei löydy valitytOsat -taulukosta ja nollaa toimipaikka tiedon', function(){

    var kaikkiOsat = [{nimi:'osa1', valittu : true, toimipaikka : 'toimipaikka1'}, {nimi:'osa2'}]

    var valitutOsat = [];
    var suodatettu = merkitseValinnatOsiinFilter(kaikkiOsat, valitutOsat, 'nimi');
    var suodatettuValittuPropertylla = _.filter(suodatettu, {valittu : true});
    var suodatettuToimipaikalla = _.filter(suodatettu, function(osa){return osa.toimipaikka;});

    expect(suodatettuValittuPropertylla.length).toEqual(0);

  });
})