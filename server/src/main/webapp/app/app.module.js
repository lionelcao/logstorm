(function(){
    'use strict';


    angular.module('app').controller("TopNavController",TopNavController);

    //angular.module("app").provider('configService',ConfigServiceProvider);
    //function ConfigServiceProvider(){
    //    var options = {};
    //
    //    this.init = function (opt) {
    //        angular.extend(options, opt);
    //    };
    //
    //    this.$get = [function () {
    //        if (!options) {
    //            throw new Error('Config options must be configured');
    //        }
    //        return options;
    //    }];
    //}

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