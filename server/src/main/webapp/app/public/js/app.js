(function() {
	'use strict';

	/* App Module */
	var logStormApp = angular.module('logStormApp', ['ngRoute', 'ngAnimate', 'ui.router', 'logStormControllers']);//, 'eagleControllers', 'featureControllers', 'eagle.service']);
	var _TRS = Math.random();

	logStormApp.config(function ($stateProvider, $urlRouterProvider, $animateProvider) {
		$urlRouterProvider.otherwise("/cluster");
		$stateProvider
		// =================== Landing ===================
			.state('cluster', {
				url: "/cluster",
				templateUrl: "partials/cluster.html?_=" + _TRS,
				controller: "clusterCtrl",
				//resolve: _resolve({featureCheck: true})
			})
	});

	logStormApp.controller('MainCtrl', function ($scope, API, UI) {
		$scope.API = API;
		$scope.UI = UI;
	});
}());
