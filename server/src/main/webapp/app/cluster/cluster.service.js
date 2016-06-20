(function(){
    'use strict';
    angular.module("app").service("clusterAPIService",ClusterAPIServiceProvider);

    // --------------
    ClusterAPIServiceProvider.$inject=["$http","$log","API_BASE_URL"];
    function ClusterAPIServiceProvider($http,$log,API_BASE_URL){
        $log.info("Loading streamingAPIService (version:"+getVersion()+")");
        var apiBaseUrl = API_BASE_URL;
        $log.info("API_BASE_URL");
        $log.info(API_BASE_URL);

        return {
            version: getVersion,
            all: listAllClusters,
            create: createCluster,
            remove: deleteCluster
        };

        // ------------

        function getVersion(){
            return "v0.1.0";
        }

        function listAllClusters(success,fail){
            $http({
                method: 'GET',
                url: apiBaseUrl+"/api/cluster",
                headers:{
                    "Accept-Content":"application/json"
                }
            }).then(success,fail)
        }

        function createCluster(cluster, success, fail){
            $http({
                method: 'POST',
                url: apiBaseUrl+"/api/cluster",
                data: cluster,
                headers:{
                    "Content-Type":"application/json"
                }
            }).then(success,fail)
        }

        function deleteCluster(){

        }
    }
})();