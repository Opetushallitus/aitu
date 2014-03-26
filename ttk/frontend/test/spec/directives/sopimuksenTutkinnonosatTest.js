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

describe('Controllers: Directives: sopimuksenTutkinnonosat:', function() {

  var controller;
  var scope;

  var testiTutkinnonOsa1 = {nimi: 'testiTutkinnonOsa1', osatunnus : 'osatunnus1'};
  var testiTutkinnonOsa2 = {nimi: 'testiTutkinnonOsa2', osatunnus : 'osatunnus2'};

  var testiOsaamisala1 = {nimi: 'testiOsaamisala1',  osaamisalatunnus : 'osaamisalatunnus1'};
  var testiOsaamisala2 = {nimi: 'testiOsaamisala2',  osaamisalatunnus : 'osaamisalatunnus2'};

  var toimipaikat = [{nimi: 'toimipaikka1', toimipaikkakoodi: 'a1234'}];

  beforeEach(function(){
    module('sopimukset');
  });

  function alustaTestidataJaSiirryOsienValintaan() {
    scope.muokkaus = true;
    scope.kaikkiTutkinnonosat = [testiTutkinnonOsa1, testiTutkinnonOsa2];
    scope.kaikkiOsaamisalat = [testiOsaamisala1, testiOsaamisala2];
    scope.valitutTutkinnonosat = [];
    scope.valitutOsaamisalat = [];
    scope.toimipaikat = toimipaikat;

    scope.$digest();

    scope.valitseKaikki = 'false';
    scope.valitseKaikkiChange();
  }

  beforeEach(inject(function($controller, $rootScope) {
    scope = $rootScope.$new(true);
    controller = $controller('sopimuksenTutkinnonosatController', {$scope: scope});
  }));

  it('Ei näytä tutkinnon osien valinta radiobuttoneita jos ei olla muokkaustilassa', function (){
    scope.muokkaus = false;
    expect(scope.naytaTutkinnonosienValinta()).toEqual(false);
  });

  it('Ei näytä muokkaustilassa tutkinnon osien valinta radiobuttoneita, jos tutkinto ei sisällä tutkinnon osia tai osaamisaloja', function(){
    scope.muokkaus = true;
    scope.kaikkiTutkinnonosat = [];
    scope.kaikkiOsaamisalat = [];

    expect(scope.naytaTutkinnonosienValinta()).toEqual(false);
  });

  it('Näyttää muokkaustilassa tutkinnon osien valinta radiobuttonit, jos tutkinto sisältää tutkinnon osia', function () {
    scope.muokkaus = true;
    scope.kaikkiTutkinnonosat = [testiTutkinnonOsa1];
    scope.kaikkiOsaamisalat = [];

    expect(scope.naytaTutkinnonosienValinta()).toEqual(true);
  });

  it('Näyttää muokkaustilassa tutkinnon osien valinta radiobuttonit, jos tutkinto sisältää osaamisaloja', function () {
    scope.muokkaus = true;
    scope.kaikkiTutkinnonosat = [];
    scope.kaikkiOsaamisalat = [testiOsaamisala1];

    expect(scope.naytaTutkinnonosienValinta()).toEqual(true);
  });

  it('Asettaa valitseKaikki arvoon true jos ei ole valittuja tutkinnon osia tai osaamisaloja ', function(){
    scope.muokkaus = true;
    scope.kaikkiTutkinnonosat = [testiTutkinnonOsa1];
    scope.kaikkiOsaamisalat = [testiOsaamisala1];
    scope.valitutTutkinnonosat = [];
    scope.valitutOsaamisalat = [];

    scope.$digest();

    expect(scope.valitseKaikki).toEqual('true');
  });

  it('Asettaa valitseKaikki arvoon false jos on valittuja tutkinnon osia tai osaamisaloja ', function(){
    scope.muokkaus = true;
    scope.kaikkiTutkinnonosat = [testiTutkinnonOsa1];
    scope.kaikkiOsaamisalat = [testiOsaamisala1];
    scope.valitutTutkinnonosat = [testiTutkinnonOsa1];
    scope.valitutOsaamisalat = [testiOsaamisala2];

    scope.$digest();

    expect(scope.valitseKaikki).toEqual('false');
  });

  it('Kaikki tutkinnon tutkinnon osat ja osaamisalat valitaan aluksi, kun radiobuttoneista valitaan optio "seuraavat tutkinnon osat ja osaamisalat.' +
    'Toimipaikaksi asetetaan oletuksena ensimmäinen toimipaikkalistalta.', function(){
    alustaTestidataJaSiirryOsienValintaan();

    expect(scope.valitutTutkinnonosat).toEqual([_.assign(testiTutkinnonOsa1, {toimipaikka : toimipaikat[0].toimipaikkakoodi}),
                                                _.assign(testiTutkinnonOsa2, {toimipaikka : toimipaikat[0].toimipaikkakoodi})]);
    expect(scope.valitutOsaamisalat).toEqual([_.assign(testiOsaamisala1, {toimipaikka : toimipaikat[0].toimipaikkakoodi}),
                                              _.assign(testiOsaamisala2, {toimipaikka : toimipaikat[0].toimipaikkakoodi})]);
    expect(scope.tutkinnonosaValittu(testiTutkinnonOsa1)).toEqual(true);
    expect(scope.tutkinnonosaValittu(testiTutkinnonOsa2)).toEqual(true);
    expect(scope.osaamisalaValittu(testiOsaamisala1)).toEqual(true);
    expect(scope.osaamisalaValittu(testiOsaamisala2)).toEqual(true);
  });

  it('Tutkinnon osia pystyy poistamaan ja lisäämään valittuihin', function () {
    alustaTestidataJaSiirryOsienValintaan();

    expect(scope.tutkinnonosaValittu(testiTutkinnonOsa1)).toEqual(true);
    expect(scope.tutkinnonosaValittu(testiTutkinnonOsa2)).toEqual(true);

    scope.valitseTutkinnonosa(testiTutkinnonOsa1);

    expect(scope.tutkinnonosaValittu(testiTutkinnonOsa1)).toEqual(false);
    expect(scope.tutkinnonosaValittu(testiTutkinnonOsa2)).toEqual(true);

    scope.valitseTutkinnonosa(testiTutkinnonOsa1);

    expect(scope.tutkinnonosaValittu(testiTutkinnonOsa1)).toEqual(true);
    expect(scope.tutkinnonosaValittu(testiTutkinnonOsa2)).toEqual(true);
  });

  it('Osaamisaloja osia pystyy poistamaan ja lisäämään valittuihin', function () {
    alustaTestidataJaSiirryOsienValintaan();

    expect(scope.osaamisalaValittu(testiOsaamisala1)).toEqual(true);
    expect(scope.osaamisalaValittu(testiOsaamisala2)).toEqual(true);

    scope.valitseOsaamisala(testiOsaamisala1);

    expect(scope.osaamisalaValittu(testiOsaamisala1)).toEqual(false);
    expect(scope.osaamisalaValittu(testiOsaamisala2)).toEqual(true);

    scope.valitseOsaamisala(testiOsaamisala1);

    expect(scope.osaamisalaValittu(testiOsaamisala1)).toEqual(true);
    expect(scope.osaamisalaValittu(testiOsaamisala2)).toEqual(true);
  });
})