(function() {
    'use strict';
    angular.module("app").controller("ConfigurationController",ConfigurationController);
    ConfigurationController.$inject=["$scope","$log"];

    function ConfigurationController($scope,$log){
        $log.info("Loading configuration");
    }
})();