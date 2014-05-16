'use strict';

angular.module('mock.EnumResource', []).factory('EnumResource', function() {
  return {
    get: function(_, callback) {
      callback([{nimi: 'valinta1'}, {nimi: 'valinta2'}, {nimi: 'valinta3'}]);
    }
  };
});
