(function() {
    'use strict';
    angular.module("app").controller("ManagementController",ManagementController);
    ManagementController.$inject=["$scope","$log"];

    function ManagementController($scope,$log){
        $log.info("Loading management");
    }
})();