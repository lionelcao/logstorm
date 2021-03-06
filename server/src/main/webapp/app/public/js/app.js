(function() {
	'use strict';

	/* App Module */
	var logStormApp = angular.module('logStormApp', ['ngRoute', 'ngAnimate', 'ui.router', 'logStormControllers']);
	var _TRS = Math.random();

	logStormApp.config(function ($stateProvider, $urlRouterProvider, $animateProvider) {
		$urlRouterProvider.otherwise("/cluster");
		$stateProvider
		// =================== Landing ===================
			.state('home', {
				url: "/",
				templateUrl: "partials/home.html?_=" + _TRS,
				controller: "homeCtrl"
				//resolve: _resolve({featureCheck: true})
			})
			.state('about', {
				url: "/about",
				templateUrl: "partials/about.html?_=" + _TRS,
				controller: "aboutCtrl"
			})
			.state('application', {
				url: "/application",
				templateUrl: "partials/application.html?_=" + _TRS,
				controller: "applicationCtrl"
			})
			.state('application_new', {
				url: "/application/new",
				templateUrl: "partials/application_new.html?_=" + _TRS,
				controller: "applicationNewCtrl"
			})
			.state('application_view', {
				url: "/application/:id",
				templateUrl: "partials/application_view.html?_=" + _TRS,
				controller: "applicationViewCtrl"
			})
			.state('cluster', {
				url: "/cluster",
				templateUrl: "partials/cluster.html?_=" + _TRS,
				controller: "clusterCtrl"
			})
			.state('cluster_new', {
				url: "/cluster/new",
				templateUrl: "partials/cluster_new.html?_=" + _TRS,
				controller: "clusterNewCtrl"
			})
			.state('cluster_view', {
				url: "/cluster/:id",
				templateUrl: "partials/cluster_view.html?_=" + _TRS,
				controller: "clusterViewCtrl"
			})
			.state('configuration', {
				url: "/configuration",
				templateUrl: "partials/configuration.html?_=" + _TRS,
				controller: "configurationCtrl"
			})
	});

	logStormApp.controller('MainCtrl', function ($scope, API, UI) {
		$scope.API = API;
		$scope.UI = UI;
	});
}());
