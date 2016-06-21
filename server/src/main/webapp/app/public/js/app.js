(function() {
	'use strict';

	/* App Module */
	var logStormApp = angular.module('logStormApp', ['ngRoute', 'ngAnimate', 'ui.router', 'logStormControllers']);//, 'eagleControllers', 'featureControllers', 'eagle.service']);
	var _TRS = Math.random();

	logStormApp.config(function ($stateProvider, $urlRouterProvider, $animateProvider) {
		$urlRouterProvider.otherwise("/cluster");
		$stateProvider
		// =================== Landing ===================
			.state('home', {
				url: "/",
				templateUrl: "partials/home.html?_=" + _TRS,
				controller: "homeCtrl",
				//resolve: _resolve({featureCheck: true})
			})
			.state('application', {
				url: "/application",
				templateUrl: "partials/application.html?_=" + _TRS,
				controller: "applicationCtrl",
				//resolve: _resolve({featureCheck: true})
			})
			.state('cluster', {
				url: "/cluster",
				templateUrl: "partials/cluster.html?_=" + _TRS,
				controller: "clusterCtrl",
			})
			.state('cluster_new', {
				url: "/cluster/new",
				templateUrl: "partials/cluster_new.html?_=" + _TRS,
				controller: "clusterNewCtrl",
			})
			.state('configuration', {
				url: "/configuration",
				templateUrl: "partials/configuration.html?_=" + _TRS,
				controller: "configurationCtrl",
				//resolve: _resolve({featureCheck: true})
			})
	});

	logStormApp.controller('MainCtrl', function ($scope, API, UI) {
		$scope.API = API;
		$scope.UI = UI;
	});
}());
