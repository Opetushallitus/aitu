angular.module('organisaatiomuutokset', ['ngRoute', 'services', 'resources'])

.config(function($routeProvider) {
  $routeProvider.when('/organisaatiomuutokset', {controller: "organisaatiomuutoksetController", templateUrl: "template/organisaatiomuutokset"});
})

.factory('organisaatiomuutosResource', function($resource) {
  return $resource(ophBaseUrl + "/api/organisaatiomuutos/", {id: "@id"}, {
    'get': {
      method: 'GET',
      id:"organisaatiomuutoslistaus",
      isArray: true
    },
    'tehty': {
      method: 'POST',
      url: ophBaseUrl + "/api/organisaatiomuutos/:id/tehty"
    },
    'maara': {
      method: 'GET',
      url: ophBaseUrl + "/api/organisaatiomuutos/maara"
    }
  });
})

.controller('organisaatiomuutoksetController', ['$scope', 'organisaatiomuutosResource', 
  function($scope, organisaatiomuutosResource) {
    $scope.organisaatiomuutokset = organisaatiomuutosResource.get();

    $scope.merkitseTehdyksi = function(index, id) {
      organisaatiomuutosResource.tehty({"id": id});
      $scope.organisaatiomuutokset.splice(index, 1);
    };
}]);