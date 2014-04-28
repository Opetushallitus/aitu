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

angular.module('directives', ['services', 'resources'])
  .directive('hakutulokset', function($parse, i18n){
    return {
      restrict: 'E',
      scope : {
        hakutulokset : "=",
        tuloksiaSivulla: "=",
        otsikot: "@"
      },
      transclude: true,
      templateUrl: 'template/hakutulokset',
      link: function(scope, element, attrs) {
        scope.parsitutOtsikot = eval(scope.otsikot);
        scope.nykyinenSivu = 1;
        scope.$watchCollection('hakutulokset', function(){
          scope.nykyinenSivu = 1;
        });
        scope.i18n = i18n;
      }
    };
  })
  .directive('hakutuloslaskuri', function(){
    return {
      restrict: 'E',
      templateUrl: 'template/hakutuloslaskuri',
      link: function(scope, element, attrs) {
        scope.mista = function(nykyinenSivu, tuloksiaSivulla ){ return (nykyinenSivu - 1) * tuloksiaSivulla + 1; };
        scope.mihin = function(nykyinenSivu, tuloksiaSivulla, tuloksiaYhteensa  ){return Math.min((nykyinenSivu - 1) * tuloksiaSivulla + tuloksiaSivulla, tuloksiaYhteensa );};
      }
    };
  })
  .directive('sivutusnavigaatio', function(){
    return {
      restrict: 'E',
      templateUrl: 'template/sivutusnavigaatio'
    };
  })
  .directive('latausIndikaattori', ['apiCallInterceptor', function(apiCallInterceptor){

    function tarkastaPyynnonTila(metodiIdt, tarkastusFunktio) {
      var val = _.all( metodiIdt,
        function(id) {
          var pyynto = _.pick(apiCallInterceptor.pyynnot, id);
          return !_.isEmpty(pyynto) ? _.all(_.values(pyynto), tarkastusFunktio) : true;
        });
      return val;
    }

    function paivitaStatus(metodiIdt, scope) {
      var ok = tarkastaPyynnonTila(metodiIdt, function(pyynto) {return pyynto.viimeinenPyyntoOnnistui;});
      var valmis = tarkastaPyynnonTila(metodiIdt, function(pyynto) {return pyynto.pyyntojaKaynnissa === 0;});
      scope.latausKaynnissa = !valmis;
      scope.virhe = !ok;
    }

    function statusPaivitettyViimeksi(metodiIdt) {
      return _(apiCallInterceptor.pyynnot).pick(metodiIdt).map(function(pyynto){return pyynto.paivitetty;}).max().value();
    }

    return {
      scope : {
        viesti: '@',
        metodiIdt : '@',
        virheviesti: '@',
        yritaUudelleen : '&'
      },
      transclude: true,
      templateUrl : 'template/latausindikaattori',
      restrict: 'A',
      link: function(scope, element, attrs) {
        var idt = eval(attrs.metodiIdt);
        paivitaStatus(idt, scope);

        scope.$watch(function() {
          return statusPaivitettyViimeksi(idt);
        }, function(paivitetty){
          if(paivitetty) {
            paivitaStatus(idt, scope);
          }
        });
      }
    };
  }])
  .directive('kielenVaihto', ['kieli', function(kieli){
    return {
      restrict: 'E',
      scope : {},
      templateUrl : 'template/kielen-vaihto',
      replace: true,
      link: function(scope, element, attrs) {
        scope.locale = kieli;
      }
    };
  }])
  .directive('sopimustenListaus', function(){
    return {
      restrict: 'E',
      scope : {
        sopimukset : '=',
        naytaTutkinnot : '=',
        naytaPerusteSarake : '=',
        otsikko : '=',
        piilotaJarjestajat : '='
      },
      templateUrl : 'template/sopimusten-listaus',
      link: function(scope) {}
    };
  })
  .directive('vanhojenSopimustenListaus', function(){
    return {
      restrict: 'E',
      scope : {
        sopimukset : '=',
        naytaTutkinnot : '=',
        otsikko : '=',
        piilotaJarjestajat : '='
      },
      templateUrl : 'template/vanhojen-sopimusten-listaus',
      link: function(scope) {
        scope.nautaVanhatSopimukset = false;
        scope.toggleNaytaVanhat = function() {
          scope.nautaVanhatSopimukset = !scope.nautaVanhatSopimukset;
        };
      }
    };
  })
  .directive('enumValikko', ['i18n', 'EnumResource', '$compile', function (i18n, EnumResource, $compile) {
    return {
      restrict: 'E',
      replace: true,
      scope: {
        arvo: "=",
        nimi: "@"
      },
      template : '<span></span>',
      link: function (scope, element, attrs) {
        var template = '<select ng-model="arvo">';
        if(attrs.sallityhja) template += '<option value=""></option>';
        template += '<option ng-selected="{{arvo === e.nimi}}" ng-repeat="e in arvot" value="{{e.nimi}}">{{ i18n.enum[nimi + "-arvo"][e.nimi] }}</option></select>';
        scope.i18n = i18n;
        EnumResource.get({'enum': attrs.nimi}, function(arvot){
          scope.arvot = arvot;
          //Compiletaan template vasta kun enumdata on saatavilla. Muutoin ie9 rendaa selectin väärin.
          var compiled = $compile(template)(scope);
          element.append(compiled);
        });
      }
    };
  }])
  .directive('enumMonivalinta', ['i18n', 'EnumResource', function (i18n, EnumResource) {
    return {
      restrict: 'E',
      replace: true,
      scope: {
        arvot: "=",
        otsikko: "@",
        nimi: "@"
      },
      templateUrl : 'template/enum-monivalinta',
      controller: function ($scope) {
        var data = [];
        function enumNimi(item) {
          return i18n.enum[$scope.nimi + '-arvo'][item.nimi];
        }
        $scope.optiot = {
          width: '100%',
          multiple: true,
          data: function() {
            return { results: data, text: enumNimi };
          },
          formatSelection: enumNimi,
          formatResult: enumNimi,
          id : function(object) {
            return object.nimi;
          },
          formatNoMatches: function () {
            return i18n.yleiset['ei-tuloksia'];
          }
        };
        EnumResource.get({'enum': $scope.nimi}, function(enumArvot){ data = enumArvot; });
      }
    };
  }])
  .directive('enumArvo', ['i18n', function (i18n) {
    return {
      restrict: 'E',
      replace: true,
      scope: {
        arvo: "=",
        nimi: "@"
      },
      template: "<span>{{ i18n.enum[nimi + '-arvo'][arvo] }}</span>",
      link: function (scope, element, attrs) {
        scope.i18n = i18n;
      }
    };
  }])
  .directive('pvmValitsin', ['pvm',  function(pvm) {
    return {
      restrict: 'E',
      replace: true,
      scope : {
        valittuPvm : '=',
        oletusPvm : '=',
        minPvm : '=',
        maxPvm : '=',
        otsikko : '@'
      },
      templateUrl : 'template/pvm-valitsin',
      link : function(scope) {
        if(!scope.valittuPvm) {
          scope.valittuPvm = scope.oletusPvm;
        }

        //Bootstrap datepicker ei osaa parsia muotoa dd.MM.yyyy päivämäärästringejä.
        //Muunnetaan string muotoiset päivämäärät dateiksi.
        scope.$watch('valittuPvm', function(value) {
          if(value && !scope.valittuDate) {
            scope.valittuDate = pvm.parsiPvm(value);
          }
        });

        scope.$watch('valittuDate', function(value) {
          scope.valittuPvm = pvm.dateToPvm(value);
        });

        scope.$watch('minPvm', function(value) {
          if(value) {
            scope.minDate = pvm.parsiPvm(value);
          }
        });

        scope.$watch('maxPvm', function(value) {
          if(value) {
            scope.maxDate = pvm.parsiPvm(value);
          }
        });

        scope.open = function($event) {
          $event.preventDefault();
          $event.stopPropagation();
          scope.opened = true;
        };
      }
    };
  }])
  .directive('formatteddate', ['$filter', 'pvm', function ($filter, pvm) {

    function parseDate(viewValue) {
      if(typeof viewValue === 'string' && viewValue !== '') {
        var parsittu = pvm.parsiPvm(viewValue);
        if(parsittu) {
          return parsittu;
        } else {
          return "Invalid date";
        }
      }
      return viewValue;
    }

    return {
      link: function (scope, element, attrs, ctrl) {
        ctrl.$parsers.unshift(parseDate);
      },
      priority: 1, //<-- Formatteddate- direktiivin link funktio suoritetaan datepickerin link funktion jälkeen.
                   //    Näin saadaan custom parsefuktio parseriketjun ensimmäiseksi
      restrict: 'A',
      require: 'ngModel'
    };
  }])
  .directive('accordion', function(){
    return {
      link: function(scope, element, attrs) {
        var header = element.find('.accordion-header');
        var body = element.find('.accordion-body');

        if(attrs.oletuksenaAuki) {
          avaaTaiSulje();
        }

        function avaaTaiSulje() {
          header.toggleClass('collapse');
          body.toggleClass('collapse');
        }

        header.bind('click', function(){
          avaaTaiSulje();
        });
      }
    };
  })
  .directive('hakuValitsin', ['i18n', 'kieli',  function(i18n, kieli){
    return {
      restrict: 'E',
      replace: true,
      scope : {
        otsikko : '@',
        url : '@',
        model : '=',
        // Select2 hävittää saamastaan model-oliosta kenttiä valinnan
        // vaihtuessa. Tämän vuoksi ei voida antaa Angular-scopessa olevaa
        // modelia suoraan Select2:lle, vaan annetaan sille eri olio, ja
        // pidetään watcheilla niiden id- ja text-kentät synkassa.
        modelIdProperty : '@',
        modelTextProperty : '@',
        searchPropertyMap : '@'
      },
      templateUrl : 'template/haku-valitsin',
      controller : function($scope) {
        var modelIdProp = $scope.modelIdProperty;
        var modelTextProp = $scope.modelTextProperty;
        var searchPropertyMap = $scope.$eval($scope.searchPropertyMap);

        $scope.selection = $scope.model || {};

        $scope.$watch('selection', function(value){
          if(value && value[modelIdProp]) {
            $scope.model = $scope.model || {};
            _.assign($scope.model, value);
          }
          else if ($scope.model) {
            delete $scope.model[modelIdProp];
          }
        });

        $scope.$watch('model', function(value) {
          if (value) {
            $scope.selection[modelIdProp] = value[modelIdProp];
            $scope.selection[modelTextProp] = lokalisoituTeksti(value, modelTextProp);
          }
        });

        function lokalisoituTeksti(obj, textProp) {
          var teksti = '';

          if(_.has(obj, textProp)) {
            teksti = obj[textProp];
          } else {
            teksti = obj[textProp + '_' + kieli];
          }
          return teksti;
        }

        function mapSearchResult(obj) {
          if (searchPropertyMap) {
            return _.transform(searchPropertyMap, function (result, toKey, fromKey) {
              result[toKey] = obj[fromKey];
            });
          } else {
            return obj;
          }
        }

        $scope.options = {
          width: '100%',
          minimumInputLength : 1,
          allowClear : true,
          ajax: {
            url : $scope.url,
            dataType: 'json',
            quietMillis: 500,
            data: function (term) {
              return {
                termi: term // search term
              };
            },
            results: function (data) {
              return {results: _.map(data, mapSearchResult)};
            }
          },
          formatResult : function(object) {
            return lokalisoituTeksti(object, modelTextProp);
          },
          formatSelection : function(object) {
            return lokalisoituTeksti(object, modelTextProp);
          },
          id : function(object) {
            return object[modelIdProp];
          },
          formatNoMatches: function () {
            return i18n.yleiset['ei-tuloksia'];
          },
          formatInputTooShort: function () {
            return i18n.yleiset['anna-hakuehto'];
          },
          formatSearching: function () {
            return i18n.yleiset['etsitaan'];
          }
        };
      }
    };
  }])

  .directive('fileUpload', function(){
    return {
      restrict: 'E',
      replace : true,
      templateUrl : 'template/file-upload',
      scope : {
        apiMetodi : '@',
        uploadValmis : '=',
        liitetyyppi: '@'
      },
      link: function(scope, el, attrs){
        scope.apiMetodi = attrs.apiMetodi;
        scope.tiedostoValittu = false;
        scope.reset = reset;

        el.find('input[type=file]').change(function(event){
          var filename = event.target.value? _.last(event.target.value.split('\\')) : '';
          scope.tiedostoValittu = filename.length > 0;
          el.find('input.valittu-tiedosto').val(filename);
          scope.$apply();
        });

        scope.uploadOk = function(r){
          reset();
          scope.uploadValmis(r, scope.liitetyyppi);
        };

        function reset() {
          scope.tiedostoValittu = false;
          el.find('form')[0].reset();
        }
      }
    };
  })

  .directive('copyright', function() {
    return {
      restrict: 'A',
      replace : true,
      template : '<span>Copyright &copy; Opetushallitus {{vuosi}} <span class="separator">|</span> <a href="http://www.oph.fi">www.oph.fi</a></span>',
      scope : {},
      link : function(scope) {
        scope.vuosi = new Date().getFullYear();
      }
    };
  })

  .directive('jarjestettavaTaulukko', function() {
    return {
      restrict: 'A',
      scope : true,
      controller : 'jarjestettavaTaulukkoController'
    };
  })

  .controller('jarjestettavaTaulukkoController', ['$scope', '$attrs', '$filter', function($scope, $attrs, $filter) {
    var sarakkeet = {};
    var nykyinenJarjestys;
    var vastakkainenJarjestys = false;
    var jarjestettavaData;

    $scope.$watch($attrs.jarjestettavaTaulukko, function(value) {
      jarjestettavaData = value;
      if(jarjestettavaData) {
        jarjesta();
      }
    });

    this.lisaaJarjestettavaSarake = function(sarake, elementtiScope, oletusJarjestely) {
      sarakkeet[sarake] = elementtiScope;
      if(oletusJarjestely) {
        nykyinenJarjestys = sarake;
      }
    };

    this.sarakettaKlikattu = function(sarake) {
      $scope.$apply(function(){
        vastakkainenJarjestys = nykyinenJarjestys === sarake ? !vastakkainenJarjestys : false;
        nykyinenJarjestys = sarake;
        jarjesta();
      });
    };

    function jarjesta() {
      if(nykyinenJarjestys) {
        _(sarakkeet).omit(nykyinenJarjestys).forIn(function(elementtiScope){elementtiScope.asetaJarjestely(false);});
        sarakkeet[nykyinenJarjestys].asetaJarjestely(true, vastakkainenJarjestys);

        var jarjestelyFn = sarakkeet[nykyinenJarjestys].jarjestelyFn;

        if(!jarjestelyFn) {
          jarjestelyFn = 'orderBy';
        }

        var jarjestelyFnParts = jarjestelyFn.split(':');

        $scope[$attrs.jarjestettyData] = $filter(_.first(jarjestelyFnParts)).apply(this, [jarjestettavaData, nykyinenJarjestys, vastakkainenJarjestys].concat(_.rest(jarjestelyFnParts)));
      }
    }
  }])

  .directive('jarjestettavaSarake', function() {
    return {
      restrict: 'A',
      scope : {
        jarjestelyFn : '@'
      },
      require: '^jarjestettavaTaulukko',
      link : function(scope, el, attrs, jarjestettavaTaulukkoController) {

        var jarjestelyIkoni = $('<span class="sort-icon"></span>');
        jarjestettavaTaulukkoController.lisaaJarjestettavaSarake(attrs.jarjestettavaSarake, scope, attrs.oletusJarjestely !== undefined);

        el.addClass('sortable');
        el.append(jarjestelyIkoni);

        el.click(function(){
          jarjestettavaTaulukkoController.sarakettaKlikattu(attrs.jarjestettavaSarake);
        });

        scope.asetaJarjestely = function(jarjestaTallaSarakkeella, reverse) {
          if(jarjestaTallaSarakkeella) {
            jarjestelyIkoni.toggleClass('sortby', !reverse).toggleClass('sortby-reverse', reverse);
          } else {
            jarjestelyIkoni.removeClass('sortby sortby-reverse');
          }
        };
      }
    };
  })
  .directive('authToiminto', ['kayttooikeudet', function(kayttooikeudetService) {

    function resolveTemplate(el, attrs) {
      if(attrs.authLinkki !== undefined) {
        return 'template/auth-linkki';
      } else if(attrs.authNappi !== undefined) {
        return 'template/auth-nappi';
      }
    }

    return {
      restrict: 'E',
      replace: true,
      transclude: true,
      templateUrl : resolveTemplate,
      scope: true,
      link: function(scope, element, attrs) {

        var kayttooikeudet, entityId;

        var vaadittuOikeus = attrs.oikeus;
        var konteksti = attrs.konteksti;

        scope.sallittu = false;
        scope.href = attrs.href;

        function paivitaOikeus() {
          scope.sallittu = onkoSalittu();
        }

        function onkoSalittu() {
          if(kayttooikeudet && kayttooikeudet.$resolved) {
            try {
              return kayttooikeudet.roolitunnus === 'YLLAPITAJA' ||
                _(kayttooikeudet[konteksti]).filter(function(value){return value.tunniste == entityId}).pluck('oikeudet').flatten().contains(vaadittuOikeus);
            } catch(e) {}
          }
          return false;
        }

        kayttooikeudetService.hae().then(function(oikeudet) {
          kayttooikeudet = oikeudet;
          paivitaOikeus();
        });

        attrs.$observe('entityId', function(value) {
          entityId = value;
          paivitaOikeus();
        });
      }
    };
  }])

  .directive('jasenyyksienListaus', function(){
    return {
      restrict: 'E',
      scope : {
        jasenet : '=',
        naytaToimikunta : '=',
        naytaSahkoposti : '=',
        otsikko : '=',
        piilotaNimi : '=',
        piilotaKielisyys : '=',
        piilotaJarjesto : '=',
        piilotaKesto : '='
      },
      templateUrl : 'template/jasenyyksien-listaus',
      controller : ['$scope', 'pvm', function($scope, pvm){
        $scope.naytaJasenyydenAlkuPvm = function(jasen) {
          var d = pvm.parsiPvm(jasen.alkupvm);
          if(d) {
            return new Date().getTime() < d.getTime();
          } else {
            return false;
          }
        }
      }]
    };
  })
  .directive('vanhojenJasenyyksienListaus', function(){
    return {
      restrict: 'E',
      scope : {
        jasenet : '=',
        naytaToimikunta : '=',
        naytaSahkoposti : '=',
        otsikko : '=',
        piilotaNimi : '=',
        piilotaKielisyys : '=',
        piilotaJarjesto : '=',
        piilotaKesto : '=',
        salliPiilotus : '='
      },
      templateUrl : 'template/vanhojen-jasenyyksien-listaus',
      link: function(scope) {
        scope.naytaVanhatJasenyydet = false;
        scope.toggleNaytaVanhat = function() {
          scope.naytaVanhatJasenyydet = !scope.naytaVanhatJasenyydet;
        };
      }
    };
  })
  .directive('luotu', function(){
    return {
      restrict: 'E',
      scope : {
        model: '='
      },
      templateUrl : 'template/luotu',
      replace: true
    };
  })
  .directive('muutettu', function(){
    return {
      restrict: 'E',
      scope : {
        model: '='
      },
      templateUrl : 'template/muutettu',
      replace: true
    };
  })

  .directive('booleanSelect', ['boolValues', function(boolValues){
    return {
      restrict: 'E',
      replace: true,
      scope : {
        model: '='
      },
      template : '<select ng-model="model" ng-options="b.value as b.name for b in boolValues">',
      link : function(scope) {
        scope.boolValues = boolValues;
      }
    };
  }])

  .directive('tallenna', ['apiCallInterceptor', function(apiCallInterceptor){

    function pyyntojaKaynnissa(metodiIdt) {
      return !_(apiCallInterceptor.pyynnot).pick(metodiIdt).every({pyyntojaKaynnissa : 0});
    }

    return {
      restrict: 'E',
      scope: {
        disabloiPyyntojenAjaksi: '@',
        teksti : '='
      },
      template : '<button>{{teksti}}</button>',
      replace : true,
      link : function(scope, el) {
        var idt = scope.$eval(scope.disabloiPyyntojenAjaksi);
        var tarkistaOnkoPyyntoja = _.partial(pyyntojaKaynnissa, idt);
        scope.$watch(tarkistaOnkoPyyntoja, function(value){
          if(value) {
            el.prop('disabled', true);
          } else {
            el.prop('disabled', false);
          }
        });
      }
    };
  }])

  .directive('input', function(){
    return {
      restrict: 'E',
      link: function(scope, element) {
        $(element).placeholder();
      }
    }
  });
