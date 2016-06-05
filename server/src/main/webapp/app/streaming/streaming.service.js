(function(){
    'use strict';
    angular.module("app").factory("streamingAPIService",StreamingAPIService);

    // --------------
    StreamingAPIService.$inject=["$http","$log","API_BASE_URL"];
    function StreamingAPIService($http,$log,API_BASE_URL){
        $log.info("Loading streamingAPIService (version:"+getVersion()+")");
        return {
            version: getVersion
        };
        function getVersion(){
            return "v0.1.0";
        }
    }
})();