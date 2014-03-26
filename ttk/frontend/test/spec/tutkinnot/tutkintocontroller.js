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

describe('Controller: TutkintoController:', function () {

  // load the controller's module
  beforeEach(function(){
    module('tutkinnot');
    module('filters');
  });

  // Capture dependencies
  var $routeParams, TutkintoResource;
  var captureDependencies = ['$routeParams', 'TutkintoResource', function(service1, service2) {
    $routeParams = service1;
    TutkintoResource = service2;
  }];

  function initDependencies() {
    module('mock.$routeParams', 'mock.TutkintoResource');
    inject(captureDependencies);
  }

  // Initialize the controller and a mock scope
  var TutkintoController,
    scope;
  var initController = ['$controller', '$rootScope', function($controller, $rootScope) {
    scope = $rootScope.$new();
    TutkintoController = $controller('TutkintoController', {
      $scope: scope
    });
  }];

  function injectController() {
    inject(initController);
  }

  describe('luonti:', function () {
    beforeEach(function() {
      initDependencies();
    });

    var tutkinto, tutkintoPromise;
    beforeEach(function() {
      tutkintoPromise = {
        then: jasmine.createSpy('then')
      };
      TutkintoResource.get.andReturn({
        $promise: tutkintoPromise
      });
    });

    function palautaTutkinto(tutkinto) {
      var callback = tutkintoPromise.then.mostRecentCall.args[0];
      callback(tutkinto);
    }

    it('pitäisi hakea tutkinto tutkintotunnuksella', function () {
      $routeParams.tutkintotunnus = '123456';
      injectController();
      expect(TutkintoResource.get).toHaveBeenCalledWith({
        tutkintotunnus: '123456'
      });
    });

    describe('toimikunnat:', function() {

      function testaaToimikuntienAlustusJaOdota(odotetut) {
        beforeEach(function() {
          injectController();
          palautaTutkinto(tutkinto);
        });

        it('pitäisi näyttää tutkinnosta vastaava toimikunta', function () {
          expect(scope.toimikunnat.nykyiset).toEqual(odotetut.nykyiset);
        });

        it('pitäisi näyttää tyhjänä vanhat toimikunnat', function () {
          expect(scope.toimikunnat.vanhat).toEqual(odotetut.vanhat);
        });
      }

      describe('yksi voimassaoleva toimikunta:', function() {
        var toimikunnat = [{
          nimi: 'TTK1',
          voimassa: true
        }];
        beforeEach(function() {
          tutkinto = {
            tutkintotoimikunta: toimikunnat
          };
        });

        testaaToimikuntienAlustusJaOdota({nykyiset: toimikunnat, vanhat: []});
      });

      describe('kaksi voimassaolevaa toimikuntaa:', function() {
        var toimikunnat = [{
          nimi: 'TTK1',
          voimassa: true
        }, {
          nimi: 'TTK2',
          voimassa: true
        }];
        beforeEach(function() {
          tutkinto = {
            tutkintotoimikunta: toimikunnat
          };
        });

        testaaToimikuntienAlustusJaOdota({nykyiset: toimikunnat, vanhat: []});
      });

      describe('kaksi toimikuntaa, vain toinen voimassa:', function() {
        var toimikunta1 = {
          nimi: 'TTK1',
          voimassa: false
        },
        toimikunta2 = {
          nimi: 'TTK2',
          voimassa: true
        },
        toimikunnat = [toimikunta1, toimikunta2];
        beforeEach(function() {
          tutkinto = {
            tutkintotoimikunta: toimikunnat
          };
        });

        testaaToimikuntienAlustusJaOdota({nykyiset: [toimikunta2], vanhat: [toimikunta1]});
      });
    });

    describe('sopimukset:', function() {

      function testaaSopimustenAlustusJaOdota(odotetut) {
        beforeEach(function() {
          injectController();
          palautaTutkinto(tutkinto);
        });

        it('pitäisi näyttää nykyiset sopimukset', function () {
          expect(scope.sopimukset.nykyiset).toEqual(odotetut.nykyiset);
        });

        it('pitäisi näyttää vanhat sopimukset', function () {
          expect(scope.sopimukset.vanhat).toEqual(odotetut.vanhat);
        });
      }

      describe('tutkinto on voimassa:', function() {
        describe('yksi voimassaoleva sopimus:', function() {
          var sopimus1 = {
            sopimusnumero: "1",
            voimassa: true
          },
          sopimus_ja_tutkinto = [{
            jarjestamissopimus: sopimus1
          }];

          beforeEach(function() {
            tutkinto = {
              tutkintotoimikunta: [],
              sopimus_ja_tutkinto: sopimus_ja_tutkinto,
              voimassa: true
            };
          });

          testaaSopimustenAlustusJaOdota({nykyiset: [sopimus1], vanhat: []});
        });

        describe('kaksi voimassaolevaa sopimusta:', function() {
          var sopimus1 = {
            sopimusnumero: "1",
            voimassa: true
          },
          sopimus2 = {
            sopimusnumero: "2",
            voimassa: true
          },
          sopimus_ja_tutkinto = [{
            jarjestamissopimus: sopimus1
          }, {
            jarjestamissopimus: sopimus2
          }];

          beforeEach(function() {
            tutkinto = {
              tutkintotoimikunta: [],
              sopimus_ja_tutkinto: sopimus_ja_tutkinto,
              voimassa: true
            };
          });

          testaaSopimustenAlustusJaOdota({nykyiset: [sopimus1, sopimus2], vanhat: []});
        });

        describe('kaksi sopimusta, vain toinen voimassa:', function() {
          var sopimus1 = {
            sopimusnumero: "1",
            voimassa: true
          },
          sopimus2 = {
            sopimusnumero: "2",
            voimassa: false
          },
          sopimus_ja_tutkinto = [{
            jarjestamissopimus: sopimus1
          }, {
            jarjestamissopimus: sopimus2
          }];

          beforeEach(function() {
            tutkinto = {
              tutkintotoimikunta: [],
              sopimus_ja_tutkinto: sopimus_ja_tutkinto,
              voimassa: true
            };
          });

          testaaSopimustenAlustusJaOdota({nykyiset: [sopimus1], vanhat: [sopimus2]});
        });
      });

      describe('tutkinto ei ole voimassa:', function() {
        describe('sopimuksia ei erotella:', function() {
          var sopimus1 = {
            sopimusnumero: "1",
            voimassa: false
          },
          sopimus2 = {
            sopimusnumero: "2",
            voimassa: true
          },
          sopimus_ja_tutkinto = [{
            jarjestamissopimus: sopimus1
          }, {
            jarjestamissopimus: sopimus2
          }];

          beforeEach(function() {
            tutkinto = {
              tutkintotoimikunta: [],
              sopimus_ja_tutkinto: sopimus_ja_tutkinto,
              voimassa: false
            };
          });

          testaaSopimustenAlustusJaOdota({nykyiset: [sopimus1, sopimus2], vanhat: []});
        });
      });
    });
  });
});
