// Copyright (c) 2015 The Finnish National Board of Education - Opetushallitus
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

angular.module('suoritus', [])

  .config(['$routeProvider', function($routeProvider) {
    $routeProvider.when('/lisaa-suoritus', {controller: 'SuoritusController', templateUrl: 'template/suoritus'});
    $routeProvider.when('/muokkaa-suoritus/:suoritusid', {controller:'SuoritusController', templateUrl:'template/suoritus'});
  }])

  .controller('SuoritusController', ['$routeParams', '$location', '$modal', '$scope', 'Arvioija', 'Osaamisala', 'Koulutustoimija', 'Rahoitusmuoto', 'Suorittaja', 'Suoritus', 'Tutkinnonosa', 'TutkintoResource', 'Varmistus', 'i18n', 
   function($routeParams, $location, $modal, $scope, Arvioija, Osaamisala, Koulutustoimija, Rahoitusmuoto, Suorittaja, Suoritus, Tutkinnonosa, TutkintoResource, Varmistus, i18n) {
	  
    $scope.vuodet = _.range(1, 21);

    Rahoitusmuoto.haeKaikki().then(function(rahoitusmuodot) {
      $scope.rahoitusmuodot = rahoitusmuodot;
    });

    Suorittaja.haeKaikki().then(function(suorittajat) {
      $scope.suorittajat = suorittajat;
    });

    Tutkinnonosa.haeKaikki().then(function(tutkinnonosat) {
      $scope.tutkinnonosat = tutkinnonosat;
    });

    Arvioija.haeKaikki().then(function(arvioijat) {
      $scope.arvioijat = arvioijat;
    });
    
    TutkintoResource.query(function(tutkinnot) {
      $scope.tutkinnot = tutkinnot;
    });

    Koulutustoimija.haeKaikkiNimet().then(function(koulutustoimijat) {
      $scope.koulutustoimijat = koulutustoimijat;
    });

    $scope.form = {
      osat: [],
      arvioijat: []
    };
    $scope.osat = [];
    $scope.form.valmistava_koulutus = false;
//    $scope.form.suoritusaika = now;
    
    // ladataan editoitavaksi
    if ($routeParams.suoritusid) {
        Suoritus.haeId($routeParams.suoritusid).then(function(suoritus) {
        	$scope.form.rahoitusmuoto = suoritus.rahoitusmuoto;
        	$scope.form.suorittaja = suoritus.suorittaja;
        	$scope.form.koulutustoimija = suoritus.koulutustoimija;
        	$scope.form.opiskelijavuosi = "" + suoritus.opiskelijavuosi;
        	$scope.form.jarjestamismuoto = suoritus.jarjestamismuoto;
        	$scope.form.jarjestelyt = suoritus.jarjestelyt;
        	$scope.form.paikka = suoritus.paikka;
        	$scope.form.valmistava_koulutus = suoritus.valmistava_koulutus;
        	$scope.form.suoritusaika_alku = suoritus.suoritusaika_alku;
        	$scope.form.suoritusaika_loppu = suoritus.suoritusaika_loppu;
        	$scope.form.tutkinto = suoritus.tutkinto;
        	$scope.form.suorituskerta_id = suoritus.suorituskerta_id;
            $scope.form.arvioijat = suoritus.arvioijat;
           // $scope.arvioijat = suoritus.arvioijat;
            $scope.osat = _.map(suoritus.osat, function(osa) {
                var result = _.pick(osa, ['arvosana', 'kieli', 'todistus', 'suoritus_id','arvosanan_korotus','osaamisen_tunnustaminen']);
                result.tutkinnonosa = {
                	tutkinnonosa_id: osa.tutkinnonosa,
                	osatunnus: osa.osatunnus,
                	nimi: osa.nimi, // TODO: sv ja fi
                	nayttotutkinto_nimi_fi: suoritus.tutkinto_nimi_fi,
                	nayttotutkinto_nimi_sv: suoritus.tutkinto_nimi_sv,
                	tutkinto: {
                		nimi: suoritus.tutkinto_nimi_fi,
                		tutkintotunnus: suoritus.tutkinto,
                		nimi_fi: suoritus.tutkinto_nimi_fi,
                		nimi_sv: suoritus.tutkinto_nimi_sv
                	}
                };
                result.osaamisala_id = osa.osaamisala;
                return result;
            });
            $scope.form.osat = $scope.osat;
        });
     } 
    
     $scope.$watchCollection('osat', function(osat) {
      $scope.form.osat = _.map(osat, function(osa) {
        var result = _.pick(osa, ['osaamisala', 'arvosana', 'arvosanan_korotus', 'kieli', 'todistus', 'osaamisen_tunnustaminen', 'osaamisala_id', 'suoritus_id']);
        result.tutkinnonosa_id = osa.tutkinnonosa.tutkinnonosa_id;
        return result;
      });
     });

     $scope.muokkaaOsa = function(muokattavaOsa) {
    	 var modalInstance = $modal.open({
             templateUrl: 'template/modal/suoritus-tutkinnonosa',
             controller: 'SuoritusTutkinnonosaModalController',
             resolve: {
               osa: function() {return muokattavaOsa; },
               tutkinnot: function() { return $scope.tutkinnot; },
               tutkinto: function() { return $scope.form.tutkinto; }
             }
    	 });
     };
     
    $scope.lisaaTutkinnonosa = function() {
      var modalInstance = $modal.open({
        templateUrl: 'template/modal/suoritus-tutkinnonosa',
        controller: 'SuoritusTutkinnonosaModalController',
        resolve: {
          osa: function() {return null; },
          tutkinnot: function() { return $scope.tutkinnot; },
          tutkinto: function() { return $scope.form.tutkinto; }
        }
      });

      modalInstance.result.then(function(uusiOsa) {
        if (uusiOsa.osaamisen_tunnustaminen) {
          uusiOsa.arvosana = null;
          uusiOsa.arvosanan_korotus = false;
        }
        uusiOsa.osaamisala_id = uusiOsa.osaamisala; 
        if (!_.find($scope.osat, function(osa) {
            return osa.tutkinnonosa.tutkinnonosa_id === uusiOsa.tutkinnonosa.tutkinnonosa_id;
          })) {
          $scope.osat.push(uusiOsa);
        }
      });
    };

    $scope.poistaArvioija = function(poistettavaArvioija) {
        _.remove($scope.form.arvioijat, function(arvioija) {
          return arvioija.arvioija_id === poistettavaArvioija.arvioija_id;
      });
    };
    
    $scope.lisaaArvioija = function() {
        var modalInstance = $modal.open({
          templateUrl: 'template/modal/suoritus-arvioija',
          controller: 'SuoritusArvioijaModalController',
          resolve: {
            arvioijat: function() { return $scope.arvioijat; }
          }
        });

        modalInstance.result.then(function(uusiArvioija) {
        	// ei tuplata samaa arvioijaa suorituskerralle
            if (!_.find($scope.form.arvioijat, {'arvioija_id' : uusiArvioija.arvioija_id})) {
               var foArvioija = _.find($scope.arvioijat, {'arvioija_id' : uusiArvioija.arvioija_id});
               if (!foArvioija) {
            	   // täysin uusi arvioija
            	   $scope.form.arvioijat.push(uusiArvioija);
               } else {
            	   $scope.form.arvioijat.push(foArvioija);
               }
            }
        });
      };

    $scope.poistaOsa = function(poistettavaOsa) {
      Varmistus.varmista(i18n.arviointipaatokset.poistetaanko_tutkinnonosa, i18n.arviointipaatokset.poista_tutkinnonosa_teksti, i18n.arviointipaatokset.poista_tutkinnonosa).then(function() {
        _.remove($scope.osat, function(osa) {
          return osa.tutkinnonosa.tutkinnonosa_id === poistettavaOsa.tutkinnonosa.tutkinnonosa_id;
        });
      });
    };

    $scope.lisaaSuoritus = function() {
      Suoritus.lisaa($scope.form).then(function() {
        $location.url('/arviointipaatokset');
      });
    };

    $scope.peruuta = function() {
      $location.url('/arviointipaatokset');
    };
  }])

  .controller('SuoritusArvioijaModalController', ['$modalInstance', '$scope', 'Arvioija','arvioijat',
                                                  function($modalInstance, $scope, Arvioija, arvioijat) {

    $scope.arvioijat = arvioijat;

	$scope.ok = function() {
	  $modalInstance.close($scope.form);
	};

    $scope.sulje = function() {
      $modalInstance.dismiss();
    };
	}])
   
  .controller('SuoritusTutkinnonosaModalController', ['$modalInstance', '$scope', 'Osaamisala', 'Tutkinnonosa', 'tutkinnot', 'tutkinto', 'osa', 
                                                      function($modalInstance, $scope, Osaamisala, Tutkinnonosa, tutkinnot, tutkinto, osa) {
	  $scope.tutkinnot = tutkinnot;
	  if (osa == null) {
		  $scope.form = {
		    arvosana: 'hyvaksytty',
		    arvosanan_korotus: false,
		    kieli: 'fi',
		    todistus: false,
		    osaamisen_tunnustaminen: null, 
		    osaamisala_id: null,
		    osaamisala: null
		  };
		  $scope.tutkinto = tutkinto;
	  } else { // TODO: select-fields

		  $scope.form = {
    	     arvosana: osa.arvosana,
		     arvosanan_korotus: osa.arvosanan_korotus,
		     kieli: osa.kieli,
		     todistus: osa.todistus,
		     osaamisen_tunnustaminen: osa.osaamisen_tunnustaminen,
		     osaamisala_id: osa.osaamisala_id,
		     osaamisala: null
		  };
		  $scope.tutkinto = osa.tutkinnonosa.tutkinto.tutkintotunnus;
		  $scope.tutkinnonosa = osa.tutkinnonosa;
	  }

    $scope.$watch('tutkinto', function(tutkinto) {
      if (tutkinto !== undefined) {
        Tutkinnonosa.hae(tutkinto).then(function(tutkinnonosat) {
          $scope.tutkinnonosat = tutkinnonosat;
          $scope.form.tutkinnonosa = _.find($scope.tutkinnonosat, 'tutkinnonosa_id', $scope.tutkinnonosa);
        });
        Osaamisala.hae(tutkinto).then(function(osaamisalat) {
          $scope.osaamisalat = osaamisalat.osaamisala;
        });
      }
    });
    
//    if (osa && osa.tutkinnonosa && osa.tutkinnonosa.osatunnus) {
//    	$scope.form.tutkinnonosa=osa.tutkinnonosa;
 //   }

    $scope.ok = function() {
      $modalInstance.close($scope.form);
    };

    $scope.sulje = function() {
      $modalInstance.dismiss();
    };
  }])
;