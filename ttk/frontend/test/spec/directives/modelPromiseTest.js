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

describe('Directives: modelPromise:', function() {

  beforeEach(module('directives'));

  var modelPromise;
  var $rootScope;
  beforeEach(inject(function(_modelPromise_, _$rootScope_){
    modelPromise = _modelPromise_;
    $rootScope = _$rootScope_;
  }))

  it('Angular resourcella kutsuttaessa palauttaa sen oman promisen', function() {
    var model = {$promise: "promise"};
    expect(modelPromise(model)).toEqual("promise");
  });

  it('Tavallisella oliolla kutsuttuna palauttaa promisen, joka palauttaa ko. olion', function() {
    var model = {foo: "bar"};
    var promiseModel;
    modelPromise(model).then(function(m){
      promiseModel = m;
    })
    $rootScope.$apply();
    expect(promiseModel).toEqual(model);
  });

  it('Nullilla/undefinedillä kutsuttuna palauttaa promisen, joka palauttaa ko. arvon', function() {
    var model = null;
    var promiseModel;
    modelPromise(model).then(function(m){
      promiseModel = m;
    })
    $rootScope.$apply();
    expect(promiseModel).toEqual(model);
  });

});
