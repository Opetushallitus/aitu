'use strict';

describe('Directives: enumValikko:', function() {

  var $rootScope, $compile;

  function luoElementti() {
    var html = '<enum-valikko pakollinen="pakollinen" nimi="test-enum" arvo="testiEnumValinta"></enum-valikko>';
    var elementti =  $compile(html)($rootScope);
    $rootScope.$digest();
    return elementti;
  }

  function optionTeksti(options, indeksi) {
    return $(options[indeksi - 1]).text();
  }

  beforeEach(module(
    'directives',
    'mock.EnumResource',
    'mock.i18n'
  ))

  beforeEach(inject(function(_$compile_, _$rootScope_) {
    $rootScope = _$rootScope_;
    $compile = _$compile_;
  }));

  it('Ei pakollinen enum valikko näyttää tyhjän valinnan tekstin muodossa "Ei valintaa"', function() {
    $rootScope.pakollinen = false;
    var elementti = luoElementti();

    var options = elementti.find('option');

    expect(optionTeksti(options, 1)).toEqual('ei valintaa');
    expect(optionTeksti(options, 2)).toEqual('valinta1');
    expect(optionTeksti(options, 3)).toEqual('valinta2');
    expect(optionTeksti(options, 4)).toEqual('valinta3');

  });

  it('Pakollinen enum valikko näyttää tyhjän valinnan tekstin muodossa "Valitse"', function() {
    $rootScope.pakollinen = true;
    var elementti = luoElementti();

    var options = elementti.find('option');

    expect(optionTeksti(options, 1)).toEqual('valitse');
    expect(optionTeksti(options, 2)).toEqual('valinta1');
    expect(optionTeksti(options, 3)).toEqual('valinta2');
    expect(optionTeksti(options, 4)).toEqual('valinta3');

  });

  it('Näyttää default valinnan oikein', function() {
    $rootScope.pakollinen = true;
    $rootScope.testiEnumValinta = 'valinta2';
    var elementti = luoElementti();

    expect(elementti.find('option:selected').text()).toEqual('valinta2');
  });

  it('Ei näytä tyhjää valintaa, jos pakollisesta enum valikosta on item valittuna.', function() {
    $rootScope.pakollinen = true;
    $rootScope.testiEnumValinta = 'valinta2';
    var elementti = luoElementti();

    var options = elementti.find('option:not(".ng-hide")');

    expect(optionTeksti(options, 1)).toEqual('valinta1');
    expect(optionTeksti(options, 2)).toEqual('valinta2');
    expect(optionTeksti(options, 3)).toEqual('valinta3');

  });


  it('Asettaa skooppimuuttujan arvoon null kun valinta vaihdetaan tyhjään arvoon ei-pakollisessa enum-valikossa', function() {
    $rootScope.pakollinen = false;
    $rootScope.testiEnumValinta = 'valinta2';
    var elementti = luoElementti();

    expect(elementti.find('option:selected').text()).toEqual('valinta2');

    elementti.find('select').val('ei valintaa');
    elementti.find('select').trigger('change');

    $rootScope.$digest();

    expect($rootScope.testiEnumValinta).toEqual(null);

  });
});

