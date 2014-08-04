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

describe('Services: virheLogitusTest', function () {

  var rootScope;
  var lahetaPalvelimelleSpy;

  beforeEach(function() {

    lahetaPalvelimelleSpy = jasmine.createSpy();

    module('yhteiset.palvelut.virheLogitus');
    module(function($provide){
      $provide.provider('$exceptionHandler', {
        $get: function( virheLogitus ) {
          return( virheLogitus );
        }
      });
      $provide.factory('virheLogitusApi', function(){
        return {
          lahetaPalvelimelle : lahetaPalvelimelleSpy
        }
      });
    });
  });

  beforeEach(inject(function($rootScope){
    rootScope = $rootScope;
  }));

  it('Pitäisi lähettää logituspyyntö palvelimelle virheen sattuessa', function() {
    var virheViesti = 'Tapahtui virhe!';
    rootScope.$apply(function(){throw new Error(virheViesti)});
    expect(lahetaPalvelimelleSpy.mostRecentCall.args[0].message).toEqual(virheViesti);
  });

});