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

describe('Controllers: Directives: jarjestettavaTaulukkoController:', function() {

  var controller;
  var scope;

  var rivi1 = {sarake1 : 'b', sarake2 : 'a'};
  var rivi2 = {sarake1 : 'a', sarake2 : 'b'};

  var asetaJarjestelySarake1;
  var asetaJarjestelySarake2;

  beforeEach(function(){
    module('directives');
    module(function($provide){
      $provide.value('$element', {});
      $provide.value('$attrs', {
        jarjestettavaTaulukko : 'testiData',
        jarjestettyData : 'testiDataJarjestetty'
      });
    });
  });

  beforeEach(inject(function($controller, $rootScope) {
    scope = $rootScope.$new(true);
    controller = $controller('jarjestettavaTaulukkoController', {$scope: scope});

    asetaJarjestelySarake1 = jasmine.createSpy("asetaJarjestelySarake1");
    asetaJarjestelySarake2 = jasmine.createSpy("asetaJarjestelySarake2");

    controller.lisaaJarjestettavaSarake('sarake1', sarakeScope(asetaJarjestelySarake1), true);
    controller.lisaaJarjestettavaSarake('sarake2', sarakeScope(asetaJarjestelySarake2));

    scope.testiData = [rivi1, rivi2];

    scope.$digest();

    function sarakeScope(spy) {
      return {asetaJarjestely : spy}
    }
  }));

  it('Pitäisi järjestää taulukko alussa default sarakkeen mukaan', function(){
    expect(scope.testiDataJarjestetty).toEqual([rivi2, rivi1]);
    expect(asetaJarjestelySarake1).toHaveBeenCalledWith(true, false);
    expect(asetaJarjestelySarake2).toHaveBeenCalledWith(false);
  });

  it('Taulukon pitäisi järjestellä klikatun sarakkeen mukaan. Seuraavat klikkaukset samaan sarakkeeseen kääntävät järjestyksen.', function(){
    controller.sarakettaKlikattu('sarake2');

    scope.$digest();

    expect(scope.testiDataJarjestetty).toEqual([rivi1, rivi2]);
    expect(asetaJarjestelySarake1).toHaveBeenCalledWith(false);
    expect(asetaJarjestelySarake2).toHaveBeenCalledWith(true, false);

    controller.sarakettaKlikattu('sarake2');

    scope.$digest();

    expect(scope.testiDataJarjestetty).toEqual([rivi2, rivi1]);
    expect(asetaJarjestelySarake1).toHaveBeenCalledWith(false);
    expect(asetaJarjestelySarake2).toHaveBeenCalledWith(true, true);

  });
})