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

angular.module('mock.i18n', []).factory('i18n', function() {
  return {
    'virheet' : {'virhe-api-metodissa' : 'Default virheviesti' },
    'yleiset' : {'alkaen' : 'alkaen', 'asti' : 'asti'},
    'henkilo' : {
      'muokkaus-onnistui' : 'Muokkaus onnistui',
      'muokkaus-ei-onnistunut' : 'Muokkaus ei onnistunut',
      'uuden-luonti-onnistui' : 'Uuden luonti onnistui',
      'uuden-luonti-ei-onnistunut' : 'Uuden luonti ei onnistunut',
      'etunimi' : 'Etunimi (lokalisoitu)',
      'kustomoitu-operaation-nimi-onnistui' : 'Kustomoitu ilmoitus onnistumisesta',
      'kustomoitu-operaation-nimi-ei-onnistunut' : 'Kustomoitu ilmoitus epäonnistumisesta'
    },
    'palautteet' : {
      'api-metodi-kutsu-ei-onnistunut' : 'Virhe metodissa'
    },
    'yleinen' : {
      'kylla' : 'kyllä',
      'ei' : 'ei'
    }
  };
});