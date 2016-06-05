(function() {
    'use strict';
    angular.module("app")
        .controller("ClusterListController",ClusterListController)
        .controller("ClusterCreateController",ClusterCreateController)
        .controller("ClusterViewController",ClusterViewController)
        .controller("ClusterEditController",ClusterEditController);

    // ========================

    ClusterListController.$inject=["$scope","$log"];
    function ClusterListController($scope,$log){
        $log.info("Loading Cluster");
    }

    ClusterViewController.$inject=["$scope","$log"];
    function ClusterViewController($scope,$log){
        $log.info("Loading Cluster");
    }

    ClusterCreateController.$inject=["$scope","$log"];
    function ClusterCreateController($scope,$log){
        $log.info("Loading Cluster");
    }

    ClusterEditController.$inject=["$scope","$log"];
    function ClusterEditController($scope,$log){
        $log.info("Loading Cluster");
    }
})();