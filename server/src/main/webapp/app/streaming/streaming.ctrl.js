(function() {
    'use strict';
    angular.module("app")
        .controller("StreamingListController",StreamingListController)
        .controller("StreamingCreateController",StreamingCreateController)
        .controller("StreamingViewController",StreamingViewController)
        .controller("StreamingEditController",StreamingEditController);

    // ========================

    StreamingListController.$inject=["$scope","$log","streamingAPIService"];
    function StreamingListController($scope,$log,streamingAPIService){
        $log.info("Loading streaming list");
    }

    StreamingViewController.$inject=["$scope","$log","streamingAPIService"];
    function StreamingViewController($scope,$log,streamingAPIService){
        $log.info("Loading streaming view");
    }

    StreamingCreateController.$inject=["$scope","$log","streamingAPIService"];
    function StreamingCreateController($scope,$log,streamingAPIService){
        $log.info("Loading streaming create");
    }

    StreamingEditController.$inject=["$scope","$log","streamingAPIService"];
    function StreamingEditController($scope,$log,streamingAPIService){
        $log.info("Loading streaming edit");
    }
})();