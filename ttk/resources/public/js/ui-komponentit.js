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

angular.module('uiKomponentit', ['ui.bootstrap', '$strap.directives'])

  .value('$strapConfig', {
    datepicker: {
      language: 'fi/sv',
      format: 'dd.mm.yyyy',
      weekStart: 1,
      minViewMode : 0,
      viewMode : 0

    }
  })

  .run(['i18n', 'kieli', function(i18n, kieli) {
    var paivat = i18n.kalenteri.paivat.split(',');
    var kuukaudet = i18n.kalenteri.kuukaudet.split(',');

    $.fn.datepicker.dates['fi/sv'] = {
      days: paivat,
      daysShort: paivat,
      daysMin: paivat,
      months: kuukaudet,
      monthsShort: kuukaudet
    };

    //Jätetään pois tarpeeton vuosinäkymä datepickeristä.
    var datePickerProto = $.fn.datepicker.Constructor.prototype;
    datePickerProto._showMode = datePickerProto.showMode;
    datePickerProto.showMode = showModeWithoutYear;

    function showModeWithoutYear(arg) {
      if(isNaN(arg) || this.viewMode + arg <= 1) {
        datePickerProto._showMode.apply(this, [arg]);
      }
    }

    $.fn.select2.ajaxDefaults.params.headers = {"Accept-Language" : kieli};

  }])