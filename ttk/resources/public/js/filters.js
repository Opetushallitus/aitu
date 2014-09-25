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

angular.module('filters', [] )

  .filter('sivutusSuodatin', function() {
    return function(suodatettava, nykyinenSivu, tuloksiaSivulla) {
      return _(suodatettava).rest((nykyinenSivu - 1)*tuloksiaSivulla).first(tuloksiaSivulla).value();
    };
  })

  .filter('kokonimi', function() {
    return function(objs, term) {
      if(term == undefined) {
        return objs;
      }
      var lowerTerm = term.toLowerCase();
      return _(objs).filter(function(obj){
        var nimi = obj.etunimi.toLowerCase() + ' ' + obj.sukunimi.toLowerCase();
        return (nimi.indexOf(lowerTerm) > -1);
      }).value();

    };
  })

  .filter('henkilonToimikunta', ['$filter', function($filter) {
    return function(objs, term) {
      if(term == undefined || term == "") {
        return objs;
      }
      return _.filter(objs, function(obj){
        var tulos = $filter('suomiJaRuotsi')(obj.jasenyydet, 'nimi', term);
        return tulos && tulos.length > 0;
      });
    };
  }])

  .filter('voimassaOlevat', function() {
    return function(entityt, voimassaolevat) {
      return _(entityt).filter({'voimassa' : voimassaolevat}).value();
    };
  })

  .filter('voimassaoloAika', ['i18n', '$filter', function(i18n, $filter) {

    var dateFormat = 'dd.MM.yyyy';

    return function(alkupvm, loppupvm) {
      if(alkupvm && loppupvm) {
        return $filter('date')(alkupvm, dateFormat) + ' - ' + $filter('date')(loppupvm, dateFormat);
      } else if (alkupvm && !loppupvm) {
        return $filter('date')(alkupvm, dateFormat) + ' ' + i18n.yleiset.alkaen;
      } else if (loppupvm && !alkupvm) {
        return $filter('date')(loppupvm, dateFormat) + ' ' + i18n.yleiset.asti;
      } else {
        return '-';
      }
    };
  }])

  .filter('suomiJaRuotsi', function() {
    return function(entityt, kentta, term) {
      if(term === undefined) {
        return entityt;
      }
      var lowerTerm = term.toLowerCase();
      return _.filter(entityt, function(entity) {
        var arvo_fi = entity[kentta + "_fi"].toLowerCase();
        var arvo_sv = entity[kentta + "_sv"].toLowerCase();
        return (arvo_fi.indexOf(lowerTerm) > -1 || arvo_sv.indexOf(lowerTerm) > -1);
      });
    };
  })

  .filter('orderByLokalisoitu', ['$filter','kieli', function($filter, kieli) {
    return function(entityt, kentta, reverse){
      return $filter('orderBy')(entityt, kentta + '_' + kieli, reverse);
    };
  }])

  .filter('orderByDate', ['$filter', 'pvm', function($filter, pvm) {
    return function(entityt, kentta, reverse){
      return $filter('orderBy')(entityt, function(entity){
        var d = pvm.parsiPvm(entity[kentta]);
        if(d) {
          return d.getTime();
        } else {
          return 0;
        }
      }, reverse);
    };
  }])

  .filter('orderByEnumArvo', ['$filter', 'i18n', function($filter, i18n){
    return function(entityt, kentta, reverse, enumNimi){
      return $filter('orderBy')(entityt, function(entity){
        return i18n.enum[enumNimi + '-arvo'][entity[kentta]];
      }, reverse);
    };
  }])

  .filter('tutkintorakenneHakuFilter', function() {
    return function(koulutusalat, term) {
      if(term === undefined) {
        return koulutusalat;
      }
      var lowerTerm = term.toLowerCase();
      var koulutusalatMapped = _.map(koulutusalat, function(koulutusala) {
        var opintoalat = _(koulutusala.opintoala).map(function(opintoala){
          var tutkinnot = _.filter(opintoala.nayttotutkinto, function(tutkinto) {
            return term.length === 0 ||
              (tutkinto.nimi_fi && tutkinto.nimi_fi.toLowerCase().indexOf(lowerTerm) > -1) ||
              (tutkinto.nimi_sv && tutkinto.nimi_sv.toLowerCase().indexOf(lowerTerm) > -1);
          });
          return _(opintoala).omit('nayttotutkinto').merge({nayttotutkinto : tutkinnot}).value();
        }).filter(function(opintoala){return opintoala.nayttotutkinto.length > 0;}).value();
        return _(koulutusala).omit('opintoala').merge({opintoala : opintoalat}).value();
      });

      return _.filter(koulutusalatMapped, function(koulutusala) {
        return koulutusala.opintoala.length > 0;
      });
    };
  })

  .filter('rivit', function() {
    return function(teksti) {
      if (teksti) {
        return teksti.split(/\n/g);
      } else {
        return [];
      }
    };
  })

  .filter('lokalisoi', ['kieli', function(kieli){
    return function(_, obj, prop) {
      return obj ? obj[prop + '_' + kieli] : '';
    };
  }])

  .filter('nimivuosi', ['$filter', function($filter) {
    return function(_, toimikunta, nimi_prop, vuosi_prop) {
      if (!toimikunta)
        return '';
      var nimi = $filter('lokalisoi')(null, toimikunta, nimi_prop);
      if(toimikunta[vuosi_prop]) {
        var vuosi = toimikunta[vuosi_prop].split(".")[2];
        return nimi + " (" + vuosi + ")";
      }
      return nimi;
    };
  }])

  .filter('orderByToimikuntaNimiVuosi', ['$filter', 'i18n', function($filter, i18n){
    return function(entityt, kentta, reverse, ttkNimi, ttkAlkupaiva){
      return $filter('orderBy')(entityt, function(entity){
        var filtered = $filter('nimivuosi')(this, entity[kentta], ttkNimi, ttkAlkupaiva);
        return filtered;
      }, reverse);
    };
  }])

  .filter('merkitseValinnatOsiin', function(){

    function merkitseValinnat(osa, valitutOsat, tunnisteProperty) {
      var hakuEhto = {};
      hakuEhto[tunnisteProperty] = osa[tunnisteProperty];

      var valittuOsa = _.find(valitutOsat, hakuEhto);

      if(valittuOsa !== undefined) {
        osa.valittu = true;
        osa.toimipaikka = valittuOsa.toimipaikka;
      } else {
        osa.valittu = false;
        osa.toimipaikka = null;
      }

      return osa;
    }

    return function (kaikkiOsat, valitutOsat, tunnisteProperty) {
      return _.map(kaikkiOsat, function(osa){return merkitseValinnat(osa, valitutOsat, tunnisteProperty);});
    }
  })

  .filter('toimipaikanNimi', function() {

    return function(toimipaikkakoodi, toimipaikat) {
      var toimipaikka = _(toimipaikat).find({toimipaikkakoodi : toimipaikkakoodi});

      if(toimipaikka) {
        return toimipaikka.nimi;
      } else {
        return toimipaikkakoodi;
      }
    }
  })

  .filter('muotoileNayttotutkintomestari', ['i18n', function(i18n) {
    return function(arvo){
      var naytettavaArvo = i18n.yleinen['ei-tiedossa'];
      if(arvo === true) {
        naytettavaArvo = i18n.yleinen.kylla;
      } else if(arvo === false) {
        naytettavaArvo = i18n.yleinen.ei;
      }
      return naytettavaArvo;
    };
  }])

  .filter('kielisyys', [function() {
    return function(entityt, kentta, terms) {
      if(_.isEmpty(terms)) {
        return entityt;
      }
      return _.filter(entityt, function(entity) {
        return _.some(terms, {'nimi': entity[kentta]});
      });
    };
  }])

  .filter('sopimukset', [function() {
    return function(entityt, ehto) {
      if(ehto === "kaikki") {
        return entityt;
      } else if (ehto === "kylla") {
        return _.filter(entityt, function(entity) {
          return entity.sopimusten_maara > 0;
        });
      } else {
        return _.filter(entityt, function(entity) {
          return entity.sopimusten_maara === 0;
        });
      }
    }
  }])

  .filter('parametrit', [function() {
    return function(input, property, kentta) {
      if(input && input.length > 0) {
        return kentta + "=" + _.pluck(input, property).join("&" + kentta + "=");
      } else {
        return "";
      }
    }
  }]);
