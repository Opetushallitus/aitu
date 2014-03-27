'use strict';

angular.module('aitulocale', [], ['$provide', function($provide) {
  var i18n = window.aituI18n;
  var PLURAL_CATEGORY = {ZERO: 'zero', ONE: 'one', TWO: 'two', FEW: 'few', MANY: 'many', OTHER: 'other'};

  var paivat = i18n.kalenteri.paivat.split(',');
  var kuukaudet = i18n.kalenteri.kuukaudet.split(',');

  var kieli =  window.location.pathname.indexOf('/fi/') > -1 ? 'fi' : 'sv';

  $provide.value('i18n', i18n);

  $provide.value('kieli', kieli);

  $provide.value('$locale', {
    'DATETIME_FORMATS': {
      'AMPMS': [
        'ap.',
        'ip.'
      ],
      'DAY': paivat,
      'MONTH': kuukaudet,
      'SHORTDAY': paivat,
      'SHORTMONTH': kuukaudet,
      'fullDate': 'cccc, d. MMMM y',
      'longDate': 'd. MMMM y',
      'medium': 'd.M.yyyy H.mm.ss',
      'mediumDate': 'd.M.yyyy',
      'mediumTime': 'H.mm.ss',
      'short': 'd.M.yyyy H.mm',
      'shortDate': 'dd.MM.yyyy',
      'shortTime': 'H.mm'
    },
    'NUMBER_FORMATS': {
      'CURRENCY_SYM': '\u20ac',
      'DECIMAL_SEP': ',',
      'GROUP_SEP': '\u00a0',
      'PATTERNS': [
        {
          'gSize': 3,
          'lgSize': 3,
          'macFrac': 0,
          'maxFrac': 3,
          'minFrac': 0,
          'minInt': 1,
          'negPre': '-',
          'negSuf': '',
          'posPre': '',
          'posSuf': ''
        },
        {
          'gSize': 3,
          'lgSize': 3,
          'macFrac': 0,
          'maxFrac': 2,
          'minFrac': 2,
          'minInt': 1,
          'negPre': '-',
          'negSuf': '\u00a0\u00a4',
          'posPre': '',
          'posSuf': '\u00a0\u00a4'
        }
      ]
    },
    'id': kieli,
    'pluralCat': function (n) {  if (n == 1) {   return PLURAL_CATEGORY.ONE;  }  return PLURAL_CATEGORY.OTHER;}
  });
}]);