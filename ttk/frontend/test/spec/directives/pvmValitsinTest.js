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

describe('Directives: pvmValitsin', function() {
  var elementti;
  var input;
  var $rootScope;
  var $compile;

  function luoElementti() {
    $rootScope.valittuPvm = '';
    elementti = $compile('<pvm-valitsin valittu-pvm="valittuPvm" otsikko="pvm"></pvm-valitsin>')($rootScope);
    $rootScope.$digest();
    input = elementti.find('input');
  }

  function syotaElementtiinArvo(valittuPvm) {
    input.val(valittuPvm);
    input.trigger('change');
    $rootScope.$digest();
  }

  function inputValidi() {
    return input.hasClass('ng-valid');
  }

  function inputEiValidi() {
    return input.hasClass('ng-invalid-date');
  }

  beforeEach(module('directives', 'ui.bootstrap', 'template/pvm-valitsin'));

  beforeEach(inject(function(_$compile_, _$rootScope_){
    $rootScope = _$rootScope_;
    $compile = _$compile_;
  }));

  it('Käsittelee syötetyn validin päivämäärän oikein.', function() {
    var pvm = "24.03.1980";

    luoElementti();

    syotaElementtiinArvo(pvm);
    expect($rootScope.valittuPvm).toEqual(pvm);
    expect(inputValidi()).toEqual(true);
  });

  it('Ei hyväksy syötettyä epävalidia päivämärää', function() {
    var pvm = "32.03.1980";

    luoElementti();

    syotaElementtiinArvo(pvm);
    expect($rootScope.valittuPvm).toEqual(null);
    expect(inputEiValidi()).toEqual(true);
  });

  it('Muuttaa inputin epävalidiksi jos päivämäärä muutetaan epävalidiksi.', function() {
    var validiPvm = "24.03.1980";
    var eiValidiPvm =  "32.03.1980";

    luoElementti();

    syotaElementtiinArvo(validiPvm);
    expect($rootScope.valittuPvm).toEqual(validiPvm);
    expect(inputValidi()).toEqual(true);

    syotaElementtiinArvo(eiValidiPvm);
    expect($rootScope.valittuPvm).toEqual(null);
    expect(inputEiValidi()).toEqual(true);
  });

  it('Muuttaa inputin validiksi jos ei-validi päivämäärä muutetaan validiksi.', function() {
    var validiPvm = "24.03.1980";
    var eiValidiPvm =  "32.03.1980";

    luoElementti();

    syotaElementtiinArvo(eiValidiPvm);
    expect($rootScope.valittuPvm).toEqual(null);
    expect(inputEiValidi()).toEqual(true);

    syotaElementtiinArvo(validiPvm);
    expect($rootScope.valittuPvm).toEqual(validiPvm);
    expect(inputValidi()).toEqual(true);
  });
});