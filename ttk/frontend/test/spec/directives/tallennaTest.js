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

describe('Directives: tallenna', function() {
  var interceptor;
  var elementti;
  var rootScope;

  var pyynto = {
    id: 'sopimuksen-luonti',
    method: "GET",
    url: '/api/henkilo' };

  var vastaus = {
    config : {
      id: 'sopimuksen-luonti',
      method: "GET",
      url: '/api/henkilo' },
    status : 200 };

  beforeEach(function(){
    module('directives');
    module('mock.i18n');
  });

  beforeEach(inject(function($compile, $rootScope, apiCallInterceptor){
    rootScope = $rootScope;
    interceptor = apiCallInterceptor;
    elementti = $compile('<tallenna disabloi-pyyntojen-ajaksi="[\'sopimuksen-luonti\']" teksti="\'Tallenna\'"></tallenna>')($rootScope);
    rootScope.$digest();
  }));

  it('Näyttää teksti attribuutin arvon button tagien sisällä', function() {
    expect(elementti.html()).toEqual('Tallenna');
  });

  it('Disabloi napin kun pyyntö käynnisssä ja enabloi kun vastaus saapuu', function() {
    interceptor.apiPyynto(pyynto);
    rootScope.$digest();
    expect(elementti.prop('disabled')).toEqual(true);
    interceptor.apiVastaus(vastaus);
    rootScope.$digest();
    expect(elementti.prop('disabled')).toEqual(false);
  });
});