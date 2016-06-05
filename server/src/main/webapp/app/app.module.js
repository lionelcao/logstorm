(function(){
    'use strict';
    angular.module('app').controller("TopNavController",TopNavController);

    TopNavController.$inject=["$scope","$location","$log"];
    function TopNavController($scope,$location,$log){
        $log.info("Load top-nav");
        $scope.isActive = function(route){
            return route == $location.path();
        };
        $scope.isPrefixWith = function(route_prefix){
            return $location.path().startsWith(route_prefix);
        }
    }
})();