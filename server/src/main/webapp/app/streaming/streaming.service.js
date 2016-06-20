(function(){
    'use strict';
    angular.module("app").factory("streamingAPIService",StreamingAPIService);

    // --------------
    StreamingAPIService.$inject=["$http","$log","API_BASE_URL"];
    function StreamingAPIService($http,$log,apiBaseUrl){
        $log.info("Loading streamingAPIService (version:"+getVersion()+")");
        return {
            version: getVersion,
            findAll: findAll
        };
        function getVersion(){
            return "v0.1.0";
        }

        function findAll(success, fail){
            $http({
                method: "GET",
                url: apiBaseUrl+"/api/pipeline"
            }).then(function(response){
                success(response.data.data)
            },function(response){
                fail(response);
            })
        }
    }
})();