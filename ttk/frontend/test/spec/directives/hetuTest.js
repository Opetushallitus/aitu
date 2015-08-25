// Copyright (c) 2015 The Finnish National Board of Education - Opetushallitus
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

describe('Directives: hetu', function() {
  var $compile;
  var $rootScope;

  beforeEach(module('direktiivit.hetu'));

  beforeEach(inject(function(_$compile_, _$rootScope_) {
    $compile = _$compile_;
    $rootScope = _$rootScope_;

    var element = $compile('<form name="form"><input ng-model="hetu" hetu></form>')($rootScope);
    $rootScope.$digest();
  }));

  var test = function(hetu, valid) {
    $rootScope.hetu = hetu;
    $rootScope.$digest();
    expect($rootScope.form.$valid).toBe(valid, hetu);
  };

  it('Works', function() {
    expect($rootScope.form).not.toBe(undefined);

    test('250895-8672', true);
    test('250805A4468', true);

    test('x', false);
    test('123456-1234', false);

    // Päivämäärävalidointia ei ole
    //test('991299-1232', false);
  });
});