(function() {
    'use strict';
    angular.module("app")
        .controller("ClusterListController",ClusterListController)
        .controller("ClusterCreateController",ClusterCreateController)
        .controller("ClusterViewController",ClusterViewController)
        .controller("ClusterEditController",ClusterEditController);

    // ========================

    ClusterListController.$inject=["$scope","$log","clusterAPIService"];
    function ClusterListController($scope,$log,clusterAPIService){
        $log.info("Loading Cluster");
        $scope.message={};
        $scope.clusters=[];
        $scope.loadClusters = loadClusters;
        $scope.loadClusters();

        function loadClusters() {
            clusterAPIService.all(function (response) {
                $scope.message = null;
                $log.info(response);
                if (response.data.success) {
                    $scope.clusters = response.data.data;
                } else {
                    $scope.message = {
                        level: "error",
                        text: response.data.message
                    };
                }
            }, function (response) {
                $scope.message = {
                    level: "error",
                    text: "Failed to load clusters from API"
                };
            });
        }
    }

    ClusterViewController.$inject=["$scope","$log"];
    function ClusterViewController($scope,$log){
        $log.info("Loading Cluster");
    }

    ClusterCreateController.$inject=["$scope","$log","clusterAPIService"];
    function ClusterCreateController($scope,$log,clusterAPIService){
        $log.info("Loading Cluster");
        $scope.clusterName=null;
        $scope.clusterType=null;
        $scope.clusterProps=null;
        $scope.addCluster = addCluster;
        $scope.message=null;

        function info(msg){
            $log.info(msg);
            $scope.message={
                level:"info",
                text:msg
            };
        }

        function error(msg){
            $log.error(msg);
            $scope.message={
                level:"error",
                text:msg
            };
        }

        function addCluster(){
            var cluster = {
                name: $scope.clusterName,
                adapterClass: $scope.clusterType,
                properties: $scope.clusterProps
            };
            if(cluster.properties!=null){
                cluster.properties = JSON.parse(cluster.properties);
            }
            clusterAPIService.create(cluster,function(response){
                info(response);
            },function(response){
                error(response.data);
            });
        }
    }

    ClusterEditController.$inject=["$scope","$log"];
    function ClusterEditController($scope,$log){
        $log.info("Loading Cluster");
    }
})();