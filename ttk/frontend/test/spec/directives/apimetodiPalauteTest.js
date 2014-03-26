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

describe('Directives: apimetodiPalaute', function(){

  var rootScope;
  var luoController;
  var interceptor;

  function luoPyynto(metodi) {
    return {
      id: 'henkilometodi',
      method: metodi,
      i18n : 'henkilo',
      url : '/api/henkilo'
    }
  }

  function luoVastaus(metodi, status, virheet) {
    return {
      config : {
        id: 'henkilometodi' ,
        method: metodi,
        i18n : 'henkilo',
        url : '/api/henkilo'
      },
      status : status,
      data : {errors: virheet}
    }
  }

  beforeEach(module(
    'apimetodiPalaute',
    'mock.i18n',
    'services',
    'mock.services.edellinenLokaatio'
  ));

  beforeEach(inject(function($controller, $rootScope, apiCallInterceptor) {
    rootScope = $rootScope;
    interceptor = apiCallInterceptor;

    luoController = function(scope){
      var controllerScope = scope ? scope : $rootScope.$new(true);
      controllerScope.palautteet = [];
      $controller('apimetodiPalauteController', {$scope: controllerScope});
      return controllerScope;
    };
  }));

  it('pitäisi näyttää POST metodin ok vastaus', function(){

    var scope = luoController();

    interceptor.apiPyynto(luoPyynto('POST'));
    interceptor.apiVastaus(luoVastaus('POST', 200));

    rootScope.$digest();

    expect(scope.palautteet[0].viesti).toEqual('Uuden luonti onnistui')

  });
  it('pitäisi näyttää POST metodin virhevastaus', function(){
    var scope = luoController();

    interceptor.apiPyynto(luoPyynto('POST'));
    interceptor.apiVastaus(luoVastaus('POST', 404, {'etunimi' : ['Pakollinen kenttä']}), true);

    rootScope.$digest();
    expect(scope.palautteet[0].viesti).toEqual('Uuden luonti ei onnistunut');
    expect(scope.palautteet[0].virheet[0]['ominaisuus']).toEqual('Etunimi (lokalisoitu)');
    expect(scope.palautteet[0].virheet[0]['viesti']).toEqual('Pakollinen kenttä');
  });
  it('pitäisi näyttää PUT metodin ok vastaus', function(){
    var scope = luoController();

    interceptor.apiPyynto(luoPyynto('PUT'));
    interceptor.apiVastaus(luoVastaus('PUT', 200));

    rootScope.$digest();
    expect(scope.palautteet[0].viesti).toEqual('Muokkaus onnistui');
  });
  it('pitäisi näyttää PUT metodin virhevastaus', function(){
    var scope = luoController();

    interceptor.apiPyynto(luoPyynto('PUT'));
    interceptor.apiVastaus(luoVastaus('PUT', 404, {'etunimi' : ['Pakollinen kenttä']}), true);

    rootScope.$digest();
    expect(scope.palautteet[0].viesti).toEqual('Muokkaus ei onnistunut');
    expect(scope.palautteet[0].virheet[0]['ominaisuus']).toEqual('Etunimi (lokalisoitu)');
    expect(scope.palautteet[0].virheet[0]['viesti']).toEqual('Pakollinen kenttä');
  });

  it('pitäisi näyttää kustomoitu ok ilmoitus', function(){
    var scope = luoController();

    interceptor.apiPyynto(_.merge( luoPyynto('POST'), {i18n : 'henkilo|kustomoitu-operaation-nimi'}));
    interceptor.apiVastaus(_.merge( luoVastaus('POST', 200), {config: {i18n : 'henkilo|kustomoitu-operaation-nimi'}}));

    rootScope.$digest();
    expect(scope.palautteet[0].viesti).toEqual('Kustomoitu ilmoitus onnistumisesta');

  });

  it('pitäisi näyttää kustomoitu virheilmoitus', function(){
    var scope = luoController();

    interceptor.apiPyynto(_.merge( luoPyynto('POST'), {i18n : 'henkilo|kustomoitu-operaation-nimi'}));
    interceptor.apiVastaus(_.merge( luoVastaus('POST', 500), {config: {i18n : 'henkilo|kustomoitu-operaation-nimi'}}));

    rootScope.$digest();
    expect(scope.palautteet[0].viesti).toEqual('Kustomoitu ilmoitus epäonnistumisesta');

  });

  it('ei pitäisi näyttää GET metodin ok vastausta', function(){
    var scope = luoController();

    interceptor.apiPyynto(luoPyynto('GET'));
    interceptor.apiVastaus(luoVastaus('GET', 200));

    rootScope.$digest();
    expect(scope.palautteet.length).toEqual(0);
  });

  it('pitäisi näyttää GET metodin virhevastaus', function(){
    var scope = luoController();

    interceptor.apiPyynto(_.merge( luoPyynto('GET'), {i18n: null}));
    interceptor.apiVastaus(_.merge( luoVastaus('GET', 500), {config : {i18n: null}}), true);

    rootScope.$digest();
    expect(scope.palautteet[0].viesti).toEqual('Virhe metodissa /api/henkilo (500)');
  });

  it('pitäisi näyttää POST ja PUT metodien ok vastaukset redirektoinnin jälkeen ja poistaa ilmoitukset seuraavalla sivun vaihdolla', function(){
    var scope = luoController(rootScope);

    interceptor.apiPyynto(luoPyynto('POST'));
    interceptor.apiVastaus(luoVastaus('POST', 200));

    rootScope.$digest();
    rootScope.$emit('$locationChangeStart');
    expect(scope.palautteet.length).toEqual(1);
    rootScope.$emit('$locationChangeStart');
    rootScope.$digest();
    expect(scope.palautteet.length).toEqual(0);
  });

  it('pitäisi poistaa virhevastaukset sivunvaihdon jälkeen', function(){
    var scope = luoController(rootScope);

    interceptor.apiPyynto(luoPyynto('POST'));
    interceptor.apiVastaus(luoVastaus('POST', 500));
    interceptor.apiPyynto(_.merge(luoPyynto('GET'), {id: 'toinenmetodi'}));
    interceptor.apiVastaus(_.merge(luoVastaus('GET', 404), {id: 'toinenmetodi'}));

    rootScope.$digest();
    expect(scope.palautteet.length).toEqual(2);
    rootScope.$emit('$locationChangeStart');
    rootScope.$digest();
    expect(scope.palautteet.length).toEqual(0);
  });

  it('pitäisi päivittää wizardin sivun vaihtuessa saapuneiden vastausten palautteet', function(){
    var scope = luoController(rootScope);

    interceptor.apiPyynto(luoPyynto('POST'));
    interceptor.apiVastaus(luoVastaus('POST', 200));

    rootScope.$emit('wizardPageChange');

    expect(scope.palautteet[0].redirectFlag).toEqual(false);
  });
});
