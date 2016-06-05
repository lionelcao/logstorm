(function() {
    'use strict';
    angular.module("app").controller("ConfigurationController",ConfigurationController);
    ConfigurationController.$inject=["$scope","$log","$http","API_BASE_URL"];

    function ConfigurationController($scope,$log,$http,API_BASE_URL){
        $log.info("Loading configuration");
        $http.get(API_BASE_URL+"/api/configprops").then(function(response){
                $scope.apiConfigProps=response.data;
        },function(response){
            $scope.apiConfigProps=response.data;
        });
    }
})();