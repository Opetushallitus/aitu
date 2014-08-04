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

angular.module('services', ['ngResource', 'ngCookies'])
  .factory('apiCallInterceptor', ['i18n', function(i18n){

    var pyynnot = {};
    var vastaukset = {
      paivitetty : null,
      lista : []
    };
    var vastausCallbackit = {};

    function asetaVastausCallback(metodiId, callback) {
      if(!vastausCallbackit[metodiId]) {
        vastausCallbackit[metodiId] = [];
      }

      vastausCallbackit[metodiId].push(callback);
    }

    // Käytetään suhteellista aikaleimaa, koska saman millisekunnin aikana voi
    // tulla käsittelyyn useita pyyntöjä ja vastauksia. Ennen käytettiin
    // aikaleimana new Date().getTime(), mikä aiheutti bugin OPH-330.
    var seuraavaAikaleima = 0;
    function aikaleima() {
      return seuraavaAikaleima++;
    }

    function seuraaPyyntoa(pyynto) {
      return pyynto.id; //Seuraa pyyntöjä joilla id asetettu. Muista pyynnöistä talletetaan vain virhevastaukset.
    }

    function apiPyynto(pyynto) {
      if(seuraaPyyntoa(pyynto)) {
        if(pyynnot[pyynto.id]) {
          pyynnot[pyynto.id].pyyntojaKaynnissa++;
          pyynnot[pyynto.id].paivitetty = aikaleima();
        } else {
          pyynnot[pyynto.id] = {url : pyynto.url, pyyntojaKaynnissa: 1, viimeinenPyyntoOnnistui : true, paivitetty : aikaleima(), pyyntoObj : pyynto};
        }
      }
    }

    function apiVastaus(vastaus, virhe) {
      var seuraa = seuraaPyyntoa(vastaus.config);

      if(seuraa) {
        var id = vastaus.config.id;
        var pyynto = pyynnot[id];
        pyynto.pyyntojaKaynnissa--;
        pyynto.viimeinenPyyntoOnnistui = !virhe;
        pyynto.paivitetty = aikaleima();

        if(vastausCallbackit[id]) {
          _.each(vastausCallbackit[id], function(callback) {callback();});
        }
      }

      if(seuraa || virhe) {
        vastaukset.paivitetty = aikaleima();
        vastaukset.lista.push(vastaus);
      }
    }

    return {
      pyynnot : pyynnot,
      vastaukset : vastaukset,
      apiPyynto : apiPyynto,
      apiVastaus : apiVastaus,
      asetaVastausCallback : asetaVastausCallback
    };

  }])
  .factory('varmistaPoistuminen', ['$rootScope', 'i18n', function($rootScope, i18n) {
    var kysyVarmistus = false;

    window.addEventListener("beforeunload", confirmBeforeUnload, false);

    $rootScope.$on('$locationChangeStart', function (event) {
      if(kysyVarmistus) {
        if (confirm(i18n["haluatko-poistua"])) {
          kysyVarmistus = false;
        } else {
          event.preventDefault();
        }
      }
    });

    function confirmBeforeUnload(e) {
      if (kysyVarmistus) {
        var confirmationMessage = i18n["haluatko-poistua"];
        (e || window.event).returnValue = confirmationMessage;     //Gecko + IE
        return confirmationMessage;                                //Webkit, Safari, Chrome etc.

        // Toisin kuin Angularin $locationChangeStart-eventissä, täällä ei
        // tarvitse välittää varmistaPoistuminen-flagin myöhemmistä arvoista,
        // koska unloadin jälkeen kaikki skriptit ladataan uudestaan.
      }
    }

    return {
      kysyVarmistusPoistuttaessa : function() {
        kysyVarmistus = true;
      },
      tallenna : function(resource, okCallback) {
        var promise = resource.$promise ?  resource.$promise : resource;
        promise.then(
          function(response){
            kysyVarmistus = false;
            okCallback(response);
          });
      }
    };
  }])

  .factory('pvm',['$filter', function($filter) {
    return {
      parsiPvm : function(pvm) {
        if(pvm) {
          try {
            var parts = pvm.split('.');
            var parsittu = new Date(parts[2], parts[1] - 1, parts[0]);
            if(parsittu.getDate() == parts[0] && parsittu.getMonth() == parts[1] -1 && parsittu.getFullYear() == parts[2]) {
              return parsittu;
            }
          } catch(e) {}
        }
        return null;
      },
      dateToPvm : function(date) {
        return $filter('date')(date, 'dd.MM.yyyy');
      }
    };
  }])

  .factory('edellinenLokaatio', ['$location', '$rootScope', function($location, $rootScope){

    var nykyinen = $location.path();
    var edellinen;

    $rootScope.$on('$locationChangeSuccess', function(){
      edellinen = nykyinen;
      nykyinen = $location.path();
    });

    return function() {
      if(edellinen) {
        $location.path(edellinen);
      }
    }
  }])

  .factory('boolValues', ['i18n',  function(i18n){
    return [{value: true, name: i18n['yleinen']['kylla']}, {value: false, name: i18n['yleinen']['ei']}];
  }])

  .factory('debounce', ['$timeout', function($timeout) {

    return function(fn, delay) {
      var promise,
        thisArg;

      return function(){
        var args = arguments;
        thisArg = this;
        $timeout.cancel(promise);
        promise = $timeout(function(){
          fn.apply(thisArg, args);
        }, delay);
      };
    };
  }])

  .run(['edellinenLokaatio',  function(edellinenLokaatio){
    //Injektointi serviceille, jotka halutaan instantioida heti.
  }]);
