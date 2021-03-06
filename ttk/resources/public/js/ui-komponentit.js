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

angular.module('uiKomponentit', ['ui.bootstrap', 'ngCookies'])

  .config(['datepickerConfig', function(datepickerConfig) {
    datepickerConfig.formatDay = 'd';
    datepickerConfig.formatMonth = 'MMMM';
    datepickerConfig.formatYear = 'yyyy';
    datepickerConfig.formatDayHeader = 'EEE';
    datepickerConfig.formatDayTitle = 'MMMM yyyy';
    datepickerConfig.formatMonthTitle = 'yyyy';
    datepickerConfig.showWeeks = false;
    datepickerConfig.startingDay = 1;
  }])

  .run(['i18n', 'kieli', '$cookies', function(i18n, kieli, $cookies) {
    $.fn.select2.ajaxDefaults.params.headers = {"Accept-Language" : kieli, "x-xsrf-token" : $cookies.get('XSRF-TOKEN')};
  }])