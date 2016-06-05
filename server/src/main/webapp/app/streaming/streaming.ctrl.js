(function() {
    'use strict';
    angular.module("app")
        .controller("StreamingListController",StreamingListController)
        .controller("StreamingCreateController",StreamingCreateController)
        .controller("StreamingViewController",StreamingViewController)
        .controller("StreamingEditController",StreamingEditController);

    // ========================

    StreamingListController.$inject=["$scope","$log"];
    function StreamingListController($scope,$log){
        $log.info("Loading streaming");
    }

    StreamingViewController.$inject=["$scope","$log"];
    function StreamingViewController($scope,$log){
        $log.info("Loading streaming");
    }

    StreamingCreateController.$inject=["$scope","$log"];
    function StreamingCreateController($scope,$log){
        $log.info("Loading streaming");
    }

    StreamingEditController.$inject=["$scope","$log"];
    function StreamingEditController($scope,$log){
        $log.info("Loading streaming");
    }
})();