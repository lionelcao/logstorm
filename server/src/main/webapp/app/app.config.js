(function() {
    'use strict';
    angular.module('app', ['ngRoute'])
        .value('$routerRootComponent', 'app')
        .value("current_module","home")
        .config(routeConfig);

    routeConfig.$inject=["$locationProvider", "$routeProvider"];
    function routeConfig($locationProvider, $routeProvider) {
        //$locationProvider.hashPrefix("!");
        $routeProvider
            .when('/', {
                templateUrl: "home/home.html",
                controller: "HomeController"
            })
            .when('/streaming', {
                templateUrl: "streaming/streaming.list.html",
                controller: "StreamingListController"
            })
            .when('/streaming/create', {
                templateUrl: "streaming/streaming.create.html",
                controller: "StreamingCreateController"
            })
            .when('/streaming/:id', {
                templateUrl: "streaming/streaming.view.html",
                controller: "StreamingViewController"
            })
            .when('/cluster', {
                templateUrl: "cluster/cluster.list.html",
                controller: "ClusterListController"
            })
            .when('/cluster/create', {
                templateUrl: "cluster/cluster.create.html",
                controller: "ClusterCreateController"
            })
            .when('/cluster/:id', {
                templateUrl: "cluster/cluster.view.html",
                controller: "ClusterViewController"
            })
            .when('/configuration', {
                templateUrl: "configuration/configuration.html",
                controller: "ConfigurationController"
            });
    }
})();