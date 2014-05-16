'use strict';

describe('Directives: enumValikko:', function() {

  var $rootScope, $compile;

  function luoElementti() {
    var html = '<boolean-select pakollinen="pakollinen" model="testiBooleanValinta"></boolean-select>';
    var elementti =  $compile(html)($rootScope);
    $rootScope.$digest();
    return elementti;
  }

  function optionTeksti(options, indeksi) {
    return options.filter(':nth-child(' + indeksi + ')').text();
  }

  beforeEach(module(
    'directives',
    'services',
    'mock.i18n'
  ))

  beforeEach(inject(function(_$compile_, _$rootScope_) {
    $rootScope = _$rootScope_;
    $compile = _$compile_;
  }));

  it('Ei pakollinen boolean valikko näyttää tyhjän valinnan tekstin muodossa "Ei valintaa"', function() {
    $rootScope.pakollinen = false;
    var elementti = luoElementti();

    var options = elementti.find('option');

    expect(optionTeksti(options, 1)).toEqual('ei valintaa');
    expect(optionTeksti(options, 2)).toEqual('kyllä');
    expect(optionTeksti(options, 3)).toEqual('ei');

  });

  it('Pakollinen boolean valikko näyttää tyhjän valinnan tekstin muodossa "Valitse"', function() {
    $rootScope.pakollinen = true;
    var elementti = luoElementti();

    var options = elementti.find('option');

    expect(optionTeksti(options, 1)).toEqual('valitse');
    expect(optionTeksti(options, 2)).toEqual('kyllä');
    expect(optionTeksti(options, 3)).toEqual('ei');
  });

  it('Näyttää default valinnan oikein', function() {
    $rootScope.pakollinen = true;
    $rootScope.testiBooleanValinta = true;
    var elementti = luoElementti();

    expect(elementti.find('option[selected="selected"]').text()).toEqual('kyllä');
  });

  it('Ei näytä tyhjää valintaa, jos pakollisesta boolean valikosta on item valittuna.', function() {
    $rootScope.pakollinen = true;
    $rootScope.testiBooleanValinta = true;
    var elementti = luoElementti();

    var options = elementti.find('option');

    expect(optionTeksti(options, 1)).toEqual('kyllä');
    expect(optionTeksti(options, 2)).toEqual('ei');

  });

  it('Asettaa skooppimuuttujan arvoon null kun valinta vaihdetaan tyhjään arvoon ei-pakollisessa boolean-valikossa', function() {
    $rootScope.pakollinen = false;
    $rootScope.testiBooleanValinta = true;
    var elementti = luoElementti();

    expect(elementti.find('option[selected="selected"]').text()).toEqual('kyllä');

    elementti.find('select').val('ei valintaa');
    elementti.find('select').trigger('change');

    $rootScope.$digest();

    expect($rootScope.testiEnumValinta).toEqual(null);

  });
});
