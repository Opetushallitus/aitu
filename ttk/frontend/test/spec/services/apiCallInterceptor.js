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

describe('apiCallInterceptor', function () {

  var pyynto = {
    id: "henkilolistaus" ,
    method: "GET",
    url: "/api/henkilo" };

  var vastaus = {
    config : {
      id: "henkilolistaus" ,
      method: "GET",
      url: "/api/henkilo" },
    status : 200 };

  var virheVastaus = _.extend(vastaus, {status: 500, data : {errors : {virhe1 : ["virheviesti1", "virheviesti2"], virhe2 : ["virheviesti3", "virheviesti4"]}}});

  beforeEach(module('mock.i18n'));
  beforeEach(module('services'));

  it('talettaa apipyynnön seurattavaksi jos pyynnölle asetettu id',  inject(function(apiCallInterceptor) {
    apiCallInterceptor.apiPyynto(pyynto);
    expect(apiCallInterceptor.pyynnot[pyynto.id].pyyntoObj).toEqual(pyynto);
  }));

  it('pyynnön lähtiessä kasvattaa pyyntojaKaynnissa -arvoa',  inject(function(apiCallInterceptor) {
    apiCallInterceptor.apiPyynto(pyynto);
    expect(apiCallInterceptor.pyynnot[pyynto.id].pyyntojaKaynnissa).toEqual(1);
  }));

  it('pyynnön vastauksen tullessa vähentää pyyntojaKaynnissa -arvoa',  inject(function(apiCallInterceptor) {
    apiCallInterceptor.apiPyynto(pyynto);
    apiCallInterceptor.apiVastaus(vastaus);
    expect(apiCallInterceptor.pyynnot[pyynto.id].pyyntojaKaynnissa).toEqual(0);
    expect(apiCallInterceptor.pyynnot[pyynto.id].viimeinenPyyntoOnnistui).toEqual(true);
  }));

  it('pyynnön virhevastaus vähentää pyyntojaKaynnissa -arvoa sekä kasvattaa virheet -arvoa',  inject(function(apiCallInterceptor) {
    apiCallInterceptor.apiPyynto(pyynto);
    apiCallInterceptor.apiVastaus(virheVastaus, true);
    expect(apiCallInterceptor.pyynnot[pyynto.id].pyyntojaKaynnissa).toEqual(0);
    expect(apiCallInterceptor.pyynnot[pyynto.id].viimeinenPyyntoOnnistui).toEqual(false);
  }));

  it('tallettaa vastauksen kun pyynnön id on asetettu',  inject(function(apiCallInterceptor) {
    var p = _.extend({okViesti : "operaatio onnistui"}, pyynto);
    apiCallInterceptor.apiPyynto(p);
    apiCallInterceptor.apiVastaus(vastaus);
    expect(apiCallInterceptor.vastaukset.lista[0]).toEqual(vastaus);
  }));

  it('tallettaa virheellsen vastauksen',  inject(function(apiCallInterceptor) {
    var vastaus = {status: 200, config : {}}
    apiCallInterceptor.apiPyynto({});
    apiCallInterceptor.apiVastaus(vastaus, true);
    expect(apiCallInterceptor.vastaukset.lista[0]).toEqual(vastaus);
  }));


});