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

angular.module('directives', ['services', 'resources', 'ngCookies'])
  .directive('transcludeWithSurroundingScope', function() {
    return {
      link: {
        pre: function(scope, element, attr, ctrl, transclude) {
          if (transclude) {
            transclude(scope, function(clone) {
              element.append(clone);
            })
          }
        }
      }
    };
  })
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
      return _.max(_(apiCallInterceptor.pyynnot).pick(metodiIdt).map(function(pyynto){return pyynto.paivitetty;}).value());
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
      replace: true,
      scope : {
        sopimukset : '=',
        naytaTutkinnot : '=',
        naytaPerusteSarake : '=',
        otsikko : '=',
        piilotaKoulutustoimijat : '='
      },
      templateUrl : 'template/sopimusten-listaus',
      link: function(scope) {}
    };
  })
  .directive('enumValikko', ['i18n', 'EnumResource', '$compile', function (i18n, EnumResource, $compile) {

    var template = '<select ng-model="arvo" ng-required="pakollinen" ng-options="arvo.nimi as arvo.label for arvo in arvot">';
    template += '<option value="" ng-show="(pakollinen && !arvo) || !pakollinen" ng-bind="pakollinen ? i18n.yleiset[\'valitse\'] : i18n.yleiset[\'ei-valintaa\']"></option></select>';

    return {
      restrict: 'E',
      replace: true,
      scope: {
        arvo : '=',
        nimi : '@',
        pakollinen: '='
      },
      template : '<span>' + template + '</span>',
      link: function (scope, element, attrs) {

        var nimi = scope.nimi;

        scope.i18n = i18n;
        EnumResource.get({'enum': attrs.nimi}, function(arvot){
          scope.arvot = _.map(arvot, function(arvo) {return {nimi: arvo.nimi, label: i18n.enum[nimi + "-arvo"][arvo.nimi]}});
          //Compiletaan template vasta kun enumdata on saatavilla. Muutoin ie9 rendaa selectin väärin.
          var compiled = $compile(template)(scope);
          element.empty();
          element.append(compiled);
        });

        scope.$watch('arvo', function(value) {
          if(value === '') {
           scope.arvo = null;
          }
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
  // Workaround https://github.com/angular-ui/bootstrap/issues/4170
  .directive('pvmValidointi', [function() {
    return {
      restrict: 'A',
      require: 'ngModel',
      link: function(scope, element, attrs, ctrl) {
        ctrl.$validators.pvmValidointi = function(modelValue) {
          if (scope.minDate !== undefined && modelValue < scope.minDate) {
            return false;
          }
          if (scope.maxDate !== undefined && modelValue > scope.maxDate) {
            return false;
          }
          return true;
        };
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
        otsikko : '@',
        pakollinen : '=',
        tyhjennettava: '@'
      },
      templateUrl : 'template/pvm-valitsin',
      link : function(scope) {
        // oletusPvm-model voi osoittaa toisen komponentin valintaan, jolloin sen arvo on saatavilla vasta myöhemmin
        scope.$watch('oletusPvm', function(oletusPvm) {
          if (oletusPvm !== undefined && scope.valittuPvm === undefined) {
            scope.valittuPvm = scope.oletusPvm;
          }
        });

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
  .factory('modelPromise', ['$q', function($q) {
    return function(model){
      // Palauttaa promisen, joka sisältää annetun modelin. Jos model on Angular
      // resource, palauttaa sen oman promisen.
      return (model && model.$promise) || $q.when(model);
    }
  }])

  .directive('hakuValitsin', ['i18n', 'kieli', function(i18n, kieli, modelPromise) {

    return {
      restrict: 'E',
      replace: true,
      scope : {
        otsikko : '@',
        url : '@',
        model : '=',
        modelIdProperty : '@',
        modelTextProperty : '@',
        pakollinen : '=',
        monivalinta : '='
      },
      templateUrl : 'template/haku-valitsin',
      controller : function($scope) {
        var modelIdProp = $scope.modelIdProperty;
        var modelTextProp = $scope.modelTextProperty;

        function lokalisoituTeksti(obj) {
          var teksti = '';

          if(_.has(obj, modelTextProp)) {
            teksti = obj[modelTextProp];
          } else {
            teksti = obj[modelTextProp + '_' + kieli];
          }
          return teksti;
        }

        $scope.options = {
          width: '100%',
          minimumInputLength : 1,
          allowClear : true,
          multiple : $scope.monivalinta,
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
              return {results: data};
            }
          },
          formatResult : lokalisoituTeksti,
          formatSelection : lokalisoituTeksti,
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
          },
          initSelection : function () {}
        };
      }
    };
  }])

  .directive('fileUpload', ['$cookies' , function($cookies){
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

        // http://stackoverflow.com/questions/651700/how-to-have-jquery-restrict-file-types-on-upload
        el.find('input[type=file]').change(function(event){
          var filename = event.target.value? _.last(event.target.value.split('\\')) : '';

          el.find('input.x-xsrf-token').val($cookies.get('XSRF-TOKEN'));

          scope.tiedostoValittu = filename.length > 0;
          el.find('input.valittu-tiedosto').val(filename);

          // Kts. palvelinpään http-util/allowed-mimetypes
          var allowed_mime_types = ["application/pdf",
                                    "image/gif", "image/jpeg", "image/png",
                                    "text/plain", "text/rtf",
                                    "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                                    "application/msword"];
          var file = this.files[0];
          if (! _.contains(allowed_mime_types, file.type)) {
        	  alert("Tiedostotyyppi ei kelpaa: " + file.type);
          } else {
        	  scope.$apply();
          }
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
  }])

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

    $scope.$watch($attrs.jarjestettavaTaulukko, function(value) {
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

  .directive('jarjestettavaSarake', function() {
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
      } else if(attrs.authBlokki !== undefined) {
        return 'template/auth-blokki';
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
        var sallitutRoolit = attrs.sallitutRoolit ? scope.$eval(attrs.sallitutRoolit) : [];
        sallitutRoolit.push('YLLAPITAJA');

        scope.sallittu = false;
        scope.href = attrs.href;

        function paivitaOikeus() {
          scope.sallittu = onkoSalittu();
        }

        function onkoSalittu() {
          if(kayttooikeudet && kayttooikeudet.$resolved) {
            try {
              return _.contains(sallitutRoolit, kayttooikeudet.roolitunnus) ||
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
      replace: true,
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
      replace: true,
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

  .directive('booleanSelect', ['boolValues', 'i18n', function(boolValues, i18n){

    var template = '<select ng-model="model" ng-required="pakollinen" ng-options="b.value as b.name for b in boolValues">';
    template += '<option value="" ng-show="(pakollinen && !model) || !pakollinen" ng-bind="pakollinen ? i18n.yleiset[\'valitse\'] : i18n.yleiset[\'ei-valintaa\']"></option></select>';

    return {
      restrict: 'E',
      replace: true,
      scope : {
        model: '=',
        pakollinen: '='
      },
      template : template,
      link : function(scope) {
        scope.boolValues = boolValues;
        scope.i18n = i18n;

        scope.$watch('model', function(value) {
          if(value === '') {
            scope.model = null;
          }
        })
      }
    };
  }])

  .directive('tallenna', ['apiCallInterceptor', function(apiCallInterceptor){

    function onkoPyyntojaKaynnissa(metodiIdt) {
      return !_(apiCallInterceptor.pyynnot).pick(metodiIdt).every({pyyntojaKaynnissa : 0});
    }

    return {
      restrict: 'E',
      scope: {
        disabloiPyyntojenAjaksi: '@',
        formiValidi : '=',
        teksti : '=',
        class : '@'
      },
      template : '<button class="{{class}}" ng-disabled="tallennusDisabloitu">{{teksti}}</button>',
      replace : true,
      link : function(scope, el) {
        var idt = scope.$eval(scope.disabloiPyyntojenAjaksi);
        var pyyntojaKaynnissa = false;
        var tarkistaOnkoPyyntoja = _.partial(onkoPyyntojaKaynnissa, idt);

        scope.tallennusDisabloitu = false;

        function paivitaTila() {
          scope.tallennusDisabloitu = scope.formiValidi === false || pyyntojaKaynnissa;
        }

        scope.$watch(tarkistaOnkoPyyntoja, function(value){
          pyyntojaKaynnissa = value ? true : false;
          paivitaTila();
        });

        scope.$watch('formiValidi', paivitaTila );
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
  })

  .directive('pakollisiaKenttia', ['i18n', function(i18n) {
    return {
      restrict: 'E',
      replace: true,
      template: '<div class="pakollisia-kenttia-teksti">{{i18n.yleiset["pakollisia-kenttia"]}}</div>',
      link: function(scope) {
        scope.i18n = i18n;
      }
    }
  }])

  .directive('paatosPdf',['i18n', function(i18n) {
    return {
      restrict: 'E',
      scope : {jasenet: '=jasenet'},
      controller : 'paatosPdfController',
      templateUrl : 'template/paatos-pdf',
      link: function (scope, elem, attrs) {
        scope.i18n = i18n;
        scope.paatokset = attrs.paatokset.split(',');
      },
      controller: ['$scope', '$http', 'kieli', 'pvm', '$routeParams', function ($scope, $http, kieli, pvm, $routeParams){
        $scope.tulostaPaatosModal = false;
        $scope.showLomake = true;
        $scope.paatosIframeSrc = '';
        $scope.type = '';

        $http.get(ophBaseUrl + '/api/ttk/paatospohja-oletukset').
          success(function(data) {
            $scope.paatosPDF = data;
            $scope.paatosPDF.kieli = kieli;
          });

        $scope.showTulostaPaatosModal = function (type) {
          $scope.type = type;
          $scope.tulostaPaatosModal = true;
          $scope.showLomake = true;
          $scope.paatosPDF.paivays = pvm.dateToPvm(Date.now());

          if(type!="asettamis"){
            $scope.paatosPDF.paatosteksti = '';
          }
        };

        $scope.hideTulostaPaatosModal = function () {
          $scope.tulostaPaatosModal = false;
          $scope.showLomake = true;
        };

        $scope.esikatselePaatos = function (type) {
          $scope.paatosIframeSrc = "";
          $scope.showLomake = false;
          var diaari = {
            'diaarinumero': $routeParams.id
          };

          var nocache = Date.now();
          $scope.paatosPDF.lataa = true;
          $scope.paatosPDF.nocache = nocache;
          $scope.paatosPDF.diaarinumero = diaari.diaarinumero;

          window.tulostaPaatos = {
            paatosPDFUrl : '../../api/ttk/'+encodeURIComponent(diaari.diaarinumero)+'/'+type+'paatos?'+ $.param($scope.paatosPDF)
          };
          $scope.paatosIframeSrc = "../pdf-viewer/pdf-viewer/viewer.html?nocache="+nocache;
        };

        $scope.lataaPDF = function() {
          window.location = window.tulostaPaatos.paatosPDFUrl;
        };
      }]
    };
  }]);
