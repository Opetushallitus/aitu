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

    var formHtml = '<form name="testForm">';
    formHtml += '<input ng-model="testModel" ng-required="true">';
    formHtml += '<tallenna formi-validi="testForm.$valid" disabloi-pyyntojen-ajaksi="[\'sopimuksen-luonti\']" teksti="\'Tallenna\'"></tallenna></form>';

    rootScope = $rootScope;
    interceptor = apiCallInterceptor;
    elementti = $compile(formHtml)($rootScope);
    rootScope.$digest();
  }));

  it('Näyttää teksti attribuutin arvon button tagien sisällä', function() {
    expect(elementti.text()).toEqual('Tallenna');
  });

  it('Disabloi napin kun pyyntö käynnisssä ja enabloi kun vastaus saapuu', function() {
    var button = elementti.find('button');

    rootScope.testModel = "syöte";
    interceptor.apiPyynto(pyynto);
    rootScope.$digest();

    expect(button.prop('disabled')).toEqual(true);

    interceptor.apiVastaus(vastaus);
    rootScope.$digest();

    expect(button.prop('disabled')).toEqual(false);
  });

  it('Disabloi tallennuksen jos pakollisia kenttiä ei ole täytetty ja enabloi kun pakolliset kentät täytetty.', function() {
    var input = elementti.find('input');
    var button = elementti.find('button');

    expect(button.prop('disabled')).toEqual(true);

    input.val('syöte');
    input.trigger('change');
    rootScope.$digest();

    expect(button.prop('disabled')).toEqual(false);
  });
});