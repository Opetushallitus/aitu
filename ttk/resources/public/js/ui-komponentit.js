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

angular.module('uiKomponentit', ['ui.bootstrap'])

  .constant('datepickerConfig', {
    dayFormat: 'd',
    monthFormat: 'MMMM',
    yearFormat: 'yyyy',
    dayHeaderFormat: 'EEE',
    dayTitleFormat: 'MMMM yyyy',
    monthTitleFormat: 'yyyy',
    showWeeks: false,
    startingDay: 1,
    yearRange: 20,
    minDate: null,
    maxDate: null
  })

  .run(['i18n', 'kieli', function(i18n, kieli) {
    $.fn.select2.ajaxDefaults.params.headers = {"Accept-Language" : kieli};
  }])